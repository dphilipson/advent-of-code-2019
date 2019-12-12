package day12

import util.readStringPerLine
import kotlin.math.absoluteValue

private data class DimensionState(val position: Long, val velocity: Long)
private data class CycleDescription(val startIndex: Long, val length: Long)

fun main() {
    val positions: List<List<Long>> = readStringPerLine("src/day12/input12.txt")
        .map { line ->
            line.replace("""[^\-0-9,]+""".toRegex(), "")
                .split(",")
                .map { it.toLong() }
        }
    val initialStates = (0 until 3).map { i ->
        positions.map { DimensionState(it[i], 0L) }
    }
    println(solvePart1(initialStates, 1000))
    println(solvePart2(initialStates))
}

private fun solvePart1(initialStates: List<List<DimensionState>>, numSteps: Int): Long {
    val finalStates = generateSequence(initialStates) { updateMoonStates(it) }
        .drop(numSteps)
        .first()
    return finalStates.first().indices.asSequence().map { i ->
        finalStates.asSequence()
            .map { it[i].position.absoluteValue }
            .sum() *
                finalStates.asSequence()
                    .map { it[i].velocity.absoluteValue }
                    .sum()

    }.sum()
}

private fun solvePart2(initialStates: List<List<DimensionState>>): Long {
    val cycles = initialStates.map { getCycle(it) }
    val startIndex = cycles.asSequence()
        .map { it.startIndex }
        .max()!!
    val cycleLength = cycles.asSequence()
        .map { it.length }
        .reduce { a, b -> lcm(a, b) }
    return startIndex + cycleLength
}

private fun updateMoonStates(states: List<List<DimensionState>>): List<List<DimensionState>> =
    states.map { updateDimension(it) }

private fun getCycle(states: List<DimensionState>): CycleDescription {
    val seenStates = mutableMapOf<List<DimensionState>, Long>()
    var currentStates = states
    var i = 0L
    while (currentStates !in seenStates) {
        seenStates[currentStates] = i
        currentStates = updateDimension(currentStates)
        i++
    }
    val startIndex = seenStates.getValue(currentStates)
    return CycleDescription(startIndex, i - startIndex)
}

private fun updateDimension(states: List<DimensionState>): List<DimensionState> {
    val velocityUpdates = states.asSequence().map { 0L }.toMutableList()
    for (i in states.indices) {
        for (j in states.indices) {
            if (states[i].position < states[j].position) {
                velocityUpdates[i]++
                velocityUpdates[j]--
            }
        }
    }
    val newVelocities = states.zip(velocityUpdates) { dimension, update -> dimension.velocity + update }
    val newPositions = states.zip(newVelocities) { dimension, velocity -> dimension.position + velocity }
    return newPositions.zip(newVelocities) { position, velocity -> DimensionState(position, velocity) }
}

private fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

private tailrec fun gcd(a: Long, b: Long): Long = when {
    a < 0 -> gcd(-a, b)
    b < 0 -> gcd(a, -b)
    a > b -> gcd(b, a)
    a == 0L -> b
    else -> gcd(b % a, a)
}