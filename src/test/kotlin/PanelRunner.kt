import bitbucket.data.*
import ui.*
import java.awt.image.BufferedImage
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.SwingUtilities
import kotlin.collections.HashMap

object PanelRunner {

    val br = "feature/TOSX-1980-it-is-a-feature-that-has-a-workitem-branch"
    val image = javaClass.classLoader.getResource("avatar.png")

    @JvmStatic
    fun main(args: Array<String>) {
        val frame = JFrame()
        awtExecutor = Executor { command -> SwingUtilities.invokeLater(command) }
        imagesSource = object: MediaSource<BufferedImage> {
            override fun retrieve(url: URL): CompletableFuture<BufferedImage> {
                val future = CompletableFuture<BufferedImage>()
                future.complete(ImageIO.read(image))
                return future
            }
        }
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
                Links(listOf(Links.Link("https://www.atlassian.com/software/bitbucket")))), false),
                PRParticipant(User("reviewer2", "reviewer2@mail.com", 4, "Second Reviewer",
                Links(listOf(Links.Link("https://www.atlassian.com/software/bitbucket")))), true)),
                Date(System.currentTimeMillis()), Date(System.currentTimeMillis()),
                Links(listOf(Links.Link("https://developer.atlassian.com/bitbucket/api/2/reference/")))
        )
    }
}