package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class User(@JsonProperty("name") val name: String,
           @JsonProperty("emailAddress") val emailAddress: String,
           @JsonProperty("id") val id: Long,
           @JsonProperty("displayName") val displayName: String) {

}
