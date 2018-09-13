package bitbucket

import bitbucket.data.Approve
import bitbucket.data.PR
import bitbucket.data.PagedResponse
import bitbucket.data.SimpleUser
import bitbucket.httpparams.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectWriter
import com.palominolabs.http.url.UrlBuilder
import http.HttpAuthRequestFactory
import http.HttpResponseHandler
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.ByteArrayEntity
import java.net.URL

class BitbucketClient(
        private val httpClient: HttpClient,
        private val httpRequestFactory: HttpAuthRequestFactory,
        private val baseUrl: URL,
        private val project: String,
        private val repoSlug: String,
        private val userSlug: String,
        objReader: ObjectReader,
        private val objWriter: ObjectWriter
    ) {

    private val responseHandler = HttpResponseHandler(objReader, object : TypeReference<PagedResponse<PR>>() {})

    fun reviewedPRs(): Deferred<List<PR>> {
        return inbox(Role.REVIEWER)
    }

    fun ownPRs(): Deferred<List<PR>> {
        return  inbox(Role.AUTHOR)
    }

    fun openPRs(): Deferred<List<PR>> {
        return findPRs(PRState.OPEN, PROrder.NEWEST)
    }


    // /rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/participants/{userSlug}
    fun approve(pr: PR) {
        val urlBuilder = urlBuilder().pathSegments(
                "projects", project, "repos", repoSlug, "pull-requests", pr.id.toString(), "participants", userSlug)
        println(urlBuilder.toUrlString())
        val request = httpRequestFactory.createPut(urlBuilder.toUrlString())
        request.setHeader("Content-Type", "application/json")
        val body = objWriter.writeValueAsBytes(Approve(SimpleUser(userSlug)))
        val entity = ByteArrayEntity(body)
        request.entity = entity
        HttpResponseHandler.handle(httpClient.execute(request))
    }

    /**
     * Calls /rest/api/1.0/inbox/pull-requests
     * @see <a href="https://docs.atlassian.com/bitbucket-server/rest/5.13.0/bitbucket-rest.html#idm46209336621072">inbox</a>
     */
    private fun inbox(role: Role, limit: Limit = Limit.Default, start: Start = Start.Zero): Deferred<List<PR>> {
        val urlBuilder = urlBuilder().pathSegments("inbox", "pull-requests")
        applyParameters(urlBuilder, role, start, limit)

        val request = httpRequestFactory.createGet(urlBuilder.toUrlString())
        return async {replayPageRequest(request) {inbox(role, limit, Start(it))} }
    }

    private fun findPRs(state: PRState, order: PROrder, start: Start = Start.Zero): Deferred<List<PR>> {
        val urlBuilder = urlBuilder()
                .pathSegments("projects", project, "repos", repoSlug, "pull-requests")
        applyParameters(urlBuilder, start, order, state)

        val request = httpRequestFactory.createGet(urlBuilder.toUrlString())
        return async {replayPageRequest(request) {findPRs(state, order, Start(it))} }
    }

    private fun urlBuilder() = UrlBuilder.fromUrl(baseUrl).pathSegments("rest", "api", "1.0")

    private fun applyParameters(urlBuilder: UrlBuilder, vararg params: HttpRequestParameter) {
        for (param in params)
            param.apply(urlBuilder)
    }

    private fun sendRequest(request : HttpUriRequest ) = responseHandler.handle(httpClient.execute(request))

    private suspend fun replayPageRequest(request: HttpUriRequest, replay: (Int) -> Deferred<List<PR>>): List<PR> {
        val pagedResponse = sendRequest(request)
        val prs = ArrayList(pagedResponse.values)
        if (!pagedResponse.isLastPage)
            prs.addAll(replay.invoke(pagedResponse.nextPageStart).await())
        return prs
    }
}