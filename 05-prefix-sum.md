# Topic 5: Prefix Sum

> Document 5 of 20 in the FAANG DSA Prep series.

**Connection to Topic 1 (Sliding Window):** Sliding Window works for subarray problems with positive numbers. Prefix Sum + HashMap handles the general case including negatives. Know when to pick which. This distinction comes up in almost every prefix sum interview question.

**Top companies asking these problems:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple

---

## Overview

Prefix Sum is a preprocessing technique that trades O(n) space for O(1) range queries. It converts expensive O(n) subarray sum computations into O(1) subtraction operations.

The basic idea: precompute a running total so that any range sum becomes a single subtraction. Instead of looping from index `l` to `r` every time someone asks "what's the sum of this range?", you answer in constant time.

The more powerful extension: **Prefix Sum + HashMap**. This solves "count/find subarrays with sum = k" in O(n), including arrays with negative numbers. Sliding window cannot handle this case. This is the single most important technique in this document.

---

## Table of Contents

1. [Core Concept](#1-core-concept)
2. [ELI5 Explanations](#2-eli5-explanations)
3. [When to Use — Recognition Signals](#3-when-to-use--recognition-signals)
4. [Core Templates in Java](#4-core-templates-in-java)
   - [Template 1: Basic Prefix Sum Array](#template-1-basic-prefix-sum-array)
   - [Template 2: Prefix Sum + HashMap (Count Subarrays with Sum = K)](#template-2-prefix-sum--hashmap-count-subarrays-with-sum--k)
   - [Template 3: Prefix Sum + HashMap (Longest/Shortest Subarray)](#template-3-prefix-sum--hashmap-longestshortest-subarray-with-sum--k)
   - [Template 4: Difference Array (Range Updates)](#template-4-difference-array-range-updates)
   - [Template 5: 2D Prefix Sum](#template-5-2d-prefix-sum)
5. [Real-World Applications](#5-real-world-applications)
6. [Problem Categories and Solutions](#6-problem-categories-and-solutions)
   - [Category A: Basic Prefix Sum](#category-a-basic-prefix-sum)
   - [Category B: Prefix Sum + HashMap (Count Problems)](#category-b-prefix-sum--hashmap--count-problems)
   - [Category C: Difference Array](#category-c-difference-array)
   - [Category D: 2D Prefix Sum](#category-d-2d-prefix-sum)
   - [Category E: Advanced / Hybrid](#category-e-advanced--hybrid)
7. [Common Mistakes and Edge Cases](#7-common-mistakes-and-edge-cases)
8. [Pattern Comparison — Critical Section](#8-pattern-comparison--critical-section)
9. [Quick Reference Cheat Sheet](#9-quick-reference-cheat-sheet)
10. [Practice Roadmap](#10-practice-roadmap)

---

## 1. Core Concept

### What is Prefix Sum?

Given an array `arr` of length `n`, the prefix sum array `prefix` has length `n+1` where:

```
prefix[0] = 0
prefix[i] = arr[0] + arr[1] + ... + arr[i-1]
           = prefix[i-1] + arr[i-1]
```

The `+1` size and the offset are intentional. `prefix[i]` represents the sum of the first `i` elements (indices 0 through i-1). `prefix[0] = 0` represents the empty prefix — the sum of zero elements.

**Range sum query:** To find the sum of `arr[l..r]` (inclusive, 0-indexed):

```
sum(l, r) = prefix[r+1] - prefix[l]
```

One subtraction. No loop. O(1) per query after O(n) preprocessing.

**Why this formula works:**
- `prefix[r+1]` = sum of arr[0..r]
- `prefix[l]` = sum of arr[0..l-1]
- Subtracting removes the part before index `l`, leaving exactly arr[l..r]

### The Key Extension: Prefix Sum + HashMap

This is where prefix sum becomes genuinely powerful for interviews.

**Problem:** Count subarrays with sum exactly equal to `k`.

**Naive approach:** O(n²) — try all pairs (l, r).

**Prefix sum + HashMap approach:** O(n).

The insight: a subarray `arr[l..r]` has sum `k` if and only if:
```
prefix[r+1] - prefix[l] = k
prefix[l] = prefix[r+1] - k
```

So as you compute `prefix[j]` for each position `j`, you need to know how many times the value `prefix[j] - k` has appeared in the prefix sums computed so far. A HashMap stores this frequency.

**Algorithm:**
1. Initialize `map.put(0, 1)` — the empty prefix exists once
2. Maintain a running `prefixSum`
3. For each element: `prefixSum += arr[i]`
4. Add `map.getOrDefault(prefixSum - k, 0)` to the count
5. Update `map.put(prefixSum, map.getOrDefault(prefixSum, 0) + 1)`

**Why `map.put(0, 1)` is non-negotiable:** If you omit it, you miss all subarrays that start at index 0. Concrete example: `arr = [3, 4, 7]`, `k = 3`. The subarray `[3]` (index 0 to 0) has sum 3. When you process index 0, `prefixSum = 3`, and you look up `3 - 3 = 0` in the map. If 0 isn't there, you miss this subarray. The initialization `map.put(0, 1)` represents the fact that the empty prefix (before any element) has sum 0, and it exists exactly once.

### Beyond Sums

**Prefix XOR:** Same idea, but with XOR instead of addition. `xor(l, r) = prefix[r+1] ^ prefix[l]`. Works because XOR is its own inverse.

**Prefix product:** `product(l, r) = prefix[r+1] / prefix[l]`. Requires no zeros in the array for division to work. LC 238 avoids division entirely using left and right passes.

**Prefix count:** Count occurrences of a specific value in a range. Build a prefix count array for each value, or use a HashMap of prefix counts.

**Difference array:** The inverse operation. Instead of querying ranges, you're updating ranges. Store the changes, then prefix-sum them to get the final values.

### 2D Prefix Sum

For a 2D matrix, precompute `prefix[i][j]` = sum of all elements in the rectangle from (0,0) to (i-1, j-1).

**Build formula (inclusion-exclusion):**
```
prefix[i][j] = prefix[i-1][j] + prefix[i][j-1] - prefix[i-1][j-1] + matrix[i-1][j-1]
```

**Query formula** for rectangle from (r1,c1) to (r2,c2):
```
sum = prefix[r2+1][c2+1] - prefix[r1][c2+1] - prefix[r2+1][c1] + prefix[r1][c1]
```

The inclusion-exclusion removes the double-subtracted corner. Draw the four rectangles to verify the signs every time until it's memorized.

---

## 2. ELI5 Explanations

### Basic Prefix Sum

Imagine you're tracking how much money you've saved each day. Your running total each day IS the prefix sum. If you want to know how much you saved between Tuesday and Friday, just subtract Tuesday's running total from Friday's. No need to add up each day individually.

Day 1: saved $3, total = $3
Day 2: saved $5, total = $8
Day 3: saved $2, total = $10
Day 4: saved $7, total = $17

"How much did I save on days 2 and 3?" = total after day 3 minus total after day 1 = $10 - $3 = $7. Correct.

### Prefix Sum + HashMap

You're walking along a number line. You record every position you've been at and how many times you've been there. If you're currently at position 15 and you need to find how many times you've crossed a stretch of exactly 5, you check how many times you were at position 10. That's the HashMap lookup.

The "stretch of exactly 5" is your target sum `k`. Your current position is the current prefix sum. Position 10 is `current_prefix - k`. The HashMap tells you how many times you've been at position 10 before.

### Difference Array

Instead of recording totals, you record changes. "+5 at index 2, -5 at index 7" means "add 5 to everything from index 2 to 6". When you do the prefix sum of these changes, you get the actual values. It's the inverse of prefix sum.

Think of it like a thermostat schedule. You don't write down the temperature for every hour. You write "turn up 5 degrees at 8am, turn down 5 degrees at 6pm". The actual temperature at any hour is the running sum of all those changes.

---

## 3. When to Use — Recognition Signals

### Green Flags (reach for prefix sum)

- "subarray sum equals k" — Template 2 (Prefix Sum + HashMap)
- "count subarrays with sum..." — Template 2
- "range sum query" with multiple queries on the same array — Template 1
- "continuous subarray sum divisible by k" — Template 2 with modulo
- "subarray with equal 0s and 1s" — transform 0 to -1, then "sum = 0" with Template 2/3
- "find pivot index" (left sum = right sum) — Template 1
- "product of array except self" — prefix product + suffix product
- "range addition" / "increment range" / "add to all elements in range" — Template 4 (difference array)
- Negative numbers present in a sum problem — sliding window won't work, use prefix sum + HashMap
- "how many rooms booked on day X" / "occupancy at time T" — difference array

### When NOT to Use

- Single query, no preprocessing needed: just loop once, O(n) is fine
- "Longest subarray with sum <= k" with all positive numbers: use sliding window (Topic 1) — it's simpler and same complexity
- You need the actual elements, not just their sums
- Array is modified between queries: use a segment tree or Binary Indexed Tree (BIT/Fenwick Tree) instead — prefix sum is static
- "Maximum subarray sum": use Kadane's algorithm, not prefix sum

### The Negative Numbers Rule

This is the most important decision point:

- All positive numbers + sum problem: sliding window often works and is simpler
- Any negative numbers + sum problem: you need prefix sum + HashMap

Sliding window relies on the monotonic property: adding elements increases the sum, removing elements decreases it. Negative numbers break this. Prefix sum + HashMap has no such requirement.

---

## 4. Core Templates in Java

### Template 1: Basic Prefix Sum Array

```java
/**
 * Basic Prefix Sum Array
 *
 * Key invariant: prefix[i] = sum of arr[0..i-1]
 *                prefix[0] = 0 (empty prefix)
 *                prefix has size n+1
 *
 * Range query: sum(l, r) = prefix[r+1] - prefix[l]
 *   where l and r are 0-indexed, inclusive
 */
public class BasicPrefixSum {

    private long[] prefix;

    public BasicPrefixSum(int[] arr) {
        int n = arr.length;
        prefix = new long[n + 1];  // size n+1, not n
        prefix[0] = 0;             // empty prefix
        for (int i = 1; i <= n; i++) {
            prefix[i] = prefix[i - 1] + arr[i - 1];
        }
    }

    /**
     * Returns sum of arr[l..r] inclusive, 0-indexed.
     * Time: O(1)
     */
    public long query(int l, int r) {
        return prefix[r + 1] - prefix[l];
    }
}
```

**Execution trace** for `arr = [2, 4, 1, 7, 3]`:

```
i=0: prefix[0] = 0
i=1: prefix[1] = prefix[0] + arr[0] = 0 + 2 = 2
i=2: prefix[2] = prefix[1] + arr[1] = 2 + 4 = 6
i=3: prefix[3] = prefix[2] + arr[2] = 6 + 1 = 7
i=4: prefix[4] = prefix[3] + arr[3] = 7 + 7 = 14
i=5: prefix[5] = prefix[4] + arr[4] = 14 + 3 = 17

prefix = [0, 2, 6, 7, 14, 17]

query(1, 3) = prefix[4] - prefix[1] = 14 - 2 = 12
Verify: arr[1] + arr[2] + arr[3] = 4 + 1 + 7 = 12. Correct.

query(0, 4) = prefix[5] - prefix[0] = 17 - 0 = 17
Verify: 2 + 4 + 1 + 7 + 3 = 17. Correct.
```

**Common pitfalls:**
- Using `prefix[r] - prefix[l-1]` instead of `prefix[r+1] - prefix[l]` — both work but the `n+1` size version is cleaner and avoids negative index when `l=0`
- Forgetting `prefix[0] = 0` — Java initializes arrays to 0, but be explicit
- Using `int` when values can overflow — use `long` for safety

---

### Template 2: Prefix Sum + HashMap (Count Subarrays with Sum = K)

This is the most important template in this document. Memorize it completely.

```java
/**
 * Count subarrays with sum exactly equal to k.
 *
 * Key invariant: map stores (prefix_sum -> frequency)
 *                map.put(0, 1) ALWAYS initialized before the loop
 *
 * For each position j:
 *   - We want to count how many indices i exist such that
 *     prefix[j+1] - prefix[i] = k
 *     i.e., prefix[i] = prefix[j+1] - k
 *   - The map tells us how many times (prefix[j+1] - k) has appeared
 *
 * Time: O(n)
 * Space: O(n)
 */
public int countSubarraysWithSumK(int[] arr, int k) {
    Map<Long, Integer> map = new HashMap<>();
    map.put(0L, 1);  // CRITICAL: empty prefix has sum 0, appears once

    long prefixSum = 0;
    int count = 0;

    for (int i = 0; i < arr.length; i++) {
        prefixSum += arr[i];

        // How many previous prefix sums equal (prefixSum - k)?
        // Each such prefix sum corresponds to a valid subarray ending at i.
        count += map.getOrDefault(prefixSum - k, 0);

        // Record this prefix sum for future iterations
        map.put(prefixSum, map.getOrDefault(prefixSum, 0) + 1);
    }

    return count;
}
```

**Why `map.put(0, 1)` is required — concrete example:**

```
arr = [3, 4, 7], k = 3

Without map.put(0, 1):
  i=0: prefixSum=3, look up (3-3)=0 in map -> not found, count=0
       map: {3:1}
  i=1: prefixSum=7, look up (7-3)=4 in map -> not found, count=0
       map: {3:1, 7:1}
  i=2: prefixSum=14, look up (14-3)=11 in map -> not found, count=0
  Result: 0. WRONG. The subarray [3] (index 0) has sum 3.

With map.put(0, 1):
  map starts: {0:1}
  i=0: prefixSum=3, look up (3-3)=0 in map -> found 1 time, count=1
       map: {0:1, 3:1}
  i=1: prefixSum=7, look up (7-3)=4 in map -> not found, count=1
       map: {0:1, 3:1, 7:1}
  i=2: prefixSum=14, look up (14-3)=11 in map -> not found, count=1
  Result: 1. CORRECT.
```

The subarray `[3]` starts at index 0. For it to be counted, we need `prefix[0] = 0` to be in the map. That's exactly what `map.put(0, 1)` provides.

**Execution trace** for `arr = [1, 1, 1]`, `k = 2`:

```
map = {0:1}
prefixSum = 0, count = 0

i=0: arr[0]=1
  prefixSum = 0 + 1 = 1
  look up (1 - 2) = -1 in map -> not found
  count = 0
  map = {0:1, 1:1}

i=1: arr[1]=1
  prefixSum = 1 + 1 = 2
  look up (2 - 2) = 0 in map -> found 1 time
  count = 0 + 1 = 1
  map = {0:1, 1:1, 2:1}

i=2: arr[2]=1
  prefixSum = 2 + 1 = 3
  look up (3 - 2) = 1 in map -> found 1 time
  count = 1 + 1 = 2
  map = {0:1, 1:1, 2:1, 3:1}

Result: 2
Verify: subarrays with sum 2 are [1,1] at indices (0,1) and [1,1] at indices (1,2). Correct.
```

**Common pitfalls:**
- Forgetting `map.put(0, 1)` — misses subarrays starting at index 0
- Updating the map BEFORE checking for `prefixSum - k` — this would count the current element as part of a zero-length subarray
- Using `int` for `prefixSum` when values can overflow

---

### Template 3: Prefix Sum + HashMap (Longest/Shortest Subarray with Sum = K)

```java
/**
 * Find the LONGEST subarray with sum exactly equal to k.
 *
 * Key difference from Template 2:
 *   - Map stores (prefix_sum -> FIRST index where this sum appeared)
 *   - We want the earliest occurrence to maximize length
 *   - We do NOT update the map if the prefix sum already exists
 *     (we want the first occurrence, not the latest)
 *
 * For SHORTEST subarray: store LATEST index, update map every time.
 *
 * Time: O(n)
 * Space: O(n)
 */
public int longestSubarrayWithSumK(int[] arr, int k) {
    Map<Long, Integer> map = new HashMap<>();
    map.put(0L, -1);  // empty prefix at index -1 (before the array starts)

    long prefixSum = 0;
    int maxLen = 0;

    for (int i = 0; i < arr.length; i++) {
        prefixSum += arr[i];

        if (map.containsKey(prefixSum - k)) {
            int startIndex = map.get(prefixSum - k);
            maxLen = Math.max(maxLen, i - startIndex);
        }

        // Only store FIRST occurrence (do not overwrite)
        if (!map.containsKey(prefixSum)) {
            map.put(prefixSum, i);
        }
    }

    return maxLen;
}
```

**Key difference from Template 2:**
- Template 2 stores frequency (how many times each prefix sum appeared)
- Template 3 stores the first index (to maximize subarray length)
- For longest: don't overwrite existing entries in the map
- For shortest: always overwrite (store latest index)

**Execution trace** for `arr = [1, -1, 5, -2, 3]`, `k = 3`:

```
map = {0: -1}
prefixSum = 0, maxLen = 0

i=0: arr[0]=1
  prefixSum = 1
  look up (1-3) = -2 -> not in map
  map doesn't have 1, so map = {0:-1, 1:0}

i=1: arr[1]=-1
  prefixSum = 0
  look up (0-3) = -3 -> not in map
  map already has 0 (at index -1), don't overwrite
  map = {0:-1, 1:0}

i=2: arr[2]=5
  prefixSum = 5
  look up (5-3) = 2 -> not in map
  map = {0:-1, 1:0, 5:2}

i=3: arr[3]=-2
  prefixSum = 3
  look up (3-3) = 0 -> found at index -1
  length = 3 - (-1) = 4, maxLen = 4
  map = {0:-1, 1:0, 5:2, 3:3}

i=4: arr[4]=3
  prefixSum = 6
  look up (6-3) = 3 -> found at index 3
  length = 4 - 3 = 1, maxLen stays 4
  map = {0:-1, 1:0, 5:2, 3:3, 6:4}

Result: 4
Verify: subarray [1,-1,5,-2] (indices 0-3) has sum 3. Length 4. Correct.
```

---

### Template 4: Difference Array (Range Updates)

```java
/**
 * Difference Array for range updates.
 *
 * Problem: Apply multiple range updates [l, r, val] to an array,
 *          then answer point queries.
 *
 * Key idea:
 *   diff[l] += val   means "start adding val from index l"
 *   diff[r+1] -= val means "stop adding val after index r"
 *   Final array = prefix sum of diff
 *
 * Time: O(1) per update, O(n) to reconstruct
 * Space: O(n)
 *
 * Use when: many range updates, then one final read of all values
 * Don't use when: you need to query after each update (use BIT/segment tree)
 */
public int[] applyRangeUpdates(int n, int[][] updates) {
    int[] diff = new int[n + 1];  // size n+1 to handle r+1 = n safely

    for (int[] update : updates) {
        int l = update[0];
        int r = update[1];
        int val = update[2];

        diff[l] += val;
        if (r + 1 <= n) {
            diff[r + 1] -= val;
        }
    }

    // Reconstruct: prefix sum of diff
    int[] result = new int[n];
    int running = 0;
    for (int i = 0; i < n; i++) {
        running += diff[i];
        result[i] = running;
    }

    return result;
}
```

**Execution trace** for `n=5`, updates: `[1,3,2]`, `[2,4,3]`, `[0,2,-1]`:

```
Initial diff = [0, 0, 0, 0, 0, 0]  (size 6)

Update [1,3,2]: diff[1]+=2, diff[4]-=2
  diff = [0, 2, 0, 0, -2, 0]

Update [2,4,3]: diff[2]+=3, diff[5]-=3
  diff = [0, 2, 3, 0, -2, -3]

Update [0,2,-1]: diff[0]+=-1, diff[3]-=(-1)=+1
  diff = [-1, 2, 3, 1, -2, -3]

Prefix sum of diff:
  i=0: running = -1, result[0] = -1
  i=1: running = -1+2 = 1, result[1] = 1
  i=2: running = 1+3 = 4, result[2] = 4
  i=3: running = 4+1 = 5, result[3] = 5
  i=4: running = 5+(-2) = 3, result[4] = 3

result = [-1, 1, 4, 5, 3]

Verify manually:
  Index 0: only update [0,2,-1] applies -> -1. Correct.
  Index 1: updates [1,3,2] and [0,2,-1] apply -> 2 + (-1) = 1. Correct.
  Index 2: all three apply -> 2 + 3 + (-1) = 4. Correct.
  Index 3: updates [1,3,2] and [2,4,3] apply -> 2 + 3 = 5. Correct.
  Index 4: only [2,4,3] applies -> 3. Correct.
```

**Common pitfalls:**
- Forgetting to handle `r+1 = n` (out of bounds) — use size `n+1` for the diff array
- Applying the prefix sum to the diff array in-place vs. a separate result array — both work, be consistent

---

### Template 5: 2D Prefix Sum

```java
/**
 * 2D Prefix Sum for rectangle range queries.
 *
 * prefix[i][j] = sum of all elements in rectangle (0,0) to (i-1, j-1)
 * prefix has size (m+1) x (n+1) — one extra row and column of zeros
 *
 * Build formula (inclusion-exclusion):
 *   prefix[i][j] = prefix[i-1][j] + prefix[i][j-1]
 *                - prefix[i-1][j-1] + matrix[i-1][j-1]
 *
 * Query for rectangle (r1,c1) to (r2,c2) inclusive, 0-indexed:
 *   sum = prefix[r2+1][c2+1]
 *       - prefix[r1][c2+1]
 *       - prefix[r2+1][c1]
 *       + prefix[r1][c1]
 *
 * Time: O(m*n) build, O(1) per query
 * Space: O(m*n)
 */
public class TwoDPrefixSum {

    private long[][] prefix;

    public TwoDPrefixSum(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        prefix = new long[m + 1][n + 1];  // extra row and column of zeros

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                prefix[i][j] = prefix[i - 1][j]      // rectangle above
                             + prefix[i][j - 1]       // rectangle to the left
                             - prefix[i - 1][j - 1]   // subtract double-counted corner
                             + matrix[i - 1][j - 1];  // add current cell
            }
        }
    }

    /**
     * Sum of rectangle from (r1,c1) to (r2,c2) inclusive, 0-indexed.
     * Time: O(1)
     */
    public long query(int r1, int c1, int r2, int c2) {
        return prefix[r2 + 1][c2 + 1]
             - prefix[r1][c2 + 1]
             - prefix[r2 + 1][c1]
             + prefix[r1][c1];
    }
}
```

**Execution trace** for a 3x3 matrix:

```
matrix = [[1, 2, 3],
          [4, 5, 6],
          [7, 8, 9]]

Build prefix (4x4, initialized to 0):

i=1, j=1: prefix[1][1] = prefix[0][1] + prefix[1][0] - prefix[0][0] + matrix[0][0]
                        = 0 + 0 - 0 + 1 = 1

i=1, j=2: prefix[1][2] = prefix[0][2] + prefix[1][1] - prefix[0][1] + matrix[0][1]
                        = 0 + 1 - 0 + 2 = 3

i=1, j=3: prefix[1][3] = 0 + 3 - 0 + 3 = 6

i=2, j=1: prefix[2][1] = prefix[1][1] + prefix[2][0] - prefix[1][0] + matrix[1][0]
                        = 1 + 0 - 0 + 4 = 5

i=2, j=2: prefix[2][2] = prefix[1][2] + prefix[2][1] - prefix[1][1] + matrix[1][1]
                        = 3 + 5 - 1 + 5 = 12

i=2, j=3: prefix[2][3] = 6 + 12 - 3 + 6 = 21

i=3, j=1: prefix[3][1] = 5 + 0 - 0 + 7 = 12
i=3, j=2: prefix[3][2] = 12 + 12 - 5 + 8 = 27
i=3, j=3: prefix[3][3] = 21 + 27 - 12 + 9 = 45

prefix = [[0,  0,  0,  0],
          [0,  1,  3,  6],
          [0,  5, 12, 21],
          [0, 12, 27, 45]]

Query (1,1) to (2,2) — the center 2x2 submatrix [[5,6],[8,9]]:
  sum = prefix[3][3] - prefix[1][3] - prefix[3][1] + prefix[1][1]
      = 45 - 6 - 12 + 1 = 28
Verify: 5 + 6 + 8 + 9 = 28. Correct.

Query (0,0) to (1,1) — top-left 2x2 [[1,2],[4,5]]:
  sum = prefix[2][2] - prefix[0][2] - prefix[2][0] + prefix[0][0]
      = 12 - 0 - 0 + 0 = 12
Verify: 1 + 2 + 4 + 5 = 12. Correct.
```

**Memorization tip for the query formula:** Think of it as "big rectangle minus two side rectangles plus the double-subtracted corner". Draw it:

```
+--+--+--+
|  |  |  |
+--+--+--+
|  |XX|XX|
+--+--+--+
|  |XX|XX|
+--+--+--+

XX = the rectangle we want (r1,c1) to (r2,c2)

prefix[r2+1][c2+1] = entire top-left rectangle including XX
prefix[r1][c2+1]   = rectangle above XX
prefix[r2+1][c1]   = rectangle to the left of XX
prefix[r1][c1]     = corner that was subtracted twice, add it back
```

---

## 5. Real-World Applications

### 1. Database Aggregation / OLAP Cubes

Data warehouses precompute cumulative sums to enable O(1) range queries. "Total revenue from March to July" is a prefix sum subtraction on monthly totals. OLAP cubes extend this to multiple dimensions — the 2D prefix sum is the 2D version of this. Systems like Apache Druid and Snowflake use materialized aggregations that are conceptually prefix sums.

### 2. Image Processing — Integral Images (Summed-Area Tables)

2D prefix sums enable O(1) computation of any rectangle's pixel sum in an image. The Viola-Jones face detection algorithm (2001) uses this for Haar feature computation — it needs to evaluate thousands of rectangle features per image position, and doing each in O(1) instead of O(area) makes real-time detection feasible. OpenCV's `integral()` function computes exactly this. Adaptive thresholding and mean filtering also use integral images.

### 3. Network Traffic Monitoring

Cumulative byte counters on network interfaces are prefix sums. The counter on a network interface increments monotonically. Traffic in a time window = counter_end - counter_start. Tools like `ifstat`, `iftop`, and SNMP-based monitoring systems all use this principle. The "bytes transferred between 2pm and 3pm" is a prefix sum query.

### 4. Genomics / Bioinformatics

GC-content in a DNA segment (count of G and C bases) uses prefix count arrays. Given a genome string of length 3 billion, answering "how many G/C bases in positions 1000-2000?" in O(1) requires a prefix count array built in O(n). Tools like samtools and bioinformatics pipelines use this for coverage analysis and GC-bias detection.

### 5. Game Development — Terrain Height Maps

2D prefix sums on height maps enable fast average-height queries in terrain rendering. "What's the average height in this 100x100 tile?" is a 2D prefix sum query. Used in level-of-detail (LOD) systems, collision detection, and procedural terrain generation. The Unity and Unreal Engine terrain systems use similar precomputed structures.

### 6. Flight/Hotel Booking Systems — Occupancy Tracking

"How many rooms are booked on day X?" is a difference array problem. Each booking adds +1 at check-in day and -1 at checkout day. The prefix sum of these changes gives occupancy per day. This is more efficient than updating every day in the range for each booking. Booking.com, Airbnb, and airline reservation systems use this pattern for availability queries.

---

## 6. Problem Categories and Solutions

---

### Category A: Basic Prefix Sum

---

#### LC 303 — Range Sum Query - Immutable (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given an integer array, handle multiple queries of the form `sumRange(left, right)` returning the sum of elements between indices `left` and `right` inclusive.

**Why prefix sum:** Multiple queries on the same static array. O(n) preprocessing, O(1) per query.

```java
class NumArray {
    private long[] prefix;

    public NumArray(int[] nums) {
        int n = nums.length;
        prefix = new long[n + 1];
        for (int i = 1; i <= n; i++) {
            prefix[i] = prefix[i - 1] + nums[i - 1];
        }
    }

    public int sumRange(int left, int right) {
        return (int)(prefix[right + 1] - prefix[left]);
    }
}
```

**Complexity:** O(n) build, O(1) per query, O(n) space.

**Key insight:** The naive approach is O(n) per query. With k queries, that's O(n*k). Prefix sum makes it O(n + k). For large k, this is a massive win.

---

#### LC 724 — Find Pivot Index (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Goldman Sachs

**Problem:** Find the leftmost index where the sum of elements to the left equals the sum of elements to the right.

**Why prefix sum:** We need left sum and right sum at each index. Prefix sum gives both in O(1).

```java
public int pivotIndex(int[] nums) {
    int n = nums.length;
    long[] prefix = new long[n + 1];
    for (int i = 1; i <= n; i++) {
        prefix[i] = prefix[i - 1] + nums[i - 1];
    }

    long total = prefix[n];

    for (int i = 0; i < n; i++) {
        long leftSum = prefix[i];           // sum of nums[0..i-1]
        long rightSum = total - prefix[i + 1]; // sum of nums[i+1..n-1]
        if (leftSum == rightSum) {
            return i;
        }
    }

    return -1;
}
```

**Complexity:** O(n) time, O(n) space.

**Optimization:** You can do this in O(1) space by computing total first, then tracking left sum as you go:

```java
public int pivotIndex(int[] nums) {
    long total = 0;
    for (int num : nums) total += num;

    long leftSum = 0;
    for (int i = 0; i < nums.length; i++) {
        // leftSum = sum of nums[0..i-1]
        // rightSum = total - nums[i] - leftSum
        if (leftSum == total - nums[i] - leftSum) {
            return i;
        }
        leftSum += nums[i];
    }

    return -1;
}
```

**Key insight:** `leftSum == rightSum` is equivalent to `2 * leftSum + nums[i] == total`.

---

#### LC 238 — Product of Array Except Self (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe, Goldman Sachs, Uber

**Problem:** Return an array where `output[i]` is the product of all elements except `nums[i]`. No division allowed. O(n) time.

**Why prefix product:** `output[i] = (product of all elements to the left of i) * (product of all elements to the right of i)`. Two passes.

**O(n) space solution (easier to understand):**

```java
public int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] left = new int[n];   // left[i] = product of nums[0..i-1]
    int[] right = new int[n];  // right[i] = product of nums[i+1..n-1]
    int[] output = new int[n];

    left[0] = 1;
    for (int i = 1; i < n; i++) {
        left[i] = left[i - 1] * nums[i - 1];
    }

    right[n - 1] = 1;
    for (int i = n - 2; i >= 0; i--) {
        right[i] = right[i + 1] * nums[i + 1];
    }

    for (int i = 0; i < n; i++) {
        output[i] = left[i] * right[i];
    }

    return output;
}
```

**O(1) extra space solution (the trick interviewers want):**

Use the output array itself as the left prefix product, then do a right-to-left pass with a running right product:

```java
public int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] output = new int[n];

    // First pass: output[i] = product of nums[0..i-1]
    output[0] = 1;
    for (int i = 1; i < n; i++) {
        output[i] = output[i - 1] * nums[i - 1];
    }
    // output = [1, nums[0], nums[0]*nums[1], ...]

    // Second pass: multiply by suffix product from the right
    int rightProduct = 1;
    for (int i = n - 1; i >= 0; i--) {
        output[i] *= rightProduct;
        rightProduct *= nums[i];
    }

    return output;
}
```

**Execution trace** for `nums = [1, 2, 3, 4]`:

```
After first pass (left prefix products):
  output = [1, 1, 2, 6]
  output[0] = 1 (no elements to the left)
  output[1] = 1 (just nums[0]=1)
  output[2] = 1*2 = 2
  output[3] = 1*2*3 = 6

Second pass (multiply by right suffix products):
  rightProduct = 1

  i=3: output[3] = 6 * 1 = 6, rightProduct = 1 * 4 = 4
  i=2: output[2] = 2 * 4 = 8, rightProduct = 4 * 3 = 12
  i=1: output[1] = 1 * 12 = 12, rightProduct = 12 * 2 = 24
  i=0: output[0] = 1 * 24 = 24, rightProduct = 24 * 1 = 24

output = [24, 12, 8, 6]

Verify:
  output[0] = 2*3*4 = 24. Correct.
  output[1] = 1*3*4 = 12. Correct.
  output[2] = 1*2*4 = 8. Correct.
  output[3] = 1*2*3 = 6. Correct.
```

**Complexity:** O(n) time, O(1) extra space (output array doesn't count).

**Why no division:** If you used division, you'd need to handle zeros specially (one zero makes all outputs 0 except the zero's position; two zeros make all outputs 0). The prefix product approach handles zeros naturally.

---

#### LC 1480 — Running Sum of 1d Array (Easy)

**Companies:** Amazon, Google, Microsoft

**Problem:** Return the running sum of an array where `runningSum[i] = sum(nums[0..i])`.

This is literally computing the prefix sum in-place. Warm-up problem.

```java
public int[] runningSum(int[] nums) {
    for (int i = 1; i < nums.length; i++) {
        nums[i] += nums[i - 1];
    }
    return nums;
}
```

**Complexity:** O(n) time, O(1) space (in-place).

Note: this modifies the input. If that's not allowed, copy first.

---

### Category B: Prefix Sum + HashMap — Count Problems

---

#### LC 560 — Subarray Sum Equals K (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe, Goldman Sachs, Uber

**Frequency:** One of the top 10 most asked interview questions across FAANG. Extremely likely to appear.

**Problem:** Given an integer array `nums` and an integer `k`, return the total number of subarrays whose sum equals `k`.

**Why this is hard for other approaches:**
- Brute force: O(n²) — too slow
- Sliding window: doesn't work because of negative numbers (can't shrink window predictably)
- Prefix sum + HashMap: O(n) — the right tool

**Solution:**

```java
public int subarraySum(int[] nums, int k) {
    Map<Long, Integer> map = new HashMap<>();
    map.put(0L, 1);  // CRITICAL: empty prefix

    long prefixSum = 0;
    int count = 0;

    for (int i = 0; i < nums.length; i++) {
        prefixSum += nums[i];

        // Number of subarrays ending at i with sum = k
        count += map.getOrDefault(prefixSum - k, 0);

        // Record this prefix sum
        map.put(prefixSum, map.getOrDefault(prefixSum, 0) + 1);
    }

    return count;
}
```

**Detailed dry run** for `nums = [1, 2, 3, -3, 3]`, `k = 3`:

```
Initial state:
  map = {0: 1}
  prefixSum = 0
  count = 0

Step i=0, nums[0]=1:
  prefixSum = 0 + 1 = 1
  Look up (1 - 3) = -2 in map -> not found, add 0
  count = 0
  map.put(1, 1)
  map = {0:1, 1:1}

Step i=1, nums[1]=2:
  prefixSum = 1 + 2 = 3
  Look up (3 - 3) = 0 in map -> found 1 time, add 1
  count = 1  [subarray [1,2] found]
  map.put(3, 1)
  map = {0:1, 1:1, 3:1}

Step i=2, nums[2]=3:
  prefixSum = 3 + 3 = 6
  Look up (6 - 3) = 3 in map -> found 1 time, add 1
  count = 2  [subarray [3] at index 2 found]
  map.put(6, 1)
  map = {0:1, 1:1, 3:1, 6:1}

Step i=3, nums[3]=-3:
  prefixSum = 6 + (-3) = 3
  Look up (3 - 3) = 0 in map -> found 1 time, add 1
  count = 3  [subarray [1,2,3,-3] found]
  map.put(3, 2)  // 3 now appears twice
  map = {0:1, 1:1, 3:2, 6:1}

Step i=4, nums[4]=3:
  prefixSum = 3 + 3 = 6
  Look up (6 - 3) = 3 in map -> found 2 times, add 2
  count = 5  [subarrays [3,-3,3] and [3] at index 4 found]
  map.put(6, 2)
  map = {0:1, 1:1, 3:2, 6:2}

Final count = 5

Verify all subarrays with sum 3:
  [1,2] (indices 0-1): 1+2=3. Yes.
  [3] (index 2): 3. Yes.
  [1,2,3,-3] (indices 0-3): 1+2+3-3=3. Yes.
  [3,-3,3] (indices 2-4): 3-3+3=3. Yes.
  [3] (index 4): 3. Yes.
Total: 5. Correct.
```

**What happens when k=0:**

```
nums = [0, 0, 0], k = 0

map = {0:1}
prefixSum = 0, count = 0

i=0: prefixSum=0, look up 0 -> found 1, count=1, map={0:2}
i=1: prefixSum=0, look up 0 -> found 2, count=3, map={0:3}
i=2: prefixSum=0, look up 0 -> found 3, count=6, map={0:4}

Result: 6
Subarrays: [0], [0], [0], [0,0], [0,0], [0,0,0] = 6. Correct.
```

**Complexity:** O(n) time, O(n) space.

**Interview follow-up questions:**
- "What if the array has only positive numbers?" — sliding window also works, but prefix sum + HashMap is still correct
- "What if k=0?" — handled correctly, as shown above
- "Can you do it in O(1) space?" — no, the HashMap is necessary for O(n) time; O(1) space would require O(n²) time

---

#### LC 523 — Continuous Subarray Sum (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given an integer array `nums` and an integer `k`, return `true` if there is a continuous subarray of length at least 2 whose sum is a multiple of `k`.

**Key trick:** If `prefix[j] % k == prefix[i] % k`, then `(prefix[j] - prefix[i]) % k == 0`, meaning the subarray `nums[i..j-1]` has sum divisible by `k`.

Store the first occurrence of each remainder. If the same remainder appears again at least 2 positions later, we found our subarray.

```java
public boolean checkSubarraySum(int[] nums, int k) {
    // map: remainder -> first index where this remainder appeared
    Map<Integer, Integer> map = new HashMap<>();
    map.put(0, -1);  // empty prefix at index -1

    int prefixSum = 0;

    for (int i = 0; i < nums.length; i++) {
        prefixSum += nums[i];
        int remainder = prefixSum % k;

        if (map.containsKey(remainder)) {
            // Check length >= 2: current index i, previous index map.get(remainder)
            // Subarray is from map.get(remainder)+1 to i, length = i - map.get(remainder)
            if (i - map.get(remainder) >= 2) {
                return true;
            }
        } else {
            map.put(remainder, i);  // store FIRST occurrence only
        }
    }

    return false;
}
```

**Why store first occurrence:** We want the longest possible subarray to maximize the chance of length >= 2. Storing the first occurrence gives us the earliest start.

**Why `map.put(0, -1)`:** The empty prefix has remainder 0 at index -1. If we see remainder 0 at index 1 or later, the subarray from index 0 to that index has sum divisible by k, and its length is at least 2.

**Execution trace** for `nums = [23, 2, 4, 6, 7]`, `k = 6`:

```
map = {0: -1}
prefixSum = 0

i=0: prefixSum=23, remainder=23%6=5
  5 not in map, map.put(5, 0)
  map = {0:-1, 5:0}

i=1: prefixSum=25, remainder=25%6=1
  1 not in map, map.put(1, 1)
  map = {0:-1, 5:0, 1:1}

i=2: prefixSum=29, remainder=29%6=5
  5 in map at index 0
  length = 2 - 0 = 2 >= 2 -> return true

Subarray: nums[1..2] = [2, 4], sum = 6, divisible by 6. Correct.
```

**Edge case:** `k = 1` — every integer is divisible by 1, so any subarray of length >= 2 works. The algorithm handles this correctly since all remainders are 0.

**Complexity:** O(n) time, O(min(n, k)) space.

---

#### LC 974 — Subarray Sums Divisible by K (Medium)

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given an integer array `nums` and an integer `k`, return the number of non-empty subarrays with sum divisible by `k`.

**Critical Java detail:** Java's `%` operator returns negative results for negative operands. `(-3) % 5 = -3` in Java, but mathematically the remainder should be `2`. You must normalize.

```java
public int subarraysDivByK(int[] nums, int k) {
    // map: remainder -> frequency
    Map<Integer, Integer> map = new HashMap<>();
    map.put(0, 1);  // empty prefix

    int prefixSum = 0;
    int count = 0;

    for (int num : nums) {
        prefixSum += num;

        // Normalize remainder to [0, k-1]
        // Java's % can be negative for negative prefixSum
        int remainder = ((prefixSum % k) + k) % k;

        count += map.getOrDefault(remainder, 0);
        map.put(remainder, map.getOrDefault(remainder, 0) + 1);
    }

    return count;
}
```

**Why `((prefixSum % k) + k) % k`:**

```
prefixSum = -3, k = 5
Java: (-3) % 5 = -3  (WRONG for our purposes)
Fix: ((-3 % 5) + 5) % 5 = (-3 + 5) % 5 = 2 % 5 = 2  (CORRECT)

prefixSum = 7, k = 5
Java: 7 % 5 = 2  (already correct)
Fix: ((7 % 5) + 5) % 5 = (2 + 5) % 5 = 7 % 5 = 2  (still correct)
```

The formula works for both positive and negative values.

**Execution trace** for `nums = [4, 5, 0, -2, -3, 1]`, `k = 5`:

```
map = {0:1}
prefixSum = 0, count = 0

i=0: num=4, prefixSum=4, remainder=4
  map has no 4, count=0, map={0:1, 4:1}

i=1: num=5, prefixSum=9, remainder=9%5=4
  map has 4 (count 1), count=1, map={0:1, 4:2}

i=2: num=0, prefixSum=9, remainder=4
  map has 4 (count 2), count=3, map={0:1, 4:3}

i=3: num=-2, prefixSum=7, remainder=7%5=2
  map has no 2, count=3, map={0:1, 4:3, 2:1}

i=4: num=-3, prefixSum=4, remainder=4
  map has 4 (count 3), count=6, map={0:1, 4:4, 2:1}

i=5: num=1, prefixSum=5, remainder=0
  map has 0 (count 1), count=7, map={0:2, 4:4, 2:1}

Result: 7
```

**Complexity:** O(n) time, O(min(n, k)) space.

---

#### LC 930 — Binary Subarrays With Sum (Medium)

**Companies:** Amazon, Google, Meta

**Problem:** Given a binary array `nums` and an integer `goal`, return the number of non-empty subarrays with sum equal to `goal`.

This can be solved with either prefix sum + HashMap (Template 2) or sliding window (Topic 1, Template 5 — "at most" trick).

**Prefix Sum + HashMap solution (works for all cases):**

```java
public int numSubarraysWithSum(int[] nums, int goal) {
    Map<Integer, Integer> map = new HashMap<>();
    map.put(0, 1);

    int prefixSum = 0;
    int count = 0;

    for (int num : nums) {
        prefixSum += num;
        count += map.getOrDefault(prefixSum - goal, 0);
        map.put(prefixSum, map.getOrDefault(prefixSum, 0) + 1);
    }

    return count;
}
```

**Sliding Window solution (works because binary array has no negatives):**

Count subarrays with sum exactly `goal` = count with sum at most `goal` minus count with sum at most `goal - 1`.

```java
public int numSubarraysWithSum(int[] nums, int goal) {
    return atMost(nums, goal) - atMost(nums, goal - 1);
}

private int atMost(int[] nums, int goal) {
    if (goal < 0) return 0;
    int left = 0, sum = 0, count = 0;
    for (int right = 0; right < nums.length; right++) {
        sum += nums[right];
        while (sum > goal) {
            sum -= nums[left++];
        }
        count += right - left + 1;
    }
    return count;
}
```

**When to use which:** Both are O(n). The prefix sum + HashMap solution is more general (works with negatives). The sliding window solution is slightly more space-efficient (O(1) vs O(n)) but only works for non-negative arrays.

---

#### LC 525 — Contiguous Array (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given a binary array `nums`, return the maximum length of a contiguous subarray with an equal number of 0s and 1s.

**Key transformation:** Replace every 0 with -1. Now "equal 0s and 1s" becomes "sum = 0". Find the longest subarray with sum 0.

This uses Template 3 (first occurrence map for longest subarray).

```java
public int findMaxLength(int[] nums) {
    // Transform: 0 -> -1, 1 -> 1
    // Find longest subarray with sum = 0

    Map<Integer, Integer> map = new HashMap<>();
    map.put(0, -1);  // empty prefix at index -1

    int prefixSum = 0;
    int maxLen = 0;

    for (int i = 0; i < nums.length; i++) {
        prefixSum += (nums[i] == 0) ? -1 : 1;

        if (map.containsKey(prefixSum)) {
            maxLen = Math.max(maxLen, i - map.get(prefixSum));
        } else {
            map.put(prefixSum, i);  // store FIRST occurrence
        }
    }

    return maxLen;
}
```

**Execution trace** for `nums = [0, 1, 0, 1, 1, 0]`:

```
After transformation: [-1, 1, -1, 1, 1, -1]

map = {0: -1}
prefixSum = 0, maxLen = 0

i=0: val=-1, prefixSum=-1
  -1 not in map, map.put(-1, 0)
  map = {0:-1, -1:0}

i=1: val=1, prefixSum=0
  0 in map at index -1
  length = 1 - (-1) = 2, maxLen = 2
  (don't update map, keep first occurrence)

i=2: val=-1, prefixSum=-1
  -1 in map at index 0
  length = 2 - 0 = 2, maxLen = 2

i=3: val=1, prefixSum=0
  0 in map at index -1
  length = 3 - (-1) = 4, maxLen = 4

i=4: val=1, prefixSum=1
  1 not in map, map.put(1, 4)

i=5: val=-1, prefixSum=0
  0 in map at index -1
  length = 5 - (-1) = 6, maxLen = 6

Result: 6
Verify: entire array [0,1,0,1,1,0] has three 0s and three 1s. Length 6. Correct.
```

**Complexity:** O(n) time, O(n) space.

---

### Category C: Difference Array

---

#### LC 370 — Range Addition (Medium, Premium)

**Companies:** Amazon, Google, Microsoft

**Problem:** Given a length `n` array initialized to all zeros, apply `k` range update operations of the form `[startIndex, endIndex, inc]` (add `inc` to all elements from `startIndex` to `endIndex` inclusive), then return the final array.

**The canonical difference array problem.**

```java
public int[] getModifiedArray(int length, int[][] updates) {
    int[] diff = new int[length + 1];  // +1 to handle endIndex+1 = length

    for (int[] update : updates) {
        int start = update[0];
        int end = update[1];
        int inc = update[2];

        diff[start] += inc;
        diff[end + 1] -= inc;
    }

    // Prefix sum to reconstruct
    int[] result = new int[length];
    int running = 0;
    for (int i = 0; i < length; i++) {
        running += diff[i];
        result[i] = running;
    }

    return result;
}
```

**Complexity:** O(n + k) time where k is number of updates, O(n) space.

**Why this beats naive:** Naive approach updates every element in the range: O(n*k) total. Difference array: O(k) for all updates + O(n) to reconstruct = O(n + k).

---

#### LC 1109 — Corporate Flight Bookings (Medium)

**Companies:** Amazon, Google, Bloomberg

**Problem:** There are `n` flights. You're given bookings where `bookings[i] = [first, last, seats]` means `seats` seats are booked on every flight from `first` to `last` (1-indexed). Return an array of the total number of seats booked on each flight.

Same as LC 370 but 1-indexed.

```java
public int[] corpFlightBookings(int[][] bookings, int n) {
    int[] diff = new int[n + 1];  // 1-indexed, size n+1

    for (int[] booking : bookings) {
        int first = booking[0] - 1;  // convert to 0-indexed
        int last = booking[1] - 1;
        int seats = booking[2];

        diff[first] += seats;
        if (last + 1 <= n - 1) {
            diff[last + 1] -= seats;
        }
    }

    int[] result = new int[n];
    int running = 0;
    for (int i = 0; i < n; i++) {
        running += diff[i];
        result[i] = running;
    }

    return result;
}
```

**Alternative: keep 1-indexed throughout:**

```java
public int[] corpFlightBookings(int[][] bookings, int n) {
    int[] diff = new int[n + 2];  // 1-indexed, extra space for last+1

    for (int[] booking : bookings) {
        diff[booking[0]] += booking[2];
        diff[booking[1] + 1] -= booking[2];
    }

    int[] result = new int[n];
    int running = 0;
    for (int i = 1; i <= n; i++) {
        running += diff[i];
        result[i - 1] = running;
    }

    return result;
}
```

**Complexity:** O(n + k) time, O(n) space.

---

#### LC 1094 — Car Pooling (Medium)

**Companies:** Amazon, Google, Microsoft, Bloomberg, Uber

**Problem:** A car has `capacity` seats. Given trips where `trips[i] = [numPassengers, from, to]`, return `true` if it's possible to pick up and drop off all passengers without exceeding capacity at any point.

**Key insight:** Passengers board at `from` and leave at `to`. This is a difference array problem. Check if the running sum ever exceeds capacity.

```java
public boolean carPooling(int[][] trips, int capacity) {
    // Maximum location is 1000 per constraints
    int[] diff = new int[1001];

    for (int[] trip : trips) {
        int passengers = trip[0];
        int from = trip[1];
        int to = trip[2];

        diff[from] += passengers;   // board at 'from'
        diff[to] -= passengers;     // leave at 'to' (not 'to+1', they leave before arriving)
    }

    int current = 0;
    for (int i = 0; i < diff.length; i++) {
        current += diff[i];
        if (current > capacity) {
            return false;
        }
    }

    return true;
}
```

**Important detail:** Passengers leave at `to`, not `to + 1`. The problem says they drop off at `to`, so the car is free at location `to`. Hence `diff[to] -= passengers` (not `diff[to+1]`).

**Execution trace** for `trips = [[2,1,5],[3,3,7]]`, `capacity = 4`:

```
diff = [0, 0, 0, 0, 0, 0, 0, 0, ...]

Trip [2,1,5]: diff[1]+=2, diff[5]-=2
  diff = [0, 2, 0, 0, 0, -2, 0, ...]

Trip [3,3,7]: diff[3]+=3, diff[7]-=3
  diff = [0, 2, 0, 3, 0, -2, 0, -3, ...]

Prefix sum:
  i=0: current=0
  i=1: current=2 (2 passengers board)
  i=2: current=2
  i=3: current=5 > 4 -> return false

Result: false. Correct (at location 3-4, we'd have 5 passengers but capacity is 4).
```

**Complexity:** O(n + max_location) time, O(max_location) space.

---

### Category D: 2D Prefix Sum

---

#### LC 304 — Range Sum Query 2D - Immutable (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given a 2D matrix, handle multiple queries of the form `sumRegion(row1, col1, row2, col2)` returning the sum of elements in the rectangle.

```java
class NumMatrix {
    private long[][] prefix;

    public NumMatrix(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        prefix = new long[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                prefix[i][j] = prefix[i - 1][j]
                             + prefix[i][j - 1]
                             - prefix[i - 1][j - 1]
                             + matrix[i - 1][j - 1];
            }
        }
    }

    public int sumRegion(int row1, int col1, int row2, int col2) {
        return (int)(
            prefix[row2 + 1][col2 + 1]
          - prefix[row1][col2 + 1]
          - prefix[row2 + 1][col1]
          + prefix[row1][col1]
        );
    }
}
```

**Complexity:** O(m*n) build, O(1) per query, O(m*n) space.

**The formula to memorize:**

```
Query (r1,c1) to (r2,c2):
  = prefix[r2+1][c2+1]   // full rectangle from (0,0) to (r2,c2)
  - prefix[r1][c2+1]     // subtract top strip
  - prefix[r2+1][c1]     // subtract left strip
  + prefix[r1][c1]       // add back double-subtracted corner
```

---

#### LC 1314 — Matrix Block Sum (Medium)

**Companies:** Amazon, Google

**Problem:** Given an `m x n` matrix and an integer `k`, return a matrix `answer` where `answer[i][j]` is the sum of all elements `mat[r][c]` for `i - k <= r <= i + k` and `j - k <= c <= j + k`.

```java
public int[][] matrixBlockSum(int[][] mat, int k) {
    int m = mat.length;
    int n = mat[0].length;

    // Build 2D prefix sum
    long[][] prefix = new long[m + 1][n + 1];
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            prefix[i][j] = prefix[i - 1][j] + prefix[i][j - 1]
                         - prefix[i - 1][j - 1] + mat[i - 1][j - 1];
        }
    }

    int[][] answer = new int[m][n];
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            // Rectangle bounds, clamped to matrix boundaries
            int r1 = Math.max(0, i - k);
            int c1 = Math.max(0, j - k);
            int r2 = Math.min(m - 1, i + k);
            int c2 = Math.min(n - 1, j + k);

            answer[i][j] = (int)(
                prefix[r2 + 1][c2 + 1]
              - prefix[r1][c2 + 1]
              - prefix[r2 + 1][c1]
              + prefix[r1][c1]
            );
        }
    }

    return answer;
}
```

**Complexity:** O(m*n) time and space.

**Key detail:** Clamp the rectangle bounds with `Math.max` and `Math.min` to stay within the matrix.

---

#### LC 363 — Max Sum of Rectangle No Larger Than K (Hard)

**Companies:** Google, Amazon, Microsoft

**Problem:** Given an `m x n` matrix and an integer `k`, return the max sum of a rectangle in the matrix such that its sum is no larger than `k`.

**Approach:** Fix two column boundaries (left and right). Compress each row between those columns into a 1D array. Then find the maximum subarray sum no larger than `k` in that 1D array using prefix sums + a sorted set (TreeSet in Java).

```java
public int maxSumSubmatrix(int[][] matrix, int k) {
    int m = matrix.length;
    int n = matrix[0].length;
    int result = Integer.MIN_VALUE;

    // Fix left column
    for (int left = 0; left < n; left++) {
        int[] rowSum = new int[m];  // rowSum[i] = sum of row i from left to right

        // Expand right column
        for (int right = left; right < n; right++) {
            // Add current column to rowSum
            for (int i = 0; i < m; i++) {
                rowSum[i] += matrix[i][right];
            }

            // Find max subarray sum <= k in rowSum using prefix sum + TreeSet
            TreeSet<Integer> set = new TreeSet<>();
            set.add(0);  // empty prefix
            int prefixSum = 0;

            for (int sum : rowSum) {
                prefixSum += sum;
                // We want: prefixSum - prevPrefix <= k
                //          prevPrefix >= prefixSum - k
                // Find smallest prevPrefix >= prefixSum - k
                Integer ceiling = set.ceiling(prefixSum - k);
                if (ceiling != null) {
                    result = Math.max(result, prefixSum - ceiling);
                }
                set.add(prefixSum);
            }
        }
    }

    return result;
}
```

**Complexity:** O(n² * m * log m) time, O(m) space.

**Why TreeSet:** We need the smallest prefix sum that is >= `prefixSum - k`. `TreeSet.ceiling(x)` gives the smallest element >= x in O(log n).

---

### Category E: Advanced / Hybrid

---

#### LC 437 — Path Sum III (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given a binary tree and a target sum, return the number of paths that sum to the target. Paths don't need to start or end at the root or a leaf.

**Key insight:** Prefix sum works on tree paths too. During DFS, the path from root to current node is a sequence of values. The prefix sum of this path is the running sum. Use a HashMap to count how many times each prefix sum has appeared on the current root-to-node path.

**Critical difference from array version:** You must backtrack (remove from the map) when you leave a node, because the HashMap should only contain prefix sums on the current root-to-node path.

```java
public int pathSum(TreeNode root, int targetSum) {
    Map<Long, Integer> map = new HashMap<>();
    map.put(0L, 1);  // empty prefix
    return dfs(root, 0L, targetSum, map);
}

private int dfs(TreeNode node, long currentSum, int target, Map<Long, Integer> map) {
    if (node == null) return 0;

    currentSum += node.val;

    // Count paths ending at this node with sum = target
    int count = map.getOrDefault(currentSum - target, 0);

    // Add current prefix sum to map
    map.put(currentSum, map.getOrDefault(currentSum, 0) + 1);

    // Recurse into children
    count += dfs(node.left, currentSum, target, map);
    count += dfs(node.right, currentSum, target, map);

    // BACKTRACK: remove current prefix sum when leaving this node
    map.put(currentSum, map.get(currentSum) - 1);

    return count;
}
```

**Why backtracking is essential:**

```
Tree:
    10
   /  \
  5    -3
 / \     \
3   2     11

Without backtracking: when processing node 2 (right child of 5),
the map would still contain prefix sums from the path 10->5->3,
which is a DIFFERENT path. This would give wrong counts.

With backtracking: when we finish processing the subtree rooted at 3,
we remove its prefix sum from the map before processing node 2.
```

**Complexity:** O(n) time (each node visited once), O(n) space (map + recursion stack).

---

#### LC 1310 — XOR Queries of a Subarray (Medium)

**Companies:** Amazon, Google

**Problem:** Given an array `arr` and queries where each query is `[l, r]`, return the XOR of all elements from index `l` to `r`.

**Prefix XOR:** Same principle as prefix sum, but with XOR instead of addition. XOR is its own inverse: `a ^ a = 0`.

```java
public int[] xorQueries(int[] arr, int[][] queries) {
    int n = arr.length;
    int[] prefix = new int[n + 1];
    prefix[0] = 0;

    for (int i = 1; i <= n; i++) {
        prefix[i] = prefix[i - 1] ^ arr[i - 1];
    }

    int[] result = new int[queries.length];
    for (int i = 0; i < queries.length; i++) {
        int l = queries[i][0];
        int r = queries[i][1];
        result[i] = prefix[r + 1] ^ prefix[l];
    }

    return result;
}
```

**Why XOR works:** `prefix[r+1] ^ prefix[l]` = XOR of arr[0..r] XOR'd with XOR of arr[0..l-1]. Since XOR is its own inverse, the elements from 0 to l-1 cancel out, leaving XOR of arr[l..r].

**Complexity:** O(n + q) time, O(n) space.

---

#### LC 528 — Random Pick with Weight (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given an array of positive integers `w` where `w[i]` is the weight of index `i`, implement `pickIndex()` that randomly picks an index with probability proportional to its weight.

**Approach:** Build prefix sum of weights. The prefix sum creates "buckets" of size proportional to each weight. Generate a random number in `[1, total_weight]` and binary search for which bucket it falls in.

```java
class Solution {
    private int[] prefix;
    private Random random;

    public Solution(int[] w) {
        int n = w.length;
        prefix = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            prefix[i] = prefix[i - 1] + w[i - 1];
        }
        random = new Random();
    }

    public int pickIndex() {
        int total = prefix[prefix.length - 1];
        int target = random.nextInt(total) + 1;  // [1, total]

        // Binary search: find first index where prefix[i] >= target
        int lo = 1, hi = prefix.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (prefix[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }

        return lo - 1;  // convert from prefix index to original index
    }
}
```

**Why this gives correct probabilities:**

```
w = [1, 3, 2], total = 6
prefix = [0, 1, 4, 6]

Random target in [1, 6]:
  target=1: binary search finds prefix index 1 -> original index 0 (weight 1, prob 1/6)
  target=2,3,4: binary search finds prefix index 2 -> original index 1 (weight 3, prob 3/6)
  target=5,6: binary search finds prefix index 3 -> original index 2 (weight 2, prob 2/6)

Probabilities: 1/6, 3/6, 2/6. Matches weights. Correct.
```

**Complexity:** O(n) build, O(log n) per query.

---

## 7. Common Mistakes and Edge Cases

### Mistake Table

| Mistake | Why it happens | How to fix |
|---------|---------------|------------|
| Forgetting `map.put(0, 1)` | Subarrays starting from index 0 need the empty prefix | ALWAYS initialize: `map.put(0, 1)` before the loop |
| Off-by-one in prefix array indexing | `prefix[i]` represents sum of first `i` elements, not element at index `i` | `prefix` has size `n+1`. `sum(l,r) = prefix[r+1] - prefix[l]` |
| Negative mod in Java | Java's `%` can return negative: `(-3) % 5 = -3`, not `2` | Use `((prefix % k) + k) % k` to normalize |
| Using prefix sum where sliding window is better | All-positive sum problems with "at most" constraint | If all positive and looking for longest/shortest: sliding window. If negatives or exact count: prefix sum. |
| Integer overflow in prefix sum | Large values accumulate | Use `long` instead of `int` for prefix array |
| 2D inclusion-exclusion sign errors | The formula has adds and subtracts that are easy to mix up | Draw the 4-rectangle diagram every time until memorized |
| Updating map before checking `prefixSum - k` | Would count zero-length subarrays | Always check first, then update map |
| Not backtracking in tree DFS | HashMap contains prefix sums from other paths | Remove from map when leaving a node |
| Using `diff[r+1]` when `r+1 = n` | Array out of bounds | Use `diff` array of size `n+1` |
| Storing latest index instead of first for "longest" | Gives shorter subarrays | For longest: only store if key not present. For shortest: always overwrite. |

### Edge Cases to Test

**Empty array:**
```java
// prefix sum of empty array
int[] arr = {};
// prefix = [0], no queries possible
// Template 2: map = {0:1}, loop doesn't execute, count = 0
```

**Single element:**
```java
// arr = [5], k = 5
// prefix = [0, 5]
// Template 2: i=0, prefixSum=5, look up 0 -> found 1, count=1
// Correct: [5] is the only subarray, sum=5=k
```

**Subarray is the entire array:**
```java
// arr = [1, 2, 3], k = 6
// At i=2: prefixSum=6, look up 0 -> found 1, count=1
// Correct: [1,2,3] has sum 6
```

**k = 0 in "sum divisible by k":**
```java
// k=0 would cause division by zero in modulo
// LC 974 guarantees k >= 1, but check constraints
```

**All zeros:**
```java
// arr = [0, 0, 0], k = 0
// Every subarray has sum 0, count = n*(n+1)/2
// Template 2 handles this correctly
```

**Negative numbers only:**
```java
// arr = [-1, -2, -3], k = -3
// prefixSum at i=0: -1, look up -1-(-3)=2 -> not found
// prefixSum at i=1: -3, look up -3-(-3)=0 -> found 1, count=1
// Subarray [-1,-2] has sum -3. Correct.
```

**Large prefix sums (overflow):**
```java
// arr has 10^5 elements each with value 10^4
// Max prefix sum = 10^9, fits in int
// But if values are 10^9, prefix sum can reach 10^14 -> use long
int[] arr = new int[100000];
Arrays.fill(arr, 1000000000);
long prefixSum = 0;  // must be long
```

---

## 8. Pattern Comparison — Critical Section

### Prefix Sum vs Sliding Window

This is the most important decision you'll make in subarray sum problems.

| Problem Type | All Positive Numbers | Has Negative Numbers |
|-------------|---------------------|---------------------|
| Subarray sum = k (count) | Sliding Window (Topic 1, Template 5) OR Prefix Sum+HashMap | Prefix Sum + HashMap ONLY |
| Longest subarray sum = k | Sliding Window (if positives, simpler) | Prefix Sum + HashMap (first occurrence) |
| Shortest subarray sum >= k | Sliding Window (Topic 1, Template 3) | Deque + Prefix Sum (LC 862) |
| Subarray sum at most k | Sliding Window | Prefix Sum + Binary Search |
| Count subarrays sum divisible by k | Prefix Sum + HashMap (modulo trick) | Prefix Sum + HashMap (modulo trick) |

**The rule:** When you see negative numbers in a sum problem, reach for prefix sum + HashMap. Sliding window's shrink logic breaks with negatives.

**Why sliding window breaks with negatives:**

```
arr = [1, -1, 1, -1, 1], k = 1

Sliding window attempt:
  right=0: sum=1, found! But we'd shrink left...
  After shrinking: left=1, sum=0
  right=1: sum=-1, expand right
  right=2: sum=0, expand right
  right=3: sum=-1, expand right
  right=4: sum=0, expand right
  Missed: [1,-1,1] at indices 0-2, [-1,1] at indices 1-2, etc.

The problem: when sum > k, we shrink left. But with negatives,
shrinking might decrease the sum further (if we remove a positive)
or increase it (if we remove a negative). The monotonic property is gone.
```

### Prefix Sum vs Segment Tree / BIT

| Feature | Prefix Sum | Segment Tree | BIT (Fenwick Tree) |
|---------|-----------|-------------|-------------------|
| Build time | O(n) | O(n) | O(n log n) |
| Range query | O(1) | O(log n) | O(log n) |
| Point update | O(n) rebuild | O(log n) | O(log n) |
| Range update | O(1) with diff array | O(log n) | O(log n) |
| Space | O(n) | O(n) | O(n) |
| Use when | Static array, many queries | Dynamic array, mixed queries | Dynamic array, prefix queries |

**Use prefix sum when:** The array doesn't change between queries. If elements are updated, you'd need to rebuild the prefix array (O(n)), which defeats the purpose.

### Difference Array vs Naive Range Updates

| Approach | Per Update | Final Read | Total for k updates |
|---------|-----------|-----------|-------------------|
| Naive (update each element) | O(range size) | O(1) | O(n*k) worst case |
| Difference Array | O(1) | O(n) | O(k + n) |

**Use difference array when:** You have many range updates and only need to read the final values once. If you need to query values between updates, use a BIT or segment tree.

### Prefix Sum vs Kadane's Algorithm

| Problem | Use |
|---------|-----|
| Maximum subarray sum | Kadane's algorithm (O(n), O(1) space) |
| Count subarrays with sum = k | Prefix Sum + HashMap |
| Longest subarray with sum = k | Prefix Sum + HashMap (first occurrence) |
| Maximum subarray sum no larger than k | Prefix Sum + TreeSet (O(n log n)) |

Kadane's is for maximum sum. Prefix sum is for exact sum queries and counting.

---

## 9. Quick Reference Cheat Sheet

### Template Selector

```
Is the array static (no updates between queries)?
  YES -> Prefix Sum
  NO  -> Segment Tree or BIT

What kind of query?
  Range sum (multiple queries)     -> Template 1 (Basic Prefix Sum)
  Count subarrays with sum = k     -> Template 2 (Prefix Sum + HashMap, frequency)
  Longest subarray with sum = k    -> Template 3 (Prefix Sum + HashMap, first index)
  Range updates, then read values  -> Template 4 (Difference Array)
  2D rectangle sum queries         -> Template 5 (2D Prefix Sum)

Are there negative numbers?
  YES -> Must use Prefix Sum + HashMap (not sliding window)
  NO  -> Sliding window may be simpler for some problems
```

### Prefix Sum + HashMap Initialization Checklist

Before writing the loop, verify:

```
[ ] Map initialized: Map<Long, Integer> map = new HashMap<>();
[ ] Empty prefix added: map.put(0L, 1);
[ ] prefixSum initialized to 0 (long, not int)
[ ] count initialized to 0
[ ] Inside loop: prefixSum += arr[i] FIRST
[ ] Then: count += map.getOrDefault(prefixSum - k, 0)
[ ] Then: map.put(prefixSum, map.getOrDefault(prefixSum, 0) + 1)
[ ] For "longest": only put if key not present (first occurrence)
[ ] For "count": always put (frequency)
```

### 2D Prefix Sum — Build and Query on One Card

```java
// BUILD (m+1 x n+1 array, 1-indexed):
prefix[i][j] = prefix[i-1][j] + prefix[i][j-1] - prefix[i-1][j-1] + matrix[i-1][j-1];

// QUERY (r1,c1) to (r2,c2), 0-indexed:
sum = prefix[r2+1][c2+1] - prefix[r1][c2+1] - prefix[r2+1][c1] + prefix[r1][c1];

// Memory aid: "big minus top minus left plus corner"
```

### Difference Array — Formula on One Card

```java
// SETUP:
int[] diff = new int[n + 1];  // size n+1

// UPDATE range [l, r] by val:
diff[l] += val;
diff[r + 1] -= val;

// RECONSTRUCT:
int running = 0;
for (int i = 0; i < n; i++) {
    running += diff[i];
    result[i] = running;
}
```

### Negative Mod in Java

```java
// WRONG: Java's % can be negative
int remainder = prefixSum % k;  // can be negative if prefixSum < 0

// CORRECT: normalize to [0, k-1]
int remainder = ((prefixSum % k) + k) % k;
```

### Time/Space Complexity Summary

| Template | Build Time | Query Time | Space |
|---------|-----------|-----------|-------|
| Basic Prefix Sum (1D) | O(n) | O(1) | O(n) |
| Prefix Sum + HashMap (count) | O(n) | N/A (single pass) | O(n) |
| Prefix Sum + HashMap (longest) | O(n) | N/A (single pass) | O(n) |
| Difference Array | O(k) updates | O(n) reconstruct | O(n) |
| 2D Prefix Sum | O(m*n) | O(1) | O(m*n) |
| Prefix XOR | O(n) | O(1) | O(n) |

---

## 10. Practice Roadmap

### Week 1 — Easy + Foundation (15 min each)

Goal: Get the basic prefix sum array into muscle memory. Understand the `n+1` size and the `prefix[r+1] - prefix[l]` formula.

| Problem | Difficulty | Key Concept | Time Budget |
|---------|-----------|-------------|-------------|
| LC 1480 — Running Sum of 1d Array | Easy | Literally computing prefix sum | 10 min |
| LC 303 — Range Sum Query - Immutable | Easy | Build + query pattern | 15 min |
| LC 724 — Find Pivot Index | Easy | Left sum = right sum | 15 min |
| LC 238 — Product of Array Except Self | Medium | Prefix product, O(1) space trick | 20 min |

After week 1: You should be able to write Template 1 from memory and explain the `+1` offset.

### Week 2 — Medium: HashMap Technique (25 min each)

Goal: Master Template 2 (count) and Template 3 (longest). The `map.put(0, 1)` initialization should be automatic.

| Problem | Difficulty | Key Concept | Time Budget |
|---------|-----------|-------------|-------------|
| LC 560 — Subarray Sum Equals K | Medium | THE canonical problem, Template 2 | 25 min |
| LC 525 — Contiguous Array | Medium | Transform 0->-1, Template 3 | 25 min |
| LC 523 — Continuous Subarray Sum | Medium | Modulo trick, first occurrence | 25 min |
| LC 974 — Subarray Sums Divisible by K | Medium | Negative mod in Java | 25 min |

After week 2: You should be able to solve LC 560 in under 10 minutes. The HashMap pattern should feel natural.

### Week 3 — Medium: Difference Array + 2D (25 min each)

Goal: Understand difference arrays as the inverse of prefix sum. Build the 2D prefix sum formula from the inclusion-exclusion principle.

| Problem | Difficulty | Key Concept | Time Budget |
|---------|-----------|-------------|-------------|
| LC 1094 — Car Pooling | Medium | Difference array, capacity check | 20 min |
| LC 1109 — Corporate Flight Bookings | Medium | Difference array, 1-indexed | 20 min |
| LC 304 — Range Sum Query 2D | Medium | 2D prefix sum, inclusion-exclusion | 30 min |
| LC 930 — Binary Subarrays With Sum | Medium | Prefix sum OR sliding window | 20 min |

After week 3: You should be able to derive the 2D prefix sum formula from scratch and implement the difference array without looking it up.

### Week 4 — Advanced (35 min each)

Goal: Apply prefix sum in non-obvious contexts (trees, XOR, weighted random).

| Problem | Difficulty | Key Concept | Time Budget |
|---------|-----------|-------------|-------------|
| LC 437 — Path Sum III | Medium | Prefix sum on tree, backtracking | 35 min |
| LC 528 — Random Pick with Weight | Medium | Prefix sum + binary search | 30 min |
| LC 363 — Max Sum of Rectangle No Larger Than K | Hard | 2D -> 1D reduction, TreeSet | 45 min |
| LC 1310 — XOR Queries of a Subarray | Medium | Prefix XOR | 20 min |

After week 4: You should be able to recognize prefix sum in disguised forms and apply it to trees and XOR problems.

### Total: 16 problems over 4 weeks

### Interview Preparation Notes

**For LC 560 (most asked):** Practice until you can write the solution in 5 minutes. Be ready to explain why `map.put(0, 1)` is needed with a concrete example. Know the time and space complexity cold.

**For LC 238 (very frequently asked):** Always show the O(1) space optimization. Interviewers specifically ask for it. Explain the two-pass approach clearly.

**For any prefix sum + HashMap problem:** State the invariant before coding: "The map stores the frequency of each prefix sum seen so far. `map.put(0, 1)` represents the empty prefix."

**When asked about negative numbers:** Immediately say "sliding window won't work here because of negatives — I'll use prefix sum + HashMap."

**For 2D prefix sum:** Draw the four rectangles on the whiteboard before writing the formula. It prevents sign errors and shows the interviewer you understand the derivation.

---

*Document 5 of 20 in the FAANG DSA Prep series.*

*Previous: Topic 4 — Binary Search*
*Next: Topic 6 — (coming soon)*
