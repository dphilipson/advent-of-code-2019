package day10

import util.readStringPerLine
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.sign

private typealias Grid = List<List<Boolean>>
private data class Location(val i: Int, val j: Int)

fun main() {
    val grid = readStringPerLine("src/day10/input10.txt")
        .asSequence()
        .map { line -> line.map { it == '#' }}
        .toList()
    println(solvePart1(grid))
    println(solvePart2(grid))
}

private fun solvePart1(grid: Grid): Int =
    getAllCoordinates(grid)
    .map { (i, j) -> getAsteroidsByRepresentativeDirection(grid, i, j).size }
    .max()!!

private fun solvePart2(grid: Grid): Int {
    val (bestI, bestJ) = getBestLocation(grid)
    val asteroidsByDirection = getAsteroidsByRepresentativeDirection(grid, bestI, bestJ)
    val sortedLines = asteroidsByDirection.entries.asSequence()
        .sortedBy {-atan2(it.key.j.toDouble(), it.key.i.toDouble()) }
        .map { it.value }
        .toList()
    val countsByLine = MutableList(sortedLines.size) { 0 }
    var lineIndex = 0
    for (vaporizedCount in 0 until 199) {
        while (countsByLine[lineIndex] >= sortedLines[lineIndex].size) {
            lineIndex = (lineIndex + 1) % sortedLines.size
        }
        countsByLine[lineIndex]++
        lineIndex = (lineIndex + 1) % sortedLines.size
    }
    while (countsByLine[lineIndex] >= sortedLines[lineIndex].size) {
        lineIndex = (lineIndex + 1) % sortedLines.size
    }
    val asteroid = sortedLines[lineIndex][countsByLine[lineIndex]]
    return 100 * asteroid.j + asteroid.i
}

private fun getBestLocation(grid: Grid): Location =
    getAllCoordinates(grid)
        .maxBy { (i, j) -> getAsteroidsByRepresentativeDirection(grid, i, j).size }!!

private fun getAllCoordinates(grid: Grid): Sequence<Location> =
    grid.indices.asSequence().flatMap { i ->
        grid[0].indices.asSequence().map { j -> Location(i, j) }
    }

private fun getAsteroidCoordinates(grid: Grid): Sequence<Location> =
    getAllCoordinates(grid).filter { (i, j) -> grid[i][j] }

private fun getAsteroidsByRepresentativeDirection(grid: Grid, i: Int, j: Int): Map<Location, List<Location>> =
    getAsteroidCoordinates(grid)
        .filter { i != it.i || j != it.j }
        .groupBy { getRepresentativePoint(it.i - i, it.j - j) }
        .mapValues { entry -> entry.value.sortedBy { (it.i - i).absoluteValue + (it.j - j).absoluteValue } }

private fun getRepresentativePoint(i: Int, j: Int): Location {
    if (i == 0) {
        return Location(0, j.sign)
    }
    if (j == 0) {
        return Location(i.sign, j)
    }
    val gcd = gcd(i, j)
    return Location(i / gcd, j / gcd)
}

private tailrec fun gcd(a: Int, b: Int): Int = when {
    a < 0 -> gcd(-a, b)
    b < 0 -> gcd(a, -b)
    a > b -> gcd(b, a)
    a == 0 -> b
    else -> gcd(b % a, a)
}
