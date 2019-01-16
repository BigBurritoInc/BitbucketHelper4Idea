package ui

import bitbucket.data.PR
import java.awt.GridBagLayout
import javax.swing.JPanel
import java.awt.GridBagConstraints
import java.awt.Insets


abstract class Panel : JPanel(), Listener {

    private val gbc: GridBagConstraints = GridBagConstraints()

    init {
        val layout = GridBagLayout()
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        gbc.gridx = 0
        gbc.anchor = GridBagConstraints.WEST
        gbc.insets = Insets(3, 3, 3, 3)
        setLayout(layout)
    }

    fun dataUpdated(diff: Diff) {
        diff.added.values.sortedBy { it.updatedAt }
                .forEach{ add(createPRComponent(it), gbc, 0) }

        synchronized(treeLock) {
            for (i in 0 until componentCount) {
                val component = getComponent(i) as PRComponent
                if (diff.removed.containsKey(component.pr.id))
                    remove(i)
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