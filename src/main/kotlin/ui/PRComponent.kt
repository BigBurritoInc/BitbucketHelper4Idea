package ui

import bitbucket.data.PR
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import java.io.IOException
import java.awt.Desktop.getDesktop
import java.awt.Desktop.isDesktopSupported
import java.net.URL


class PRComponent(val pr: PR): JPanel() {

    //todo: use idea colors
    private val selectedBg = Color(227, 241, 250)
    private val unselectedBg = Color(242, 242, 242)
    private val selectedBorder = Color(7, 135, 222)
    private val unselectedBorder = Color(205, 205, 205)
    private val approveGreen = Color(89, 168, 105)

    private val checkoutBtn = JButton("▼ Checkout")
    private val approveBtn = JButton("✓ Approve")


    init {
        layout = GridBagLayout()
        val c = GridBagConstraints()

        c.gridwidth = 3;
        c.anchor = GridBagConstraints.WEST

        val title = Link("http://google.com", pr.title)
        title.font = Font(title.font.name, Font.TRUETYPE_FONT,16)

        c.insets = Insets(4, 2, 4, 2)
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx = 0
        c.gridy = 0
        add(title, c)

        val to = JHtmlLabel("To branch: <i>${pr.toBranch}</i>")
        to.font = Font(to.font.name, Font.TRUETYPE_FONT, 12)

        c.ipady = 0
        c.gridx = 0
        c.gridy = 1
        add(to, c)

        c.gridwidth = 1;
        val by = JHtmlLabel("By: ${pr.author.user.displayName}")
        by.font = Font(by.font.name, Font.TRUETYPE_FONT, 12)
        c.gridx = 0
        c.gridy = 2
        add(by, c)

        approveBtn.preferredSize = Dimension(120, 20)
        approveBtn.addActionListener { Model.approve(pr) }
        approveBtn.foreground = approveGreen
        c.gridx = 1
        add(approveBtn, c)


        checkoutBtn.preferredSize = Dimension(120, 20)
        checkoutBtn.maximumSize = Dimension(120, 20)
        c.gridx = 2
        c.anchor = GridBagConstraints.EAST
        add(checkoutBtn, c)
        checkoutBtn.addActionListener { Model.checkout(pr) }

        border = BorderFactory.createLineBorder(unselectedBorder, 2)
    }

    fun currentBranchChanged(branch: String) {
        val isActive = pr.fromBranch == branch
        background = if (isActive) { selectedBg } else { unselectedBg }
        border = BorderFactory.createLineBorder(
                if (isActive) { selectedBorder } else { unselectedBorder }, 2)
        approveBtn.isVisible = isActive
        checkoutBtn.isVisible = !isActive
    }
}

class JHtmlLabel(txt: String): JTextPane() {
    init {
        contentType = "text/html"
        text = "<html>$txt</html>"
        isEditable = false
        background = null
        border = null
    }
}

class Link(url: String, txt: String): JButton() {
    init {
        text = "<HTML>$txt</HTML>"
        horizontalAlignment = SwingConstants.LEFT
        isBorderPainted = false
        isOpaque = false
        toolTipText = url
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
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