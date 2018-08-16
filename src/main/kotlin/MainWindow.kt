import MainWindow.Model.checkout
import MainWindow.Model.own
import bitbucket.data.PR
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.content.ContentManager
import java.awt.Color
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.LayoutManager

import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import java.awt.GridBagConstraints



class MainWindow : ToolWindowFactory {

    var window: ToolWindow? = null


    override fun createToolWindowContent(prj: Project, window: ToolWindow) {
        this.window = window
        val cm = window.contentManager
        val gbc = GridBagConstraints()
        gbc.fill = GridBagConstraints.VERTICAL
        val layout = GridBagLayout()

        val reviewingPanel = object : Panel(layout, gbc) {
            override fun ownUpdated(diff: Diff) {}

            override fun reviewedUpdated(diff: Diff) {
                dataUpdated(diff)
            }
        }

        val ownPanel = object : Panel(layout, gbc) {
            override fun ownUpdated(diff: Diff) {
                dataUpdated(diff)
            }

            override fun reviewedUpdated(diff: Diff) {}
        }
        addTab(cm, reviewingPanel, "Reviewing")
        addTab(cm, ownPanel, "Created")
        Model.addListener(reviewingPanel)
        Model.addListener(ownPanel)
    }

    private fun addTab(contentManager: ContentManager, component: JComponent, tabName: String) {
        val content = contentManager.factory.createContent(component, tabName, false)
        contentManager.addContent(content)
    }

    class PRComponent(val pr: PR): JButton(pr.title) {
        fun currentBranchChanged(branch: String) {
            background = if (pr.fromBranch == branch) {
                Color.CYAN
            } else {
                Color.GRAY
            }
        }
    }

    abstract class Panel(layout: LayoutManager?, val gbc: GridBagConstraints) : JPanel(layout), Listener {

        fun dataUpdated(diff: Diff) {
            diff.added.forEach{
                val prComponent = createPRComponent(it.value)
                add(prComponent, gbc)
            }

            synchronized(treeLock) {
                for (i in 0 until componentCount) {
                    val component = getComponent(i) as PRComponent
                    if (diff.removed.containsKey(component.pr.id))
                        remove(i)
                }
            }
            repaint()
        }

        private fun createPRComponent(pr: PR): PRComponent {
            val prComp = PRComponent(pr)
            prComp.addActionListener{ checkout(prComp.pr.fromBranch) }
            return prComp
        }

        override fun currentBranchChanged(branchName: String) {
            synchronized(treeLock) {
                for (i in 0 until componentCount) {
                    val component = getComponent(i) as PRComponent
                    component.currentBranchChanged(branchName)
                }
            }
        }
    }

    object Model {
        private val vcs: VCS = Git
        private var own: PRState = PRState()
        private var reviewing: PRState = PRState()
        private val listeners: MutableList<MainWindow.Listener> = ArrayList()

        fun updateOwnPRs(prs: List<PR>) {
            synchronized(this) {
                val diff = own.createDiff(prs)
                if (diff.hasAnyUpdates()) {
                    own = own.createNew(prs)
                    ApplicationManager.getApplication().invokeLater{ ownUpdated(diff) }
                }
            }
            branchChanged()
        }

        fun updateReviewingPRs(prs: List<PR>) {
            synchronized(this) {
                val diff = reviewing.createDiff(prs)
                if (diff.hasAnyUpdates()) {
                    reviewing = reviewing.createNew(prs)
                    ApplicationManager.getApplication().invokeLater{ reviewingUpdated(diff) }
                }
            }
            branchChanged()
        }

        private fun reviewingUpdated(diff: Diff) {
            listeners.forEach{ it.reviewedUpdated(diff) }
        }

        private fun ownUpdated(diff: Diff) {
            listeners.forEach{ it.ownUpdated(diff) }
        }

        fun checkout(branchName: String) {
            vcs.checkoutBranch(branchName)
            branchChanged()
        }

        private fun branchChanged() {
            ApplicationManager.getApplication().invokeLater {
                listeners.forEach{ it.currentBranchChanged(currentBranch()) }
            }
        }

        private fun currentBranch(): String {
            return vcs.currentBranch()
        }

        fun addListener(listener: Listener) {
            listeners.add(listener)
        }
    }

    interface Listener {
        fun ownUpdated(diff: Diff)
        fun reviewedUpdated(diff: Diff)
        fun currentBranchChanged(branchName: String)
    }

    class PRState(private val prsMap: Map<Long, PR> = HashMap()) {
        fun createDiff(prs: List<PR>): Diff {
            val newMap = prs.map { it.id to it }.toMap()
            val removed = prsMap.filterKeys { !newMap.containsKey(it) }
            val added = newMap.filterKeys { !prsMap.containsKey(it) }
            //todo: updated keys
            return Diff(added, emptyMap(), removed)
        }

        fun createNew(prs: List<PR>): PRState {
            return PRState(prs.map { it.id to it }.toMap())
        }
    }

    data class Diff(
            val added: Map<Long, PR>,
            val updated: Map<Long, PR>,
            val removed: Map<Long, PR>
    ) {
        fun hasAnyUpdates(): Boolean {
            return added.isNotEmpty() || updated.isNotEmpty() || removed.isNotEmpty()
        }
    }
}