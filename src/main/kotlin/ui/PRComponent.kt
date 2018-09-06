package ui

import bitbucket.data.PR
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.IOException
import java.net.URL
import java.util.function.Consumer
import javax.swing.*


class PRComponent(val pr: PR): JPanel() {

    //todo: use idea colors
    private val selectedBg = Color(227, 241, 250)
    private val unselectedBg = Color(242, 242, 242)
    private val selectedBorder = Color(7, 135, 222)
    private val unselectedBorder = Color(205, 205, 205)
    private val approveColor = Color(89, 168, 105)

    private val checkoutBtn = JButton("▼ Checkout")
    private val approveBtn = JButton("Approve")

    private val title: Link

    init {
        layout = GridBagLayout()
        val c = GridBagConstraints()

        c.gridwidth = 3
        c.anchor = GridBagConstraints.WEST

        title = Link(pr.links.getSelfHref(), pr.title)
        title.font = Font(title.font.name, Font.TRUETYPE_FONT,18)

        c.insets = Insets(4, 20, 2, 2)
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        c.gridy = 0
        add(title, c)

        val to = JLabel("To: ${pr.toBranch}")
        to.preferredSize = Dimension(120, 30)
        to.font = Font(to.font.name, Font.TRUETYPE_FONT, 16)

        c.ipady = 0
        c.gridx = 0
        c.gridy = 1
        add(to, c)

        c.insets = Insets(4, 20, 10, 2)
        c.gridwidth = 1
        val by = JLabel("<html>By: <b>${pr.author.user.displayName}</b></html>")
        by.font = Font(by.font.name, Font.TRUETYPE_FONT, 16)
        by.preferredSize = Dimension(200, 30)
        c.weightx = 0.0
        c.gridx = 0
        c.gridy = 2
        add(by, c)

        c.weightx = 1.0
        c.fill = GridBagConstraints.EAST
        c.gridx = 2
        approveBtn.preferredSize = Dimension(120, 40)
        approveBtn.addActionListener {
            Model.approve(pr, Consumer {approved ->
                if (approved) {
                    approveBtn.text = "Approved"
                    approveBtn.isEnabled = false
                }
            })
        }
        approveBtn.font = Font(approveBtn.font.name, Font.PLAIN, 16)
        approveBtn.foreground = approveColor
        add(approveBtn, c)

        checkoutBtn.font = Font(checkoutBtn.font.name, Font.PLAIN, 16)
        checkoutBtn.preferredSize = Dimension(120, 40)
        checkoutBtn.maximumSize = Dimension(120, 30)

        add(checkoutBtn, c)
        checkoutBtn.addActionListener { Model.checkout(pr) }

        if (pr.reviewers.isNotEmpty()) {
            var reviewerOffset = 200
            pr.reviewers.forEach {
                c.insets = Insets(4, reviewerOffset, 10, 2)
                val picLabel = ReviewerComponentFactory.create(it)
                add(picLabel, c)
                //todo there is will be a problem then a number of reviewers is high. Better approach is
                //todo to show 2 first reviewers and to hide others in pop up menu
                reviewerOffset += 50
            }
        }

        border = BorderFactory.createLineBorder(unselectedBorder, 2)
    }

    fun currentBranchChanged(branch: String) {
        val isActive = pr.fromBranch == branch
        background = if (isActive) { selectedBg } else { unselectedBg }
        title.background = background
        border = BorderFactory.createLineBorder(
                if (isActive) { selectedBorder } else { unselectedBorder }, 2)
        approveBtn.isVisible = isActive
        checkoutBtn.isVisible = !isActive
    }
}

class Link(url: String, txt: String): JButton() {
    init {
        text = "<html>$txt</html>"
        horizontalAlignment = SwingConstants.LEFT
        isBorderPainted = false
        isOpaque = false
        isContentAreaFilled = false
        toolTipText = url
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        background = JPanel().background
        addActionListener(OpenUrlAction(URL(url)))
    }
}

class OpenUrlAction(val url: URL): ActionListener {
    override fun actionPerformed(e: ActionEvent?) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(url.toURI())
            } catch (e: IOException) {
                //todo ???
                println(e)
            }
        } else { //todo ???
            println("not desktop supported")
        }
    }

}