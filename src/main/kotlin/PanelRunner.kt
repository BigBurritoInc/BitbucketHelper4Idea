import bitbucket.data.*
import ui.Diff
import ui.createReviewPanel
import ui.wrapIntoScroll
import java.util.*
import javax.swing.JFrame
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

    fun createPR(id: Long): PR {
        var title = "This is a pull request submitted by a programmer here with # $id"
        for (p in 0 .. id % 3)
           title += " more info"


        var to = "feature/TOSX-1955-it-is-a-feature-that-has-a-story-branch"

        for (k in 0 .. id % 4)
            to += "8984"

        return PR(id, title,
                    PRParticipant(User("har993", "billybobharley.is.here@tdameritrade.com", 2, "Billy Bob Harley"), false), false,
                    Branch("$br$id"),
                    Branch(to), emptyList(),
                    Date(System.currentTimeMillis()), Date(System.currentTimeMillis()))
    }
}