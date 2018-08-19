package ui

import bitbucket.data.PR
import com.intellij.openapi.application.ApplicationManager
import VCS
import Git
import bitbucket.createClient
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.Notifications
import com.intellij.notification.NotificationType
import com.intellij.util.concurrency.AppExecutorUtil


object Model {
    private val vcs: VCS = Git
    private val initOwnState = PRState()
    private val initReviewingState = PRState()

    private var own: PRState = initOwnState
    private var reviewing: PRState = initReviewingState
    private val listeners: MutableList<Listener> = ArrayList()
    val notificationGroup = NotificationGroup("MyBitbucket group",
            NotificationDisplayType.BALLOON, true)

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
                notifyNewPR(diff)
                reviewing = reviewing.createNew(prs)
                ApplicationManager.getApplication().invokeLater{ reviewingUpdated(diff) }
            }
        }
        branchChanged()
    }

    private fun notifyNewPR(diff: Diff) {
        ApplicationManager.getApplication().invokeLater{
            if (diff.added.isNotEmpty()) {
                val message = if (diff.added.size == 1) {
                    val pr = diff.added.values.iterator().next()
                    "New Pull Request is available: \n ${pr.title} \n By: <b>${pr.author.user.displayName}</b>"
                } else {
                    "${diff.added.size} pull requests are available"
                }
                val notification = notificationGroup.createNotification(message, NotificationType.INFORMATION)
                Notifications.Bus.notify(notification, Git.currentProject())
            }
        }
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
        AppExecutorUtil.getAppScheduledExecutorService().execute {
            try {
                val result = createClient().approve(pr)
                if (result)
                    approvedNotification(pr)
            } catch (e: Exception) {
                //todo: handle
                print(e)
            }

        }
    }

    fun approvedNotification(pr: PR) {
        ApplicationManager.getApplication().invokeLater{
            val message = "PR #${pr.id} is approved"
            val notification = notificationGroup.createNotification(message, NotificationType.INFORMATION)
            Notifications.Bus.notify(notification, Git.currentProject())
        }
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