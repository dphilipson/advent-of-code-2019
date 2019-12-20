package day18

import util.readStringPerLine
import util.search
import util.uniformSearch

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
    val totalKeyCount = dungeons.asSequence()
        .flatMap { dungeon -> dungeon.keysBySpace.keys.asSequence() }
        .distinct()
        .count()
    return search(
        initialState = SearchState(dungeons.map { it.startSpace }, setOf()),
        getNextStates = { state ->
            (dungeons.indices).asSequence()
                .flatMap { i ->
                    val dungeon = dungeons[i]
                    getPotentialMoves(dungeon, state.spaces[i], state.keys).asSequence()
                        .map { (potentialLocation, stepCount) ->
                            val newKey = dungeon.keysBySpace.getValue(potentialLocation)
                            val newKeys = state.keys.toMutableSet().apply { add(newKey) }
                            val newSpaces = state.spaces.toMutableList().apply { this[i] = potentialLocation }
                            Pair(SearchState(newSpaces, newKeys), stepCount)
                        }
                }.toList()
        },
        isTerminalState = { state -> state.keys.size == totalKeyCount }
    ).finalState?.second ?: throw Exception("Explored everywhere and didn't get all keys.")
}

private fun getPotentialMoves(
    dungeon: Dungeon,
    initialLocation: Location,
    keys: Set<Char>
): List<Pair<Location, Int>> {
    fun isAtNewKey(location: Location): Boolean {
        val keyAtLocation = dungeon.keysBySpace[location]
        return keyAtLocation != null && keyAtLocation !in keys
    }
    return uniformSearch(
        initialState = initialLocation,
        getNextStates = { location ->
            if (isAtNewKey(location))
                listOf()
            else
                location.neighbors().filter { dungeon.isOpen(it, keys) }
        },
        isTerminalState = { false }
    ).seenStates.asSequence()
        .filter { (location, _) -> isAtNewKey(location) }
        .map { (location, distance) -> Pair(location, distance) }
        .toList()
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
        .flatMap { y ->
            lines[y].indices.asSequence()
                .filter { x -> lines[x][y] == '@' }
                .map { x -> Location(x, y) }
        }
        .first()
