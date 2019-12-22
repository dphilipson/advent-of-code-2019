package day21

import intcode.runIntcode
import util.readLongs

fun main() {
    val input = readLongs("src/day21/input21.txt")[0]
    println(solvePart1(input))
    println(solvePart2(input))
}

private fun solvePart1(input: List<Long>): Long {
    // !(A && B && C) && D
    val programText =
        """
            NOT J J
            AND A J
            AND B J
            AND C J
            NOT J J
            AND D J
            WALK
            
        """.trimIndent()
    return runSpringdroid(input, programText)
}

private fun solvePart2(input: List<Long>): Long {
    // Previous condition, and also
    // !(!E && !H) -> E || H
    val programText =
        """
            NOT J J
            AND A J
            AND B J
            AND C J
            NOT J J
            AND D J
            OR E T
            OR H T
            AND T J
            RUN
            
        """.trimIndent()
    return runSpringdroid(input, programText)
}

private fun runSpringdroid(input: List<Long>, programText: String): Long {
    val program = programText.asSequence()
        .map { it.toLong() }
        .toList()
    return runIntcode(input, program).outputs.last()
}

private fun printOutput(output: List<Long>) {
    output.asSequence()
        .map { it.toChar() }
        .joinToString("")
        .splitToSequence("\n")
        .forEach { println(it) }
}