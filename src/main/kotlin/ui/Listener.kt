package ui

interface Listener {
    fun ownUpdated(diff: Diff) {}
    fun reviewedUpdated(diff: Diff) {}
    fun currentBranchChanged(branchName: String) {}
    fun ownCountChanged(count: Int) {}
    fun reviewedCountChanged(count: Int) {}
}