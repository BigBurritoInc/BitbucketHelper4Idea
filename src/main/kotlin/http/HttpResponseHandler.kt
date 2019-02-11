package http

import bitbucket.ClientListener
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectReader
import com.intellij.openapi.diagnostic.Logger
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import java.io.InputStream

class HttpResponseHandler<T>(
        private val objectReader: ObjectReader,
        private val bodyType: TypeReference<T>,
        private val listener: ClientListener) {

    fun handle(response: HttpResponse): T =
            process(response, { objectReader.forType(bodyType).readValue(it) }, listener)

    companion object {
        private val log = Logger.getInstance("HttpResponseHandler")
        /**
         * Use this handle when response body is empty or is not needed
         */
        fun handle(response: HttpResponse) {
            process(response, {})
        }

        private fun  <T> process(
                response: HttpResponse,
                mapper: (InputStream) -> T,
                listener: ClientListener = object: ClientListener {}
        ): T {
            val status = response.statusLine
            val statusCode =  status.statusCode
            log.debug("Status code received: $statusCode")
            return when (statusCode) {
                HttpStatus.SC_OK -> mapper.invoke(response.entity.content)
                HttpStatus.SC_FORBIDDEN -> {
                    listener.actionForbidden()
                    mapper.invoke(response.entity.content)
                }
                HttpStatus.SC_UNAUTHORIZED -> {
                    listener.invalidCredentials()
                    throw UnauthorizedException
                }
                else -> throw RuntimeException("Status code: ${status.statusCode}, reason ${status.reasonPhrase}")
            }
        }

    }

    object UnauthorizedException: RuntimeException()
}