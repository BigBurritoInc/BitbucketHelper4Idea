package bitbucket

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.ServiceManager
import http.HttpAuthRequestFactory
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.nio.client.HttpAsyncClient
import ui.PasswordDialog
import ui.Storer
import java.net.URL

object BitbucketClientFactory {

    var password:CharArray = kotlin.CharArray(0)
    val storer = ServiceManager.getService<Storer>(CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext())!!, Storer::class.java);

    fun createClient(): BitbucketClient {

        if (password.isEmpty()) {
            val dialog = PasswordDialog(false)
            dialog.showAndGet()
            password = dialog.getPassword()
        }

        val objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val settings = storer.settings

        return BitbucketClient(
                createHttpClient(),
                HttpAuthRequestFactory(settings.login, String(password)),
                URL(settings.url), settings.project, settings.slug, settings.login,
                objectMapper.reader(), objectMapper.writer())
    }

    private fun createHttpClient(): HttpAsyncClient {
        val client = HttpAsyncClients.createDefault()
        client.start()
        return client
    }
}
