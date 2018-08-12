package bitbucket

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import http.AuthHttpClient
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

fun createClient(): BitbucketClient {
    //todo normal try with res
    val br = Files.newBufferedReader(Paths.get("C:\\opt\\repo.properties"))
    val props = Properties()
    props.load(br)
    br.close()
    val objectMapper = ObjectMapper()
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    return BitbucketClient(
            AuthHttpClient(props.getProperty("user"), props.getProperty("pass")),
            URL(props.getProperty("url")),
            props.getProperty("project"),
            props.getProperty("slug"),
            objectMapper.reader(), objectMapper.writer())
}