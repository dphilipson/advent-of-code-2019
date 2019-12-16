package day16

import util.readStringPerLine
import kotlin.math.absoluteValue

fun main() {
    val input = readStringPerLine("src/day16/input16.txt")[0].map { it - '0' }
    println(solvePart1(input))
    println(solvePart2(input))
}

private fun solvePart1(input: List<Int>): Int =
    generateSequence(input) { runPhase(it) }
        .drop(100)
        .first()
        .asSequence()
        .take(8)
        .joinToString("")
        .toInt()

private fun solvePart2(input: List<Int>): Int {
    val messageOffset = getMessageOffset(input)
    val suffix = (messageOffset until input.size * 10000).asSequence()
        .map { input[it % input.size] }
        .toMutableList()
    for (i in 1..100) {
        updateSuffix(suffix)
    }
    return suffix.asSequence()
        .take(8)
        .joinToString("")
        .toInt()
}

private fun runPhase(input: List<Int>): List<Int> =
    input.indices.map { i ->
        (input.asSequence()
            .zip(getPattern(i)) { a, b -> a * b }
            .sum() % 10).absoluteValue
    }

private val BASE_PATTERN = listOf(0, 1, 0, -1)

private fun getPattern(n: Int): Sequence<Int> =
    generateSequence { BASE_PATTERN }
        .flatten()
        .flatMap { element -> generateSequence { element }.take(n + 1) }
        .drop(1)

private fun getMessageOffset(input: List<Int>): Int =
    input.asSequence()
        .take(7)
        .joinToString("")
        .toInt()

private fun updateSuffix(suffix: MutableList<Int>) {
    var sum = 0
    for (i in suffix.indices.reversed()) {
        sum += suffix[i]
        suffix[i] = sum % 10
    }
}