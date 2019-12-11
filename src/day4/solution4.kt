package day4

import kotlin.math.min

fun main() {
    val range = 357253..892942
    println(solvePart1(range))
    println(solvePart2(range))
}

private fun solvePart1(range: IntRange): Int = range.asSequence()
    .map { it.toString() }
    .filter {
        var hasAdjacent = false
        for (i in 1 until it.length) {
            if (it[i] == it[i - 1]) {
                hasAdjacent = true
            }
            if (it[i] < it[i - 1]) {
                return@filter false
            }
        }
        hasAdjacent
    }
    .count()

private fun solvePart2(range: IntRange): Int = range.asSequence()
    .map { it.toString() }
    .filter {
        var shortestRun = Int.MAX_VALUE
        var currentRun = 1
        fun endRun() {
            if (currentRun > 1) {
                shortestRun = min(shortestRun, currentRun)
                currentRun = 1
            }
        }
        for (i in 1 until it.length) {
            if (it[i] == it[i - 1]) {
                currentRun++
            } else {
                endRun()
            }
            if (it[i] < it[i - 1]) {
                return@filter false
            }
        }
        endRun()
        shortestRun == 2
    }
    .count()
