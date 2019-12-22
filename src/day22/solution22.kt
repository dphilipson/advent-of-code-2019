package day22

import util.readStringPerLine

private sealed class ShuffleStep {
    abstract fun apply(deck: List<Int>): List<Int>
    abstract fun invert(deckSize: Long, binomial: ModularBinomial): ModularBinomial
}

private object DealIntoNewStack : ShuffleStep() {
    override fun apply(deck: List<Int>): List<Int> = deck.asReversed()
    override fun invert(deckSize: Long, binomial: ModularBinomial): ModularBinomial =
        -binomial + (deckSize - 1)

    override fun toString(): String = "DealIntoNewStack"
}

private data class Cut(val n: Int) : ShuffleStep() {
    override fun apply(deck: List<Int>): List<Int> {
        val positiveN = positiveMod(n, deck.size)
        return listOf(deck.subList(positiveN, deck.size), deck.subList(0, positiveN)).flatten()
    }

    override fun invert(deckSize: Long, binomial: ModularBinomial): ModularBinomial = binomial + n.toLong()

}

private data class DealWithIncrement(val increment: Int) : ShuffleStep() {
    override fun apply(deck: List<Int>): List<Int> {
        val newDeck = deck.toMutableList()
        for (i in deck.indices) {
            newDeck[(i * increment) % deck.size] = deck[i]
        }
        return newDeck
    }

    override fun invert(deckSize: Long, binomial: ModularBinomial): ModularBinomial =
        binomial * inverseModP(increment.toLong(), deckSize)
}

private class ModularBinomial(rawCoefficient: Long, rawConstant: Long, val modulus: Long) {
    val coefficient = normalize(rawCoefficient)
    val constant = normalize(rawConstant)

    companion object {
        fun identityWithModulus(modulus: Long): ModularBinomial = ModularBinomial(1, 0, modulus)
    }

    operator fun plus(other: ModularBinomial): ModularBinomial =
        ModularBinomial(coefficient + other.coefficient, constant + other.constant, modulus)

    operator fun plus(c: Long): ModularBinomial = this + ModularBinomial(0, c, modulus)

    operator fun unaryMinus(): ModularBinomial = this * -1

    operator fun minus(other: ModularBinomial): ModularBinomial = this + -other

    operator fun minus(c: Long): ModularBinomial = this + -c

    operator fun times(c: Long): ModularBinomial =
        ModularBinomial(multiply(c, coefficient), multiply(c, constant), modulus)

    /*
     * f(x) = ax + b
     * f^2(x) = a(ax + b) + b = a^2 x + ab + b
     * f^3(x) = a(a^2 x + ab + b) + b = a^3 x + a^2b + ab + b
     *        = a^3x + ((a^3 - 1) / (a - 1)) b
     * f^n(x) = a^n x + (a^n - 1) * (a - 1)^-1 * b
     */
    fun iterate(n: Long): ModularBinomial {
        val newCoefficient = modPow(coefficient, n, modulus)
        val newConstant = multiply(multiply(newCoefficient - 1, inverseModP(coefficient - 1, modulus)), constant)
        return ModularBinomial(newCoefficient, newConstant, modulus)
    }

    fun evaluate(x: Long): Long = normalize(multiply(coefficient, x) + constant)

    override fun toString(): String = "${coefficient}x + $constant"

    private fun normalize(x: Long): Long = positiveMod(x, modulus)
    private fun multiply(a: Long, b: Long): Long = modMultiply(a, b, modulus)
}

fun main() {
    val lines = readStringPerLine("src/day22/input22.txt")
    val steps = lines.map { parseStep(it) }
    println(solvePart1(steps))
    println(solvePart2(steps))
}

private fun solvePart1(steps: List<ShuffleStep>): Int =
    steps.fold((0 until 10007).toList()) { deck, step -> step.apply(deck) }.indexOf(2019)

private fun solvePart2(steps: List<ShuffleStep>): Long {
    val deckSize = 119315717514047L
    val repeatCount = 101741582076661L
    return steps
        .asReversed()
        .fold(ModularBinomial.identityWithModulus(deckSize)) { binomial, step ->
            step.invert(deckSize, binomial)
        }
        .iterate(repeatCount)
        .evaluate(2020)
}


private val CUT_PATTERN = """cut (-?\d+)""".toRegex()
private val DEAL_WITH_INCREMENT_PATTERN = """deal with increment (\d+)""".toRegex()

private fun parseStep(line: String): ShuffleStep {
    if (line == "deal into new stack") {
        return DealIntoNewStack
    }
    CUT_PATTERN.matchEntire(line)?.let { return Cut(it.groupValues[1].toInt()) }
    DEAL_WITH_INCREMENT_PATTERN.matchEntire(line)?.let { return DealWithIncrement(it.groupValues[1].toInt()) }
    throw Exception("Could not parse line \"$line\"")
}

private fun positiveMod(a: Int, b: Int): Int = ((a % b) + b) % b
private fun positiveMod(a: Long, b: Long): Long = ((a % b) + b) % b

private fun inverseModP(x: Long, p: Long): Long = modPow(x, p - 2, p)

private fun modPow(base: Long, exponent: Long, modulus: Long): Long {
    if (exponent == 0L) {
        return 1L
    }
    val sqrt = modPow(base, exponent / 2, modulus)
    val result = modMultiply(sqrt, sqrt, modulus)
    return if (exponent % 2L == 1L) modMultiply(result, base, modulus) else result
}

/** Avoids overflow */
private fun modMultiply(a: Long, b: Long, m: Long): Long {
    var result = 0L
    var x = a % m
    var y = b
    while (y > 0) {
        if (y % 2 == 1L) {
            result = (result + x) % m
        }
        x = (x * 2) % m
        y /= 2
    }
    return result
}
