package intcode

data class IntcodeSnapshot(val registers: List<Int>, val instructionIndex: Int?)
data class IntcodeResult(val snapshot: IntcodeSnapshot, val outputs: List<Int>)

fun runIntcode(initialRegisters: List<Int>, inputs: List<Int>): IntcodeResult =
    runFromSnapshot(IntcodeSnapshot(initialRegisters, 0), inputs)

fun runFromSnapshot(snapshot: IntcodeSnapshot, inputs: List<Int>): IntcodeResult {
    val registers = snapshot.registers.toMutableList()
    var i = snapshot.instructionIndex ?: throw Exception("Cannot run from completed snapshot.")
    var inputIndex = 0
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
                if (inputIndex < inputs.size) {
                    write(0, inputs[inputIndex])
                    inputIndex++
                    i += 2
                } else {
                    return IntcodeResult(IntcodeSnapshot(registers, i), outputs)
                }
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
            99 -> return IntcodeResult(IntcodeSnapshot(registers, null), outputs)
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
