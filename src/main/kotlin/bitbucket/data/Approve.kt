package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Approve(@JsonProperty("user") val user: SimpleUser,
                   @JsonProperty("status") val status: String = "APPROVED",
                   @JsonProperty("approved") val approved: String = "true")

