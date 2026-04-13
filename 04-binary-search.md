# Topic 4: Binary Search

*This is document 4 of 20 in the FAANG DSA Prep series.*

Binary Search is far more than searching a sorted array. It's a thinking framework: whenever a problem has a monotonic condition (false, false, ..., true, true, ...), you can binary search for the boundary. This insight turns "search on answer" problems from impossible to trivial.

**Difficulty distribution** (based on LeetCode's 258 tagged problems):
- Easy: 10%
- Medium: 55%
- Hard: 35%

**Top companies asking Binary Search:**

| Company | Problem Count |
|---------|--------------|
| Google | 191 |
| Amazon | 181 |
| Microsoft | 122 |
| Meta | 117 |
| Bloomberg | 107 |

---

## Table of Contents

1. [Core Concept](#1-core-concept)
2. [ELI5](#2-eli5)
3. [When to Use](#3-when-to-use)
4. [Core Templates in Java](#4-core-templates-in-java)
   - [The Overflow Problem](#the-overflow-problem)
   - [Template 1: Standard Binary Search](#template-1-standard-binary-search)
   - [Template 2: Lower Bound and Upper Bound](#template-2-lower-bound-and-upper-bound)
   - [Template 3: Binary Search on Answer](#template-3-binary-search-on-answer)
   - [Template Comparison Summary](#template-comparison-summary)
5. [Real-World Applications](#5-real-world-applications)
6. [Problem Categories and Solutions](#6-problem-categories-and-solutions)
   - [Category A: Classic Binary Search](#category-a-classic-binary-search)
   - [Category B: Modified Array Search](#category-b-modified-array-search)
   - [Category C: Binary Search on Answer](#category-c-binary-search-on-answer)
   - [Category D: Advanced Problems](#category-d-advanced-problems)
7. [Common Mistakes and Edge Cases](#7-common-mistakes-and-edge-cases)
8. [Pattern Comparison](#8-pattern-comparison)
9. [Quick Reference Cheat Sheet](#9-quick-reference-cheat-sheet)
10. [Practice Roadmap](#10-practice-roadmap)

---

## 1. Core Concept

### What is Binary Search?

Binary search is an algorithm that finds a value within a sorted structure in `O(log n)` time. The mechanic: compare the target against the middle element, eliminate half the remaining search space, repeat until found or the space is empty.

That description is correct but incomplete.

### The Real Insight

Binary search doesn't require a sorted array. It requires a **monotonic predicate**: a boolean function `f(x)` that produces a sequence like this:

```
Index:     0     1     2     3     4     5     6
f(x):    false false false true  true  true  true
                           ^
                        boundary
```

You're not "finding a value." You're finding the **boundary** where the predicate flips from false to true (or true to false). The sorted-array case is just the most obvious instance: `f(index) = (arr[index] >= target)`.

Once you internalize this framing, binary search becomes applicable to a surprisingly large class of problems where the array itself is never searched at all.

### Three Categories of Binary Search

**Category 1: Classic Binary Search**

Find a target in a sorted array. The array itself is the search space. Time: `O(log n)`.

**Category 2: Binary Search on Modified Arrays**

Rotated sorted arrays, peak finding, searching in a sorted matrix. The structure is array-based but the predicate is more subtle. The array may not be uniformly sorted. Time: `O(log n)`.

**Category 3: Binary Search on Answer Space**

The most powerful variant. The search space is not array indices but the *answer itself*. "What is the minimum speed that lets Koko eat all bananas in H hours?" Binary search over possible speed values, not over array indices.

This is what interviewers mean when they say "this is a binary search problem" about something that looks nothing like a sorted-array lookup.

Time: `O(n * log(answer_range))` because evaluating the predicate at each midpoint requires scanning the full input.

### Why O(log n)?

Each iteration eliminates half the remaining search space:

```
Start:           n elements
After step 1:    n/2 elements
After step 2:    n/4 elements
After step k:    n / 2^k elements
```

Search ends when `n / 2^k = 1`, giving `k = log2(n)`.

For concrete intuition:
- n = 1,000: about 10 iterations
- n = 1,000,000: about 20 iterations
- n = 1,000,000,000: about 30 iterations

A billion-element search in 30 steps. That's the power of logarithmic scaling.

### Connection to Topics 1-3

From the previous documents in this series:
- **Topic 2 (Two Pointers):** Two pointers scan linearly, O(n). Binary search reduces the scan to O(log n) when the predicate is monotonic.
- **Topic 3 (Sliding Window):** Sliding window finds an optimal contiguous subarray in O(n). Some sliding window problems have a binary search version: binary search on the answer (window size or threshold), then verify with a single linear pass.

Binary search is not a replacement for these patterns. It's an orthogonal tool that applies when the search space has a monotonic structure.

---

## 2. ELI5

### Classic Binary Search

Think of a dictionary. You want to find "elephant." You don't start at page 1 and flip every page. You open to the middle. The middle page has words starting with "M." Too far right. You take the left half and open to its middle. You get words starting with "G." Still too far. Take the left half again. Now you're at "D." Close. Take the right half of that remaining chunk. Each time, you throw away half the remaining work.

That's binary search. The dictionary's alphabetical order is the monotonic property that makes it work.

### Search on Answer Space

Imagine I'm thinking of a number between 1 and 1000. After each guess, I tell you "too high" or "too low." What's your strategy?

You'd always guess the middle. Not 1, not 1000. The middle. That eliminates half the possibilities regardless of my answer.

Now swap "number between 1 and 1000" with "minimum ship capacity between 1 and 500,000." Same strategy. The key is that the condition "capacity is sufficient" is monotonically true: if capacity C works, then C+1 definitely works too. That monotonicity is all binary search needs.

### The Boundary Insight

Picture a row of 100 light switches. The left switches are all OFF. The right switches are all ON. There's exactly one spot where OFF becomes ON. You need to find it.

Checking every switch left to right is O(n). Or: check switch 50. If it's ON, the boundary is at 50 or to its left. If it's OFF, the boundary is to the right. Either way, 50 switches eliminated. Keep doing that. That's binary search for the boundary.

This is exactly what Template 2 (lower bound) does on an array, and what Template 3 (search on answer) does to the answer space.

---

## 3. When to Use

### Green Flags

- "sorted array" + "find target/position" - Classic binary search (Template 1)
- "find first/last position of element" - Lower/upper bound (Template 2)
- "search in rotated sorted array" - Modified binary search (Template 1 variant)
- "find peak element" - Binary search on slope direction (Template 2 variant)
- "search in 2D matrix" where rows and columns are sorted - Matrix binary search
- "find minimum/maximum X such that condition(X)" - Search on answer (Template 3)
- "minimum speed/capacity/time to accomplish..." - Search on answer
- "kth smallest/largest" in sorted structure - Binary search on value
- "minimum number of days/workers/boats to..." - Search on answer
- The answer exists in a range, and feasibility is easy to check - Search on answer

**The strongest signal for search on answer:** the problem asks for "minimum X such that [something] is possible." Ask yourself: if X works, does X+1 also work? If yes, the condition is monotonic, and you can binary search on X.

### When NOT to Use

- Unsorted data without a discoverable monotonic predicate: use a hash map or sort first
- You need ALL elements matching a condition: binary search finds one boundary, not a collection
- Data changes frequently during the search: binary search needs a static search space
- The search space cannot be halved by checking a single element
- Predicate evaluation itself is too expensive: O(n log n) predicate makes total cost worse than brute force

### The Monotonicity Test

Before applying binary search, explicitly verify the predicate is monotonic. Write out:

```
canAchieve(1)  = false
canAchieve(2)  = false
canAchieve(5)  = true
canAchieve(6)  = true
canAchieve(10) = true
```

If you ever see `false, true, false` or `true, false, true`, binary search will not work. Use dynamic programming or a different approach instead.

---

## 4. Core Templates in Java

Off-by-one errors are the single biggest reason candidates fail binary search problems. Three clean templates cover every case. Knowing *which* to use, and *why* the loop condition differs between them, is what separates candidates who solve these in 10 minutes from those who spend 30 minutes debugging.

---

### The Overflow Problem

Before any template, the mid calculation.

**Wrong:**
```java
int mid = (left + right) / 2;
```

**Why it's wrong:**

If `left = 1_500_000_000` and `right = 2_000_000_000`, then `left + right = 3_500_000_000`. A 32-bit `int` holds at most `2,147,483,647` (roughly 2.1 billion). The sum overflows, wraps around to a negative number, and your `mid` is negative. Your binary search silently produces wrong results. No compiler warning. No runtime error. Just a subtle bug.

**Correct:**
```java
int mid = left + (right - left) / 2;
```

**Why this works:**

`right - left` is always non-negative (since `right >= left` inside the loop) and always fits in an `int` (since both are valid indices or answer-space values within int range). Adding that delta to `left` stays within range.

Algebraically, `left + (right - left) / 2 = (2*left + right - left) / 2 = (left + right) / 2`. Same math, no overflow.

**When to use `long`:**

If your answer space uses `long` values, or if the predicate involves multiplying mid by itself (like LC 69 Sqrt), use `long` for mid:

```java
long mid = left + (right - left) / 2;
```

This matters: for `mid = 46341`, `mid * mid = 2,147,488,281`, which overflows int. The cutoff is `sqrt(Integer.MAX_VALUE) ≈ 46340`. Any binary search problem involving squared values needs `long` arithmetic.

---

### Template 1: Standard Binary Search

**Use when:** You need to find an exact target value in a sorted array and return -1 if not found.

**Problems that use this:** LC 704, LC 33, LC 74, LC 540, LC 81

**Decision criteria:**
- You return -1 when the target is absent
- You don't care which occurrence you find (or the array has unique elements)
- You need the exact index of the target

```java
/**
 * Template 1: Standard Binary Search
 *
 * Invariant: if target exists, it is in arr[left..right] (inclusive on both ends)
 *
 * Loop condition: while (left <= right)
 *   Rationale: when left == right, there is exactly one unchecked element.
 *   Without the equals, we'd skip that element and possibly miss the target.
 *   The loop exits when left > right, meaning the search space is empty.
 *
 * Mid calculation: left + (right - left) / 2
 *   This is floor division. Mid is always in [left, right].
 *   For left=3, right=4: mid = 3 + 0 = 3 (biased toward left).
 *
 * Update rules:
 *   arr[mid] < target  -> left = mid + 1   (mid is too small, exclude it)
 *   arr[mid] > target  -> right = mid - 1  (mid is too large, exclude it)
 *   Both updates exclude mid from the next search space.
 *   This guarantees the space strictly shrinks every iteration: no infinite loop.
 *
 * Termination state:
 *   Found: returned the index directly inside the loop.
 *   Not found: left == right + 1. Left points to where target would be inserted.
 */
public int binarySearch(int[] arr, int target) {
    int left = 0, right = arr.length - 1;

    while (left <= right) {
        int mid = left + (right - left) / 2;

        if (arr[mid] == target) {
            return mid;           // Exact match
        } else if (arr[mid] < target) {
            left = mid + 1;       // Target in right half
        } else {
            right = mid - 1;      // Target in left half
        }
    }

    return -1;  // Search space exhausted, target not present
}
```

**Common pitfall with Template 1:**

Never write `left = mid` or `right = mid` in Template 1. This causes an infinite loop.

Example: `left = 3, right = 4`. Floor mid = `3 + (4-3)/2 = 3`.
If `arr[mid] < target` and you write `left = mid` instead of `left = mid + 1`, you get `left = 3`. Next iteration: same state. Infinite loop.

Always use `left = mid + 1` and `right = mid - 1` in Template 1.

**Execution trace on `arr = [1, 3, 5, 7, 9]`, target = 7:**

```
Step 1: left=0, right=4, mid=2 → arr[2]=5 < 7 → left = 3
Step 2: left=3, right=4, mid=3 → arr[3]=7 == 7 → return 3
```

Result: 3. Correct.

**Execution trace on `arr = [1, 3, 5, 7, 9]`, target = 6 (not present):**

```
Step 1: left=0, right=4, mid=2 → arr[2]=5 < 6 → left = 3
Step 2: left=3, right=4, mid=3 → arr[3]=7 > 6 → right = 2
Step 3: left=3, right=2 → left > right → exit loop
return -1
```

Result: -1. Correct. Note that at termination, `left = 3` is exactly where 6 would be inserted.

**Execution trace on `arr = [5]`, target = 5 (single element):**

```
Step 1: left=0, right=0, mid=0 → arr[0]=5 == 5 → return 0
```

Result: 0. The `left <= right` condition is critical here. With `left < right`, the loop would never execute.

---

### Template 2: Lower Bound and Upper Bound

**Use when:** You need the first or last occurrence of a value, or the insertion point. This template never returns -1; it always returns a valid position.

**Problems that use this:** LC 34, LC 35, LC 153, LC 162

**Decision criteria:**
- "Find first position of X" - lower bound
- "Find last position of X" - upper bound (then subtract 1, or adjust)
- "Find insertion point for X in sorted array" - lower bound
- You need to count occurrences: `upperBound(x) - lowerBound(x)`

```java
/**
 * Lower Bound: find the FIRST index where arr[i] >= target.
 * Equivalently: the leftmost position to insert target to keep arr sorted.
 *
 * Range: [0, arr.length]
 *   right is initialized to arr.length, NOT arr.length - 1.
 *   Why: if target is larger than all elements, the insertion point is arr.length.
 *   Setting right = arr.length - 1 would prevent returning that valid answer.
 *
 * Loop condition: while (left < right)
 *   NOT <=. When left == right, we have our answer. The loop terminates
 *   precisely when left == right: that is the lower bound.
 *
 * Update rules:
 *   arr[mid] < target  -> left = mid + 1   (mid is definitely NOT the lower bound)
 *   arr[mid] >= target -> right = mid      (mid COULD be the lower bound, preserve it)
 *
 * Critical: right = mid, NOT right = mid - 1.
 *   If we set right = mid - 1 when arr[mid] >= target, we might skip the actual answer.
 *   We keep mid as a candidate by setting right = mid.
 *
 * Does this cause infinite loop?
 *   No. In the arr[mid] >= target branch, right = mid.
 *   mid = left + (right - left) / 2 < right when left < right.
 *   So right strictly decreases. In the other branch, left = mid + 1 > mid.
 *   Space shrinks every iteration.
 */
public int lowerBound(int[] arr, int target) {
    int left = 0, right = arr.length;   // right = arr.length, not arr.length - 1

    while (left < right) {
        int mid = left + (right - left) / 2;

        if (arr[mid] < target) {
            left = mid + 1;   // mid is definitely not the lower bound
        } else {
            right = mid;      // mid might be the lower bound, keep it
        }
    }

    return left;  // left == right: this is the lower bound
}

/**
 * Upper Bound: find the FIRST index where arr[i] > target.
 * Equivalently: the position one past the last occurrence of target.
 *
 * Number of occurrences of target = upperBound(target) - lowerBound(target)
 *
 * The only difference from lower bound: the condition flips.
 *   arr[mid] <= target -> left = mid + 1   (mid is at or before the last target)
 *   arr[mid] > target  -> right = mid      (mid might be the upper bound)
 */
public int upperBound(int[] arr, int target) {
    int left = 0, right = arr.length;

    while (left < right) {
        int mid = left + (right - left) / 2;

        if (arr[mid] <= target) {
            left = mid + 1;   // mid is not past the last target
        } else {
            right = mid;      // mid might be the upper bound
        }
    }

    return left;  // left == right: first index where arr[i] > target
}
```

**Why `right = arr.length` and not `arr.length - 1`:**

Consider `arr = [1, 2, 3]`, target = 5. The correct lower bound is index 3, which is `arr.length`. If you initialize `right = arr.length - 1 = 2`, the loop can never return 3. You'd return 2 (last element), which is wrong.

**Execution trace on `arr = [1, 2, 2, 2, 3, 4]`, target = 2, lower bound:**

```
left=0, right=6

Step 1: mid = 0 + (6-0)/2 = 3 → arr[3]=2 >= 2 → right = 3
Step 2: left=0, right=3, mid = 0 + (3-0)/2 = 1 → arr[1]=2 >= 2 → right = 1
Step 3: left=0, right=1, mid = 0 + (1-0)/2 = 0 → arr[0]=1 < 2 → left = 1
Step 4: left=1, right=1 → loop exits (left == right)

return 1
```

Result: 1. The first occurrence of 2 is at index 1. Correct.

**Execution trace on `arr = [1, 2, 2, 2, 3, 4]`, target = 2, upper bound:**

```
left=0, right=6

Step 1: mid=3 → arr[3]=2 <= 2 → left = 4
Step 2: left=4, right=6, mid=5 → arr[5]=4 > 2 → right = 5
Step 3: left=4, right=5, mid=4 → arr[4]=3 > 2 → right = 4
Step 4: left=4, right=4 → loop exits

return 4
```

Result: 4. The first index greater than all 2s is 4 (which holds the value 3). Count of 2s = upperBound - lowerBound = 4 - 1 = 3. The values at indices 1, 2, 3 are all 2. Correct.

**Execution trace on `arr = [1, 2, 2, 2, 3, 4]`, target = 5 (beyond all elements):**

```
lower bound:
  All mid values: arr[mid] < 5 → left keeps incrementing
  Eventually: left = 6 = arr.length
return 6
```

Result: 6. Correct insertion point. The `right = arr.length` initialization made this possible.

---

### Template 3: Binary Search on Answer

**Use when:** The problem asks for "minimum X such that condition(X) is satisfied" or "maximum X such that condition(X) is satisfied." The answer itself is binary searched, not an array index.

**Problems that use this:** LC 875, LC 1011, LC 410, LC 1283, LC 69, LC 4 (indirectly)

**The four-step method:**

1. **Identify the answer space `[lo, hi]`.** What's the smallest possible answer? What's the largest?
2. **Write the predicate `boolean canAchieve(int x, ...)`**. Given answer = x, can we satisfy the condition? This must be O(n) or faster.
3. **Verify monotonicity.** If `canAchieve(x)` is true, is `canAchieve(x+1)` also true (for minimum search)? Draw it out. If not monotonic, binary search will not work.
4. **Apply the template.** The binary search itself is mechanical once steps 1-3 are correct.

```java
/**
 * Template 3a: Find MINIMUM value such that predicate is true.
 *
 * Predicate shape: [false, false, ..., true, true, true]
 *                                      ^
 *                              we want this first true
 *
 * Same loop structure as Template 2: while (lo < hi), same asymmetric updates.
 * Because we're searching for a boundary, same as lower bound.
 *
 * canAchieve(mid) = true  -> hi = mid      (mid satisfies, but smaller might too)
 * canAchieve(mid) = false -> lo = mid + 1  (mid fails, must go higher)
 */
public int findMinimumAnswer(int lo, int hi /* , other params */) {
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;

        if (canAchieve(mid)) {
            hi = mid;       // mid works, search for something smaller
        } else {
            lo = mid + 1;   // mid fails, need something bigger
        }
    }

    return lo;  // lo == hi: the minimum value that satisfies the condition
}

/**
 * Template 3b: Find MAXIMUM value such that predicate is true.
 *
 * Predicate shape: [true, true, ..., false, false]
 *                               ^
 *                       we want this last true
 *
 * KEY DIFFERENCE: mid uses CEILING, not floor.
 * mid = lo + (hi - lo + 1) / 2
 *
 * Why ceiling? Suppose lo=3, hi=4. Floor mid = 3.
 * If canAchieve(3)=true -> lo = mid = 3. Now lo=3, hi=4. Same state. Infinite loop.
 * Ceiling mid = 4. If canAchieve(4)=true -> lo = 4. Now lo=hi=4. Loop exits. Correct.
 */
public int findMaximumAnswer(int lo, int hi /* , other params */) {
    while (lo < hi) {
        int mid = lo + (hi - lo + 1) / 2;  // CEILING, not floor

        if (canAchieve(mid)) {
            lo = mid;       // mid satisfies, but larger might too
        } else {
            hi = mid - 1;   // mid fails, must go lower
        }
    }

    return lo;  // lo == hi: the maximum value that satisfies the condition
}
```

**Why floor for Template 3a but ceiling for Template 3b:**

The rule is: whichever branch sets `lo = mid` or `hi = mid` (without +1 or -1), the mid must be biased *away* from that side to prevent stagnation.

- Template 3a: `hi = mid` branch exists. Mid must be biased toward `lo`. Use floor: `lo + (hi - lo) / 2`.
- Template 3b: `lo = mid` branch exists. Mid must be biased toward `hi`. Use ceiling: `lo + (hi - lo + 1) / 2`.

If you get this wrong, you'll either have an infinite loop or skip the correct answer. Test with `lo = k, hi = k+1` to verify your mid calculation doesn't stagnate.

**Execution trace for Template 3a on Koko Bananas (brief):**

For `piles = [3, 6, 7, 11]`, `h = 8`, answer space `[1, 11]`:

```
lo=1, hi=11, mid=6: canFinish(6)=true  → hi=6
lo=1, hi=6,  mid=3: canFinish(3)=false → lo=4
lo=4, hi=6,  mid=5: canFinish(5)=true  → hi=5
lo=4, hi=5,  mid=4: canFinish(4)=true  → hi=4
lo=4, hi=4 → exit
return 4
```

Full dry run in the Koko Bananas problem section below.

---

### Template Comparison Summary

| Aspect | Template 1 | Template 2 | Template 3a | Template 3b |
|--------|-----------|-----------|------------|------------|
| Goal | Exact match | First boundary | Min satisfying | Max satisfying |
| Loop | `while (left <= right)` | `while (left < right)` | `while (lo < hi)` | `while (lo < hi)` |
| right init | `arr.length - 1` | `arr.length` | `maxAnswer` | `maxAnswer` |
| left update | `left = mid + 1` | `left = mid + 1` | `lo = mid + 1` | `lo = mid` |
| right update | `right = mid - 1` | `right = mid` | `hi = mid` | `hi = mid - 1` |
| Mid | floor | floor | floor | CEILING |
| Not found | returns -1 | N/A (always valid) | N/A | N/A |
| Termination | `left > right` | `left == right` | `lo == hi` | `lo == hi` |
| left at exit | first element > target | lower bound | min answer | max answer |

**Quick selector:**

```
Do you need the exact value, return -1 if absent?
  → Template 1

Do you need the insertion point, or first/last occurrence?
  → Template 2 (lower/upper bound)

Do you need minimum X where condition(X) is true?
  → Template 3a (floor mid, hi = mid on true)

Do you need maximum X where condition(X) is true?
  → Template 3b (ceiling mid, lo = mid on true)
```

---

## 5. Real-World Applications

### Database Indexing (B-Trees)

Every database query on an indexed column uses binary search. PostgreSQL and MySQL InnoDB store index data in B-trees. Each tree node holds a sorted list of keys. Finding the right key within a node is binary search. Traversing root to leaf is O(log n) where n is the number of indexed rows. Without this, every `SELECT` on an indexed column would require a full table scan, making databases unusable at scale.

The B+ tree variant (used by most databases for range queries) stores all data in leaf nodes connected as a linked list. Binary search finds the starting leaf; then a linear scan handles the range. This is exactly lower bound followed by iteration.

### Git Bisect

`git bisect` is binary search on commit history. You have a bug absent in an old commit but present in HEAD. You know the "good" boundary and "bad" boundary. `git bisect` picks the middle commit. You test it. Mark it good or bad. Git picks the next middle. Repeat.

It finds the exact commit that introduced the bug in `O(log n)` test runs instead of `O(n)`. For a project with 1024 commits between "good" and HEAD, you find the culprit in 10 test runs.

### IP Routing Table Lookup

Routers forward packets using routing tables: sorted lists of network prefixes and their associated outgoing interfaces. Finding which prefix matches an incoming packet's destination IP is longest prefix match, implemented with binary search through sorted prefix tables. This runs millions of times per second per router. The efficiency difference between O(n) and O(log n) directly determines how many packets a router can handle.

### Deployment Rollback

Which deployment broke production? Binary search over your deployment history. Mark old deployments as "working" and recent ones as "broken." Test the middle deployment. In 10 binary search steps, you've narrowed the culprit from 1024 deployments to 1. Many deployment platforms expose this as a feature. It's the same insight as `git bisect`, applied to production systems.

### Resource Allocation

"What is the minimum number of servers needed to handle this load?" Binary search on server count. Write a simulation that checks if N servers can handle the load. Binary search finds the minimum N where the simulation passes. This is Template 3a applied to infrastructure planning. The same pattern applies to thread pool sizing, cache capacity planning, and replica count in distributed systems.

### Compiler Optimization

Register allocation during compilation uses binary search to find the optimal point at which to "spill" registers to memory. Instruction scheduling algorithms use binary search on pipeline depth constraints. These aren't textbook binary searches, but the same monotonic-predicate insight is applied: "what is the minimum resource budget at which compilation succeeds?"

---

## 6. Problem Categories and Solutions

---

### Category A: Classic Binary Search

These problems use Template 1 directly, or Template 2 for exact position queries.

---

#### LC 704: Binary Search (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe, Oracle

**Problem:** Given a sorted array `nums` of distinct integers and a target, return the index of target or -1.

**Approach:** Direct application of Template 1. This is the template problem itself. If you're not solving this in under 90 seconds, practice Template 1 until it's mechanical.

```java
class Solution {
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }
}
```

**Time:** O(log n) | **Space:** O(1)

**Edge cases:**
- `nums = [5]`, target = 5: loop runs once, returns 0
- `nums = [5]`, target = 3: loop runs once (left=0 <= right=0), mid=0, arr[0]=5 > 3, right=-1. Next check: left=0 > right=-1, exits. Returns -1.
- target smaller than all elements: left reaches 0 then exits when right becomes -1
- target larger than all elements: right reaches nums.length-1 then left overshoots it

---

#### LC 34: Find First and Last Position of Element in Sorted Array (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe, LinkedIn, Goldman Sachs

**Problem:** Given a sorted array and target, return `[first_index, last_index]`. Return `[-1, -1]` if not found.

**Approach:** Two binary searches. Lower bound for first occurrence, upper bound for last occurrence. This is the canonical use case for Template 2.

```java
class Solution {
    public int[] searchRange(int[] nums, int target) {
        int first = lowerBound(nums, target);

        // If lower bound is out of range, or points to a different value: not found
        if (first == nums.length || nums[first] != target) {
            return new int[]{-1, -1};
        }

        // Upper bound gives first index > target, so last = upper - 1
        int last = upperBound(nums, target) - 1;

        return new int[]{first, last};
    }

    // First index where nums[i] >= target
    private int lowerBound(int[] nums, int target) {
        int left = 0, right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    // First index where nums[i] > target
    private int upperBound(int[] nums, int target) {
        int left = 0, right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

**Dry Run on `nums = [5, 7, 7, 8, 8, 10]`, target = 8:**

Lower bound (finding first 8):

```
left=0, right=6

Step 1: mid=3 → nums[3]=8 >= 8 → right = 3
Step 2: left=0, right=3, mid=1 → nums[1]=7 < 8 → left = 2
Step 3: left=2, right=3, mid=2 → nums[2]=7 < 8 → left = 3
Step 4: left=3, right=3 → exit

lowerBound returns 3
```

Upper bound (finding first element strictly greater than 8):

```
left=0, right=6

Step 1: mid=3 → nums[3]=8 <= 8 → left = 4
Step 2: left=4, right=6, mid=5 → nums[5]=10 > 8 → right = 5
Step 3: left=4, right=5, mid=4 → nums[4]=8 <= 8 → left = 5
Step 4: left=5, right=5 → exit

upperBound returns 5
last = 5 - 1 = 4
```

Result: `[3, 4]`. Check: `nums[3]=8, nums[4]=8`. Correct.

**Dry Run on `nums = [5, 7, 7, 8, 8, 10]`, target = 6 (not present):**

Lower bound:

```
Step 1: mid=3 → nums[3]=8 >= 6 → right = 3
Step 2: left=0, right=3, mid=1 → nums[1]=7 >= 6 → right = 1
Step 3: left=0, right=1, mid=0 → nums[0]=5 < 6 → left = 1
Step 4: left=1, right=1 → exit

lowerBound returns 1
nums[1] = 7 ≠ 6 → return [-1, -1]
```

Result: `[-1, -1]`. Correct.

**Time:** O(log n) | **Space:** O(1)

---

#### LC 35: Search Insert Position (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given a sorted array and target, return the index if found, or the index where it would be inserted to maintain sorted order.

**Approach:** This is exactly `lowerBound`. Template 2, return position directly. The position returned is the first index where `nums[i] >= target`. If target is in the array, that's its first occurrence. If not, that's the insertion point.

```java
class Solution {
    public int searchInsert(int[] nums, int target) {
        int left = 0, right = nums.length;  // right = length, not length-1

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

**Time:** O(log n) | **Space:** O(1)

**Examples:**
- `nums = [1, 3, 5, 6]`, target = 5: returns 2 (found)
- `nums = [1, 3, 5, 6]`, target = 2: returns 1 (would be inserted between 1 and 3)
- `nums = [1, 3, 5, 6]`, target = 7: returns 4 = nums.length (larger than all)
- `nums = [1, 3, 5, 6]`, target = 0: returns 0 (smaller than all)

---

#### LC 74: Search a 2D Matrix (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Search in an `m x n` matrix where each row is sorted left-to-right, and the first element of each row is greater than the last element of the previous row. Return true if target is found.

**Approach:** The matrix is conceptually a single sorted array of `m * n` elements. Binary search on indices `[0, m*n - 1]`. Convert flat index to 2D: `row = mid / n`, `col = mid % n`.

```java
class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int left = 0, right = m * n - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int value = matrix[mid / n][mid % n];

            if (value == target) {
                return true;
            } else if (value < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }
}
```

**Time:** O(log(m * n)) | **Space:** O(1)

**The index conversion trick:**

For a 3x4 matrix (rows 0-2, cols 0-3), flat index 7 maps to:
- `row = 7 / 4 = 1`
- `col = 7 % 4 = 3`

That's `matrix[1][3]`. This trick appears in multiple matrix problems. Memorize it.

**Why Template 1 here?** Because we want to return true/false (exact match), not an insertion point. Template 1's -1 return maps cleanly to false.

---

### Category B: Modified Array Search

These problems apply binary search to arrays that aren't uniformly sorted. The predicate is more subtle, but the framework is identical.

---

#### LC 33: Search in Rotated Sorted Array (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe, Uber, Goldman Sachs, LinkedIn

**Problem:** A sorted array is rotated at some unknown pivot. Find a target. Return index or -1. Assume distinct elements.

**The key insight:** Even after rotation, one half of the array at any mid point is always fully sorted. Either `[left, mid]` is sorted, or `[mid, right]` is sorted. Determine which half is sorted, check if target falls in it, and eliminate the other half.

**How to determine which half is sorted:** Compare `nums[left]` with `nums[mid]`.
- `nums[left] <= nums[mid]`: left half is sorted (no rotation point in it)
- `nums[left] > nums[mid]`: right half is sorted (rotation point is in left half)

```java
class Solution {
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            }

            if (nums[left] <= nums[mid]) {
                // Left half [left..mid] is sorted
                if (nums[left] <= target && target < nums[mid]) {
                    // Target falls within the sorted left half
                    right = mid - 1;
                } else {
                    // Target is in the right half (or absent)
                    left = mid + 1;
                }
            } else {
                // Right half [mid..right] is sorted
                if (nums[mid] < target && target <= nums[right]) {
                    // Target falls within the sorted right half
                    left = mid + 1;
                } else {
                    // Target is in the left half (or absent)
                    right = mid - 1;
                }
            }
        }

        return -1;
    }
}
```

**Dry Run on `nums = [4, 5, 6, 7, 0, 1, 2]`, target = 0:**

```
Initial: left=0, right=6

Step 1: mid=3, nums[3]=7, not target
  nums[left]=4 <= nums[mid]=7 → left half [4,5,6,7] is sorted
  Is 4 <= 0 < 7? No → target in right half → left = 4

Step 2: left=4, right=6, mid=5, nums[5]=1, not target
  nums[left]=nums[4]=0 <= nums[mid]=1 → left half [0,1] is sorted
  Is 0 <= 0 < 1? Yes → target in left half → right = 4

Step 3: left=4, right=4, mid=4, nums[4]=0 == target → return 4
```

Result: 4. Correct.

**Dry Run on `nums = [4, 5, 6, 7, 0, 1, 2]`, target = 3:**

```
Initial: left=0, right=6

Step 1: mid=3, nums[3]=7, not target
  Left half sorted: [4,5,6,7]
  Is 4 <= 3 < 7? No → go right → left = 4

Step 2: left=4, right=6, mid=5, nums[5]=1, not target
  Left half sorted: [0,1]
  Is 0 <= 3 < 1? No → go right → left = 6

Step 3: left=6, right=6, mid=6, nums[6]=2, not target
  Left half sorted: [2] (single element)
  Is 2 <= 3 < 2? No → go right → left = 7

Step 4: left=7 > right=6 → exit

return -1
```

Result: -1. Correct.

**Why `nums[left] <= nums[mid]` with `<=` (not `<`)?**

When `left == mid` (single element left half), you still classify it as "sorted" to handle the edge case correctly. A single element is trivially sorted.

**Time:** O(log n) | **Space:** O(1)

---

#### LC 153: Find Minimum in Rotated Sorted Array (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, LinkedIn

**Problem:** Find the minimum element in a rotated sorted array. Assume distinct elements.

**The key insight:** Compare `nums[mid]` with `nums[right]`, not `nums[left]`. The minimum is the "disruption point" where the sorted order breaks. If `nums[mid] > nums[right]`, the disruption (minimum) is in the right half. Otherwise it's in the left half, and mid itself could be the minimum.

**Why compare with right, not left?**

If the array is not rotated at all (e.g., `[1, 2, 3, 4, 5]`), `nums[mid]` is always <= `nums[right]`, so `right` shrinks toward 0. Returns `nums[0]`. Correct.

Comparing with left creates ambiguity when the array is fully sorted: `nums[left] <= nums[mid]` is true, but it's also true in a rotated array where left half is sorted. The two cases are indistinguishable without additional context.

```java
class Solution {
    public int findMin(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                // Minimum is in the right half (mid is not the minimum)
                left = mid + 1;
            } else {
                // Minimum is at mid or in the left half
                right = mid;
            }
        }

        return nums[left];  // left == right
    }
}
```

**Time:** O(log n) | **Space:** O(1)

**Trace on `nums = [3, 4, 5, 1, 2]`:**

```
left=0, right=4, mid=2, nums[2]=5 > nums[4]=2 → left = 3
left=3, right=4, mid=3, nums[3]=1 < nums[4]=2 → right = 3
left=3, right=3 → exit

return nums[3] = 1
```

Result: 1. Correct.

---

#### LC 162: Find Peak Element (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** A peak element is one greater than its neighbors. Return any peak index. Assume `nums[-1] = nums[n] = -infinity`. For any two adjacent elements, `nums[i] != nums[i+1]`.

**The key insight:** Binary search on a non-sorted array. The trick: if `nums[mid] < nums[mid + 1]`, there is a peak to the right of mid (the slope is ascending). Otherwise, there's a peak at mid or to the left. This directional information is the monotonic property that makes binary search work here.

**Why does following the ascending slope always lead to a peak?**

The problem guarantees `nums[-1] = nums[n] = -infinity`. Any sequence starting on an ascending slope must eventually come down (because it eventually reaches -infinity). So following the ascending slope guarantees you'll reach a local maximum, not fall off the array.

```java
class Solution {
    public int findPeakElement(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] < nums[mid + 1]) {
                // Ascending slope: peak is strictly to the right of mid
                left = mid + 1;
            } else {
                // Descending slope: peak is at mid or to the left
                right = mid;
            }
        }

        return left;  // left == right: this is a peak
    }
}
```

**Time:** O(log n) | **Space:** O(1)

**Note:** This problem accepts any valid peak, so multiple correct answers exist. Binary search finds one of them.

---

#### LC 540: Single Element in a Sorted Array (Medium)

**Companies:** Amazon, Google, Microsoft, Bloomberg

**Problem:** Every element in a sorted array appears exactly twice except one. Find the single element. O(log n) required.

**The key insight:** Before the single element, every pair starts at an even index: `nums[0]==nums[1]`, `nums[2]==nums[3]`, etc. After the single element, the pairing shifts: pairs start at odd indices. Binary search on this parity property.

```java
class Solution {
    public int singleNonDuplicate(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Force mid to be even so we compare (mid, mid+1) pair
            if (mid % 2 == 1) mid--;

            if (nums[mid] == nums[mid + 1]) {
                // This pair is intact: single element is to the right
                left = mid + 2;
            } else {
                // This pair is disrupted: single element is at mid or to the left
                right = mid;
            }
        }

        return nums[left];
    }
}
```

**Trace on `nums = [1, 1, 2, 3, 3, 4, 4, 8, 8]`:**

```
left=0, right=8

Step 1: mid=4, force even: mid=4, nums[4]=3 == nums[5]=4? No.
  Pair disrupted → right = 4

Step 2: left=0, right=4, mid=2, force even: mid=2, nums[2]=2 == nums[3]=3? No.
  Pair disrupted → right = 2

Step 3: left=0, right=2, mid=1, force even: mid=0, nums[0]=1 == nums[1]=1? Yes.
  Pair intact → left = 2

Step 4: left=2, right=2 → exit

return nums[2] = 2
```

Result: 2. Correct.

**Time:** O(log n) | **Space:** O(1)

---

#### LC 81: Search in Rotated Sorted Array II (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Same as LC 33 but the array may contain duplicates. Return true or false.

**The complication:** When `nums[left] == nums[mid]`, you can't determine which half is sorted. Example: `[1, 1, 1, 2, 1]` vs `[1, 2, 1, 1, 1]`. At mid, both show `nums[left] == nums[mid] == 1`, but the rotation is different.

**Solution:** When the ambiguous case occurs, increment left and decrement right. This degrades worst case to O(n) when all elements are duplicates.

```java
class Solution {
    public boolean search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) return true;

            // Ambiguous case: can't determine sorted half
            if (nums[left] == nums[mid] && nums[mid] == nums[right]) {
                left++;
                right--;
            } else if (nums[left] <= nums[mid]) {
                // Left half is sorted
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                // Right half is sorted
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return false;
    }
}
```

**Time:** O(log n) average, O(n) worst case | **Space:** O(1)

**When is worst case O(n)?** Input like `[1, 1, 1, 1, 1, 1, 1, 2, 1, 1]`. Every mid shows duplicates at both boundaries. You increment left and decrement right each iteration, making O(n) total passes.

---

### Category C: Binary Search on Answer

This is the category that separates strong candidates from exceptional ones. The pattern is always the same: the answer lies in some range, and for a given candidate answer, you can check feasibility in O(n) time. Binary search reduces the total candidates checked from O(answer_range) to O(log(answer_range)).

The structure of every problem in this category follows the same scaffold:

```java
// 1. Define answer space
int lo = minimumPossibleAnswer;
int hi = maximumPossibleAnswer;

// 2. Binary search for the boundary
while (lo < hi) {
    int mid = lo + (hi - lo) / 2;

    if (canAchieve(mid, /* problem params */)) {
        hi = mid;       // mid satisfies, but smaller might too
    } else {
        lo = mid + 1;   // mid fails, need something bigger
    }
}

// 3. lo == hi: the minimum valid answer
return lo;
```

The work is in defining `lo`, `hi`, and `canAchieve`. The binary search itself is mechanical.

---

#### LC 875: Koko Eating Bananas (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Citadel, DoorDash, Goldman Sachs (41 companies)

**Problem:** Koko has `piles` of bananas and `h` hours. Each hour she eats up to `k` bananas from one pile (she stops that pile if she finishes early). Find the minimum `k` that lets her finish all piles in `h` hours.

**Why binary search on answer?**

If Koko can finish with speed `k`, she can definitely finish with speed `k+1` (she'd finish each pile faster or at the same rate). So `canFinish(k)` is monotonically: `[false, false, ..., true, true, true]`. Binary search finds the first `true`.

**Answer space:**
- `lo = 1`: minimum meaningful speed (eating 0 doesn't help)
- `hi = max(piles)`: at this speed, she finishes any single pile in exactly 1 hour, using at most `piles.length` hours total, which is always <= h (since h >= piles.length per constraints)

**Predicate `canFinish(k, piles, h)`:**

For each pile, hours needed = `ceil(pile / k)`. Use integer arithmetic: `(pile + k - 1) / k`. Sum all. If sum <= h, she finishes.

```java
class Solution {
    public int minEatingSpeed(int[] piles, int h) {
        int lo = 1;
        int hi = 0;
        for (int pile : piles) {
            hi = Math.max(hi, pile);
        }

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;

            if (canFinish(mid, piles, h)) {
                hi = mid;       // mid works, search for smaller speed
            } else {
                lo = mid + 1;   // mid is too slow, need faster
            }
        }

        return lo;
    }

    private boolean canFinish(int speed, int[] piles, int h) {
        int totalHours = 0;
        for (int pile : piles) {
            totalHours += (pile + speed - 1) / speed;  // ceiling division
            if (totalHours > h) return false;            // early exit optimization
        }
        return true;
    }
}
```

**Full Dry Run on `piles = [3, 6, 7, 11]`, `h = 8`:**

Answer space: `lo = 1, hi = max(3,6,7,11) = 11`.

```
Iteration 1:
  lo=1, hi=11, mid = 1 + (11-1)/2 = 6
  canFinish(6):
    pile=3:  ceil(3/6)  = 1 hour
    pile=6:  ceil(6/6)  = 1 hour
    pile=7:  ceil(7/6)  = 2 hours
    pile=11: ceil(11/6) = 2 hours
    total = 6 hours ≤ 8 → true
  hi = 6

Iteration 2:
  lo=1, hi=6, mid = 1 + (6-1)/2 = 3
  canFinish(3):
    pile=3:  ceil(3/3)  = 1 hour
    pile=6:  ceil(6/3)  = 2 hours
    pile=7:  ceil(7/3)  = 3 hours (running total = 6)
    pile=11: ceil(11/3) = 4 hours (running total = 10 > 8 → early exit)
    → false
  lo = 4

Iteration 3:
  lo=4, hi=6, mid = 4 + (6-4)/2 = 5
  canFinish(5):
    pile=3:  ceil(3/5)  = 1 hour
    pile=6:  ceil(6/5)  = 2 hours
    pile=7:  ceil(7/5)  = 2 hours
    pile=11: ceil(11/5) = 3 hours
    total = 8 ≤ 8 → true
  hi = 5

Iteration 4:
  lo=4, hi=5, mid = 4 + (5-4)/2 = 4
  canFinish(4):
    pile=3:  ceil(3/4)  = 1 hour
    pile=6:  ceil(6/4)  = 2 hours
    pile=7:  ceil(7/4)  = 2 hours
    pile=11: ceil(11/4) = 3 hours
    total = 8 ≤ 8 → true
  hi = 4

lo=4, hi=4 → loop exits (lo == hi)
return 4
```

**Verification:** Speed 4, hours used: 1+2+2+3 = 8. Exactly h. Speed 3 needs 10 hours (too slow). Minimum is 4. Correct.

**Why `(pile + speed - 1) / speed` for ceiling?**

For integer `a` and divisor `d`, ceiling division is `(a + d - 1) / d`. This avoids converting to double and using `Math.ceil`. For `pile=7, speed=3`: `(7+2)/3 = 9/3 = 3`. Check: `ceil(7/3) = 3`. Correct.

**Time:** O(n * log(max(piles))) | **Space:** O(1)

---

#### LC 1011: Capacity To Ship Packages Within D Days (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Ship `weights[]` packages in `d` days. Packages are loaded in order; ship can carry at most `capacity` weight per day. Find minimum capacity to ship all packages within d days.

**Same structure as Koko.** The predicate `canShip(capacity)`:

Simulate loading packages greedily: for each package, if adding it would exceed capacity, start a new day. Count days used. If days <= d, feasible.

**Answer space:**
- `lo = max(weights)`: must fit the heaviest single package in one day
- `hi = sum(weights)`: carry everything in one day (worst case is always feasible if this is our bound)

```java
class Solution {
    public int shipWithinDays(int[] weights, int days) {
        int lo = 0, hi = 0;
        for (int w : weights) {
            lo = Math.max(lo, w);   // must fit heaviest package
            hi += w;                // one day: carry everything
        }

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;

            if (canShip(mid, weights, days)) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }

        return lo;
    }

    private boolean canShip(int capacity, int[] weights, int days) {
        int daysNeeded = 1, currentLoad = 0;

        for (int w : weights) {
            if (currentLoad + w > capacity) {
                daysNeeded++;
                currentLoad = 0;
                if (daysNeeded > days) return false;  // early exit
            }
            currentLoad += w;
        }

        return true;
    }
}
```

**Time:** O(n * log(sum(weights))) | **Space:** O(1)

**Note on bounds:** Setting `lo = max(weights)` is correct because if capacity < max(weights), there exists a package that can't be shipped in a single day, which is impossible (each package must ship on some day). Setting `hi = sum(weights)` guarantees feasibility (1 day, ship everything).

---

#### LC 410: Split Array Largest Sum (Hard)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Split `nums` into exactly `k` non-empty subarrays. Minimize the largest subarray sum.

**Why binary search on answer?**

The answer is the maximum subarray sum in the optimal split. If a maximum sum of X is achievable, then maximum sum of X+1 is also achievable (same split, just a looser constraint). Predicate `canSplit(maxSum)`: can you split nums into at most k subarrays where each sum <= maxSum?

**Answer space:**
- `lo = max(nums)`: every element must fit in some subarray, so max element is a lower bound
- `hi = sum(nums)`: one subarray containing everything

```java
class Solution {
    public int splitArray(int[] nums, int k) {
        long lo = 0, hi = 0;
        for (int n : nums) {
            lo = Math.max(lo, n);
            hi += n;
        }

        while (lo < hi) {
            long mid = lo + (hi - lo) / 2;

            if (canSplit(mid, nums, k)) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }

        return (int) lo;
    }

    private boolean canSplit(long maxSum, int[] nums, int k) {
        int subarrays = 1;
        long currentSum = 0;

        for (int n : nums) {
            if (currentSum + n > maxSum) {
                subarrays++;
                currentSum = 0;
                if (subarrays > k) return false;
            }
            currentSum += n;
        }

        return true;
    }
}
```

**Note on `long`:** The sum of all elements can exceed `int` range (up to `10^4 * 10^9 = 10^13`). Use `long` for `lo`, `hi`, `mid`, and sum accumulations.

**Time:** O(n * log(sum(nums))) | **Space:** O(1)

**The greedy predicate:** The `canSplit` function greedily packs as many elements as possible into each subarray without exceeding `maxSum`. This greedy approach is correct because packing more elements into earlier subarrays only helps (it gives more room for later subarrays, never hurting feasibility).

---

#### LC 1283: Find the Smallest Divisor Given a Threshold (Medium)

**Companies:** Amazon, Google, Microsoft

**Problem:** Find the smallest positive integer divisor such that `sum(ceil(nums[i] / divisor))` for all `i` is at most `threshold`.

**Answer space:**
- `lo = 1`: smallest possible divisor
- `hi = max(nums)`: dividing everything by max(nums) gives sum = nums.length, which is <= threshold per constraints

```java
class Solution {
    public int smallestDivisor(int[] nums, int threshold) {
        int lo = 1, hi = 0;
        for (int n : nums) hi = Math.max(hi, n);

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;

            if (getSum(nums, mid) <= threshold) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }

        return lo;
    }

    private int getSum(int[] nums, int divisor) {
        int sum = 0;
        for (int n : nums) {
            sum += (n + divisor - 1) / divisor;
        }
        return sum;
    }
}
```

**Time:** O(n * log(max(nums))) | **Space:** O(1)

**Recognizing the pattern:** Any problem with "minimum divisor/speed/capacity/workers such that sum/total <= threshold" is search on answer. The predicate is always: compute the total with the candidate value, check against threshold.

---

#### LC 69: Sqrt(x) (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Find the integer square root of x (floor). Return the largest integer `n` where `n*n <= x`.

**Approach:** Binary search on answer space `[0, x]`. Find maximum `n` where `n*n <= x`. This is Template 3b (find maximum satisfying answer), so use ceiling mid.

```java
class Solution {
    public int mySqrt(int x) {
        if (x == 0) return 0;

        long lo = 1, hi = x;

        while (lo < hi) {
            long mid = lo + (hi - lo + 1) / 2;  // Ceiling: maximum variant

            if (mid * mid <= x) {
                lo = mid;       // mid is valid, but larger might also work
            } else {
                hi = mid - 1;   // mid is too large
            }
        }

        return (int) lo;
    }
}
```

**Why `long`?** For `x = Integer.MAX_VALUE = 2,147,483,647`, the answer is about 46341. `mid` can reach up to x = 2,147,483,647. `mid * mid` for mid near 46341 is about 2.1 billion, which is near `int` max. For larger mid values, it overflows. Using `long` prevents this.

**Why `hi = x` (not `hi = x/2`)?**

For `x = 1`: `x/2 = 0`, so `lo = 1, hi = 0`. The loop never executes. Return `lo = 1`. But that's wrong: `sqrt(1) = 1`. The `if (x == 0) return 0` handles x=0, and `hi = x` handles x=1 correctly: `lo=1, hi=1`, loop exits immediately, returns 1. You could use `hi = x/2 + 1` as an optimization but it adds complexity for little gain in an interview setting.

**Time:** O(log x) | **Space:** O(1)

---

#### LC 4: Median of Two Sorted Arrays (Hard)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, Adobe

**Problem:** Given two sorted arrays `nums1` and `nums2`, find the median of their combined elements. Required time: O(log(m+n)).

**Why this is hard:**

The naive approach (merge then find middle) is O(m+n). The constraint forces a binary search approach. The solution requires binary searching on a partition point, not on values. This is a conceptually different use of binary search from all prior problems.

**The Core Idea:**

The median divides the combined array into two halves. Instead of merging, binary search for a partition of `nums1` and `nums2` such that all elements in the "left group" are <= all elements in the "right group," and the left group has exactly `(m + n + 1) / 2` elements.

Let `i` = number of elements taken from `nums1` for the left group. Then `j = half - i` elements come from `nums2`. Binary search on `i` in `[0, m]`.

**Valid partition condition:**

A partition `(i, j)` is valid when:
- `nums1[i-1] <= nums2[j]`: last element of nums1's left portion <= first of nums2's right portion
- `nums2[j-1] <= nums1[i]`: last element of nums2's left portion <= first of nums1's right portion

If `nums1[i-1] > nums2[j]`: nums1's left portion is too large. Decrease `i` (move i left).
If `nums2[j-1] > nums1[i]`: nums2's left portion is too large. Increase `i` (which decreases `j`).

**Edge case handling:**

When `i = 0`, there's no `nums1[i-1]`. Use `Integer.MIN_VALUE` (effectively -infinity).
When `i = m`, there's no `nums1[i]`. Use `Integer.MAX_VALUE` (effectively +infinity).
Same for `j = 0` and `j = n`.

```java
class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        // Always binary search on the shorter array for O(log(min(m,n)))
        if (nums1.length > nums2.length) {
            return findMedianSortedArrays(nums2, nums1);
        }

        int m = nums1.length, n = nums2.length;
        int half = (m + n + 1) / 2;  // size of left group

        int lo = 0, hi = m;  // i ranges over [0, m]

        while (lo <= hi) {
            int i = lo + (hi - lo) / 2;  // elements from nums1 in left group
            int j = half - i;             // elements from nums2 in left group

            // Boundary values (with infinity substitution for edges)
            int nums1Left  = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1];
            int nums1Right = (i == m) ? Integer.MAX_VALUE : nums1[i];
            int nums2Left  = (j == 0) ? Integer.MIN_VALUE : nums2[j - 1];
            int nums2Right = (j == n) ? Integer.MAX_VALUE : nums2[j];

            if (nums1Left <= nums2Right && nums2Left <= nums1Right) {
                // Valid partition found
                int maxLeft  = Math.max(nums1Left, nums2Left);
                int minRight = Math.min(nums1Right, nums2Right);

                if ((m + n) % 2 == 0) {
                    return (maxLeft + minRight) / 2.0;
                } else {
                    return maxLeft;
                }

            } else if (nums1Left > nums2Right) {
                // nums1's left side is too large: move i left
                hi = i - 1;
            } else {
                // nums2's left side is too large: move i right
                lo = i + 1;
            }
        }

        throw new IllegalArgumentException("Input arrays are not sorted");
    }
}
```

**Detailed walkthrough on `nums1 = [1, 3]`, `nums2 = [2, 4, 5, 6]`:**

Total = 6, half = 3. Binary search on i in [0, 2].

```
lo=0, hi=2

Step 1: i=1, j=2
  nums1Left=nums1[0]=1, nums1Right=nums1[1]=3
  nums2Left=nums2[1]=4, nums2Right=nums2[2]=5
  Check: nums1Left(1) <= nums2Right(5)? Yes.
  Check: nums2Left(4) <= nums1Right(3)? NO. 4 > 3.
  nums2's left is too large → need more elements from nums1 → lo = i+1 = 2

Step 2: lo=2, hi=2, i=2, j=1
  nums1Left=nums1[1]=3, nums1Right=MAX_VALUE (i==m)
  nums2Left=nums2[0]=2, nums2Right=nums2[1]=4
  Check: nums1Left(3) <= nums2Right(4)? Yes.
  Check: nums2Left(2) <= nums1Right(MAX)? Yes.
  Valid partition!

  maxLeft  = max(3, 2) = 3
  minRight = min(MAX, 4) = 4
  Total=6 is even: median = (3 + 4) / 2.0 = 3.5
```

Result: 3.5. Correct (merged array: [1, 2, 3, 4, 5, 6], median = (3+4)/2).

**Walkthrough on `nums1 = [1, 2]`, `nums2 = [3, 4]`:**

Total = 4, half = 2.

```
lo=0, hi=2

Step 1: i=1, j=1
  nums1Left=1, nums1Right=2
  nums2Left=3, nums2Right=4
  Check: 1 <= 4? Yes. 3 <= 2? NO.
  nums2Left too large → lo = 2

Step 2: lo=2, hi=2, i=2, j=0
  nums1Left=nums1[1]=2, nums1Right=MAX (i==m)
  nums2Left=MIN (j==0), nums2Right=nums2[0]=3
  Check: 2 <= 3? Yes. MIN <= MAX? Yes.
  Valid!
  maxLeft = max(2, MIN) = 2
  minRight = min(MAX, 3) = 3
  Total=4 is even: (2+3)/2.0 = 2.5
```

Result: 2.5. Correct.

**Why always binary search on the shorter array?**

`j = half - i`. If `nums1` is longer (m > n), then with i=0, j = half = (m+n+1)/2 > n, which is out of bounds for nums2. Swapping ensures m <= n, so j = half - i is always valid.

**Time:** O(log(min(m, n))) | **Space:** O(1)

This problem is worth understanding deeply. The partition insight (binary search over partition points rather than values) appears in several variants. If you can explain *why* the partition is valid and *why* comparing with `nums2Right` tells you to move `i` left, you've demonstrated binary search mastery that most candidates don't reach.

---

### Category D: Advanced Problems

---

#### LC 378: Kth Smallest Element in a Sorted Matrix (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given an `n x n` matrix where each row and column is sorted in ascending order, find the kth smallest element.

**Approach:** Binary search on value. The answer is some value in `[matrix[0][0], matrix[n-1][n-1]]`. For a candidate value `mid`, count how many matrix elements are <= mid. If count < k, the answer is larger. If count >= k, the answer is at most mid.

**Counting elements <= mid in O(n):**

Start at the bottom-left corner. If `matrix[row][col] <= mid`, all elements in that column from row 0 to row are <= mid. Add `row + 1` to count, move right. If `matrix[row][col] > mid`, move up. Each step either advances the column or decreases the row, so at most `2n` steps total.

```java
class Solution {
    public int kthSmallest(int[][] matrix, int k) {
        int n = matrix.length;
        int lo = matrix[0][0], hi = matrix[n-1][n-1];

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            int count = countLessEqual(matrix, mid, n);

            if (count < k) {
                lo = mid + 1;   // Not enough elements <= mid, answer is larger
            } else {
                hi = mid;       // Enough elements, answer might be mid or smaller
            }
        }

        return lo;  // lo is guaranteed to be an actual matrix value
    }

    private int countLessEqual(int[][] matrix, int target, int n) {
        int count = 0;
        int row = n - 1, col = 0;  // Start at bottom-left

        while (row >= 0 && col < n) {
            if (matrix[row][col] <= target) {
                count += row + 1;  // All elements in this column from [0][col] to [row][col]
                col++;
            } else {
                row--;
            }
        }

        return count;
    }
}
```

**Time:** O(n * log(max - min)) | **Space:** O(1)

**Why is `lo` at termination guaranteed to be an actual matrix value?**

The count function has "jumps" only at actual matrix values. If `lo` converges to a value not in the matrix, the count would be the same at that value and at the nearest smaller matrix value, meaning binary search would have settled on the smaller one. The argument is subtle but the conclusion holds: the answer is always a valid matrix element.

---

#### LC 287: Find the Duplicate Number (Medium)

This problem was covered in Topic 3 (Fast and Slow Pointers) using Floyd's cycle detection algorithm, which achieves O(n) time with O(1) space. Worth noting that it can also be solved with binary search:

Binary search on value in `[1, n]`. For a candidate value `mid`, count how many elements in `nums` are <= mid. By the pigeonhole principle, if `count > mid`, there's a duplicate in `[1, mid]`. Otherwise the duplicate is in `(mid, n]`.

```java
// Binary search approach (O(n log n), not optimal but educational)
public int findDuplicate(int[] nums) {
    int lo = 1, hi = nums.length - 1;

    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        int count = 0;
        for (int n : nums) {
            if (n <= mid) count++;
        }
        if (count > mid) {
            hi = mid;   // Duplicate is in [lo, mid]
        } else {
            lo = mid + 1;
        }
    }

    return lo;
}
```

Floyd's is strictly better here (O(n) vs O(n log n)). The binary search approach illustrates that one problem can have multiple algorithmic solutions. Knowing both solutions and articulating the tradeoffs is the sign of a well-prepared candidate.

---

#### LC 668: Kth Smallest Number in Multiplication Table (Hard)

**Companies:** Google, Amazon

**Problem:** Given an `m x n` multiplication table (where `table[i][j] = i * j`, 1-indexed), find the kth smallest entry.

**Approach:** Binary search on value in `[1, m*n]`. For a candidate value `mid`, count how many entries in the table are <= mid. For row `i` (1-indexed), values are `i, 2i, 3i, ..., n*i`. The count of values <= mid in row i is `min(mid / i, n)`. Sum across all rows.

```java
class Solution {
    public int findKthNumber(int m, int n, int k) {
        int lo = 1, hi = m * n;

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            int count = countLessEqual(mid, m, n);

            if (count < k) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }

        return lo;
    }

    private int countLessEqual(int mid, int m, int n) {
        int count = 0;
        for (int i = 1; i <= m; i++) {
            // Row i has values: i, 2i, 3i, ..., n*i
            // Values <= mid: i, 2i, ..., floor(mid/i)*i
            // Count = min(mid/i, n)
            count += Math.min(mid / i, n);
        }
        return count;
    }
}
```

**Time:** O(m * log(m*n)) | **Space:** O(1)

**Why does binary search converge to an actual table entry?**

Same argument as LC 378. The count function is a step function that only changes at actual table values (multiples of 1 through m). Binary search converges to the exact kth value.

---

## 7. Common Mistakes and Edge Cases

### Common Mistakes

| Mistake | Why It Happens | How to Fix |
|---------|---------------|------------|
| Integer overflow in mid | `(left + right)` overflows int for large values | Always use `left + (right - left) / 2` |
| Infinite loop: `right = mid - 1` in Template 2 | Not realizing mid could be the answer | In Template 2, when `arr[mid] >= target`, use `right = mid` not `right = mid - 1` |
| Infinite loop: `lo = mid` in Template 3a | Using max-variant mid (ceiling) for min-variant | Template 3a (find minimum) uses floor mid; Template 3b (find maximum) uses ceiling |
| Off-by-one in right initialization | Using `arr.length - 1` when `arr.length` is a valid answer | For lower/upper bound, use `right = arr.length` |
| Wrong half in rotated minimum | Comparing `nums[mid]` with `nums[left]` instead of `nums[right]` | For finding minimum in rotated array: compare `nums[mid]` with `nums[right]` |
| Non-monotonic predicate | Applying binary search when the predicate isn't monotonic | Draw out predicate values for small inputs before coding |
| Missing duplicates handling | Standard binary search assumes unique values | For duplicate arrays, either handle the `nums[left] == nums[mid]` ambiguity or accept O(n) worst case |
| Overflow in predicate | `mid * mid` or sum accumulation overflows int | Use `long` for any multiplication or sum in the predicate function |
| Wrong bounds for answer space | `lo` or `hi` too tight, excluding the valid answer | Think carefully: what's the smallest valid answer? Largest? Test edge inputs mentally. |
| `while (left <= right)` in Template 2/3 | Copy-pasting Template 1 | Template 2 and Template 3 use `while (left < right)`. Template 1 uses `while (left <= right)`. Different termination semantics. |

### Edge Cases to Always Test

**Empty array:**
Most binary search problems assume non-empty arrays. But if the constraint says `1 <= nums.length`, you're safe. Always read the constraints.

**Single element:**
`left = right = 0`. Template 1's loop runs once with `mid = 0`. Template 2's loop exits immediately (left == right already). Template 3's loop also exits immediately. Make sure your return value is correct for this case.

**Target smaller than all elements:**
Template 1 returns -1 (correct). Template 2's lower bound returns 0 (correct: insertion point is before everything). Template 2's upper bound also returns 0.

**Target larger than all elements:**
Template 1 returns -1 (correct). Template 2's lower bound returns `arr.length` (correct: insertion point after everything). This is why right must be initialized to `arr.length`.

**All elements identical:**
- LC 34: `lowerBound = 0`, `upperBound = arr.length`. Both binary searches work correctly.
- LC 33: every mid comparison is ambiguous for LC 81. The `nums[left] == nums[mid] == nums[right]` branch handles this.
- LC 540: works correctly since parity-based logic doesn't depend on value comparison.

**Array of size 2:**
`left = 0, right = 1`. Template 1: `mid = 0`. Template 2: `mid = 0`. Check mid, update one pointer. Loop exits. Single-element search space. Common off-by-one source. Trace manually.

**Search space of size 1:**
`lo == hi` at the start. Template 3's `while (lo < hi)` loop never executes. Returns `lo`. This is correct only if your `lo` initialization is the right answer for this degenerate case. Always verify.

**Integer range boundaries:**
When answer space spans most of int range (`lo = 0, hi = Integer.MAX_VALUE`), use `long` for mid: `long mid = (long) lo + (hi - lo) / 2`. The calculation `(long) lo + ((long) hi - lo) / 2` is safer still.

### The Infinite Loop Checklist

Before submitting, verify:

1. Does every branch reduce the search space?
   - `lo = mid + 1`: yes, strictly increases lo
   - `hi = mid - 1`: yes, strictly decreases hi
   - `lo = mid`: only safe if ceiling mid is used (Template 3b)
   - `hi = mid`: only safe if the branch has `hi = mid` (not `hi = mid - 1`), and floor mid is used (Template 2, Template 3a)

2. Are the loop condition and termination semantics consistent?
   - `while (lo <= hi)`: terminates when `lo > hi`. Use Template 1.
   - `while (lo < hi)`: terminates when `lo == hi`. Use Template 2 and 3.

3. For Template 3b (find maximum), is ceiling mid used?
   Test: set `lo = k, hi = k+1`. Trace one iteration. If the state doesn't change, you have infinite loop.

---

## 8. Pattern Comparison

### Binary Search vs Topics 1-3

| Aspect | Binary Search | Two Pointers (Topic 2) | Sliding Window (Topic 3) |
|--------|--------------|------------------------|--------------------------|
| Core requirement | Monotonic predicate | Sorted data or two-pointer invariant | Contiguous subarray |
| Time complexity | O(log n) search, O(n log n) search-on-answer | O(n) | O(n) |
| Space complexity | O(1) | O(1) | O(1) (or O(k) for window contents) |
| Primary use case | Find boundary/threshold in ordered space | Find pairs with sum/distance constraint | Optimize subarray length or sum |
| When it fails | No monotonic structure exists | Non-contiguous elements needed | Non-contiguous or non-subarray answer |
| Data modification | Doesn't work if data changes during search | Works on streaming data if sorted | Works on streaming data |

### Binary Search vs Linear Scan

Binary search wins when:
- Multiple queries on the same static sorted data (pay O(n log n) to sort once, then O(log n) per query)
- The data is already sorted and the dataset is large (n > ~100)
- The search space itself is large (answer-space binary search)

Linear scan wins when:
- Data is unsorted and you're doing a one-time search (sorting would cost more than the search)
- The dataset is tiny (for n < 32, linear scan often beats binary search due to cache locality and branch prediction)
- Sequential memory access is significantly faster than random access on your hardware
- The predicate isn't monotonic and you must check each element

### Binary Search on Answer vs Greedy

Both solve optimization problems ("find minimum X such that..."). They're complementary, not competing.

**Greedy:** The locally optimal choice at each step leads to a globally optimal solution. No evaluation of multiple candidates. O(n) or O(n log n) typically. Requires a correctness proof: why is the greedy choice always globally optimal?

**Binary Search on Answer:** No need to reason about local optimality. You only need to check if a specific candidate answer is feasible. Binary search does the optimization. Requires a predicate that is O(n) or faster and is monotonic.

If a problem has an obvious greedy strategy with a clean proof, use greedy. If you can't find a greedy argument but can write a feasibility check, use binary search on answer. These two patterns often solve the same problem class; binary search on answer is the fallback when the greedy argument is elusive.

### Lower Bound vs Upper Bound Decision Table

| Query | Which to use | Notes |
|-------|--------------|-------|
| Find first occurrence of x | `lowerBound(x)`, verify `arr[result] == x` | Returns n if not found |
| Find last occurrence of x | `upperBound(x) - 1`, verify `arr[result] == x` | Returns -1 if not found |
| Count occurrences of x | `upperBound(x) - lowerBound(x)` | Returns 0 if not found |
| Insertion point for x | `lowerBound(x)` | Maintains sorted order |
| Number of elements < x | `lowerBound(x)` | Same as insertion point |
| Number of elements <= x | `upperBound(x)` | |
| Number of elements > x | `arr.length - upperBound(x)` | |
| Number of elements >= x | `arr.length - lowerBound(x)` | |

### Identifying the Right Template (Interview Decision Process)

When you see a binary search problem in an interview:

1. **Is there an array to search in?** If yes, is the goal finding an exact value or a boundary position?
   - Exact value: Template 1
   - First/last occurrence or insertion point: Template 2

2. **Is the array rotated or otherwise non-uniformly sorted?** Apply Template 1 with "which half is sorted" logic.

3. **Is there no array to binary search in, but an answer range?** That's search on answer. Apply Template 3.

4. **Before coding Template 3:** write `canAchieve` first. Verify monotonicity. Then the binary search loop is just Template 3a or 3b depending on min/max.

---

## 9. Quick Reference Cheat Sheet

### Template Selector Decision Tree

```
Looking for exact target value in sorted array?
  Yes → Template 1 (while left <= right, return -1 if not found)

Looking for insertion point / first or last occurrence?
  Yes → Template 2 (while left < right, right = mid when condition holds)

Looking for minimum X such that condition(X) is true?
  Yes → Template 3a (floor mid, hi = mid when true, lo = mid+1 when false)

Looking for maximum X such that condition(X) is true?
  Yes → Template 3b (ceiling mid, lo = mid when true, hi = mid-1 when false)

Rotated sorted array, find target?
  Yes → Template 1 variant: check which half is sorted at each step

Find peak element?
  Yes → Template 2 variant: compare nums[mid] with nums[mid+1], move toward ascending slope

Rotated sorted array, find minimum?
  Yes → Template 2 variant: compare nums[mid] with nums[right], not nums[left]
```

### Loop Condition Cheat Sheet

| Condition | Terminates when | `right` initialized to | Best for |
|-----------|----------------|------------------------|---------|
| `while (left <= right)` | `left > right` | `arr.length - 1` | Finding exact match |
| `while (left < right)` | `left == right` | `arr.length` or `maxAnswer` | Finding boundary |

**Memory trick:** Template 1 uses `<=` because right starts inside the array (`arr.length - 1`) and the last valid check is when left equals right. Template 2 and 3 use `<` because the loop's purpose is to *converge* to a single value, and convergence is signaled by `left == right`.

### Three Templates Side by Side

```java
// Template 1: Exact match in sorted array
int left = 0, right = n - 1;
while (left <= right) {
    int mid = left + (right - left) / 2;
    if (arr[mid] == target)  return mid;
    if (arr[mid] < target)   left  = mid + 1;
    else                     right = mid - 1;
}
return -1;

// Template 2: Lower bound (first index >= target)
int left = 0, right = n;
while (left < right) {
    int mid = left + (right - left) / 2;
    if (arr[mid] < target)  left  = mid + 1;
    else                    right = mid;
}
return left;   // left == right

// Template 3a: Minimum satisfying answer
int lo = minAnswer, hi = maxAnswer;
while (lo < hi) {
    int mid = lo + (hi - lo) / 2;  // Floor
    if (canAchieve(mid))  hi = mid;
    else                  lo = mid + 1;
}
return lo;   // lo == hi

// Template 3b: Maximum satisfying answer
int lo = minAnswer, hi = maxAnswer;
while (lo < hi) {
    int mid = lo + (hi - lo + 1) / 2;  // Ceiling!
    if (canAchieve(mid))  lo = mid;
    else                  hi = mid - 1;
}
return lo;   // lo == hi
```

### Answer Space Setup for Common "Search on Answer" Problems

| Problem Type | lo | hi |
|-------------|----|----|
| Minimum eating/painting/processing speed | 1 | max(input) |
| Minimum ship/truck capacity | max(weights) | sum(weights) |
| Minimum days/workers/buses | 1 | sum(work) |
| Minimum split maximum | max(nums) | sum(nums) |
| Minimum divisor | 1 | max(nums) |
| Integer square root | 0 | x |
| Kth smallest in sorted matrix | matrix[0][0] | matrix[n-1][n-1] |

### Complexity Summary for All Covered Problems

| Problem | Category | Time | Space |
|---------|----------|------|-------|
| LC 704: Binary Search | Classic | O(log n) | O(1) |
| LC 34: First/Last Position | Classic | O(log n) | O(1) |
| LC 35: Search Insert Position | Classic | O(log n) | O(1) |
| LC 74: Search 2D Matrix | Classic | O(log mn) | O(1) |
| LC 33: Rotated Sorted Array | Modified | O(log n) | O(1) |
| LC 153: Min in Rotated | Modified | O(log n) | O(1) |
| LC 162: Peak Element | Modified | O(log n) | O(1) |
| LC 540: Single Non-Duplicate | Modified | O(log n) | O(1) |
| LC 81: Rotated with Duplicates | Modified | O(log n) avg / O(n) worst | O(1) |
| LC 875: Koko Eating Bananas | Answer | O(n log max(piles)) | O(1) |
| LC 1011: Ship Packages | Answer | O(n log sum(weights)) | O(1) |
| LC 410: Split Array Largest Sum | Answer | O(n log sum(nums)) | O(1) |
| LC 1283: Smallest Divisor | Answer | O(n log max(nums)) | O(1) |
| LC 69: Sqrt(x) | Answer | O(log x) | O(1) |
| LC 4: Median Two Sorted Arrays | Answer | O(log(min(m,n))) | O(1) |
| LC 378: Kth Smallest in Matrix | Advanced | O(n log(max-min)) | O(1) |
| LC 287: Find Duplicate (BS variant) | Advanced | O(n log n) | O(1) |
| LC 668: Kth in Multiplication Table | Advanced | O(m log mn) | O(1) |

### Mid Calculation Reference

```java
// Standard (floor): use in Template 1, 2, 3a
int mid = left + (right - left) / 2;

// Ceiling: use ONLY in Template 3b (find maximum satisfying)
int mid = left + (right - left + 1) / 2;

// For long ranges or predicates with multiplication:
long mid = left + (right - left) / 2;  // long version
```

### The Five Questions Before Coding Any Binary Search

1. What is my search space? What do `left` and `right` represent, exactly?
2. What is my predicate? What boolean condition am I searching for the boundary of?
3. Is the predicate monotonic? (If not, binary search won't work.)
4. Which template fits? (Exact match, boundary, min answer, max answer?)
5. What happens at the boundaries? (left=0, right=0, or lo=hi at start?)

Answering all five before writing a single line of code eliminates 90% of binary search bugs.

---

## 10. Practice Roadmap

### Week 1: Foundation (15 minutes per problem)

Build template muscle memory. Code each template from scratch without looking at references. Repeat until each one is automatic.

| Problem | Template | What to Learn |
|---------|----------|---------------|
| LC 704: Binary Search | Template 1 | The base template, loop condition `<=`, return -1 |
| LC 35: Search Insert Position | Template 2 | Lower bound, `right = arr.length`, `right = mid` |
| LC 69: Sqrt(x) | Template 3b | Maximum satisfying, ceiling mid, `long` overflow |
| LC 278: First Bad Version | Template 3a | Minimum satisfying, predicate API, floor mid |

**Goal after Week 1:** Produce any of the three templates from memory in under 90 seconds, explain why each line is written the way it is, and name a specific bug you'd have if you changed any single detail.

### Week 2: Medium Classic (25 minutes per problem)

Apply templates to modified structures. Focus on identifying *which* half is guaranteed sorted and *why*.

| Problem | Template | Key Insight to Master |
|---------|----------|-----------------------|
| LC 34: First/Last Position | Template 2 | Lower + upper bound combined, count = upper - lower |
| LC 33: Rotated Sorted Array | Template 1 variant | Which half is sorted, the two-condition check |
| LC 153: Min in Rotated | Template 2 variant | Compare with right, not left |
| LC 162: Peak Element | Template 2 variant | Slope direction as monotonic predicate |
| LC 74: 2D Matrix Search | Template 1 | `mid / n` and `mid % n` index conversion |

**Goal after Week 2:** Read a problem, identify the correct template within 30 seconds, and code a correct solution within 15 minutes. The "which half is sorted" logic in LC 33 should feel natural.

### Week 3: Search on Answer (30 minutes per problem)

The high-value category. Focus on the predicate first. Don't touch the binary search loop until the predicate is written and tested mentally.

| Problem | Predicate to Write | Answer Space |
|---------|--------------------|--------------|
| LC 875: Koko Bananas | `canFinish(speed, piles, h)` | `[1, max(piles)]` |
| LC 1011: Ship Packages | `canShip(capacity, weights, days)` | `[max(w), sum(w)]` |
| LC 410: Split Array | `canSplit(maxSum, nums, k)` | `[max(nums), sum(nums)]` |
| LC 1283: Smallest Divisor | `getSum(nums, div) <= threshold` | `[1, max(nums)]` |
| LC 540: Single Non-Duplicate | Parity-based (not search-on-answer, but good medium) | N/A |

**Goal after Week 3:** Given any "minimum X such that..." problem, write the predicate function before considering the binary search. The binary search is mechanical once the predicate is correct. Be able to identify the answer space bounds from problem constraints.

### Week 4: Hard Problems (40 minutes per problem)

| Problem | Key Challenge |
|---------|---------------|
| LC 4: Median Two Sorted Arrays | Partition binary search, edge cases with MIN/MAX_VALUE sentinels |
| LC 378: Kth Smallest in Matrix | Binary search on value + O(n) counting walk from bottom-left |
| LC 668: Kth in Multiplication Table | Count function using integer division per row |
| LC 81: Rotated with Duplicates | Handle ambiguous case with `left++, right--` |

**Goal after Week 4:** These represent the full difficulty range of binary search problems. Any binary search problem you encounter in an interview should map to one of the four categories in this document.

### The Two-Question Test for Any Binary Search Problem

Before writing any code, answer these two questions out loud (or on the whiteboard):

1. **What is my search space?** (`left` and `right` are concrete values, not abstract concepts)
2. **What is my predicate?** (`canAchieve(x)` returns true or false, and you can explain *why* it's monotonic)

If you can answer both clearly and specifically, the code is mechanical. If you struggle to answer either, stop and think before touching the keyboard. Writing code before answering these questions is the most common source of binary search bugs in interviews.

### Problem Count Summary

- Week 1: 4 problems (Easy + Foundation)
- Week 2: 5 problems (Medium Classic)
- Week 3: 5 problems (Search on Answer)
- Week 4: 4 problems (Hard)
- **Total: 18 problems over 4 weeks**

This covers the full binary search problem space at the depth required for FAANG interviews. After completing this roadmap, binary search problems should feel like pattern recognition rather than problem solving.

---

*Document 4 of 20 in the FAANG DSA Prep series.*

*Previous: Topic 3 covers Fast and Slow Pointers and Cycle Detection.*
*Next: Topic 5 covers Recursion and Backtracking fundamentals.*
