package com.example.demos.algorithms

import org.junit.Test
import org.junit.Assert.assertEquals

class QuickSortTest {
	@Test
	fun sortsSmallUnsortedList_andPrintsBeforeAfter() {
		val original = mutableListOf(5, 2, 9, 1, 5, 6)
		// Print the list before sorting so you can visually confirm the input.
		println("Before: $original")

		// Use the non-mutating overload which returns a sorted copy.
		val sorted = quickSort(original)

		// Print the resulting sorted list for visual confirmation.
		println("After:  $sorted")

		// Verify the returned list is sorted and the original list is unchanged.
		assertEquals(listOf(1, 2, 5, 5, 6, 9), sorted)
		assertEquals(listOf(5, 2, 9, 1, 5, 6), original)
	}
}
