package ui

import bitbucket.data.PR
import bitbucket.data.PRParticipant
import com.intellij.ide.BrowserUtil
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.UIUtil.ComponentStyle.MINI
import java.awt.*
import java.awt.image.BufferedImage
import java.net.URL
import java.util.function.Consumer
import javax.swing.*


class PRComponent(val pr: PR, private val imagesSource: MediaSource): JPanel() {

    private val approveColor = Color(89, 168, 105)

    private val checkoutBtn = JButton("▼ Checkout")
    private val approveBtn = JButton("Approve")

    private val title: Link
    private val toBranch: JBLabel
    private val author: JBLabel
    private var reviewerOffset = 200
    private val c = GridBagConstraints()

    init {
        layout = GridBagLayout()

        c.gridwidth = 3
        c.anchor = GridBagConstraints.WEST

        title = Link(URL(pr.links.getSelfHref()), pr.title)
        c.insets = Insets(4, 18, 2, 2)
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        c.gridy = 0
        add(title, c)

        toBranch = JBLabel("To: ${pr.toBranch}", MINI)
        toBranch.preferredSize = Dimension(120, 30)
        c.insets = Insets(4, 20, 0, 2)
        c.ipady = 0
        c.gridx = 0
        c.gridy = 1
        add(toBranch, c)

        c.gridwidth = 1
        author = JBLabel("<html>By: <b>${pr.author.user.displayName}</b></html>", MINI)
        author.preferredSize = Dimension(200, 30)
        c.weightx = 0.0
        c.gridx = 0
        c.gridy = 2
        c.insets = Insets(4, 20, 8, 2)
        add(author, c)

        c.weightx = 1.0
        c.fill = GridBagConstraints.EAST
        c.gridx = 2
        val buttonSize = Dimension(120, 24)
        approveBtn.preferredSize = buttonSize
        approveBtn.addActionListener {
            Model.approve(pr, Consumer {approved ->
                if (approved) {
                    approveBtn.text = "Approved"
                    approveBtn.isEnabled = false
                }
            })
        }
        approveBtn.foreground = approveColor
        approveBtn.font = UIUtil.getButtonFont()
        add(approveBtn, c)

        checkoutBtn.preferredSize = buttonSize
        checkoutBtn.maximumSize = buttonSize
        checkoutBtn.font = UIUtil.getButtonFont()
        add(checkoutBtn, c)
        checkoutBtn.addActionListener { Model.checkout(pr) }

        val reviewers = pr.reviewers.sortedWith(compareByDescending { it.approved })

        if (reviewers.isNotEmpty()) {
            reviewers.forEach {reviewer ->
                imagesSource.retrieveImage(URL(reviewer.user.links.getIconHref()))
                        .thenApply { image -> addReviewerImage(reviewer, image) }
            }
        }

        border = UIUtil.getTextFieldBorder()
    }

    fun currentBranchChanged(branch: String) {
        val isActive = pr.fromBranch == branch
        background = UIUtil.getListBackground(isActive)
        setComponentsForeground(UIUtil.getListForeground(isActive))
        title.background = UIUtil.getListBackground(isActive)
        approveBtn.isVisible = isActive
        checkoutBtn.isVisible = !isActive
    }

    private fun setComponentsForeground(color: Color) {
        title.foreground = color
        toBranch.foreground = color
        author.foreground = color
    }

    private fun addReviewerImage(reviewer: PRParticipant, image: BufferedImage) {
        synchronized(treeLock) {
            c.insets = Insets(4, reviewerOffset, 10, 2)
            val picLabel = ReviewerComponentFactory.create(reviewer, image)
            add(picLabel, c)
            //todo there is will be a problem then a number of reviewers is high. Better approach is
            //todo to show 2 first reviewers and to hide others in pop up menu
            reviewerOffset += 30
        }
    }
}

class Link(url: URL, txt: String): JButton() {
    private val innerMargin = Insets(0, 2, 1, 0);

    init {
        text = txt
        horizontalAlignment = SwingConstants.LEFT
        isBorderPainted = false
        isOpaque = false
        isContentAreaFilled = false
        toolTipText = url.toString()
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        margin = innerMargin
        isRolloverEnabled = false
        addActionListener { BrowserUtil.browse(url) }
    }
}