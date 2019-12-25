package day24

import util.readStringPerLine

private data class Location(val x: Int, val y: Int) {
    fun neighbors(): List<Location> =
        listOf(Location(x - 1, y), Location(x + 1, y), Location(x, y - 1), Location(x, y + 1))
}

private data class DeepLocation(val x: Int, val y: Int, val depth: Int) {
    fun neighbors(): List<DeepLocation> =
        listOf(
            listOf(
                DeepLocation(x - 1, y, depth),
                DeepLocation(x + 1, y, depth),
                DeepLocation(x, y - 1, depth),
                DeepLocation(x, y + 1, depth)
            ).filter { it.x in 0 until 5 && it.y in 0 until 5 && (x != 2 || y != 2) },
            if (x == 0) listOf(DeepLocation(1, 2, depth - 1)) else listOf(),
            if (x == 4) listOf(DeepLocation(3, 2, depth - 1)) else listOf(),
            if (y == 0) listOf(DeepLocation(2, 1, depth - 1)) else listOf(),
            if (y == 4) listOf(DeepLocation(2, 3, depth - 1)) else listOf(),
            if (x == 1 && y == 2) (0 until 5).map { DeepLocation(0, it, depth + 1) } else listOf(),
            if (x == 3 && y == 2) (0 until 5).map { DeepLocation(4, it, depth + 1) } else listOf(),
            if (x == 2 && y == 1) (0 until 5).map { DeepLocation(it, 0, depth + 1) } else listOf(),
            if (x == 2 && y == 3) (0 until 5).map { DeepLocation(it, 4, depth + 1) } else listOf()
        ).flatten()
}

fun main() {
    val input = readStringPerLine("src/day24/input24.txt")
    val initialLocations = parseLocations(input)
    println(solvePart1(initialLocations))
    val initialDeepLocations = initialLocations.asSequence()
        .map { (x, y) -> DeepLocation(x, y, 0) }
        .toSet()
    println(solvePart2(initialDeepLocations))
}

private fun solvePart1(initialState: Set<Location>): Int {
    tailrec fun recur(state: Set<Location>, seenStates: MutableSet<Set<Location>>): Int =
        if (state in seenStates)
            getBiodiversityRating(state)
        else
            recur(runStep(state, 5), seenStates.apply { add(state) })
    return recur(initialState, mutableSetOf())
}

private fun solvePart2(initialState: Set<DeepLocation>): Int =
    generateSequence(initialState) { runDeepStep(it) }.elementAt(200).size

private fun runStep(locations: Set<Location>, size: Int): Set<Location> =
    (0 until size).asSequence()
        .flatMap { x ->
            (0 until size).asSequence()
                .map { y -> Location(x, y) }
                .filter { location ->
                    val neighborCount = location.neighbors().asSequence()
                        .filter { it in locations }
                        .count()
                    if (location in locations) neighborCount == 1 else neighborCount == 1 || neighborCount == 2
                }
        }
        .toSet()

private fun runDeepStep(state: Set<DeepLocation>): Set<DeepLocation> {
    val minDepth = state.asSequence()
        .map { it.depth }
        .min()!!
    val maxDepth = state.asSequence()
        .map { it.depth }
        .max()!!
    return (0 until 5).asSequence()
        .flatMap { x ->
            (0 until 5).asSequence().flatMap { y ->
                (minDepth - 1..maxDepth + 1).asSequence()
                    .map { depth -> DeepLocation(x, y, depth) }
                    .filter { location ->
                        val neighborCount = location.neighbors().asSequence()
                            .filter { it in state }
                            .count()
                        if (location in state) neighborCount == 1 else neighborCount == 1 || neighborCount == 2
                    }
            }
        }
        .toSet()
}

private fun getBiodiversityRating(locations: Set<Location>): Int =
    (0 until 5).asSequence()
        .flatMap { x ->
            (0 until 5).asSequence()
                .filter { y -> Location(x, y) in locations }
                .map { y -> (1).shl(x + 5 * y) }
        }
        .sum()

private fun parseLocations(input: List<String>): Set<Location> =
    input.indices.asSequence()
        .flatMap { y ->
            input[y].indices.asSequence()
                .filter { x -> input[y][x] == '#' }
                .map { x -> Location(x, y) }
        }
        .toSet()
