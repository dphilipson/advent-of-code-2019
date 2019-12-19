package day19

import intcode.runIntcode
import util.readLongs

private data class Location(val x: Int, val y: Int)

fun main() {
    val program = readLongs("src/day19/input19.txt", ",")[0]
    printTractorBeam(program)
    println(solvePart1(program))
    println(solvePart2(program))
}

private fun solvePart1(program: List<Long>): Int =
    (0..49).asSequence().flatMap { x ->
        (0..49).asSequence().filter { y -> isInBeam(program, x, y) }
    }
        .count()

private fun solvePart2(program: List<Long>): Int =
    leftBorderLocations(program)
        .first { (x, y) ->
            isInBeam(program, x, y - 99) && isInBeam(program, x + 99, y - 99)
        }
        .let { (x, y) -> x * 10000 + y - 99 }

private fun isInBeam(program: List<Long>, x: Int, y: Int): Boolean =
    runIntcode(program, listOf(x.toLong(), y.toLong())).outputs[0] == 1L

private fun leftBorderLocations(program: List<Long>): Sequence<Location> =
    generateSequence(Location(4, 5)) { (x, y) -> // Initial point (4, 5) found by inspection.
        if (isInBeam(program, x, y + 1)) Location(x, y + 1) else Location(x + 1, y + 1)
    }

private fun printTractorBeam(program: List<Long>) {
    for (y in 0 until 80) {
        println((0 until 80).asSequence()
            .map { x -> if (isInBeam(program, x, y)) '#' else '.' }
            .joinToString(""))
    }
}