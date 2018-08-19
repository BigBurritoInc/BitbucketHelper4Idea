package http

import org.apache.http.HttpHeaders
import org.apache.http.HttpMessage
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import java.nio.charset.StandardCharsets
import java.util.*

class HttpAuthRequestFactory(private val user: String, private val password: String) {

    fun createPost(url: String): HttpPost {
        val request = HttpPost(url)
        addAuthHeader(request)
        return request
    }

    fun createPut(url: String): HttpPut {
        val request = HttpPut(url)
        addAuthHeader(request)
        return request
    }
    fun createGet(url: String): HttpGet {
        val request = HttpGet(url)
        addAuthHeader(request)
        return request
    }

    private fun addAuthHeader(request: HttpMessage) {
        val authHeader = "Basic " + authString()
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader)
    }

    private fun authString(): String {
        val decodedPassword = String(Base64.getDecoder().decode(password))
        val authArray = ("$user:$decodedPassword").toByteArray(StandardCharsets.UTF_8)
        return Base64.getEncoder().encodeToString(authArray)
    }
}