package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Project(@JsonProperty("key") val key: String)
