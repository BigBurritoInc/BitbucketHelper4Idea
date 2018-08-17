import bitbucket.data.Branch
import bitbucket.data.PR
import bitbucket.data.PRAuthor
import bitbucket.data.User
import ui.Diff
import ui.createReviewPanel
import ui.wrapIntoScroll
import java.util.*
import javax.swing.JFrame
import javax.swing.JScrollPane
import kotlin.collections.HashMap

object PanelRunner {

    val br = "refs/heads/feature/TOSX-1980-it-is-a-feature-that-has-a-workitem-branch"

    @JvmStatic fun main(args: Array<String>) {
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

    private fun createPR(id: Long): PR {
        return PR(id, "This is a pull request submitted by a programmer, it has a long description and it is here with # $id",
                PRAuthor(User("har993", "billybobharley.is.here@tdameritrade.com", 2, "Billy Bob Harley")), false,
                Branch("$br$id"),
                Branch("ref/heads/feature/TOSX-1955-it-is-a-feature-that-has-a-story-branch"),
                Date(System.currentTimeMillis()), Date(System.currentTimeMillis()))
    }
}