package bitbucket.data.merge

import com.fasterxml.jackson.annotation.JsonProperty

data class MergeStatus(
        @JsonProperty("canMerge") val canMerge: Boolean,
        @JsonProperty("conflicted") val conflicted: Boolean,
        @JsonProperty("vetoes") val vetoes: List<Veto>
) {
    var unknown: Boolean = true
    fun vetoesSummaries() = vetoes.joinToString { it.summaryMessage }
}

data class Veto(
        @JsonProperty("summaryMessage") val summaryMessage: String,
        @JsonProperty("detailedMessage") val detailedMessage: String
)
