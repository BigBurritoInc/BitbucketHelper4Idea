package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

data class PR(@JsonProperty("id") val id: Long,
              @JsonProperty("title") val title: String,
              @JsonProperty("author") val author: PRAuthor,
              @JsonProperty("closed") val closed: Boolean,
              @JsonProperty("fromRef") private val from: Branch,
              @JsonProperty("toRef") private val to: Branch,
              @JsonProperty("createdDate") private val createdDate: Date,
              @JsonProperty("updatedDate") private val updatedDate: Date) {

    val fromBranch: String
        get() = toUsualBranchName(from.name)

    val toBranch: String
        get() = toUsualBranchName(to.name)

    val createdAt: ZonedDateTime
        get() = ZonedDateTime.ofInstant(createdDate.toInstant(), ZoneId.of("UTC"))

    val updatedAt: ZonedDateTime
        get() = ZonedDateTime.ofInstant(updatedDate.toInstant(), ZoneId.of("UTC"))

    private fun toUsualBranchName(canonicalName: String): String {
        return canonicalName.replace("ref/heads/", "").replace("refs/heads/", "")
    }
}