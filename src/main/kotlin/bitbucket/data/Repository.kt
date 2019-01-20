package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Repository(
    @JsonProperty("slug") val slug: String,
    @JsonProperty("project") val project: Project
)