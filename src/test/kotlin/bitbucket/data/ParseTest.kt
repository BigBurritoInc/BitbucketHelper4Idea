package bitbucket.data

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

/**
 * Some tests to check PR deserialization from json
 *
 * @author Sergey Lukashevich
 */
class ParseTest {
    private val objMapper = ObjectMapper()
    private val prTypeRef = object: TypeReference<PR>() { }

    @Before
    fun setUp() {
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Test
    fun testParsePR() {
        // Simple test that only checks that PR was parsed from JSON. No other checks here.
        val objectReader = objMapper.reader()
        val pr = objectReader.forType(prTypeRef).readValue<PR>(javaClass.getResourceAsStream("pr_sample.json"))
        assertNotNull(pr)
    }

    @Test
    fun testParsePRWithoutEmail() {
        // Some reviewers can be without email. Here we just check that such json is parsed correctly
        val objectReader = objMapper.reader()
        val pr = objectReader.forType(prTypeRef).readValue<PR>(javaClass.getResourceAsStream("pr_sample_noemail.json"))
        assertNotNull(pr)
    }
}