import bitbucket.BitbucketClient
import bitbucket.createClient
import bitbucket.data.PagedResponse
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.util.concurrency.AppExecutorUtil
import rx.Observable
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class PostStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(
                UpdateTask(createClient()), 0, 30, TimeUnit.SECONDS)
    }

    class UpdateTask(private val client: BitbucketClient): Runnable {
        override fun run() {
            println("I was called!")
            process(client.requestReviewedPRs(), Consumer {
                MainWindow.Model.updateReviewingPRs(it)
            })
            process(client.requestOwnPRs(), Consumer {
                MainWindow.Model.updateOwnPRs(it)
            })
        }

        private fun <T> process(obs: Observable<PagedResponse<T>>, consumer: Consumer<List<T>>) {
            println("waiting...")
            try {
            val prs = obs.doOnError { print(it) }
                    .flatMap { Observable.from(it.values) }
                    .toList().toBlocking().toFuture().get()
                println("done!")
                consumer.accept(prs)
                println("accepted")
            } catch (e: Exception) {
                println("EXXXX $e")
            }

        }

        private fun invokeLater(runnable: Runnable) {
            ApplicationManager.getApplication().invokeLater(runnable)
        }
    }
}