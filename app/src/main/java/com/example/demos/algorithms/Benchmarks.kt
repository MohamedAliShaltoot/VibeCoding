package com.example.demos.algorithms

import kotlin.random.Random
import kotlin.system.measureNanoTime

/**
 * Simple benchmark runner that prints execution times (ms) for three sorting
 * implementations: recursive quickSort (the non-mutating overload),
 * iterativeQuickSort (in-place), and Kotlin's built-in List.sorted().
 *
 * Scenarios:
 * - randomLarge: random list of 50,000 integers
 * - sortedLarge: already-sorted list of 50,000 integers
 * - randomSmall: random list of 5,000 integers
 *
 * Note: the recursive quickSort may throw StackOverflowError on pathological
 * inputs such as the already-sorted large list; that case is caught and a
 * clear message is printed instead of letting the benchmark crash.
 */
fun runSortBenchmarks() {
    val seed = 42
    val rnd = Random(seed)

    println("Preparing datasets...")

    val randomLarge = List(50_000) { rnd.nextInt() }
    val sortedLarge = (1..50_000).toList()
    val randomSmall = List(5_000) { rnd.nextInt() }

    fun timeMs(block: () -> Unit): Long = measureNanoTime(block) / 1_000_000

    println("\n--- Benchmark: Random large (50,000) ---")
    runScenario(randomLarge, ::timeMs)

    println("\n--- Benchmark: Already-sorted large (50,000) ---")
    runScenario(sortedLarge, ::timeMs, allowRecursiveOverflow = true)

    println("\n--- Benchmark: Random small (5,000) ---")
    runScenario(randomSmall, ::timeMs)
}

private fun runScenario(data: List<Int>, timeMs: ( () -> Unit) -> Long, allowRecursiveOverflow: Boolean = false) {
    // recursive quickSort (non-mutating overload) - measure on a fresh copy
    try {
        val recursiveTime = timeMs {
            val input = data.toMutableList()
            // quickSort(non-mutating) returns a sorted copy
            quickSort(input)
        }
        println("recursive quickSort: ${recursiveTime} ms")
    } catch (e: StackOverflowError) {
        if (allowRecursiveOverflow) {
            println("recursive quickSort: StackOverflowError (likely due to worst-case recursion depth)")
        } else {
            throw e
        }
    }

    // iterative quickSort (in-place) - operate on a mutable copy
    val iterativeTime = timeMs {
        val input = data.toMutableList()
        iterativeQuickSort(input)
    }
    println("iterativeQuickSort: ${iterativeTime} ms")

    // Kotlin built-in sorted() - operate on the immutable list
    val builtinTime = timeMs {
        data.sorted()
    }
    println("kotlin List.sorted(): ${builtinTime} ms")
}



