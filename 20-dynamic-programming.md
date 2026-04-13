# Topic 20: Dynamic Programming

> **Frequency**: ~15% of FAANG interviews — the highest of any single topic.
> **Reputation**: The most feared topic in technical interviews.
> **Reality**: DP is recursion + memoization, or equivalently, filling a table bottom-up. The code is rarely complex. The hard part is defining the **state** and the **transition**.

**Top companies**: Google, Amazon, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

**What this document covers**:
- 1D DP (climbing stairs, house robber, coin change, LIS)
- 2D DP (grid paths, edit distance, LCS)
- Knapsack variants (0/1, unbounded, partition, target sum)
- String DP (palindromes, edit distance, regex matching)
- Interval DP (burst balloons, matrix chain)
- State Machine DP (stock buy/sell with cooldown/fee)
- Tree DP (house robber III)
- Bitmask DP (brief overview)

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5](#eli5)
3. [Templates](#templates)
4. [Real-World Applications](#real-world-applications)
5. [Problems by Category](#problems-by-category)
   - [Category A: 1D DP](#category-a-1d-dp)
   - [Category B: 2D DP / Grid](#category-b-2d-dp--grid)
   - [Category C: String DP](#category-c-string-dp)
   - [Category D: Knapsack](#category-d-knapsack)
   - [Category E: State Machine DP](#category-e-state-machine-dp)
   - [Category F: Advanced](#category-f-advanced)
6. [How to Approach a New DP Problem](#how-to-approach-a-new-dp-problem)
7. [Common Mistakes](#common-mistakes)
8. [DP vs Other Techniques](#dp-vs-other-techniques)
9. [Cheat Sheet and 6-Week Roadmap](#cheat-sheet-and-6-week-roadmap)

---

## Core Concept

### What Makes a Problem "DP-able"?

Two properties must hold:

**1. Optimal Substructure**: The optimal solution to the problem contains optimal solutions to its subproblems. If you're finding the shortest path from A to C through B, the path A→B and B→C must each be shortest paths themselves.

**2. Overlapping Subproblems**: The same subproblems are solved multiple times. In naive Fibonacci, `fib(3)` gets computed over and over. DP caches it.

If a problem has optimal substructure but no overlapping subproblems, use Divide and Conquer (merge sort, quicksort). If it has overlapping subproblems but no optimal substructure, you might need backtracking. DP needs both.

---

### The 4-Step Framework

Every DP problem follows this exact sequence. Internalize it.

**Step 1: Define the State**
What does `dp[i]` (or `dp[i][j]`) represent? This is the hardest step. Be precise. Write it in English before writing code.

Examples:
- `dp[i]` = number of ways to reach stair `i`
- `dp[i]` = maximum money robbed from houses `0..i`
- `dp[i][j]` = minimum edits to convert `s[0..i]` to `t[0..j]`
- `dp[i][j]` = length of LCS of `s[0..i]` and `t[0..j]`

**Step 2: Write the Transition**
How does `dp[i]` relate to smaller subproblems? This is the recurrence relation.

Examples:
- `dp[i] = dp[i-1] + dp[i-2]` (climbing stairs)
- `dp[i] = max(dp[i-1], dp[i-2] + nums[i])` (house robber)
- `dp[i][j] = dp[i-1][j-1]` if chars match, else `1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])` (edit distance)

**Step 3: Identify Base Cases**
What are the known answers that don't depend on smaller subproblems?

Examples:
- `dp[0] = 1, dp[1] = 1` (climbing stairs)
- `dp[0] = 0` (coin change: 0 coins needed for amount 0)
- `dp[i][0] = i, dp[0][j] = j` (edit distance: cost to delete all chars)

**Step 4: Identify the Answer**
Which cell holds the final answer? Often `dp[n]` or `dp[m][n]`, but not always.

---

### Top-Down vs Bottom-Up

Both approaches produce identical results. Choose based on context.

**Top-Down (Memoization)**:
- Write the natural recursion first
- Add a cache (`HashMap` or array) to store computed results
- Easier to write, especially for complex state spaces
- May have recursion stack overhead
- Natural for problems where not all subproblems need solving

```java
// Top-down template
Map<Integer, Integer> memo = new HashMap<>();

int solve(int i) {
    if (i <= 1) return i;           // base case
    if (memo.containsKey(i)) return memo.get(i);  // cache hit
    int result = solve(i-1) + solve(i-2);          // transition
    memo.put(i, result);            // cache store
    return result;
}
```

**Bottom-Up (Tabulation)**:
- Fill a table iteratively, starting from base cases
- No recursion overhead
- Easier to optimize space (you can see exactly which cells you need)
- Sometimes requires careful ordering of loops

```java
// Bottom-up template
int[] dp = new int[n+1];
dp[0] = 0; dp[1] = 1;              // base cases
for (int i = 2; i <= n; i++) {
    dp[i] = dp[i-1] + dp[i-2];    // transition
}
return dp[n];                       // answer
```

---

### Space Optimization

If `dp[i]` only depends on `dp[i-1]` and `dp[i-2]`, you don't need the full array. Use two variables.

```java
// O(n) space → O(1) space
int prev2 = 0, prev1 = 1;
for (int i = 2; i <= n; i++) {
    int curr = prev1 + prev2;
    prev2 = prev1;
    prev1 = curr;
}
return prev1;
```

For 2D DP where `dp[i][j]` only depends on row `i-1`, use a 1D array and update in-place (or use two 1D arrays).

---

### DP Categories at a Glance

| Category | Key Idea | Representative Problem |
|---|---|---|
| 1D DP | State is a single index | Climbing Stairs, House Robber |
| 2D DP | State is two indices | Grid Paths, Edit Distance |
| Knapsack | Include/exclude items | Partition Equal Subset Sum |
| String DP | State over two strings | LCS, Palindrome |
| Interval DP | State is a range [i,j] | Burst Balloons |
| State Machine | Discrete states with transitions | Stock Buy/Sell |
| Tree DP | State at each tree node | House Robber III |
| Bitmask DP | State encodes a subset | TSP, Assignment |

---

## ELI5

You're climbing a staircase. At each step, you can go up 1 or 2 stairs. How many different ways can you reach the top?

The naive approach: try every combination. But you keep recalculating the same things. "How many ways to reach step 5?" gets computed dozens of times.

The DP approach: write the answer for each step on a sticky note. Step 1: 1 way. Step 2: 2 ways. Step 3: ways to reach step 2 + ways to reach step 1 = 3 ways. Step 4: 3 + 2 = 5 ways. You never recalculate anything. Each sticky note gets filled in exactly once.

That's DP: **solve once, remember forever**.

The insight that makes DP click: "To reach step N, I must have come from step N-1 or step N-2. So the number of ways to reach N equals the number of ways to reach N-1 plus the number of ways to reach N-2." That's the transition. Once you see it, the code writes itself.

---

## Templates

### Template 1: 1D DP (Linear)

**Pattern**: State is a single index. Each state depends on a constant number of previous states.

```java
// State: dp[i] = answer for subproblem of size i
// Transition: dp[i] = f(dp[i-1], dp[i-2], ...)
// Base case: dp[0], dp[1] = known values
// Answer: dp[n]

// Generic 1D DP
public int solve1D(int n) {
    if (n <= 1) return n;
    
    int[] dp = new int[n + 1];
    dp[0] = BASE_0;
    dp[1] = BASE_1;
    
    for (int i = 2; i <= n; i++) {
        dp[i] = transition(dp[i-1], dp[i-2]);
    }
    
    return dp[n];
}

// Space-optimized version (when dp[i] depends only on dp[i-1] and dp[i-2])
public int solve1DOptimized(int n) {
    if (n <= 1) return n;
    
    int prev2 = BASE_0;
    int prev1 = BASE_1;
    
    for (int i = 2; i <= n; i++) {
        int curr = transition(prev1, prev2);
        prev2 = prev1;
        prev1 = curr;
    }
    
    return prev1;
}
```

---

### Template 2: 2D DP (Grid / Two Sequences)

**Pattern**: State is two indices. Common for grid traversal or comparing two strings/arrays.

```java
// State: dp[i][j] = answer for subproblem involving first i rows and j cols
//        (or first i chars of s and first j chars of t)
// Transition: dp[i][j] = f(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])
// Base case: dp[0][j] and dp[i][0] = boundary conditions
// Answer: dp[m][n]

public int solve2D(int m, int n) {
    int[][] dp = new int[m + 1][n + 1];
    
    // Base cases: first row and first column
    for (int i = 0; i <= m; i++) dp[i][0] = BASE_ROW;
    for (int j = 0; j <= n; j++) dp[0][j] = BASE_COL;
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            dp[i][j] = transition(dp[i-1][j], dp[i][j-1], dp[i-1][j-1]);
        }
    }
    
    return dp[m][n];
}

// Space-optimized: O(m*n) → O(n) using rolling array
public int solve2DOptimized(int m, int n) {
    int[] dp = new int[n + 1];
    
    // Initialize base case for row 0
    for (int j = 0; j <= n; j++) dp[j] = BASE_COL_J;
    
    for (int i = 1; i <= m; i++) {
        int prev = dp[0];           // stores dp[i-1][j-1]
        dp[0] = BASE_ROW_I;         // update dp[i][0]
        for (int j = 1; j <= n; j++) {
            int temp = dp[j];       // save dp[i-1][j] before overwriting
            dp[j] = transition(dp[j], dp[j-1], prev);
            prev = temp;
        }
    }
    
    return dp[n];
}
```

---

### Template 3: 0/1 Knapsack

**Pattern**: For each item, decide to include or exclude. Each item can be used at most once.

```java
// State: dp[i][w] = max value using first i items with capacity w
// Transition: dp[i][w] = max(dp[i-1][w],           // exclude item i
//                            dp[i-1][w-weight[i]] + value[i])  // include item i
// Base case: dp[0][w] = 0 (no items, no value)
// Answer: dp[n][W]

public int knapsack01(int[] weights, int[] values, int W) {
    int n = weights.length;
    int[][] dp = new int[n + 1][W + 1];
    
    for (int i = 1; i <= n; i++) {
        for (int w = 0; w <= W; w++) {
            dp[i][w] = dp[i-1][w];  // exclude item i
            if (w >= weights[i-1]) {
                dp[i][w] = Math.max(dp[i][w],
                    dp[i-1][w - weights[i-1]] + values[i-1]);  // include
            }
        }
    }
    
    return dp[n][W];
}

// Space-optimized: iterate w from W down to weight[i]
// CRITICAL: must go right-to-left to avoid using item twice
public int knapsack01Optimized(int[] weights, int[] values, int W) {
    int n = weights.length;
    int[] dp = new int[W + 1];
    
    for (int i = 0; i < n; i++) {
        for (int w = W; w >= weights[i]; w--) {  // RIGHT TO LEFT
            dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
        }
    }
    
    return dp[W];
}
```

---

### Template 4: Unbounded Knapsack

**Pattern**: Each item can be used unlimited times.

```java
// State: dp[w] = max value achievable with capacity w
// Transition: dp[w] = max over all items i: dp[w - weight[i]] + value[i]
// Key difference from 0/1: iterate w LEFT TO RIGHT (allows reuse)

public int unboundedKnapsack(int[] weights, int[] values, int W) {
    int n = weights.length;
    int[] dp = new int[W + 1];
    
    for (int w = 1; w <= W; w++) {
        for (int i = 0; i < n; i++) {
            if (w >= weights[i]) {
                dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
            }
        }
    }
    
    return dp[W];
}

// Coin change framing: minimize coins (unbounded)
public int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);  // "infinity"
    dp[0] = 0;
    
    for (int a = 1; a <= amount; a++) {
        for (int coin : coins) {
            if (a >= coin) {
                dp[a] = Math.min(dp[a], dp[a - coin] + 1);
            }
        }
    }
    
    return dp[amount] > amount ? -1 : dp[amount];
}
```

---

### Template 5: String DP (Two-String Problems)

**Pattern**: State involves positions in two strings. Covers LCS, edit distance, regex matching.

```java
// State: dp[i][j] = answer for s[0..i-1] and t[0..j-1]
// Transition: depends on whether s[i-1] == t[j-1]
// Base case: dp[i][0] and dp[0][j] = empty string cases

// LCS template
public int lcs(String s, String t) {
    int m = s.length(), n = t.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s.charAt(i-1) == t.charAt(j-1)) {
                dp[i][j] = dp[i-1][j-1] + 1;
            } else {
                dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
            }
        }
    }
    
    return dp[m][n];
}

// Edit distance template
public int editDistance(String s, String t) {
    int m = s.length(), n = t.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 0; i <= m; i++) dp[i][0] = i;  // delete all of s
    for (int j = 0; j <= n; j++) dp[0][j] = j;  // insert all of t
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s.charAt(i-1) == t.charAt(j-1)) {
                dp[i][j] = dp[i-1][j-1];  // no operation needed
            } else {
                dp[i][j] = 1 + Math.min(dp[i-1][j-1],   // replace
                               Math.min(dp[i-1][j],       // delete from s
                                        dp[i][j-1]));     // insert into s
            }
        }
    }
    
    return dp[m][n];
}
```

---

### Template 6: Interval DP

**Pattern**: State is a range `[i, j]`. Solve smaller intervals first, build up to the full range. Loop order: length first, then start index.

```java
// State: dp[i][j] = answer for subarray/substring from index i to j
// Transition: dp[i][j] = optimal over all split points k in [i, j]
// Base case: dp[i][i] = single element answer
// Answer: dp[0][n-1]

public int intervalDP(int[] arr) {
    int n = arr.length;
    int[][] dp = new int[n][n];
    
    // Base case: single elements
    for (int i = 0; i < n; i++) {
        dp[i][i] = BASE;
    }
    
    // Fill by increasing length
    for (int len = 2; len <= n; len++) {
        for (int i = 0; i <= n - len; i++) {
            int j = i + len - 1;
            dp[i][j] = WORST;  // initialize to worst
            
            // Try all split points
            for (int k = i; k < j; k++) {
                dp[i][j] = optimal(dp[i][j],
                    combine(dp[i][k], dp[k+1][j], arr, i, j, k));
            }
        }
    }
    
    return dp[0][n-1];
}
```

---

### Template 7: State Machine DP

**Pattern**: At each step, you're in one of several discrete states. Transitions between states have costs or gains.

```java
// Stock buy/sell state machine:
// States: HOLD (holding stock), SOLD (just sold), COOLDOWN (resting)
//
//   +--------+   sell   +--------+
//   |  HOLD  |--------->|  SOLD  |
//   +--------+          +--------+
//       ^                    |
//       | buy                | (automatic)
//       |                    v
//   +----------+        +----------+
//   | COOLDOWN |<-------|  COOLDOWN|
//   +----------+        +----------+
//
// hold[i]  = max profit on day i while holding stock
// sold[i]  = max profit on day i just after selling
// cool[i]  = max profit on day i in cooldown (can't buy)

public int stockWithCooldown(int[] prices) {
    int n = prices.length;
    int hold = Integer.MIN_VALUE;  // haven't bought yet
    int sold = 0;
    int cool = 0;
    
    for (int price : prices) {
        int prevHold = hold;
        int prevSold = sold;
        int prevCool = cool;
        
        hold = Math.max(prevHold, prevCool - price);  // keep holding or buy
        sold = prevHold + price;                       // sell today
        cool = Math.max(prevCool, prevSold);           // rest or continue resting
    }
    
    return Math.max(sold, cool);
}
```

---

### Template 8: Tree DP

**Pattern**: State is defined at each node. Solve children first (post-order), combine results at parent.

```java
// State: for each node, compute two values:
//   rob[node]   = max profit if we rob this node
//   skip[node]  = max profit if we skip this node
//
// Transition:
//   rob[node]  = node.val + skip[left] + skip[right]
//   skip[node] = max(rob[left], skip[left]) + max(rob[right], skip[right])

public int treeDP(TreeNode root) {
    int[] result = dfs(root);
    return Math.max(result[0], result[1]);
}

// Returns [rob, skip] for this subtree
private int[] dfs(TreeNode node) {
    if (node == null) return new int[]{0, 0};
    
    int[] left = dfs(node.left);
    int[] right = dfs(node.right);
    
    int rob = node.val + left[1] + right[1];
    int skip = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
    
    return new int[]{rob, skip};
}
```

---

## Real-World Applications

**GPS Route Optimization**: Finding the shortest path in a road network uses DP (Bellman-Ford, Floyd-Warshall). The state is the current node; the transition is relaxing edges.

**Text Justification (Knuth-Plass)**: TeX uses DP to break paragraphs into lines. The state is the word index; the transition minimizes a badness function over all possible line breaks. This is interval DP in disguise.

**Speech Recognition (Viterbi Algorithm)**: Hidden Markov Models use DP to find the most likely sequence of words given audio. The state is (time step, phoneme); the transition follows acoustic and language model probabilities. This is state machine DP.

**Bioinformatics (Sequence Alignment)**: Comparing DNA or protein sequences uses edit distance (Needleman-Wunsch, Smith-Waterman). The state is positions in two sequences; the transition allows substitutions, insertions, and deletions with different costs.

**Financial Portfolio Optimization**: Selecting assets under budget constraints is a knapsack problem. The state is (asset index, remaining budget); the transition includes or excludes each asset.

**Compiler Optimization (Instruction Selection)**: Compilers use tree DP to select the best machine instructions for an expression tree. The state is (tree node, register assignment); the transition picks the cheapest instruction covering each subtree.

---

## Problems by Category

---

## Category A: 1D DP

---

### LC 70 — Climbing Stairs

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty**: Easy
**Pattern**: 1D DP, Fibonacci variant

**Problem**: You can climb 1 or 2 steps at a time. How many distinct ways to reach step `n`?

**4-Step Framework**:
1. **State**: `dp[i]` = number of distinct ways to reach step `i`
2. **Transition**: `dp[i] = dp[i-1] + dp[i-2]` (came from step i-1 or step i-2)
3. **Base case**: `dp[1] = 1`, `dp[2] = 2`
4. **Answer**: `dp[n]`

**Top-Down**:
```java
public int climbStairs(int n) {
    int[] memo = new int[n + 1];
    return helper(n, memo);
}

private int helper(int n, int[] memo) {
    if (n <= 2) return n;
    if (memo[n] != 0) return memo[n];
    memo[n] = helper(n - 1, memo) + helper(n - 2, memo);
    return memo[n];
}
```

**Bottom-Up**:
```java
public int climbStairs(int n) {
    if (n <= 2) return n;
    int[] dp = new int[n + 1];
    dp[1] = 1;
    dp[2] = 2;
    for (int i = 3; i <= n; i++) {
        dp[i] = dp[i-1] + dp[i-2];
    }
    return dp[n];
}
```

**Space-Optimized** (O(1)):
```java
public int climbStairs(int n) {
    if (n <= 2) return n;
    int prev2 = 1, prev1 = 2;
    for (int i = 3; i <= n; i++) {
        int curr = prev1 + prev2;
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
```

**Complexity**: Time O(n), Space O(1) optimized.

---

### LC 198 — House Robber *(MUST DRY RUN)*

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty**: Medium
**Pattern**: 1D DP, include/exclude

**Problem**: Rob houses in a row. Adjacent houses have alarms. Maximize total money.

**4-Step Framework**:
1. **State**: `dp[i]` = maximum money robbed from houses `0..i`
2. **Transition**: `dp[i] = max(dp[i-1], dp[i-2] + nums[i])` — skip house i, or rob it (can't rob i-1)
3. **Base case**: `dp[0] = nums[0]`, `dp[1] = max(nums[0], nums[1])`
4. **Answer**: `dp[n-1]`

**Dry Run** with `nums = [2, 7, 9, 3, 1]`:

```
i=0: dp[0] = 2                          (only house 0)
i=1: dp[1] = max(2, 7) = 7             (skip 0 and take 7, or take 2)
i=2: dp[2] = max(dp[1], dp[0]+9)
           = max(7, 2+9) = max(7, 11) = 11   (rob houses 0 and 2)
i=3: dp[3] = max(dp[2], dp[1]+3)
           = max(11, 7+3) = max(11, 10) = 11  (keep 11)
i=4: dp[4] = max(dp[3], dp[2]+1)
           = max(11, 11+1) = max(11, 12) = 12  (rob houses 0, 2, 4)

Answer: dp[4] = 12
```

**Table**:
```
Index:  0    1    2    3    4
nums:   2    7    9    3    1
dp:     2    7   11   11   12
```

**Top-Down**:
```java
public int rob(int[] nums) {
    int[] memo = new int[nums.length];
    Arrays.fill(memo, -1);
    return helper(nums, nums.length - 1, memo);
}

private int helper(int[] nums, int i, int[] memo) {
    if (i < 0) return 0;
    if (i == 0) return nums[0];
    if (memo[i] != -1) return memo[i];
    memo[i] = Math.max(helper(nums, i-1, memo),
                       helper(nums, i-2, memo) + nums[i]);
    return memo[i];
}
```

**Bottom-Up**:
```java
public int rob(int[] nums) {
    int n = nums.length;
    if (n == 1) return nums[0];
    
    int[] dp = new int[n];
    dp[0] = nums[0];
    dp[1] = Math.max(nums[0], nums[1]);
    
    for (int i = 2; i < n; i++) {
        dp[i] = Math.max(dp[i-1], dp[i-2] + nums[i]);
    }
    
    return dp[n-1];
}
```

**Space-Optimized**:
```java
public int rob(int[] nums) {
    int n = nums.length;
    if (n == 1) return nums[0];
    
    int prev2 = nums[0];
    int prev1 = Math.max(nums[0], nums[1]);
    
    for (int i = 2; i < n; i++) {
        int curr = Math.max(prev1, prev2 + nums[i]);
        prev2 = prev1;
        prev1 = curr;
    }
    
    return prev1;
}
```

**Complexity**: Time O(n), Space O(1) optimized.

---

### LC 213 — House Robber II

**Companies**: Amazon, Google, Meta, Microsoft
**Difficulty**: Medium
**Pattern**: 1D DP, circular array

**Problem**: Houses arranged in a circle. First and last are adjacent. Maximize robbery.

**Key Insight**: In a circle, you can't rob both house 0 and house n-1. So run House Robber twice:
- Once on `nums[0..n-2]` (exclude last)
- Once on `nums[1..n-1]` (exclude first)
- Return the max of both.

```java
public int rob(int[] nums) {
    int n = nums.length;
    if (n == 1) return nums[0];
    if (n == 2) return Math.max(nums[0], nums[1]);
    
    return Math.max(
        robRange(nums, 0, n - 2),
        robRange(nums, 1, n - 1)
    );
}

private int robRange(int[] nums, int start, int end) {
    int prev2 = 0, prev1 = 0;
    for (int i = start; i <= end; i++) {
        int curr = Math.max(prev1, prev2 + nums[i]);
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
```

**Complexity**: Time O(n), Space O(1).

---

### LC 322 — Coin Change *(MUST DRY RUN)*

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty**: Medium
**Pattern**: Unbounded knapsack, 1D DP

**Problem**: Given coin denominations and a target amount, find the minimum number of coins.

**4-Step Framework**:
1. **State**: `dp[a]` = minimum coins needed to make amount `a`
2. **Transition**: `dp[a] = min over all coins c: dp[a - c] + 1`
3. **Base case**: `dp[0] = 0` (0 coins for amount 0)
4. **Answer**: `dp[amount]` (or -1 if unreachable)

**Dry Run** with `coins = [1, 5, 6, 9]`, `amount = 11`:

```
dp[0] = 0
dp[1] = min(dp[0]+1) = 1                    (use coin 1)
dp[2] = min(dp[1]+1) = 2                    (use coin 1 twice)
dp[3] = min(dp[2]+1) = 3
dp[4] = min(dp[3]+1) = 4
dp[5] = min(dp[4]+1, dp[0]+1) = min(5,1) = 1   (use coin 5)
dp[6] = min(dp[5]+1, dp[1]+1, dp[0]+1) = min(2,2,1) = 1  (use coin 6)
dp[7] = min(dp[6]+1, dp[2]+1, dp[1]+1) = min(2,3,2) = 2  (coins 1+6)
dp[8] = min(dp[7]+1, dp[3]+1, dp[2]+1) = min(3,4,3) = 3
dp[9] = min(dp[8]+1, dp[4]+1, dp[3]+1, dp[0]+1) = min(4,5,4,1) = 1  (coin 9)
dp[10]= min(dp[9]+1, dp[5]+1, dp[4]+1, dp[1]+1) = min(2,2,5,2) = 2  (coins 1+9 or 5+5)
dp[11]= min(dp[10]+1, dp[6]+1, dp[5]+1, dp[2]+1) = min(3,2,2,3) = 2  (coins 5+6)

Answer: dp[11] = 2
```

**Table**:
```
Amount: 0  1  2  3  4  5  6  7  8  9  10  11
dp:     0  1  2  3  4  1  1  2  3  1   2   2
```

**Top-Down**:
```java
public int coinChange(int[] coins, int amount) {
    int[] memo = new int[amount + 1];
    Arrays.fill(memo, -2);  // -2 = unvisited, -1 = impossible
    return helper(coins, amount, memo);
}

private int helper(int[] coins, int rem, int[] memo) {
    if (rem == 0) return 0;
    if (rem < 0) return -1;
    if (memo[rem] != -2) return memo[rem];
    
    int min = Integer.MAX_VALUE;
    for (int coin : coins) {
        int sub = helper(coins, rem - coin, memo);
        if (sub >= 0) min = Math.min(min, sub + 1);
    }
    
    memo[rem] = (min == Integer.MAX_VALUE) ? -1 : min;
    return memo[rem];
}
```

**Bottom-Up**:
```java
public int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);  // "infinity"
    dp[0] = 0;
    
    for (int a = 1; a <= amount; a++) {
        for (int coin : coins) {
            if (a >= coin) {
                dp[a] = Math.min(dp[a], dp[a - coin] + 1);
            }
        }
    }
    
    return dp[amount] > amount ? -1 : dp[amount];
}
```

**Complexity**: Time O(amount * coins.length), Space O(amount).

---

### LC 300 — Longest Increasing Subsequence

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs
**Difficulty**: Medium
**Pattern**: 1D DP (O(n²)) or patience sorting (O(n log n))

**Problem**: Find the length of the longest strictly increasing subsequence.

**4-Step Framework (O(n²))**:
1. **State**: `dp[i]` = length of LIS ending at index `i`
2. **Transition**: `dp[i] = max over all j < i where nums[j] < nums[i]: dp[j] + 1`
3. **Base case**: `dp[i] = 1` (each element is an LIS of length 1)
4. **Answer**: `max(dp[0..n-1])`

**O(n²) Solution**:
```java
public int lengthOfLIS(int[] nums) {
    int n = nums.length;
    int[] dp = new int[n];
    Arrays.fill(dp, 1);
    int maxLen = 1;
    
    for (int i = 1; i < n; i++) {
        for (int j = 0; j < i; j++) {
            if (nums[j] < nums[i]) {
                dp[i] = Math.max(dp[i], dp[j] + 1);
            }
        }
        maxLen = Math.max(maxLen, dp[i]);
    }
    
    return maxLen;
}
```

**O(n log n) Solution** (patience sorting / binary search):

The idea: maintain a `tails` array where `tails[i]` is the smallest tail element of all increasing subsequences of length `i+1`. Use binary search to find where to place each element.

```java
public int lengthOfLIS(int[] nums) {
    List<Integer> tails = new ArrayList<>();
    
    for (int num : nums) {
        int lo = 0, hi = tails.size();
        
        // Binary search: find first tail >= num
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (tails.get(mid) < num) lo = mid + 1;
            else hi = mid;
        }
        
        if (lo == tails.size()) {
            tails.add(num);  // extend LIS
        } else {
            tails.set(lo, num);  // replace to keep tails small
        }
    }
    
    return tails.size();
}
```

**Dry Run** with `nums = [10, 9, 2, 5, 3, 7, 101, 18]`:
```
num=10: tails=[]  → lo=0=size → tails=[10]
num=9:  tails=[10] → lo=0 (10>=9) → tails=[9]
num=2:  tails=[9]  → lo=0 (9>=2)  → tails=[2]
num=5:  tails=[2]  → lo=1=size    → tails=[2,5]
num=3:  tails=[2,5]→ lo=1 (5>=3)  → tails=[2,3]
num=7:  tails=[2,3]→ lo=2=size    → tails=[2,3,7]
num=101:tails=[2,3,7]→lo=3=size   → tails=[2,3,7,101]
num=18: tails=[2,3,7,101]→lo=3(101>=18)→tails=[2,3,7,18]

Answer: tails.size() = 4
```

**Complexity**: O(n²) / O(n log n) time, O(n) space.

---

### LC 91 — Decode Ways

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty**: Medium
**Pattern**: 1D DP, string parsing

**Problem**: A message encoded as digits (A=1, B=2, ..., Z=26). Count the number of ways to decode it.

**4-Step Framework**:
1. **State**: `dp[i]` = number of ways to decode `s[0..i-1]`
2. **Transition**:
   - If `s[i-1] != '0'`: `dp[i] += dp[i-1]` (single digit decode)
   - If `s[i-2..i-1]` forms a valid two-digit number (10-26): `dp[i] += dp[i-2]`
3. **Base case**: `dp[0] = 1` (empty string: 1 way), `dp[1] = s[0] != '0' ? 1 : 0`
4. **Answer**: `dp[n]`

```java
public int numDecodings(String s) {
    int n = s.length();
    int[] dp = new int[n + 1];
    dp[0] = 1;
    dp[1] = s.charAt(0) != '0' ? 1 : 0;
    
    for (int i = 2; i <= n; i++) {
        int oneDigit = s.charAt(i-1) - '0';
        int twoDigit = Integer.parseInt(s.substring(i-2, i));
        
        if (oneDigit >= 1) dp[i] += dp[i-1];
        if (twoDigit >= 10 && twoDigit <= 26) dp[i] += dp[i-2];
    }
    
    return dp[n];
}
```

**Space-Optimized**:
```java
public int numDecodings(String s) {
    int n = s.length();
    int prev2 = 1;
    int prev1 = s.charAt(0) != '0' ? 1 : 0;
    
    for (int i = 2; i <= n; i++) {
        int curr = 0;
        int oneDigit = s.charAt(i-1) - '0';
        int twoDigit = Integer.parseInt(s.substring(i-2, i));
        
        if (oneDigit >= 1) curr += prev1;
        if (twoDigit >= 10 && twoDigit <= 26) curr += prev2;
        
        prev2 = prev1;
        prev1 = curr;
    }
    
    return prev1;
}
```

**Complexity**: Time O(n), Space O(1) optimized.

---

## Category B: 2D DP / Grid

---

### LC 62 — Unique Paths

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty**: Medium
**Pattern**: 2D DP, grid traversal

**Problem**: Robot starts at top-left of m×n grid. Can only move right or down. Count paths to bottom-right.

**4-Step Framework**:
1. **State**: `dp[i][j]` = number of unique paths to reach cell `(i, j)`
2. **Transition**: `dp[i][j] = dp[i-1][j] + dp[i][j-1]` (came from above or left)
3. **Base case**: `dp[0][j] = 1` (top row), `dp[i][0] = 1` (left column)
4. **Answer**: `dp[m-1][n-1]`

**Bottom-Up**:
```java
public int uniquePaths(int m, int n) {
    int[][] dp = new int[m][n];
    
    // Base cases: first row and first column
    for (int i = 0; i < m; i++) dp[i][0] = 1;
    for (int j = 0; j < n; j++) dp[0][j] = 1;
    
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            dp[i][j] = dp[i-1][j] + dp[i][j-1];
        }
    }
    
    return dp[m-1][n-1];
}
```

**Space-Optimized** (O(n)):
```java
public int uniquePaths(int m, int n) {
    int[] dp = new int[n];
    Arrays.fill(dp, 1);  // first row: all 1s
    
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            dp[j] += dp[j-1];  // dp[j] = dp[j] (above) + dp[j-1] (left)
        }
    }
    
    return dp[n-1];
}
```

**Complexity**: Time O(m*n), Space O(n) optimized.

---

### LC 64 — Minimum Path Sum

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty**: Medium
**Pattern**: 2D DP, grid traversal with costs

**Problem**: Grid of non-negative integers. Find path from top-left to bottom-right minimizing sum.

**4-Step Framework**:
1. **State**: `dp[i][j]` = minimum path sum to reach cell `(i, j)`
2. **Transition**: `dp[i][j] = grid[i][j] + min(dp[i-1][j], dp[i][j-1])`
3. **Base case**: `dp[0][0] = grid[0][0]`, first row/column are cumulative sums
4. **Answer**: `dp[m-1][n-1]`

```java
public int minPathSum(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    int[][] dp = new int[m][n];
    
    dp[0][0] = grid[0][0];
    
    // First row
    for (int j = 1; j < n; j++) dp[0][j] = dp[0][j-1] + grid[0][j];
    // First column
    for (int i = 1; i < m; i++) dp[i][0] = dp[i-1][0] + grid[i][0];
    
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            dp[i][j] = grid[i][j] + Math.min(dp[i-1][j], dp[i][j-1]);
        }
    }
    
    return dp[m-1][n-1];
}
```

**Space-Optimized** (modify grid in-place or use 1D array):
```java
public int minPathSum(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    int[] dp = new int[n];
    
    dp[0] = grid[0][0];
    for (int j = 1; j < n; j++) dp[j] = dp[j-1] + grid[0][j];
    
    for (int i = 1; i < m; i++) {
        dp[0] += grid[i][0];
        for (int j = 1; j < n; j++) {
            dp[j] = grid[i][j] + Math.min(dp[j], dp[j-1]);
        }
    }
    
    return dp[n-1];
}
```

**Complexity**: Time O(m*n), Space O(n) optimized.

---

### LC 152 — Maximum Product Subarray

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty**: Medium
**Pattern**: 1D DP, track both min and max

**Problem**: Find the contiguous subarray with the largest product.

**Key Insight**: Negative numbers flip min to max. Track both `maxProd` and `minProd` at each position.

**4-Step Framework**:
1. **State**: `maxDp[i]` = max product of subarray ending at `i`; `minDp[i]` = min product
2. **Transition**:
   - `maxDp[i] = max(nums[i], maxDp[i-1]*nums[i], minDp[i-1]*nums[i])`
   - `minDp[i] = min(nums[i], maxDp[i-1]*nums[i], minDp[i-1]*nums[i])`
3. **Base case**: `maxDp[0] = minDp[0] = nums[0]`
4. **Answer**: `max(maxDp[0..n-1])`

```java
public int maxProduct(int[] nums) {
    int n = nums.length;
    int maxProd = nums[0];
    int minProd = nums[0];
    int result = nums[0];
    
    for (int i = 1; i < n; i++) {
        int tempMax = maxProd;
        maxProd = Math.max(nums[i], Math.max(maxProd * nums[i], minProd * nums[i]));
        minProd = Math.min(nums[i], Math.min(tempMax * nums[i], minProd * nums[i]));
        result = Math.max(result, maxProd);
    }
    
    return result;
}
```

**Why track min?** If `nums[i]` is negative and `minProd` is very negative, their product is a large positive number. Example: `[-2, 3, -4]` — at index 2, `minProd = -6`, `nums[2] = -4`, product = 24.

**Complexity**: Time O(n), Space O(1).

---

## Category C: String DP

---

### LC 1143 — Longest Common Subsequence *(MUST DRY RUN)*

**Companies**: Amazon, Google, Meta, Microsoft
**Difficulty**: Medium
**Pattern**: 2D DP, two-string comparison

**Problem**: Find the length of the longest common subsequence of two strings.

**4-Step Framework**:
1. **State**: `dp[i][j]` = LCS length of `s[0..i-1]` and `t[0..j-1]`
2. **Transition**:
   - If `s[i-1] == t[j-1]`: `dp[i][j] = dp[i-1][j-1] + 1`
   - Else: `dp[i][j] = max(dp[i-1][j], dp[i][j-1])`
3. **Base case**: `dp[i][0] = 0`, `dp[0][j] = 0` (empty string has LCS 0)
4. **Answer**: `dp[m][n]`

**Dry Run** with `s = "abcde"`, `t = "ace"`:

```
     ""  a  c  e
""    0  0  0  0
a     0  1  1  1
b     0  1  1  1
c     0  1  2  2
d     0  1  2  2
e     0  1  2  3

Answer: dp[5][3] = 3  (LCS = "ace")
```

**How to fill**: For each cell `(i,j)`:
- `s[i-1]='a', t[j-1]='a'` → match → `dp[1][1] = dp[0][0]+1 = 1`
- `s[i-1]='b', t[j-1]='a'` → no match → `dp[2][1] = max(dp[1][1], dp[2][0]) = max(1,0) = 1`
- `s[i-1]='c', t[j-1]='c'` → match → `dp[3][2] = dp[2][1]+1 = 2`
- `s[i-1]='e', t[j-1]='e'` → match → `dp[5][3] = dp[4][2]+1 = 3`

**Bottom-Up**:
```java
public int longestCommonSubsequence(String s, String t) {
    int m = s.length(), n = t.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s.charAt(i-1) == t.charAt(j-1)) {
                dp[i][j] = dp[i-1][j-1] + 1;
            } else {
                dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
            }
        }
    }
    
    return dp[m][n];
}
```

**Space-Optimized** (O(n)):
```java
public int longestCommonSubsequence(String s, String t) {
    int m = s.length(), n = t.length();
    int[] dp = new int[n + 1];
    
    for (int i = 1; i <= m; i++) {
        int prev = 0;  // stores dp[i-1][j-1]
        for (int j = 1; j <= n; j++) {
            int temp = dp[j];  // save dp[i-1][j] before overwriting
            if (s.charAt(i-1) == t.charAt(j-1)) {
                dp[j] = prev + 1;
            } else {
                dp[j] = Math.max(dp[j], dp[j-1]);
            }
            prev = temp;
        }
    }
    
    return dp[n];
}
```

**Complexity**: Time O(m*n), Space O(n) optimized.

---

### LC 72 — Edit Distance *(MUST DRY RUN)*

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg, Goldman Sachs
**Difficulty**: Hard
**Pattern**: 2D DP, three operations

**Problem**: Given two strings, find the minimum number of operations (insert, delete, replace) to convert one to the other.

**4-Step Framework**:
1. **State**: `dp[i][j]` = min edits to convert `s[0..i-1]` to `t[0..j-1]`
2. **Transition**:
   - If `s[i-1] == t[j-1]`: `dp[i][j] = dp[i-1][j-1]` (no operation)
   - Else: `dp[i][j] = 1 + min(dp[i-1][j-1], dp[i-1][j], dp[i][j-1])`
     - `dp[i-1][j-1]` = replace `s[i-1]` with `t[j-1]`
     - `dp[i-1][j]` = delete `s[i-1]`
     - `dp[i][j-1]` = insert `t[j-1]` into `s`
3. **Base case**: `dp[i][0] = i` (delete i chars), `dp[0][j] = j` (insert j chars)
4. **Answer**: `dp[m][n]`

**Dry Run** with `s = "horse"`, `t = "ros"`:

```
       ""  r  o  s
""      0  1  2  3
h       1  1  2  3
o       2  2  1  2
r       3  2  2  2
s       4  3  3  2
e       5  4  4  3

Answer: dp[5][3] = 3
```

**Filling the table step by step**:
```
dp[1][1]: s[0]='h', t[0]='r' → no match → 1+min(dp[0][0],dp[0][1],dp[1][0])
                                          = 1+min(0,1,1) = 1
dp[2][2]: s[1]='o', t[1]='o' → match    → dp[1][1] = 1
dp[3][1]: s[2]='r', t[0]='r' → match    → dp[2][0] = 2
dp[3][2]: s[2]='r', t[1]='o' → no match → 1+min(dp[2][1],dp[2][2],dp[3][1])
                                          = 1+min(2,1,2) = 2
dp[4][3]: s[3]='s', t[2]='s' → match    → dp[3][2] = 2
dp[5][3]: s[4]='e', t[2]='s' → no match → 1+min(dp[4][2],dp[4][3],dp[5][2])
                                          = 1+min(3,2,4) = 3
```

**Operations to convert "horse" → "ros"**:
1. Replace 'h' with 'r' → "rorse"
2. Delete 'r' → "rose"  (or replace 'r' with nothing)
3. Delete 'e' → "ros"

**Bottom-Up**:
```java
public int minDistance(String s, String t) {
    int m = s.length(), n = t.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s.charAt(i-1) == t.charAt(j-1)) {
                dp[i][j] = dp[i-1][j-1];
            } else {
                dp[i][j] = 1 + Math.min(dp[i-1][j-1],
                               Math.min(dp[i-1][j], dp[i][j-1]));
            }
        }
    }
    
    return dp[m][n];
}
```

**Space-Optimized** (O(n)):
```java
public int minDistance(String s, String t) {
    int m = s.length(), n = t.length();
    int[] dp = new int[n + 1];
    
    for (int j = 0; j <= n; j++) dp[j] = j;
    
    for (int i = 1; i <= m; i++) {
        int prev = dp[0];  // stores dp[i-1][j-1]
        dp[0] = i;         // dp[i][0] = i
        for (int j = 1; j <= n; j++) {
            int temp = dp[j];
            if (s.charAt(i-1) == t.charAt(j-1)) {
                dp[j] = prev;
            } else {
                dp[j] = 1 + Math.min(prev, Math.min(dp[j], dp[j-1]));
            }
            prev = temp;
        }
    }
    
    return dp[n];
}
```

**Complexity**: Time O(m*n), Space O(n) optimized.

---

### LC 5 — Longest Palindromic Substring

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty**: Medium
**Pattern**: 2D DP or expand-around-center

**Problem**: Find the longest palindromic substring.

**DP Approach**:
1. **State**: `dp[i][j]` = true if `s[i..j]` is a palindrome
2. **Transition**: `dp[i][j] = (s[i] == s[j]) && dp[i+1][j-1]`
3. **Base case**: `dp[i][i] = true`, `dp[i][i+1] = (s[i] == s[i+1])`
4. **Answer**: track max length palindrome found

```java
public String longestPalindrome(String s) {
    int n = s.length();
    boolean[][] dp = new boolean[n][n];
    int start = 0, maxLen = 1;
    
    // All single chars are palindromes
    for (int i = 0; i < n; i++) dp[i][i] = true;
    
    // Check length 2
    for (int i = 0; i < n - 1; i++) {
        if (s.charAt(i) == s.charAt(i+1)) {
            dp[i][i+1] = true;
            start = i;
            maxLen = 2;
        }
    }
    
    // Check lengths 3 and above
    for (int len = 3; len <= n; len++) {
        for (int i = 0; i <= n - len; i++) {
            int j = i + len - 1;
            if (s.charAt(i) == s.charAt(j) && dp[i+1][j-1]) {
                dp[i][j] = true;
                if (len > maxLen) {
                    start = i;
                    maxLen = len;
                }
            }
        }
    }
    
    return s.substring(start, start + maxLen);
}
```

**Expand-Around-Center** (O(1) space, same time):
```java
public String longestPalindrome(String s) {
    int n = s.length();
    int start = 0, maxLen = 1;
    
    for (int center = 0; center < n; center++) {
        // Odd length
        int len1 = expand(s, center, center);
        // Even length
        int len2 = expand(s, center, center + 1);
        int len = Math.max(len1, len2);
        
        if (len > maxLen) {
            maxLen = len;
            start = center - (len - 1) / 2;
        }
    }
    
    return s.substring(start, start + maxLen);
}

private int expand(String s, int left, int right) {
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        left--;
        right++;
    }
    return right - left - 1;
}
```

**Complexity**: Time O(n²), Space O(n²) for DP or O(1) for expand-around-center.

---

### LC 139 — Word Break

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty**: Medium
**Pattern**: 1D DP, string segmentation

**Problem**: Given a string and a dictionary, determine if the string can be segmented into dictionary words.

**4-Step Framework**:
1. **State**: `dp[i]` = true if `s[0..i-1]` can be segmented
2. **Transition**: `dp[i] = true` if there exists `j < i` such that `dp[j] == true` and `s[j..i-1]` is in the dictionary
3. **Base case**: `dp[0] = true` (empty string)
4. **Answer**: `dp[n]`

```java
public boolean wordBreak(String s, List<String> wordDict) {
    Set<String> dict = new HashSet<>(wordDict);
    int n = s.length();
    boolean[] dp = new boolean[n + 1];
    dp[0] = true;
    
    for (int i = 1; i <= n; i++) {
        for (int j = 0; j < i; j++) {
            if (dp[j] && dict.contains(s.substring(j, i))) {
                dp[i] = true;
                break;
            }
        }
    }
    
    return dp[n];
}
```

**Optimization**: Only check substrings whose length is within the min/max word length in the dictionary.

```java
public boolean wordBreak(String s, List<String> wordDict) {
    Set<String> dict = new HashSet<>(wordDict);
    int n = s.length();
    int maxLen = 0;
    for (String w : wordDict) maxLen = Math.max(maxLen, w.length());
    
    boolean[] dp = new boolean[n + 1];
    dp[0] = true;
    
    for (int i = 1; i <= n; i++) {
        for (int j = Math.max(0, i - maxLen); j < i; j++) {
            if (dp[j] && dict.contains(s.substring(j, i))) {
                dp[i] = true;
                break;
            }
        }
    }
    
    return dp[n];
}
```

**Complexity**: Time O(n² * L) where L is average word length, Space O(n).

---

## Category D: Knapsack

---

### LC 416 — Partition Equal Subset Sum

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty**: Medium
**Pattern**: 0/1 Knapsack, subset sum

**Problem**: Can you partition an array into two subsets with equal sum?

**Key Insight**: If total sum is odd, impossible. Otherwise, find a subset summing to `total/2`. This is 0/1 knapsack where each number is an item with weight = value = number.

**4-Step Framework**:
1. **State**: `dp[s]` = true if a subset with sum `s` exists
2. **Transition**: `dp[s] = dp[s] || dp[s - nums[i]]` (include or exclude `nums[i]`)
3. **Base case**: `dp[0] = true`
4. **Answer**: `dp[target]` where `target = sum/2`

```java
public boolean canPartition(int[] nums) {
    int sum = 0;
    for (int n : nums) sum += n;
    if (sum % 2 != 0) return false;
    
    int target = sum / 2;
    boolean[] dp = new boolean[target + 1];
    dp[0] = true;
    
    for (int num : nums) {
        // RIGHT TO LEFT: each item used at most once (0/1 knapsack)
        for (int s = target; s >= num; s--) {
            dp[s] = dp[s] || dp[s - num];
        }
    }
    
    return dp[target];
}
```

**Why right-to-left?** If we go left-to-right, we might use the same number twice. Going right-to-left ensures each number is considered only once per row of the implicit 2D table.

**Complexity**: Time O(n * target), Space O(target).

---

### LC 494 — Target Sum

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty**: Medium
**Pattern**: 0/1 Knapsack, count ways

**Problem**: Assign + or - to each number. Count ways to reach target sum.

**Key Insight**: Let P = subset with +, N = subset with -. Then P - N = target and P + N = sum. So P = (sum + target) / 2. Count subsets summing to P.

```java
public int findTargetSumWays(int[] nums, int target) {
    int sum = 0;
    for (int n : nums) sum += n;
    
    // Check feasibility
    if (Math.abs(target) > sum || (sum + target) % 2 != 0) return 0;
    
    int newTarget = (sum + target) / 2;
    int[] dp = new int[newTarget + 1];
    dp[0] = 1;
    
    for (int num : nums) {
        for (int s = newTarget; s >= num; s--) {
            dp[s] += dp[s - num];
        }
    }
    
    return dp[newTarget];
}
```

**Alternative: Direct DP with HashMap** (handles negative targets naturally):
```java
public int findTargetSumWays(int[] nums, int target) {
    Map<Integer, Integer> dp = new HashMap<>();
    dp.put(0, 1);
    
    for (int num : nums) {
        Map<Integer, Integer> next = new HashMap<>();
        for (Map.Entry<Integer, Integer> e : dp.entrySet()) {
            int sum = e.getKey();
            int ways = e.getValue();
            next.merge(sum + num, ways, Integer::sum);
            next.merge(sum - num, ways, Integer::sum);
        }
        dp = next;
    }
    
    return dp.getOrDefault(target, 0);
}
```

**Complexity**: Time O(n * target), Space O(target).

---

### LC 518 — Coin Change II

**Companies**: Amazon, Google, Meta, Microsoft
**Difficulty**: Medium
**Pattern**: Unbounded knapsack, count ways

**Problem**: Count the number of combinations to make up the amount using coins (unlimited supply).

**Key Difference from Coin Change I**: Count combinations (not minimum coins). Order doesn't matter (combinations, not permutations).

**4-Step Framework**:
1. **State**: `dp[a]` = number of combinations to make amount `a`
2. **Transition**: `dp[a] += dp[a - coin]` for each coin
3. **Base case**: `dp[0] = 1`
4. **Answer**: `dp[amount]`

**Critical Loop Order**: Outer loop over coins, inner loop over amounts. This ensures each coin combination is counted once (not permutations).

```java
public int change(int amount, int[] coins) {
    int[] dp = new int[amount + 1];
    dp[0] = 1;
    
    // Outer: coins (ensures combinations, not permutations)
    for (int coin : coins) {
        // Inner: LEFT TO RIGHT (unbounded: each coin can be used multiple times)
        for (int a = coin; a <= amount; a++) {
            dp[a] += dp[a - coin];
        }
    }
    
    return dp[amount];
}
```

**Why outer=coins, inner=amounts?** If you swap the loops (outer=amounts, inner=coins), you count permutations. `[1,2]` and `[2,1]` would be counted separately. With coins outer, once you've processed coin `c`, you never go back to it, so each combination is counted once.

**Complexity**: Time O(amount * coins.length), Space O(amount).

---

### Knapsack Framing of Coin Change (LC 322 revisited)

Coin Change is unbounded knapsack where:
- Items = coins (unlimited supply)
- Weight = coin value
- Capacity = amount
- Objective = minimize number of items (not maximize value)

The key difference from standard knapsack: we minimize instead of maximize, and each item has unlimited supply (left-to-right inner loop).

---

## Category E: State Machine DP

---

### The State Machine Diagram

Stock problems are best understood as state machines. At any point, you're in one of several states, and each day you transition between them.

**Basic Buy/Sell (LC 121)**:
```
         buy              sell
[EMPTY] ------> [HOLDING] ------> [SOLD]
```

**With Cooldown (LC 309)**:
```
         buy              sell
[COOL]  ------> [HOLDING] ------> [SOLD]
  ^                                  |
  |           (next day)             |
  +----------------------------------+
```

**Full State Machine for LC 309**:
```
State transitions:
  HOLDING --sell--> SOLD
  SOLD    --auto--> COOLDOWN
  COOLDOWN --buy--> HOLDING
  COOLDOWN --rest-> COOLDOWN

Variables:
  hold  = max profit while holding stock
  sold  = max profit on day we just sold
  cool  = max profit while in cooldown (or resting)

Transitions:
  hold' = max(hold, cool - price)   // keep holding, or buy from cooldown
  sold' = hold + price               // sell today
  cool' = max(cool, sold)            // rest, or enter cooldown after selling
```

---

### LC 121 — Best Time to Buy and Sell Stock

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty**: Easy
**Pattern**: Single transaction, track minimum

**Problem**: Buy once, sell once. Maximize profit.

**Key Insight**: At each day, the best profit is `price[i] - minPrice` where `minPrice` is the minimum price seen so far.

```java
public int maxProfit(int[] prices) {
    int minPrice = Integer.MAX_VALUE;
    int maxProfit = 0;
    
    for (int price : prices) {
        minPrice = Math.min(minPrice, price);
        maxProfit = Math.max(maxProfit, price - minPrice);
    }
    
    return maxProfit;
}
```

**State Machine Framing**:
```java
public int maxProfit(int[] prices) {
    int hold = Integer.MIN_VALUE;  // profit while holding
    int sold = 0;                  // profit after selling
    
    for (int price : prices) {
        hold = Math.max(hold, -price);  // buy at most once: no previous profit
        sold = Math.max(sold, hold + price);
    }
    
    return sold;
}
```

**Complexity**: Time O(n), Space O(1).

---

### LC 309 — Best Time to Buy and Sell Stock with Cooldown

**Companies**: Amazon, Google, Meta, Microsoft
**Difficulty**: Medium
**Pattern**: State machine DP

**Problem**: Unlimited transactions, but after selling you must wait one day (cooldown).

**State Machine**:
```
hold[i]  = max profit on day i while holding stock
sold[i]  = max profit on day i just after selling
cool[i]  = max profit on day i in cooldown/rest state

Transitions:
hold[i] = max(hold[i-1], cool[i-1] - prices[i])
sold[i] = hold[i-1] + prices[i]
cool[i] = max(cool[i-1], sold[i-1])

Base cases:
hold[0] = -prices[0]  (buy on day 0)
sold[0] = 0           (can't sell on day 0 without buying)
cool[0] = 0           (resting on day 0)

Answer: max(sold[n-1], cool[n-1])
```

```java
public int maxProfit(int[] prices) {
    int hold = -prices[0];
    int sold = 0;
    int cool = 0;
    
    for (int i = 1; i < prices.length; i++) {
        int prevHold = hold;
        int prevSold = sold;
        int prevCool = cool;
        
        hold = Math.max(prevHold, prevCool - prices[i]);
        sold = prevHold + prices[i];
        cool = Math.max(prevCool, prevSold);
    }
    
    return Math.max(sold, cool);
}
```

**Trace** with `prices = [1, 2, 3, 0, 2]`:
```
Day 0: hold=-1, sold=0, cool=0
Day 1: hold=max(-1, 0-2)=-1, sold=-1+2=1, cool=max(0,0)=0
Day 2: hold=max(-1, 0-3)=-1, sold=-1+3=2, cool=max(0,1)=1
Day 3: hold=max(-1, 1-0)=1,  sold=-1+0=-1, cool=max(1,2)=2
Day 4: hold=max(1, 2-2)=1,   sold=1+2=3,  cool=max(2,-1)=2

Answer: max(3, 2) = 3
```

**Complexity**: Time O(n), Space O(1).

---

### LC 714 — Best Time to Buy and Sell Stock with Transaction Fee

**Companies**: Amazon, Google, Meta, Microsoft
**Difficulty**: Medium
**Pattern**: State machine DP

**Problem**: Unlimited transactions, but each transaction has a fee.

**State Machine** (simpler than cooldown — no cooldown state needed):
```
hold[i] = max profit while holding stock on day i
free[i] = max profit while not holding stock on day i

Transitions:
hold[i] = max(hold[i-1], free[i-1] - prices[i])
free[i] = max(free[i-1], hold[i-1] + prices[i] - fee)
```

```java
public int maxProfit(int[] prices, int fee) {
    int hold = -prices[0];
    int free = 0;
    
    for (int i = 1; i < prices.length; i++) {
        int prevHold = hold;
        hold = Math.max(hold, free - prices[i]);
        free = Math.max(free, prevHold + prices[i] - fee);
    }
    
    return free;
}
```

**Complexity**: Time O(n), Space O(1).

---

## Category F: Advanced

---

### LC 10 — Regular Expression Matching

**Companies**: Amazon, Google, Meta, Microsoft
**Difficulty**: Hard
**Pattern**: 2D DP, string matching with wildcards

**Problem**: Implement regex matching with `.` (any single char) and `*` (zero or more of preceding).

**4-Step Framework**:
1. **State**: `dp[i][j]` = true if `s[0..i-1]` matches `p[0..j-1]`
2. **Transition**:
   - If `p[j-1] == s[i-1]` or `p[j-1] == '.'`: `dp[i][j] = dp[i-1][j-1]`
   - If `p[j-1] == '*'`:
     - Zero occurrences: `dp[i][j] = dp[i][j-2]` (skip `x*`)
     - One+ occurrences: if `p[j-2] == s[i-1]` or `p[j-2] == '.'`: `dp[i][j] |= dp[i-1][j]`
3. **Base case**: `dp[0][0] = true`; `dp[0][j]` = true if `p[0..j-1]` can match empty string
4. **Answer**: `dp[m][n]`

```java
public boolean isMatch(String s, String p) {
    int m = s.length(), n = p.length();
    boolean[][] dp = new boolean[m + 1][n + 1];
    dp[0][0] = true;
    
    // Base case: patterns like a*, a*b*, a*b*c* can match empty string
    for (int j = 2; j <= n; j++) {
        if (p.charAt(j-1) == '*') {
            dp[0][j] = dp[0][j-2];
        }
    }
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            char sc = s.charAt(i-1);
            char pc = p.charAt(j-1);
            
            if (pc == sc || pc == '.') {
                dp[i][j] = dp[i-1][j-1];
            } else if (pc == '*') {
                // Zero occurrences of preceding char
                dp[i][j] = dp[i][j-2];
                // One or more occurrences
                char prev = p.charAt(j-2);
                if (prev == sc || prev == '.') {
                    dp[i][j] |= dp[i-1][j];
                }
            }
        }
    }
    
    return dp[m][n];
}
```

**Complexity**: Time O(m*n), Space O(m*n).

---

### LC 44 — Wildcard Matching

**Companies**: Amazon, Google, Meta, Microsoft
**Difficulty**: Hard
**Pattern**: 2D DP, simpler than regex

**Problem**: Implement wildcard matching with `?` (any single char) and `*` (any sequence including empty).

**Key Difference from LC 10**: `*` here matches any sequence directly (not "zero or more of preceding"). Simpler transition.

```java
public boolean isMatch(String s, String p) {
    int m = s.length(), n = p.length();
    boolean[][] dp = new boolean[m + 1][n + 1];
    dp[0][0] = true;
    
    // '*' can match empty string
    for (int j = 1; j <= n; j++) {
        if (p.charAt(j-1) == '*') dp[0][j] = dp[0][j-1];
    }
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            char sc = s.charAt(i-1);
            char pc = p.charAt(j-1);
            
            if (pc == sc || pc == '?') {
                dp[i][j] = dp[i-1][j-1];
            } else if (pc == '*') {
                dp[i][j] = dp[i][j-1]    // '*' matches empty
                         || dp[i-1][j];   // '*' matches one more char
            }
        }
    }
    
    return dp[m][n];
}
```

**Complexity**: Time O(m*n), Space O(m*n).

---

### LC 312 — Burst Balloons (Interval DP)

**Companies**: Amazon, Google, Meta, Microsoft
**Difficulty**: Hard
**Pattern**: Interval DP

**Problem**: Burst all balloons. When you burst balloon `i`, you get `nums[i-1] * nums[i] * nums[i+1]` coins. Maximize total coins.

**Key Insight**: Think backwards. Instead of "which balloon to burst first?", ask "which balloon to burst last in range [i,j]?" If balloon `k` is the last to burst in `[i,j]`, then `nums[i-1] * nums[k] * nums[j+1]` is its contribution, and the left and right subranges are independent.

**4-Step Framework**:
1. **State**: `dp[i][j]` = max coins from bursting all balloons in range `(i, j)` exclusive (boundaries are virtual)
2. **Transition**: `dp[i][j] = max over k in (i,j): dp[i][k] + nums[i]*nums[k]*nums[j] + dp[k][j]`
3. **Base case**: `dp[i][j] = 0` when `j - i < 2` (no balloons between i and j)
4. **Answer**: `dp[0][n+1]`

```java
public int maxCoins(int[] nums) {
    int n = nums.length;
    // Add virtual balloons with value 1 at boundaries
    int[] arr = new int[n + 2];
    arr[0] = arr[n + 1] = 1;
    for (int i = 0; i < n; i++) arr[i + 1] = nums[i];
    
    int m = n + 2;
    int[][] dp = new int[m][m];
    
    // Fill by increasing length of interval
    for (int len = 2; len < m; len++) {
        for (int i = 0; i < m - len; i++) {
            int j = i + len;
            // k is the LAST balloon to burst in (i, j)
            for (int k = i + 1; k < j; k++) {
                dp[i][j] = Math.max(dp[i][j],
                    dp[i][k] + arr[i] * arr[k] * arr[j] + dp[k][j]);
            }
        }
    }
    
    return dp[0][m - 1];
}
```

**Why "last to burst"?** If k is last, then when we burst k, all other balloons in (i,j) are already gone. So the neighbors of k are exactly i and j. This makes the subproblems independent.

**Complexity**: Time O(n³), Space O(n²).

---

### LC 337 — House Robber III (Tree DP)

**Companies**: Amazon, Google, Meta, Microsoft
**Difficulty**: Medium
**Pattern**: Tree DP, post-order traversal

**Problem**: Houses arranged in a binary tree. Adjacent nodes (parent-child) can't both be robbed. Maximize robbery.

**4-Step Framework**:
1. **State**: For each node, compute `[rob, skip]` where `rob` = max if we rob this node, `skip` = max if we don't
2. **Transition**:
   - `rob[node] = node.val + skip[left] + skip[right]`
   - `skip[node] = max(rob[left], skip[left]) + max(rob[right], skip[right])`
3. **Base case**: null node returns `[0, 0]`
4. **Answer**: `max(rob[root], skip[root])`

```java
public int rob(TreeNode root) {
    int[] result = dfs(root);
    return Math.max(result[0], result[1]);
}

// Returns [rob, skip] for this subtree
private int[] dfs(TreeNode node) {
    if (node == null) return new int[]{0, 0};
    
    int[] left = dfs(node.left);
    int[] right = dfs(node.right);
    
    // Rob this node: can't rob children
    int rob = node.val + left[1] + right[1];
    
    // Skip this node: children can be robbed or not (take best)
    int skip = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
    
    return new int[]{rob, skip};
}
```

**Why not use a HashMap?** The array approach is cleaner and avoids HashMap overhead. Each node returns exactly the information its parent needs.

**Complexity**: Time O(n), Space O(h) where h is tree height.

---

### LC 516 — Longest Palindromic Subsequence

**Companies**: Amazon, Google, Meta, Microsoft
**Difficulty**: Medium
**Pattern**: Interval DP or 2D DP

**Problem**: Find the length of the longest palindromic subsequence.

**Key Insight**: LPS of `s` = LCS of `s` and `reverse(s)`. Or use interval DP directly.

**Interval DP Approach**:
1. **State**: `dp[i][j]` = LPS length of `s[i..j]`
2. **Transition**:
   - If `s[i] == s[j]`: `dp[i][j] = dp[i+1][j-1] + 2`
   - Else: `dp[i][j] = max(dp[i+1][j], dp[i][j-1])`
3. **Base case**: `dp[i][i] = 1`
4. **Answer**: `dp[0][n-1]`

```java
public int longestPalindromeSubseq(String s) {
    int n = s.length();
    int[][] dp = new int[n][n];
    
    // Base case: single chars
    for (int i = 0; i < n; i++) dp[i][i] = 1;
    
    // Fill by increasing length
    for (int len = 2; len <= n; len++) {
        for (int i = 0; i <= n - len; i++) {
            int j = i + len - 1;
            if (s.charAt(i) == s.charAt(j)) {
                dp[i][j] = (len == 2) ? 2 : dp[i+1][j-1] + 2;
            } else {
                dp[i][j] = Math.max(dp[i+1][j], dp[i][j-1]);
            }
        }
    }
    
    return dp[0][n-1];
}
```

**LCS Approach** (cleaner):
```java
public int longestPalindromeSubseq(String s) {
    String rev = new StringBuilder(s).reverse().toString();
    return lcs(s, rev);
}

private int lcs(String s, String t) {
    int m = s.length(), n = t.length();
    int[] dp = new int[n + 1];
    
    for (int i = 1; i <= m; i++) {
        int prev = 0;
        for (int j = 1; j <= n; j++) {
            int temp = dp[j];
            if (s.charAt(i-1) == t.charAt(j-1)) dp[j] = prev + 1;
            else dp[j] = Math.max(dp[j], dp[j-1]);
            prev = temp;
        }
    }
    
    return dp[n];
}
```

**Complexity**: Time O(n²), Space O(n²) or O(n) with LCS approach.

---

### LC 647 — Palindromic Substrings

**Companies**: Amazon, Google, Meta, Microsoft
**Difficulty**: Medium
**Pattern**: Expand-around-center or 2D DP

**Problem**: Count all palindromic substrings.

```java
public int countSubstrings(String s) {
    int n = s.length();
    int count = 0;
    
    for (int center = 0; center < n; center++) {
        // Odd length palindromes
        count += expand(s, center, center);
        // Even length palindromes
        count += expand(s, center, center + 1);
    }
    
    return count;
}

private int expand(String s, int left, int right) {
    int count = 0;
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        count++;
        left--;
        right++;
    }
    return count;
}
```

**Complexity**: Time O(n²), Space O(1).

---

## How to Approach a New DP Problem

This is the most important section. Memorizing solutions doesn't help in interviews. This framework does.

---

### The 4-Phase Interview Framework

**Phase 1: Brute Force Recursion**

Start here. Always. Don't try to be clever.

Ask: "Can I solve this recursively by breaking it into smaller subproblems?"

Write the recursive solution without worrying about efficiency. This forces you to:
- Identify the subproblem structure
- Find the natural state (the parameters of your recursive function)
- Write the transition (the recursive call)

**Phase 2: Identify Overlapping Subproblems**

Draw the recursion tree. Are the same calls made multiple times? If yes, add memoization.

This converts your O(2^n) brute force into O(n) or O(n²) with one line of change.

**Phase 3: Convert to Bottom-Up**

Flip the recursion: instead of top-down with memoization, fill a table from base cases upward.

The loop order must respect dependencies: fill `dp[i]` only after all `dp[j]` it depends on are filled.

**Phase 4: Optimize Space**

Look at the transition. Which previous cells does `dp[i]` depend on? If only `dp[i-1]`, use two variables. If only the previous row, use a 1D array.

---

### Framework Applied: LC 322 (Coin Change) from Scratch

**Problem**: `coins = [1, 2, 5]`, `amount = 11`. Find minimum coins.

**Phase 1: Brute Force Recursion**

"To make amount `a`, I can use any coin `c` and then solve for `a - c`."

```java
// Brute force: O(coins^amount) — exponential
int solve(int[] coins, int amount) {
    if (amount == 0) return 0;
    if (amount < 0) return -1;
    
    int min = Integer.MAX_VALUE;
    for (int coin : coins) {
        int sub = solve(coins, amount - coin);
        if (sub >= 0) min = Math.min(min, sub + 1);
    }
    
    return min == Integer.MAX_VALUE ? -1 : min;
}
```

**Phase 2: Add Memoization**

The state is `amount`. Same `amount` gets computed many times.

```java
// Memoized: O(amount * coins.length)
int[] memo = new int[amount + 1];
Arrays.fill(memo, -2);  // -2 = unvisited

int solve(int[] coins, int amount) {
    if (amount == 0) return 0;
    if (amount < 0) return -1;
    if (memo[amount] != -2) return memo[amount];
    
    int min = Integer.MAX_VALUE;
    for (int coin : coins) {
        int sub = solve(coins, amount - coin);
        if (sub >= 0) min = Math.min(min, sub + 1);
    }
    
    memo[amount] = (min == Integer.MAX_VALUE) ? -1 : min;
    return memo[amount];
}
```

**Phase 3: Bottom-Up**

Fill `dp[0..amount]` from left to right. `dp[a]` depends on `dp[a - coin]` for all coins, which are smaller indices — already filled.

```java
int[] dp = new int[amount + 1];
Arrays.fill(dp, amount + 1);  // "infinity"
dp[0] = 0;

for (int a = 1; a <= amount; a++) {
    for (int coin : coins) {
        if (a >= coin) {
            dp[a] = Math.min(dp[a], dp[a - coin] + 1);
        }
    }
}

return dp[amount] > amount ? -1 : dp[amount];
```

**Phase 4: Space Optimization**

`dp[a]` depends on `dp[a - coin]` for various coins. Since coins can be large, we can't reduce to O(1). The 1D array is already optimal.

---

### Framework Applied: LC 1143 (LCS) from Scratch

**Problem**: `s = "abcde"`, `t = "ace"`. Find LCS length.

**Phase 1: Brute Force Recursion**

"Compare last characters. If they match, include both and recurse on the rest. If not, try excluding one from each."

```java
// Brute force: O(2^(m+n))
int lcs(String s, String t, int i, int j) {
    if (i == 0 || j == 0) return 0;
    if (s.charAt(i-1) == t.charAt(j-1)) {
        return 1 + lcs(s, t, i-1, j-1);
    }
    return Math.max(lcs(s, t, i-1, j), lcs(s, t, i, j-1));
}
```

**Phase 2: Add Memoization**

State is `(i, j)`. Use a 2D memo array.

```java
int[][] memo = new int[m+1][n+1];
// Initialize to -1

int lcs(String s, String t, int i, int j) {
    if (i == 0 || j == 0) return 0;
    if (memo[i][j] != -1) return memo[i][j];
    
    if (s.charAt(i-1) == t.charAt(j-1)) {
        memo[i][j] = 1 + lcs(s, t, i-1, j-1);
    } else {
        memo[i][j] = Math.max(lcs(s, t, i-1, j), lcs(s, t, i, j-1));
    }
    return memo[i][j];
}
```

**Phase 3: Bottom-Up**

Fill the 2D table. `dp[i][j]` depends on `dp[i-1][j-1]`, `dp[i-1][j]`, `dp[i][j-1]` — all smaller indices.

```java
int[][] dp = new int[m+1][n+1];
for (int i = 1; i <= m; i++) {
    for (int j = 1; j <= n; j++) {
        if (s.charAt(i-1) == t.charAt(j-1)) dp[i][j] = dp[i-1][j-1] + 1;
        else dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
    }
}
return dp[m][n];
```

**Phase 4: Space Optimization**

`dp[i][j]` depends only on row `i-1`. Use a 1D array with a `prev` variable to track `dp[i-1][j-1]`.

```java
int[] dp = new int[n + 1];
for (int i = 1; i <= m; i++) {
    int prev = 0;
    for (int j = 1; j <= n; j++) {
        int temp = dp[j];
        if (s.charAt(i-1) == t.charAt(j-1)) dp[j] = prev + 1;
        else dp[j] = Math.max(dp[j], dp[j-1]);
        prev = temp;
    }
}
return dp[n];
```

---

### Recognizing DP Problems in Interviews

**Strong signals**:
- "How many ways to..." (counting)
- "What is the minimum/maximum..." (optimization)
- "Is it possible to..." (feasibility)
- The problem involves sequences, strings, or grids
- Choices at each step affect future choices

**Weak signals (might be greedy instead)**:
- "Find the optimal..." — check if greedy works first
- Sorting helps — often greedy

**DP vs Greedy test**: Can a locally optimal choice lead to a globally suboptimal result? If yes, use DP. If no, greedy might work.

---

## Common Mistakes

**1. Wrong State Definition (Too Few Dimensions)**

The most common mistake. If your state doesn't capture enough information to make the transition, you'll get wrong answers.

Example: In stock problems with cooldown, `dp[i]` = max profit on day `i` isn't enough. You need to know whether you're holding stock or in cooldown. Add a dimension or use separate arrays for each state.

Rule of thumb: your state must capture everything needed to make the next decision.

**2. Wrong Base Case**

Off-by-one errors in base cases cause subtle bugs. Always verify:
- What does `dp[0]` mean? Is it the empty case?
- For 2D DP, what do `dp[i][0]` and `dp[0][j]` represent?

Example: In edit distance, `dp[i][0] = i` (delete all i characters from s). Forgetting this gives wrong answers for strings with empty targets.

**3. Off-by-One in Table Dimensions**

If your string has length `n`, your DP array needs size `n+1` to accommodate the empty string case at index 0. This is the most common source of `ArrayIndexOutOfBoundsException`.

```java
// WRONG: dp[n] can't hold dp[n] (last index is n-1)
int[] dp = new int[n];

// CORRECT: dp[0..n] where dp[0] is the base case
int[] dp = new int[n + 1];
```

**4. Wrong Loop Direction in Knapsack**

- 0/1 Knapsack (each item once): inner loop RIGHT TO LEFT
- Unbounded Knapsack (items reusable): inner loop LEFT TO RIGHT

Swapping these gives wrong answers that are hard to debug.

**5. Trying to Be Clever Before Being Correct**

Don't jump to the optimized solution. Always:
1. Write brute force recursion first
2. Add memoization
3. Convert to bottom-up
4. Then optimize space

Skipping steps leads to bugs that are hard to trace. In an interview, a correct O(n²) solution beats a buggy O(n) solution every time.

**6. Not Considering All Transitions**

In state machine DP, missing a transition is fatal. Draw the state diagram explicitly. List every possible state and every possible transition before writing code.

**7. Initializing DP Array Incorrectly**

For minimization problems, initialize to `Integer.MAX_VALUE` or `amount + 1` (a safe "infinity"). For maximization, initialize to `Integer.MIN_VALUE` or 0 depending on context. Using 0 for a minimization problem gives wrong answers.

---

## DP vs Other Techniques

### DP vs Greedy (Topic 19)

| Criterion | Dynamic Programming | Greedy |
|---|---|---|
| Subproblem overlap | Yes | No (or irrelevant) |
| Decision scope | Global (considers all futures) | Local (best current choice) |
| Correctness proof | Optimal substructure | Exchange argument |
| Time complexity | Usually O(n²) or worse | Usually O(n log n) |
| Space | O(n) to O(n²) | O(1) to O(n) |
| When to use | Local optimum ≠ global optimum | Local optimum = global optimum |

**Test**: Try greedy first. If you can construct a counterexample where greedy fails, use DP.

Example: Coin change with `coins = [1, 3, 4]`, `amount = 6`. Greedy picks 4+1+1 = 3 coins. DP finds 3+3 = 2 coins.

Example: Activity selection (interval scheduling). Greedy (earliest finish time) is optimal. No DP needed.

### DP vs Backtracking (Topic 13)

| Criterion | Dynamic Programming | Backtracking |
|---|---|---|
| Subproblem overlap | Yes — cache results | No — each path is unique |
| State space | Polynomial | Exponential |
| When to use | Counting/optimization | Enumeration/constraint satisfaction |
| Memory | O(state space) | O(depth) |

**Key distinction**: If two different paths through the recursion tree reach the same subproblem, use DP (cache it). If every path is unique (like generating all permutations), use backtracking.

Example: Count ways to make change → DP (same amount reached many ways).
Example: Generate all valid parentheses → Backtracking (each sequence is unique).

### DP vs Divide and Conquer

| Criterion | Dynamic Programming | Divide and Conquer |
|---|---|---|
| Subproblem overlap | Yes | No |
| Subproblem independence | No (share subproblems) | Yes (independent) |
| Examples | Fibonacci, LCS, knapsack | Merge sort, quicksort, binary search |

**The difference**: Divide and Conquer splits into non-overlapping subproblems. DP handles overlapping ones. Merge sort's subproblems never overlap. Fibonacci's do.

---

## Cheat Sheet and 6-Week Roadmap

### Quick Reference: State Definitions

| Problem | State | Transition |
|---|---|---|
| Climbing Stairs | `dp[i]` = ways to reach step i | `dp[i-1] + dp[i-2]` |
| House Robber | `dp[i]` = max money from 0..i | `max(dp[i-1], dp[i-2]+nums[i])` |
| Coin Change | `dp[a]` = min coins for amount a | `min(dp[a-c]+1)` for each coin c |
| LIS | `dp[i]` = LIS length ending at i | `max(dp[j]+1)` for j<i, nums[j]<nums[i] |
| Unique Paths | `dp[i][j]` = paths to (i,j) | `dp[i-1][j] + dp[i][j-1]` |
| LCS | `dp[i][j]` = LCS of s[0..i-1], t[0..j-1] | match: `dp[i-1][j-1]+1`, else `max(dp[i-1][j], dp[i][j-1])` |
| Edit Distance | `dp[i][j]` = edits to convert s[0..i-1] to t[0..j-1] | match: `dp[i-1][j-1]`, else `1+min(3 neighbors)` |
| 0/1 Knapsack | `dp[w]` = max value with capacity w | `max(dp[w], dp[w-wt]+val)` right-to-left |
| Unbounded Knapsack | `dp[w]` = max value with capacity w | `max(dp[w], dp[w-wt]+val)` left-to-right |
| Burst Balloons | `dp[i][j]` = max coins in range (i,j) | `max(dp[i][k]+arr[i]*arr[k]*arr[j]+dp[k][j])` |
| Stock + Cooldown | `hold, sold, cool` | state machine transitions |
| Tree Robber | `[rob, skip]` per node | post-order combination |

---

### Space Optimization Rules

| Dependency | Optimization |
|---|---|
| `dp[i]` depends on `dp[i-1]` only | Two variables |
| `dp[i]` depends on `dp[i-1]` and `dp[i-2]` | Three variables |
| `dp[i][j]` depends on row `i-1` only | 1D array (rolling) |
| `dp[i][j]` depends on `dp[i-1][j-1]` too | 1D array + `prev` variable |
| 0/1 Knapsack | 1D array, right-to-left |
| Unbounded Knapsack | 1D array, left-to-right |

---

### 6-Week DP Roadmap

DP needs the most time of any topic. Don't rush it.

**Week 1: 1D DP Foundations**
- LC 70 (Climbing Stairs)
- LC 198 (House Robber)
- LC 213 (House Robber II)
- LC 322 (Coin Change)
- LC 91 (Decode Ways)
- Goal: Master the 4-step framework. Dry run every problem.

**Week 2: 2D DP and Grid**
- LC 62 (Unique Paths)
- LC 64 (Minimum Path Sum)
- LC 152 (Maximum Product Subarray)
- LC 300 (LIS — both O(n²) and O(n log n))
- Goal: Get comfortable with 2D tables and space optimization.

**Week 3: String DP**
- LC 1143 (LCS — build the full table by hand)
- LC 72 (Edit Distance — build the full table by hand)
- LC 5 (Longest Palindromic Substring)
- LC 139 (Word Break)
- LC 516 (Longest Palindromic Subsequence)
- Goal: Internalize the two-string DP pattern.

**Week 4: Knapsack Variants**
- LC 416 (Partition Equal Subset Sum)
- LC 494 (Target Sum)
- LC 518 (Coin Change II)
- LC 647 (Palindromic Substrings)
- Goal: Understand 0/1 vs unbounded, and the loop direction rule.

**Week 5: State Machine and Advanced**
- LC 121 (Best Time to Buy/Sell Stock)
- LC 309 (With Cooldown)
- LC 714 (With Fee)
- LC 337 (House Robber III)
- LC 312 (Burst Balloons)
- Goal: Draw state diagrams before coding. Master interval DP.

**Week 6: Hard Problems and Review**
- LC 10 (Regular Expression Matching)
- LC 44 (Wildcard Matching)
- Review all 25 problems without looking at solutions
- Practice the 4-phase framework on 2-3 new problems
- Goal: Fluency. You should be able to identify the DP pattern within 2 minutes.

---

### The 25-Problem List

| # | Problem | Category | Difficulty | Must Dry Run |
|---|---|---|---|---|
| 1 | LC 70 Climbing Stairs | 1D DP | Easy | No |
| 2 | LC 198 House Robber | 1D DP | Medium | YES |
| 3 | LC 213 House Robber II | 1D DP | Medium | No |
| 4 | LC 322 Coin Change | 1D DP / Knapsack | Medium | YES |
| 5 | LC 300 LIS | 1D DP | Medium | No |
| 6 | LC 91 Decode Ways | 1D DP | Medium | No |
| 7 | LC 62 Unique Paths | 2D DP | Medium | No |
| 8 | LC 64 Minimum Path Sum | 2D DP | Medium | No |
| 9 | LC 152 Max Product Subarray | 1D DP | Medium | No |
| 10 | LC 1143 LCS | String DP | Medium | YES |
| 11 | LC 72 Edit Distance | String DP | Hard | YES |
| 12 | LC 5 Longest Palindromic Substring | String DP | Medium | No |
| 13 | LC 139 Word Break | String DP | Medium | No |
| 14 | LC 516 Longest Palindromic Subsequence | Interval DP | Medium | No |
| 15 | LC 647 Palindromic Substrings | String DP | Medium | No |
| 16 | LC 416 Partition Equal Subset Sum | Knapsack | Medium | No |
| 17 | LC 494 Target Sum | Knapsack | Medium | No |
| 18 | LC 518 Coin Change II | Knapsack | Medium | No |
| 19 | LC 121 Best Time Buy/Sell | State Machine | Easy | No |
| 20 | LC 309 Buy/Sell with Cooldown | State Machine | Medium | No |
| 21 | LC 714 Buy/Sell with Fee | State Machine | Medium | No |
| 22 | LC 337 House Robber III | Tree DP | Medium | No |
| 23 | LC 312 Burst Balloons | Interval DP | Hard | No |
| 24 | LC 10 Regex Matching | String DP | Hard | No |
| 25 | LC 44 Wildcard Matching | String DP | Hard | No |

---

### Pattern Recognition Quick Guide

**See "how many ways"** → DP counting (coin change II, decode ways, target sum)

**See "minimum/maximum"** → DP optimization (coin change, edit distance, LIS)

**See "can you partition/split"** → Knapsack (partition equal subset, word break)

**See two strings** → 2D string DP (LCS, edit distance, regex)

**See a grid** → 2D grid DP (unique paths, min path sum)

**See buy/sell/cooldown** → State machine DP

**See a tree with constraints** → Tree DP (house robber III)

**See "burst/remove elements, gain depends on neighbors"** → Interval DP (burst balloons)

**See palindrome** → Expand-around-center or interval DP

---

### Bitmask DP (Brief Overview)

For completeness: bitmask DP is used when the state needs to track which elements from a small set have been used. The state is a bitmask where bit `i` = 1 means element `i` is included.

**Traveling Salesman Problem (TSP)**:
- State: `dp[mask][i]` = min cost to visit all cities in `mask`, ending at city `i`
- Transition: `dp[mask | (1<<j)][j] = min(dp[mask][i] + dist[i][j])`
- Complexity: O(2^n * n²)

**Assignment Problem**:
- State: `dp[mask]` = min cost to assign first `popcount(mask)` workers to the cities in `mask`
- Complexity: O(2^n * n)

Bitmask DP is rarely asked at FAANG for non-specialized roles. Know it exists; don't spend more than a day on it.

---

### Final Notes

DP is the topic where preparation pays off most. Unlike graph algorithms (which you either know or don't), DP is a skill that improves with practice. The 4-step framework is your anchor. Every time you see a new problem, go through the steps in order.

The biggest mistake candidates make: jumping to the optimized solution without understanding the structure. Start with brute force. Add memoization. Convert to bottom-up. Optimize space. This sequence never fails.

The second biggest mistake: not dry running. Build the table by hand for at least the first 10 problems. Once you've filled a dozen DP tables manually, the patterns become instinctive.

The third biggest mistake: treating DP as a collection of tricks to memorize. It's not. It's one idea — optimal substructure with overlapping subproblems — applied in many contexts. Once you see that, the "tricks" become obvious.

---

*Document 20 of 20 — DSA Interview Preparation Series*
*Java only. FAANG focused. No fluff.*
