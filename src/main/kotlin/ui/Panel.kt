package ui

import bitbucket.data.PR
import java.awt.Component
import javax.swing.JPanel
import javax.swing.BoxLayout




abstract class Panel : JPanel(), Listener {

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
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