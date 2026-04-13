# Topic 13: Backtracking

**Complexity at a glance:** O(2^n) for subsets, O(n!) for permutations, O(n^n) worst case for constraint satisfaction.

Backtracking is controlled recursion. You explore all possibilities by building candidates incrementally and abandoning ("pruning") a candidate the moment it's clear it can't lead to a valid solution. Think of it as DFS on a decision tree where you can undo your choices.

The pattern appears everywhere at FAANG: subsets, permutations, combinations, constraint satisfaction (N-Queens, Sudoku), and string partitioning. It's one of the most tested topics because it requires both recursive thinking AND careful state management. Candidates who understand the `choose → explore → unchoose` loop cleanly stand out.

**This pattern solves:**
- Subsets: all possible subsets of a set (include/exclude each element)
- Permutations: all orderings of elements (swap or used[] array)
- Combinations: choose k elements from n (subsets with size constraint)
- Constraint satisfaction: N-Queens, Sudoku, valid placements
- String partitioning: palindrome partitioning, IP address restoration
- Grid search: word search, path finding with constraints

**Top companies:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5 Intuition](#eli5-intuition)
3. [The Three Problem Categories](#the-three-problem-categories)
4. [Templates](#templates)
5. [Real-World Applications](#real-world-applications)
6. [Duplicate Handling — The Critical Section](#duplicate-handling--the-critical-section)
7. [Problems by Category](#problems-by-category)
   - [Category A: Subsets and Combinations](#category-a-subsets-and-combinations)
   - [Category B: Permutations](#category-b-permutations)
   - [Category C: String Backtracking](#category-c-string-backtracking)
   - [Category D: Constraint Satisfaction](#category-d-constraint-satisfaction)
   - [Category E: Grid and Search](#category-e-grid-and-search)
8. [Common Mistakes](#common-mistakes)
9. [Backtracking vs DP vs BFS](#backtracking-vs-dp-vs-bfs)
10. [Cheat Sheet](#cheat-sheet)
11. [Study Roadmap](#study-roadmap)

---

## Core Concept

### Backtracking = DFS on a Decision Tree

Every backtracking problem is a tree traversal problem in disguise. At each node in the tree, you make a decision (choose an element, place a queen, add a character). You recurse down that branch. When you hit a dead end or a complete solution, you undo your last decision and try the next option.

The three-step loop is the entire algorithm:

```
1. CHOOSE   — make a decision (add element to path, mark cell as visited, etc.)
2. EXPLORE  — recurse with the updated state
3. UNCHOOSE — undo the decision (remove element from path, unmark cell, etc.)
```

This is what separates backtracking from plain DFS: **you undo state changes before returning**. In DFS on a graph, you might mark nodes visited and never unmark them. In backtracking, you always restore state so the next branch sees a clean slate.

### The Decision Tree Mental Model

For `nums = [1, 2, 3]`, generating all subsets:

```
                        []
              /          |          \
           [1]          [2]         [3]
          /   \          |
       [1,2] [1,3]     [2,3]
        |
     [1,2,3]
```

Each path from root to any node is a valid subset. The algorithm visits every node exactly once. At each node, you decide: include the next element or skip it.

### Key Insight: Start Index Controls Reuse

- `recurse(i + 1)` — no reuse, each element used at most once (subsets, combinations)
- `recurse(i)` — reuse allowed (Combination Sum where same element can repeat)
- `recurse(0)` with `used[]` array — permutations (order matters, restart from beginning)

### Pruning

Pruning cuts branches before you recurse into them. The earlier you prune, the faster the algorithm. Sorting the input often enables pruning because once a candidate is too large, all subsequent candidates are also too large.

```java
// Without pruning: try all candidates
for (int i = start; i < nums.length; i++) {
    path.add(nums[i]);
    recurse(i + 1, path, result);
    path.remove(path.size() - 1);
}

// With pruning: stop early if remaining sum can't be satisfied
for (int i = start; i < nums.length; i++) {
    if (nums[i] > remaining) break; // sorted array, no point continuing
    path.add(nums[i]);
    recurse(i + 1, remaining - nums[i], path, result);
    path.remove(path.size() - 1);
}
```

---

## ELI5 Intuition

You're solving a maze. At each fork, you pick one path. If you hit a dead end, you walk back to the last fork and try a different path. You keep doing this until you find the exit or have tried everything.

The "walking back" is the backtrack step. The "trying a different path" is the loop. The "dead end" is your pruning condition.

Now imagine you're not just finding one exit but mapping every possible path through the maze. That's backtracking for generating all subsets or permutations.

The key realization: **you can only walk back if you remember exactly what you did**. That's why you undo state changes. If you painted a wall red when you entered a room, you paint it back to white when you leave. The next explorer who enters that room sees it as white, not red.

---

## The Three Problem Categories

### Category 1: Subsets (Include/Exclude)

At each position, you decide: include this element or skip it. The start index moves forward so you never revisit earlier elements.

```
nums = [1, 2, 3]
At position 0: include 1 or skip
At position 1: include 2 or skip
At position 2: include 3 or skip
```

Result: 2^n subsets (each element has 2 choices).

### Category 2: Permutations (All Orderings)

Every element can appear at every position. You use a `used[]` boolean array to track which elements are already in the current path. The loop always starts from 0.

```
nums = [1, 2, 3]
Position 0: try 1, 2, or 3
Position 1: try any unused element
Position 2: try the remaining unused element
```

Result: n! permutations.

### Category 3: Combinations (Choose K from N)

Like subsets but you stop recursing when `path.size() == k`. The start index still moves forward to avoid duplicates.

```
n = 4, k = 2
Choose 2 from [1, 2, 3, 4]
Result: [1,2], [1,3], [1,4], [2,3], [2,4], [3,4]
```

Result: C(n, k) = n! / (k! * (n-k)!) combinations.

---

## Templates

### Template 1: Subsets

```java
// LC 78 — Subsets
// Time: O(n * 2^n), Space: O(n) recursion stack
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, int start, List<Integer> path, List<List<Integer>> result) {
    // Add current path to result at EVERY node (not just leaves)
    result.add(new ArrayList<>(path));

    for (int i = start; i < nums.length; i++) {
        // CHOOSE: add nums[i] to path
        path.add(nums[i]);

        // EXPLORE: recurse with next start index
        backtrack(nums, i + 1, path, result);

        // UNCHOOSE: remove last element (backtrack)
        path.remove(path.size() - 1);
    }
}
```

**Decision tree for `[1, 2, 3]`:**

```
backtrack(start=0, path=[])         -> add []
  i=0: path=[1]
  backtrack(start=1, path=[1])      -> add [1]
    i=1: path=[1,2]
    backtrack(start=2, path=[1,2])  -> add [1,2]
      i=2: path=[1,2,3]
      backtrack(start=3, path=[1,2,3]) -> add [1,2,3]
      path=[1,2]
    path=[1]
    i=2: path=[1,3]
    backtrack(start=3, path=[1,3])  -> add [1,3]
    path=[1]
  path=[]
  i=1: path=[2]
  backtrack(start=2, path=[2])      -> add [2]
    i=2: path=[2,3]
    backtrack(start=3, path=[2,3])  -> add [2,3]
    path=[2]
  path=[]
  i=2: path=[3]
  backtrack(start=3, path=[3])      -> add [3]
  path=[]

Result: [], [1], [1,2], [1,2,3], [1,3], [2], [2,3], [3]
```

**State trace (path contents at each add):**

| Call depth | start | path before add | path added to result |
|------------|-------|-----------------|----------------------|
| 0          | 0     | []              | []                   |
| 1          | 1     | [1]             | [1]                  |
| 2          | 2     | [1,2]           | [1,2]                |
| 3          | 3     | [1,2,3]         | [1,2,3]              |
| 2 (back)   | 3     | [1,3]           | [1,3]                |
| 1 (back)   | 2     | [2]             | [2]                  |
| 2          | 3     | [2,3]           | [2,3]                |
| 1 (back)   | 3     | [3]             | [3]                  |

---

### Template 2: Permutations

```java
// LC 46 — Permutations
// Time: O(n * n!), Space: O(n)
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    boolean[] used = new boolean[nums.length];
    backtrack(nums, used, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> result) {
    // Base case: path is complete
    if (path.size() == nums.length) {
        result.add(new ArrayList<>(path));
        return;
    }

    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue; // skip already-used elements

        // CHOOSE
        used[i] = true;
        path.add(nums[i]);

        // EXPLORE
        backtrack(nums, used, path, result);

        // UNCHOOSE
        path.remove(path.size() - 1);
        used[i] = false;
    }
}
```

**Permutation tree for `[1, 2, 3]`:**

```
backtrack(path=[], used=[F,F,F])
  i=0: choose 1, path=[1], used=[T,F,F]
    backtrack(path=[1], used=[T,F,F])
      i=0: skip (used)
      i=1: choose 2, path=[1,2], used=[T,T,F]
        backtrack(path=[1,2], used=[T,T,F])
          i=0: skip, i=1: skip
          i=2: choose 3, path=[1,2,3] -> ADD [1,2,3]
          unchoose 3
        unchoose 2, path=[1]
      i=2: choose 3, path=[1,3], used=[T,F,T]
        backtrack(path=[1,3], used=[T,F,T])
          i=0: skip, i=2: skip
          i=1: choose 2, path=[1,3,2] -> ADD [1,3,2]
          unchoose 2
        unchoose 3, path=[1]
    unchoose 1, path=[]
  i=1: choose 2, path=[2], used=[F,T,F]
    backtrack(path=[2], used=[F,T,F])
      i=0: choose 1, path=[2,1], used=[T,T,F]
        backtrack(path=[2,1])
          i=2: choose 3, path=[2,1,3] -> ADD [2,1,3]
        unchoose 1
      i=1: skip
      i=2: choose 3, path=[2,3], used=[F,T,T]
        backtrack(path=[2,3])
          i=0: choose 1, path=[2,3,1] -> ADD [2,3,1]
        unchoose 3
    unchoose 2, path=[]
  i=2: choose 3, path=[3], used=[F,F,T]
    backtrack(path=[3])
      i=0: choose 1, path=[3,1]
        backtrack(path=[3,1])
          i=1: choose 2, path=[3,1,2] -> ADD [3,1,2]
        unchoose 1
      i=1: choose 2, path=[3,2]
        backtrack(path=[3,2])
          i=0: choose 1, path=[3,2,1] -> ADD [3,2,1]
        unchoose 2
    unchoose 3

Result: [1,2,3], [1,3,2], [2,1,3], [2,3,1], [3,1,2], [3,2,1]
```

---

### Template 3: Combinations (Choose K)

```java
// LC 77 — Combinations
// Time: O(k * C(n,k)), Space: O(k)
public List<List<Integer>> combine(int n, int k) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(1, n, k, new ArrayList<>(), result);
    return result;
}

private void backtrack(int start, int n, int k, List<Integer> path, List<List<Integer>> result) {
    // Base case: found a valid combination
    if (path.size() == k) {
        result.add(new ArrayList<>(path));
        return;
    }

    // Pruning: need (k - path.size()) more elements
    // Only iterate if enough elements remain
    for (int i = start; i <= n - (k - path.size()) + 1; i++) {
        // CHOOSE
        path.add(i);

        // EXPLORE
        backtrack(i + 1, n, k, path, result);

        // UNCHOOSE
        path.remove(path.size() - 1);
    }
}
```

The pruning condition `i <= n - (k - path.size()) + 1` is worth understanding. If you need 2 more elements and you're at position 4 in a range of 1..5, you can still pick [4,5]. But if you're at position 5, you can only pick [5] which isn't enough. So you stop early.

---

### Template 4: Constraint Satisfaction

```java
// Generic constraint satisfaction template
// Used for N-Queens, Sudoku, etc.
private void backtrack(State state, List<State> result) {
    // Base case: state is complete and valid
    if (isComplete(state)) {
        result.add(copyOf(state));
        return;
    }

    for (Option option : getOptions(state)) {
        if (isValid(state, option)) {
            // CHOOSE: apply option to state
            applyOption(state, option);

            // EXPLORE
            backtrack(state, result);

            // UNCHOOSE: undo option
            undoOption(state, option);
        }
    }
}
```

The key difference from subsets/permutations: you validate BEFORE recursing, not after. This is where most pruning happens. For N-Queens, you check row/column/diagonal conflicts before placing a queen. For Sudoku, you check row/column/box constraints before placing a digit.

---

## Real-World Applications

**Compiler regex matching:** Regular expression engines use backtracking to match patterns. When a `.*` matches too many characters and the rest of the pattern fails, the engine backtracks and tries matching fewer characters.

**SAT solvers:** Boolean satisfiability solvers (used in chip design verification, formal proofs) are essentially backtracking algorithms with sophisticated pruning. DPLL and CDCL algorithms are backtracking at their core.

**Puzzle generators:** Sudoku puzzle generators create valid boards by placing digits with backtracking, then remove cells to create the puzzle. The same algorithm that solves Sudoku generates it.

**Route planning with constraints:** Finding routes that satisfy multiple constraints (avoid toll roads, stay under distance limit, visit specific waypoints) uses backtracking when greedy approaches fail.

**Configuration space search:** When configuring a system with interdependent options (compiler flags, network settings), backtracking explores valid configurations by trying options and undoing incompatible ones.

**AI game playing:** Minimax with alpha-beta pruning is backtracking on a game tree. You explore moves, evaluate positions, and prune branches that can't improve the current best outcome.

---

## Duplicate Handling — The Critical Section

This is the #1 thing candidates get wrong. When the input has duplicate elements, naive backtracking generates duplicate results. The fix is a two-step process: sort first, then skip duplicates at the same recursion level.

### Why Duplicates Appear

Consider `nums = [1, 1, 2]` for subsets. Without duplicate handling:

```
Start with 1 (index 0): [1], [1,1], [1,1,2], [1,2]
Start with 1 (index 1): [1], [1,2]          <- DUPLICATES
Start with 2 (index 2): [2]
```

The subsets `[1]` and `[1,2]` appear twice because there are two `1`s at the same level.

### Fix for Subsets and Combinations

```java
// Sort first — REQUIRED
Arrays.sort(nums);

private void backtrack(int[] nums, int start, List<Integer> path, List<List<Integer>> result) {
    result.add(new ArrayList<>(path));

    for (int i = start; i < nums.length; i++) {
        // Skip duplicates at the SAME recursion level
        // i > start means: we've already tried this value at this level
        if (i > start && nums[i] == nums[i - 1]) continue;

        path.add(nums[i]);
        backtrack(nums, i + 1, path, result);
        path.remove(path.size() - 1);
    }
}
```

**Why `i > start` and not `i > 0`?**

The condition `i > start` means "we're not at the first position of this recursion level." If `i == start`, this is the first element we're trying at this level, so we should try it even if it's a duplicate of a previous element (from a different level).

Concrete example with `[1, 1, 2]`:

```
backtrack(start=0, path=[])
  i=0: nums[0]=1, i==start so NO skip. path=[1]
    backtrack(start=1, path=[1])
      i=1: nums[1]=1, i==start so NO skip. path=[1,1]
        backtrack(start=2, path=[1,1])
          i=2: path=[1,1,2] -> add
        path=[1,1]
      path=[1]
      i=2: nums[2]=2, i>start but nums[2]!=nums[1]. path=[1,2] -> add
    path=[]
  i=1: nums[1]=1, i>start AND nums[1]==nums[0] -> SKIP
  i=2: nums[2]=2, i>start but nums[2]!=nums[1]. path=[2] -> add
    backtrack(start=3, path=[2]) -> add [2]

Result: [], [1], [1,1], [1,1,2], [1,2], [2]  (no duplicates)
```

If you used `i > 0` instead of `i > start`, you'd skip `nums[1]=1` even when building `[1,1]` from the second level, which would incorrectly prevent `[1,1]` from being generated.

### Fix for Permutations

Permutations use a `used[]` array, so the duplicate condition is different:

```java
Arrays.sort(nums); // Sort first

private void backtrack(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> result) {
    if (path.size() == nums.length) {
        result.add(new ArrayList<>(path));
        return;
    }

    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;

        // Skip duplicates: if nums[i] == nums[i-1] and nums[i-1] is NOT used,
        // then we're at the same level and would generate a duplicate permutation
        if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;

        used[i] = true;
        path.add(nums[i]);
        backtrack(nums, used, path, result);
        path.remove(path.size() - 1);
        used[i] = false;
    }
}
```

**Why `!used[i-1]` for permutations?**

This is the trickiest part. Consider `nums = [1, 1, 2]` (after sorting).

When building permutations, `[1a, 1b, 2]` and `[1b, 1a, 2]` are duplicates (both look like `[1, 1, 2]`).

The condition `!used[i-1]` means: "the previous identical element is NOT currently in our path." If `nums[i-1]` is not used, it means we're at the same recursion level as when we would have tried `nums[i-1]` first. Skipping here prevents trying `nums[i]` before `nums[i-1]` at the same level.

Alternatively, you can use `used[i-1]` (the opposite condition) which enforces that you always use the first duplicate before the second. Both work, but `!used[i-1]` is more commonly seen and slightly more efficient.

**Concrete trace with `[1, 1, 2]`:**

```
i=0: nums[0]=1, used=[F,F,F]. No skip. used=[T,F,F], path=[1]
  i=0: skip (used)
  i=1: nums[1]=1. i>0, nums[1]==nums[0], !used[0]? used[0]=T so !T=F -> NO skip
       used=[T,T,F], path=[1,1]
    i=2: nums[2]=2. used=[T,T,T], path=[1,1,2] -> ADD
  i=2: nums[2]=2. used=[T,F,T], path=[1,2]
    i=1: nums[1]=1. i>0, nums[1]==nums[0], !used[0]? used[0]=T so !T=F -> NO skip
         path=[1,2,1] -> ADD
  path=[]

i=1: nums[1]=1. i>0, nums[1]==nums[0], !used[0]? used[0]=F so !F=T -> SKIP

i=2: nums[2]=2. used=[F,F,T], path=[2]
  i=0: nums[0]=1. used=[T,F,T], path=[2,1]
    i=1: nums[1]=1. i>0, nums[1]==nums[0], !used[0]? used[0]=T so !T=F -> NO skip
         path=[2,1,1] -> ADD
  i=1: nums[1]=1. i>0, nums[1]==nums[0], !used[0]? used[0]=F so !F=T -> SKIP
  i=2: skip (used)

Result: [1,1,2], [1,2,1], [2,1,1]  (correct, no duplicates)
```

### Summary Table

| Problem type | Sort? | Skip condition |
|---|---|---|
| Subsets II | Yes | `i > start && nums[i] == nums[i-1]` |
| Combination Sum II | Yes | `i > start && nums[i] == nums[i-1]` |
| Permutations II | Yes | `i > 0 && nums[i] == nums[i-1] && !used[i-1]` |

---

## Problems by Category

---

### Category A: Subsets and Combinations

---

#### LC 78 — Subsets

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given an integer array `nums` of unique elements, return all possible subsets (the power set).

**Approach:** Classic backtracking. Add current path to result at every node (not just leaves). Use start index to avoid revisiting earlier elements.

**Full decision tree for `[1, 2, 3]`:**

```
Level 0 (start=0):  []
                   /  |  \
Level 1:         [1] [2] [3]
                /  \   \
Level 2:     [1,2][1,3][2,3]
              |
Level 3:   [1,2,3]

Nodes visited (in order):
[] -> [1] -> [1,2] -> [1,2,3] -> [1,3] -> [2] -> [2,3] -> [3]
```

```java
class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, int start, List<Integer> path,
                           List<List<Integer>> result) {
        // Add at every node — subsets include partial paths
        result.add(new ArrayList<>(path));

        for (int i = start; i < nums.length; i++) {
            path.add(nums[i]);           // CHOOSE
            backtrack(nums, i + 1, path, result); // EXPLORE
            path.remove(path.size() - 1); // UNCHOOSE
        }
    }
}
```

**Time:** O(n * 2^n) — 2^n subsets, each takes O(n) to copy.
**Space:** O(n) recursion stack.

---

#### LC 90 — Subsets II

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given an integer array `nums` that may contain duplicates, return all possible subsets without duplicates.

**Approach:** Sort first, then skip duplicates at the same recursion level with `i > start && nums[i] == nums[i-1]`.

```java
class Solution {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums); // REQUIRED for duplicate detection
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, int start, List<Integer> path,
                           List<List<Integer>> result) {
        result.add(new ArrayList<>(path));

        for (int i = start; i < nums.length; i++) {
            // Skip duplicates at the same level
            if (i > start && nums[i] == nums[i - 1]) continue;

            path.add(nums[i]);           // CHOOSE
            backtrack(nums, i + 1, path, result); // EXPLORE
            path.remove(path.size() - 1); // UNCHOOSE
        }
    }
}
```

**Dry run with `[1, 2, 2]`:**

```
backtrack(start=0, path=[]) -> add []
  i=0: path=[1]
    backtrack(start=1, path=[1]) -> add [1]
      i=1: path=[1,2]
        backtrack(start=2, path=[1,2]) -> add [1,2]
          i=2: i>start, nums[2]==nums[1] -> SKIP
        path=[1]
      i=2: i>start, nums[2]==nums[1] -> SKIP
    path=[]
  i=1: path=[2]
    backtrack(start=2, path=[2]) -> add [2]
      i=2: i>start, nums[2]==nums[1] -> SKIP
    path=[]
  i=2: i>start, nums[2]==nums[1] -> SKIP

Result: [], [1], [1,2], [2]
```

Wait, that's missing `[1,2,2]` and `[2,2]`. Let me re-trace with `[1, 2, 2]`:

```
backtrack(start=0, path=[]) -> add []
  i=0: nums[0]=1. path=[1]
    backtrack(start=1, path=[1]) -> add [1]
      i=1: nums[1]=2. i==start, no skip. path=[1,2]
        backtrack(start=2, path=[1,2]) -> add [1,2]
          i=2: nums[2]=2. i==start(2), no skip. path=[1,2,2]
            backtrack(start=3) -> add [1,2,2]
          path=[1,2]
        path=[1]
      i=2: nums[2]=2. i>start(1), nums[2]==nums[1] -> SKIP
    path=[]
  i=1: nums[1]=2. i>start(0), nums[1]!=nums[0] -> no skip. path=[2]
    backtrack(start=2, path=[2]) -> add [2]
      i=2: nums[2]=2. i==start(2), no skip. path=[2,2]
        backtrack(start=3) -> add [2,2]
      path=[2]
    path=[]
  i=2: nums[2]=2. i>start(0), nums[2]==nums[1] -> SKIP

Result: [], [1], [1,2], [1,2,2], [2], [2,2]  (correct, 6 unique subsets)
```

**Time:** O(n * 2^n). **Space:** O(n).

---

#### LC 77 — Combinations

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given two integers `n` and `k`, return all possible combinations of `k` numbers chosen from the range `[1, n]`.

**Approach:** Like subsets but stop when `path.size() == k`. Add pruning to avoid unnecessary iterations.

```java
class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(1, n, k, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int start, int n, int k, List<Integer> path,
                           List<List<Integer>> result) {
        if (path.size() == k) {
            result.add(new ArrayList<>(path));
            return;
        }

        // Pruning: need (k - path.size()) more elements
        // Last valid start = n - (k - path.size()) + 1
        int need = k - path.size();
        for (int i = start; i <= n - need + 1; i++) {
            path.add(i);                          // CHOOSE
            backtrack(i + 1, n, k, path, result); // EXPLORE
            path.remove(path.size() - 1);          // UNCHOOSE
        }
    }
}
```

**Example: n=4, k=2**

```
backtrack(start=1, path=[])
  i=1: path=[1]
    backtrack(start=2, path=[1])
      i=2: path=[1,2] -> ADD (size==k)
      i=3: path=[1,3] -> ADD
      i=4: path=[1,4] -> ADD
  i=2: path=[2]
    backtrack(start=3, path=[2])
      i=3: path=[2,3] -> ADD
      i=4: path=[2,4] -> ADD
  i=3: path=[3]
    backtrack(start=4, path=[3])
      i=4: path=[3,4] -> ADD
  i=4: pruned (4 > 4 - 1 + 1 = 4, so i<=4, actually i=4 is valid)
       Wait: need=1, n-need+1 = 4-1+1 = 4. So i goes up to 4.
       path=[4] -> backtrack(start=5) -> need=1, 5 > 4-1+1=4, loop doesn't run

Result: [1,2],[1,3],[1,4],[2,3],[2,4],[3,4]
```

**Time:** O(k * C(n,k)). **Space:** O(k).

---

#### LC 39 — Combination Sum

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given an array of distinct integers `candidates` and a target integer `target`, return all unique combinations where the chosen numbers sum to `target`. The same number may be chosen unlimited times.

**Key difference from LC 77:** Pass `i` (not `i+1`) to allow reuse of the same element.

```java
class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates); // Sort for pruning
        List<List<Integer>> result = new ArrayList<>();
        backtrack(candidates, 0, target, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int start, int remaining,
                           List<Integer> path, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            // Pruning: sorted array, if current > remaining, all subsequent are too
            if (candidates[i] > remaining) break;

            path.add(candidates[i]);                    // CHOOSE
            backtrack(candidates, i, remaining - candidates[i], path, result); // EXPLORE (i, not i+1)
            path.remove(path.size() - 1);               // UNCHOOSE
        }
    }
}
```

**Example: candidates=[2,3,6,7], target=7**

```
backtrack(start=0, remaining=7, path=[])
  i=0: candidates[0]=2. path=[2], remaining=5
    i=0: path=[2,2], remaining=3
      i=0: path=[2,2,2], remaining=1
        i=0: 2>1, break
        i=1: 3>1, break
      path=[2,2]
      i=1: path=[2,2,3], remaining=0 -> ADD [2,2,3]
    path=[2]
    i=1: path=[2,3], remaining=2
      i=1: 3>2, break
    path=[2]
    i=2: path=[2,6], remaining=-1... wait, 6>5, break
  i=1: candidates[1]=3. path=[3], remaining=4
    i=1: path=[3,3], remaining=1
      i=1: 3>1, break
    path=[3]
    i=2: 6>4, break
  i=2: candidates[2]=6. path=[6], remaining=1
    i=2: 6>1, break
  i=3: candidates[3]=7. path=[7], remaining=0 -> ADD [7]

Result: [2,2,3], [7]
```

**Time:** O(n^(T/M)) where T=target, M=min candidate. **Space:** O(T/M) recursion depth.

---

#### LC 40 — Combination Sum II

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given a collection of candidate numbers (may have duplicates) and a target, find all unique combinations that sum to target. Each number may only be used once.

**Two differences from LC 39:** Use `i+1` (no reuse), and skip duplicates at same level.

```java
class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates); // REQUIRED
        List<List<Integer>> result = new ArrayList<>();
        backtrack(candidates, 0, target, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int start, int remaining,
                           List<Integer> path, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) break; // Pruning

            // Skip duplicates at the same level
            if (i > start && candidates[i] == candidates[i - 1]) continue;

            path.add(candidates[i]);                        // CHOOSE
            backtrack(candidates, i + 1, remaining - candidates[i], path, result); // EXPLORE
            path.remove(path.size() - 1);                   // UNCHOOSE
        }
    }
}
```

**Why both `i > start` AND `candidates[i] > remaining` matter:**

- `candidates[i] > remaining` prunes branches where the current candidate is already too large.
- `i > start && candidates[i] == candidates[i-1]` prevents duplicate combinations at the same recursion level.

**Time:** O(2^n). **Space:** O(n).

---

#### LC 216 — Combination Sum III

**Companies:** Amazon, Google, Microsoft

**Problem:** Find all valid combinations of `k` numbers that sum up to `n`. Only numbers 1-9 can be used, each used at most once.

```java
class Solution {
    public List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(1, k, n, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int start, int k, int remaining,
                           List<Integer> path, List<List<Integer>> result) {
        if (path.size() == k && remaining == 0) {
            result.add(new ArrayList<>(path));
            return;
        }
        // Pruning: too many elements or remaining can't be satisfied
        if (path.size() == k || remaining <= 0) return;

        for (int i = start; i <= 9; i++) {
            if (i > remaining) break; // Pruning

            path.add(i);                              // CHOOSE
            backtrack(i + 1, k, remaining - i, path, result); // EXPLORE
            path.remove(path.size() - 1);              // UNCHOOSE
        }
    }
}
```

**Time:** O(C(9,k)). **Space:** O(k).

---

### Category B: Permutations

---

#### LC 46 — Permutations

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given an array `nums` of distinct integers, return all possible permutations.

**Full solution with complete permutation tree already shown in Templates section.**

```java
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, boolean[] used, List<Integer> path,
                           List<List<Integer>> result) {
        if (path.size() == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;

            used[i] = true;              // CHOOSE
            path.add(nums[i]);

            backtrack(nums, used, path, result); // EXPLORE

            path.remove(path.size() - 1); // UNCHOOSE
            used[i] = false;
        }
    }
}
```

**Alternative: swap-based approach (modifies input array)**

```java
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, result);
        return result;
    }

    private void backtrack(int[] nums, int start, List<List<Integer>> result) {
        if (start == nums.length) {
            List<Integer> perm = new ArrayList<>();
            for (int n : nums) perm.add(n);
            result.add(perm);
            return;
        }

        for (int i = start; i < nums.length; i++) {
            swap(nums, start, i);              // CHOOSE
            backtrack(nums, start + 1, result); // EXPLORE
            swap(nums, start, i);              // UNCHOOSE (swap back)
        }
    }

    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
```

The swap approach is slightly more memory-efficient (no `used[]` array) but modifies the input. The `used[]` approach is cleaner and easier to extend to Permutations II.

**Time:** O(n * n!). **Space:** O(n).

---

#### LC 47 — Permutations II

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given a collection of numbers that might contain duplicates, return all unique permutations.

**Duplicate handling for permutations already explained in detail in the Duplicate Handling section.**

```java
class Solution {
    public List<List<Integer>> permuteUnique(int[] nums) {
        Arrays.sort(nums); // REQUIRED
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, boolean[] used, List<Integer> path,
                           List<List<Integer>> result) {
        if (path.size() == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;

            // Skip duplicate: if nums[i] == nums[i-1] and nums[i-1] is NOT used,
            // we're at the same level and would generate a duplicate
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;

            used[i] = true;              // CHOOSE
            path.add(nums[i]);

            backtrack(nums, used, path, result); // EXPLORE

            path.remove(path.size() - 1); // UNCHOOSE
            used[i] = false;
        }
    }
}
```

**Verification with `[1, 1, 2]`:** Should produce `[1,1,2], [1,2,1], [2,1,1]` — 3 unique permutations (not 3! = 6).

**Time:** O(n * n!). **Space:** O(n).

---

### Category C: String Backtracking

---

#### LC 17 — Letter Combinations of a Phone Number

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given a string containing digits 2-9, return all possible letter combinations that the number could represent (phone keypad mapping).

**Approach:** At each digit position, try all letters mapped to that digit. This is a combination problem on strings.

```java
class Solution {
    private static final String[] PHONE = {
        "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
    };

    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.isEmpty()) return result;
        backtrack(digits, 0, new StringBuilder(), result);
        return result;
    }

    private void backtrack(String digits, int index, StringBuilder path,
                           List<String> result) {
        if (index == digits.length()) {
            result.add(path.toString());
            return;
        }

        String letters = PHONE[digits.charAt(index) - '0'];
        for (char c : letters.toCharArray()) {
            path.append(c);                          // CHOOSE
            backtrack(digits, index + 1, path, result); // EXPLORE
            path.deleteCharAt(path.length() - 1);    // UNCHOOSE
        }
    }
}
```

**Decision tree for "23":**

```
index=0, digit='2', letters="abc"
  'a': path="a"
    index=1, digit='3', letters="def"
      'd': path="ad" -> ADD
      'e': path="ae" -> ADD
      'f': path="af" -> ADD
  'b': path="b"
    'd': path="bd" -> ADD
    'e': path="be" -> ADD
    'f': path="bf" -> ADD
  'c': path="c"
    'd': path="cd" -> ADD
    'e': path="ce" -> ADD
    'f': path="cf" -> ADD

Result: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
```

**Time:** O(4^n * n) where n = digits length (4 is max letters per digit). **Space:** O(n).

---

#### LC 131 — Palindrome Partitioning

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given a string `s`, partition `s` such that every substring of the partition is a palindrome. Return all possible palindrome partitioning.

**Approach:** At each position, try all possible substrings starting at that position. Only recurse if the substring is a palindrome.

```java
class Solution {
    public List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        backtrack(s, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(String s, int start, List<String> path,
                           List<List<String>> result) {
        if (start == s.length()) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int end = start + 1; end <= s.length(); end++) {
            String sub = s.substring(start, end);
            if (!isPalindrome(sub)) continue; // Pruning: skip non-palindromes

            path.add(sub);                          // CHOOSE
            backtrack(s, end, path, result);         // EXPLORE
            path.remove(path.size() - 1);            // UNCHOOSE
        }
    }

    private boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left++) != s.charAt(right--)) return false;
        }
        return true;
    }
}
```

**Optimization:** Precompute palindrome check with DP to avoid O(n) check per substring.

```java
class Solution {
    public List<List<String>> partition(String s) {
        int n = s.length();
        // dp[i][j] = true if s[i..j] is palindrome
        boolean[][] dp = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                dp[i][j] = s.charAt(i) == s.charAt(j) &&
                           (j - i <= 2 || dp[i + 1][j - 1]);
            }
        }

        List<List<String>> result = new ArrayList<>();
        backtrack(s, 0, new ArrayList<>(), result, dp);
        return result;
    }

    private void backtrack(String s, int start, List<String> path,
                           List<List<String>> result, boolean[][] dp) {
        if (start == s.length()) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int end = start; end < s.length(); end++) {
            if (!dp[start][end]) continue; // O(1) palindrome check

            path.add(s.substring(start, end + 1)); // CHOOSE
            backtrack(s, end + 1, path, result, dp); // EXPLORE
            path.remove(path.size() - 1);            // UNCHOOSE
        }
    }
}
```

**Example: s = "aab"**

```
backtrack(start=0, path=[])
  end=0: "a" is palindrome. path=["a"]
    backtrack(start=1, path=["a"])
      end=1: "a" is palindrome. path=["a","a"]
        backtrack(start=2, path=["a","a"])
          end=2: "b" is palindrome. path=["a","a","b"] -> ADD
      end=2: "ab" not palindrome. skip
  end=1: "aa" is palindrome. path=["aa"]
    backtrack(start=2, path=["aa"])
      end=2: "b" is palindrome. path=["aa","b"] -> ADD
  end=2: "aab" not palindrome. skip

Result: [["a","a","b"], ["aa","b"]]
```

**Time:** O(n * 2^n). **Space:** O(n^2) for DP table.

---

#### LC 93 — Restore IP Addresses

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given a string `s` containing only digits, return all possible valid IP addresses that can be formed by inserting dots into `s`.

**Approach:** An IP address has exactly 4 parts, each between 0 and 255, with no leading zeros. Backtrack by choosing where to place each dot.

```java
class Solution {
    public List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        backtrack(s, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(String s, int start, List<String> parts,
                           List<String> result) {
        // Base case: 4 parts and used all characters
        if (parts.size() == 4 && start == s.length()) {
            result.add(String.join(".", parts));
            return;
        }
        // Pruning: too many parts or not enough characters
        if (parts.size() == 4 || start == s.length()) return;

        // Each part can be 1-3 digits
        for (int len = 1; len <= 3; len++) {
            if (start + len > s.length()) break;

            String part = s.substring(start, start + len);

            // Pruning: no leading zeros (except "0" itself)
            if (part.length() > 1 && part.charAt(0) == '0') break;

            // Pruning: value must be <= 255
            if (Integer.parseInt(part) > 255) break;

            parts.add(part);                              // CHOOSE
            backtrack(s, start + len, parts, result);     // EXPLORE
            parts.remove(parts.size() - 1);               // UNCHOOSE
        }
    }
}
```

**Example: s = "25525511135"**

```
backtrack(start=0, parts=[])
  len=1: part="2". parts=["2"]
    len=1: part="5". parts=["2","5"]
      len=1: part="5". parts=["2","5","5"]
        len=1: part="2" -> "2.5.5.2" but remaining="5511135" too long for 1 part
        len=2: part="25" -> "2.5.5.25" remaining="11135" too long
        len=3: part="255" -> "2.5.5.255" remaining="11135" too long
      ...
  len=3: part="255". parts=["255"]
    len=3: part="255". parts=["255","255"]
      len=2: part="11". parts=["255","255","11"]
        len=3: part="135". parts=["255","255","11","135"] -> ADD "255.255.11.135"
      len=3: part="111". parts=["255","255","111"]
        len=2: part="35". parts=["255","255","111","35"] -> ADD "255.255.111.35"
    ...

Result includes: "255.255.11.135", "255.255.111.35"
```

**Time:** O(3^4) = O(1) since at most 4 parts each with 3 choices. **Space:** O(1) excluding output.

---

### Category D: Constraint Satisfaction

---

#### LC 51 — N-Queens

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Place `n` queens on an n×n chessboard such that no two queens attack each other. Return all distinct solutions.

**Approach:** Place one queen per row. For each row, try each column. Before placing, check if the position conflicts with any previously placed queen (same column, same diagonal).

```java
class Solution {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        int[] queens = new int[n]; // queens[row] = column of queen in that row
        Arrays.fill(queens, -1);
        backtrack(queens, 0, n, result);
        return result;
    }

    private void backtrack(int[] queens, int row, int n,
                           List<List<String>> result) {
        if (row == n) {
            result.add(buildBoard(queens, n));
            return;
        }

        for (int col = 0; col < n; col++) {
            if (isValid(queens, row, col)) {
                queens[row] = col;              // CHOOSE
                backtrack(queens, row + 1, n, result); // EXPLORE
                queens[row] = -1;               // UNCHOOSE
            }
        }
    }

    private boolean isValid(int[] queens, int row, int col) {
        for (int r = 0; r < row; r++) {
            int c = queens[r];
            // Same column
            if (c == col) return false;
            // Same diagonal: |row - r| == |col - c|
            if (Math.abs(row - r) == Math.abs(col - c)) return false;
        }
        return true;
    }

    private List<String> buildBoard(int[] queens, int n) {
        List<String> board = new ArrayList<>();
        for (int row = 0; row < n; row++) {
            char[] line = new char[n];
            Arrays.fill(line, '.');
            line[queens[row]] = 'Q';
            board.add(new String(line));
        }
        return board;
    }
}
```

**Optimized with sets for O(1) conflict checking:**

```java
class Solution {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        Set<Integer> cols = new HashSet<>();
        Set<Integer> diag1 = new HashSet<>(); // row - col (top-left to bottom-right)
        Set<Integer> diag2 = new HashSet<>(); // row + col (top-right to bottom-left)
        int[] queens = new int[n];
        backtrack(queens, 0, n, cols, diag1, diag2, result);
        return result;
    }

    private void backtrack(int[] queens, int row, int n,
                           Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2,
                           List<List<String>> result) {
        if (row == n) {
            result.add(buildBoard(queens, n));
            return;
        }

        for (int col = 0; col < n; col++) {
            if (cols.contains(col)) continue;
            if (diag1.contains(row - col)) continue;
            if (diag2.contains(row + col)) continue;

            // CHOOSE
            queens[row] = col;
            cols.add(col);
            diag1.add(row - col);
            diag2.add(row + col);

            // EXPLORE
            backtrack(queens, row + 1, n, cols, diag1, diag2, result);

            // UNCHOOSE
            queens[row] = -1;
            cols.remove(col);
            diag1.remove(row - col);
            diag2.remove(row + col);
        }
    }

    private List<String> buildBoard(int[] queens, int n) {
        List<String> board = new ArrayList<>();
        for (int row = 0; row < n; row++) {
            char[] line = new char[n];
            Arrays.fill(line, '.');
            line[queens[row]] = 'Q';
            board.add(new String(line));
        }
        return board;
    }
}
```

**Why `row - col` and `row + col` for diagonals?**

All cells on the same top-left-to-bottom-right diagonal have the same `row - col` value. All cells on the same top-right-to-bottom-left diagonal have the same `row + col` value. Storing these in sets gives O(1) conflict detection.

**N=4 trace (partial):**

```
Row 0: try col 0,1,2,3
  col=0: queens=[0,-,-,-], cols={0}, diag1={0}, diag2={0}
    Row 1: try col 0,1,2,3
      col=0: cols has 0 -> skip
      col=1: diag2 has 0+1=1? No. diag1 has 1-1=0? Yes -> skip
      col=2: diag1 has 1-2=-1? No. diag2 has 1+2=3? No. cols has 2? No -> place
             queens=[0,2,-,-]
        Row 2: try col 0,1,2,3
          col=0: diag2 has 2+0=2? No. diag1 has 2-0=2? No. cols has 0? No -> place
                 queens=[0,2,0,-]... wait, col=0 is in cols? No, cols={0,2}. Yes! skip
          col=1: cols has 1? No. diag1 has 2-1=1? No. diag2 has 2+1=3? Yes -> skip
          col=2: cols has 2? Yes -> skip
          col=3: cols has 3? No. diag1 has 2-3=-1? No. diag2 has 2+3=5? No -> place
                 queens=[0,2,3,-] (wait, col=0 is in cols={0,2}, so col=0 is blocked)
                 Actually queens=[0,2,_,_], placing at row=2,col=3
            Row 3: try col 0,1,2,3
              col=0: cols has 0? Yes -> skip
              col=1: cols has 1? No. diag1 has 3-1=2? No. diag2 has 3+1=4? No -> place
                     queens=[0,2,3,1] -> ADD board
```

**Time:** O(n!). **Space:** O(n).

---

#### LC 37 — Sudoku Solver

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Write a program to solve a Sudoku puzzle by filling the empty cells.

**Approach:** For each empty cell, try digits 1-9. Before placing, check row/column/box constraints. Recurse. If no digit works, return false (backtrack).

```java
class Solution {
    public void solveSudoku(char[][] board) {
        solve(board);
    }

    private boolean solve(char[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] != '.') continue;

                for (char c = '1'; c <= '9'; c++) {
                    if (isValid(board, row, col, c)) {
                        board[row][col] = c;          // CHOOSE

                        if (solve(board)) return true; // EXPLORE

                        board[row][col] = '.';         // UNCHOOSE
                    }
                }
                return false; // No valid digit found — backtrack
            }
        }
        return true; // All cells filled
    }

    private boolean isValid(char[][] board, int row, int col, char c) {
        for (int i = 0; i < 9; i++) {
            // Check row
            if (board[row][i] == c) return false;
            // Check column
            if (board[i][col] == c) return false;
            // Check 3x3 box
            int boxRow = 3 * (row / 3) + i / 3;
            int boxCol = 3 * (col / 3) + i % 3;
            if (board[boxRow][boxCol] == c) return false;
        }
        return true;
    }
}
```

**Key insight:** The function returns `boolean` instead of `void`. When a valid solution is found, it returns `true` immediately without continuing to explore. This is the "find first solution" variant of backtracking.

**Box index formula:** For a cell at `(row, col)`, the 3x3 box starts at `(3*(row/3), 3*(col/3))`. The 9 cells in the box are at offsets `(i/3, i%3)` for `i` from 0 to 8.

**Time:** O(9^m) where m = number of empty cells. **Space:** O(m) recursion depth.

---

### Category E: Grid and Search

---

#### LC 79 — Word Search

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given an m×n grid of characters and a string `word`, return true if `word` exists in the grid. The word can be constructed from letters of sequentially adjacent cells (horizontally or vertically adjacent). The same cell may not be used more than once.

**Approach:** For each cell that matches `word[0]`, start a DFS. Mark cells as visited before recursing, unmark after.

```java
class Solution {
    private int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

    public boolean exist(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == word.charAt(0)) {
                    if (backtrack(board, word, i, j, 0)) return true;
                }
            }
        }
        return false;
    }

    private boolean backtrack(char[][] board, String word, int row, int col, int index) {
        if (index == word.length()) return true; // Found complete word

        if (row < 0 || row >= board.length ||
            col < 0 || col >= board[0].length) return false;

        if (board[row][col] != word.charAt(index)) return false;

        char temp = board[row][col];
        board[row][col] = '#'; // CHOOSE: mark as visited

        for (int[] dir : dirs) {
            if (backtrack(board, word, row + dir[0], col + dir[1], index + 1)) {
                board[row][col] = temp; // UNCHOOSE before returning
                return true;
            }
        }

        board[row][col] = temp; // UNCHOOSE: restore cell
        return false;
    }
}
```

**Note:** This modifies the board in-place instead of using a separate `visited[][]` array. Both approaches work. The in-place approach saves O(m*n) space but mutates input.

**Trace for board `[["A","B","C"],["S","F","C"],["A","D","E"]]`, word = "ABCCED":**

```
Start at (0,0)='A', index=0
  Mark (0,0)='#'
  Try (0,1)='B', index=1
    Mark (0,1)='#'
    Try (0,2)='C', index=2
      Mark (0,2)='#'
      Try (1,2)='C', index=3
        Mark (1,2)='#'
        Try (2,2)='E', index=4
          Mark (2,2)='#'
          Try (2,1)='D', index=5
            Mark (2,1)='#'
            index=6 == word.length() -> return true
```

**Time:** O(m * n * 4^L) where L = word length. **Space:** O(L) recursion depth.

---

#### LC 22 — Generate Parentheses

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

**Problem:** Given `n` pairs of parentheses, generate all combinations of well-formed parentheses.

**Approach:** Track open and close counts. Add `(` if open < n. Add `)` if close < open. This is backtracking with built-in validity constraints.

```java
class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(n, 0, 0, new StringBuilder(), result);
        return result;
    }

    private void backtrack(int n, int open, int close, StringBuilder path,
                           List<String> result) {
        if (path.length() == 2 * n) {
            result.add(path.toString());
            return;
        }

        if (open < n) {
            path.append('(');                              // CHOOSE
            backtrack(n, open + 1, close, path, result);  // EXPLORE
            path.deleteCharAt(path.length() - 1);          // UNCHOOSE
        }

        if (close < open) {
            path.append(')');                              // CHOOSE
            backtrack(n, open, close + 1, path, result);  // EXPLORE
            path.deleteCharAt(path.length() - 1);          // UNCHOOSE
        }
    }
}
```

**Decision tree for n=2:**

```
backtrack(open=0, close=0, path="")
  open<2: append '('
  backtrack(open=1, close=0, path="(")
    open<2: append '('
    backtrack(open=2, close=0, path="((")
      open==2: can't add '('
      close<open: append ')'
      backtrack(open=2, close=1, path="(()")
        close<open: append ')'
        backtrack(open=2, close=2, path="(())") -> ADD "(())"
    close<open: append ')'
    backtrack(open=1, close=1, path="()")
      open<2: append '('
      backtrack(open=2, close=1, path="()(")
        close<open: append ')'
        backtrack(open=2, close=2, path="()()") -> ADD "()()"

Result: ["(())", "()()"]
```

**Why this generates only valid parentheses:** The constraint `close < open` ensures we never close more than we've opened. The constraint `open < n` ensures we don't exceed n pairs. Together, they guarantee every generated string is valid.

**Time:** O(4^n / sqrt(n)) — the nth Catalan number. **Space:** O(n).

---

## Common Mistakes

### 1. Not Undoing State (Forgetting to Remove Last Element)

```java
// WRONG: path is never restored
for (int i = start; i < nums.length; i++) {
    path.add(nums[i]);
    backtrack(nums, i + 1, path, result);
    // Missing: path.remove(path.size() - 1);
}

// CORRECT
for (int i = start; i < nums.length; i++) {
    path.add(nums[i]);
    backtrack(nums, i + 1, path, result);
    path.remove(path.size() - 1); // ALWAYS undo
}
```

This is the most common mistake. Without the undo step, the path keeps growing and you get garbage results.

### 2. Wrong Duplicate Skip Condition

```java
// WRONG for subsets/combinations: skips too aggressively
if (i > 0 && nums[i] == nums[i - 1]) continue; // Should be i > start

// CORRECT
if (i > start && nums[i] == nums[i - 1]) continue;

// WRONG for permutations: wrong condition
if (i > 0 && nums[i] == nums[i - 1] && used[i - 1]) continue; // Should be !used[i-1]

// CORRECT
if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;
```

### 3. Not Sorting Before Duplicate Handling

```java
// WRONG: duplicate detection requires sorted array
// Without sort, nums[i] == nums[i-1] doesn't catch all duplicates
private void backtrack(int[] nums, ...) {
    // nums is not sorted — duplicate check is unreliable
}

// CORRECT: sort in the public method before calling backtrack
public List<List<Integer>> subsetsWithDup(int[] nums) {
    Arrays.sort(nums); // REQUIRED
    backtrack(nums, 0, ...);
}
```

### 4. Shallow Copy of Path

```java
// WRONG: adds reference to path, which gets modified later
result.add(path); // path is a reference, not a copy

// CORRECT: add a copy
result.add(new ArrayList<>(path));
```

This is subtle. If you add `path` directly, all entries in `result` point to the same list. By the time the algorithm finishes, they all show the final (empty) state of `path`.

### 5. Modifying Input Array Without Restoring

```java
// WRONG: swap-based permutation without restoring
for (int i = start; i < nums.length; i++) {
    swap(nums, start, i);
    backtrack(nums, start + 1, result);
    // Missing: swap(nums, start, i); // swap back
}

// CORRECT
for (int i = start; i < nums.length; i++) {
    swap(nums, start, i);
    backtrack(nums, start + 1, result);
    swap(nums, start, i); // Restore
}
```

### 6. Wrong Base Case

```java
// WRONG: base case never triggers for subsets (we add at every node)
if (start == nums.length) {
    result.add(new ArrayList<>(path));
    return;
}
// This only adds the full-length subset, missing all partial subsets

// CORRECT for subsets: add at every node, no explicit base case needed
result.add(new ArrayList<>(path)); // Add before the loop
for (int i = start; i < nums.length; i++) { ... }

// CORRECT for permutations: add only when path is complete
if (path.size() == nums.length) {
    result.add(new ArrayList<>(path));
    return;
}
```

### 7. Stack Overflow on Large Inputs

Backtracking has recursion depth proportional to the solution length. For very large inputs, consider:
- Iterative approaches (rare for backtracking)
- Increasing JVM stack size: `java -Xss64m Solution`
- Pruning aggressively to reduce depth

### 8. Integer Overflow in Pruning

```java
// WRONG: can overflow if candidates[i] is large
if (remaining - candidates[i] < 0) break;

// CORRECT: compare directly
if (candidates[i] > remaining) break;
```

---

## Backtracking vs DP vs BFS

### Backtracking vs Dynamic Programming

| Aspect | Backtracking | Dynamic Programming |
|--------|-------------|---------------------|
| Goal | Explore ALL paths, collect solutions | Find optimal solution, avoid recomputation |
| Subproblems | Overlapping subproblems are recomputed | Overlapping subproblems are cached |
| State | Explicit undo (backtrack) | No undo, build from smaller subproblems |
| Output | All solutions or first valid solution | Single optimal value or solution |
| When to use | Need all solutions, constraint satisfaction | Optimization, counting, overlapping subproblems |

**Example:** Coin change problem.
- Backtracking: finds ALL combinations of coins that sum to target. O(2^n).
- DP: finds the MINIMUM number of coins. O(n * amount).

If the problem asks "find all" or "is it possible," think backtracking. If it asks "minimum," "maximum," or "count," think DP.

### Backtracking vs BFS

| Aspect | Backtracking (DFS) | BFS |
|--------|-------------------|-----|
| Space | O(depth) — stack | O(width) — queue |
| Shortest path | No | Yes (unweighted) |
| All solutions | Yes | Possible but memory-intensive |
| Pruning | Natural (skip branches) | Harder to prune |
| When to use | All solutions, constraint satisfaction | Shortest path, level-order traversal |

**Example:** Word ladder.
- BFS: finds the SHORTEST transformation sequence. Natural fit.
- Backtracking: finds ALL transformation sequences. Exponential space.

### Time Complexity Summary

| Problem type | Time complexity | Explanation |
|---|---|---|
| Subsets | O(n * 2^n) | 2^n subsets, O(n) to copy each |
| Permutations | O(n * n!) | n! permutations, O(n) to copy each |
| Combinations C(n,k) | O(k * C(n,k)) | C(n,k) combinations, O(k) to copy each |
| Combination Sum | O(n^(T/M)) | T=target, M=min candidate |
| N-Queens | O(n!) | n choices for row 0, n-1 for row 1, etc. |
| Sudoku | O(9^m) | m empty cells, 9 choices each |
| Word Search | O(m*n*4^L) | L=word length, 4 directions |

---

## Cheat Sheet

### The Universal Template

```java
void backtrack(State state, List<State> result) {
    if (isGoal(state)) {
        result.add(copy(state));
        return; // or don't return for subsets (add at every node)
    }

    for (Choice choice : getChoices(state)) {
        if (!isValid(state, choice)) continue; // Pruning

        applyChoice(state, choice);   // CHOOSE
        backtrack(state, result);      // EXPLORE
        undoChoice(state, choice);     // UNCHOOSE
    }
}
```

### When to Add to Result

| Problem | When to add |
|---------|-------------|
| Subsets | At every node (before the loop) |
| Permutations | Only at leaves (when path.size() == n) |
| Combinations | Only at leaves (when path.size() == k) |
| Combination Sum | Only when remaining == 0 |
| N-Queens | Only when all rows are filled |

### Start Index Rules

| Problem | Recurse with |
|---------|-------------|
| Subsets, Combinations | `i + 1` (no reuse) |
| Combination Sum (reuse allowed) | `i` (same element can repeat) |
| Permutations | `0` with `used[]` array |

### Duplicate Handling Quick Reference

```java
// Subsets II, Combination Sum II (sort first)
if (i > start && nums[i] == nums[i - 1]) continue;

// Permutations II (sort first)
if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;
```

### Pruning Patterns

```java
// Sorted array: stop when candidate exceeds remaining
if (candidates[i] > remaining) break; // break, not continue

// Combinations: stop when not enough elements remain
for (int i = start; i <= n - (k - path.size()) + 1; i++)

// Palindrome partitioning: skip non-palindromes
if (!isPalindrome(s, start, end)) continue;

// N-Queens: check conflicts before placing
if (!isValid(queens, row, col)) continue;
```

### Problem Recognition

| Signal in problem | Pattern |
|---|---|
| "all subsets" / "power set" | Subsets template |
| "all permutations" / "all orderings" | Permutations template |
| "combinations of size k" | Combinations template |
| "combinations that sum to target" | Combination Sum template |
| "partition string into..." | String backtracking |
| "place N queens" / "fill Sudoku" | Constraint satisfaction |
| "find word in grid" | Grid DFS with backtracking |
| "generate valid parentheses" | Constrained string building |

---

## Study Roadmap

### Week 1: Foundations (Days 1-5)

**Day 1-2: Subsets**
- LC 78 (Subsets) — draw the decision tree by hand first
- LC 90 (Subsets II) — understand the `i > start` duplicate condition

**Day 3-4: Permutations**
- LC 46 (Permutations) — trace through the `used[]` array manually
- LC 47 (Permutations II) — understand `!used[i-1]` condition

**Day 5: Combinations**
- LC 77 (Combinations) — add the pruning optimization
- LC 216 (Combination Sum III) — two constraints at once

### Week 2: Intermediate (Days 6-10)

**Day 6-7: Combination Sum variants**
- LC 39 (Combination Sum) — reuse allowed, `recurse(i)` not `recurse(i+1)`
- LC 40 (Combination Sum II) — no reuse + duplicates

**Day 8: String backtracking**
- LC 17 (Letter Combinations) — phone keypad mapping
- LC 22 (Generate Parentheses) — constraints built into the recursion

**Day 9-10: More string backtracking**
- LC 131 (Palindrome Partitioning) — precompute palindromes with DP
- LC 93 (Restore IP Addresses) — multiple pruning conditions

### Week 3: Advanced (Days 11-15)

**Day 11-12: Grid search**
- LC 79 (Word Search) — in-place visited marking

**Day 13-14: Constraint satisfaction**
- LC 51 (N-Queens) — diagonal conflict detection
- LC 37 (Sudoku Solver) — return boolean for early termination

**Day 15: Review**
- Re-solve LC 78, 46, 51 from memory
- Time yourself: each should take under 15 minutes
- Focus on writing the `choose → explore → unchoose` pattern cleanly

### The 15 Problems in Priority Order

| Priority | Problem | Why |
|---|---|---|
| 1 | LC 78 Subsets | Foundation of all backtracking |
| 2 | LC 46 Permutations | Second fundamental pattern |
| 3 | LC 39 Combination Sum | Reuse variant, common in interviews |
| 4 | LC 22 Generate Parentheses | Most common backtracking interview question |
| 5 | LC 79 Word Search | Grid backtracking, very common |
| 6 | LC 90 Subsets II | Duplicate handling for subsets |
| 7 | LC 47 Permutations II | Duplicate handling for permutations |
| 8 | LC 40 Combination Sum II | Duplicate handling for combinations |
| 9 | LC 17 Letter Combinations | String mapping pattern |
| 10 | LC 131 Palindrome Partitioning | String partitioning + DP optimization |
| 11 | LC 51 N-Queens | Classic constraint satisfaction |
| 12 | LC 77 Combinations | Pure combination with pruning |
| 13 | LC 216 Combination Sum III | Multiple constraints |
| 14 | LC 93 Restore IP Addresses | Multiple pruning conditions |
| 15 | LC 37 Sudoku Solver | Hard constraint satisfaction |

### Interview Tips

**Before coding:**
1. Identify the category: subsets, permutations, combinations, or constraint satisfaction?
2. Ask: are there duplicates in the input? If yes, you need sort + skip.
3. Ask: can elements be reused? Determines `i` vs `i+1` in recursion.

**While coding:**
1. Write the base case first.
2. Write the loop with `choose → explore → unchoose` explicitly.
3. Add pruning after the basic version works.

**After coding:**
1. Trace through a small example by hand.
2. Check: are you copying the path when adding to result?
3. Check: are you undoing ALL state changes (not just the path)?

**Common follow-ups:**
- "Can you optimize this?" — Add pruning, precompute palindromes, use sets for O(1) conflict detection.
- "What's the time complexity?" — Count the number of nodes in the decision tree.
- "What if the input has duplicates?" — Sort + skip condition.

### Complexity Reference Card

```
Problem                  | Time          | Space  | Notes
-------------------------|---------------|--------|---------------------------
LC 78  Subsets           | O(n * 2^n)    | O(n)   | 2^n subsets
LC 90  Subsets II        | O(n * 2^n)    | O(n)   | Same, fewer due to pruning
LC 46  Permutations      | O(n * n!)     | O(n)   | n! permutations
LC 47  Permutations II   | O(n * n!)     | O(n)   | Fewer due to pruning
LC 77  Combinations      | O(k*C(n,k))   | O(k)   | C(n,k) results
LC 39  Combination Sum   | O(n^(T/M))    | O(T/M) | T=target, M=min candidate
LC 40  Combination Sum II| O(2^n)        | O(n)   | Each element used once
LC 216 Comb Sum III      | O(C(9,k))     | O(k)   | Fixed range 1-9
LC 17  Letter Combos     | O(4^n * n)    | O(n)   | 4 = max letters per digit
LC 22  Gen Parentheses   | O(4^n/sqrt(n))| O(n)   | nth Catalan number
LC 79  Word Search       | O(m*n*4^L)    | O(L)   | L = word length
LC 51  N-Queens          | O(n!)         | O(n)   | n choices per row
LC 37  Sudoku Solver     | O(9^m)        | O(m)   | m = empty cells
LC 131 Palindrome Part.  | O(n * 2^n)    | O(n^2) | n^2 for DP table
LC 93  Restore IP        | O(1)          | O(1)   | Bounded input size
```

### State Management Patterns

Different problems require different state to undo:

```java
// Pattern 1: List path — remove last element
path.add(x);
recurse();
path.remove(path.size() - 1);

// Pattern 2: StringBuilder — delete last char
sb.append(c);
recurse();
sb.deleteCharAt(sb.length() - 1);

// Pattern 3: boolean[] used array — toggle
used[i] = true;
recurse();
used[i] = false;

// Pattern 4: Grid cell — restore original value
char temp = board[r][c];
board[r][c] = '#';
recurse();
board[r][c] = temp;

// Pattern 5: Multiple state variables — undo all
cols.add(col); diag1.add(r-col); diag2.add(r+col);
recurse();
cols.remove(col); diag1.remove(r-col); diag2.remove(r+col);
```

The rule: **every state change in CHOOSE must have a corresponding undo in UNCHOOSE**. If you change 3 things, you undo 3 things. Missing even one causes subtle bugs that are hard to trace.

### Recognizing Backtracking vs Other Approaches

Sometimes backtracking is not the right tool. Here's how to tell:

**Use backtracking when:**
- Problem asks for ALL solutions (all subsets, all permutations)
- Problem has hard constraints that eliminate large branches early
- Input size is small (n <= 20 for subsets, n <= 8 for permutations)
- No overlapping subproblems (or you don't care about efficiency)

**Don't use backtracking when:**
- Problem asks for COUNT of solutions (use DP instead)
- Problem asks for MINIMUM/MAXIMUM (use DP or greedy)
- Input is large and you need polynomial time
- Subproblems overlap significantly (DP will be much faster)

**Example:** "Count the number of subsets that sum to target."
- Backtracking: O(2^n) — generates all subsets, counts valid ones
- DP (0/1 knapsack): O(n * target) — much faster

**Example:** "Find all subsets that sum to target."
- Backtracking: O(2^n) — the right tool, no way around it
- DP: can't enumerate all solutions efficiently

---

*Document 13 of 20 — DSA Prep Series*
