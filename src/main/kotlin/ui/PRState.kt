package ui

import bitbucket.data.PR

class PRState(private val prsMap: Map<Long, PR> = HashMap()) {
    fun createDiff(prs: List<PR>): Diff {
        val newMap = prs.map { it.id to it }.toMap()
        val removed = prsMap.filterKeys { !newMap.containsKey(it) }
        val added = newMap.filterKeys { !prsMap.containsKey(it) }
        val updated = newMap.filterKeys { prsMap.containsKey(it) && prsMap[it] != newMap[it] }
        return Diff(added, updated, removed)
    }

    fun createNew(prs: List<PR>): PRState {
        return PRState(prs.map { it.id to it }.toMap())
    }
}