package bitbucket

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import http.HttpAuthRequestFactory
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.nio.client.HttpAsyncClient
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

fun createClient(): BitbucketClient {
    val info = bitbucketInfo()
    val objectMapper = ObjectMapper()
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    return BitbucketClient(
            createHttpClient(),
            HttpAuthRequestFactory(info.user, info.password),
            info.baseURL, info.project, info.repoSlug, objectMapper.reader(), objectMapper.writer())
}

private fun createHttpClient(): HttpAsyncClient {
    val client = HttpAsyncClients.createDefault()
    client.start()
    return client
}

private fun loadProperties(): Properties {
    val br = Files.newBufferedReader(Paths.get("C:\\opt\\repo.properties"))
    val props = Properties()
    props.load(br)
    br.close()
    return props
}

private fun bitbucketInfo(): BitbucketInfo {
    return BitbucketInfo(loadProperties())
}

class BitbucketInfo(props: Properties) {
    val baseURL: URL = URL(props.getProperty("url"))
    val project: String = props.getProperty("project")
    val repoSlug: String = props.getProperty("slug")
    val user: String = props.getProperty("user")
    val password: String = props.getProperty("pass")
}