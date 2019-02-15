package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class PRProperties(
    @JsonProperty("commentCount") val commentCount: Int
)
