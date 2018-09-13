import bitbucket.BitbucketClient
import bitbucket.BitbucketClientFactory
import com.intellij.util.concurrency.AppExecutorUtil
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.runBlocking
import ui.Model
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

object UpdateTaskHolder {
    var future: ScheduledFuture<*>? = null // todo get rid of null, find more right way to store

    fun reschedule() {
        future?.cancel(true)
        val client = BitbucketClientFactory.createClient()
        future = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(
                UpdateTask(client), 0, 15, TimeUnit.SECONDS)
    }

    class UpdateTask(private val client: BitbucketClient): Runnable {
        override fun run() {
            runBlocking {
                process(client.reviewedPRs(), Consumer {
                    Model.updateReviewingPRs(it)
                })
                process(client.ownPRs(), Consumer {
                    Model.updateOwnPRs(it)
                })
            }
        }

        private suspend fun <T> process(obs: Deferred<List<T>>, consumer: Consumer<List<T>>) {
            try {
                val prs = obs.await()
                consumer.accept(prs)
            } catch (e: Exception) {
                //todo: handle properly
                println(e)
            }
        }
    }
}