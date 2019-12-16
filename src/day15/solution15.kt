package day15

import intcode.IntcodeState
import intcode.runIntcode
import intcode.runIntcodeFromState
import util.readLongs
import java.util.*

private enum class MoveResult {
    WALL, MOVED, REACHED_OXYGEN;

    companion object {
        fun fromLong(l: Long): MoveResult = values()[l.toInt()]
    }
}

private enum class Direction(val isX: Boolean, val isPositive: Boolean) {
    NORTH(false, true),
    SOUTH(false, false),
    WEST(true, false),
    EAST(true, true);

    companion object {
        fun fromLong(l: Long): Direction = values()[l.toInt() - 1]
    }
}

private data class Location(val x: Int, val y: Int)
private data class ExplorationState(val state: IntcodeState, val location: Location, val depth: Int)
private data class ExploreResult(val oxygenDistance: Int, val oxygenLocation: Location, val spaces: List<Location>)

fun main() {
    val program = readLongs("src/day15/input15.txt", ",")[0]
    val (oxygenDistance, oxygenLocation, spaces) = exploreArea(program)
    println(oxygenDistance)
    println(getMaxDepth(oxygenLocation, spaces))
}

private fun exploreArea(program: List<Long>): ExploreResult {
    val pendingStates = ArrayDeque<ExplorationState>()
    pendingStates.add(ExplorationState(runIntcode(program, listOf()).state, Location(0, 0), 0))
    val seenLocations = mutableSetOf<Location>()
    var oxygenDistance: Int? = null
    var oxygenLocation: Location? = null
    while (pendingStates.isNotEmpty()) {
        val (state, location, depth) = pendingStates.pop()
        if (location in seenLocations) {
            continue
        }
        seenLocations.add(location)
        for (move in 1L..4) {
            val (newState, outputs) = runIntcodeFromState(state, listOf(move))
            val newLocation = getNewLocation(location, Direction.fromLong(move))
            fun addNewLocation() = pendingStates.add(
                ExplorationState(newState, newLocation, depth + 1)
            )
            when (MoveResult.fromLong(outputs[0])) {
                MoveResult.WALL -> ({})()
                MoveResult.MOVED -> addNewLocation()
                MoveResult.REACHED_OXYGEN -> {
                    oxygenDistance = depth + 1
                    oxygenLocation = newLocation
                    addNewLocation()
                }
            }
        }
    }
    return ExploreResult(oxygenDistance!!, oxygenLocation!!, seenLocations.toList())
}

private fun getMaxDepth(startLocation: Location, spaces: List<Location>): Int {
    val remainingSpaces = spaces.toMutableSet()
    var depth = 0
    var pendingSpaces = listOf(startLocation)
    remainingSpaces.remove(startLocation)
    while (remainingSpaces.isNotEmpty()) {
        pendingSpaces = pendingSpaces.asSequence()
            .flatMap { space -> (1L..4).asSequence().map { getNewLocation(space, Direction.fromLong(it)) } }
            .filter { it in remainingSpaces }
            .toList()
        remainingSpaces.removeAll(pendingSpaces)
        depth++
    }
    return depth
}

private fun getNewLocation(location: Location, direction: Direction): Location {
    val (x, y) = location
    return Location(
        x + if (direction.isX) if (direction.isPositive) 1 else -1 else 0,
        y + if (!direction.isX) if (direction.isPositive) 1 else -1 else 0
    )
}
