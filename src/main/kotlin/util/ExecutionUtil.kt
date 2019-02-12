package util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.concurrency.AppExecutorUtil
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

fun <T> doInAppExecutor(task: () -> T): CompletableFuture<T>{
    return CompletableFuture.supplyAsync(Supplier {
        task.invoke()
    }, AppExecutorUtil.getAppScheduledExecutorService())
}

fun invokeLater(runnable: () -> Unit) {
    ApplicationManager.getApplication().invokeLater(runnable)
}