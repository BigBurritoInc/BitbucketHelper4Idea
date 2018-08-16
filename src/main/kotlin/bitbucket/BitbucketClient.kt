package bitbucket

import bitbucket.data.*
import bitbucket.httpparams.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectWriter
import com.intellij.util.containers.stream
import com.palominolabs.http.url.UrlBuilder
import http.HttpAuthRequestFactory
import http.ResponseCallback
import org.apache.http.HttpResponse
import org.apache.http.nio.client.HttpAsyncClient
import rx.Observable
import rx.Observer
import rx.subjects.ReplaySubject
import rx.subjects.Subject
import java.net.URL
import java.util.function.Consumer

class BitbucketClient(
        private val httpClient: HttpAsyncClient,
        private val httpRequestFactory: HttpAuthRequestFactory,
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
        return observe(Consumer { observePRs(it, PRState.OPEN, PROrder.NEWEST) })
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
    private fun observeInbox(
            observer: Observer<PagedResponse<PR>>,
            role: Role,
            start: Start = Start.Zero,
            limit: Limit = Limit.Default
    ) {
        val urlBuilder = urlBuilder().pathSegments("inbox", "pull-requests")
        applyParameters(urlBuilder, role, start, limit)

        val request = httpRequestFactory.createGet(urlBuilder.toUrlString())
        httpClient.execute(request,
                object : PagedResponseCallback<PR>(
                    objReader,
                    object : TypeReference<PagedResponse<PR>>() {},
                    observer
                ) {
                    override fun onMorePagesExist(pagedResponse: PagedResponse<PR>) {
                        observeInbox(observer, role, Start(pagedResponse.nextPageStart), limit)
                    }
                })
    }

    private fun observePRs(
            observer: Observer<PagedResponse<PR>>,
            state: PRState,
            order: PROrder,
            start: Start = Start.Zero
    ) {
        val urlBuilder = urlBuilder()
                .pathSegments("projects", project, "repos", slug, "pull-requests")
        applyParameters(urlBuilder, start, order, state)

        val request = httpRequestFactory.createGet(urlBuilder.toUrlString())
        httpClient.execute(request,
                object : PagedResponseCallback<PR>(
                        objReader,
                        object : TypeReference<PagedResponse<PR>>() {},
                        observer
                ) {
                    override fun onMorePagesExist(pagedResponse: PagedResponse<PR>) {
                        observePRs(observer, state, order, Start(pagedResponse.nextPageStart))
                    }
                })

    }

    private fun urlBuilder() = UrlBuilder.fromUrl(baseUrl).pathSegments("rest", "api", "1.0")

    private fun applyParameters(urlBuilder: UrlBuilder, vararg params: HttpRequestParameter) {
        for (param in params)
            param.apply(urlBuilder)
    }

    abstract class PagedResponseCallback<T>(
            private val objReader: ObjectReader,
            private val typeRef: TypeReference<PagedResponse<T>>,
            observer: Observer<PagedResponse<T>>
    ): ResponseCallback<PagedResponse<T>>(observer) {

        abstract fun onMorePagesExist(pagedResponse: PagedResponse<T>)

        override fun onSuccess(response: HttpResponse, observer: Observer<PagedResponse<T>>) {
            val pagedResponse: PagedResponse<T> = objReader.forType(typeRef)
                    .readValue(response.entity.content)

            observer.onNext(pagedResponse)

            if (pagedResponse.isLastPage) {
                observer.onCompleted()
            } else {
                onMorePagesExist(pagedResponse);
            }
        }
    }
}