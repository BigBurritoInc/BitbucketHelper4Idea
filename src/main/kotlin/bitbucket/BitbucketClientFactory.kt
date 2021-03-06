package bitbucket

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import http.AccessTokenRequestFactory
import http.BasicAuthRequestFactory
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients
import ui.getStorerService

object BitbucketClientFactory {

    var password: CharArray = kotlin.CharArray(0)
    private val storer = getStorerService()

    fun createClient(listener: ClientListener = object : ClientListener {}): BitbucketClient {
        val objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val settings = storer.settings
        val requestFactory = if (settings.useAccessTokenAuth) {
            AccessTokenRequestFactory(settings.accessToken)
        } else {
            BasicAuthRequestFactory(settings.login, String(password))
        }
        return BitbucketClient(
                createHttpClient(),
                requestFactory,
                settings, objectMapper.reader(), objectMapper.writer(), listener)
    }

    private fun createHttpClient(): HttpClient {
        // Using system "client" allows us reuse standard Idea SSLContext and TrustManager
        return HttpClients.createSystem()
    }
}