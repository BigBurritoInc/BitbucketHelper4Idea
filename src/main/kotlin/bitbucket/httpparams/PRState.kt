package bitbucket.httpparams

import bitbucket.data.HttpRequestParameter
import com.palominolabs.http.url.UrlBuilder

enum class PRState(private val paramValue: String): HttpRequestParameter {
    ALL("all"),
    OPEN("open"),
    DECLINED("declined"),
    MERGED("merged");

    override fun apply(urlBuilder: UrlBuilder) {
        urlBuilder.queryParam("state", paramValue)
    }
}