package day14

import util.readStringPerLine
import kotlin.math.max

private data class Amount(val count: Long, val unit: String)
private data class Recipe(val outputCount: Long, val parts: List<Amount>)

fun main() {
    val lines = readStringPerLine("src/day14/input14.txt")
    val recipes = parseRecipes(lines)
    println(solvePart1(recipes))
    println(solvePart2(recipes))
}

private fun solvePart1(recipes: Map<String, Recipe>): Long = getOreRequirement(recipes, 1L)

private fun solvePart2(recipes: Map<String, Recipe>) =
    findHighestSatisfyingNumber { getOreRequirement(recipes, it) <= 1000000000000L }

private fun getOreRequirement(recipes: Map<String, Recipe>, fuelAmount: Long): Long {
    val surplus = mutableMapOf<String, Long>().withDefault { 0 }
    val needsCrafting = mutableListOf(Amount(fuelAmount, "FUEL"))
    var oreRequired = 0L
    while (needsCrafting.isNotEmpty()) {
        val (count, unit) = needsCrafting.removeAt(needsCrafting.size - 1)
        val requiredCount = max(0, count - surplus.getValue(unit))
        surplus[unit] = max(0, surplus.getValue(unit) - count)
        val (outputCount, parts) = recipes.getValue(unit)
        val scale = (requiredCount + outputCount - 1) / outputCount
        for (part in parts) {
            if (part.unit == "ORE") {
                oreRequired += part.count * scale
            } else {
                needsCrafting += part.copy(count = part.count * scale)
            }
        }
        surplus[unit] = surplus.getValue(unit) + scale * outputCount - requiredCount
    }
    return oreRequired
}

private fun findHighestSatisfyingNumber(predicate: (Long) -> Boolean): Long {
    var high = 1L
    while (predicate(high)) {
        high *= 2
    }
    var low = high / 2
    while (high - low > 1) {
        val guess = (low + high) / 2
        if (predicate(guess)) {
            low = guess
        } else {
            high = guess
        }
    }
    return low
}

private fun parseRecipes(lines: List<String>): Map<String, Recipe> =
    lines.asSequence()
        .map { parseRecipe(it) }
        .toMap()


private fun parseRecipe(line: String): Pair<String, Recipe> {
    val match = """(.+) => (.+)""".toRegex().matchEntire(line)!!
    val parts = match.groups[1]!!.value
        .splitToSequence(", ")
        .map { parseAmount(it) }
        .toList()
    val result = parseAmount(match.groups[2]!!.value)
    return Pair(result.unit, Recipe(result.count, parts))
}

private fun parseAmount(s: String): Amount {
    val (count, unit) = s.split(" ")
    return Amount(count.toLong(), unit)
}