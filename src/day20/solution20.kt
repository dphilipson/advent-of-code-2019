package day20

import util.readStringPerLine
import util.uniformSearch

private data class Location(val x: Int, val y: Int) {
    fun neighbors(): List<Location> = listOf(
        Location(x + 1, y),
        Location(x - 1, y),
        Location(x, y + 1),
        Location(x, y - 1)
    )
}

private data class LocationWithDepth(val location: Location, val level: Int)

private data class Maze(
    val spaces: Set<Location>,
    val portals: Map<Location, Location>,
    val start: Location,
    val end: Location,
    val width: Int,
    val height: Int

) {
    fun isOuterLocation(location: Location): Boolean {
        val (x, y) = location
        return x == 2 || x == width - 3 || y == 2 || y == height - 3
    }
}

fun main() {
    val grid = readStringPerLine("src/day20/input20.txt")
    val maze = parseMaze(grid)
    println(solvePart1(maze))
    println(solvePart2(maze))
}

private fun solvePart1(maze: Maze): Int {
    return uniformSearch(
        initialState = maze.start,
        getNextStates = { location ->
            listOf(
                location.neighbors().filter { it in maze.spaces },
                if (location in maze.portals) listOf(maze.portals.getValue(location)) else listOf()
            ).flatten()
        },
        isTerminalState = { it == maze.end }
    ).finalState!!.second
}

private fun solvePart2(maze: Maze): Int =
    uniformSearch(
        initialState = LocationWithDepth(maze.start, 0),
        getNextStates = { (location, level) ->
            val nextStates = mutableListOf<LocationWithDepth>()
            for (neighbor in location.neighbors()) {
                if (neighbor in maze.spaces) {
                    nextStates.add(LocationWithDepth(neighbor, level))
                }
            }
            maze.portals[location]?.let { otherLocation ->
                val isOuterLocation = maze.isOuterLocation(location)
                if (level > 0 || !isOuterLocation) {
                    nextStates.add(LocationWithDepth(otherLocation, level + if (isOuterLocation) -1 else 1))
                }
            }
            nextStates
        },
        isTerminalState = { (location, level) -> location == maze.end && level == 0 }
    ).finalState!!.second

private val PORTAL_NAME = "[A-Z][A-Z]".toRegex()

private fun parseMaze(grid: List<String>): Maze {
    fun at(x: Int, y: Int): Char = grid[y].elementAtOrElse(x) { ' ' }

    val width = grid[2].length + 2
    val height = grid.size
    val firstInsideX = (2 until width - 2).first { x -> at(x, height / 2) !in ".#" }
    val lastInsideX = (2 until width - 2).reversed().first { x -> at(x, height / 2) !in ".#" }
    val firstInsideY = (2 until height - 2).first { y -> at(width / 2, y) !in ".#" }
    val lastInsideY = (2 until width - 2).reversed().first { y -> at(width / 2, y) !in ".#" }
    val spaces = (2 until width - 2).asSequence()
        .flatMap { x ->
            (2 until height - 2).asSequence()
                .filter { y -> at(x, y) == '.' }
                .map { y -> Location(x, y) }
        }
        .toSet()
    val portalsByName = mutableMapOf<String, Location>()
    val portals = mutableMapOf<Location, Location>()
    var start: Location? = null
    var end: Location? = null

    fun recordPortal(name: String, location: Location) {
        val otherLocation = portalsByName[name]
        if (otherLocation != null) {
            portals[location] = otherLocation
            portals[otherLocation] = location
            portalsByName.remove(name)
        } else {
            portalsByName[name] = location
        }
    }

    for (x in 2 until width - 2) {
        for (y1 in listOf(0, firstInsideY, lastInsideY - 1, height - 2)) {
            val y2 = y1 + 1
            val name = listOf(at(x, y1), at(x, y2)).joinToString("")
            val location = Location(x, if (y1 == 0 || y1 == lastInsideY - 1) y2 + 1 else y1 - 1)
            when {
                name == "AA" -> start = location
                name == "ZZ" -> end = location
                name.matches(PORTAL_NAME) -> recordPortal(name, location)
            }

        }
    }
    for (y in 2 until height - 2) {
        for (x1 in listOf(0, firstInsideX, lastInsideX - 1, width - 2)) {
            val x2 = x1 + 1
            val name = listOf(at(x1, y), at(x2, y)).joinToString("")
            val location = Location(if (x1 == 0 || x1 == lastInsideX - 1) x2 + 1 else x1 - 1, y)
            when {
                name == "AA" -> start = location
                name == "ZZ" -> end = location
                name.matches(PORTAL_NAME) -> recordPortal(name, location)
            }

        }
    }
    return Maze(spaces, portals, start!!, end!!, width, height)
}