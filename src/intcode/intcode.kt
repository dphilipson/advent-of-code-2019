package intcode

data class IntcodeState(val registers: List<Long>, val instructionIndex: Int?, val relativeBase: Int)
data class IntcodeResult(val state: IntcodeState, val outputs: List<Long>)

fun runIntcode(initialRegisters: List<Long>, inputs: List<Long>): IntcodeResult =
    runIntcodeFromState(IntcodeState(initialRegisters, 0, 0), inputs)

fun runIntcodeFromState(state: IntcodeState, inputs: List<Long>): IntcodeResult {
    val registers = state.registers.toMutableList()
    var i = state.instructionIndex ?: throw Exception("Cannot run from completed state.")
    var relativeBase = state.relativeBase
    var inputIndex = 0
    val outputs = mutableListOf<Long>()

    fun getRegister(index: Int): Long = registers.getOrElse(index) { 0 }

    while (true) {
        val instruction = registers[i]
        val opcode = (instruction % 100).toInt()
        val modes = getParameterModes(instruction)

        fun getIndexForParam(paramIndex: Int): Int {
            val rawIndex = i + paramIndex + 1
            return when (val mode = modes.getOrElse(paramIndex) { 0 }) {
                0 -> getRegister(rawIndex).toInt()
                1 -> rawIndex
                2 -> getRegister(rawIndex).toInt() + relativeBase
                else -> throw Exception("Invalid parameter mode $mode.")
            }
        }

        fun eval(paramIndex: Int): Long = getRegister(getIndexForParam(paramIndex))

        fun write(paramIndex: Int, value: Long) {
            val writeIndex = getIndexForParam(paramIndex)
            while (registers.size <= writeIndex) {
                registers.add(0L)
            }
            registers[writeIndex] = value
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
                    return IntcodeResult(IntcodeState(registers, i, relativeBase), outputs)
                }
            }
            4 -> {
                outputs.add(eval(0))
                i += 2
            }
            5 -> i = if (eval(0) != 0L) eval(1).toInt() else i + 3
            6 -> i = if (eval(0) == 0L) eval(1).toInt() else i + 3
            7 -> {
                write(2, if (eval(0) < eval(1)) 1 else 0)
                i += 4
            }
            8 -> {
                write(2, if (eval(0) == eval(1)) 1 else 0)
                i += 4
            }
            9 -> {
                relativeBase += eval(0).toInt()
                i += 2
            }
            99 -> return IntcodeResult(IntcodeState(registers, null, relativeBase), outputs)
            else -> throw Exception("Invalid op code $opcode.")
        }
    }
}

private fun getParameterModes(instruction: Long): List<Int> =
    instruction
        .toString()
        .dropLast(2)
        .reversed()
        .map { it - '0' }
