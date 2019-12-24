package day23

import intcode.runIntcode
import intcode.runIntcodeFromState
import util.readLongs
import java.util.*

private data class Location(val x: Long, val y: Long)

fun main() {
    val program = readLongs("src/day23/input23.txt", ",")[0]
    println(solvePart1(program))
    println(solvePart2(program))
}

private fun solvePart1(program: List<Long>): Long {
    val initialIntcodeResults = (0L..49).map { runIntcode(program, listOf(it)) }
    val computers = initialIntcodeResults.asSequence()
        .map { it.state }
        .toMutableList()
    val queues = generateSequence { ArrayDeque<Location>() }.take(50).toList()
    initialIntcodeResults.forEach { result ->
        result.outputs.asSequence()
            .chunked(3)
            .forEach { (address, x, y) -> queues[address.toInt()].add(Location(x, y)) }
    }
    while (true) {
        for (i in computers.indices) {
            val queuedLocation = queues[i].poll()
            val input = if (queuedLocation != null) listOf(queuedLocation.x, queuedLocation.y) else listOf(-1L)
            val (state, outputs) = runIntcodeFromState(computers[i], input)
            computers[i] = state
            outputs.asSequence()
                .chunked(3)
                .forEach { (address, x, y) ->
                    if (address == 255L) {
                        return y
                    } else {
                        queues[address.toInt()].add(Location(x, y))
                    }
                }
        }
    }
}

private fun solvePart2(program: List<Long>): Long {
    val initialIntcodeResults = (0L..49).map { runIntcode(program, listOf(it)) }
    val computers = initialIntcodeResults.asSequence()
        .map { it.state }
        .toMutableList()
    val queues = generateSequence { ArrayDeque<Location>() }.take(50).toList()
    initialIntcodeResults.forEach { result ->
        result.outputs.asSequence()
            .chunked(3)
            .forEach { (address, x, y) -> queues[address.toInt()].add(Location(x, y)) }
    }
    var natLocation = Location(0L, 0L)
    var lastNatY: Long? = null
    while (true) {
        if (queues.all { it.isEmpty() }) {
            if (lastNatY == natLocation.y) {
                return lastNatY
            }
            lastNatY = natLocation.y
            queues[0].add(natLocation)
        }
        for (i in computers.indices) {
            val queuedLocation = queues[i].poll()
            val input = if (queuedLocation != null) listOf(queuedLocation.x, queuedLocation.y) else listOf(-1L)
            val (state, outputs) = runIntcodeFromState(computers[i], input)
            computers[i] = state
            outputs.asSequence()
                .chunked(3)
                .forEach { (address, x, y) ->
                    val location = Location(x, y)
                    if (address == 255L) {
                        natLocation = location
                    } else {
                        queues[address.toInt()].add(location)
                    }
                }
        }
    }
}