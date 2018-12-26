import bitbucket.BitbucketClient
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import http.HttpAuthRequestFactory
import org.apache.http.impl.client.HttpClients
import ui.Settings

object Runner {
    @JvmStatic fun main(args: Array<String>) {
        val objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val settings = Settings()
        settings.login = args[0]
        settings.url = args[2]
        settings.project = args[3]
        settings.slug = args[4]
        val client = BitbucketClient(
                HttpClients.createDefault(),
                HttpAuthRequestFactory(args[0], args[1]),
                settings,
                objectMapper.reader(), objectMapper.writer())

        client.ownPRs().forEach { println("OwnPR: $it") }
        client.reviewedPRs().forEach { println("OwnPR: $it") }
    }
}