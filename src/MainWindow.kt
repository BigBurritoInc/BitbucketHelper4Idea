import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.content.ContentManager
import javax.swing.JComponent

class MainWindow : ToolWindowFactory {
    override fun createToolWindowContent(prj: Project, window: ToolWindow) {
        val cm = window.contentManager
        //todo: add some real content here
        addTab(cm, JBLabel("Reviewing"), "Reviewing")
        addTab(cm, JBLabel("Created"), "Created")
    }

    private fun addTab(contentManager: ContentManager, component: JComponent, tabName: String) {
        val content = contentManager.factory.createContent(component, tabName, false)
        contentManager.addContent(content)
    }
}