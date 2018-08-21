package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

data class PR(@JsonProperty("id") val id: Long,
              @JsonProperty("title") val title: String,
              @JsonProperty("author") val author: PRParticipant,
              @JsonProperty("closed") val closed: Boolean,
              @JsonProperty("fromRef") private val from: Branch,
              @JsonProperty("toRef") private val to: Branch,
              @JsonProperty("reviewers") private val reviewers: List<PRParticipant>,
              @JsonProperty("createdDate") private val createdDate: Date,
              @JsonProperty("updatedDate") private val updatedDate: Date) {

    val fromBranch: String
        get() = from.name

    val toBranch: String
        get() = to.name

    val createdAt: ZonedDateTime
        get() = ZonedDateTime.ofInstant(createdDate.toInstant(), ZoneId.of("UTC"))

    val updatedAt: ZonedDateTime
        get() = ZonedDateTime.ofInstant(updatedDate.toInstant(), ZoneId.of("UTC"))

    fun isApprovedBy(username: String): Boolean {
        val iterator = reviewers.filter { it.user.name == username }.iterator()
        if (iterator.hasNext())
            return iterator.next().approved
        return false
    }
}