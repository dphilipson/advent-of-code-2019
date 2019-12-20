package util

import java.util.*

data class SearchResult<S>(val seenStates: Map<S, Int>, val finalState: Pair<S, Int>?)

inline fun <S> search(
    initialState: S,
    getNextStates: (S) -> Iterable<Pair<S, Int>>,
    isTerminalState: (S) -> Boolean
): SearchResult<S> {
    val seenStates = mutableMapOf<S, Int>()
    val pendingStates = PriorityQueue<Pair<S, Int>> { p1, p2 -> compareValuesBy(p1, p2) { it.second } }
    pendingStates.add(Pair(initialState, 0))
    while (pendingStates.isNotEmpty()) {
        val stateWithDistance = pendingStates.remove()
        val (state, distance) = stateWithDistance
        if (state in seenStates) {
            continue
        }
        seenStates[state] = distance
        if (isTerminalState(state)) {
            return SearchResult(seenStates, stateWithDistance)
        }
        for ((nextState, nextDistance) in getNextStates(state)) {
            pendingStates.add(Pair(nextState, distance + nextDistance))
        }
    }
    return SearchResult(seenStates, null)
}

inline fun <S> uniformSearch(
    initialState: S,
    getNextStates: (S) -> Iterable<S>,
    isTerminalState: (S) -> Boolean
): SearchResult<S> =
    search(initialState, { state -> getNextStates(state).map { Pair(it, 1) } }, isTerminalState)