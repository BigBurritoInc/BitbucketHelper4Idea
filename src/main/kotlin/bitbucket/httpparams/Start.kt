package bitbucket.httpparams

import bitbucket.data.HttpRequestParameter
import com.palominolabs.http.url.UrlBuilder

open class Start(private val index: Int): HttpRequestParameter {
    override fun apply(urlBuilder: UrlBuilder) {
        urlBuilder.queryParam("start", index.toString())
    }

    object Zero: Start(0)
}