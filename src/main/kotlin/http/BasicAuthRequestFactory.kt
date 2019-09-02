package http

import org.apache.http.HttpHeaders
import org.apache.http.HttpMessage
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * This implementation of RequestFactory creates HTTP-requests
 * that use Basic Auth with username and password.
 */
class BasicAuthRequestFactory(private val user: String, private val password: String): RequestFactory() {

    override fun addAuthHeader(request: HttpMessage) {
        val authHeader = "Basic " + authString()
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader)
    }

    private fun authString(): String {
        val authArray = ("$user:$password").toByteArray(StandardCharsets.UTF_8)
        return Base64.getEncoder().encodeToString(authArray)
    }
}