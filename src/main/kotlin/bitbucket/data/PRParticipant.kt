package bitbucket.data

import bitbucket.httpparams.ParticipantStatus
import com.fasterxml.jackson.annotation.JsonProperty

data class PRParticipant(
        @JsonProperty("user") val user: User,
        @JsonProperty("approved") val approved: Boolean,
        @JsonProperty("status") val status: ParticipantStatus
)
