package http

import com.ning.http.client.AsyncCompletionHandler
import com.ning.http.client.Response
import org.slf4j.LoggerFactory
import rx.Observer

abstract class ResponseHandler<T>(val observer: Observer<T>) : AsyncCompletionHandler<Void>() {

    companion object {
        val log = LoggerFactory.getLogger(ResponseHandler::class.java)
    }

    override fun onThrowable(t: Throwable?) {
        observer.onError(RuntimeException("Request Failure", t!!))
    }

    override fun onCompleted(response: Response?): Void? {
        if (response == null) {
            observer.onError(NullPointerException("response is null"))
            return null
        }

        if (response.statusCode != 200) {
            //todo: if authentication is required there could be a valid statusCode != 200
            observer.onError(RuntimeException("Status code: ${response.statusCode}, body ${response.responseBody}"))
            return null
        }
        log.debug("Response body: ${response.responseBody}")
        onSuccess(response, observer)
        return null
    }

    abstract fun onSuccess(response: Response, observer: Observer<T>)
}
