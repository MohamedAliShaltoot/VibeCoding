# QuickSort Lab — Copilot Process Log

## Step 2: Implementation
**Prompt used:**
"Write a QuickSort implementation in Kotlin as a function `fun quickSort(arr: MutableList<Int>,
low: Int = 0, high: Int = arr.size - 1)`. Use the Lomuto partition scheme. Sort in-place. Add
inline comments explaining the partitioning step and the recursive calls. Also add a second
overload `fun quickSort(arr: MutableList<Int>): MutableList<Int>`..."

**Result:** Copilot generated a correct Lomuto-partition recursive QuickSort on the first try.

**Modification made:** Original overload mutated the caller's list and returned the same
reference. Asked Copilot: "Modify the second quickSort overload so it does not mutate the
original list — it should sort a copy and return that copy, leaving the input list unchanged."
Result: Copilot correctly used `arr.toMutableList()` to create a defensive copy before sorting.
Verified original list stays unmodified.

## Step 3: Code Explanation
**Prompt used:** "Explain how the quickSort function and the partition function in this file
work, step by step. Describe the role of the pivot, how the partitioning loop rearranges
elements, and how the recursive calls eventually sort the full list. Write the explanation
so it could be used directly as documentation..."


**Explanation (Copilot output, trimmed of planning notes):**
**What the functions are**
- `quickSort(arr, low, high)`: in-place recursive QuickSort using Lomuto partitioning.
- `quickSort(arr)`: convenience overload that copies the list, sorts the copy, returns it
  unchanged from the original.
- `partition(arr, low, high)`: chooses `arr[high]` as pivot, rearranges so elements ≤ pivot
  come before it and elements > pivot come after, places pivot in final position, returns
  its index.

**Role of the pivot**
The pivot divides the subarray into two parts: elements ≤ pivot and elements > pivot. This
implementation always picks `arr[high]` as the pivot. After partitioning, the pivot sits at
its final sorted index — everything left of it is ≤ pivot, everything right is > pivot.

**Partition loop walkthrough**
`i = low - 1` tracks the boundary of the "≤ pivot" region. As `j` scans from `low` to
`high - 1`, any element ≤ pivot gets swapped into that boundary region and `i` advances.
After the loop, the pivot is swapped into position `i + 1`, which becomes the returned
`pivotIndex`.

**Concrete example:** `[5, 2, 9, 1, 5, 6]` with pivot `6` partitions step by step into
`[5, 2, 1, 5, 6, 9]`, with pivot landing at index 4 — left side (`[5,2,1,5]`) all ≤ 6, right
side (`[9]`) all > 6.

**Recursion:** after partitioning, the pivot is in its final position, so the algorithm
recursively sorts the left subarray and the right subarray independently. Base case is
`low >= high` (zero or one element, already sorted). Since each partition fixes at least
one element's final position and subarrays shrink each time, recursion always terminates.

**Edge cases & properties**
- In-place version mutates the array; the overload returns a non-mutating sorted copy.
- Empty and single-element lists are handled correctly by the base case.
- Not a stable sort — equal elements can be reordered relative to each other.

**Complexity:** average O(n log n), worst case O(n²) (e.g. already-sorted input with
last-element pivot). Average O(log n) extra stack space from recursion, worst case O(n)
stack depth on degenerate partitions. Randomized or median-of-three pivot selection would
reduce worst-case risk.

**Practical note:** for very small subarrays, QuickSort's overhead can exceed simpler
algorithms like insertion sort — a common optimization is switching to insertion sort
below a size threshold (e.g. <16 elements).

## Iterative Version
**Prompt used:** "Implement an iterative version of QuickSort in Kotlin in this same file,
named iterativeQuickSort(arr: MutableList<Int>). Use an explicit stack (ArrayDeque) to hold
the (low, high) ranges instead of recursion, but reuse the existing partition function..."

**Result:** Copilot generated a correct iterative version reusing the existing partition
function. Notably, it only pushes subranges with 2+ elements rather than pushing every
range and discarding trivial ones later — a small memory/performance optimization over the
naive approach. Verified it works on the same test cases as the recursive version.

### Step 4: Comparison

**Practical comparison (prompt: "Write a JUnit test that verifies the recursive quickSort
and the new iterativeQuickSort produce identical sorted output...")**

Added `recursiveAndIterativeProduceSameSortedOutput()` to QuickSortTest.kt — builds a list
with duplicates, sorts it with both the recursive (non-mutating) and iterative versions,
asserts identical output. Test passed.

**Conceptual comparison (prompt: "Compare the recursive and iterative quickSort
implementations in this file in terms of memory usage and risk of stack overflow on very
large inputs...")**

The recursive version uses the JVM call stack for recursion frames — average depth O(log n),
but worst-case (highly unbalanced partitions) depth reaches O(n), risking a stack overflow on
very large inputs. The iterative version uses a heap-allocated `ArrayDeque` instead, following
the same size bounds (average O(log n), worst-case O(n)) but avoiding the JVM call-stack limit
entirely — eliminating stack-overflow risk, at the cost of slightly higher per-entry overhead
from boxing. Note: pushing the larger partition onto the stack first (so the smaller one is
processed next) bounds the maximum stack depth closer to O(log n) regardless of input order.

## Step 5: Comparison with Other Sorting Algorithms
**Prompt used:** "Compare QuickSort, MergeSort, and HeapSort in terms of average and worst-case
time complexity, space complexity, and stability. Then explain what sorting algorithm Kotlin's
built-in MutableList.sort() and List.sorted() use internally, and how it compares to QuickSort
in practice."

**Result:**

| Algorithm | Avg Time | Worst Time | Space | Stable |
|---|---|---|---|---|
| QuickSort | O(n log n) | O(n²) | O(log n) avg, O(n) worst (stack) | No |
| MergeSort | O(n log n) | O(n log n) | O(n) auxiliary | Yes |
| HeapSort | O(n log n) | O(n log n) | O(1) | No |

Kotlin's `MutableList.sort()` / `List.sorted()` on the JVM (and Android) use **TimSort** — a
stable hybrid of merge sort and insertion sort, optimized for partially-ordered real-world
data, with O(n) auxiliary memory. A well-implemented QuickSort can be marginally faster on
fully random data due to better cache locality, but TimSort avoids QuickSort's O(n²) worst
case and is stable — which is why it's the sensible default for general-purpose sorting,
while a custom QuickSort makes sense when stability isn't required and worst-case risk is
mitigated (e.g. randomized pivot selection).

## Step 6: Unit Tests
**Prompt used (initial pass):** "Write a JUnit test in Kotlin for the quickSort function above.
Include one test that sorts a small unsorted list and prints the list before and after sorting
so I can visually confirm it works."

**Prompt used (remaining scenarios):** "Add JUnit tests for the quickSort function covering
these additional scenarios: an empty list, a list that is already sorted, and a large list of
10,000 randomly generated integers. For the large dataset test, also verify the iterative
version produces the same sorted result."

**Result:** Five tests in QuickSortTest.kt covering small-list-with-duplicates, recursive vs
iterative equivalence, empty list, already-sorted list (100 elements), and a large random
list (10,000 elements) cross-checked against both implementations. All pass.

**Note:** sorted and large were tested as separate scenarios, not combined — so the documented
O(n²)/stack-overflow worst case hasn't actually been triggered yet. Deliberately saved for
step 8.

## Step 7: Android UI (Jetpack Compose)
**Prompt used:** "Create a Jetpack Compose composable function called QuickSortScreen() in this
file. It should have a TextField where the user enters a comma-separated list of integers, a
Button labeled 'Sort' that parses the input into a List<Int>, calls the quickSort(arr:
MutableList<Int>) function from the com.example.demos.algorithms package, and a Text below
showing the sorted result. If the input contains anything that isn't a valid integer, show an
error message instead of crashing. Use remember and mutableStateOf for state."

**Result:** Copilot generated QuickSortScreen() and unexpectedly placed it inside
MainActivity.kt rather than the new file I had open, also wiring it into setContent {}
automatically — which would otherwise have been a separate manual step. Verified on an
emulator: valid input sorts and displays correctly; invalid input shows a red error message.

**Note:** Empty input falls through silently to the placeholder text rather than showing a
result or error — flagged for step 8.

## Step 8: Debug and Refine

### Intentional bug
**Bug introduced manually:** Changed `for (j in low until high)` to `for (j in low..high)` in
`partition`, which incorrectly included the pivot element (`arr[high]`) in its own comparison
loop, corrupting the partition boundary (`i`) and producing incorrect sort results.

**Prompt used:** "My QuickSort tests are failing. Can you find the bug in the partition
function and explain what's wrong and how to fix it?"

**Result:** Reverted to `for (j in low until high)`. Reran QuickSortTest — all 5 tests pass.

### Organic edge case: empty UI input
**Issue found:** Sort button gave no clear feedback when input was empty or blank — fell
through silently to the placeholder text, indistinguishable from "not yet sorted."

**Prompt used:** "In the QuickSortScreen composable, the Sort button currently shows no clear
feedback when the input is empty or blank... Update it so blank input shows an explicit error
message..."

**Result:** Added two checks — `input.isBlank()` for empty/whitespace input, and a separate
`tokens.isEmpty()` check for inputs like ",,,," that aren't blank but contain no actual
numbers. Both display "Please enter at least one number." Verified on emulator: empty input,
comma-only input, invalid integers, and valid input all behave correctly now.

## Step 9: Benchmark and Analyze Performance
**Prompt used:** "Write a benchmarking function... that compares execution time of the
recursive quickSort, iterativeQuickSort, and Kotlin's built-in List.sorted() across these
scenarios: a random list of 50,000 integers, an already-sorted list of 50,000 integers, and
a smaller random list of 5,000 integers..."

**Raw results:**

| Scenario | recursive quickSort | iterativeQuickSort | List.sorted() |
|---|---|---|---|
| Random, 50,000 | 48 ms | 59 ms | 42 ms |
| Already-sorted, 50,000 | StackOverflowError | 64,771 ms (~65 sec) | 2 ms |
| Random, 5,000 | 2 ms | 3 ms | 14 ms |

**Analysis:** Random data shows all three performing comparably, consistent with average-case
O(n log n). The already-sorted case confirms the documented worst case directly: the recursive
version overflowed the JVM stack at 50,000 levels of recursion; the iterative version avoided
the crash but still exhibited the same O(n²) degradation (~65 sec vs 59ms on random data of
the same size); Kotlin's built-in sort (TimSort) finished in 2ms by exploiting the existing
ascending run, matching its documented adaptive behavior. The small-random result (built-in
slower than custom implementations) is likely JVM warm-up/measurement noise rather than a
real signal, given the inconsistency with every other result.
