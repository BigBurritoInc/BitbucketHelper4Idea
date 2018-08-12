package bitbucket.data

import com.palominolabs.http.url.UrlBuilder

interface HttpRequestParameter {
    fun apply(urlBuilder: UrlBuilder)
}