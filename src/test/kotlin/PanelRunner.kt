import bitbucket.data.*
import ui.Diff
import ui.createReviewPanel
import ui.wrapIntoScroll
import java.util.*
import javax.swing.JFrame
import kotlin.collections.HashMap

object PanelRunner {

    val br = "feature/TOSX-1980-it-is-a-feature-that-has-a-workitem-branch"

    @JvmStatic
    fun main(args: Array<String>) {
        val frame = JFrame()
        val panel = createReviewPanel()
        val map = HashMap<Long, PR>()
        for (i in 1..20) {
            map[i.toLong()] = createPR(i.toLong())
        }
        panel.dataUpdated(Diff(map, emptyMap(), emptyMap()))
        panel.currentBranchChanged("feature/TOSX-1980-it-is-a-feature-that-has-a-workitem-branch3")
        frame.contentPane.add(wrapIntoScroll(panel))

        frame.pack()
        frame.isVisible = true
    }

    fun createPR(id: Long): PR {
        var title = "This is a pull request submitted by a programmer here with # $id"
        for (p in 0..id % 3)
            title += " more info"


        var to = "feature/TOSX-1955-it-is-a-feature-that-has-a-story-branch"

        for (k in 0..id % 4)
            to += "8984"

        return PR(id, title,
                PRParticipant(User("har993", "billybobharley.is.here@tdameritrade.com", 2, "Billy Bob Harley",
                        Links(listOf(Links.Link("https://developer.atlassian.com/bitbucket/api/2/reference/")))), false),
                false,
                Branch("$br$id"),
                Branch(to), listOf(PRParticipant(User("reviewer1", "reviewer1@mail.com", 3, "First Reviewer",
                Links(listOf(Links.Link("http://trikky.ru/wp-content/blogs.dir/1/files/2013/03/spanch-bob.gif")))), false),
                PRParticipant(User("reviewer2", "reviewer2@mail.com", 4, "Second Reviewer",
                Links(listOf(Links.Link("http://artshop-vrn.ru/upload/iblock/954/95470f37aa56aaa96e454b45e8d1a539.jpg")))), true)),
                Date(System.currentTimeMillis()), Date(System.currentTimeMillis()),
                Links(listOf(Links.Link("https://developer.atlassian.com/bitbucket/api/2/reference/")))
        )
    }
}