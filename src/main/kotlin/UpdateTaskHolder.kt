import bitbucket.BitbucketClient
import bitbucket.BitbucketClientFactory
import bitbucket.ClientListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.concurrency.AppExecutorUtil
import http.HttpResponseHandler
import ui.Model
import java.io.IOException
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object UpdateTaskHolder {
    private val log = Logger.getInstance(UpdateTaskHolder::class.java)
    private val lock = Object()
    var task: CancellableTask = DummyTask()

    fun reschedule() {
        createAndRun { task.createToReschedule(it) }
    }

    fun scheduleNew() {
        createAndRun { task.createNew(it) }
    }

    private fun createAndRun(factory: (BitbucketClient) -> CancellableTask) {
        synchronized(lock) {
            //this lock is needed to make task initialization atomic
            //if you want to cancel this task from another thread, you will have to wait until this block completes
            task.cancel()
            val client = BitbucketClientFactory.createClient(NotifyingClientListener())
            task = factory.invoke(client)
            val future = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(
                    task, 0, 15, TimeUnit.SECONDS)
            task.setFuture(future)
        }
    }

    class UpdateTask(private val client: BitbucketClient) : CancellableTask {
        override fun createToReschedule(client: BitbucketClient): CancellableTask {
            return UpdateTask(client)
        }

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
        override fun createToReschedule(client: BitbucketClient): CancellableTask {
            return DummyTask()
        }
        override fun setFuture(future: ScheduledFuture<*>) {}
        override fun cancel() {}
        override fun run() {}
    }

    interface CancellableTask: Runnable {
        fun setFuture(future: ScheduledFuture<*>)
        fun createNew(client: BitbucketClient): CancellableTask {
            return UpdateTask(client)
        }
        fun createToReschedule(client: BitbucketClient): CancellableTask
        fun cancel()
    }

    class NotifyingClientListener: ClientListener {
        private val errorCounter: AtomicInteger = AtomicInteger(0)

        override fun invalidCredentials() {
            Model.showNotification("Invalid BitBucket credentials! \n" +
                    "Or it could be required to enter captcha in the web-interface.", NotificationType.WARNING)

        }

        override fun requestFailed(e: Exception) {
            log.error("Request failed", e)
            Model.showNotification(
                    "Request to BitBucket failed, it may be unreachable or the settings are incorrect",
                    NotificationType.ERROR)
            val errors = errorCounter.incrementAndGet()
            if (errors > 5) {
                task.cancel()
                log.warn("UpdateTask is cancelled due to the high request error rate")
            }
        }
    }
}
