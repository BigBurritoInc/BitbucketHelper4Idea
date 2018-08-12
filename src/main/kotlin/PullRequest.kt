import java.time.ZonedDateTime

data class PullRequest(
    val vcs: VCS,
    val branch: String,
    val targetBranch: String,
    val author: String,
    val created: ZonedDateTime,
    val updated: ZonedDateTime
) {
    fun checkoutBranch() {

    }
}