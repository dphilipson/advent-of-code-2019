import intcode.runFromSnapshot
import intcode.runIntcode
import util.readLongs

fun main() {
    val program = readLongs("src/day7.txt", ",")[0]
    println(solvePart1(program))
    println(solvePart2(program))
}

private fun solvePart1(program: List<Long>): Long =
    permutations(0L..4).asSequence()
        .map { applyAllAmplifiers(program, it) }
        .max()!!

private fun solvePart2(program: List<Long>): Long =
    permutations(5L..9).asSequence()
        .map { applyAmplifiersInLoop(program, it) }
        .max()!!

private fun applyAllAmplifiers(program: List<Long>, phaseSettings: List<Long>): Long =
    phaseSettings.fold(0L) { input, phaseSetting -> applyAmplifer(program, phaseSetting, input) }

private fun applyAmplifer(program: List<Long>, phaseSetting: Long, input: Long): Long =
    runIntcode(program, listOf(phaseSetting, input)).outputs[0]

private fun applyAmplifiersInLoop(program: List<Long>, phaseSettings: List<Long>): Long {
    val amplifiers = phaseSettings.asSequence()
        .map { runIntcode(program, listOf(it)).snapshot }
        .toMutableList()
    var i = 0
    var signal = 0L
    while (amplifiers.last().instructionIndex != null) {
        val (snapshot, outputs) = runFromSnapshot(amplifiers[i], listOf(signal))
        amplifiers[i] = snapshot
        signal = outputs[0]
        i = (i + 1) % amplifiers.size
    }
    return signal
}

private fun <T> permutations(items: Iterable<T>): List<List<T>> {
    val itemsList = items.toList()
    return when (itemsList.isEmpty()) {
        true -> listOf(listOf())
        false -> permutations(itemsList.subList(1, itemsList.size))
            .flatMap { permutation ->
                (0..permutation.size).map { i ->
                    val newPermutation = permutation.toMutableList()
                    newPermutation.add(i, itemsList[0])
                    newPermutation
                }
            }
    }
}
