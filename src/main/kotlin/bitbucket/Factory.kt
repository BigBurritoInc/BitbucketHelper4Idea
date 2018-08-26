package bitbucket

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.ui.DialogWrapper
import http.HttpAuthRequestFactory
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.nio.client.HttpAsyncClient
import ui.Storer
import java.net.URL
import javax.swing.JComponent
import javax.swing.JPasswordField

fun createClient(): BitbucketClient {
    val info = BitbucketInfo()

    if (PostStartupActivity.password == null) {
        val dialog = PasswordPromptDialog(false)
        dialog.showAndGet()
        PostStartupActivity.password = dialog.getPassword()
    }

    val objectMapper = ObjectMapper()
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    return BitbucketClient(
            createHttpClient(),
            HttpAuthRequestFactory(info.user, String(PostStartupActivity.password!!)),
            info.baseURL, info.project, info.repoSlug, info.user,
            objectMapper.reader(), objectMapper.writer())
}

private fun createHttpClient(): HttpAsyncClient {
    val client = HttpAsyncClients.createDefault()
    client.start()
    return client
}

class BitbucketInfo {
    val storer = ServiceManager.getService<Storer>(CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext())!!, Storer::class.java);
    val baseURL: URL = URL(storer.settings.url)
    val project: String = storer.settings.project
    val repoSlug: String = storer.settings.slug
    val user: String = storer.settings.login
}

class PasswordPromptDialog(canBeParent: Boolean) : DialogWrapper(canBeParent) {
    val passwordField = JPasswordField()
    init {
        title = "MyBitbucket"
        init()
    }
    override fun createCenterPanel(): JComponent? {
        return passwordField
    }

    fun getPassword(): CharArray {
        return passwordField.password
    }
}