package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class PRParticipant(
        @JsonProperty("user") val user: User,
        @JsonProperty("approved") val approved: Boolean)
