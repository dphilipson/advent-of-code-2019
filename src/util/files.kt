package util

import java.io.File

private val WHITESPACE = Regex("\\s+")

fun readSingleString(filename: String): String = File(filename).readLines().first()

fun readStringPerLine(filename: String): List<String> = File(filename).bufferedReader().readLines()

fun readIntPerLine(filename: String): List<Int> = File(filename)
    .bufferedReader()
    .lineSequence()
    .map { it.toInt() }
    .toList()

fun readStrings(filename: String): List<List<String>> = File(filename)
    .bufferedReader()
    .lineSequence()
    .map { it.split(WHITESPACE) }
    .toList()

fun readStrings(filename: String, separator: String = ","): List<List<String>> = File(filename)
    .bufferedReader()
    .lineSequence()
    .map { it.split(separator) }
    .toList()

fun readInts(filename: String): List<List<Int>> = File(filename)
    .bufferedReader()
    .lineSequence()
    .map {
        it
            .split(WHITESPACE)
            .map { token -> token.toInt() }
    }
    .toList()

fun readInts(filename: String, separator: String = ","): List<List<Int>> = File(filename)
    .bufferedReader()
    .lineSequence()
    .map {
        it
            .split(separator)
            .map { token -> token.toInt() }
    }
    .toList()