package bitbucket

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.ServiceManager
import http.HttpAuthRequestFactory
import org.apache.http.impl.client.HttpClients
import ui.Storer

object BitbucketClientFactory {

    var password:CharArray = kotlin.CharArray(0)
    val storer = ServiceManager.getService<Storer>(CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext())!!, Storer::class.java)

    fun createClient(listener: ClientListener = object: ClientListener {}): BitbucketClient {

        val objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val settings = storer.settings

        return BitbucketClient(
                createHttpClient(),
                HttpAuthRequestFactory(settings.login, String(password)),
                settings, objectMapper.reader(), objectMapper.writer(), listener)
    }

    private fun createHttpClient() =  HttpClients.createDefault()
}
