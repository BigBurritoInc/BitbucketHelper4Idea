interface VCS {
    fun checkoutBranch(branch: String)
    fun currentBranch(): String
}