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
[paste everything from "What the functions are" through "Summary" here]
## Step 6 (early pass): Unit Test
**Prompt used:** "Write a JUnit test in Kotlin for the quickSort function above. Include one
test that sorts a small unsorted list and prints the list before and after sorting so I can
visually confirm it works."

**Result:** Copilot generated a test covering a list with duplicate values, using both println
for visual confirmation and assertEquals for automated verification. Test passed. Still need to
add cases for empty list, already-sorted list, and a large dataset to fully satisfy step 6.