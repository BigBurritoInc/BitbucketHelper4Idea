package bitbucket.data

import bitbucket.data.merge.MergeStatus
import bitbucket.data.merge.Veto
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

data class PR(@JsonProperty("id") val id: Long,
              @JsonProperty("title") val title: String,
              @JsonProperty("author") val author: PRParticipant,
              @JsonProperty("closed") val closed: Boolean,
              @JsonProperty("fromRef") private val from: Branch,
              @JsonProperty("toRef") private val to: Branch,
              @JsonProperty("reviewers") val reviewers: Set<PRParticipant>,
              @JsonProperty("createdDate") private val createdDate: Date,
              @JsonProperty("updatedDate") private val updatedDate: Date,
              @JsonProperty("links") val links: Links,
              @JsonProperty("version") val version: Int
) {
    @Volatile //Excluded from the class constructor, so doesn't participate in equals()
    var mergeStatus = MergeStatus(false, false, listOf(Veto("", "")))

    val fromBranch: String
        get() = from.name

    val toBranch: String
        get() = to.name

    val projectKey: String
        get() = to.repository.project.key

    val repoSlug: String
        get() = to.repository.slug

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