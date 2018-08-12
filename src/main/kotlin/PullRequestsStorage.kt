interface PullRequestsStorage {
    fun own(): List<PullRequest>
    fun reviewing(): List<PullRequest>
}