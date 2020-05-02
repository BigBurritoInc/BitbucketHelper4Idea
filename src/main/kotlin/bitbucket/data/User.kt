package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class User(@JsonProperty("name") val name: String,
                // under some circumstances email can be null
                @JsonProperty("emailAddress") val emailAddress: String?,
                @JsonProperty("id") val id: Long,
                @JsonProperty("displayName") val displayName: String,
                @JsonProperty("links") val links: Links)