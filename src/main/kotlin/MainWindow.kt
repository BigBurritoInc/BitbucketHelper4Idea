import bitbucket.BitbucketClientFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentManager
import ui.*
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*


class MainWindow : ToolWindowFactory {

    var window: ToolWindow? = null

    override fun createToolWindowContent(prj: Project, window: ToolWindow) {
        this.window = window
        val cm = window.contentManager
        val reviewingPanel = createReviewPanel()
        val ownPanel = createOwnPanel()

        val reviewingContent = addTab(cm, wrapIntoJBScroll(reviewingPanel), "Reviewing")
        addTab(cm, wrapIntoJBScroll(ownPanel), "Created")
        val loginContent = addTab(cm, createLoginPanel(cm, reviewingContent), "Login")
        cm.setSelectedContent(loginContent)

        Model.addListener(reviewingPanel)
        Model.addListener(ownPanel)
    }

    private fun createLoginPanel(contentManager: ContentManager, reviewingContent: Content): JPanel {
        val wrapper = JPanel(BorderLayout())
        val passwordField = JPasswordField()

        val listener = {
            BitbucketClientFactory.password = passwordField.password
            UpdateTaskHolder.reschedule()
            passwordField.text = ""
            contentManager.setSelectedContent(reviewingContent)
        }

        passwordField.addKeyListener(object : KeyListener{
            override fun keyTyped(e: KeyEvent?) { }

            override fun keyPressed(e: KeyEvent?) {
                if (e != null && e.keyCode == KeyEvent.VK_ENTER)
                    listener.invoke()
            }

            override fun keyReleased(e: KeyEvent?) { }
        })

        val button = JButton("Login")
        button.addActionListener {listener.invoke()}

        val panel = JPanel(VerticalLayout(5))
        panel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        panel.add(passwordField)
        panel.add(button)
        wrapper.add(panel, BorderLayout.NORTH)
        return wrapper
    }

    private fun addTab(contentManager: ContentManager, component: JComponent, tabName: String): Content {
        val content = contentManager.factory.createContent(component, tabName, false)
        contentManager.addContent(content)
        return content
    }
}