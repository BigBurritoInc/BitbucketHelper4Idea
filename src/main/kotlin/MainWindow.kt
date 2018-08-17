import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentManager
import ui.*
import ui.Panel
import java.awt.*

import javax.swing.JComponent
import javax.swing.JScrollPane




class MainWindow : ToolWindowFactory {

    var window: ToolWindow? = null

    override fun createToolWindowContent(prj: Project, window: ToolWindow) {
        this.window = window
        val cm = window.contentManager
        val reviewingPanel = createReviewPanel()
        val ownPanel = createOwnPanel()
        addTab(cm, wrapIntoScroll(reviewingPanel), "Reviewing")
        addTab(cm, wrapIntoScroll(ownPanel), "Created")
        Model.addListener(reviewingPanel)
        Model.addListener(ownPanel)
    }

    private fun addTab(contentManager: ContentManager, component: JComponent, tabName: String) {
        val content = contentManager.factory.createContent(component, tabName, false)
        contentManager.addContent(content)
    }
}