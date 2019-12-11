package day9

import intcode.runIntcode
import util.readLongs

fun main() {
    val program = readLongs("src/day9/input9.txt", ",")[0]
    println(solvePart1(program))
    println(solvePart2(program))
}

private fun solvePart1(program: List<Long>): Long = runIntcode(program, listOf(1L)).outputs[0]

private fun solvePart2(program: List<Long>): Long = runIntcode(program, listOf(2L)).outputs[0]
