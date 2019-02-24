import bitbucket.data.*
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PRTest {
    private val bob = PRParticipant(User("Bob", "bob@address.com",
            1, "Bobby", Links(listOf(Links.Link("self_1")))), false, ParticipantStatus.UNAPPROVED)
    private val aaron = PRParticipant(User("Aaron", "aaron@address.com",
            1, "Aa", Links(listOf(Links.Link("self_2")))), false, ParticipantStatus.NEEDS_WORK)
    private val repo = Repository("slug1", Project("key1"))
    private val from = Branch("br1", repo)
    private val to = Branch("br2", repo)
    private val props = PRProperties(1)
    private val pr1 = PR(1, "PR#1", bob, false, from, to, setOf(bob, aaron),
            Date(1), Date(2), props, Links(listOf(Links.Link("href0"))), 0)

    @Test
    fun testEquality() {
        //same as pr1, but Aaron and Bob are swapped
        val pr1Variation = PR(1, "PR#1", bob, false, from, to, setOf(aaron, bob),
                Date(1), Date(2), props, Links(listOf(Links.Link("href0"))), 0)
        assertEquals(pr1, pr1Variation)
    }

    @Test
    fun testInequalityIfBobApproved() {
        val bobApproved = PRParticipant(User("Bob", "bob@address.com",
                1, "Bobby", Links(listOf(Links.Link("self_1")))), true, ParticipantStatus.APPROVED)
        val pr1Variation = PR(1, "PR#1", bobApproved, false, from, to, setOf(bob, aaron),
                Date(1), Date(2), props, Links(listOf(Links.Link("href0"))), 0)
        assertNotEquals(pr1, pr1Variation)
    }

}