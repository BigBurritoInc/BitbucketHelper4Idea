package http

import com.ning.http.client.AsyncHttpClient
import java.nio.charset.StandardCharsets
import java.util.*

class AuthHttpClient(
        private val user: String,
        private val password: String
    ): AsyncHttpClient() {

    override fun requestBuilder(method: String?, url: String?): BoundRequestBuilder {
        val requestBuilder = super.requestBuilder(method, url)
        return requestBuilder.addHeader("Authorization", "Basic " + authString())
    }

    private fun authString(): String {
        val decodedPassword = String(Base64.getDecoder().decode(this.password))
        val authArray = ("$user:$decodedPassword").toByteArray(StandardCharsets.UTF_8)
        return Base64.getEncoder().encodeToString(authArray)
    }
}