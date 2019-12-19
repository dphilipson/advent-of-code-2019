package day18

import util.readStringPerLine
import java.util.*

private data class Location(val x: Int, val y: Int) {
    fun neighbors(): List<Location> =
        listOf(Location(x - 1, y), Location(x + 1, y), Location(x, y - 1), Location(x, y + 1))
}

private data class Dungeon(
    val spaces: Set<Location>,
    val keysBySpace: Map<Location, Char>,
    val doorsBySpace: Map<Location, Char>,
    val startSpace: Location
) {
    fun isOpen(space: Location, keys: Set<Char>): Boolean =
        space in spaces && (space !in doorsBySpace || doorsBySpace[space] in keys)
}

private data class SearchState(val spaces: List<Location>, val keys: Set<Char>)

fun main() {
    val input = readStringPerLine("src/day18/input18.txt")
    println(solvePart1(input))
    println(solvePart2(input))
}

private fun solvePart1(input: List<String>): Int = solveDungeons(listOf(readDungeon(input)))

private fun solvePart2(input: List<String>): Int = solveDungeons(readMultiDungeon(input))

private fun solveDungeons(dungeons: List<Dungeon>): Int {
    val seenStates = mutableSetOf<SearchState>()
    val pendingStates = PriorityQueue<Pair<SearchState, Int>> { p1, p2 -> compareValues(p1.second, p2.second) }
    pendingStates.add(Pair(SearchState(dungeons.map { it.startSpace }, setOf()), 0))
    val totalKeyCount = dungeons.asSequence()
        .flatMap { dungeon -> dungeon.keysBySpace.keys.asSequence() }
        .distinct()
        .count()
    while (pendingStates.isNotEmpty()) {
        val (state, stepCount) = pendingStates.remove()
        if (state in seenStates) {
            continue
        }
        seenStates.add(state)
        if (state.keys.size == totalKeyCount) {
            return stepCount
        }
        for (i in dungeons.indices) {
            val dungeon = dungeons[i]
            for ((potentialLocation, count) in getPotentialMoves(dungeon, state.spaces[i], state.keys)) {
                val newKey = dungeon.keysBySpace.getValue(potentialLocation)
                val newKeys = state.keys.toMutableSet().apply { add(newKey) }
                val newSpaces = state.spaces.toMutableList().apply { this[i] = potentialLocation }
                pendingStates.add(Pair(SearchState(newSpaces, newKeys), stepCount + count))
            }
        }
    }
    throw Exception("Explored everywhere and didn't get all keys.")
}

private fun getPotentialMoves(dungeon: Dungeon, initialLocation: Location, keys: Set<Char>): List<Pair<Location, Int>> {
    val seenLocations = mutableSetOf<Location>()
    val pendingLocations = ArrayDeque<Pair<Location, Int>>()
    pendingLocations.add(Pair(initialLocation, 0))
    val result = mutableListOf<Pair<Location, Int>>()
    while (pendingLocations.isNotEmpty()) {
        val (location, stepCount) = pendingLocations.remove()
        if (location in seenLocations) {
            continue
        }
        seenLocations.add(location)
        val keyAtLocation = dungeon.keysBySpace[location]
        if (keyAtLocation != null && keyAtLocation !in keys) {
            result.add(Pair(location, stepCount))
            continue
        }
        for (neighbor in location.neighbors()) {
            if (dungeon.isOpen(neighbor, keys)) {
                pendingLocations.add(Pair(neighbor, stepCount + 1))
            }
        }
    }
    return result
}

private fun readDungeon(lines: List<String>): Dungeon = readSubDungeon(lines, lines[0].indices, lines.indices)

private fun readSubDungeon(
    lines: List<String>,
    xRange: IntRange,
    yRange: IntRange,
    startSpace: Location? = null
): Dungeon {
    val spaces = mutableSetOf<Location>()
    val keysBySpace = mutableMapOf<Location, Char>()
    val doorsBySpace = mutableMapOf<Location, Char>()
    var start: Location? = startSpace
    for (y in yRange) {
        val line = lines[y]
        for (x in xRange) {
            val location = Location(x, y)
            val c = line[x]
            if (c != '#') {
                spaces.add(location)
            }
            when {
                c.isLowerCase() -> keysBySpace[location] = c
                c.isUpperCase() -> doorsBySpace[location] = c.toLowerCase()
                c == '@' && start == null -> start = location
            }
        }
    }
    return Dungeon(spaces, keysBySpace, doorsBySpace, start!!)
}

private fun readMultiDungeon(lines: List<String>): List<Dungeon> {
    val (x, y) = findStartLocation(lines)
    val xRange1 = 0 until x
    val xRange2 = x + 1 until lines[0].length
    val yRange1 = 0 until y
    val yRange2 = y + 1 until lines.size
    return listOf(
        readSubDungeon(lines, xRange1, yRange1, Location(x - 1, y - 1)),
        readSubDungeon(lines, xRange2, yRange1, Location(x + 1, y - 1)),
        readSubDungeon(lines, xRange1, yRange2, Location(x - 1, y + 1)),
        readSubDungeon(lines, xRange2, yRange2, Location(x + 1, y + 1))
    )
}

private fun findStartLocation(lines: List<String>): Location =
    (lines.indices).asSequence()
        .flatMap { y -> lines[y].indices.asSequence()
            .filter { x -> lines[x][y] == '@'}
            .map { x -> Location(x, y) }
        }
        .first()
