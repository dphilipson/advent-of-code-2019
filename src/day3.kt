import kotlin.math.absoluteValue
import kotlin.math.sign

private data class Step(val isX: Boolean, val delta: Int)
private data class Location(val x: Int, val y: Int)

fun main() {
    val (steps1, steps2) = readStrings("src/day3.txt", ",")
        .map { rawSteps -> rawSteps.map { parseStep(it) } }
    println(solvePart1(steps1, steps2))
    println(solvePart2(steps1, steps2))
}

private fun parseStep(stepText: String): Step {
    val direction = stepText[0]
    val amount = stepText.drop(1).toInt()
    return Step(
        direction == 'L' || direction == 'R',
        amount * (if (direction == 'U' || direction == 'R') 1 else -1)
    )
}

private fun solvePart1(steps1: List<Step>, steps2: List<Step>): Int =
    getPathLocations(steps1)
        .toSet()
        .intersect(getPathLocations(steps2))
        .asSequence()
        .map { (x, y) -> x.absoluteValue + y.absoluteValue }
        .min() ?: throw Exception("No intersection found.")

private fun solvePart2(steps1: List<Step>, steps2: List<Step>): Int {
    val locations1 = getPathLocations(steps1)
    val locations2 = getPathLocations(steps2)
    return locations1
        .toSet()
        .intersect(locations2)
        .asSequence()
        .map { locations1.indexOf(it) + locations2.indexOf(it) + 2 } // +2 to offset zero-indexing for each.
        .min() ?: throw Exception("No intersection found.")
}

private fun getPathLocations(steps: List<Step>): List<Location> {
    val locations = mutableListOf<Location>()
    var location = Location(0, 0)
    for ((isX, delta) in steps) {
        val (x, y) = location
        for (i in 1..delta.absoluteValue) {
            locations += Location(
                x + if (isX) delta.sign * i else 0,
                y + if (!isX) delta.sign * i else 0
            )
        }
        location = locations.last()
    }
    return locations
}
