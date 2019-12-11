package day8

import util.readSingleString

private const val WIDTH = 25
private const val HEIGHT = 6
private const val AREA = WIDTH * HEIGHT

fun main() {
    val input = readSingleString("src/day8/input8.txt")
    println(solvePart1(input))
    solvePart2(input)
}

private fun solvePart1(input: String): Int =
    input
        .chunked(AREA)
        .map { getCountsMap(it.asIterable()) }
        .minBy { it.getValue('0') }!!
        .let { it.getValue('1') * it.getValue('2') }

private fun solvePart2(input: String) {
    val layers = input.chunked(AREA)
    val resolvedPixels = (0 until AREA)
        .map { i -> resolveLayers(layers.indices.map { layers[it][i] }) }
        .map { if (it == '0') ' ' else '█' }
    resolvedPixels
        .chunked(WIDTH)
        .map { it.joinToString("") }
        .forEach { println(it) }
}

private fun <T>getCountsMap(xs: Iterable<T>): Map<T, Int> {
    val result = mutableMapOf<T, Int>().withDefault { 0 }
    xs.forEach { result[it] = result.getValue(it) + 1 }
    return result
}

private fun resolveLayers(layers: List<Char>): Char = layers.find { it != '2' }!!