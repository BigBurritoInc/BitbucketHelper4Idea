package ui

import bitbucket.data.PR

class PRState(private val prsMap: Map<Long, PR> = HashMap()) {
    fun createDiff(prs: List<PR>): Diff {
        val newMap = prs.map { it.id to it }.toMap()
        val removed = prsMap.filterKeys { !newMap.containsKey(it) }
        val added = newMap.filterKeys { !prsMap.containsKey(it) }
        //todo: updated keys
        return Diff(added, emptyMap(), removed)
    }

    fun createNew(prs: List<PR>): PRState {
        return PRState(prs.map { it.id to it }.toMap())
    }
}