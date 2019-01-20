import bitbucket.BitbucketClientFactory
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentManager
import ui.*
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.net.ConnectException
import javax.swing.*


class MainWindow : ToolWindowFactory, DumbAware {

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
        val messageField = JBLabel()
        val button = JButton("Login")
        button.isEnabled = false
        val listener = {
            try {
                messageField.text = ""
                getStorerService().settings.validate()
                BitbucketClientFactory.password = passwordField.password
                UpdateTaskHolder.scheduleNew()
                passwordField.text = ""
                button.isEnabled = false
                contentManager.setSelectedContent(reviewingContent)
            } catch (e: ConfigurationException) {
                messageField.text = e.title + ". " + e.message
            }
        }
        button.addActionListener {listener.invoke()}

        passwordField.addKeyListener(object : KeyListener{
            override fun keyTyped(e: KeyEvent?) { }

            override fun keyPressed(e: KeyEvent?) {
                if (e != null && e.keyCode == KeyEvent.VK_ENTER)
                    listener.invoke()
            }

            override fun keyReleased(e: KeyEvent?) {
                button.isEnabled = !passwordField.password.isEmpty()
            }
        })
        val panel = JPanel(VerticalLayout(5))
        panel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        panel.add(passwordField)
        panel.add(button)
        panel.add(messageField)
        wrapper.add(panel, BorderLayout.NORTH)
        return wrapper
    }

    private fun addTab(contentManager: ContentManager, component: JComponent, tabName: String): Content {
        val content = contentManager.factory.createContent(component, tabName, false)
        contentManager.addContent(content)
        return content
    }
}