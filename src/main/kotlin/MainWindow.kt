import MainWindow.Model.checkout
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.content.ContentManager
import java.awt.Color
import java.awt.GridLayout
import java.awt.LayoutManager
import java.util.*
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class MainWindow : ToolWindowFactory {
    override fun createToolWindowContent(prj: Project, window: ToolWindow) {
        val cm = window.contentManager
        //todo: add some real content here
        val button = PRComponent("master", "master")
        val button2 = PRComponent("release/414_cucumber", "release/414_cucumber")
        val panel = Panel(GridLayout(2, 1))
        val components = listOf(button, button2)
        panel.addComponents(components)
        panel.stateChanged(Model.currentBranch())
        addTab(cm, panel, "Reviewing")
        addTab(cm, JBLabel("Created"), "Created")
    }

    private fun addTab(contentManager: ContentManager, component: JComponent, tabName: String) {
        val content = contentManager.factory.createContent(component, tabName, false)
        contentManager.addContent(content)
    }

    class PRComponent(text: String, val branchName: String): JButton(text) {
        //todo: No active branch on plugin's start
        fun currentStateChanged(branch: String) {
            if (branchName == branch)
                background = Color.CYAN
        }
    }

    class Panel(layout: LayoutManager?) : JPanel(layout) {
        private val pullRequests: MutableList<PRComponent> = ArrayList()

        fun addComponents(components: List<PRComponent>) {
            for (component: PRComponent in components) {
                component.addActionListener{ checkout(component.branchName) }
                add(component)
                pullRequests.add(component)
            }
        }

        fun stateChanged(branchName: String) {
            for (pr: PRComponent in pullRequests)
                pr.currentStateChanged(branchName)
        }
    }

    object Model {
        private val vcs: VCS = Git

        fun checkout(branchName: String) {
            vcs.checkoutBranch(branchName)
        }

        fun currentBranch(): String {
            return vcs.currentBranch()
        }
    }
}