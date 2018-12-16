import bitbucket.BitbucketClient
import bitbucket.BitbucketClientFactory
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.concurrency.AppExecutorUtil
import http.HttpResponseHandler
import ui.Model
import java.io.IOException
import java.lang.RuntimeException
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object UpdateTaskHolder {
    private val log = Logger.getInstance(UpdateTaskHolder::class.java)
    private val lock = Object()
    var task: CancellableTask = DummyTask()

    fun reschedule() {
        synchronized(lock) {
            //this lock is needed to make task initialization atomic
            //if you want to cancel this task from another thread, you will have to wait until this block completes
            task.cancel()
            val client = BitbucketClientFactory.createClient(createInvalidCredentialsAction())
            task = UpdateTask(client)
            val future = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(
                    task, 0, 15, TimeUnit.SECONDS)
            task.setFuture(future)
        }
    }

    private fun createInvalidCredentialsAction(): () -> Unit {
        return {
            Model.showNotification("Invalid BitBucket credentials! \n" +
                    "Or it could be required to enter captcha in the web-interface.", NotificationType.WARNING)

        }
    }

    class UpdateTask(private val client: BitbucketClient) : CancellableTask {
        @Volatile
        var taskFuture: ScheduledFuture<*>? = null

        override fun run() {
            try {
                log.debug("Running UpdateTask...")
                Model.updateReviewingPRs(client.reviewedPRs())
                Model.updateOwnPRs(client.ownPRs())
            } catch (e: HttpResponseHandler.UnauthorizedException) {
                println("UnauthorizedException")
                cancel()
            } catch (e: IOException) {
                println("IOException: ${e.message}")
                log.warn(e)
                Model.showNotification("Error while trying to connect to a remote host: ${e.message} \n" +
                        "Either myBitbucket settings are invalid or the host is unreachable",
                        NotificationType.WARNING)
            } catch (e: Exception) {
                println("Error while trying to execute update task: ${e.message}")
                log.warn(e)
            }
        }

        override fun setFuture(future: ScheduledFuture<*>) {
            this.taskFuture = future
        }

        override fun cancel() {
            synchronized(lock) {
                taskFuture?.cancel(true)
            }
        }
    }

    //This class does nothing
    class DummyTask: CancellableTask {
        override fun setFuture(future: ScheduledFuture<*>) {}
        override fun cancel() {}
        override fun run() {}
    }

    interface CancellableTask: Runnable {
        fun setFuture(future: ScheduledFuture<*>)
        fun cancel()
    }
}
