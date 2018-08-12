package bitbucket

import bitbucket.data.*
import bitbucket.httpparams.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectWriter
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.Response
import com.palominolabs.http.url.UrlBuilder
import http.ResponseHandler
import rx.Observable
import rx.Observer
import rx.subjects.ReplaySubject
import rx.subjects.Subject
import java.net.URL
import java.util.function.Consumer

class BitbucketClient(
        private val httpClient: AsyncHttpClient,
        private val baseUrl: URL,
        private val project: String,
        private val slug: String,
        private val objReader: ObjectReader,
        private val objWriter: ObjectWriter
    ) {

    fun requestReviewedPRs(): Observable<PagedResponse<PR>> {
        return observe(Consumer { observeInbox(it, Role.REVIEWER)})
    }

    fun requestOwnPRs(): Observable<PagedResponse<PR>> {
        return observe(Consumer { observeInbox(it, Role.AUTHOR)})
    }

    fun requestOpenPRs(): Observable<PagedResponse<PR>> {
        return observe(Consumer { observePRs(it, PRState.OPEN, PROrder.NEWEST, 0) })
    }

    private fun observe(method: Consumer<Observer<PagedResponse<PR>>>): Observable<PagedResponse<PR>> {
        val subj: Subject<PagedResponse<PR>, PagedResponse<PR>> = ReplaySubject.create()
        method.accept(subj)
        return subj
    }

    /**
     * Calls /rest/api/1.0/inbox/pull-requests
     * @see https://docs.atlassian.com/bitbucket-server/rest/5.13.0/bitbucket-rest.html#idm46209336621072
     */
    private fun observeInbox(observer: Observer<PagedResponse<PR>>, role: Role, start: Start = Start.Zero, limit: Limit = Limit.Default) {
        val urlBuilder = urlBuilder().pathSegments("inbox", "pull-requests")
        role.apply(urlBuilder)
        start.apply(urlBuilder)
        limit.apply(urlBuilder)

        httpClient.prepareGet(urlBuilder.toUrlString())
                .execute(object : PagedResponseHandler<PR>(
                        objReader,
                        object : TypeReference<PagedResponse<PR>>() {},
                        observer
                ) {
                    override fun onNotLastPage(pagedResponse: PagedResponse<PR>) {
                        observeInbox(observer, role, Start(pagedResponse.nextPageStart), limit)
                    }
                })
    }

    ///REST/API/1.0/DASHBOARD/PULL-REQUESTS?STATE&ROLE&PARTICIPANTSTATUS&ORDER doesn't work for some reason with 404 http error code
    private fun observeDashboard(
            observer: Observer<PagedResponse<PR>>,
            role: Role,
            participantStatus: ParticipantStatus = ParticipantStatus.ANY,
            order: PROrder = PROrder.NEWEST,
            state: PRState = PRState.OPEN,
            start: Int = 0) {
        val urlBuilder = urlBuilder().pathSegments("dashboard", "pull-requests")
        participantStatus.apply(urlBuilder)
        order.apply(urlBuilder)
        role.apply(urlBuilder)
        state.apply(urlBuilder)
        urlBuilder.queryParam("start", Integer.toString(start))

        httpClient.prepareGet(urlBuilder.toUrlString())
                .execute(object : PagedResponseHandler<PR>(
                        objReader,
                        object : TypeReference<PagedResponse<PR>>() {},
                        observer
                ) {
                    override fun onNotLastPage(pagedResponse: PagedResponse<PR>) {
                        observeDashboard(observer,role, participantStatus, order, state,
                                pagedResponse.nextPageStart)
                    }
                })

    }

    private fun observePRs(
            observer: Observer<PagedResponse<PR>>, state: PRState, order: PROrder, start: Int) {
        val urlBuilder = urlBuilder()
                .pathSegments("projects", project, "repos", slug, "pull-requests")
                .queryParam("start", Integer.toString(start))
        order.apply(urlBuilder)
        state.apply(urlBuilder)

        httpClient.prepareGet(urlBuilder.toUrlString())
                .execute(object : PagedResponseHandler<PR>(
                                objReader,
                                object : TypeReference<PagedResponse<PR>>() {},
                                observer
                        ) {
                            override fun onNotLastPage(pagedResponse: PagedResponse<PR>) {
                                observePRs(observer, state, order, pagedResponse.nextPageStart)
                            }
                        })
    }

    private fun urlBuilder() = UrlBuilder.fromUrl(baseUrl).pathSegments("rest", "api", "1.0")

    abstract class PagedResponseHandler<T>(
            private val objReader: ObjectReader,
            private val typeRef: TypeReference<PagedResponse<T>>,
            observer: Observer<PagedResponse<T>>
    ) : ResponseHandler<PagedResponse<T>>(observer) {

        abstract fun onNotLastPage(pagedResponse: PagedResponse<T>)

        override fun onSuccess(response: Response, observer: Observer<PagedResponse<T>>) {
            val pagedResponse: PagedResponse<T> = objReader.forType(typeRef)
                    .readValue(response.responseBodyAsStream)

            observer.onNext(pagedResponse)

            if (pagedResponse.isLastPage) {
                observer.onCompleted()
            } else {
                onNotLastPage(pagedResponse);
            }
        }
    }

}