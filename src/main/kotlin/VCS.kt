interface VCS {
    fun checkoutBranch(branch: String, listener:Runnable)
    fun currentBranch(): String
    fun updateProject()
}