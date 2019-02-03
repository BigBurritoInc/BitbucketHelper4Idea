package ui

import bitbucket.data.PR
import com.intellij.ui.components.panels.VerticalLayout
import java.awt.Component
import javax.swing.JPanel




abstract class Panel : JPanel(), Listener {
    companion object {
        const val GAP_BETWEEN_PR_COMPONENTS = 5
    }

    init {
        layout = VerticalLayout(GAP_BETWEEN_PR_COMPONENTS)
    }

    fun dataUpdated(diff: Diff) {
        diff.added.values.sortedBy { it.updatedAt }
                .forEach{ add(createPRComponent(it), 0) }

        val toRemove = mutableListOf<Component>()
        for (i in 0 until componentCount) {
            val component = getComponent(i) as PRComponent
            if (diff.removed.containsKey(component.pr.id))
                toRemove.add(component)
        }
        toRemove.forEach { remove(it) }
        for (i in 0 until componentCount) {
            val component = getComponent(i) as PRComponent
            if (diff.updated.containsKey(component.pr.id)) {
                remove(component)
                add(createPRComponent(diff.updated[component.pr.id]!!), i)
            }
        }
        repaint()
    }

    abstract fun createPRComponent(pr: PR): PRComponent

    override fun currentBranchChanged(branchName: String) {
        synchronized(treeLock) {
            for (i in 0 until componentCount) {
                val component = getComponent(i) as PRComponent
                component.currentBranchChanged(branchName)
            }
        }
    }
}