import bitbucket.BitbucketClient
import bitbucket.BitbucketClientFactory
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.concurrency.AppExecutorUtil
import kotlinx.coroutines.experimental.Runnable
import ui.Model
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object UpdateTaskHolder {
    private val log = Logger.getInstance(UpdateTaskHolder::class.java)
    var future: ScheduledFuture<*>? = null // todo get rid of null, find more right way to store

    fun reschedule() {
        future?.cancel(true)
        val client = BitbucketClientFactory.createClient(createInvalidCredentialsAction())
        future = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(
                UpdateTask(client), 0, 15, TimeUnit.SECONDS)
    }

    private fun createInvalidCredentialsAction(): () -> Unit {
        return {
            Model.showNotification("Invalid BitBucket credentials! \n" +
                    "Or it could be required to enter captcha in the web-interface.", NotificationType.WARNING)

        }
    }

    class UpdateTask(private val client: BitbucketClient) : Runnable {
        override fun run() {
            log.debug("Running UpdateTask...")
            Model.updateReviewingPRs(client.reviewedPRs())
            Model.updateOwnPRs(client.ownPRs())
        }
    }
}
