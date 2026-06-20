@file:Suppress("unused")

package com.example.demos.algorithms
/*
 * In-place QuickSort using the Lomuto partition scheme.
 * Sorts arr[low->high] recursively; pivot is always arr[high].
 * Average O(n log n), worst case O(n^2); not a stable sort.
 */

fun quickSort(arr: MutableList<Int>, low: Int = 0, high: Int = arr.size - 1) {
	if (low >= high) return

	val pivotIndex = partition(arr, low, high)

	// Recursively sort the elements smaller than the pivot.
	quickSort(arr, low, pivotIndex - 1)
	// Recursively sort the elements larger than the pivot.
	quickSort(arr, pivotIndex + 1, high)
}

/**
 * Returns a sorted copy of [arr] without mutating the original list.
 */
fun quickSort(arr: MutableList<Int>): MutableList<Int> {
	val copy = arr.toMutableList()
	quickSort(copy, 0, copy.size - 1)
	return copy
}

/**
 * Lomuto partition: moves the pivot (arr[high]) into its final sorted
 * position so everything before it is <= pivot and everything after is
 * > pivot. Returns the pivot's final index.
 */
private fun partition(arr: MutableList<Int>, low: Int, high: Int): Int {
	val pivot = arr[high]
	var i = low - 1

	for (j in low until high) {
		// Move every value smaller than the pivot to the left side.
		if (arr[j] <= pivot) {
			i++
			val temp = arr[i]
			arr[i] = arr[j]
			arr[j] = temp
		}
	}

	// Place the pivot between the smaller and larger partitions.
	val temp = arr[i + 1]
	arr[i + 1] = arr[high]
	arr[high] = temp

	return i + 1
}
