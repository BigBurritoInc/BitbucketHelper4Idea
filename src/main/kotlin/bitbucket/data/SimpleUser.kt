package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class SimpleUser(@JsonProperty("name") val name: String)