import util.readIntPerLine

fun main() {
    val masses = readIntPerLine("src/day1.txt")
    println(solvePart1(masses))
    println(solvePart2(masses))
}

private fun solvePart1(masses: List<Int>): Int =
    masses
        .asSequence()
        .map { it / 3 - 2 }
        .sum()

private fun solvePart2(masses: List<Int>): Int =
    masses
        .asSequence()
        .map { getExtendedFuelRequirementForMass(it) }
        .sum()

private fun getExtendedFuelRequirementForMass(mass: Int): Int =
    generateSequence(mass) { it / 3 - 2 }
        .drop(1)
        .takeWhile { it > 0 }
        .sum()
