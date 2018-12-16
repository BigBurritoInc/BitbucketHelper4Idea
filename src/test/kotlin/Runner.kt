import bitbucket.BitbucketClient
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import http.HttpAuthRequestFactory
import org.apache.http.impl.client.HttpClients
import java.net.URL

object Runner {
    @JvmStatic fun main(args: Array<String>) {
        val objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val client = BitbucketClient(
                HttpClients.createDefault(),
                HttpAuthRequestFactory(args[0], args[1]),
                URL(args[2]), args[3], args[3], args[0],
                objectMapper.reader(), objectMapper.writer())

        client.ownPRs().forEach { println("OwnPR: $it") }
        client.reviewedPRs().forEach { println("OwnPR: $it") }
    }
}