fun main() {
    val registers = readInts("src/day2.txt", ",")[0]
    println(solvePart1(registers))
    println(solvePart2(registers))
}

private fun solvePart1(registers: List<Int>): Int = executeProgram(registers, 12, 2)[0]

private fun solvePart2(registers: List<Int>): Int {
    for (noun in 0..99) {
        for (verb in 0..99) {
            if (executeProgram(registers, noun, verb)[0] == 19690720) {
                return 100 * noun + verb
            }
        }
    }
    throw Exception("Could not find noun-verb pair.")
}

private fun executeProgram(registers: List<Int>, noun: Int, verb: Int): List<Int> {
    val mutRegisters = registers.toMutableList()
    mutRegisters[1] = noun
    mutRegisters[2] = verb
    val at = { i: Int -> mutRegisters[mutRegisters[i]] }
    for (i in 0 until (mutRegisters.size - 1) step 4) {
        when (val opCode = mutRegisters[i]) {
            1 -> mutRegisters[mutRegisters[i + 3]] = at(i + 1) + at(i + 2)
            2 -> mutRegisters[mutRegisters[i + 3]] = at(i + 1) * at(i + 2)
            99 -> return mutRegisters
            else -> throw Exception("Invalid op code $opCode.")
        }
    }
    throw Exception("Reached end of program without exit code 99.")
}