package bitbucket

import bitbucket.data.Approve
import bitbucket.data.PR
import bitbucket.data.PagedResponse
import bitbucket.data.SimpleUser
import bitbucket.data.merge.MergeStatus
import bitbucket.data.merge.Veto
import bitbucket.httpparams.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectWriter
import com.intellij.openapi.diagnostic.Logger
import com.palominolabs.http.url.UrlBuilder
import http.HttpResponseHandler
import http.RequestFactory
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.ByteArrayEntity
import ui.Settings
import java.net.URL

class BitbucketClient(
        private val httpClient: HttpClient,
        private val httpRequestFactory: RequestFactory,
        private val settings: Settings,
        objReader: ObjectReader,
        private val objWriter: ObjectWriter,
        private val listener: ClientListener
    ) {
    private val log = Logger.getInstance("BitbucketClient")
    private val mergeStatusResponseHandler = HttpResponseHandler(
            objReader, object : TypeReference<MergeStatus>() {}, listener)
    private val pagedResponseHandler = HttpResponseHandler(
            objReader, object : TypeReference<PagedResponse<PR>>() {}, listener)
    private val pullRequestResponseHandler = HttpResponseHandler(
            objReader, object : TypeReference<PR>() {}, listener)

    fun reviewedPRs(): List<PR> {
        return inbox(Role.REVIEWER)
    }

    fun ownPRs(): List<PR> {
        return inbox(Role.AUTHOR)
    }

    // /rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/participants/{userSlug}
    fun approve(pr: PR) {
        try {
            val urlBuilder = urlBuilder().pathSegments(
                    "projects", settings.project, "repos", settings.slug, "pull-requests", pr.id.toString(), "participants", settings.login)
            println(urlBuilder.toUrlString())
            val request = httpRequestFactory.createPut(urlBuilder.toUrlString())
            val body = objWriter.writeValueAsBytes(Approve(SimpleUser(settings.login)))
            val entity = ByteArrayEntity(body)
            request.entity = entity
            HttpResponseHandler.handle(httpClient.execute(request))
        } catch (e: Exception) {
            listener.requestFailed(e)
            throw e
        }
    }

    fun merge(pr: PR): PR {
        return try {
            val urlBuilder = mergeUrl(pr)
            val request = httpRequestFactory.createPost(urlBuilder.toUrlString())
            sendRequest(request, pullRequestResponseHandler)
        } catch (e: Exception) {
            listener.requestFailed(e)
            pr
        }
    }

    /**
     * Calls /rest/api/1.0/inbox/pull-requests
     * @see <a href="https://docs.atlassian.com/bitbucket-server/rest/5.13.0/bitbucket-rest.html#idm46209336621072">inbox</a>
     */
    private fun inbox(role: Role, limit: Limit = Limit.Default, start: Start = Start.Zero): List<PR> {
        return try {
            val urlBuilder = urlBuilder().pathSegments("inbox", "pull-requests")
            applyParameters(urlBuilder, role, start, limit)

            val request = httpRequestFactory.createGet(urlBuilder.toUrlString())
            filterByProject(replayPageRequest(request) { inbox(role, limit, Start(it)) })
        } catch (e: Exception) {
            listener.requestFailed(e)
            emptyList()
        }
    }

    fun retrieveMergeStatus(pr: PR): MergeStatus {
        return try {
            val urlBuilder = mergeUrl(pr)
            val request = httpRequestFactory.createGet(urlBuilder.toUrlString())
            val mergeStatus = sendRequest(request, mergeStatusResponseHandler)
            mergeStatus.unknown = false
            mergeStatus
        } catch (e: Exception) {
            listener.requestFailed(e)
            MergeStatus(false, false, listOf(Veto("Request Error", "")))
        }
    }

    private fun mergeUrl(pr: PR): UrlBuilder {
        return urlBuilder().pathSegments(
                "projects", settings.project, "repos", settings.slug, "pull-requests", pr.id.toString(), "merge")
                .queryParam("version", pr.version.toString())
    }

    private fun filterByProject(prs: List<PR>): List<PR> {
        return prs.filter {
            it.projectKey.equals(settings.project, true)
            && it.repoSlug.equals(settings.slug, true)
        }
    }

    private fun findPRs(state: PRState, order: PROrder, start: Start = Start.Zero): List<PR> {
        val urlBuilder = urlBuilder()
                .pathSegments("projects", settings.project, "repos", settings.slug, "pull-requests")
        applyParameters(urlBuilder, start, order, state)

        val request = httpRequestFactory.createGet(urlBuilder.toUrlString())
        return replayPageRequest(request) {findPRs(state, order, Start(it))}
    }

    private fun urlBuilder() = UrlBuilder.fromUrl(URL(settings.url)).pathSegments("rest", "api", "1.0")

    private fun applyParameters(urlBuilder: UrlBuilder, vararg params: HttpRequestParameter) {
        for (param in params)
            param.apply(urlBuilder)
    }

    private fun <T> sendRequest(request : HttpUriRequest, responseHandler: HttpResponseHandler<T>) =
            responseHandler.handle(httpClient.execute(request))

    private fun replayPageRequest(request: HttpUriRequest, replay: (Int) -> List<PR>): List<PR> {
        try {
            val pagedResponse = sendRequest(request, pagedResponseHandler)
            val prs = ArrayList(pagedResponse.values)
            if (!pagedResponse.isLastPage)
                prs.addAll(replay.invoke(pagedResponse.nextPageStart))
            return prs
        } catch (e: HttpResponseHandler.UnauthorizedException) {
            log.info(e)
        }
        return emptyList()
    }
}