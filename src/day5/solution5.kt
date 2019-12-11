package day5

import util.readInts

fun main() {
    val registers = readInts("src/day5/input5.txt", ",")[0]
    println(solvePart1(registers))
    println(solvePart2(registers))
}

private fun solvePart1(registers: List<Int>): Int = executeProgram(registers, 1).last()

private fun solvePart2(registers: List<Int>): Int = executeProgram(registers, 5)[0]

private fun executeProgram(initialRegisters: List<Int>, input: Int): List<Int> {
    val registers = initialRegisters.toMutableList()
    var i = 0
    val outputs = mutableListOf<Int>()
    while (true) {
        val instruction = registers[i]
        val opcode = instruction % 100
        val modes = getParameterModes(instruction, 2)

        fun eval(paramIndex: Int): Int = when (val mode = modes[paramIndex]) {
            0 -> registers[registers[i + paramIndex + 1]]
            1 -> registers[i + paramIndex + 1]
            else -> throw Exception("Invalid parameter mode $mode.")
        }

        fun write(paramIndex: Int, value: Int) {
            registers[registers[i + paramIndex + 1]] = value
        }

        when (opcode) {
            1 -> {
                write(2, eval(0) + eval(1))
                i += 4
            }
            2 -> {
                write(2, eval(0) * eval(1))
                i += 4
            }
            3 -> {
                write(0, input)
                i += 2
            }
            4 -> {
                outputs.add(eval(0))
                i += 2
            }
            5 -> i = if (eval(0) != 0) eval(1) else i + 3
            6 -> i = if (eval(0) == 0) eval(1) else i + 3
            7 -> {
                write(2, if (eval(0) < eval(1)) 1 else 0)
                i += 4
            }
            8 -> {
                write(2, if (eval(0) == eval(1)) 1 else 0)
                i += 4
            }
            99 -> return outputs
            else -> throw Exception("Invalid op code $opcode.")
        }
    }
}

private fun getParameterModes(instruction: Int, length: Int): List<Int> =
    instruction
        .toString()
        .dropLast(2)
        .padStart(length, '0')
        .reversed()
        .map { it - '0' }
