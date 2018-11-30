import bitbucket.BitbucketClient
import bitbucket.BitbucketClientFactory
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.concurrency.AppExecutorUtil
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Runnable
import kotlinx.coroutines.experimental.runBlocking
import ui.Model
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

object UpdateTaskHolder {
    private val log = Logger.getInstance(UpdateTaskHolder::class.java)
    var future: ScheduledFuture<*>? = null // todo get rid of null, find more right way to store

    fun reschedule() {
        future?.cancel(true)
        val client = BitbucketClientFactory.createClient()
        future = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(
                UpdateTask(client), 0, 15, TimeUnit.SECONDS)
    }

    class UpdateTask(private val client: BitbucketClient) : Runnable {
        override fun run() {
            log.debug("Running UpdateTask...")
            Model.updateReviewingPRs(client.reviewedPRs())
            Model.updateOwnPRs(client.ownPRs())
        }
    }
}
