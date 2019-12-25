package day25

import intcode.IntcodeState
import intcode.runIntcode
import intcode.runIntcodeFromState
import util.readLongs

fun main() {
    val program = readLongs("src/day25/input25.txt", ",")[0]
    solvePart1(program)
}

private fun solvePart1(program: List<Long>) {
    val state = pickUpAllItemsAndGoToCheckpoint(program)
    val inventory = listOf(
        "planetoid",
        "candy cane",
        "ornament",
        "easter egg",
        "spool of cat6",
        "fixed point",
        "hypercube",
        "monolith"
    )
    subsets(inventory).asSequence()
        .map { subset ->
            val input = subset.asSequence()
                .map { "drop $it\n"}
                .joinToString("") + "west\n"
            val outputs = runIntcodeFromState(state, input.map { it.toLong() }).outputs
            asAscii(outputs)
        }
        .filter { "Alert!" !in it }
        .forEach { println(it) }
}

private fun pickUpAllItemsAndGoToCheckpoint(program: List<Long>): IntcodeState {
    val input = """
        south
        take fixed point
        north
        north
        take spool of cat6
        north
        take monolith
        north
        take hypercube
        south
        west
        take planetoid
        east
        south
        east
        north
        take candy cane
        south
        east
        take easter egg
        east
        south
        take ornament
        west
        south
        
    """.trimIndent()
    return runIntcode(program, input.map { it.toLong() }).state
}

private fun <T> subsets(items: List<T>): List<List<T>> =
    if (items.isEmpty())
        listOf(listOf())
    else {
        val partialSubsets = subsets(items.subList(0, items.size - 1))
        partialSubsets + partialSubsets.map { it + items.last() }
    }

private fun runInteractive(program: List<Long>) {
    val initialResult = runIntcode(program, listOf())
    var state = initialResult.state
    printAscii(initialResult.outputs)
    while (true) {
        val result = runIntcodeFromState(state, readAscii())
        state = result.state
        printAscii(result.outputs)
    }
}

private fun readAscii(): List<Long> = "${readLine()}\n".map { it.toLong() }

private fun printAscii(line: List<Long>) {
    println(line.asSequence()
        .map { it.toChar() }
        .joinToString(""))
}

private fun asAscii(line: List<Long>): String = line.asSequence()
    .map { it.toChar() }
    .joinToString("")
