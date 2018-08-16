package http

import org.apache.http.HttpResponse
import org.apache.http.concurrent.FutureCallback
import rx.Observer
import java.lang.Exception

abstract class ResponseCallback<T>(private val observer: Observer<T>): FutureCallback<HttpResponse> {

    abstract fun onSuccess(response: HttpResponse, observer: Observer<T>)

    override fun cancelled() {
        observer.onError(RuntimeException("Unexpected cancel"))
    }

    override fun completed(response: HttpResponse) {
        if (response.statusLine.statusCode == 200) {
            onSuccess(response, observer)
        } else {
            val status = response.statusLine
            //todo: if authentication is required there could be a valid statusCode != 200
            observer.onError(RuntimeException("Status code: ${status.statusCode}, reason ${status.reasonPhrase}"))
        }
    }

    override fun failed(e: Exception?) {
        observer.onError(e)
    }
}