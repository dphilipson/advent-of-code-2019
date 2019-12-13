package day13

import intcode.runIntcode
import intcode.runIntcodeFromState
import util.readLongs
import kotlin.math.sign

private const val BLOCK = 2L
private const val PADDLE = 3L
private const val BALL = 4L

private data class Coord(val x: Int, val y: Int)

fun main() {
    val program = readLongs("src/day13/input13.txt", ",")[0]
    println(solvePart1(program))
    println(solvePart2(program))
}

private fun solvePart1(program: List<Long>): Int =
    runIntcode(program, listOf()).outputs.asSequence()
        .chunked(3)
        .filter { it[2] == BLOCK }
        .count()

private fun solvePart2(program: List<Long>): Long {
    val updatedProgram = program.toMutableList()
    updatedProgram[0] = 2L
    val initialResult = runIntcode(updatedProgram, listOf())
    var ballX = getBallX(initialResult.outputs)!!
    var paddleX = getPaddleX(initialResult.outputs)!!
    var score = getScoreUpdate(initialResult.outputs) ?: 0L
    var state = initialResult.state
    while (state.instructionIndex != null) {
        val input = (ballX - paddleX).sign.toLong()
        paddleX += input
        val result = runIntcodeFromState(state, listOf(input))
        state = result.state
        getBallX(result.outputs)?.let { ballX = it }
        getScoreUpdate(result.outputs)?.let { score = it }
    }
    return score
}

private fun getBallX(outputs: List<Long>) = getTileX(outputs, BALL)
private fun getPaddleX(outputs: List<Long>) = getTileX(outputs, PADDLE)

private fun getTileX(outputs: List<Long>, tile: Long): Long? =
    outputs.asSequence()
        .chunked(3)
        .find { (it[0] != -1L || it[1] != 0L) && it[2] == tile }
        ?.let { it[0] }

private fun getScoreUpdate(outputs: List<Long>): Long? =
    outputs.asSequence()
        .chunked(3)
        .find { it[0] == -1L && it[1] == 0L }
        ?.let { it[2] }


private fun renderBoard(outputs: List<Long>) {
    val tilesByLocation = outputs.asSequence()
        .chunked(3) { Pair(Coord(it[0].toInt(), it[1].toInt()), it[2]) }
        .filter { it.first != Coord(-1, 0) }
        .toMap()
    val maxX = tilesByLocation.keys.asSequence().map { it.x }.max()!!
    val maxY = tilesByLocation.keys.asSequence().map { it.y }.max()!!
    for (y in 0..maxY) {
        println((0..maxX).asSequence()
            .map { x -> renderTile(tilesByLocation.getOrDefault(Coord(x, y), EMPTY)) }
            .joinToString("")
        )
    }
}

private fun renderTile(tile: Long): Char = when (tile) {
    EMPTY -> ' '
    WALL -> '█'
    BLOCK -> '☐'
    PADDLE -> '-'
    BALL -> '•'
    else -> throw Exception("Invalid tile $tile")
}

private fun getInput(): Long = when (readLine()) {
    "a" -> -1
    "d" -> 1
    else  -> 0
}