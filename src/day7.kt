fun main() {
    val program = readInts("src/day7.txt", ",")[0]
    println(solvePart1(program))
    println(solvePart2(program))
}

private fun solvePart1(program: List<Int>): Int =
    permutations(0..4).asSequence()
        .map { applyAllAmplifiers(program, it) }
        .max()!!

private fun solvePart2(program: List<Int>): Int =
    permutations(5..9).asSequence()
        .map { applyAmplifiersInLoop(program, it) }
        .max()!!

private fun applyAllAmplifiers(program: List<Int>, phaseSettings: List<Int>): Int =
    phaseSettings.fold(0) { input, phaseSetting -> applyAmplifer(program, phaseSetting, input) }

private fun applyAmplifer(program: List<Int>, phaseSetting: Int, input: Int): Int =
    runIntcode(program, listOf(phaseSetting, input)).outputs[0]

private fun applyAmplifiersInLoop(program: List<Int>, phaseSettings: List<Int>): Int {
    val amplifiers = phaseSettings.asSequence()
        .map { runIntcode(program, listOf(it)).snapshot }
        .toMutableList()
    var i = 0
    var signal = 0
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
