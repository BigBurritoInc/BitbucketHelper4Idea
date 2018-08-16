package http

import org.apache.http.HttpHeaders
import org.apache.http.client.methods.HttpGet
import java.nio.charset.StandardCharsets
import java.util.*

class HttpAuthRequestFactory(private val user: String, private val password: String) {

    fun createGet(url: String): HttpGet {
        val request = HttpGet(url)
        val authHeader = "Basic " + authString()
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader)
        return request
    }

    private fun authString(): String {
        val decodedPassword = String(Base64.getDecoder().decode(password))
        val authArray = ("$user:$decodedPassword").toByteArray(StandardCharsets.UTF_8)
        return Base64.getEncoder().encodeToString(authArray)
    }
}