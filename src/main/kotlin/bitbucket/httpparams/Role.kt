package bitbucket.httpparams

import com.palominolabs.http.url.UrlBuilder

enum class Role(private val paramValue: String): HttpRequestParameter {
    REVIEWER("reviewer"),
    AUTHOR("author"),
    PARTICIPANT("participant"),
    ANY("any") {
        override fun apply(urlBuilder: UrlBuilder) {
            //do not add any parameter
        }
    };

    override fun apply(urlBuilder: UrlBuilder) {
        urlBuilder.queryParam("role", paramValue)
    }
}