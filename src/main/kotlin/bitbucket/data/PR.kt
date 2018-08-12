package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class PR(@JsonProperty("id") val id: Long,
              @JsonProperty("title") val title: String,
              @JsonProperty("author") val author: PRAuthor,
              @JsonProperty("closed") val closed: Boolean,
              @JsonProperty("fromRef") val from: Branch,
              @JsonProperty("toRef") val to: Branch,
              @JsonProperty("createdDate") val createdDate: Date,
              @JsonProperty("updatedDate") val updatedDate: Date)