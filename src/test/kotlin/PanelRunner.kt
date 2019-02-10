
import bitbucket.data.*
import ui.*
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import javax.swing.Icon
import javax.swing.JFrame
import javax.swing.SwingUtilities
import kotlin.collections.HashMap
import kotlin.collections.HashSet

object PanelRunner {

    val br = "feature/TOSX-1980-it-is-a-feature-that-has-a-workitem-branch"

    @JvmStatic
    fun main(args: Array<String>) {
        val frame = JFrame()
        awtExecutor = Executor { command -> SwingUtilities.invokeLater(command) }
        imagesSource = object : MediaSource<Icon> {
            override fun retrieve(url: URL): CompletableFuture<Icon> {
                val future = CompletableFuture<Icon>()
                future.complete(ReviewerComponentFactory.defaultAvatarIcon)
                return future
            }
        }
        val panel = createReviewPanel()
        val map = HashMap<Long, PR>()
        for (i in 0..20) {
            map[i.toLong()] = createPR(i.toLong(), i % 10)
        }
        panel.dataUpdated(Diff(map, emptyMap(), emptyMap(), emptyMap()))
        panel.currentBranchChanged("feature/TOSX-1980-it-is-a-feature-that-has-a-workitem-branch3")
        frame.contentPane.add(wrapIntoJBScroll(panel))

        frame.pack()
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
    }

    fun createPR(id: Long, reviewersCount: Int): PR {
        var title = "This is a pull request submitted by a programmer here with # $id"
        for (p in 0..id % 3)
            title += " more info"


        var to = "feature/TOSX-1955-it-is-a-feature-that-has-a-story-branch"

        for (k in 0..id % 4)
            to += "8984"
        val repo = Repository("slug", Project("project_key"))

        val reviewers = HashSet<PRParticipant>()
        if (reviewersCount != 0) {
            for (userId in 0..reviewersCount) {
                reviewers.add(PRParticipant(
                        User("UserName$userId", "username$userId@email.com", userId.toLong(), "FirstName$userId LastName$userId",
                                Links(listOf(Links.Link("https://www.atlassian.com/software/bitbucket")))),
                        userId % 2 == 0,
                        ParticipantStatus.values()[(userId % ParticipantStatus.values().size)]
                ))
            }
        }

        return PR(id, title,
                PRParticipant(User("har993", "billybobharley.is.here@tdameritrade.com", 2, "Billy Bob Harley",
                        Links(listOf(Links.Link("https://developer.atlassian.com/bitbucket/api/2/reference/")))), false, ParticipantStatus.UNAPPROVED),
                false,
                Branch("$br$id", repo),
                Branch(to, repo),
                reviewers,
                Date(System.currentTimeMillis()), Date(System.currentTimeMillis()),
                Links(listOf(Links.Link("https://developer.atlassian.com/bitbucket/api/2/reference/"))),0
        )
    }
}