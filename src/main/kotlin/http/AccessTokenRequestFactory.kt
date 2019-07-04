package http

import org.apache.http.HttpHeaders
import org.apache.http.HttpMessage

/**
 * This implementation of RequestFactory creates requests that use Access Token to authenticate in Bitbucket.
 */
class AccessTokenRequestFactory(private val token: String): RequestFactory() {
    override fun addAuthHeader(request: HttpMessage) {
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer $token")
    }
}