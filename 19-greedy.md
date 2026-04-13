# Topic 19: Greedy Algorithms

> **Series position:** Document 19 of 20
> **Difficulty range:** Easy to Hard
> **Interview frequency:** Greedy problems appear in ~25% of FAANG interviews. Amazon asks scheduling and array greedy constantly. Google favors proof-heavy problems like Jump Game II and Patching Array. Meta asks string partition and stock problems. Bloomberg asks gas station and candy variants. Apple asks jump game and subarray problems.
> **Top companies:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple
> **Prerequisites:** Topic 12 (Heaps & Priority Queues), Topic 18 (Intervals), Topic 5 (Prefix Sum)

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5 Intuition](#eli5-intuition)
3. [Core Templates in Java](#core-templates-in-java)
4. [Real-World Applications](#real-world-applications)
5. [Problem Categories and Solutions](#problem-categories-and-solutions)
   - [Category A: Array Greedy](#category-a-array-greedy)
   - [Category B: Scheduling and Sorting Greedy](#category-b-scheduling-and-sorting-greedy)
   - [Category C: String and Partition Greedy](#category-c-string-and-partition-greedy)
   - [Category D: Advanced Greedy](#category-d-advanced-greedy)
6. [Common Mistakes](#common-mistakes)
7. [Algorithm Comparison](#algorithm-comparison)
8. [Quick Reference Cheat Sheet](#quick-reference-cheat-sheet)
9. [Practice Roadmap](#practice-roadmap)

---

## Core Concept

### What Greedy Means

A greedy algorithm makes the **locally optimal choice at each step**, never reconsidering past decisions, with the hope that local optimality leads to global optimality.

The key word is "hope." Greedy doesn't always work. It only works when the problem has two properties:

**1. Greedy Choice Property:** A globally optimal solution can be reached by making locally optimal choices. You don't need to consider all possibilities — the greedy pick at each step is always part of some optimal solution.

**2. Optimal Substructure:** An optimal solution to the problem contains optimal solutions to its subproblems. (This is also required for DP, but greedy additionally requires the greedy choice property.)

Without both properties, greedy fails. The 0/1 knapsack problem has optimal substructure but NOT the greedy choice property — that's why it needs DP.

---

### Greedy vs DP: The Critical Distinction

This is the most important thing to understand before any greedy interview question.

| Property | Greedy | Dynamic Programming |
|---|---|---|
| Decisions | One choice per step, never revisit | Considers all choices, stores results |
| Overlapping subproblems | Not required | Required |
| Greedy choice property | Required | Not required |
| Time complexity | Often O(n log n) | Often O(n²) or O(n·k) |
| Space complexity | Often O(1) or O(n) | Often O(n) or O(n·k) |
| Correctness | Needs proof | Guaranteed by recurrence |

**The decision rule:**
- Does the problem have overlapping subproblems? → DP
- Does the problem have only optimal substructure, and can you prove a greedy choice is always safe? → Greedy
- Neither? → Backtracking or brute force

**Classic trap:** Coin change with arbitrary denominations looks greedy but needs DP. Coin change with US denominations (25, 10, 5, 1) works with greedy because of their specific mathematical relationship.

---

### How to Prove Greedy Correctness

In interviews, you must argue why greedy works. There are three standard proof techniques:

**1. Exchange Argument (most common)**

Assume there's an optimal solution that differs from the greedy solution. Show that you can swap the non-greedy choice for the greedy choice without making the solution worse. This proves the greedy choice is always at least as good.

Example for Activity Selection: Suppose optimal picks activity B before greedy's activity A, and A finishes earlier than B. Swap B for A — the solution is still valid (A ends earlier, so it conflicts with fewer future activities) and has the same count. Therefore greedy is optimal.

**2. Greedy Stays Ahead**

Show that after each step, the greedy solution is at least as good as any other solution at that point. Prove by induction.

Example for Jump Game II: After k jumps, greedy has reached at least as far as any other strategy using k jumps.

**3. Matroid Theory (advanced)**

For problems with matroid structure (like minimum spanning tree), greedy is provably optimal. Rarely needed in interviews.

---

### Common Greedy Strategies

**Sort by some criterion, then iterate:** Most greedy problems start with sorting. The sorting criterion encodes the greedy choice. Activity selection sorts by end time. Queue reconstruction sorts by height descending, then k ascending. Getting the sort wrong is the most common greedy bug.

**Always pick the locally best option:** At each step, pick the option that maximizes (or minimizes) the immediate objective. Jump Game picks the farthest reachable position. Kadane's decides whether to extend the current subarray or start fresh.

**Two-pass greedy:** Some problems need two passes in opposite directions because the constraint flows both ways. Candy is the canonical example — left-to-right enforces the left neighbor constraint, right-to-left enforces the right neighbor constraint.

**Heap-based greedy:** When "locally best" changes dynamically, use a priority queue. Task Scheduler uses a max-heap to always schedule the most frequent remaining task.

---

## ELI5 Intuition

You're making change for 41 cents. You have quarters (25¢), dimes (10¢), nickels (5¢), and pennies (1¢).

Greedy says: always pick the largest coin that fits.
- 41¢: pick quarter → 16¢ remaining
- 16¢: pick dime → 6¢ remaining
- 6¢: pick nickel → 1¢ remaining
- 1¢: pick penny → done

4 coins. That's optimal.

This works for US coins because of their specific mathematical relationship. But imagine a coin system with denominations 1, 3, 4. You need to make 6¢.

Greedy: pick 4 → 2¢ remaining → pick 1 → 1¢ remaining → pick 1. That's 3 coins.
Optimal: pick 3 → 3¢ remaining → pick 3. That's 2 coins.

Greedy failed. The greedy choice property doesn't hold for this coin system.

**The lesson:** Greedy is fast and elegant, but it needs a proof. In interviews, after you identify a greedy approach, spend 30 seconds explaining why the greedy choice is always safe. That's what separates a good answer from a great one.

---

## Core Templates in Java

### Template 1: Sort + Iterate Greedy

The most common pattern. Sort by a criterion that makes the greedy choice obvious, then make one pass.

```java
// Generic sort + iterate greedy
// Example: Activity Selection (pick max non-overlapping activities)
public int activitySelection(int[][] activities) {
    // Sort by end time — greedy criterion
    Arrays.sort(activities, (a, b) -> a[1] - b[1]);
    
    int count = 1;
    int lastEnd = activities[0][1];
    
    for (int i = 1; i < activities.length; i++) {
        // Greedy choice: take activity if it doesn't conflict
        if (activities[i][0] >= lastEnd) {
            count++;
            lastEnd = activities[i][1];
        }
    }
    
    return count;
}
```

**When to use:** Problems where you can sort inputs and make a single left-to-right decision at each element. The sort criterion is the key insight.

**Complexity:** O(n log n) for sort, O(n) for iteration. Total: O(n log n).

---

### Template 2: Two-Pass Greedy

Some constraints flow in both directions. One pass can't satisfy both simultaneously. Make two passes: one left-to-right, one right-to-left, then combine.

```java
// Generic two-pass greedy
// Example: Candy distribution (each child gets more than neighbors with lower rating)
public int twoPassGreedy(int[] ratings) {
    int n = ratings.length;
    int[] candies = new int[n];
    Arrays.fill(candies, 1);  // Everyone gets at least 1
    
    // Pass 1: left to right — enforce left neighbor constraint
    for (int i = 1; i < n; i++) {
        if (ratings[i] > ratings[i - 1]) {
            candies[i] = candies[i - 1] + 1;
        }
    }
    
    // Pass 2: right to left — enforce right neighbor constraint
    for (int i = n - 2; i >= 0; i--) {
        if (ratings[i] > ratings[i + 1]) {
            // Take max to satisfy BOTH constraints
            candies[i] = Math.max(candies[i], candies[i + 1] + 1);
        }
    }
    
    int total = 0;
    for (int c : candies) total += c;
    return total;
}
```

**When to use:** Problems where each element has constraints from both sides (left neighbor AND right neighbor). The `Math.max` in the second pass is critical — you must satisfy both constraints simultaneously.

**Complexity:** O(n) time, O(n) space.

---

### Template 3: Heap-Based Greedy

When the "locally best" choice changes as you process elements, use a priority queue to always access the current best option in O(log n).

```java
// Generic heap-based greedy
// Example: Task Scheduler (minimize total time with cooldown)
public int heapGreedy(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;
    
    // Max-heap: always schedule most frequent task
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    for (int f : freq) {
        if (f > 0) maxHeap.offer(f);
    }
    
    int time = 0;
    
    while (!maxHeap.isEmpty()) {
        List<Integer> temp = new ArrayList<>();
        
        // Process one "cycle" of n+1 slots
        for (int i = 0; i <= n; i++) {
            if (!maxHeap.isEmpty()) {
                int f = maxHeap.poll();
                if (f > 1) temp.add(f - 1);
                time++;
            } else if (!temp.isEmpty()) {
                // Still have tasks but heap is empty — idle
                time++;
            }
            // If both empty, cycle is done
            if (maxHeap.isEmpty() && temp.isEmpty()) break;
        }
        
        // Re-add remaining tasks
        maxHeap.addAll(temp);
    }
    
    return time;
}
```

**When to use:** Problems where you repeatedly need the "best" remaining option and the set of options changes over time. Common in scheduling, Huffman coding, and Dijkstra's.

**Complexity:** O(n log k) where k is the number of distinct options.

---

## Real-World Applications

**Huffman Coding (Data Compression)**

Huffman coding assigns shorter bit sequences to more frequent characters. It's a heap-based greedy: repeatedly merge the two least-frequent nodes into a new node. The greedy choice (always merge the two smallest) is provably optimal by exchange argument. Used in ZIP, JPEG, MP3.

**Activity Selection / Job Scheduling**

Given n jobs with start/end times, pick the maximum number of non-overlapping jobs. Greedy: sort by end time, always pick the earliest-finishing job that doesn't conflict. This is the foundation of calendar scheduling systems.

**Dijkstra's Algorithm (Shortest Path)**

Dijkstra's is a greedy algorithm: at each step, pick the unvisited node with the smallest known distance. The greedy choice works because edge weights are non-negative — once you've finalized a node's distance, no future path can improve it. (With negative edges, this breaks — use Bellman-Ford instead.)

**Load Balancing**

Assign tasks to servers to minimize the maximum load. Greedy: always assign the next task to the least-loaded server. Implemented with a min-heap. Used in distributed systems and cloud computing.

**Fractional Knapsack**

Given items with weights and values, fill a knapsack of capacity W to maximize value. You can take fractions of items. Greedy: sort by value/weight ratio, take items greedily. This works because you can always take a fraction — unlike 0/1 knapsack where you must take whole items (which needs DP).

**Network Routing (Minimum Spanning Tree)**

Kruskal's and Prim's algorithms for MST are both greedy. Kruskal's sorts edges by weight and adds them if they don't create a cycle. Prim's always picks the cheapest edge connecting the current tree to a new vertex. Both are provably optimal via matroid theory.

---

## Problem Categories and Solutions

---

## Category A: Array Greedy

---

### LC 55 — Jump Game

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty:** Medium
**Pattern:** Greedy reach tracking

**Problem:** Given array `nums` where `nums[i]` is the max jump length from index i, return true if you can reach the last index.

**Greedy Insight:** Track the farthest index reachable at any point. If you're at index i and i > maxReach, you're stuck. Otherwise, update maxReach = max(maxReach, i + nums[i]).

**Why greedy works:** If you can reach index i, you can reach any index j ≤ i (by taking smaller jumps). So "farthest reachable" is a complete characterization of what's possible. The greedy choice (always update to farthest) never misses any reachable index.

**Dry Run Trace:**

```
nums = [2, 3, 1, 1, 4]
       i=0  i=1  i=2  i=3  i=4

i=0: maxReach = max(0, 0+2) = 2
     Can we reach i=0? 0 <= 2 ✓

i=1: maxReach = max(2, 1+3) = 4
     Can we reach i=1? 1 <= 4 ✓

i=2: maxReach = max(4, 2+1) = 4
     Can we reach i=2? 2 <= 4 ✓

i=3: maxReach = max(4, 3+1) = 4
     Can we reach i=3? 3 <= 4 ✓

i=4: maxReach = max(4, 4+4) = 8
     Can we reach i=4? 4 <= 8 ✓

Last index = 4, maxReach = 8 >= 4 → return true
```

```
nums = [3, 2, 1, 0, 4]
       i=0  i=1  i=2  i=3  i=4

i=0: maxReach = max(0, 0+3) = 3
i=1: maxReach = max(3, 1+2) = 3
i=2: maxReach = max(3, 2+1) = 3
i=3: maxReach = max(3, 3+0) = 3
i=4: Can we reach i=4? 4 <= 3? NO → return false
```

**Implementation:**

```java
// LC 55 - Jump Game
// Time: O(n), Space: O(1)
public boolean canJump(int[] nums) {
    int maxReach = 0;
    
    for (int i = 0; i < nums.length; i++) {
        // If current index is beyond what we can reach, we're stuck
        if (i > maxReach) return false;
        
        // Update farthest reachable index
        maxReach = Math.max(maxReach, i + nums[i]);
    }
    
    return true;
}
```

**Alternative (backward greedy):**

```java
// Backward approach: track the "goal" — start from end, move goal left
public boolean canJumpBackward(int[] nums) {
    int goal = nums.length - 1;
    
    for (int i = nums.length - 2; i >= 0; i--) {
        // If we can reach the current goal from i, move goal to i
        if (i + nums[i] >= goal) {
            goal = i;
        }
    }
    
    // If goal moved all the way to 0, we can reach the end
    return goal == 0;
}
```

**Complexity:** O(n) time, O(1) space.

**Interview tip:** The backward approach is slightly easier to explain. "I track the leftmost position from which I can reach the end. If that position reaches index 0, I can make it."

---

### LC 45 — Jump Game II

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** BFS-level greedy (implicit BFS)

**Problem:** Same setup as LC 55, but return the minimum number of jumps to reach the last index. Guaranteed to be reachable.

**Greedy Insight:** Think of this as BFS levels. Level 0 is index 0. Level 1 is all indices reachable in 1 jump. Level 2 is all indices reachable in 2 jumps. The answer is the level at which you first reach the last index.

You don't need an actual BFS queue. Track `currentEnd` (the farthest index in the current level) and `farthest` (the farthest index reachable from the current level). When you reach `currentEnd`, you've exhausted the current level — increment jumps and set `currentEnd = farthest`.

**Why greedy works:** At each "level," you want to extend as far as possible. Taking the farthest jump at each level minimizes the number of levels (jumps) needed. This is the "greedy stays ahead" argument — after k jumps, greedy has reached at least as far as any other strategy.

```java
// LC 45 - Jump Game II
// Time: O(n), Space: O(1)
public int jump(int[] nums) {
    int jumps = 0;
    int currentEnd = 0;   // End of current BFS level
    int farthest = 0;     // Farthest reachable from current level
    
    // Don't process the last index — we don't need to jump from there
    for (int i = 0; i < nums.length - 1; i++) {
        // Update farthest reachable from this level
        farthest = Math.max(farthest, i + nums[i]);
        
        // Reached end of current level — must jump
        if (i == currentEnd) {
            jumps++;
            currentEnd = farthest;
            
            // Early exit if we can already reach the end
            if (currentEnd >= nums.length - 1) break;
        }
    }
    
    return jumps;
}
```

**Trace:**

```
nums = [2, 3, 1, 1, 4]

Level 0: [0]           currentEnd=0, farthest=0
i=0: farthest = max(0, 0+2) = 2
     i==currentEnd → jumps=1, currentEnd=2

Level 1: [1, 2]        currentEnd=2, farthest=2
i=1: farthest = max(2, 1+3) = 4
i=2: farthest = max(4, 2+1) = 4
     i==currentEnd → jumps=2, currentEnd=4
     currentEnd >= 4 → break

Answer: 2 jumps
```

**Complexity:** O(n) time, O(1) space.

**Common mistake:** Processing the last index in the loop. If `i < nums.length - 1`, you avoid an unnecessary jump increment when you're already at the destination.

---

### LC 53 — Maximum Subarray (Kadane's Algorithm)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty:** Medium
**Pattern:** Greedy/DP hybrid — Kadane's algorithm

**Problem:** Find the contiguous subarray with the largest sum.

**Kadane's Algorithm — The Greedy/DP Hybrid**

Kadane's is fascinating because it can be viewed as both greedy and DP, and understanding both views deepens your intuition.

**DP view:** Let `dp[i]` = maximum subarray sum ending at index i.
- `dp[i] = max(nums[i], dp[i-1] + nums[i])`
- Either start a new subarray at i, or extend the previous one.
- Answer = max of all `dp[i]`.

**Greedy view:** At each index, make a greedy decision: should I extend the current subarray or start fresh? If the current running sum is negative, starting fresh is always better — a negative prefix only hurts any future subarray. So: if `currentSum < 0`, reset to 0 (start fresh from next element).

**Why greedy works here:** The greedy choice (reset when sum goes negative) is provably optimal. Any subarray that includes a negative-sum prefix can be improved by dropping that prefix. So the greedy choice never discards a better option.

```java
// LC 53 - Maximum Subarray (Kadane's)
// Time: O(n), Space: O(1)
public int maxSubArray(int[] nums) {
    int maxSum = nums[0];
    int currentSum = nums[0];
    
    for (int i = 1; i < nums.length; i++) {
        // Greedy choice: extend current subarray or start fresh
        // If currentSum is negative, starting fresh is always better
        currentSum = Math.max(nums[i], currentSum + nums[i]);
        maxSum = Math.max(maxSum, currentSum);
    }
    
    return maxSum;
}
```

**Trace:**

```
nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]

i=0: currentSum = -2,  maxSum = -2
i=1: currentSum = max(1, -2+1) = max(1,-1) = 1,   maxSum = 1
i=2: currentSum = max(-3, 1-3) = max(-3,-2) = -2, maxSum = 1
i=3: currentSum = max(4, -2+4) = max(4,2) = 4,    maxSum = 4
i=4: currentSum = max(-1, 4-1) = max(-1,3) = 3,   maxSum = 4
i=5: currentSum = max(2, 3+2) = max(2,5) = 5,     maxSum = 5
i=6: currentSum = max(1, 5+1) = max(1,6) = 6,     maxSum = 6
i=7: currentSum = max(-5, 6-5) = max(-5,1) = 1,   maxSum = 6
i=8: currentSum = max(4, 1+4) = max(4,5) = 5,     maxSum = 6

Answer: 6 (subarray [4,-1,2,1])
```

**With subarray tracking (follow-up):**

```java
// LC 53 - Maximum Subarray with indices
public int[] maxSubArrayWithIndices(int[] nums) {
    int maxSum = nums[0];
    int currentSum = nums[0];
    int start = 0, end = 0, tempStart = 0;
    
    for (int i = 1; i < nums.length; i++) {
        if (nums[i] > currentSum + nums[i]) {
            // Start fresh
            currentSum = nums[i];
            tempStart = i;
        } else {
            currentSum += nums[i];
        }
        
        if (currentSum > maxSum) {
            maxSum = currentSum;
            start = tempStart;
            end = i;
        }
    }
    
    return new int[]{maxSum, start, end};
}
```

**Divide and conquer approach (O(n log n), useful for follow-up):**

```java
// Divide and conquer — useful when asked for O(n log n) or segment tree variant
public int maxSubArrayDivideConquer(int[] nums) {
    return helper(nums, 0, nums.length - 1);
}

private int helper(int[] nums, int left, int right) {
    if (left == right) return nums[left];
    
    int mid = left + (right - left) / 2;
    int leftMax = helper(nums, left, mid);
    int rightMax = helper(nums, mid + 1, right);
    int crossMax = crossSum(nums, left, mid, right);
    
    return Math.max(Math.max(leftMax, rightMax), crossMax);
}

private int crossSum(int[] nums, int left, int mid, int right) {
    int leftSum = Integer.MIN_VALUE, sum = 0;
    for (int i = mid; i >= left; i--) {
        sum += nums[i];
        leftSum = Math.max(leftSum, sum);
    }
    
    int rightSum = Integer.MIN_VALUE;
    sum = 0;
    for (int i = mid + 1; i <= right; i++) {
        sum += nums[i];
        rightSum = Math.max(rightSum, sum);
    }
    
    return leftSum + rightSum;
}
```

**Complexity:** Kadane's: O(n) time, O(1) space. Divide and conquer: O(n log n) time, O(log n) space.

**Interview note:** Kadane's is the expected answer. The divide and conquer approach comes up as a follow-up when the interviewer asks "can you solve this with a segment tree?" or "what if the array is distributed across machines?"

---

### LC 122 — Best Time to Buy and Sell Stock II

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** Greedy peaks and valleys

**Problem:** You can buy and sell on any day, multiple times (but only hold one stock at a time). Maximize profit.

**Greedy Insight:** Capture every upward slope. If `prices[i+1] > prices[i]`, add the difference to profit. This is equivalent to buying at every valley and selling at every peak.

**Why greedy works:** Any profit from a multi-day hold can be decomposed into single-day profits. If you hold from day 1 to day 3 and prices go 1→3→5, profit = 4. But 4 = (3-1) + (5-3) = 2 + 2. So capturing every positive day-to-day difference is equivalent to any optimal buy-sell strategy.

```java
// LC 122 - Best Time to Buy and Sell Stock II
// Time: O(n), Space: O(1)
public int maxProfit(int[] prices) {
    int profit = 0;
    
    for (int i = 1; i < prices.length; i++) {
        // Capture every upward movement
        if (prices[i] > prices[i - 1]) {
            profit += prices[i] - prices[i - 1];
        }
    }
    
    return profit;
}
```

**Trace:**

```
prices = [7, 1, 5, 3, 6, 4]

i=1: 1 < 7 → skip
i=2: 5 > 1 → profit += 4 → profit = 4
i=3: 3 < 5 → skip
i=4: 6 > 3 → profit += 3 → profit = 7
i=5: 4 < 6 → skip

Answer: 7
```

**Complexity:** O(n) time, O(1) space.

---

## Category B: Scheduling and Sorting Greedy

---

### LC 134 — Gas Station

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** Circular greedy with total sum check

**Problem:** n gas stations in a circle. `gas[i]` is gas available at station i. `cost[i]` is gas needed to travel from i to i+1. Find the starting station index to complete the circuit, or -1 if impossible.

**Greedy Insight (two observations):**

1. **Feasibility check:** If `sum(gas) < sum(cost)`, it's impossible. No starting point works.

2. **Starting point:** If a solution exists, start from the station after the point where the running sum hits its minimum. Equivalently: if you run out of gas at station j (running sum goes negative), the start must be after j. Reset the start to j+1 and reset the running sum.

**Why greedy works:** If you can't reach station j from start s, then no station between s and j can be a valid start either. Here's why: if you start at some station k between s and j, you arrive at k with 0 gas (you just started). But if you had started at s, you'd arrive at k with some non-negative amount of gas (since you made it from s to k). So starting at k is strictly worse than starting at s for reaching j. Since s couldn't reach j, k can't either.

```java
// LC 134 - Gas Station
// Time: O(n), Space: O(1)
public int canCompleteCircuit(int[] gas, int[] cost) {
    int totalGas = 0;
    int currentGas = 0;
    int startStation = 0;
    
    for (int i = 0; i < gas.length; i++) {
        int net = gas[i] - cost[i];
        totalGas += net;
        currentGas += net;
        
        // Can't reach next station from current start
        if (currentGas < 0) {
            // Try starting from next station
            startStation = i + 1;
            currentGas = 0;
        }
    }
    
    // If total gas >= total cost, a solution exists and startStation is it
    return totalGas >= 0 ? startStation : -1;
}
```

**Trace:**

```
gas  = [1, 2, 3, 4, 5]
cost = [3, 4, 5, 1, 2]
net  = [-2,-2,-2, 3, 3]

i=0: currentGas = -2 < 0 → startStation=1, currentGas=0
i=1: currentGas = -2 < 0 → startStation=2, currentGas=0
i=2: currentGas = -2 < 0 → startStation=3, currentGas=0
i=3: currentGas = 3
i=4: currentGas = 6

totalGas = -2-2-2+3+3 = 0 >= 0 → return startStation = 3
```

**Complexity:** O(n) time, O(1) space.

---

### LC 135 — Candy

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Hard
**Pattern:** Two-pass greedy

**Problem:** n children in a row with ratings. Each child must get at least 1 candy. Children with higher ratings than their neighbors must get more candy. Minimize total candies.

**Why two passes are needed:** The constraint is bidirectional. Child i must have more candy than child i-1 (if rating[i] > rating[i-1]) AND more than child i+1 (if rating[i] > rating[i+1]). A single left-to-right pass can only enforce the left constraint. A single right-to-left pass can only enforce the right constraint. You need both.

**Two-pass strategy:**
- Pass 1 (left to right): If `ratings[i] > ratings[i-1]`, set `candies[i] = candies[i-1] + 1`. This satisfies the "more than left neighbor" constraint.
- Pass 2 (right to left): If `ratings[i] > ratings[i+1]`, set `candies[i] = max(candies[i], candies[i+1] + 1)`. The `max` is critical — you must satisfy BOTH constraints, so take the larger of the two requirements.

**Why greedy works:** Each pass independently satisfies one direction of the constraint. The `max` in the second pass ensures both constraints are satisfied simultaneously with the minimum possible values. Any solution must satisfy both constraints, so the minimum satisfying both is optimal.

```java
// LC 135 - Candy
// Time: O(n), Space: O(n)
public int candy(int[] ratings) {
    int n = ratings.length;
    int[] candies = new int[n];
    Arrays.fill(candies, 1);  // Everyone gets at least 1
    
    // Pass 1: Left to right
    // Enforce: if ratings[i] > ratings[i-1], give more than left neighbor
    for (int i = 1; i < n; i++) {
        if (ratings[i] > ratings[i - 1]) {
            candies[i] = candies[i - 1] + 1;
        }
    }
    
    // Pass 2: Right to left
    // Enforce: if ratings[i] > ratings[i+1], give more than right neighbor
    // Use max to satisfy BOTH left and right constraints
    for (int i = n - 2; i >= 0; i--) {
        if (ratings[i] > ratings[i + 1]) {
            candies[i] = Math.max(candies[i], candies[i + 1] + 1);
        }
    }
    
    int total = 0;
    for (int c : candies) total += c;
    return total;
}
```

**Detailed Trace:**

```
ratings = [1, 0, 2]

Initial: candies = [1, 1, 1]

Pass 1 (left to right):
i=1: ratings[1]=0 > ratings[0]=1? NO → candies[1] stays 1
i=2: ratings[2]=2 > ratings[1]=0? YES → candies[2] = candies[1]+1 = 2

After pass 1: candies = [1, 1, 2]

Pass 2 (right to left):
i=1: ratings[1]=0 > ratings[2]=2? NO → candies[1] stays 1
i=0: ratings[0]=1 > ratings[1]=0? YES → candies[0] = max(1, 1+1) = 2

After pass 2: candies = [2, 1, 2]

Total = 2+1+2 = 5
```

```
ratings = [1, 2, 2]

Initial: candies = [1, 1, 1]

Pass 1 (left to right):
i=1: ratings[1]=2 > ratings[0]=1? YES → candies[1] = 2
i=2: ratings[2]=2 > ratings[1]=2? NO → candies[2] stays 1

After pass 1: candies = [1, 2, 1]

Pass 2 (right to left):
i=1: ratings[1]=2 > ratings[2]=2? NO → candies[1] stays 2
i=0: ratings[0]=1 > ratings[1]=2? NO → candies[0] stays 1

After pass 2: candies = [1, 2, 1]

Total = 1+2+1 = 4
```

**Edge case — equal ratings:** Equal ratings don't require more candy. Only strictly greater ratings trigger the constraint. This is why `ratings[i] > ratings[i-1]` uses strict inequality.

**Complexity:** O(n) time, O(n) space.

---

### LC 406 — Queue Reconstruction by Height

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium
**Pattern:** Sort + insert greedy

**Problem:** People described as `[h, k]` where h is height and k is the number of people in front with height >= h. Reconstruct the queue.

**Greedy Insight:** Sort by height descending, then by k ascending. Insert each person at position k in the result list.

**Why this works:** When you process people in decreasing height order, everyone already inserted is at least as tall as the current person. So inserting at position k means exactly k people in front are taller or equal — which is exactly what k requires. Shorter people inserted later don't affect the k-count of taller people already placed.

**Exchange argument:** Suppose we process a shorter person before a taller one. The shorter person's insertion might shift the taller person's position, violating their k constraint. Processing tallest first avoids this.

```java
// LC 406 - Queue Reconstruction by Height
// Time: O(n^2) due to list insertions, Space: O(n)
public int[][] reconstructQueue(int[][] people) {
    // Sort: taller first, then by k ascending for same height
    Arrays.sort(people, (a, b) -> {
        if (a[0] != b[0]) return b[0] - a[0];  // Descending height
        return a[1] - b[1];                      // Ascending k
    });
    
    List<int[]> result = new LinkedList<>();
    
    for (int[] person : people) {
        // Insert at position k — exactly k people in front are taller/equal
        result.add(person[1], person);
    }
    
    return result.toArray(new int[people.length][]);
}
```

**Trace:**

```
people = [[7,0],[4,4],[7,1],[5,0],[6,1],[5,2]]

After sort (desc height, asc k):
[[7,0],[7,1],[6,1],[5,0],[5,2],[4,4]]

Insert [7,0] at pos 0: [[7,0]]
Insert [7,1] at pos 1: [[7,0],[7,1]]
Insert [6,1] at pos 1: [[7,0],[6,1],[7,1]]
Insert [5,0] at pos 0: [[5,0],[7,0],[6,1],[7,1]]
Insert [5,2] at pos 2: [[5,0],[7,0],[5,2],[6,1],[7,1]]
Insert [4,4] at pos 4: [[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]]

Answer: [[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]]
```

**Complexity:** O(n² ) time (LinkedList insertions are O(n) each), O(n) space.

**Note:** Using LinkedList instead of ArrayList matters here. ArrayList insertions at arbitrary positions are O(n) due to shifting, but LinkedList insertions are O(1) once you have the position (though finding the position is still O(n) for LinkedList). In practice, both are O(n²) total, but LinkedList has better constants for this pattern.

---

### LC 452 — Minimum Number of Arrows to Burst Balloons

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium
**Pattern:** Interval greedy (cross-reference: Topic 18)
**Cross-reference:** See Topic 18 (Intervals) for the full interval overlap framework.

**Problem:** Balloons on a 2D plane, each described by `[xstart, xend]`. Arrows shot vertically burst all balloons whose x-range includes the arrow's x-position. Find minimum arrows.

**Greedy Insight:** Sort by end position. Shoot an arrow at the end of the first balloon. This arrow bursts all balloons that overlap with the first balloon's end. Move to the first balloon not burst, repeat.

**Why greedy works:** Shooting at the end of the current balloon is optimal — it's the rightmost position that still bursts the current balloon, maximizing the chance of bursting future balloons too. Any arrow shot to the left of this position would burst fewer future balloons.

```java
// LC 452 - Minimum Number of Arrows to Burst Balloons
// Time: O(n log n), Space: O(1)
public int findMinArrowShots(int[][] points) {
    // Sort by end position
    Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));
    
    int arrows = 1;
    int arrowPos = points[0][1];  // Shoot at end of first balloon
    
    for (int i = 1; i < points.length; i++) {
        // If this balloon starts after the current arrow, need a new arrow
        if (points[i][0] > arrowPos) {
            arrows++;
            arrowPos = points[i][1];  // Shoot at end of this balloon
        }
        // Otherwise, current arrow bursts this balloon too
    }
    
    return arrows;
}
```

**Note on integer overflow:** Use `Integer.compare(a[1], b[1])` instead of `a[1] - b[1]` in the comparator. The subtraction can overflow for large negative values.

**Complexity:** O(n log n) time, O(1) space.

---

## Category C: String and Partition Greedy

---

### LC 763 — Partition Labels

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** Last occurrence greedy (cross-reference: Topic 18)
**Cross-reference:** See Topic 18 (Intervals) for the interval merging perspective.

**Problem:** Partition string s into as many parts as possible so each letter appears in at most one part. Return the sizes of the parts.

**Greedy Insight:** For each character, find its last occurrence. A partition can end at position i only if all characters seen so far have their last occurrence at or before i. Track the farthest last occurrence seen — that's the minimum end of the current partition.

```java
// LC 763 - Partition Labels
// Time: O(n), Space: O(1) — 26 characters
public List<Integer> partitionLabels(String s) {
    // Find last occurrence of each character
    int[] last = new int[26];
    for (int i = 0; i < s.length(); i++) {
        last[s.charAt(i) - 'a'] = i;
    }
    
    List<Integer> result = new ArrayList<>();
    int start = 0;
    int end = 0;
    
    for (int i = 0; i < s.length(); i++) {
        // Extend current partition to include last occurrence of this char
        end = Math.max(end, last[s.charAt(i) - 'a']);
        
        // If we've reached the end of the current partition
        if (i == end) {
            result.add(end - start + 1);
            start = i + 1;
        }
    }
    
    return result;
}
```

**Complexity:** O(n) time, O(1) space.

---

### LC 678 — Valid Parenthesis String

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium
**Pattern:** Range tracking greedy

**Problem:** String with `(`, `)`, and `*` where `*` can be `(`, `)`, or empty. Return true if the string is valid.

**Greedy Insight:** Track a range `[lo, hi]` of possible open parenthesis counts. `lo` is the minimum possible open count (treating `*` as `)` or empty), `hi` is the maximum (treating `*` as `(`). If `hi < 0` at any point, it's impossible. If `lo > 0` at the end, there are unclosed parentheses.

**Why greedy works:** By tracking the range of possibilities rather than a single count, you avoid committing to a specific interpretation of `*` prematurely. The range captures all valid interpretations simultaneously.

```java
// LC 678 - Valid Parenthesis String
// Time: O(n), Space: O(1)
public boolean checkValidString(String s) {
    int lo = 0;  // Minimum possible open count (treat * as ) or empty)
    int hi = 0;  // Maximum possible open count (treat * as ()
    
    for (char c : s.toCharArray()) {
        if (c == '(') {
            lo++;
            hi++;
        } else if (c == ')') {
            lo--;
            hi--;
        } else {  // c == '*'
            lo--;  // Treat * as ) or empty
            hi++;  // Treat * as (
        }
        
        // hi < 0 means even with all * as (, we have too many )
        if (hi < 0) return false;
        
        // lo can't go below 0 — can't have negative open count
        lo = Math.max(lo, 0);
    }
    
    // Valid if minimum possible open count is 0
    return lo == 0;
}
```

**Trace:**

```
s = "(*)"

c='(': lo=1, hi=1
c='*': lo=0, hi=2
c=')': lo=-1→0, hi=1

lo=0 → return true
```

```
s = "(*))"

c='(': lo=1, hi=1
c='*': lo=0, hi=2
c=')': lo=-1→0, hi=1
c=')': lo=-1→0, hi=0

lo=0 → return true
```

**Complexity:** O(n) time, O(1) space.

---

### LC 846 — Hand of Straights

**Companies:** Amazon, Google, Microsoft
**Difficulty:** Medium
**Pattern:** Sorted map greedy

**Problem:** Given a hand of cards and group size W, can you rearrange all cards into groups of W consecutive cards?

**Greedy Insight:** Always start a new group from the smallest available card. If you can't form a complete consecutive group starting from the smallest card, it's impossible.

**Why greedy works:** The smallest card must be the start of some group (it can't be in the middle or end of a group, because there's nothing smaller to precede it). So greedily starting groups from the smallest card is the only valid choice.

```java
// LC 846 - Hand of Straights
// Time: O(n log n), Space: O(n)
public boolean isNStraightHand(int[] hand, int groupSize) {
    if (hand.length % groupSize != 0) return false;
    
    // Count frequencies, sorted by card value
    TreeMap<Integer, Integer> count = new TreeMap<>();
    for (int card : hand) {
        count.merge(card, 1, Integer::sum);
    }
    
    while (!count.isEmpty()) {
        int start = count.firstKey();  // Smallest available card
        
        // Try to form a group starting from 'start'
        for (int i = start; i < start + groupSize; i++) {
            if (!count.containsKey(i)) return false;
            
            int remaining = count.get(i) - 1;
            if (remaining == 0) {
                count.remove(i);
            } else {
                count.put(i, remaining);
            }
        }
    }
    
    return true;
}
```

**Complexity:** O(n log n) time (TreeMap operations), O(n) space.

---

## Category D: Advanced Greedy

---

### LC 621 — Task Scheduler

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** Heap-based greedy (cross-reference: Topic 12)
**Cross-reference:** See Topic 12 (Heaps & Priority Queues) for the heap-based scheduling framework.

**Problem:** Tasks labeled A-Z, CPU cooldown n between same tasks. Find minimum time to finish all tasks.

**Greedy Insight:** Always schedule the most frequent remaining task. This minimizes idle time by using the cooldown period productively.

**Mathematical shortcut:** The answer is `max(tasks.length, (maxFreq - 1) * (n + 1) + countOfMaxFreq)`.

- `(maxFreq - 1) * (n + 1)`: The most frequent task creates `maxFreq - 1` "frames" of size `n + 1`.
- `+ countOfMaxFreq`: Add the last occurrence of all tasks with max frequency.
- `tasks.length`: If there are enough tasks to fill all frames, no idle time is needed.

```java
// LC 621 - Task Scheduler (mathematical approach)
// Time: O(n), Space: O(1)
public int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;
    
    int maxFreq = 0;
    for (int f : freq) maxFreq = Math.max(maxFreq, f);
    
    // Count tasks with maximum frequency
    int countOfMaxFreq = 0;
    for (int f : freq) {
        if (f == maxFreq) countOfMaxFreq++;
    }
    
    // Formula: max(total tasks, minimum time with idle slots)
    int minTime = (maxFreq - 1) * (n + 1) + countOfMaxFreq;
    return Math.max(tasks.length, minTime);
}
```

**Heap-based simulation (more general, handles follow-ups):**

```java
// LC 621 - Task Scheduler (heap simulation)
// Time: O(n log 26) = O(n), Space: O(1)
public int leastIntervalHeap(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;
    
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    for (int f : freq) {
        if (f > 0) maxHeap.offer(f);
    }
    
    int time = 0;
    Queue<int[]> cooldown = new LinkedList<>();  // [remaining_freq, available_at_time]
    
    while (!maxHeap.isEmpty() || !cooldown.isEmpty()) {
        time++;
        
        // Release tasks whose cooldown has expired
        if (!cooldown.isEmpty() && cooldown.peek()[1] == time) {
            maxHeap.offer(cooldown.poll()[0]);
        }
        
        if (!maxHeap.isEmpty()) {
            int f = maxHeap.poll();
            if (f > 1) {
                cooldown.offer(new int[]{f - 1, time + n + 1});
            }
        }
        // else: CPU is idle this cycle
    }
    
    return time;
}
```

**Complexity:** O(n) time (mathematical), O(1) space.

---

### LC 1899 — Merge Triplets to Form Target Triplet

**Companies:** Amazon, Google
**Difficulty:** Medium
**Pattern:** Greedy selection

**Problem:** Given triplets `[a, b, c]` and a target `[x, y, z]`, you can merge two triplets by taking the max of each component. Find if you can form the target.

**Greedy Insight:** A triplet is "useful" if none of its components exceed the corresponding target component. Among useful triplets, check if the union of their max values equals the target.

**Why greedy works:** Any triplet with a component exceeding the target will corrupt that component when merged. So we must exclude such triplets. Among the remaining triplets, we want to check if we can achieve each target component — and since merging takes max, we just need some useful triplet to have each target component value.

```java
// LC 1899 - Merge Triplets to Form Target Triplet
// Time: O(n), Space: O(1)
public boolean mergeTriplets(int[][] triplets, int[] target) {
    int[] best = new int[3];  // Best achievable values from valid triplets
    
    for (int[] t : triplets) {
        // Skip triplets that would corrupt any target component
        if (t[0] > target[0] || t[1] > target[1] || t[2] > target[2]) {
            continue;
        }
        
        // Merge this triplet into best
        best[0] = Math.max(best[0], t[0]);
        best[1] = Math.max(best[1], t[1]);
        best[2] = Math.max(best[2], t[2]);
    }
    
    return best[0] == target[0] && best[1] == target[1] && best[2] == target[2];
}
```

**Complexity:** O(n) time, O(1) space.

---

### LC 330 — Patching Array

**Companies:** Google, Amazon
**Difficulty:** Hard
**Pattern:** Greedy reach extension

**Problem:** Given sorted array `nums` and integer n, find the minimum number of patches (numbers to add) so that every number in `[1, n]` can be represented as a sum of some subset of the array.

**Greedy Insight:** Maintain `reach` = the maximum number we can represent using elements processed so far. If `reach >= n`, we're done. If the next element `nums[i] <= reach + 1`, adding it extends our reach to `reach + nums[i]`. Otherwise, we must patch with `reach + 1` (the smallest number we can't represent), which extends reach to `2 * reach + 1`.

**Why greedy works:** If we can represent all numbers in `[1, reach]`, and the next element is `x <= reach + 1`, then we can represent all numbers in `[1, reach + x]`. The greedy patch choice (`reach + 1`) is optimal because it's the smallest missing number, and patching with it maximally extends our reach (doubles it plus 1).

```java
// LC 330 - Patching Array
// Time: O(m + log n) where m = nums.length, Space: O(1)
public int minPatches(int[] nums, int n) {
    long reach = 0;   // Can represent all numbers in [1, reach]
    int patches = 0;
    int i = 0;
    
    while (reach < n) {
        if (i < nums.length && nums[i] <= reach + 1) {
            // nums[i] fits — extend reach
            reach += nums[i];
            i++;
        } else {
            // Gap: can't represent reach+1, must patch with it
            reach += reach + 1;  // Patch with reach+1, new reach = 2*reach+1
            patches++;
        }
    }
    
    return patches;
}
```

**Trace:**

```
nums = [1, 3], n = 6

reach=0: nums[0]=1 <= 0+1=1 → reach=1, i=1
reach=1: nums[1]=3 <= 1+1=2? NO → patch with 2, reach=3, patches=1
reach=3: nums[1]=3 <= 3+1=4 → reach=6, i=2
reach=6 >= 6 → done

Answer: 1 patch (add 2)
Verification: {1,2,3} can make 1,2,3,4(1+3),5(2+3),6(1+2+3) ✓
```

**Note on `long`:** `reach` must be `long` because `2 * reach + 1` can overflow `int` when n is large (up to 2^31 - 1).

**Complexity:** O(m + log n) time — at most log n patches needed (each patch at least doubles reach), O(1) space.

---

## Common Mistakes

### 1. Applying Greedy When DP Is Needed

The most dangerous mistake. Greedy fails when the greedy choice property doesn't hold.

**0/1 Knapsack:** You can't greedily pick items by value/weight ratio. Example: capacity=4, items=[(weight=3,value=4),(weight=2,value=3),(weight=2,value=3)]. Greedy picks the first item (ratio 4/3 ≈ 1.33) for value 4. DP picks the last two items for value 6.

**Coin change with arbitrary denominations:** Greedy fails for denominations like {1, 3, 4} when making 6. Greedy gives 3 coins (4+1+1), DP gives 2 coins (3+3).

**Longest Increasing Subsequence:** Greedy (always extend the current subsequence) doesn't work. DP or patience sorting is needed.

**How to avoid:** Before coding greedy, ask yourself: "If I make this greedy choice, could I be cutting off a better option that I'd only discover later?" If yes, you need DP.

---

### 2. Wrong Sorting Criterion

Greedy problems often require a specific sort order, and getting it wrong produces wrong answers that are hard to debug.

**Queue Reconstruction:** Sorting by height ascending (instead of descending) breaks the algorithm. Shorter people inserted first get displaced by taller people inserted later.

**Activity Selection:** Sorting by start time (instead of end time) doesn't work. A long activity starting early blocks many short activities.

**Arrows/Balloons:** Sorting by start (instead of end) gives wrong results.

**How to avoid:** Think about what the sort criterion represents. For activity selection, "end time" means "finishes earliest, leaving most room for future activities." The sort criterion should encode the greedy choice.

---

### 3. Not Handling Ties in Sort

When two elements are equal on the primary sort key, the secondary sort key matters.

**Queue Reconstruction:** For people with the same height, sort by k ascending. If you sort by k descending, inserting at position k shifts earlier insertions, violating their constraints.

**Arrows:** For balloons with the same end position, the order doesn't matter (they'll all be burst by the same arrow). But for other interval problems, ties can matter.

**How to avoid:** Always think about what happens when two elements are equal. Write a test case with ties.

---

### 4. Not Proving Correctness

Coding a greedy solution without understanding why it works leads to bugs and failed interviews.

**Symptom:** You code a greedy solution, it passes some test cases, fails others, and you don't know why.

**Fix:** Before coding, state the greedy choice and argue why it's always safe. "I always pick X because Y. If I picked something else, Z would happen, which is worse/equal."

---

### 5. Integer Overflow in Comparators

Using `a - b` in comparators can overflow for large integers.

```java
// WRONG — can overflow
Arrays.sort(arr, (a, b) -> a[1] - b[1]);

// CORRECT
Arrays.sort(arr, (a, b) -> Integer.compare(a[1], b[1]));
```

This is especially dangerous in LC 452 (Minimum Arrows) where coordinates can be large negative numbers.

---

### 6. Off-by-One in Jump Game II

Processing the last index in the loop causes an extra jump increment.

```java
// WRONG — processes last index, may increment jumps unnecessarily
for (int i = 0; i < nums.length; i++) { ... }

// CORRECT — stop before last index
for (int i = 0; i < nums.length - 1; i++) { ... }
```

---

### 7. Forgetting `Math.max` in Two-Pass Greedy

In the second pass of Candy (and similar two-pass problems), you must take the max of the two passes, not just overwrite.

```java
// WRONG — overwrites left-to-right result
candies[i] = candies[i + 1] + 1;

// CORRECT — satisfies both constraints
candies[i] = Math.max(candies[i], candies[i + 1] + 1);
```

---

## Algorithm Comparison

### Greedy vs Dynamic Programming

The most important comparison for interviews.

| Scenario | Use Greedy | Use DP |
|---|---|---|
| Activity selection | Yes — sort by end time | No |
| 0/1 Knapsack | No | Yes |
| Fractional Knapsack | Yes — sort by ratio | No |
| Coin change (arbitrary) | No | Yes |
| Coin change (US denominations) | Yes | Either |
| Jump Game (can you reach?) | Yes | Either |
| Jump Game II (min jumps) | Yes | Either (DP is O(n²)) |
| Longest Increasing Subsequence | No | Yes |
| Maximum Subarray (Kadane's) | Yes (greedy/DP hybrid) | Yes |
| Edit Distance | No | Yes |
| Matrix Chain Multiplication | No | Yes |

**Decision framework:**

```
Does the problem ask for an optimal value (min/max/count)?
├── Yes: Does it have overlapping subproblems?
│   ├── Yes: DP
│   └── No: Does it have the greedy choice property?
│       ├── Yes: Greedy
│       └── No: Backtracking / brute force
└── No: Probably not optimization — different approach
```

**The key question:** "If I make the greedy choice now, do I need to reconsider it later?" If yes, DP. If no, greedy.

---

### Greedy vs Backtracking

| Property | Greedy | Backtracking |
|---|---|---|
| Decisions | One per step, never revisit | Explore all, backtrack on failure |
| Completeness | Not complete (may miss solutions) | Complete (finds all solutions) |
| Optimality | Optimal only with greedy choice property | Optimal with pruning |
| Time complexity | O(n log n) typical | Exponential worst case |
| Use case | Optimization with provable greedy choice | Constraint satisfaction, enumeration |

**When backtracking beats greedy:** Sudoku, N-Queens, word search, generating all permutations. These problems have no greedy choice property — you must explore all possibilities.

**When greedy beats backtracking:** Activity selection, Huffman coding, Dijkstra's. These have provable greedy choices that eliminate the need for exploration.

---

### Greedy vs BFS/DFS

Jump Game II is a good example where greedy and BFS are equivalent. The "BFS level" view of Jump Game II (each level = one jump) is exactly what the greedy algorithm computes, but without the overhead of an actual queue.

This pattern appears in other problems too: when BFS levels correspond to "cost" and you want minimum cost, greedy can often simulate BFS in O(n) instead of O(n + E).

---

## Quick Reference Cheat Sheet

### Problem Recognition

| Signal | Likely Pattern |
|---|---|
| "Minimum number of X" | Greedy (if greedy choice property holds) or DP |
| "Maximum number of non-overlapping" | Sort by end time, greedy |
| "Each element has a left and right constraint" | Two-pass greedy |
| "Always pick the best available" | Heap-based greedy |
| "Can you reach the end?" | Greedy reach tracking |
| "Partition into minimum parts" | Greedy with last-occurrence tracking |
| "Reconstruct from constraints" | Sort + insert greedy |

### Sorting Criteria by Problem Type

| Problem Type | Sort By |
|---|---|
| Activity selection / interval scheduling | End time ascending |
| Interval merging | Start time ascending |
| Queue reconstruction | Height descending, then k ascending |
| Arrows / balloon bursting | End position ascending |
| Fractional knapsack | Value/weight ratio descending |
| Task scheduling | Frequency descending (use heap) |

### Complexity Reference

| Algorithm | Time | Space |
|---|---|---|
| Sort + iterate | O(n log n) | O(1) |
| Two-pass greedy | O(n) | O(n) |
| Heap-based greedy | O(n log k) | O(k) |
| Kadane's | O(n) | O(1) |
| Jump Game | O(n) | O(1) |
| Jump Game II | O(n) | O(1) |
| Gas Station | O(n) | O(1) |
| Patching Array | O(m + log n) | O(1) |

### Greedy Proof Checklist

Before coding any greedy solution:

1. State the greedy choice: "At each step, I pick X."
2. Argue why it's safe: "Picking X is always at least as good as any other choice because..."
3. Identify the exchange argument: "If I swap X for Y, the solution doesn't improve because..."
4. Check edge cases: ties, empty input, single element, all same values.

---

## Practice Roadmap

### Week 1: Foundation (Days 1-5)

**Day 1-2: Array Greedy**
- LC 53 (Maximum Subarray) — understand Kadane's as greedy/DP hybrid
- LC 122 (Best Time to Buy and Sell Stock II) — simplest greedy
- LC 55 (Jump Game) — trace through the dry run above

**Day 3-4: Jump and Reach**
- LC 45 (Jump Game II) — BFS-level greedy
- LC 134 (Gas Station) — circular greedy with feasibility check

**Day 5: Two-Pass**
- LC 135 (Candy) — trace both passes manually before coding

---

### Week 2: Intermediate (Days 6-10)

**Day 6-7: Interval and Sorting Greedy**
- LC 452 (Minimum Arrows) — interval greedy
- LC 406 (Queue Reconstruction) — sort + insert

**Day 8-9: String Greedy**
- LC 763 (Partition Labels) — last occurrence tracking
- LC 678 (Valid Parenthesis String) — range tracking

**Day 10: Heap Greedy**
- LC 621 (Task Scheduler) — both mathematical and heap approaches

---

### Week 3: Advanced (Days 11-14)

**Day 11-12: Advanced Patterns**
- LC 846 (Hand of Straights) — sorted map greedy
- LC 1899 (Merge Triplets) — greedy selection

**Day 13: Hard**
- LC 330 (Patching Array) — greedy reach extension, hardest in this set

**Day 14: Review**
- Re-solve LC 55, 45, 135 from memory
- Practice explaining the greedy choice property for each problem
- Time yourself: aim for 15-20 minutes per medium, 25-30 per hard

---

### Interview Preparation Notes

**What interviewers test:**
1. Can you identify that greedy applies? (Not every optimization problem is greedy)
2. Can you articulate WHY greedy works? (Not just code it)
3. Can you handle edge cases? (Ties, empty input, single element)
4. Do you know when greedy fails? (0/1 knapsack, arbitrary coin change)

**What to say when you spot a greedy problem:**
"I think greedy might work here. The key insight is [greedy choice]. This works because [exchange argument / greedy stays ahead]. Let me verify with a small example before coding."

**Red flags that greedy won't work:**
- The problem has "0/1" choices (take or leave, not fractions)
- The problem asks for exact count/sum (not max/min)
- Small examples show greedy giving suboptimal results
- The problem is on a graph with cycles (usually needs DP or shortest path)

**The Kadane's special case:**
Kadane's algorithm is both greedy and DP. In interviews, calling it "greedy" or "DP" is both correct. The key insight is the same: at each position, decide whether to extend the current subarray or start fresh. This decision is locally optimal and globally optimal.

---

### Problem Index

| # | Problem | Difficulty | Category | Key Insight |
|---|---|---|---|---|
| LC 55 | Jump Game | Medium | Array | Track max reachable index |
| LC 45 | Jump Game II | Medium | Array | BFS-level greedy |
| LC 53 | Maximum Subarray | Medium | Array | Kadane's: reset when negative |
| LC 122 | Best Time to Buy/Sell II | Medium | Array | Capture every upward slope |
| LC 134 | Gas Station | Medium | Scheduling | Reset start after negative sum |
| LC 135 | Candy | Hard | Scheduling | Two-pass, take max |
| LC 406 | Queue Reconstruction | Medium | Scheduling | Sort desc height, insert at k |
| LC 452 | Minimum Arrows | Medium | Scheduling | Sort by end, shoot at end |
| LC 763 | Partition Labels | Medium | String | Last occurrence tracking |
| LC 678 | Valid Parenthesis String | Medium | String | Range [lo, hi] tracking |
| LC 846 | Hand of Straights | Medium | String | Start from smallest card |
| LC 621 | Task Scheduler | Medium | Advanced | Max frequency formula |
| LC 1899 | Merge Triplets | Medium | Advanced | Skip triplets exceeding target |
| LC 330 | Patching Array | Hard | Advanced | Greedy reach extension |

---

---

## Deep Dives

### Kadane's Algorithm: Full Analysis

Kadane's deserves special treatment because it sits at the intersection of greedy and DP, and interviewers frequently probe this boundary.

**The recurrence (DP view):**

```
dp[i] = maximum subarray sum ending at index i
dp[i] = max(nums[i], dp[i-1] + nums[i])
      = nums[i] + max(0, dp[i-1])
```

The base case is `dp[0] = nums[0]`. The answer is `max(dp[0], dp[1], ..., dp[n-1])`.

Since `dp[i]` only depends on `dp[i-1]`, we don't need the full array — just a rolling variable. That's why the space is O(1).

**The greedy view:**

At each index i, make a binary decision:
- **Extend:** Include nums[i] in the current subarray. New sum = currentSum + nums[i].
- **Restart:** Start a new subarray at i. New sum = nums[i].

The greedy choice: extend if `currentSum > 0`, restart if `currentSum <= 0`.

**Why this greedy choice is always correct:**

Claim: If `currentSum < 0`, any subarray ending at i that includes the prefix (the part giving currentSum) is worse than the subarray starting fresh at i.

Proof: Let S be any subarray ending at i that includes the prefix. Let S' be the subarray starting at i (just nums[i]). Then:
- sum(S) = currentSum + nums[i]
- sum(S') = nums[i]
- Since currentSum < 0: sum(S) < sum(S')

So S' is strictly better. The greedy choice (restart) is provably optimal.

**What if all numbers are negative?**

The algorithm handles this correctly because we initialize `maxSum = nums[0]` and `currentSum = nums[0]`. The loop starts at index 1. If all numbers are negative, `currentSum` will always restart (since any negative + negative is more negative than just the negative), and `maxSum` will track the least negative number.

```java
// Verify: all negative
nums = [-3, -1, -2]

i=0: currentSum = -3, maxSum = -3
i=1: currentSum = max(-1, -3-1) = max(-1,-4) = -1, maxSum = -1
i=2: currentSum = max(-2, -1-2) = max(-2,-3) = -2, maxSum = -1

Answer: -1 (correct — the subarray [-1])
```

**Common follow-up: circular maximum subarray (LC 918)**

For a circular array, the answer is either:
1. The maximum subarray in the linear array (Kadane's), OR
2. The total sum minus the minimum subarray (the "wrap-around" case)

```java
// LC 918 - Maximum Sum Circular Subarray
public int maxSubarraySumCircular(int[] nums) {
    int totalSum = 0;
    int maxSum = nums[0], currentMax = nums[0];
    int minSum = nums[0], currentMin = nums[0];
    
    for (int i = 1; i < nums.length; i++) {
        totalSum += nums[i];
        currentMax = Math.max(nums[i], currentMax + nums[i]);
        maxSum = Math.max(maxSum, currentMax);
        currentMin = Math.min(nums[i], currentMin + nums[i]);
        minSum = Math.min(minSum, currentMin);
    }
    
    totalSum += nums[0];  // Add nums[0] back (we started loop at 1)
    
    // If all numbers are negative, maxSum is the answer (can't use circular)
    // because totalSum - minSum would be 0 (empty subarray not allowed)
    if (maxSum < 0) return maxSum;
    
    return Math.max(maxSum, totalSum - minSum);
}
```

---

### The Exchange Argument: A Worked Example

The exchange argument is the most important proof technique for greedy algorithms. Here's a complete worked example for Activity Selection.

**Problem:** Given activities with start/end times, pick the maximum number of non-overlapping activities.

**Greedy:** Sort by end time. Always pick the activity that ends earliest and doesn't conflict with the last picked activity.

**Claim:** The greedy solution is optimal.

**Proof by exchange argument:**

Let G = greedy solution = {g1, g2, ..., gk} sorted by end time.
Let O = any optimal solution = {o1, o2, ..., om} sorted by end time.

We want to show k = m (greedy picks as many as optimal).

**Step 1:** Show that G and O can be made to agree on the first activity.

- g1 ends at time e(g1). o1 ends at time e(o1).
- Since greedy picks the earliest-ending activity, e(g1) ≤ e(o1).
- Swap o1 for g1 in O. The new solution O' = {g1, o2, ..., om} is still valid because:
  - g1 ends no later than o1, so g1 doesn't conflict with o2 (since o1 didn't conflict with o2).
  - O' has the same size as O.

**Step 2:** Repeat for the second activity, third, etc.

After i swaps, the first i activities of O match G. Since O has m activities and we can always swap without reducing size, G has at least m activities. But G is a valid solution, so G has at most m activities (since m is optimal). Therefore k = m.

**Conclusion:** Greedy is optimal.

This proof structure — "show you can swap the greedy choice in without making things worse" — applies to most greedy problems. Practice articulating it for each problem in this document.

---

### When Greedy Fails: Detailed Examples

**Example 1: 0/1 Knapsack**

Capacity = 5. Items: [(weight=4, value=5), (weight=3, value=4), (weight=3, value=4)].

Greedy by value/weight ratio:
- Item 1: ratio = 5/4 = 1.25 → pick it (weight used: 4, value: 5)
- Item 2: ratio = 4/3 ≈ 1.33 → can't fit (only 1 capacity left)
- Item 3: ratio = 4/3 ≈ 1.33 → can't fit

Greedy total: 5.

Optimal: pick items 2 and 3 (weight = 6 > 5, doesn't fit). Pick item 1 and item 2? Weight = 7 > 5. Pick item 2 only? Value = 4. Pick item 1 only? Value = 5.

Actually optimal is 5 here. Let's use a clearer example.

Capacity = 4. Items: [(weight=3, value=4), (weight=2, value=3), (weight=2, value=3)].

Greedy by ratio: Item 1 ratio = 4/3 ≈ 1.33, Items 2,3 ratio = 3/2 = 1.5.
- Pick item 2 (ratio 1.5, weight 2, value 3). Remaining capacity: 2.
- Pick item 3 (ratio 1.5, weight 2, value 3). Remaining capacity: 0.
- Greedy total: 6.

Wait, greedy works here. Let's use the classic counterexample.

Capacity = 4. Items: [(weight=4, value=5), (weight=3, value=4), (weight=2, value=3)].

Greedy by ratio: 5/4=1.25, 4/3≈1.33, 3/2=1.5.
- Pick item 3 (weight 2, value 3). Remaining: 2.
- Pick item 2 (weight 3)? Doesn't fit. Pick item 1 (weight 4)? Doesn't fit.
- Greedy total: 3.

Optimal: Pick item 1 (weight 4, value 5). Total: 5.

Greedy gives 3, optimal gives 5. Greedy fails.

**Why it fails:** After picking item 3, we can't fit anything else. But item 1 alone is better. The greedy choice (highest ratio first) doesn't account for the fact that a lower-ratio item might fill the knapsack better.

**The fix:** DP. `dp[w]` = maximum value achievable with capacity w.

**Example 2: Coin Change with Arbitrary Denominations**

Denominations: {1, 3, 4}. Target: 6.

Greedy: pick 4 (largest ≤ 6). Remaining: 2. Pick 1. Remaining: 1. Pick 1. Total: 3 coins.

Optimal: pick 3. Remaining: 3. Pick 3. Total: 2 coins.

**Why it fails:** Picking 4 leaves a remainder (2) that can't be covered efficiently. The greedy choice doesn't account for how the remainder will be covered.

**The fix:** DP. `dp[i]` = minimum coins to make amount i.

**The pattern:** Greedy fails when the "locally best" choice creates a remainder that's harder to cover than a "locally worse" choice that creates an easier remainder.

---

### Greedy in Graph Algorithms

Several classic graph algorithms are greedy:

**Dijkstra's Algorithm (Shortest Path)**

Greedy choice: always relax the unvisited node with the smallest tentative distance.

Why it works: With non-negative edge weights, once a node's distance is finalized, no future path can improve it. This is the greedy choice property.

Why it fails with negative edges: A negative edge discovered later could improve a previously finalized distance. Bellman-Ford handles this by relaxing all edges n-1 times.

```java
// Dijkstra's — greedy shortest path
public int[] dijkstra(int[][] graph, int src) {
    int n = graph.length;
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    
    // Min-heap: [distance, node]
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
    pq.offer(new int[]{0, src});
    
    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int d = curr[0], u = curr[1];
        
        if (d > dist[u]) continue;  // Stale entry
        
        for (int v = 0; v < n; v++) {
            if (graph[u][v] > 0) {
                int newDist = dist[u] + graph[u][v];
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.offer(new int[]{newDist, v});
                }
            }
        }
    }
    
    return dist;
}
```

**Prim's Algorithm (Minimum Spanning Tree)**

Greedy choice: always add the cheapest edge connecting the current tree to a new vertex.

Why it works: Matroid theory guarantees that greedy on a graphic matroid (the set of spanning forests) is optimal.

**Kruskal's Algorithm (Minimum Spanning Tree)**

Greedy choice: sort all edges by weight, add each edge if it doesn't create a cycle (use Union-Find to check).

Why it works: Same matroid argument as Prim's. The greedy choice (cheapest edge that doesn't create a cycle) is always part of some MST.

---

### Fractional Knapsack vs 0/1 Knapsack

This comparison is a classic interview question.

**Fractional Knapsack:** You can take fractions of items. Greedy works.

```java
// Fractional Knapsack — greedy by value/weight ratio
public double fractionalKnapsack(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    Integer[] indices = new Integer[n];
    for (int i = 0; i < n; i++) indices[i] = i;
    
    // Sort by value/weight ratio descending
    Arrays.sort(indices, (a, b) -> 
        Double.compare((double) values[b] / weights[b], 
                       (double) values[a] / weights[a]));
    
    double totalValue = 0;
    int remaining = capacity;
    
    for (int i : indices) {
        if (remaining <= 0) break;
        
        if (weights[i] <= remaining) {
            // Take the whole item
            totalValue += values[i];
            remaining -= weights[i];
        } else {
            // Take a fraction
            totalValue += (double) values[i] * remaining / weights[i];
            remaining = 0;
        }
    }
    
    return totalValue;
}
```

**Why greedy works for fractional:** You can always take a fraction. If the highest-ratio item doesn't fully fit, take as much as possible. There's no "wasted capacity" problem because fractions fill gaps perfectly.

**Why greedy fails for 0/1:** You can't take fractions. A high-ratio item might leave a gap that lower-ratio items can't fill efficiently. You need DP to consider all combinations.

**The key difference:** Divisibility. Greedy works when items are divisible (fractional knapsack, Huffman coding). DP is needed when items are indivisible (0/1 knapsack, coin change with arbitrary denominations).

---

*Next: Topic 20 — Dynamic Programming (the natural complement to greedy: when greedy fails, DP saves you)*
