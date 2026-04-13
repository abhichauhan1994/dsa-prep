# Topic 2: Two Pointers

Two Pointers is not a single technique. It's a family of four distinct sub-patterns, each solving a different class of problems, all sharing one idea: use two index variables that traverse a data structure in coordinated fashion to eliminate redundant work.

The family includes: converging pointers that close in from both ends, read/write pointers that compact arrays in-place, merge pointers that combine two sorted sequences, and fix-one-plus-sweep that reduces k-sum problems by one dimension. Knowing which sub-pattern to reach for, and why, is what separates a candidate who "knows two pointers" from one who can actually use it under pressure.

This document covers all four sub-patterns, every major problem variant, and the exact decision logic for when to use two pointers versus a HashMap, binary search, or sliding window.

---

## Table of Contents

1. [Core Concept](#1-core-concept)
2. [ELI5 — The Intuition](#2-eli5--the-intuition)
3. [When to Use — Recognition Signals](#3-when-to-use--recognition-signals)
4. [Core Templates in Java](#4-core-templates-in-java)
   - [Template 1: Opposite Direction (Converging)](#template-1-opposite-direction-converging)
   - [Template 2: Same Direction — Read/Write Pointer](#template-2-same-direction--readwrite-pointer)
   - [Template 3: Same Direction — Fast/Slow Pointer](#template-3-same-direction--fastslow-pointer)
   - [Template 4: Merge Two Sorted Arrays](#template-4-merge-two-sorted-arrays)
   - [Template 5: Fix One + Two Pointer Sweep](#template-5-fix-one--two-pointer-sweep)
5. [Real-World Applications](#5-real-world-applications)
6. [Problem Categories and Solutions](#6-problem-categories-and-solutions)
   - [Category A: Opposite Direction — Pair/Sum Problems](#category-a-opposite-direction--pairsum-problems)
   - [Category B: Same Direction — In-Place Manipulation](#category-b-same-direction--in-place-manipulation)
   - [Category C: Two Arrays — Merge/Intersection](#category-c-two-arrays--mergeintersection)
   - [Category D: Fix One + Sweep — k-Sum Problems](#category-d-fix-one--sweep--k-sum-problems)
   - [Category E: Advanced / Hard](#category-e-advanced--hard)
7. [Common Mistakes and Edge Cases](#7-common-mistakes-and-edge-cases)
8. [Pattern Comparison — Two Pointers vs Sliding Window vs HashMap](#8-pattern-comparison--two-pointers-vs-sliding-window-vs-hashmap)
9. [Quick Reference Cheat Sheet](#9-quick-reference-cheat-sheet)
10. [Practice Roadmap](#10-practice-roadmap)

---

**Difficulty Distribution** (based on LeetCode's 244 tagged two-pointer problems):
- Easy: 27%
- Medium: 59%
- Hard: 14%

**Top Companies:**
- Google, Amazon, Meta, Microsoft, Bloomberg, Apple, Uber, Goldman Sachs

---

## 1. Core Concept

### What is Two Pointers?

Two pointers is a technique where you maintain two index variables — typically called `left` and `right`, or `read` and `write`, or `i` and `j` — and move them through a data structure according to a set of rules. The key is that each pointer move is not arbitrary: it's driven by a logical invariant that guarantees you're not missing any valid answers while skipping invalid ones.

The power comes from the invariant. When you move a pointer, you're not just advancing an index — you're eliminating an entire class of possibilities from the search space. That's what turns O(n^2) into O(n).

### The Four Sub-Patterns

**Sub-pattern 1: Opposite Direction (Converging)**

```
left = 0, right = n-1
Move toward each other until left >= right
```

Used when the array is sorted and you're looking for a pair satisfying some condition. If the current pair's value is too small, move `left` right (increase the value). If too large, move `right` left (decrease the value). Each move eliminates one element from consideration.

Classic problems: Two Sum II, Container With Most Water, Valid Palindrome, Trapping Rain Water.

**Sub-pattern 2: Same Direction — Read/Write**

```
write = 0, read = 0
read scans every element; write only advances when we keep an element
```

Used for in-place array compaction. The `read` pointer visits every element. The `write` pointer marks the boundary of the "processed" region. Elements before `write` are the final answer; elements from `write` onward are scratch space.

Classic problems: Remove Duplicates, Remove Element, Move Zeroes.

**Sub-pattern 3: Same Direction — Fast/Slow**

```
slow = head, fast = head
fast moves 2 steps per iteration; slow moves 1
```

Used for cycle detection and finding the middle of a linked list. This sub-pattern is covered in depth in Topic 3 (Fast and Slow Pointers). It's mentioned here for completeness because it shares the "two pointers, same direction, different speeds" structure.

**Sub-pattern 4: Two Arrays (Merge)**

```
i = 0 (pointer into array A), j = 0 (pointer into array B)
Advance the pointer pointing to the smaller element
```

Used when merging or comparing two sorted sequences. Each pointer only moves forward, and you advance whichever pointer points to the smaller (or more relevant) element.

Classic problems: Merge Sorted Array, Intersection of Two Arrays, Interval List Intersections.

**Sub-pattern 5: Fix One + Sweep (k-Sum)**

```
for (int i = 0; i < n; i++) {       // fix one element
    int left = i + 1, right = n - 1; // two pointers on the rest
    while (left < right) { ... }
}
```

Used for 3Sum, 4Sum, and similar problems. Sort the array first. Fix one element with an outer loop, then use the converging two-pointer technique on the remaining subarray. This reduces an O(n^3) brute force to O(n^2).

### Time Complexity Improvement

| Problem Type | Brute Force | Two Pointers |
|---|---|---|
| Find pair with target sum (sorted) | O(n^2) | O(n) |
| Find triplet with target sum | O(n^3) | O(n^2) |
| Find quadruplet with target sum | O(n^4) | O(n^3) |
| Remove duplicates in-place | O(n^2) with shifting | O(n) |
| Merge two sorted arrays | O(n log n) re-sort | O(n+m) |

### The Key Insight

In a sorted array, if you fix a left pointer at position `i` and a right pointer at position `j`, and the sum `arr[i] + arr[j]` is too large, you know that `arr[i] + arr[k]` for any `k > i` and `k < j` is also too large (because `arr[k] < arr[j]`). So you can eliminate all those pairs by moving `right` left by one. One pointer move eliminates an entire column of the search space.

This is the invariant that makes two pointers work. Without sorted order (or some equivalent structural guarantee), you can't make this elimination, and you need a HashMap instead.

---

## 2. ELI5 — The Intuition

### Opposite Direction: The Hallway Walk

Two people start at opposite ends of a long hallway. They walk toward each other. When they meet, they've covered the entire hallway, but each person only walked half of it.

Now imagine the hallway is a sorted array, and you're looking for two people whose combined height equals a target. If the two people you picked are too short together, the person on the left needs to be replaced with someone taller — so they step right. If they're too tall together, the person on the right steps left. They keep adjusting until they find the right pair or meet in the middle.

The key: because the hallway is sorted (shorter people on the left, taller on the right), every step eliminates everyone behind the pointer. You never need to go back.

### Same Direction (Read/Write): The Book Sorter

A librarian has a shelf of books, some good and some damaged. One person (the reader) picks up every book and checks it. Another person (the writer) only takes the good ones and places them at the front of the shelf.

The reader is always at the same position or ahead of the writer. The writer never skips forward unless the reader hands them a good book. When the reader finishes, everything from position 0 to the writer's position is the cleaned-up shelf.

This is exactly how "remove duplicates" and "move zeros" work. The read pointer scans everything; the write pointer only advances when you want to keep an element.

### Merge (Two Arrays): The Card Sorter

Two friends each have a sorted deck of cards, face up. They compare the top cards of each deck, pick the smaller one, and place it in a new pile. They keep going until both decks are empty.

At every step, exactly one card moves from one of the two decks to the result pile. Neither friend ever needs to look at more than the top card of their deck. The result is a perfectly sorted pile, built in O(n+m) time.

### Fix + Sweep (3Sum): The Locked Anchor

You're doing the hallway walk, but now you need three people whose heights sum to a target. Lock one person in place (the anchor). Now do the hallway walk with the remaining people. When the walk finishes, move the anchor one step and repeat.

The anchor moves through every position once (O(n)). For each anchor position, the hallway walk takes O(n). Total: O(n^2). Without the anchor trick, you'd need three nested loops: O(n^3).

---

## 3. When to Use — Recognition Signals

### Green Flags for Each Sub-Pattern

**Opposite Direction (Converging):**
- "sorted array" + "find pair" + "target sum"
- "palindrome check"
- "container with most water" / "maximize area"
- "two sum in sorted array"
- "reverse array in-place"
- "closest pair to target"
- The problem involves comparing elements from both ends

**Same Direction (Read/Write):**
- "in-place" modification required
- "remove duplicates from sorted array"
- "remove element"
- "move zeros to end"
- "partition array" (all elements satisfying condition first)
- "compress array"
- You need to compact an array without extra space

**Merge (Two Arrays):**
- "merge two sorted arrays/lists"
- "intersection of two sorted arrays"
- "compare two sorted sequences"
- "interval list intersections"
- Two separate sorted inputs that need to be combined or compared

**Fix One + Sweep (k-Sum):**
- "3Sum", "4Sum", "triplets", "quadruplets"
- "find all unique triplets that sum to target"
- "closest sum of three numbers"
- "count triplets satisfying condition"
- k >= 3 elements summing to a target

### When NOT to Use Two Pointers

**Use HashMap instead when:**
- The array is unsorted and sorting would destroy needed index information
- You need to find a pair with a target sum in an unsorted array (Two Sum I)
- The problem asks for original indices, not values
- Sorting is O(n log n) and a HashMap gives O(n) — and you can't afford the sort

**Use Binary Search instead when:**
- The array is sorted and you need to find a single element (not a pair)
- You need O(log n) per query, not O(n) total
- The search space has a monotonic property you can exploit

**Use Backtracking instead when:**
- You need ALL combinations or permutations, not just pairs/triplets
- The problem is about generating subsets, not finding a specific sum

**Use a Stack instead when:**
- The problem involves matching brackets or nested structures
- You need to look back at previous elements in a non-linear way
- Trapping Rain Water can be solved with a stack (though two pointers is cleaner)

**Use appropriate traversal instead when:**
- The data structure is non-linear (tree, graph)
- Two pointers only work on linear structures (arrays, strings, linked lists)

---

## 4. Core Templates in Java

### Template 1: Opposite Direction (Converging)

```java
/**
 * OPPOSITE DIRECTION TWO POINTERS
 *
 * Use case: Find a pair in a sorted array satisfying some condition.
 *
 * Key variables:
 *   left  — starts at index 0 (smallest element)
 *   right — starts at index n-1 (largest element)
 *
 * Invariant: left < right at all times during the loop.
 *            Everything to the left of 'left' has been eliminated.
 *            Everything to the right of 'right' has been eliminated.
 *
 * Termination: left >= right (pointers have crossed or met)
 *
 * Why it works: In a sorted array, if arr[left] + arr[right] < target,
 * then arr[left] + arr[k] < target for ALL k <= right. So we can safely
 * move left++ to try a larger left value. Symmetric logic for right--.
 */
public int[] twoSumSorted(int[] arr, int target) {
    int left = 0;
    int right = arr.length - 1;

    while (left < right) {
        int sum = arr[left] + arr[right];

        if (sum == target) {
            return new int[]{left + 1, right + 1}; // 1-indexed for LC 167
        } else if (sum < target) {
            left++;  // sum too small → need a larger left element
        } else {
            right--; // sum too large → need a smaller right element
        }
    }

    return new int[]{-1, -1}; // no pair found
}
```

**Step-by-step execution trace** on `arr = [2, 7, 11, 15], target = 9`:

```
Initial: left=0 (arr[0]=2), right=3 (arr[3]=15)

Iteration 1:
  sum = 2 + 15 = 17 > 9 → right--
  left=0, right=2

Iteration 2:
  sum = 2 + 11 = 13 > 9 → right--
  left=0, right=1

Iteration 3:
  sum = 2 + 7 = 9 == 9 → found!
  return [1, 2]
```

---

### Template 2: Same Direction — Read/Write Pointer

```java
/**
 * SAME DIRECTION — READ/WRITE POINTER
 *
 * Use case: In-place array compaction (remove elements, deduplicate, partition).
 *
 * Key variables:
 *   write — the "slow" pointer; marks the boundary of the processed region.
 *           Everything at indices [0, write) is the final answer.
 *   read  — the "fast" pointer; scans every element of the array.
 *
 * Invariant: write <= read at all times.
 *            arr[0..write-1] contains only the elements we want to keep,
 *            in their original relative order.
 *
 * Termination: read reaches arr.length
 *
 * Why it works: The write pointer only advances when we decide to keep an
 * element. The read pointer always advances. So we never look at an element
 * twice, and we never shift elements (which would be O(n) per removal).
 */
public int removeElement(int[] arr, int val) {
    int write = 0; // next position to write a "kept" element

    for (int read = 0; read < arr.length; read++) {
        if (arr[read] != val) {
            // Keep this element: copy it to the write position
            arr[write] = arr[read];
            write++;
        }
        // If arr[read] == val, skip it (don't advance write)
    }

    // write is now the count of elements we kept
    return write;
}
```

**Step-by-step execution trace** on `arr = [3, 2, 2, 3], val = 3`:

```
Initial: write=0

read=0: arr[0]=3 == val → skip. write=0
read=1: arr[1]=2 != val → arr[0]=2, write=1
read=2: arr[2]=2 != val → arr[1]=2, write=2
read=3: arr[3]=3 == val → skip. write=2

Result: arr = [2, 2, _, _], return 2
```

---

### Template 3: Same Direction — Fast/Slow Pointer

```java
/**
 * SAME DIRECTION — FAST/SLOW POINTER (Floyd's Tortoise and Hare)
 *
 * Use case: Cycle detection in linked lists, finding the middle node.
 *
 * Key variables:
 *   slow — moves 1 step per iteration
 *   fast — moves 2 steps per iteration
 *
 * Invariant: fast is always ahead of slow (or equal at the start/meeting point).
 *
 * NOTE: This sub-pattern is covered in full depth in Topic 3: Fast and Slow
 * Pointers. It's included here for structural completeness — it belongs to the
 * two-pointer family but has its own dedicated topic because of the linked list
 * and cycle-detection problems it unlocks.
 *
 * Brief example: find middle of linked list
 */
public ListNode findMiddle(ListNode head) {
    ListNode slow = head;
    ListNode fast = head;

    // fast moves 2 steps; slow moves 1 step
    // when fast reaches the end, slow is at the middle
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }

    return slow; // middle node
}
```

---

### Template 4: Merge Two Sorted Arrays

```java
/**
 * MERGE TWO SORTED ARRAYS
 *
 * Use case: Combine or compare two sorted sequences.
 *
 * Key variables:
 *   i — pointer into array A (or the first sequence)
 *   j — pointer into array B (or the second sequence)
 *   k — pointer into the result array
 *
 * Invariant: Both i and j only move forward.
 *            At each step, we pick the smaller of A[i] and B[j].
 *
 * Termination: Both i and j have reached the end of their arrays.
 *
 * Why it works: Because both arrays are sorted, the smallest remaining
 * element is always at the front of one of the two arrays. We never need
 * to look further back.
 */
public int[] mergeSorted(int[] A, int[] B) {
    int i = 0, j = 0, k = 0;
    int[] result = new int[A.length + B.length];

    // While both arrays have elements remaining
    while (i < A.length && j < B.length) {
        if (A[i] <= B[j]) {
            result[k++] = A[i++]; // A's element is smaller (or equal)
        } else {
            result[k++] = B[j++]; // B's element is smaller
        }
    }

    // Drain remaining elements from whichever array isn't exhausted
    while (i < A.length) result[k++] = A[i++];
    while (j < B.length) result[k++] = B[j++];

    return result;
}
```

**Step-by-step execution trace** on `A = [1, 3, 5], B = [2, 4, 6]`:

```
Initial: i=0, j=0, k=0, result=[]

Step 1: A[0]=1 <= B[0]=2 → result[0]=1, i=1, k=1
Step 2: A[1]=3 > B[0]=2  → result[1]=2, j=1, k=2
Step 3: A[1]=3 <= B[1]=4 → result[2]=3, i=2, k=3
Step 4: A[2]=5 > B[1]=4  → result[3]=4, j=2, k=4
Step 5: A[2]=5 <= B[2]=6 → result[4]=5, i=3, k=5
Step 6: i=3 == A.length, exit main loop
Drain B: result[5]=6, j=3, k=6

Result: [1, 2, 3, 4, 5, 6]
```

---

### Template 5: Fix One + Two Pointer Sweep (3Sum Pattern)

```java
/**
 * FIX ONE + TWO POINTER SWEEP
 *
 * Use case: Find all unique triplets (or k-tuples) summing to a target.
 *
 * Key variables:
 *   i     — the "fixed" element (outer loop)
 *   left  — starts at i+1 (just after the fixed element)
 *   right — starts at n-1
 *
 * Invariant: The array is sorted. For a fixed arr[i], we use converging
 *            two pointers on arr[i+1..n-1] to find pairs summing to
 *            (target - arr[i]).
 *
 * Duplicate handling: After finding a valid triplet, skip all duplicate
 *   values for left and right. Also skip duplicate values for i in the
 *   outer loop (i > 0 && arr[i] == arr[i-1]).
 *
 * Time complexity: O(n^2) — outer loop O(n) × inner two-pointer O(n)
 * Space complexity: O(1) excluding the output list
 *
 * CRITICAL: Sort the array first. Without sorting, the two-pointer
 * invariant doesn't hold and you'll miss valid pairs.
 */
public List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums); // MUST sort first
    List<List<Integer>> result = new ArrayList<>();

    for (int i = 0; i < nums.length - 2; i++) {
        // Skip duplicate values for the fixed element
        if (i > 0 && nums[i] == nums[i - 1]) continue;

        // Early termination: if the smallest possible triplet is > 0, stop
        if (nums[i] > 0) break;

        int left = i + 1;
        int right = nums.length - 1;
        int target = -nums[i]; // we want left + right == -nums[i]

        while (left < right) {
            int sum = nums[left] + nums[right];

            if (sum == target) {
                result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                // Skip duplicates for left pointer
                while (left < right && nums[left] == nums[left + 1]) left++;
                // Skip duplicates for right pointer
                while (left < right && nums[right] == nums[right - 1]) right--;

                left++;
                right--;
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
    }

    return result;
}
```

**Step-by-step execution trace** on `nums = [-4, -1, -1, 0, 1, 2]` (after sorting):

```
Sorted: [-4, -1, -1, 0, 1, 2]

i=0, nums[0]=-4, target=4, left=1, right=5
  sum = nums[1]+nums[5] = -1+2 = 1 < 4 → left++
  sum = nums[2]+nums[5] = -1+2 = 1 < 4 → left++
  sum = nums[3]+nums[5] = 0+2 = 2 < 4 → left++
  sum = nums[4]+nums[5] = 1+2 = 3 < 4 → left++
  left=5 == right=5, exit inner loop

i=1, nums[1]=-1, target=1, left=2, right=5
  sum = nums[2]+nums[5] = -1+2 = 1 == 1 → add [-1,-1,2]
    skip duplicates: nums[2]==nums[3]? -1==0? No. nums[5]==nums[4]? 2==1? No.
    left=3, right=4
  sum = nums[3]+nums[4] = 0+1 = 1 == 1 → add [-1,0,1]
    left=4, right=3, exit inner loop

i=2, nums[2]=-1 == nums[1]=-1 → skip (duplicate)

i=3, nums[3]=0, target=0, left=4, right=5
  sum = nums[4]+nums[5] = 1+2 = 3 > 0 → right--
  left=4, right=4, exit inner loop

Result: [[-1,-1,2], [-1,0,1]]
```

---

## 5. Real-World Applications

### 1. Merge Sort (Merge Step)

The merge step of merge sort is the canonical two-pointer merge. Given two sorted halves of an array, two pointers — one per half — walk forward simultaneously, always picking the smaller element. This runs in O(n) time and is the reason merge sort achieves O(n log n) overall. Every time you call `Collections.sort()` in Java on a list of objects, you're running TimSort, which uses this exact merge step internally.

### 2. Database Sort-Merge Join

When a database executes a join on two sorted tables (or two sorted index scans), it uses the two-pointer merge pattern. PostgreSQL's merge join and MySQL's sort-merge join both walk through two sorted result sets with one pointer per set, matching rows where the join key is equal. The total cost is O(n+m) — linear in the size of both tables — which is why databases prefer this over nested-loop joins when both sides are sorted.

### 3. Git Diff Algorithm

Git's diff algorithm compares two versions of a file to find added and deleted lines. At its core, it uses a two-pointer walk through both versions, advancing through lines that match (common subsequence) and marking divergences as additions or deletions. The Myers diff algorithm, which Git uses, is fundamentally a two-pointer traversal over the edit graph of the two files.

### 4. Memory Compaction in Garbage Collection

Compacting garbage collectors (like the G1 GC in the JVM) use a read/write pointer pattern during the compaction phase. The read pointer scans all objects in the heap. The write pointer tracks the next free position. When the read pointer finds a live object, it copies it to the write position and advances both pointers. Dead objects are skipped (read advances, write stays). This is exactly the "move zeros to end" pattern, applied to heap memory.

### 5. Network Packet Stream Merging

Network monitoring systems often receive timestamped packets from multiple sorted streams (one per network interface or sensor). Merging these into a single chronological stream uses the merge-two-sorted pattern, extended to k streams with a priority queue. The two-pointer version handles the two-stream case in O(n+m) without any extra heap allocation.

### 6. Palindrome Detection in Bioinformatics

DNA sequences contain palindromic subsequences that are recognition sites for restriction enzymes (used in genetic engineering). Detecting whether a DNA segment is palindromic uses the opposite-direction two-pointer pattern: one pointer starts at the 5' end, one at the 3' end, and they walk toward each other checking complementary base pairs (A-T, G-C). This is the biological equivalent of LC 125 (Valid Palindrome).

---

## 6. Problem Categories and Solutions

---

### Category A: Opposite Direction — Pair/Sum Problems

---

#### LC 167 — Two Sum II - Input Array Is Sorted (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Adobe, Apple, TikTok, Visa, Yandex, Zoho (17 companies)

**Problem:** Given a 1-indexed sorted array `numbers`, find two numbers that add up to `target`. Return their 1-indexed positions. Exactly one solution exists. Use O(1) extra space.

**Why two pointers fit:** The array is sorted. This is the textbook case for opposite-direction two pointers. The sorted order gives us the invariant: if the current sum is too small, the left element is too small (move left right); if too large, the right element is too large (move right left).

**Why not HashMap:** The problem explicitly requires O(1) space. A HashMap would work but uses O(n) space. Also, the sorted constraint is a signal that two pointers is the intended approach.

```java
public int[] twoSum(int[] numbers, int target) {
    int left = 0;
    int right = numbers.length - 1;

    while (left < right) {
        int sum = numbers[left] + numbers[right];

        if (sum == target) {
            // 1-indexed result
            return new int[]{left + 1, right + 1};
        } else if (sum < target) {
            // Sum too small: need a larger left element
            // All pairs (left, k) for k < right are also too small — skip them
            left++;
        } else {
            // Sum too large: need a smaller right element
            // All pairs (k, right) for k > left are also too large — skip them
            right--;
        }
    }

    return new int[]{-1, -1}; // guaranteed not to reach here per problem statement
}
```

**Time:** O(n) — each pointer moves at most n times total.
**Space:** O(1).

**Key insight:** The sorted order is the invariant that makes each pointer move safe. Without it, moving `left++` might skip the valid answer.

---

#### LC 11 — Container With Most Water (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, Uber

**Problem:** Given an array `height` of n non-negative integers, where each element represents a vertical line at position i with height `height[i]`. Find two lines that together with the x-axis form a container that holds the most water. Return the maximum amount of water.

**Why two pointers fit:** The area between lines at positions `left` and `right` is `min(height[left], height[right]) * (right - left)`. We want to maximize this. Starting with the widest possible container (left=0, right=n-1) and moving inward is the right approach — but which pointer do we move?

**The greedy insight:** The area is limited by the shorter line. If we move the pointer pointing to the taller line, the width decreases AND the height can only stay the same or decrease (since the new height is still bounded by the shorter line). So we'd never improve. Moving the pointer pointing to the shorter line gives us a chance to find a taller line that compensates for the reduced width.

**Proof of correctness:** When we move the shorter pointer, we're saying: "No pair involving the current shorter line and any line between left and right can beat the current area." This is true because:
- Any such pair has smaller width than the current pair.
- The height is bounded by the shorter line (which we're about to discard).
- So the area can only be smaller.

```java
public int maxArea(int[] height) {
    int left = 0;
    int right = height.length - 1;
    int maxWater = 0;

    while (left < right) {
        // Width is the distance between the two lines
        int width = right - left;
        // Height is limited by the shorter line
        int h = Math.min(height[left], height[right]);
        // Update maximum
        maxWater = Math.max(maxWater, width * h);

        // Move the pointer pointing to the shorter line
        // Moving the taller line's pointer can never improve the result:
        // width decreases, and height is still bounded by the shorter line
        if (height[left] < height[right]) {
            left++;
        } else {
            right--; // also handles the equal case (either pointer works)
        }
    }

    return maxWater;
}
```

**Dry Run** on `height = [1, 8, 6, 2, 5, 4, 8, 3, 7]`:

```
left=0 (h=1), right=8 (h=7)
  width=8, h=min(1,7)=1, area=8. maxWater=8
  height[0]=1 < height[8]=7 → left++

left=1 (h=8), right=8 (h=7)
  width=7, h=min(8,7)=7, area=49. maxWater=49
  height[1]=8 > height[8]=7 → right--

left=1 (h=8), right=7 (h=3)
  width=6, h=min(8,3)=3, area=18. maxWater=49
  height[1]=8 > height[7]=3 → right--

left=1 (h=8), right=6 (h=8)
  width=5, h=min(8,8)=8, area=40. maxWater=49
  height[1]=8 == height[6]=8 → right-- (either works)

left=1 (h=8), right=5 (h=4)
  width=4, h=min(8,4)=4, area=16. maxWater=49
  height[1]=8 > height[5]=4 → right--

left=1 (h=8), right=4 (h=5)
  width=3, h=min(8,5)=5, area=15. maxWater=49
  height[1]=8 > height[4]=5 → right--

left=1 (h=8), right=3 (h=2)
  width=2, h=min(8,2)=2, area=4. maxWater=49
  height[1]=8 > height[3]=2 → right--

left=1 (h=8), right=2 (h=6)
  width=1, h=min(8,6)=6, area=6. maxWater=49
  height[1]=8 > height[2]=6 → right--

left=1, right=1 → left >= right, exit loop

Answer: 49
```

**Time:** O(n).
**Space:** O(1).

**Key insight:** Always move the pointer pointing to the shorter line. Moving the taller line's pointer is provably suboptimal.

---

#### LC 125 — Valid Palindrome (Easy)

**Companies:** Meta, Amazon, Microsoft, Google, Apple, Bloomberg

**Problem:** Given a string `s`, return true if it's a palindrome considering only alphanumeric characters and ignoring case.

**Why two pointers fit:** Palindrome checking is the classic opposite-direction two-pointer problem. Start from both ends, skip non-alphanumeric characters, compare what's left.

```java
public boolean isPalindrome(String s) {
    int left = 0;
    int right = s.length() - 1;

    while (left < right) {
        // Skip non-alphanumeric characters from the left
        while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
            left++;
        }
        // Skip non-alphanumeric characters from the right
        while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
            right--;
        }

        // Compare characters (case-insensitive)
        if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
            return false;
        }

        left++;
        right--;
    }

    return true;
}
```

**Time:** O(n) — each character is visited at most once.
**Space:** O(1).

**Key insight:** The inner while loops for skipping non-alphanumeric characters don't add extra complexity — the total number of skip operations across all iterations is still bounded by n.

---

#### LC 680 — Valid Palindrome II (Easy)

**Companies:** Meta, Amazon, Microsoft, Google, Bloomberg

**Problem:** Given a string `s`, return true if it can become a palindrome after deleting at most one character.

**Why two pointers fit:** Start with the standard palindrome check. When you find a mismatch, you have exactly two choices: delete the left character or delete the right character. Check if either resulting substring is a palindrome.

```java
public boolean validPalindrome(String s) {
    int left = 0;
    int right = s.length() - 1;

    while (left < right) {
        if (s.charAt(left) != s.charAt(right)) {
            // Mismatch found. Try skipping either the left or right character.
            // If either resulting substring is a palindrome, return true.
            return isPalindromeRange(s, left + 1, right) ||
                   isPalindromeRange(s, left, right - 1);
        }
        left++;
        right--;
    }

    return true; // already a palindrome, no deletion needed
}

// Helper: check if s[left..right] is a palindrome
private boolean isPalindromeRange(String s, int left, int right) {
    while (left < right) {
        if (s.charAt(left) != s.charAt(right)) return false;
        left++;
        right--;
    }
    return true;
}
```

**Time:** O(n) — the main loop runs at most n/2 times, and the helper runs at most n/2 times once.
**Space:** O(1).

**Key insight:** You only ever need to try the deletion once. After the first mismatch, you have two candidates. If neither works, the answer is false. You never need to try deleting a second character.

---

#### LC 881 — Boats to Save People (Medium)

**Companies:** Google, Amazon, Microsoft, Bloomberg

**Problem:** Given an array `people` where `people[i]` is the weight of the i-th person, and a `limit` (max weight per boat), find the minimum number of boats needed. Each boat can carry at most 2 people.

**Why two pointers fit:** Sort the array. The optimal greedy strategy is to pair the heaviest person with the lightest person. If they fit together, great — one boat for two people. If not, the heaviest person needs their own boat (no one lighter can pair with them since even the lightest person makes it too heavy).

```java
public int numRescueBoats(int[] people, int limit) {
    Arrays.sort(people);
    int left = 0;               // lightest person
    int right = people.length - 1; // heaviest person
    int boats = 0;

    while (left <= right) {
        if (people[left] + people[right] <= limit) {
            // Lightest and heaviest fit together — pair them
            left++;
            right--;
        } else {
            // Heaviest person can't pair with anyone — they go alone
            right--;
        }
        boats++; // one boat used in either case
    }

    return boats;
}
```

**Time:** O(n log n) for sorting, O(n) for the two-pointer pass.
**Space:** O(1) or O(log n) for sort stack.

**Key insight:** The greedy pairing (heaviest with lightest) is optimal. If the heaviest person can't pair with the lightest, they can't pair with anyone. Proof: if `people[right] + people[left] > limit`, then `people[right] + people[k] > limit` for all `k >= left` (since `people[k] >= people[left]`).

---

#### LC 977 — Squares of a Sorted Array (Easy)

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given a sorted array of integers (possibly with negatives), return an array of the squares of each number, sorted in non-decreasing order.

**Why two pointers fit:** After squaring, the largest values come from either end of the original array (the most negative or most positive numbers). Use two pointers from both ends, always picking the larger square and placing it at the end of the result array.

```java
public int[] sortedSquares(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    int left = 0;
    int right = n - 1;
    int pos = n - 1; // fill result from the end (largest first)

    while (left <= right) {
        int leftSq = nums[left] * nums[left];
        int rightSq = nums[right] * nums[right];

        if (leftSq > rightSq) {
            result[pos--] = leftSq;
            left++;
        } else {
            result[pos--] = rightSq;
            right--;
        }
    }

    return result;
}
```

**Time:** O(n) for the two-pointer pass (plus O(n log n) if you sort naively — this approach avoids that).
**Space:** O(n) for the result array.

**Key insight:** Build the result from the back. The largest square is always at one of the two ends of the original sorted array.

---

### Category B: Same Direction — In-Place Manipulation

---

#### LC 26 — Remove Duplicates from Sorted Array (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given a sorted array `nums`, remove duplicates in-place so each element appears only once. Return the count of unique elements. The first k elements of `nums` should hold the result.

**Why two pointers fit:** The read/write pattern is perfect here. The write pointer marks the end of the deduplicated region. The read pointer scans forward. When read finds a new value (different from the last written value), write it to the write position.

```java
public int removeDuplicates(int[] nums) {
    if (nums.length == 0) return 0;

    int write = 1; // first element is always kept; start write at index 1

    for (int read = 1; read < nums.length; read++) {
        // A new unique element is found when it differs from the previous element
        // (which is the last element we wrote, since the array is sorted)
        if (nums[read] != nums[read - 1]) {
            nums[write] = nums[read];
            write++;
        }
    }

    return write; // count of unique elements
}
```

**Time:** O(n).
**Space:** O(1).

**Key insight:** Because the array is sorted, duplicates are always adjacent. So comparing `nums[read]` with `nums[read-1]` is sufficient to detect a new unique value. No need to compare with the write position explicitly.

---

#### LC 27 — Remove Element (Easy)

**Companies:** Amazon, Google, Microsoft

**Problem:** Given an array `nums` and a value `val`, remove all occurrences of `val` in-place. Return the count of elements not equal to `val`.

```java
public int removeElement(int[] nums, int val) {
    int write = 0;

    for (int read = 0; read < nums.length; read++) {
        if (nums[read] != val) {
            // Keep this element
            nums[write] = nums[read];
            write++;
        }
        // If nums[read] == val, skip it (write doesn't advance)
    }

    return write;
}
```

**Time:** O(n).
**Space:** O(1).

**Key insight:** This is the base template for all read/write two-pointer problems. The condition `nums[read] != val` is the "keep" condition. Change this condition to solve different variants.

---

#### LC 283 — Move Zeroes (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given an array `nums`, move all zeros to the end while maintaining the relative order of non-zero elements. Do it in-place.

**Why two pointers fit:** This is the read/write pattern with "keep non-zero elements." After the pass, fill the remaining positions with zeros.

```java
public void moveZeroes(int[] nums) {
    int write = 0;

    // Phase 1: Copy all non-zero elements to the front
    for (int read = 0; read < nums.length; read++) {
        if (nums[read] != 0) {
            nums[write] = nums[read];
            write++;
        }
    }

    // Phase 2: Fill the rest with zeros
    while (write < nums.length) {
        nums[write] = 0;
        write++;
    }
}
```

**Alternative (swap-based, fewer writes):**

```java
public void moveZeroesSwap(int[] nums) {
    int write = 0;

    for (int read = 0; read < nums.length; read++) {
        if (nums[read] != 0) {
            // Swap instead of overwrite — preserves zeros in their new positions
            int temp = nums[write];
            nums[write] = nums[read];
            nums[read] = temp;
            write++;
        }
    }
}
```

**Time:** O(n).
**Space:** O(1).

**Key insight:** The two-phase approach (copy non-zeros, then fill zeros) is slightly cleaner. The swap approach does fewer total writes when there are many non-zero elements.

---

#### LC 80 — Remove Duplicates from Sorted Array II (Medium)

**Companies:** Amazon, Google, Microsoft, Meta

**Problem:** Given a sorted array, remove duplicates such that each element appears at most twice. Return the count of valid elements.

**Why this is harder than LC 26:** You need to allow up to 2 occurrences. The trick is to compare the current read element with the element two positions behind the write pointer (not one position behind).

```java
public int removeDuplicates(int[] nums) {
    if (nums.length <= 2) return nums.length;

    int write = 2; // first two elements are always kept

    for (int read = 2; read < nums.length; read++) {
        // Keep nums[read] if it's different from the element two positions back
        // in the "kept" region. This allows at most 2 occurrences.
        //
        // Why nums[write - 2]? Because if nums[read] == nums[write-1] == nums[write-2],
        // then we already have 2 copies of this value, and adding another would make 3.
        if (nums[read] != nums[write - 2]) {
            nums[write] = nums[read];
            write++;
        }
    }

    return write;
}
```

**Generalization:** For "at most k occurrences," compare with `nums[write - k]`. The template becomes:

```java
// General template: allow at most k occurrences
public int removeDuplicatesK(int[] nums, int k) {
    int write = 0;
    for (int read = 0; read < nums.length; read++) {
        if (write < k || nums[read] != nums[write - k]) {
            nums[write] = nums[read];
            write++;
        }
    }
    return write;
}
```

**Time:** O(n).
**Space:** O(1).

**Key insight:** The comparison `nums[read] != nums[write - 2]` is the key. It checks whether adding the current element would create a third consecutive duplicate in the output.

---

#### LC 75 — Sort Colors / Dutch National Flag (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given an array with values 0, 1, and 2 (representing red, white, blue), sort it in-place so all 0s come first, then 1s, then 2s. One pass, O(1) space.

**Why this needs THREE pointers:** Two pointers can partition an array into two regions. Three values require three regions, so we need three pointers: `low` (boundary of 0s), `mid` (current element), `high` (boundary of 2s).

**The Dutch National Flag algorithm (Dijkstra):**

```java
public void sortColors(int[] nums) {
    int low = 0;              // everything before low is 0
    int mid = 0;              // current element being processed
    int high = nums.length - 1; // everything after high is 2

    // Invariant:
    //   nums[0..low-1]  = all 0s
    //   nums[low..mid-1] = all 1s
    //   nums[mid..high]  = unknown (to be processed)
    //   nums[high+1..n-1] = all 2s

    while (mid <= high) {
        if (nums[mid] == 0) {
            // Current element is 0: swap with low boundary, advance both low and mid
            swap(nums, low, mid);
            low++;
            mid++;
        } else if (nums[mid] == 1) {
            // Current element is 1: it's in the right region, just advance mid
            mid++;
        } else { // nums[mid] == 2
            // Current element is 2: swap with high boundary, shrink high
            // Don't advance mid — the swapped element hasn't been processed yet
            swap(nums, mid, high);
            high--;
        }
    }
}

private void swap(int[] nums, int i, int j) {
    int temp = nums[i];
    nums[i] = nums[j];
    nums[j] = temp;
}
```

**Dry Run** on `nums = [2, 0, 2, 1, 1, 0]`:

```
Initial: low=0, mid=0, high=5
Array: [2, 0, 2, 1, 1, 0]

mid=0, nums[0]=2: swap(0,5) → [0, 0, 2, 1, 1, 2], high=4
mid=0, nums[0]=0: swap(0,0) → [0, 0, 2, 1, 1, 2], low=1, mid=1
mid=1, nums[1]=0: swap(1,1) → [0, 0, 2, 1, 1, 2], low=2, mid=2
mid=2, nums[2]=2: swap(2,4) → [0, 0, 1, 1, 2, 2], high=3
mid=2, nums[2]=1: mid=3
mid=3, nums[3]=1: mid=4
mid=4 > high=3, exit loop

Result: [0, 0, 1, 1, 2, 2]
```

**Time:** O(n) — each element is processed at most once.
**Space:** O(1).

**Key insight:** When you swap with `high`, you don't advance `mid` because the element that came from `high` hasn't been examined yet. When you swap with `low`, you can advance `mid` because `low` always points to a 1 (or the same position as `mid`), so the swapped element is already processed.

---

#### LC 344 — Reverse String (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Apple

**Problem:** Reverse a character array in-place.

```java
public void reverseString(char[] s) {
    int left = 0;
    int right = s.length - 1;

    while (left < right) {
        char temp = s[left];
        s[left] = s[right];
        s[right] = temp;
        left++;
        right--;
    }
}
```

**Time:** O(n).
**Space:** O(1).

**Key insight:** This is the simplest opposite-direction two-pointer problem. It's also the foundation for in-place reversal used in problems like "rotate array" and "reverse words in a string."

---

### Category C: Two Arrays — Merge/Intersection

---

#### LC 88 — Merge Sorted Array (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given two sorted arrays `nums1` (with extra space at the end) and `nums2`, merge `nums2` into `nums1` in-place. `nums1` has length `m + n` where the first `m` elements are valid and the last `n` are zeros.

**The key insight — merge from the END:** If you merge from the front, you'd need to shift elements in `nums1` to make room, which is O(n) per insertion. Instead, merge from the back: the largest element goes to position `m+n-1`, then `m+n-2`, and so on. No shifting needed.

```java
public void merge(int[] nums1, int m, int[] nums2, int n) {
    int i = m - 1;         // pointer to last valid element in nums1
    int j = n - 1;         // pointer to last element in nums2
    int k = m + n - 1;     // pointer to last position in nums1 (fill from here)

    // Merge from the back: always place the larger element at position k
    while (i >= 0 && j >= 0) {
        if (nums1[i] >= nums2[j]) {
            nums1[k--] = nums1[i--];
        } else {
            nums1[k--] = nums2[j--];
        }
    }

    // If nums2 still has elements, copy them (nums1 elements are already in place)
    // If nums1 still has elements, they're already in the right positions
    while (j >= 0) {
        nums1[k--] = nums2[j--];
    }
    // No need to handle remaining nums1 elements — they're already in nums1
}
```

**Time:** O(m + n).
**Space:** O(1).

**Key insight:** Merging from the back eliminates the need for shifting. The "remaining nums1 elements" case doesn't need handling because those elements are already in their correct positions in `nums1`.

---

#### LC 349 — Intersection of Two Arrays (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Adobe (20 companies)

**Problem:** Given two arrays, return their intersection (unique elements that appear in both).

**Two approaches:**

**Approach 1: Sort + Two Pointers (O(n log n) time, O(1) space)**

```java
public int[] intersection(int[] nums1, int[] nums2) {
    Arrays.sort(nums1);
    Arrays.sort(nums2);

    List<Integer> result = new ArrayList<>();
    int i = 0, j = 0;

    while (i < nums1.length && j < nums2.length) {
        if (nums1[i] == nums2[j]) {
            // Found a common element
            // Add only if it's not a duplicate in the result
            if (result.isEmpty() || result.get(result.size() - 1) != nums1[i]) {
                result.add(nums1[i]);
            }
            i++;
            j++;
        } else if (nums1[i] < nums2[j]) {
            i++; // nums1's element is smaller, advance it
        } else {
            j++; // nums2's element is smaller, advance it
        }
    }

    return result.stream().mapToInt(Integer::intValue).toArray();
}
```

**Approach 2: HashSet (O(n) time, O(n) space) — often preferred in interviews**

```java
public int[] intersectionHashSet(int[] nums1, int[] nums2) {
    Set<Integer> set1 = new HashSet<>();
    for (int n : nums1) set1.add(n);

    Set<Integer> result = new HashSet<>();
    for (int n : nums2) {
        if (set1.contains(n)) result.add(n);
    }

    return result.stream().mapToInt(Integer::intValue).toArray();
}
```

**When to use which:** If the arrays are already sorted or you need O(1) space, use two pointers. If the arrays are unsorted and you can afford O(n) space, HashSet is simpler and faster.

**Time:** O(n log n) for sort + two pointers, O(n) for HashSet.
**Space:** O(1) for two pointers (excluding output), O(n) for HashSet.

---

#### LC 350 — Intersection of Two Arrays II (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Same as LC 349, but return each element as many times as it appears in both arrays (count-based intersection).

```java
public int[] intersect(int[] nums1, int[] nums2) {
    Arrays.sort(nums1);
    Arrays.sort(nums2);

    List<Integer> result = new ArrayList<>();
    int i = 0, j = 0;

    while (i < nums1.length && j < nums2.length) {
        if (nums1[i] == nums2[j]) {
            result.add(nums1[i]); // add every occurrence, not just unique ones
            i++;
            j++;
        } else if (nums1[i] < nums2[j]) {
            i++;
        } else {
            j++;
        }
    }

    return result.stream().mapToInt(Integer::intValue).toArray();
}
```

**Follow-up questions (common in interviews):**
1. What if the arrays are already sorted? → Skip the sort, O(n+m) total.
2. What if nums1 is much smaller than nums2? → Sort nums1, binary search each element of nums1 in nums2. O(m log m + m log n).
3. What if nums2 doesn't fit in memory? → Stream nums2 from disk, use a HashMap of nums1 counts.

**Time:** O(n log n + m log m).
**Space:** O(min(n, m)) for the result.

---

#### LC 986 — Interval List Intersections (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, DoorDash, Uber (13 companies)

**Problem:** Given two lists of closed intervals `firstList` and `secondList`, each sorted by start time, return their intersection.

**Why two pointers fit:** Both lists are sorted. Use one pointer per list. At each step, compute the intersection of the current pair of intervals (if any), then advance the pointer whose interval ends first.

```java
public int[][] intervalIntersection(int[][] firstList, int[][] secondList) {
    List<int[]> result = new ArrayList<>();
    int i = 0, j = 0;

    while (i < firstList.length && j < secondList.length) {
        // Compute the intersection of firstList[i] and secondList[j]
        int lo = Math.max(firstList[i][0], secondList[j][0]); // intersection start
        int hi = Math.min(firstList[i][1], secondList[j][1]); // intersection end

        if (lo <= hi) {
            // Valid intersection exists
            result.add(new int[]{lo, hi});
        }

        // Advance the pointer whose interval ends first
        // The interval that ends first cannot intersect with any future interval
        // from the other list (since future intervals start later)
        if (firstList[i][1] < secondList[j][1]) {
            i++;
        } else {
            j++;
        }
    }

    return result.toArray(new int[0][]);
}
```

**Time:** O(n + m) where n and m are the lengths of the two lists.
**Space:** O(n + m) for the result.

**Key insight:** The interval that ends first is "used up" — it can't intersect with any future interval from the other list (since those start later). So we advance its pointer. This is the same logic as the merge two sorted arrays pattern.

---

### Category D: Fix One + Sweep — k-Sum Problems

---

#### LC 15 — 3Sum (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe, TikTok, Goldman Sachs

**Problem:** Given an integer array `nums`, return all unique triplets `[nums[i], nums[j], nums[k]]` such that `i != j != k` and `nums[i] + nums[j] + nums[k] == 0`.

**Why this is the canonical k-Sum problem:** It introduces all the key ideas: sort first, fix one element, use two pointers on the rest, and handle duplicates carefully.

**The duplicate handling is the hardest part.** There are three places where duplicates can appear:
1. The fixed element `nums[i]` — skip if `nums[i] == nums[i-1]`
2. The left pointer after finding a valid triplet — skip while `nums[left] == nums[left+1]`
3. The right pointer after finding a valid triplet — skip while `nums[right] == nums[right-1]`

```java
public List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();

    for (int i = 0; i < nums.length - 2; i++) {
        // Skip duplicate values for the fixed element
        // (i > 0 guard prevents out-of-bounds on the first iteration)
        if (i > 0 && nums[i] == nums[i - 1]) continue;

        // Optimization: if the smallest possible triplet is positive, stop
        // (nums[i] is the smallest since array is sorted; if it's > 0, all triplets > 0)
        if (nums[i] > 0) break;

        int left = i + 1;
        int right = nums.length - 1;
        int target = -nums[i]; // we need nums[left] + nums[right] == -nums[i]

        while (left < right) {
            int sum = nums[left] + nums[right];

            if (sum == target) {
                result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                // Skip duplicates for left pointer
                // Move left past all elements equal to the one we just used
                while (left < right && nums[left] == nums[left + 1]) left++;
                // Skip duplicates for right pointer
                while (left < right && nums[right] == nums[right - 1]) right--;

                // Move both pointers inward after handling duplicates
                left++;
                right--;
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
    }

    return result;
}
```

**Dry Run** on `nums = [-4, -2, -2, -2, 0, 1, 2, 2, 3]` (after sorting):

```
Sorted: [-4, -2, -2, -2, 0, 1, 2, 2, 3]
         0    1    2    3  4  5  6  7  8

i=0, nums[0]=-4, target=4, left=1, right=8
  sum = -2+3 = 1 < 4 → left++
  sum = -2+3 = 1 < 4 → left++
  sum = -2+3 = 1 < 4 → left++
  sum = 0+3 = 3 < 4 → left++
  sum = 1+3 = 4 == 4 → add [-4,1,3]
    skip left dups: nums[5]=1, nums[6]=2, no dup
    skip right dups: nums[8]=3, nums[7]=2, no dup
    left=6, right=7
  sum = 2+2 = 4 == 4 → add [-4,2,2]
    skip left dups: nums[6]=2, nums[7]=2, dup! left++
    left=7, right=7 → left >= right, exit inner loop

i=1, nums[1]=-2, target=2, left=2, right=8
  sum = -2+3 = 1 < 2 → left++
  sum = -2+3 = 1 < 2 → left++
  sum = 0+3 = 3 > 2 → right--
  sum = 0+2 = 2 == 2 → add [-2,0,2]
    skip left dups: nums[4]=0, nums[5]=1, no dup
    skip right dups: nums[7]=2, nums[6]=2, dup! right--
    left=5, right=5 → left >= right, exit inner loop

i=2, nums[2]=-2 == nums[1]=-2 → skip (duplicate)
i=3, nums[3]=-2 == nums[2]=-2 → skip (duplicate)

i=4, nums[4]=0, target=0, left=5, right=8
  sum = 1+3 = 4 > 0 → right--
  sum = 1+2 = 3 > 0 → right--
  sum = 1+2 = 3 > 0 → right--
  left=5, right=5 → left >= right, exit inner loop

i=5, nums[5]=1 > 0 → break (early termination)

Result: [[-4,1,3], [-4,2,2], [-2,0,2]]
```

**Time:** O(n^2) — O(n log n) sort + O(n^2) for the nested loops.
**Space:** O(1) excluding output.

**Key insight:** The duplicate skipping is what makes this problem hard. The pattern `while (left < right && nums[left] == nums[left+1]) left++` skips all but the last occurrence of a duplicate value, then `left++` moves past it. This ensures each unique value is used exactly once per triplet.

---

#### LC 16 — 3Sum Closest (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given an integer array `nums` and an integer `target`, find three integers whose sum is closest to `target`. Return the sum.

**Why two pointers fit:** Same structure as 3Sum. Sort, fix one, sweep with two pointers. Instead of checking for equality, track the minimum absolute difference.

```java
public int threeSumClosest(int[] nums, int target) {
    Arrays.sort(nums);
    int closest = nums[0] + nums[1] + nums[2]; // initialize with first triplet

    for (int i = 0; i < nums.length - 2; i++) {
        // Skip duplicates for the fixed element (optional optimization)
        if (i > 0 && nums[i] == nums[i - 1]) continue;

        int left = i + 1;
        int right = nums.length - 1;

        while (left < right) {
            int sum = nums[i] + nums[left] + nums[right];

            // Update closest if this sum is nearer to target
            if (Math.abs(sum - target) < Math.abs(closest - target)) {
                closest = sum;
            }

            if (sum == target) {
                return sum; // can't get closer than exact match
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
    }

    return closest;
}
```

**Time:** O(n^2).
**Space:** O(1).

**Key insight:** No duplicate handling needed for the output (we return a single sum, not a list of triplets). The early return on exact match is a useful optimization.

---

#### LC 18 — 4Sum (Medium)

**Companies:** Amazon, Google, Bloomberg, Microsoft

**Problem:** Given an integer array `nums` and an integer `target`, return all unique quadruplets that sum to `target`.

**Why two pointers fit:** Extend the 3Sum pattern by adding one more outer loop. Two outer loops fix two elements; two pointers find the remaining pair.

```java
public List<List<Integer>> fourSum(int[] nums, int target) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    int n = nums.length;

    for (int i = 0; i < n - 3; i++) {
        // Skip duplicates for first fixed element
        if (i > 0 && nums[i] == nums[i - 1]) continue;

        // Pruning: smallest possible sum with this i is too large
        if ((long) nums[i] + nums[i+1] + nums[i+2] + nums[i+3] > target) break;
        // Pruning: largest possible sum with this i is too small
        if ((long) nums[i] + nums[n-3] + nums[n-2] + nums[n-1] < target) continue;

        for (int j = i + 1; j < n - 2; j++) {
            // Skip duplicates for second fixed element
            if (j > i + 1 && nums[j] == nums[j - 1]) continue;

            // Pruning for inner loop
            if ((long) nums[i] + nums[j] + nums[j+1] + nums[j+2] > target) break;
            if ((long) nums[i] + nums[j] + nums[n-2] + nums[n-1] < target) continue;

            int left = j + 1;
            int right = n - 1;
            long remain = (long) target - nums[i] - nums[j];

            while (left < right) {
                long sum = nums[left] + nums[right];

                if (sum == remain) {
                    result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;
                    left++;
                    right--;
                } else if (sum < remain) {
                    left++;
                } else {
                    right--;
                }
            }
        }
    }

    return result;
}
```

**Time:** O(n^3) — two outer loops O(n^2) × inner two-pointer O(n).
**Space:** O(1) excluding output.

**Key insight:** Use `long` for the sum to avoid integer overflow (the problem allows values up to 10^9, and four of them can overflow int). The pruning conditions significantly speed up the solution in practice.

---

#### LC 611 — Valid Triangle Number (Medium)

**Companies:** Amazon, Bloomberg, Google, Microsoft

**Problem:** Given an integer array `nums`, return the number of triplets that can form a valid triangle (sum of any two sides > third side).

**The key observation:** For a sorted triplet `a <= b <= c`, the only condition that can fail is `a + b > c` (the other two conditions `a + c > b` and `b + c > a` are automatically satisfied when `c` is the largest). So we only need to check `nums[i] + nums[j] > nums[k]`.

**Why two pointers fit (with a twist):** Sort the array. Fix the largest element `k` (rightmost). Use two pointers `i` and `j` (where `i < j < k`) to count pairs where `nums[i] + nums[j] > nums[k]`.

```java
public int triangleNumber(int[] nums) {
    Arrays.sort(nums);
    int count = 0;
    int n = nums.length;

    // Fix the largest side (k), iterate from right to left
    for (int k = n - 1; k >= 2; k--) {
        int left = 0;
        int right = k - 1;

        while (left < right) {
            if (nums[left] + nums[right] > nums[k]) {
                // nums[left] + nums[right] > nums[k]
                // Since array is sorted, nums[left+1] + nums[right] > nums[k] too,
                // nums[left+2] + nums[right] > nums[k] too, ..., up to nums[right-1]
                // So ALL pairs (left, right), (left+1, right), ..., (right-1, right) are valid
                count += right - left;
                right--; // try a smaller right to find more pairs
            } else {
                left++; // sum too small, need a larger left
            }
        }
    }

    return count;
}
```

**Time:** O(n^2) — outer loop O(n) × inner two-pointer O(n).
**Space:** O(1).

**Key insight:** When `nums[left] + nums[right] > nums[k]`, ALL pairs `(left, right), (left+1, right), ..., (right-1, right)` are valid (since `nums[i] >= nums[left]` for `i > left`). So we can add `right - left` to the count in one step, then move `right` left.

---

### Category E: Advanced / Hard

---

#### LC 42 — Trapping Rain Water (Hard)

**Companies:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, Uber, Deutsche Bank

**Problem:** Given an array `height` representing an elevation map, compute how much water it can trap after raining.

**The core insight:** The water level at any position `i` is determined by `min(maxLeft[i], maxRight[i]) - height[i]`, where `maxLeft[i]` is the maximum height to the left of `i` (inclusive) and `maxRight[i]` is the maximum height to the right of `i` (inclusive). If this value is negative, no water is trapped at `i`.

**Three approaches:**

**Approach 1: Precompute maxLeft and maxRight arrays (O(n) time, O(n) space)**

```java
public int trapPrecompute(int[] height) {
    int n = height.length;
    int[] maxLeft = new int[n];
    int[] maxRight = new int[n];

    maxLeft[0] = height[0];
    for (int i = 1; i < n; i++) {
        maxLeft[i] = Math.max(maxLeft[i - 1], height[i]);
    }

    maxRight[n - 1] = height[n - 1];
    for (int i = n - 2; i >= 0; i--) {
        maxRight[i] = Math.max(maxRight[i + 1], height[i]);
    }

    int water = 0;
    for (int i = 0; i < n; i++) {
        water += Math.min(maxLeft[i], maxRight[i]) - height[i];
    }

    return water;
}
```

**Approach 2: Two Pointers (O(n) time, O(1) space) — the optimal solution**

The key insight for the two-pointer approach: we don't need to know both `maxLeft` and `maxRight` exactly. We only need the minimum of the two. If `maxLeft < maxRight`, then the water at the left pointer is determined by `maxLeft` (regardless of what's to the right, since `maxRight >= maxLeft`). So we can process the left pointer. Symmetric logic applies when `maxRight < maxLeft`.

```java
public int trap(int[] height) {
    int left = 0;
    int right = height.length - 1;
    int maxLeft = 0;  // max height seen so far from the left
    int maxRight = 0; // max height seen so far from the right
    int water = 0;

    while (left < right) {
        if (height[left] < height[right]) {
            // The limiting factor for the left position is maxLeft
            // (we know maxRight >= height[right] > height[left], so maxRight >= maxLeft
            //  is guaranteed — the right side won't be the bottleneck)
            if (height[left] >= maxLeft) {
                maxLeft = height[left]; // update running max from left
            } else {
                water += maxLeft - height[left]; // water trapped at this position
            }
            left++;
        } else {
            // Symmetric: the limiting factor for the right position is maxRight
            if (height[right] >= maxRight) {
                maxRight = height[right];
            } else {
                water += maxRight - height[right];
            }
            right--;
        }
    }

    return water;
}
```

**Dry Run** on `height = [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]`:

```
Initial: left=0, right=11, maxLeft=0, maxRight=0, water=0

height[0]=0 < height[11]=1:
  height[0]=0 >= maxLeft=0 → maxLeft=0. left=1

height[1]=1 >= height[11]=1 (equal, go to else):
  height[11]=1 >= maxRight=0 → maxRight=1. right=10

height[1]=1 < height[10]=2:
  height[1]=1 >= maxLeft=0 → maxLeft=1. left=2

height[2]=0 < height[10]=2:
  height[2]=0 < maxLeft=1 → water += 1-0 = 1. water=1. left=3

height[3]=2 >= height[10]=2 (equal, go to else):
  height[10]=2 >= maxRight=1 → maxRight=2. right=9

height[3]=2 >= height[9]=1:
  height[9]=1 < maxRight=2 → water += 2-1 = 1. water=2. right=8

height[3]=2 >= height[8]=2 (equal, go to else):
  height[8]=2 >= maxRight=2 → maxRight=2. right=7

height[3]=2 < height[7]=3:
  height[3]=2 >= maxLeft=1 → maxLeft=2. left=4

height[4]=1 < height[7]=3:
  height[4]=1 < maxLeft=2 → water += 2-1 = 1. water=3. left=5

height[5]=0 < height[7]=3:
  height[5]=0 < maxLeft=2 → water += 2-0 = 2. water=5. left=6

height[6]=1 < height[7]=3:
  height[6]=1 < maxLeft=2 → water += 2-1 = 1. water=6. left=7

left=7 == right=7, exit loop

Answer: 6
```

**Time:** O(n).
**Space:** O(1).

**Key insight:** The two-pointer approach works because we only need `min(maxLeft, maxRight)` to compute the water at any position. When `height[left] < height[right]`, we know `maxRight >= height[right] > height[left]`, so `maxRight >= maxLeft` is guaranteed (or will be once we update maxLeft). The left side is the bottleneck, so we process it.

---

#### LC 259 — 3Sum Smaller (Medium, Premium)

**Companies:** Amazon, Google, Meta

**Problem:** Given an array `nums` and a target, count the number of triplets `(i, j, k)` with `i < j < k` such that `nums[i] + nums[j] + nums[k] < target`.

**Why this is different from 3Sum:** We're counting pairs, not finding exact matches. When `nums[left] + nums[right] < target - nums[i]`, ALL pairs `(left, left+1), (left, left+2), ..., (left, right)` also satisfy the condition (since `nums[k] <= nums[right]` for `k <= right`). So we can count `right - left` pairs in one step.

```java
public int threeSumSmaller(int[] nums, int target) {
    Arrays.sort(nums);
    int count = 0;

    for (int i = 0; i < nums.length - 2; i++) {
        int left = i + 1;
        int right = nums.length - 1;
        int remain = target - nums[i];

        while (left < right) {
            if (nums[left] + nums[right] < remain) {
                // All pairs (left, left+1), (left, left+2), ..., (left, right) are valid
                // because nums[k] <= nums[right] for k in [left+1, right]
                count += right - left;
                left++; // try a larger left to find more pairs
            } else {
                right--; // sum too large, reduce right
            }
        }
    }

    return count;
}
```

**Time:** O(n^2).
**Space:** O(1).

**Key insight:** The counting trick `count += right - left` is the key difference from 3Sum. When the sum is less than the target, all pairs with the same left but smaller right also satisfy the condition.

---

#### LC 838 — Push Dominoes (Medium)

**Companies:** Not in the provided list, but commonly asked.

**Problem:** Given a string of dominoes (`L`, `R`, `.`), simulate the falling. `R` pushes right, `L` pushes left, `.` is standing. Return the final state.

**Why two pointers fit:** Process the string in segments between `L` and `R` forces. Use two pointers to track the boundaries of each segment and fill in the dots based on the forces on each side.

```java
public String pushDominoes(String dominoes) {
    // Add sentinel values to simplify boundary handling
    String s = "L" + dominoes + "R";
    StringBuilder result = new StringBuilder();
    int left = 0; // pointer to the last 'L' or 'R' force

    for (int right = 1; right < s.length(); right++) {
        if (s.charAt(right) == '.') continue; // skip dots, keep scanning

        // Process the segment between s[left] and s[right]
        int gap = right - left - 1; // number of dots between the two forces

        if (left > 0) {
            result.append(s.charAt(left)); // append the left force character
        }

        if (s.charAt(left) == s.charAt(right)) {
            // Same force on both sides: all dots fall in that direction
            for (int i = 0; i < gap; i++) result.append(s.charAt(left));
        } else if (s.charAt(left) == 'L' && s.charAt(right) == 'R') {
            // L...R: forces push away from each other, dots stay standing
            for (int i = 0; i < gap; i++) result.append('.');
        } else {
            // R...L: forces push toward each other, meet in the middle
            // Left half falls right, right half falls left, middle stays if odd gap
            for (int i = 0; i < gap / 2; i++) result.append('R');
            if (gap % 2 == 1) result.append('.'); // middle dot stays standing
            for (int i = 0; i < gap / 2; i++) result.append('L');
        }

        left = right; // move left pointer to current force
    }

    return result.toString();
}
```

**Time:** O(n).
**Space:** O(n) for the result.

**Key insight:** By adding sentinel `L` at the start and `R` at the end, every segment of dots is bounded by a force on each side. The four cases (LL, RR, LR, RL) cover all possibilities.

---

## 7. Common Mistakes and Edge Cases

### Common Mistakes

| Mistake | Why it happens | How to fix |
|---|---|---|
| Using opposite-direction two pointers on unsorted data | Forgetting that the invariant requires sorted order | Always sort first, or use HashMap for unsorted pair sum |
| Not handling duplicates in 3Sum | The two-pointer loop finds the same triplet multiple times | Skip identical values for all three pointers after finding a match |
| Moving both pointers when only one should move | Confusing the merge pattern with the converging pattern | In merge: advance the pointer with the smaller element. In converging: advance based on sum comparison |
| Off-by-one in merge (forgetting remaining elements) | Assuming both arrays exhaust at the same time | Always drain the remaining elements with `while (i < A.length)` and `while (j < B.length)` |
| Advancing `mid` after swapping with `high` in Dutch National Flag | The swapped element from `high` hasn't been examined yet | Only advance `mid` when `nums[mid] == 0` (swap with `low`) or `nums[mid] == 1` (no swap) |
| Integer overflow in 4Sum | `nums[i] + nums[j] + nums[k] + nums[l]` can exceed `int` range | Cast to `long` before adding |
| Infinite loop in 3Sum duplicate skipping | Writing `while (nums[left] == nums[left+1]) left++` without the `left < right` guard | Always include `left < right` in the inner while conditions |
| Not initializing `maxLeft`/`maxRight` correctly in Trapping Rain Water | Starting with 0 when the first element might be larger | Initialize with `height[0]` and `height[n-1]`, or update before computing water |

### Edge Cases to Always Test

**Empty array:**
```java
// Most two-pointer solutions handle this naturally since the while loop
// condition (left < right) is false immediately for length 0 or 1.
// But check: does your solution access arr[0] before the loop?
```

**Single element:**
```java
// For pair problems: no valid pair exists, return appropriate default.
// For in-place problems: the single element is already "processed."
```

**All elements identical:**
```java
// 3Sum: [-2,-2,-2] with target 0 → no valid triplet (sum = -6)
// Remove Duplicates: [1,1,1,1] → result is [1], count = 1
// Container With Most Water: [5,5,5,5] → area = 5 * (n-1)
```

**Already sorted / reverse sorted:**
```java
// Opposite direction: works correctly in both cases.
// Read/Write: works correctly (no elements to move).
```

**Negative numbers mixed with positive:**
```java
// 3Sum: [-4,-1,-1,0,1,2] → the early termination (nums[i] > 0) still works
//   because if nums[i] > 0 and the array is sorted, all remaining elements
//   are also > 0, so no triplet can sum to 0.
// Trapping Rain Water: heights are non-negative by definition.
```

**No valid answer:**
```java
// Two Sum II: problem guarantees a solution, but in general, return [-1,-1].
// 3Sum: return empty list.
// Container With Most Water: always has an answer (at least two elements).
```

**Array of size 2 (minimum for pair):**
```java
// Opposite direction: left=0, right=1. One iteration, then left >= right.
// Make sure your loop condition handles this correctly.
```

---

## 8. Pattern Comparison — Two Pointers vs Sliding Window vs HashMap

This section connects Topic 2 (Two Pointers) with Topic 1 (Sliding Window). These patterns are often confused because both use two index variables moving through an array. The distinction matters.

### Two Pointers vs Sliding Window

| Aspect | Two Pointers (Opposite) | Sliding Window | Two Pointers (Same Dir) |
|---|---|---|---|
| Direction | Converge inward | Both move right | Both move right |
| What they track | A pair of elements | Contiguous window content | Read vs write positions |
| Sorted requirement | Usually yes | Usually no | Depends |
| Use case | Pair sums, palindromes | Subarray/substring optimization | In-place manipulation |
| Window shrinks? | Yes (from both ends) | Yes (left pointer advances) | No (write never goes back) |
| Key invariant | Sorted order enables elimination | Window validity condition | write <= read |

**The core difference:** Sliding window maintains a contiguous subarray and optimizes over its content (sum, count, frequency). Two pointers (opposite direction) finds a pair of elements satisfying a condition, using sorted order to eliminate candidates. Two pointers (same direction) compacts an array in-place.

**When to use Sliding Window (from Topic 1):**
- "Longest subarray/substring with property X"
- "Minimum window containing all characters"
- "Maximum sum of subarray of size k"
- The answer is a contiguous subarray or substring

**When to use Two Pointers (Opposite Direction):**
- "Find pair in sorted array with sum = target"
- "Palindrome check"
- "Container with most water"
- The answer is a pair of elements (not a subarray)

**The overlap:** Some problems can be solved with either. For example, "count subarrays with sum = k" can use sliding window (for non-negative arrays) or prefix sums + HashMap. The choice depends on constraints.

### Two Pointers vs HashMap (for Pair Sum)

| Aspect | Two Pointers | HashMap |
|---|---|---|
| Sorted requirement | Yes | No |
| Time complexity | O(n) after O(n log n) sort | O(n) |
| Space complexity | O(1) | O(n) |
| Preserves indices | No (sorting changes positions) | Yes |
| Use when | Array is already sorted, or O(1) space required | Array is unsorted, or original indices needed |

**The decision rule:** If the problem gives you a sorted array and asks for O(1) space, use two pointers. If the array is unsorted and you need original indices (like Two Sum I), use a HashMap.

### Two Pointers vs Binary Search (for Pair in Sorted Array)

| Aspect | Two Pointers | Binary Search |
|---|---|---|
| Time complexity | O(n) total | O(n log n) total (n binary searches) |
| Use when | Finding one pair | Finding all pairs, or when n is small |
| Advantage | Single pass | Can find specific element faster |

For finding a single pair in a sorted array, two pointers is strictly better than binary search (O(n) vs O(n log n)). Binary search is better when you have a fixed element and need to find its complement quickly.

### Two Pointers vs Stack (for Trapping Rain Water)

| Aspect | Two Pointers | Stack |
|---|---|---|
| Time complexity | O(n) | O(n) |
| Space complexity | O(1) | O(n) |
| Intuition | Running max from both sides | Process each "valley" as you encounter it |
| Code complexity | Simpler | More complex |

Both solve Trapping Rain Water in O(n) time. The two-pointer approach is preferred in interviews because it uses O(1) space and the code is cleaner. The stack approach is useful if you need to process the water level incrementally (e.g., streaming input).

### Decision Flowchart

```
Input is a sorted array or can be sorted?
├── YES: Need to find a pair/triplet summing to target?
│   ├── YES: Two Pointers (Opposite Direction) or Fix+Sweep
│   └── NO: Need to find a single element? → Binary Search
└── NO: Need original indices? → HashMap
    Need in-place modification? → Two Pointers (Read/Write)
    Need contiguous subarray? → Sliding Window
    Need all combinations? → Backtracking
```

---

## 9. Quick Reference Cheat Sheet

### Sub-Pattern Selector

| Signal in Problem | Sub-Pattern | Template |
|---|---|---|
| Sorted array + find pair + target sum | Opposite Direction | Template 1 |
| Palindrome check | Opposite Direction | Template 1 |
| Container/water/area maximization | Opposite Direction + greedy | Template 1 |
| In-place remove/compact/partition | Read/Write (Same Direction) | Template 2 |
| Remove duplicates (sorted) | Read/Write (Same Direction) | Template 2 |
| Move zeros / partition by condition | Read/Write (Same Direction) | Template 2 |
| Merge two sorted arrays/lists | Two Arrays | Template 4 |
| Intersection of sorted arrays | Two Arrays | Template 4 |
| 3Sum / 4Sum / triplets | Fix One + Sweep | Template 5 |
| Cycle detection / middle of list | Fast/Slow | Template 3 (Topic 3) |
| Trapping rain water | Opposite Direction + running max | Template 1 variant |
| Sort 3 values (Dutch National Flag) | Three Pointers | Template 2 variant |

### Time/Space Complexity Summary

| Problem | Time | Space | Sub-Pattern |
|---|---|---|---|
| LC 167 — Two Sum II | O(n) | O(1) | Opposite Direction |
| LC 11 — Container With Most Water | O(n) | O(1) | Opposite Direction |
| LC 125 — Valid Palindrome | O(n) | O(1) | Opposite Direction |
| LC 680 — Valid Palindrome II | O(n) | O(1) | Opposite Direction |
| LC 881 — Boats to Save People | O(n log n) | O(1) | Opposite Direction |
| LC 977 — Squares of Sorted Array | O(n) | O(n) | Opposite Direction |
| LC 26 — Remove Duplicates | O(n) | O(1) | Read/Write |
| LC 27 — Remove Element | O(n) | O(1) | Read/Write |
| LC 283 — Move Zeroes | O(n) | O(1) | Read/Write |
| LC 80 — Remove Duplicates II | O(n) | O(1) | Read/Write |
| LC 75 — Sort Colors | O(n) | O(1) | Three Pointers |
| LC 344 — Reverse String | O(n) | O(1) | Opposite Direction |
| LC 88 — Merge Sorted Array | O(m+n) | O(1) | Two Arrays |
| LC 349 — Intersection I | O(n log n) | O(1) | Two Arrays |
| LC 350 — Intersection II | O(n log n) | O(1) | Two Arrays |
| LC 986 — Interval Intersections | O(n+m) | O(n+m) | Two Arrays |
| LC 15 — 3Sum | O(n^2) | O(1) | Fix+Sweep |
| LC 16 — 3Sum Closest | O(n^2) | O(1) | Fix+Sweep |
| LC 18 — 4Sum | O(n^3) | O(1) | Fix+Sweep |
| LC 611 — Valid Triangle Number | O(n^2) | O(1) | Fix+Sweep |
| LC 42 — Trapping Rain Water | O(n) | O(1) | Opposite Direction |
| LC 259 — 3Sum Smaller | O(n^2) | O(1) | Fix+Sweep |

### Duplicate Handling in k-Sum Problems

```java
// Pattern for skipping duplicates — memorize this:

// 1. Skip duplicate for fixed element (outer loop):
if (i > 0 && nums[i] == nums[i - 1]) continue;

// 2. Skip duplicates after finding a valid answer (inner two pointers):
while (left < right && nums[left] == nums[left + 1]) left++;
while (left < right && nums[right] == nums[right - 1]) right--;
left++;
right--;

// The inner while loops skip all but the LAST occurrence of a duplicate.
// Then left++ and right-- move past that last occurrence.
// This ensures each unique value is used exactly once per k-tuple.
```

### The "Move Shorter Pointer" Rule for Container Problems

```java
// In Container With Most Water and similar problems:
// Always move the pointer pointing to the SHORTER line.
// Moving the taller line's pointer can never improve the result.
// Proof: area = min(h[left], h[right]) * width
//   If we move the taller pointer: width decreases, height bounded by shorter → area decreases
//   If we move the shorter pointer: width decreases, but height might increase → area might improve
if (height[left] < height[right]) {
    left++;
} else {
    right--;
}
```

---

## 10. Practice Roadmap

### Week 1 — Easy Problems (15 minutes each)

Build the muscle memory for the basic templates. These should feel automatic.

| Problem | Pattern | Goal |
|---|---|---|
| LC 344 — Reverse String | Opposite Direction | Simplest two-pointer |
| LC 125 — Valid Palindrome | Opposite Direction | Alphanumeric filtering |
| LC 977 — Squares of Sorted Array | Opposite Direction | Build result from ends |
| LC 26 — Remove Duplicates | Read/Write | Base read/write template |
| LC 27 — Remove Element | Read/Write | Keep/skip condition |
| LC 283 — Move Zeroes | Read/Write | Two-phase compaction |

After Week 1, you should be able to write Templates 1 and 2 from memory without hesitation.

### Week 2 — Medium Core (25 minutes each)

These are the most frequently asked two-pointer problems in FAANG interviews.

| Problem | Pattern | Goal |
|---|---|---|
| LC 167 — Two Sum II | Opposite Direction | Canonical pair sum |
| LC 11 — Container With Most Water | Opposite Direction | Greedy pointer movement |
| LC 88 — Merge Sorted Array | Two Arrays | Merge from the end |
| LC 680 — Valid Palindrome II | Opposite Direction | One-deletion variant |
| LC 881 — Boats to Save People | Opposite Direction | Greedy pairing |
| LC 75 — Sort Colors | Three Pointers | Dutch National Flag |

After Week 2, you should be able to explain the greedy insight behind LC 11 and the three-pointer invariant in LC 75.

### Week 3 — Medium-Hard (30 minutes each)

These require combining the templates with additional logic (duplicate handling, counting, interval math).

| Problem | Pattern | Goal |
|---|---|---|
| LC 15 — 3Sum | Fix+Sweep | Duplicate handling |
| LC 16 — 3Sum Closest | Fix+Sweep | Tracking minimum difference |
| LC 611 — Valid Triangle Number | Fix+Sweep | Counting with two pointers |
| LC 80 — Remove Duplicates II | Read/Write | Generalized k-occurrence |
| LC 986 — Interval Intersections | Two Arrays | Interval overlap logic |
| LC 350 — Intersection II | Two Arrays | Count-based intersection |

After Week 3, you should be able to write 3Sum with correct duplicate handling from scratch, and explain why the counting trick in LC 611 works.

### Week 4 — Hard and Advanced (40 minutes each)

These require deeper insight or combining two-pointer with other techniques.

| Problem | Pattern | Goal |
|---|---|---|
| LC 42 — Trapping Rain Water | Opposite Direction | Running max from both sides |
| LC 18 — 4Sum | Fix+Sweep | Nested fix + overflow handling |
| LC 259 — 3Sum Smaller | Fix+Sweep | Counting pairs, not finding exact |

After Week 4, you should be able to derive the two-pointer solution for Trapping Rain Water from first principles, explaining why processing the side with the smaller height is correct.

### Total: 21 problems over 4 weeks

The ordering matters. Each week builds on the previous. Don't jump to Week 3 problems before the Week 1 templates are automatic — the harder problems are just compositions of the simpler ones.

---

**Connection to Topic 1 (Sliding Window):** If you're coming from the Sliding Window document, notice that both patterns use two index variables. The difference is the direction and the invariant. Sliding window always moves both pointers right, maintaining a contiguous window. Two pointers (opposite direction) moves pointers toward each other, maintaining a pair. Two pointers (same direction, read/write) moves both right but at different speeds, maintaining a compacted prefix. When you see a problem involving a sorted array and a pair, reach for two pointers. When you see a problem involving a contiguous subarray or substring, reach for sliding window.
