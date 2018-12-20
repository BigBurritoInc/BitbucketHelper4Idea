package ui

import bitbucket.data.PR
import bitbucket.data.PRParticipant
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.UIUtil.ComponentStyle.MINI
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.net.URL
import java.util.*
import java.util.concurrent.Executor
import java.util.function.Consumer
import javax.swing.*


open class PRComponent(
        val pr: PR,
        private val imagesSource: MediaSource<BufferedImage>,
        private val awtExecutor: Executor) : JPanel() {

    private val approveColor = Color(89, 168, 105)

    private val checkoutBtn = JButton("▼ Checkout")
    private val approveBtn = JButton("Approve")

    private val title: Link
    private val toBranch: JBLabel
    private val author: JBLabel
    private val reviewersPanel: ReviewersPanel
    private val c = GridBagConstraints()

    init {
        layout = GridBagLayout()

        c.gridwidth = 3
        c.anchor = GridBagConstraints.WEST

        title = Link(URL(pr.links.getSelfHref()), pr.title)
        c.insets = Insets(4, 18, 2, 2)
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

        c.weightx = 0.0
        c.gridx = 1
        val buttonSize = Dimension(120, 24)
        addApproveButton(buttonSize)
        checkoutBtn.preferredSize = buttonSize
        checkoutBtn.maximumSize = buttonSize
        checkoutBtn.font = UIUtil.getButtonFont()
        add(checkoutBtn, c)
        checkoutBtn.addActionListener { Model.checkout(pr) }

        c.gridx = 2
        c.anchor = GridBagConstraints.WEST
        c.weightx = 1.0 //let the whole PR panel stretch by resizing the right side of the reviewers panel
        reviewersPanel = ReviewersPanel(ArrayList(pr.reviewers), imagesSource, awtExecutor)
        add(reviewersPanel, c)
        border = UIUtil.getTextFieldBorder()
        maximumSize = Dimension(Integer.MAX_VALUE, 160)
    }

    open fun addApproveButton(buttonSize: Dimension) {
        approveBtn.preferredSize = buttonSize
        approveBtn.addActionListener {
            Model.approve(pr, Consumer { approved ->
                if (approved) {
                    approveBtn.text = "Approved"
                    approveBtn.isEnabled = false
                }
            })
        }
        approveBtn.foreground = approveColor
        approveBtn.font = UIUtil.getButtonFont()
        add(approveBtn, c)
    }

    fun currentBranchChanged(branch: String) {
        val isActive = pr.fromBranch == branch
        background = UIUtil.getListBackground(isActive)
        reviewersPanel.background = background
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
}

class Link(url: URL, txt: String) : JButton() {
    private val innerMargin = Insets(0, 2, 1, 0)

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

/** A pull-request where an author is yourself */
class OwnPRComponent(ownPR: PR,
                     imagesSource: MediaSource<BufferedImage>,
                     awtExecutor: Executor)
    : PRComponent(ownPR, imagesSource, awtExecutor) {

    override fun addApproveButton(buttonSize: Dimension) {
        //Do not add an approve button for own PRs
    }
}

class ReviewersPanel(reviewers: MutableList<PRParticipant>,
                     imagesSource: MediaSource<BufferedImage>,
                     awtExecutor: Executor) : JPanel(FlowLayout(FlowLayout.LEADING, 2, 2)) {
    companion object {
        const val ALWAYS_DISPLAY_REVIEWERS_COUNT = 5
    }

    init {
        reviewers.sortWith(Comparator { o1, o2 -> o1.status.compareTo(o2.status) })
        val labels: Map<PRParticipant, ReviewerItem> = reviewers.associateWith { prParticipant -> ReviewerItem(prParticipant) }

        val alwaysVisibleReviewerCount = Math.min(ALWAYS_DISPLAY_REVIEWERS_COUNT, reviewers.size)

        reviewers.take(alwaysVisibleReviewerCount).forEach { add(labels[it]) }

        val reviewersInCombo = reviewers.size - alwaysVisibleReviewerCount
        if (reviewersInCombo > 0) {
            val otherReviewersButton = JButton("+$reviewersInCombo")
            add(otherReviewersButton)
            val height = preferredSize.height
            otherReviewersButton.preferredSize = Dimension(height, height)
            val menu = JBPopupMenu()
            reviewers.takeLast(reviewersInCombo).forEach { prParticipant: PRParticipant ->
                val itemPanel = JPanel(FlowLayout(FlowLayout.LEFT))
                itemPanel.add(labels[prParticipant])
                itemPanel.add(JLabel(prParticipant.user.displayName))
                menu.add(itemPanel)
            }
            otherReviewersButton.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    menu.show(otherReviewersButton, e.x, e.y)
                }
            })
            add(menu)
        }

        labels.forEach { prParticipant: PRParticipant, label: ReviewerItem ->
            imagesSource.retrieve(URL(prParticipant.user.links.getIconHref()))
                    .thenApply { image -> ReviewerComponentFactory.createIconForPrParticipant(image) }
                    .thenAcceptAsync(Consumer { icon -> label.setAvatar(icon) }, awtExecutor)
        }
    }
}

class ReviewerItem(reviewer: PRParticipant) : JLayeredPane() {
    private val avatarLabel: JLabel = JLabel(ReviewerComponentFactory.defaultAvatarIcon)

    companion object {
        val AVATAR_Z_INDEX = Integer(0)
        val STATUS_ICON_Z_INDEX = Integer(1)
    }

    init {
        val avatarSize = ReviewerComponentFactory.avatarSize
        val statusIconSize = ReviewerComponentFactory.statusIconSize
        val size = avatarSize + statusIconSize / 3
        preferredSize = Dimension(size, size)
        avatarLabel.setBounds(0, statusIconSize / 3, avatarSize, avatarSize)
        add(avatarLabel, AVATAR_Z_INDEX)
        val statusIcon = ReviewerComponentFactory.getStatusIcon(reviewer)
        if (statusIcon != null) {
            val statusLabel = JLabel(statusIcon)
            statusLabel.setBounds(size - statusIconSize, 0, statusIconSize, statusIconSize)
            add(statusLabel, STATUS_ICON_Z_INDEX)
        }
    }

    fun setAvatar(icon: Icon) {
        avatarLabel.icon = icon
    }
}