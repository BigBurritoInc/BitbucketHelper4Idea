package ui

import bitbucket.data.PR
import com.intellij.openapi.application.ApplicationManager
import VCS
import Git

object Model {
    private val vcs: VCS = Git
    private var own: PRState = PRState()
    private var reviewing: PRState = PRState()
    private val listeners: MutableList<Listener> = ArrayList()

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

    fun checkout(pr: PR) {
        vcs.checkoutBranch(pr.fromBranch)
        branchChanged()
    }

    fun approve(pr: PR) {
        //todo: implement
        println("Approved! $pr")
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