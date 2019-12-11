package day6

import util.readStrings

fun main() {
    val orbits = readStrings("src/day6/input6.txt", ")").map { Pair(it[1], it[0]) }
    println(solvePart1(orbits))
    println(solvePart2(orbits))
}

private fun solvePart1(orbits: List<Pair<String, String>>): Int {
    val orbitMap = orbits.toMap()
    val planetToOrbitCount = mutableMapOf<String, Int>()

    fun getOrbitCount(planet: String): Int {
        planetToOrbitCount[planet]?.let { return it }
        val orbitedPlanet = orbitMap[planet] ?: return 0
        val result = 1 + getOrbitCount(orbitedPlanet)
        planetToOrbitCount[planet] = result
        return result
    }

    return orbitMap.keys.map { getOrbitCount(it) }.sum()
}

private fun solvePart2(orbits: List<Pair<String, String>>): Int {
    val orbitMap = orbits.toMap()
    val santasAncestors = generateSequence("SAN") { orbitMap[it] }
        .drop(1)
        .takeWhile { orbitMap[it] != null }
        .mapIndexed { i, planet -> Pair(planet, i) }
        .toMap()
    return generateSequence("YOU") { orbitMap[it] }
        .drop(1)
        .mapIndexed { i, planet -> Pair(planet, i) }
        .find { santasAncestors.containsKey(it.first) }
        ?.let { it.second + santasAncestors.getValue(it.first) }
        ?: throw Exception("No common ancestor found")
}
