package ui

import bitbucket.data.PR

data class Diff(
    val added: Map<Long, PR>,
    val updated: Map<Long, PR>,
    val removed: Map<Long, PR>
) {
    fun hasAnyUpdates(): Boolean {
        return added.isNotEmpty() || updated.isNotEmpty() || removed.isNotEmpty()
    }
}