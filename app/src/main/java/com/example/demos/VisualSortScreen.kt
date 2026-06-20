package com.example.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
/**
 * Snapshot of the array state at a single point during QuickSort,
 * captured purely for visualization purposes — it does not affect the
 * sorting logic itself (see the existing quickSort / partition functions
 * for the canonical, non-visual implementation).
 *
 * @property array the array contents at this moment
 * @property low current partition's lower bound
 * @property high current partition's upper bound
 * @property pivotIndex index of the pivot element in this step (-1 once fully sorted)
 * @property activeIndex the index currently being compared or just swapped
 * @property isSwap true if this step represents a swap (vs. a comparison)
 */
data class SortStep(
    val array: List<Int>,
    val low: Int,
    val high: Int,
    val pivotIndex: Int,
    val activeIndex: Int,
    val isSwap: Boolean = false
)

/**
 * Recursive QuickSort identical in logic to [quickSort] (same Lomuto partition
 * scheme), but records a [SortStep] after every comparison and every swap so
 * the UI can animate the partitioning process step by step.
 *
 * Does not mutate the input list — sorts and records steps on a defensive copy.
 */
fun quickSortWithSteps(arr: MutableList<Int>): List<SortStep> {
    val steps = mutableListOf<SortStep>()
    val copy = arr.toMutableList()

    fun partition(a: MutableList<Int>, low: Int, high: Int): Int {
        val pivot = a[high]
        var i = low - 1
        for (j in low until high) {
            // Record the comparison step before deciding whether to swap
            steps.add(SortStep(a.toList(), low, high, high, j))
            if (a[j] <= pivot) {
                i++
                if (i != j) {
                    val tmp = a[i]; a[i] = a[j]; a[j] = tmp
                    steps.add(SortStep(a.toList(), low, high, high, i, isSwap = true))
                }
            }
        }
        // Place the pivot in its final position
        val tmp = a[i + 1]; a[i + 1] = a[high]; a[high] = tmp
        steps.add(SortStep(a.toList(), low, high, i + 1, i + 1, isSwap = true))
        return i + 1
    }

    fun sort(a: MutableList<Int>, low: Int, high: Int) {
        if (low >= high) return
        val p = partition(a, low, high)
        sort(a, low, p - 1)
        sort(a, p + 1, high)
    }

    sort(copy, 0, copy.size - 1)
    // Final step: everything sorted, nothing active/pivot to highlight
    steps.add(SortStep(copy.toList(), 0, copy.size - 1, -1, -1))
    return steps
}

/**
 * Renders a single [SortStep] as a bar chart.
 *
 * Color legend (applies to both the bar and its number label below it):
 * - Red: the current pivot element
 * - Orange: the element that was just swapped into place
 * - Yellow/Amber: the element currently being compared against the pivot
 * - Blue: any other element still inside the active [low, high] range
 * - Green: final state, once the whole array is sorted (pivotIndex == -1)
 *
 * --- Number label additions ---
 * Instead of plain text under each bar, each value is shown inside a small
 * rounded "chip" colored the same as its bar, with bold white text — making
 * the value clearly readable and visually tied to its bar at a glance.
 */

@Composable
fun SortBarChart(step: SortStep, modifier: Modifier = Modifier) {
    val maxValue = (step.array.maxOrNull() ?: 1).coerceAtLeast(1)
    val barAreaHeight = 170.dp

    fun colorFor(index: Int): Color = when {
        step.pivotIndex == -1 -> Color(0xFF4CAF50) // green: fully sorted
        index == step.pivotIndex -> Color(0xFFE53935) // red: pivot
        index == step.activeIndex && step.isSwap -> Color(0xFFFF9800) // orange: just swapped
        index == step.activeIndex -> Color(0xFFFFC107) // amber: comparing
        index in step.low..step.high -> Color(0xFF2196F3) // blue: active range
        else -> Color(0xFFB0BEC5) // gray: outside current range
    }

    Column(modifier = modifier) {
        // Bars, bottom-aligned, height proportional to value
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barAreaHeight),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            step.array.forEachIndexed { index, value ->
                val heightFraction = (value.toFloat() / maxValue).coerceIn(0.05f, 1f)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 3.dp)
                        .fillMaxHeight(heightFraction)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(colorFor(index))
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Number chips: one per bar, color-matched, bold white text on a rounded background
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            step.array.forEachIndexed { index, value ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorFor(index))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = value.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}