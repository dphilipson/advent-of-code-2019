package day11

import intcode.runIntcodeFromState
import intcode.runIntcode
import util.readLongs

private enum class Direction { UP, RIGHT, DOWN, LEFT }
private data class Location(val x: Int, val y: Int)
private data class RobotState(val location: Location, val direction: Direction)
private data class PaintResult(val whiteSpaces: Set<Location>, val paintedSpaces: Set<Location>)

fun main() {
    val program = readLongs("src/day11/input11.txt", ",")[0]
    println(solvePart1(program))
    solvePart2(program)
}

private fun solvePart1(program: List<Long>): Int = runPaintingProgram(program, startsWhite = false).paintedSpaces.size

private fun solvePart2(program: List<Long>) {
    val whiteSpaces = runPaintingProgram(program, true).whiteSpaces
    val minX = whiteSpaces.asSequence().map { it.x }.min()!!
    val minY = whiteSpaces.asSequence().map { it.y }.min()!!
    val maxX = whiteSpaces.asSequence().map { it.x }.max()!!
    val maxY = whiteSpaces.asSequence().map { it.y }.max()!!
    for (y in minY..maxY) {
        println((minX..maxX).asSequence()
            .map { x -> if (Location(x, y) in whiteSpaces) '█' else ' ' }
            .joinToString(""))
    }
}

private fun runPaintingProgram(program: List<Long>, startsWhite: Boolean): PaintResult {
    val whiteSpaces = mutableSetOf<Location>()
    val paintedSpaces = mutableSetOf<Location>()
    if (startsWhite) {
        whiteSpaces.add(Location(0, 0))
    }
    var robotState = RobotState(Location(0, 0), Direction.UP)
    var programState = runIntcode(program, listOf()).state
    while (programState.instructionIndex != null) {
        paintedSpaces.add(robotState.location)
        val input = if (robotState.location in whiteSpaces) 1L else 0L
        val (newState, outputs) = runIntcodeFromState(programState, listOf(input))
        programState = newState
        val (color, turn) = outputs
        if (color == 1L) {
            whiteSpaces.add(robotState.location)
        } else {
            whiteSpaces.remove(robotState.location)
        }
        robotState = getNextRobotState(robotState, turn == 1L)
    }
    return PaintResult(whiteSpaces, paintedSpaces)
}

private fun getNextRobotState(robotState: RobotState, isRightTurn: Boolean): RobotState {
    val direction = getNextDirection(robotState.direction, isRightTurn)
    val location = getNextLocation(robotState.location, direction)
    return RobotState(location, direction)
}

private fun getNextDirection(currentDirection: Direction, isRightTurn: Boolean): Direction {
    val directions = Direction.values()
    val currentIndex = directions.indexOf(currentDirection)
    val indexDelta = if (isRightTurn) 1 else -1
    return directions[(currentIndex + indexDelta + directions.size) % directions.size]
}

private fun getNextLocation(currentLocation: Location, direction: Direction): Location {
    val (x, y) = currentLocation
    return when (direction) {
        Direction.UP -> Location(x, y - 1)
        Direction.RIGHT -> Location(x + 1, y)
        Direction.DOWN -> Location(x, y + 1)
        Direction.LEFT -> Location(x - 1, y)
    }
}