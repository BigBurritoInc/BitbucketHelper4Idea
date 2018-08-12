package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class PRAuthor(@JsonProperty("user") val user: User)
