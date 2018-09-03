package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Links(@JsonProperty("self")private val selfLink: List<Link>) {
    data class Link(@JsonProperty("href") val href: String)

    fun getSelfHref(): String = selfLink.first().href

}