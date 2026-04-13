# Topic 7: Monotonic Stack & Queue

Document 7 of 20 in the FAANG DSA Prep series.

---

## Overview

A monotonic stack (or queue) maintains elements in sorted order, either increasing or decreasing from bottom to top. The core power: it answers "next greater element" and "next smaller element" questions in O(n) time, compared to the naive O(n²) brute force.

Combined with stack-based area calculations, this data structure solves some of the hardest interview problems you'll encounter: Largest Rectangle in Histogram, Trapping Rain Water (stack approach), Sum of Subarray Minimums, and Remove K Digits. These problems appear constantly at Google, Amazon, and Meta.

**Connection to previous topics:** Topic 1 (Sliding Window) used a monotonic deque for LC 239 (Sliding Window Maximum). That document introduced the deque briefly. This document fully explains the monotonic deque, why it works, and its broader applications beyond sliding windows.

**Top companies asking these problems:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5 Intuition](#eli5-intuition)
3. [When to Use — Recognition Signals](#when-to-use--recognition-signals)
4. [Core Templates in Java](#core-templates-in-java)
5. [Real-World Applications](#real-world-applications)
6. [Problem Categories and Solutions](#problem-categories-and-solutions)
7. [Common Mistakes and Edge Cases](#common-mistakes-and-edge-cases)
8. [Pattern Comparison](#pattern-comparison)
9. [Quick Reference Cheat Sheet](#quick-reference-cheat-sheet)
10. [Practice Roadmap](#practice-roadmap)

---

## Core Concept

### What is a Monotonic Stack?

A stack where elements are always in sorted order (increasing or decreasing from bottom to top). When you push a new element, you first pop all elements that would violate the sorted order.

That's it. The entire data structure is just a regular stack with a disciplined push operation.

```
Monotonically Increasing Stack (bottom to top):
[1, 3, 5, 7]  ← valid
[1, 3, 2, 7]  ← invalid (2 < 3 violates increasing order)

Monotonically Decreasing Stack (bottom to top):
[9, 7, 4, 2]  ← valid
[9, 7, 8, 2]  ← invalid (8 > 7 violates decreasing order)
```

### What is a Monotonic Queue/Deque?

Same principle, but using a double-ended queue (deque) instead of a stack. This allows removal from both ends, which is essential for sliding window problems where elements expire from the front as the window moves.

A monotonic deque maintains sorted order AND supports efficient window expiration. This is what makes LC 239 (Sliding Window Maximum) O(n) instead of O(n log n).

### Two Variants

**1. Monotonically Increasing Stack** — elements increase from bottom to top.

When you push element `x`, you pop everything from the top that is `>= x` (or `> x` depending on the problem). The stack always has the smallest elements at the top.

Used to find: **Next Smaller Element**, **Previous Smaller Element**

**2. Monotonically Decreasing Stack** — elements decrease from bottom to top.

When you push element `x`, you pop everything from the top that is `<= x` (or `< x`). The stack always has the largest elements at the top.

Used to find: **Next Greater Element**, **Previous Greater Element**

### The O(n) Insight

The inner `while` loop looks like it could make this O(n²). It doesn't. Here's why:

Every element is pushed onto the stack exactly once and popped from the stack at most once. Over the entire array traversal, the total number of push operations is n, and the total number of pop operations is at most n. So the total work is O(2n) = O(n).

This is the same amortized analysis as dynamic array resizing.

### What Happens During a Pop

This is the key insight that unlocks every problem in this category:

When you pop element `x` because the current element `curr` violated the monotonic order:
- `curr` is the **next greater/smaller** element for `x` (the element that caused the pop)
- `stack.peek()` (the new top after popping `x`) is the **previous greater/smaller** element for `x`

So a single pop event gives you three pieces of information simultaneously:
1. Which element was popped (the "center" element)
2. Its next boundary (the current element `curr`)
3. Its previous boundary (the new stack top after popping)

LC 84 (Largest Rectangle in Histogram) and LC 907 (Sum of Subarray Minimums) both exploit all three pieces of information from a single pop.

---

## ELI5 Intuition

### Next Greater Element

You're standing in a line of people with different heights. You look to your right. The first person taller than you is your "next greater element."

Now imagine processing everyone from left to right. When a new person arrives, they look at the people waiting in the stack. If the new person is taller than the person at the top of the stack, that waiting person finally has their answer: "the next taller person is you." The waiting person leaves the stack (gets popped), records their answer, and the new person joins the stack to wait for their own answer.

A monotonically decreasing stack processes people left to right. When a taller person arrives, all shorter people currently waiting get their answer simultaneously.

People who never get popped (still in the stack at the end) have no next greater element. Their answer is -1.

### Largest Rectangle in Histogram

Imagine a bar chart made of unit-width blocks. You want to find the largest rectangle you can draw using consecutive bars, where the rectangle height is limited by the shortest bar in the range.

The monotonic stack tracks bars in increasing height order. When a shorter bar arrives, you know the taller bars can't extend further right (the new shorter bar blocks them). So you calculate their maximum rectangles right then.

For each popped bar:
- Height = the popped bar's height
- Width = from the previous bar in the stack (its left boundary) to the current bar (its right boundary)
- Area = height × width

The stack ensures you always process bars in the right order, and each bar's maximum possible rectangle is calculated exactly once.

### Sliding Window Maximum (Monotonic Deque)

You're watching a conveyor belt of numbers through a window of size k. You want the maximum in the current window at every step.

A monotonic decreasing deque keeps candidates for the maximum. When a new number arrives, any number in the deque that's smaller is useless (the new number is both newer and larger, so it'll always beat those smaller numbers while they're in the window). Remove them from the back.

When the window slides forward, the oldest element might fall out. Remove it from the front if it's no longer in the window.

The front of the deque is always the maximum of the current window.

---

## When to Use — Recognition Signals

### Green Flags (reach for monotonic stack/queue)

- "next greater element" or "next smaller element"
- "previous greater element" or "previous smaller element"
- "daily temperatures" or "how many days until..."
- "stock span" or "consecutive days with lower/higher value"
- "largest rectangle in histogram"
- "maximal rectangle" in a binary matrix
- "sum of subarray minimums" or "sum of subarray maximums"
- "sliding window maximum" or "sliding window minimum"
- "remove k digits" to get smallest/largest number
- "remove duplicate letters" with lexicographic constraints
- "next greater node in linked list"
- Any problem asking about the "nearest" element satisfying a comparison

### Red Flags (don't use monotonic stack)

- You need ALL pairs satisfying a condition, not just the nearest one (use brute force or segment tree)
- You need elements in sorted order for range queries (use TreeMap or segment tree)
- The problem is about subarrays as a whole, not individual element relationships
- You need the k-th greater/smaller element (use heap or binary search)

### The Pattern Recognition Test

Ask yourself: "For each element, do I need to find the nearest element to its left or right that is greater or smaller?" If yes, monotonic stack. If the question is about ranges, windows, or aggregates, think about whether a deque variant applies.

---

## Core Templates in Java

### Template 1: Next Greater Element (Monotonic Decreasing Stack)

**Key rule:** Store indices, not values. Indices let you calculate distances and look up values in the original array.

```java
public int[] nextGreaterElement(int[] arr) {
    int n = arr.length;
    int[] result = new int[n];
    Arrays.fill(result, -1); // default: no next greater element
    
    // Stack stores INDICES (not values)
    // Invariant: arr[stack elements] are in decreasing order from bottom to top
    Deque<Integer> stack = new ArrayDeque<>();
    
    for (int i = 0; i < n; i++) {
        // While stack is not empty AND current element is greater than
        // the element at the top of the stack → top has found its next greater
        while (!stack.isEmpty() && arr[stack.peek()] < arr[i]) {
            int poppedIdx = stack.pop();
            result[poppedIdx] = arr[i]; // arr[i] is the next greater for poppedIdx
        }
        stack.push(i);
    }
    
    // Elements remaining in stack have no next greater → result stays -1
    return result;
}
```

**Execution trace on `[2, 1, 2, 4, 3]`:**

```
i=0, arr[0]=2: stack empty, push 0.       stack=[0]        result=[-1,-1,-1,-1,-1]
i=1, arr[1]=1: arr[0]=2 >= arr[1]=1, push 1. stack=[0,1]   result=[-1,-1,-1,-1,-1]
i=2, arr[2]=2: arr[1]=1 < arr[2]=2, pop 1. result[1]=2.
               arr[0]=2 >= arr[2]=2, stop. push 2. stack=[0,2]  result=[-1,2,-1,-1,-1]
i=3, arr[3]=4: arr[2]=2 < arr[3]=4, pop 2. result[2]=4.
               arr[0]=2 < arr[3]=4, pop 0. result[0]=4.
               stack empty, push 3.        stack=[3]        result=[4,2,4,-1,-1]
i=4, arr[4]=3: arr[3]=4 >= arr[4]=3, push 4. stack=[3,4]   result=[4,2,4,-1,-1]

End: indices 3 and 4 remain in stack → result[3]=-1, result[4]=-1
Final: [4, 2, 4, -1, -1]
```

**Invariant:** At any point, the stack contains indices of elements in decreasing order of their values. The stack represents "elements waiting for their next greater element."

**Comparison direction:** Use `arr[stack.peek()] < arr[i]` for strict next greater. Use `<=` if you want "next greater or equal."

---

### Template 2: Next Smaller Element (Monotonic Increasing Stack)

Flip the comparison. Now the stack maintains increasing order (smallest at top), and we pop when a smaller element arrives.

```java
public int[] nextSmallerElement(int[] arr) {
    int n = arr.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    
    // Stack stores INDICES
    // Invariant: arr[stack elements] are in increasing order from bottom to top
    Deque<Integer> stack = new ArrayDeque<>();
    
    for (int i = 0; i < n; i++) {
        // Pop while current element is SMALLER than top → top found its next smaller
        while (!stack.isEmpty() && arr[stack.peek()] > arr[i]) {
            int poppedIdx = stack.pop();
            result[poppedIdx] = arr[i];
        }
        stack.push(i);
    }
    
    return result;
}
```

**Execution trace on `[4, 2, 1, 3, 5]`:**

```
i=0, arr[0]=4: stack empty, push 0.       stack=[0]
i=1, arr[1]=2: arr[0]=4 > arr[1]=2, pop 0. result[0]=2.
               stack empty, push 1.        stack=[1]
i=2, arr[2]=1: arr[1]=2 > arr[2]=1, pop 1. result[1]=1.
               stack empty, push 2.        stack=[2]
i=3, arr[3]=3: arr[2]=1 <= arr[3]=3, push 3. stack=[2,3]
i=4, arr[4]=5: arr[3]=3 <= arr[4]=5, push 4. stack=[2,3,4]

End: indices 2,3,4 remain → result[2]=-1, result[3]=-1, result[4]=-1
Final: [2, 1, -1, -1, -1]
```

**The only change from Template 1:** `arr[stack.peek()] > arr[i]` instead of `< arr[i]`. Everything else is identical.

---

### Template 3: Previous Greater / Previous Smaller Element

Two approaches. The cleaner one: process in the same direction, but read the answer from `stack.peek()` BEFORE pushing (not during a pop).

```java
public int[] previousGreaterElement(int[] arr) {
    int n = arr.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    
    // Stack stores INDICES, decreasing order (for previous greater)
    Deque<Integer> stack = new ArrayDeque<>();
    
    for (int i = 0; i < n; i++) {
        // Pop elements that are NOT greater than current (they can't be previous greater)
        while (!stack.isEmpty() && arr[stack.peek()] <= arr[i]) {
            stack.pop();
        }
        
        // After popping, if stack is not empty, top is the previous greater element
        if (!stack.isEmpty()) {
            result[i] = arr[stack.peek()];
        }
        
        stack.push(i);
    }
    
    return result;
}
```

**Execution trace on `[3, 1, 4, 1, 5]`:**

```
i=0, arr[0]=3: stack empty. result[0]=-1. push 0.  stack=[0]
i=1, arr[1]=1: arr[0]=3 > arr[1]=1, stop popping.
               stack not empty, result[1]=arr[0]=3. push 1. stack=[0,1]
i=2, arr[2]=4: arr[1]=1 <= arr[2]=4, pop 1.
               arr[0]=3 <= arr[2]=4, pop 0.
               stack empty. result[2]=-1. push 2.  stack=[2]
i=3, arr[3]=1: arr[2]=4 > arr[3]=1, stop.
               result[3]=arr[2]=4. push 3.          stack=[2,3]
i=4, arr[4]=5: arr[3]=1 <= arr[4]=5, pop 3.
               arr[2]=4 <= arr[4]=5, pop 2.
               stack empty. result[4]=-1. push 4.  stack=[4]

Final: [-1, 3, -1, 4, -1]
```

**For Previous Smaller:** flip the comparison to `arr[stack.peek()] >= arr[i]` in the while loop.

**Alternative approach:** Process the array in reverse with the "next" templates. "Previous greater from left" = "next greater from right" when traversing right to left.

---

### Template 4: Monotonic Deque for Sliding Window Min/Max

This is the data structure used in LC 239 (Sliding Window Maximum), cross-referenced from Topic 1.

```java
public int[] slidingWindowMaximum(int[] arr, int k) {
    int n = arr.length;
    int[] result = new int[n - k + 1];
    
    // Deque stores INDICES
    // Invariant: arr[deque elements] are in decreasing order from front to back
    // Front of deque = index of maximum in current window
    Deque<Integer> deque = new ArrayDeque<>();
    
    for (int i = 0; i < n; i++) {
        // Step 1: Remove indices that are outside the current window
        // Window is [i-k+1, i], so remove front if it's < i-k+1
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }
        
        // Step 2: Remove from BACK while back element is smaller than current
        // Those elements can never be the maximum (current is both newer and larger)
        while (!deque.isEmpty() && arr[deque.peekLast()] < arr[i]) {
            deque.pollLast();
        }
        
        deque.offerLast(i);
        
        // Step 3: Record result once window is fully formed
        if (i >= k - 1) {
            result[i - k + 1] = arr[deque.peekFirst()];
        }
    }
    
    return result;
}
```

**Execution trace on `arr=[1,3,-1,-3,5,3,6,7], k=3`:**

```
i=0, arr[0]=1:  deque=[0]
i=1, arr[1]=3:  arr[0]=1 < arr[1]=3, remove 0. deque=[1]
i=2, arr[2]=-1: arr[1]=3 >= arr[2]=-1, keep. deque=[1,2]. Window [0,2] formed. result[0]=arr[1]=3
i=3, arr[3]=-3: arr[2]=-1 >= arr[3]=-3, keep. deque=[1,2,3]. Window [1,3]. result[1]=arr[1]=3
i=4, arr[4]=5:  Front=1, window starts at 2, 1 < 2 → remove front. deque=[2,3].
                arr[3]=-3 < 5, remove 3. arr[2]=-1 < 5, remove 2. deque=[4].
                Window [2,4]. result[2]=arr[4]=5
i=5, arr[5]=3:  arr[4]=5 >= 3, keep. deque=[4,5]. Window [3,5]. result[3]=arr[4]=5
i=6, arr[6]=6:  arr[5]=3 < 6, remove 5. arr[4]=5 < 6, remove 4. deque=[6].
                Window [4,6]. result[4]=arr[6]=6
i=7, arr[7]=7:  arr[6]=6 < 7, remove 6. deque=[7]. Window [5,7]. result[5]=arr[7]=7

Final: [3, 3, 5, 5, 6, 7]
```

**For Sliding Window Minimum:** change `arr[deque.peekLast()] < arr[i]` to `arr[deque.peekLast()] > arr[i]`. The deque becomes monotonically increasing, and the front is always the minimum.

**Connection to Topic 1:** LC 239 was introduced in the Sliding Window document as an advanced problem. The deque approach here is the O(n) solution. The heap approach (also valid) is O(n log k). For large k, the deque wins.

---

## Real-World Applications

### 1. Stock Market Analysis

The Stock Span problem (LC 901) asks: for each trading day, how many consecutive previous days had a price lower than or equal to today's price? This is exactly "previous greater element" in disguise.

Financial analysts use this to compute momentum indicators. A stock with a span of 5 means it's been rising (or holding) for 5 consecutive days, which is a bullish signal in technical analysis. The monotonic stack computes spans for all days in O(n) instead of O(n²).

### 2. CPU Task Scheduling

In operating systems, when a new high-priority task arrives, the scheduler needs to find which waiting tasks have lower priority and can be preempted. This is a "next greater element" query on a priority queue. Monotonic stack logic underlies efficient priority-based preemption in real-time systems.

### 3. Skyline Problem in Architecture

Computing the skyline of a city from a list of building heights and positions uses stack-based rectangle calculations. The skyline is the outer contour of all buildings viewed from a distance. The algorithm processes buildings left to right, maintaining a stack of "active" building heights, which is conceptually similar to the histogram rectangle problem.

### 4. Histogram-based Image Analysis

In medical imaging and satellite imagery, finding the largest uniform rectangular region in a histogram (e.g., a region with consistent pixel intensity) is exactly LC 84. Radiologists use automated tools that apply this algorithm to detect anomalies in scan data.

### 5. Expression Evaluation and Parsing

Compilers use monotonic stacks for operator precedence parsing. When parsing `3 + 4 * 5`, the parser maintains a stack of operators. When a lower-precedence operator arrives, higher-precedence operators on the stack are evaluated first. This is the same "pop when violated" logic as a monotonic stack.

### 6. Temperature and Weather Analysis

"How many days until a warmer day?" is exactly LC 739 (Daily Temperatures). Weather forecasting systems use this to compute "warm spell duration" metrics. The same logic applies to any time-series data where you need the next event exceeding a threshold.

---

## Problem Categories and Solutions

### Company Tags Reference

| Problem | Companies |
|---------|-----------|
| LC 496 Next Greater Element I | Amazon, Google, Meta, Microsoft, Bloomberg |
| LC 503 Next Greater Element II | Amazon, Google, Meta, Microsoft, Bloomberg |
| LC 739 Daily Temperatures | Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs |
| LC 84 Largest Rectangle in Histogram | Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs |
| LC 85 Maximal Rectangle | Amazon, Google, Meta, Microsoft, Bloomberg, Apple |
| LC 42 Trapping Rain Water | Google, Amazon, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs |
| LC 907 Sum of Subarray Minimums | Amazon, Google, Meta, Microsoft, Bloomberg |
| LC 402 Remove K Digits | Amazon, Google, Meta, Microsoft, Bloomberg |
| LC 316 Remove Duplicate Letters | Amazon, Google, Meta, Microsoft, Bloomberg |
| LC 239 Sliding Window Maximum | Google, Amazon, Microsoft, Bloomberg, Apple, Citadel, DoorDash, Goldman Sachs (48 companies) |
| LC 901 Online Stock Span | Amazon, Google, Meta, Microsoft, Bloomberg |
| LC 962 Maximum Width Ramp | Amazon, Google |
| LC 1019 Next Greater Node in Linked List | Amazon, Google, Microsoft |

---

### Category A: Next Greater / Smaller Element

---

#### LC 496 — Next Greater Element I (Easy)

**Problem:** Given two arrays `nums1` and `nums2` where `nums1` is a subset of `nums2`, for each element in `nums1`, find its next greater element in `nums2`. Return -1 if none exists.

**Why it matters:** This is the foundational problem. Master this before anything else in this category.

**Approach:** Apply the next greater element template on `nums2`. Store results in a HashMap keyed by value (since `nums2` has no duplicates). Then look up each element of `nums1`.

```java
public int[] nextGreaterElement(int[] nums1, int[] nums2) {
    // Map: value → next greater value in nums2
    Map<Integer, Integer> nextGreater = new HashMap<>();
    Deque<Integer> stack = new ArrayDeque<>(); // stores values (nums2 has no duplicates)
    
    for (int num : nums2) {
        while (!stack.isEmpty() && stack.peek() < num) {
            nextGreater.put(stack.pop(), num);
        }
        stack.push(num);
    }
    // Remaining elements in stack have no next greater
    while (!stack.isEmpty()) {
        nextGreater.put(stack.pop(), -1);
    }
    
    int[] result = new int[nums1.length];
    for (int i = 0; i < nums1.length; i++) {
        result[i] = nextGreater.get(nums1[i]);
    }
    return result;
}
```

**Complexity:** O(m + n) time, O(n) space where n = nums2.length, m = nums1.length.

**Note:** Here we store values instead of indices because `nums2` has no duplicates and we need to look up by value. In most other problems, store indices.

---

#### LC 503 — Next Greater Element II (Medium)

**Problem:** Given a circular array, find the next greater element for each element. The array wraps around.

**Key insight:** Simulate the circular array by processing `2*n` elements using modulo indexing. Only record results for the first `n` indices.

```java
public int[] nextGreaterElements(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>(); // stores indices
    
    // Process 2*n elements to simulate circular traversal
    for (int i = 0; i < 2 * n; i++) {
        int idx = i % n; // actual index in array
        while (!stack.isEmpty() && nums[stack.peek()] < nums[idx]) {
            result[stack.pop()] = nums[idx];
        }
        // Only push during first pass (indices 0 to n-1)
        // Second pass only resolves remaining elements, doesn't add new ones
        if (i < n) {
            stack.push(idx);
        }
    }
    
    return result;
}
```

**Execution trace on `[1, 2, 1]`:**

```
i=0 (idx=0, val=1): stack empty, push 0.    stack=[0]
i=1 (idx=1, val=2): nums[0]=1 < 2, pop 0. result[0]=2. push 1. stack=[1]
i=2 (idx=2, val=1): nums[1]=2 >= 1, push 2. stack=[1,2]
i=3 (idx=0, val=1): nums[2]=1 >= 1, no pop. i>=n, don't push.
i=4 (idx=1, val=2): nums[2]=1 < 2, pop 2. result[2]=2. nums[1]=2 >= 2, stop. i>=n, don't push.
i=5 (idx=2, val=1): nums[1]=2 >= 1, no pop. i>=n, don't push.

Final: [2, -1, 2]
```

**Complexity:** O(n) time, O(n) space.

---

#### LC 739 — Daily Temperatures (Medium)

**Problem:** Given an array `temperatures`, return an array `answer` where `answer[i]` is the number of days until a warmer temperature. If no warmer day exists, `answer[i] = 0`.

**Why it matters:** This is the warm-up problem for the entire category. Every FAANG interviewer who asks LC 84 will expect you to have solved this first. The "days until" framing is just "next greater element" with distance instead of value.

**Approach:** Next greater element template. When popping index `j` because `temperatures[i] > temperatures[j]`, the answer for `j` is `i - j` (the distance).

```java
public int[] dailyTemperatures(int[] temperatures) {
    int n = temperatures.length;
    int[] answer = new int[n]; // default 0 (no warmer day)
    Deque<Integer> stack = new ArrayDeque<>(); // stores indices
    
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && temperatures[stack.peek()] < temperatures[i]) {
            int prevIdx = stack.pop();
            answer[prevIdx] = i - prevIdx; // distance to next warmer day
        }
        stack.push(i);
    }
    
    // Elements remaining in stack: answer stays 0 (no warmer day)
    return answer;
}
```

**Dry Run on `[73, 74, 75, 71, 69, 72, 76, 73]`:**

```
i=0, temp=73: stack empty. push 0.          stack=[0]        answer=[0,0,0,0,0,0,0,0]
i=1, temp=74: temp[0]=73 < 74. pop 0.       answer[0]=1-0=1. stack=[]
              stack empty. push 1.           stack=[1]        answer=[1,0,0,0,0,0,0,0]
i=2, temp=75: temp[1]=74 < 75. pop 1.       answer[1]=2-1=1. stack=[]
              stack empty. push 2.           stack=[2]        answer=[1,1,0,0,0,0,0,0]
i=3, temp=71: temp[2]=75 >= 71. push 3.     stack=[2,3]      answer=[1,1,0,0,0,0,0,0]
i=4, temp=69: temp[3]=71 >= 69. push 4.     stack=[2,3,4]    answer=[1,1,0,0,0,0,0,0]
i=5, temp=72: temp[4]=69 < 72. pop 4.       answer[4]=5-4=1.
              temp[3]=71 < 72. pop 3.        answer[3]=5-3=2.
              temp[2]=75 >= 72. stop. push 5. stack=[2,5]     answer=[1,1,0,2,1,0,0,0]
i=6, temp=76: temp[5]=72 < 76. pop 5.       answer[5]=6-5=1.
              temp[2]=75 < 76. pop 2.        answer[2]=6-2=4.
              stack empty. push 6.           stack=[6]        answer=[1,1,4,2,1,1,0,0]
i=7, temp=73: temp[6]=76 >= 73. push 7.     stack=[6,7]      answer=[1,1,4,2,1,1,0,0]

End: indices 6 and 7 remain → answer[6]=0, answer[7]=0 (already 0)
Final: [1, 1, 4, 2, 1, 1, 0, 0]
```

**Complexity:** O(n) time, O(n) space.

**Interview tip:** When the interviewer asks "can you do better than O(n²)?", this is the answer. Walk through the dry run above. It demonstrates the amortized O(n) argument clearly.

---

#### LC 901 — Online Stock Span (Medium)

**Problem:** Design a class `StockSpanner` with a method `next(price)` that returns the span of the stock's price for the current day. The span is the number of consecutive days (including today) where the price was less than or equal to today's price.

**Why it matters:** This is "previous greater element" in an online (streaming) setting. The stack persists across calls.

**Key insight:** Instead of storing all previous prices, store (price, span) pairs. When popping, accumulate the span of the popped element into the current span. This compresses consecutive lower prices into a single entry.

```java
class StockSpanner {
    // Stack stores (price, span) pairs
    // span = how many consecutive days this price "absorbed"
    private Deque<int[]> stack;
    
    public StockSpanner() {
        stack = new ArrayDeque<>();
    }
    
    public int next(int price) {
        int span = 1; // at minimum, today counts
        
        // Pop all prices that are <= current price, accumulate their spans
        while (!stack.isEmpty() && stack.peek()[0] <= price) {
            span += stack.pop()[1];
        }
        
        stack.push(new int[]{price, span});
        return span;
    }
}
```

**Execution trace on prices `[100, 80, 60, 70, 60, 75, 85]`:**

```
next(100): stack empty. span=1. push [100,1]. stack=[[100,1]]. return 1
next(80):  100 > 80, stop. span=1. push [80,1].  stack=[[100,1],[80,1]]. return 1
next(60):  80 > 60, stop. span=1. push [60,1].   stack=[[100,1],[80,1],[60,1]]. return 1
next(70):  60 <= 70, pop [60,1]. span=1+1=2.
           80 > 70, stop. push [70,2].            stack=[[100,1],[80,1],[70,2]]. return 2
next(60):  70 > 60, stop. span=1. push [60,1].   stack=[[100,1],[80,1],[70,2],[60,1]]. return 1
next(75):  60 <= 75, pop [60,1]. span=1+1=2.
           70 <= 75, pop [70,2]. span=2+2=4.
           80 > 75, stop. push [75,4].            stack=[[100,1],[80,1],[75,4]]. return 4
next(85):  75 <= 85, pop [75,4]. span=1+4=5.
           80 <= 85, pop [80,1]. span=5+1=6.
           100 > 85, stop. push [85,6].           stack=[[100,1],[85,6]]. return 6
```

**Complexity:** O(1) amortized per call (each price is pushed and popped at most once), O(n) space.

---

#### LC 1019 — Next Greater Node in Linked List (Medium)

**Problem:** Given a linked list, return an array where `result[i]` is the value of the next node with a greater value than node `i`. Return 0 if none exists.

**Approach:** Convert to array first (or process inline), then apply next greater element template.

```java
public int[] nextLargerNodes(ListNode head) {
    // Convert linked list to array
    List<Integer> vals = new ArrayList<>();
    for (ListNode curr = head; curr != null; curr = curr.next) {
        vals.add(curr.val);
    }
    
    int n = vals.size();
    int[] result = new int[n];
    Deque<Integer> stack = new ArrayDeque<>(); // stores indices
    
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && vals.get(stack.peek()) < vals.get(i)) {
            result[stack.pop()] = vals.get(i);
        }
        stack.push(i);
    }
    
    // Remaining in stack → result stays 0
    return result;
}
```

**Complexity:** O(n) time, O(n) space.

---

### Category B: Rectangle / Histogram Problems

---

#### LC 84 — Largest Rectangle in Histogram (Hard)

**This is the most important problem in this entire topic.** Google and Amazon ask it constantly. If you can solve this cleanly in an interview, you demonstrate mastery of monotonic stacks.

**Problem:** Given an array `heights` representing the heights of bars in a histogram (each bar has width 1), find the area of the largest rectangle that can be formed.

**Key insight:** For each bar `i`, the largest rectangle with height `heights[i]` extends:
- Left until it hits a bar shorter than `heights[i]`
- Right until it hits a bar shorter than `heights[i]`

So for each bar, we need its "previous smaller element" (left boundary) and "next smaller element" (right boundary). A monotonic increasing stack gives us both in a single pass.

**When we pop index `j` because `heights[i] < heights[j]`:**
- `heights[j]` is the height of the rectangle
- `i` is the right boundary (exclusive)
- `stack.peek()` is the left boundary (exclusive), which is the previous smaller element
- Width = `i - stack.peek() - 1`

**The sentinel trick:** Push -1 onto the stack initially. This handles the case where the stack is empty after popping (meaning the bar extends all the way to the left edge). The sentinel index -1 makes `width = i - (-1) - 1 = i` correct.

```java
public int largestRectangleArea(int[] heights) {
    int n = heights.length;
    int maxArea = 0;
    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(-1); // sentinel: left boundary for bars that extend to the left edge
    
    for (int i = 0; i <= n; i++) {
        // Use height 0 at index n as a sentinel to flush remaining bars
        int currHeight = (i == n) ? 0 : heights[i];
        
        while (stack.peek() != -1 && heights[stack.peek()] >= currHeight) {
            int height = heights[stack.pop()];
            int width = i - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }
        
        stack.push(i);
    }
    
    return maxArea;
}
```

**Complete Step-by-Step Stack Trace on `heights = [2, 1, 5, 6, 2, 3]`:**

```
Initial: stack=[-1], maxArea=0

i=0, currHeight=2:
  stack.peek()=-1 (sentinel), stop while loop.
  push 0. stack=[-1, 0]

i=1, currHeight=1:
  stack.peek()=0, heights[0]=2 >= currHeight=1 → enter while loop
    pop 0. height=heights[0]=2.
    new stack.peek()=-1. width = 1 - (-1) - 1 = 1.
    area = 2 * 1 = 2. maxArea = max(0, 2) = 2.
  stack.peek()=-1 (sentinel), stop.
  push 1. stack=[-1, 1]

i=2, currHeight=5:
  stack.peek()=1, heights[1]=1 < currHeight=5 → stop while loop.
  push 2. stack=[-1, 1, 2]

i=3, currHeight=6:
  stack.peek()=2, heights[2]=5 < currHeight=6 → stop while loop.
  push 3. stack=[-1, 1, 2, 3]

i=4, currHeight=2:
  stack.peek()=3, heights[3]=6 >= currHeight=2 → enter while loop
    pop 3. height=heights[3]=6.
    new stack.peek()=2. width = 4 - 2 - 1 = 1.
    area = 6 * 1 = 6. maxArea = max(2, 6) = 6.
  stack.peek()=2, heights[2]=5 >= currHeight=2 → continue while loop
    pop 2. height=heights[2]=5.
    new stack.peek()=1. width = 4 - 1 - 1 = 2.
    area = 5 * 2 = 10. maxArea = max(6, 10) = 10.
  stack.peek()=1, heights[1]=1 < currHeight=2 → stop while loop.
  push 4. stack=[-1, 1, 4]

i=5, currHeight=3:
  stack.peek()=4, heights[4]=2 < currHeight=3 → stop while loop.
  push 5. stack=[-1, 1, 4, 5]

i=6, currHeight=0 (sentinel flush):
  stack.peek()=5, heights[5]=3 >= 0 → enter while loop
    pop 5. height=heights[5]=3.
    new stack.peek()=4. width = 6 - 4 - 1 = 1.
    area = 3 * 1 = 3. maxArea = max(10, 3) = 10.
  stack.peek()=4, heights[4]=2 >= 0 → continue
    pop 4. height=heights[4]=2.
    new stack.peek()=1. width = 6 - 1 - 1 = 4.
    area = 2 * 4 = 8. maxArea = max(10, 8) = 10.
  stack.peek()=1, heights[1]=1 >= 0 → continue
    pop 1. height=heights[1]=1.
    new stack.peek()=-1. width = 6 - (-1) - 1 = 6.
    area = 1 * 6 = 6. maxArea = max(10, 6) = 10.
  stack.peek()=-1 (sentinel), stop.

Final maxArea = 10
```

**Visual confirmation:**

```
Heights: [2, 1, 5, 6, 2, 3]
         
    6
  5 6
  5 6
2   5 6 2 3
2 1 5 6 2 3
─────────────
0 1 2 3 4 5

The rectangle of area 10 spans indices 2-3 with height 5:
  [_, _, 5, 5, _, _] → 5 * 2 = 10 ✓
```

**Why the sentinel at index -1 works:**

When we pop a bar and the stack only has the sentinel (-1) left, it means the popped bar extends all the way to the left edge (index 0). The width formula `i - stack.peek() - 1 = i - (-1) - 1 = i` correctly gives the width from index 0 to index i-1.

**Why the sentinel height 0 at index n works:**

After processing all bars, some bars remain in the stack (those that never found a shorter bar to their right). By appending a virtual bar of height 0, we force all remaining bars to be popped and their areas calculated.

**Complexity:** O(n) time, O(n) space.

**Common interview follow-up:** "What if all bars have the same height?" The sentinel at index n (height 0) handles this. All bars get popped at i=n, and the width calculation gives the full array width.

---

#### LC 85 — Maximal Rectangle (Hard)

**Problem:** Given a binary matrix filled with 0s and 1s, find the largest rectangle containing only 1s.

**Key insight:** Build a histogram for each row. For row `r`, `heights[c]` = number of consecutive 1s ending at row `r` in column `c`. Then apply LC 84 on each row's histogram.

```java
public int maximalRectangle(char[][] matrix) {
    if (matrix == null || matrix.length == 0) return 0;
    int rows = matrix.length;
    int cols = matrix[0].length;
    int[] heights = new int[cols];
    int maxArea = 0;
    
    for (int r = 0; r < rows; r++) {
        // Update histogram heights for this row
        for (int c = 0; c < cols; c++) {
            if (matrix[r][c] == '1') {
                heights[c]++;
            } else {
                heights[c] = 0; // reset: no consecutive 1s ending here
            }
        }
        // Apply LC 84 on current histogram
        maxArea = Math.max(maxArea, largestRectangleArea(heights));
    }
    
    return maxArea;
}

private int largestRectangleArea(int[] heights) {
    int n = heights.length;
    int maxArea = 0;
    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(-1);
    
    for (int i = 0; i <= n; i++) {
        int currHeight = (i == n) ? 0 : heights[i];
        while (stack.peek() != -1 && heights[stack.peek()] >= currHeight) {
            int height = heights[stack.pop()];
            int width = i - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }
        stack.push(i);
    }
    
    return maxArea;
}
```

**Example on:**
```
matrix = [
  ['1','0','1','0','0'],
  ['1','0','1','1','1'],
  ['1','1','1','1','1'],
  ['1','0','0','1','0']
]
```

```
After row 0: heights = [1, 0, 1, 0, 0] → largestRect = 1
After row 1: heights = [2, 0, 2, 1, 1] → largestRect = 3
After row 2: heights = [3, 1, 3, 2, 2] → largestRect = 6
After row 3: heights = [4, 0, 0, 3, 0] → largestRect = 4

maxArea = 6
```

**Complexity:** O(rows × cols) time, O(cols) space.

**Interview tip:** When you see "maximal rectangle in binary matrix," immediately say "I'll reduce this to Largest Rectangle in Histogram applied row by row." This shows you recognize the reduction pattern.

---

#### LC 42 — Trapping Rain Water (Hard) — Stack Approach

**Cross-reference:** Topic 2 (Two Pointers) solved this with the two-pointer approach in O(n) time and O(1) space. This document shows the stack-based approach, which is O(n) time and O(n) space. Both are valid in interviews; the two-pointer approach is more space-efficient.

**Problem:** Given an elevation map, compute how much water it can trap after raining.

**Stack approach intuition:** Water is trapped in "valleys." A valley forms when we have a left wall, a lower middle section, and a right wall. The stack maintains a decreasing sequence of heights (potential left walls). When a taller bar arrives, it forms the right wall, and the top of the stack is the valley bottom.

```java
public int trap(int[] height) {
    int n = height.length;
    int water = 0;
    Deque<Integer> stack = new ArrayDeque<>(); // stores indices, decreasing heights
    
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && height[stack.peek()] < height[i]) {
            int bottom = stack.pop(); // valley bottom
            
            if (stack.isEmpty()) break; // no left wall
            
            int left = stack.peek(); // left wall index
            int right = i;          // right wall index
            
            int boundedHeight = Math.min(height[left], height[right]) - height[bottom];
            int width = right - left - 1;
            water += boundedHeight * width;
        }
        stack.push(i);
    }
    
    return water;
}
```

**Execution trace on `[0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]`:**

```
i=0, h=0: push 0.  stack=[0]
i=1, h=1: h[0]=0 < 1. pop 0 (bottom=0). stack empty, break. push 1. stack=[1]
i=2, h=0: h[1]=1 >= 0. push 2. stack=[1,2]
i=3, h=2: h[2]=0 < 2. pop 2 (bottom=2). left=1, right=3.
          boundedH = min(h[1]=1, h[3]=2) - h[2]=0 = 1-0 = 1. width=3-1-1=1. water+=1.
          h[1]=1 < 2. pop 1 (bottom=1). stack empty, break. push 3. stack=[3]
i=4, h=1: h[3]=2 >= 1. push 4. stack=[3,4]
i=5, h=0: h[4]=1 >= 0. push 5. stack=[3,4,5]
i=6, h=1: h[5]=0 < 1. pop 5 (bottom=5). left=4, right=6.
          boundedH = min(h[4]=1, h[6]=1) - h[5]=0 = 1-0 = 1. width=6-4-1=1. water+=1. total=2.
          h[4]=1 >= 1. stop. push 6. stack=[3,4,6]
i=7, h=3: h[6]=1 < 3. pop 6 (bottom=6). left=4, right=7.
          boundedH = min(h[4]=1, h[7]=3) - h[6]=1 = 1-1 = 0. width=7-4-1=2. water+=0.
          h[4]=1 < 3. pop 4 (bottom=4). left=3, right=7.
          boundedH = min(h[3]=2, h[7]=3) - h[4]=1 = 2-1 = 1. width=7-3-1=3. water+=3. total=5.
          h[3]=2 < 3. pop 3 (bottom=3). stack empty, break. push 7. stack=[7]
i=8, h=2: h[7]=3 >= 2. push 8. stack=[7,8]
i=9, h=1: h[8]=2 >= 1. push 9. stack=[7,8,9]
i=10,h=2: h[9]=1 < 2. pop 9 (bottom=9). left=8, right=10.
          boundedH = min(h[8]=2, h[10]=2) - h[9]=1 = 2-1 = 1. width=10-8-1=1. water+=1. total=6.
          h[8]=2 >= 2. stop. push 10. stack=[7,8,10]
i=11,h=1: h[10]=2 >= 1. push 11. stack=[7,8,10,11]

Final water = 6
```

**Comparison with two-pointer approach (from Topic 2):**

| Aspect | Stack Approach | Two-Pointer Approach |
|--------|---------------|---------------------|
| Time | O(n) | O(n) |
| Space | O(n) | O(1) |
| Intuition | Layer-by-layer (horizontal slices) | Column-by-column (vertical slices) |
| Code complexity | Moderate | Simple |
| Interview preference | Shows stack mastery | Shows pointer mastery |

Both are correct. In an interview, mention both and let the interviewer choose. If they ask for O(1) space, use two pointers.

---

#### LC 907 — Sum of Subarray Minimums (Medium)

**Problem:** Given an array `arr`, find the sum of `min(subarray)` for every subarray. Return the answer modulo 10^9 + 7.

**Key insight:** For each element `arr[i]`, count how many subarrays have `arr[i]` as their minimum. If `arr[i]` is the minimum of `k` subarrays, it contributes `arr[i] * k` to the total sum.

For element at index `i`:
- Let `left[i]` = number of elements to the left (including `i`) until we hit a strictly smaller element. This is `i - previousSmaller[i]`.
- Let `right[i]` = number of elements to the right (including `i`) until we hit a smaller or equal element. This is `nextSmallerOrEqual[i] - i`.

The number of subarrays where `arr[i]` is the minimum = `left[i] * right[i]`.

**Why "strictly smaller" on left but "smaller or equal" on right?** To avoid double-counting when there are duplicate minimums. This is a subtle but critical detail.

```java
public int sumSubarrayMins(int[] arr) {
    int n = arr.length;
    long MOD = 1_000_000_007L;
    long result = 0;
    
    // Stack stores indices, increasing order (for finding previous/next smaller)
    Deque<Integer> stack = new ArrayDeque<>();
    
    // For each element, compute contribution when it's popped
    // When arr[i] < arr[stack.peek()], the popped element j has:
    //   - right boundary: i (next smaller or equal)
    //   - left boundary: stack.peek() after pop (previous smaller, strictly)
    
    for (int i = 0; i <= n; i++) {
        int currVal = (i == n) ? 0 : arr[i]; // sentinel 0 flushes remaining
        
        while (!stack.isEmpty() && arr[stack.peek()] > currVal) {
            int j = stack.pop();
            int left = stack.isEmpty() ? j + 1 : j - stack.peek();
            int right = i - j;
            result = (result + (long) arr[j] * left * right) % MOD;
        }
        
        stack.push(i);
    }
    
    return (int) result;
}
```

**Trace on `[3, 1, 2, 4]`:**

```
i=0, val=3: stack empty. push 0. stack=[0]
i=1, val=1: arr[0]=3 > 1. pop 0 (j=0).
            stack empty → left = j+1 = 1. right = i-j = 1-0 = 1.
            contribution = arr[0]*1*1 = 3*1*1 = 3. result=3.
            stack empty. push 1. stack=[1]
i=2, val=2: arr[1]=1 <= 2. push 2. stack=[1,2]
i=3, val=4: arr[2]=2 <= 4. push 3. stack=[1,2,3]
i=4, val=0 (sentinel):
  arr[3]=4 > 0. pop 3 (j=3).
    left = j - stack.peek() = 3-2 = 1. right = 4-3 = 1.
    contribution = 4*1*1 = 4. result=7.
  arr[2]=2 > 0. pop 2 (j=2).
    left = j - stack.peek() = 2-1 = 1. right = 4-2 = 2.
    contribution = 2*1*2 = 4. result=11.
  arr[1]=1 > 0. pop 1 (j=1).
    stack empty → left = j+1 = 2. right = 4-1 = 3.
    contribution = 1*2*3 = 6. result=17.

Final: 17
```

**Verification:** All subarrays and their minimums:
```
[3]=3, [1]=1, [2]=2, [4]=4
[3,1]=1, [1,2]=1, [2,4]=2
[3,1,2]=1, [1,2,4]=1
[3,1,2,4]=1
Sum = 3+1+2+4+1+1+2+1+1+1 = 17 ✓
```

**Complexity:** O(n) time, O(n) space.

---

### Category C: Greedy + Monotonic Stack

---

#### LC 402 — Remove K Digits (Medium)

**Problem:** Given a non-negative integer represented as a string `num` and an integer `k`, remove `k` digits to make the resulting number as small as possible.

**Key insight:** To minimize the number, we want the leftmost digits to be as small as possible. When we see a digit smaller than the previous one, removing the previous digit makes the number smaller. This is exactly "maintain a monotonically increasing stack."

```java
public String removeKdigits(String num, int k) {
    Deque<Character> stack = new ArrayDeque<>();
    
    for (char digit : num.toCharArray()) {
        // While we can still remove digits AND top of stack is larger than current
        // removing the larger digit makes the number smaller
        while (k > 0 && !stack.isEmpty() && stack.peek() > digit) {
            stack.pop();
            k--;
        }
        stack.push(digit);
    }
    
    // If k > 0, remove from the end (the largest digits are at the top)
    while (k > 0) {
        stack.pop();
        k--;
    }
    
    // Build result, removing leading zeros
    StringBuilder sb = new StringBuilder();
    boolean leadingZero = true;
    
    // Stack is LIFO, so we need to reverse
    // Use a deque as a stack: push to front, read from front
    // Actually, let's rebuild properly:
    Deque<Character> result = new ArrayDeque<>();
    while (!stack.isEmpty()) {
        result.push(stack.pop()); // reverse the stack
    }
    
    for (char c : result) {
        if (leadingZero && c == '0') continue;
        leadingZero = false;
        sb.append(c);
    }
    
    return sb.length() == 0 ? "0" : sb.toString();
}
```

**Cleaner implementation using ArrayDeque as a stack with bottom-to-top iteration:**

```java
public String removeKdigits(String num, int k) {
    // Use LinkedList as deque for easy iteration
    Deque<Character> stack = new ArrayDeque<>();
    
    for (char digit : num.toCharArray()) {
        while (k > 0 && !stack.isEmpty() && stack.peekLast() > digit) {
            stack.pollLast();
            k--;
        }
        stack.offerLast(digit);
    }
    
    // Remove remaining k digits from the end (they're the largest)
    for (int i = 0; i < k; i++) {
        stack.pollLast();
    }
    
    // Build result, skip leading zeros
    StringBuilder sb = new StringBuilder();
    boolean leadingZero = true;
    for (char c : stack) { // iterates front to back
        if (leadingZero && c == '0') continue;
        leadingZero = false;
        sb.append(c);
    }
    
    return sb.length() == 0 ? "0" : sb.toString();
}
```

**Trace on `num="1432219", k=3`:**

```
digit='1': stack empty. push '1'. stack=[1]
digit='4': '1' <= '4'. push '4'. stack=[1,4]
digit='3': '4' > '3'. pop '4'. k=2. '1' <= '3'. push '3'. stack=[1,3]
digit='2': '3' > '2'. pop '3'. k=1. '1' <= '2'. push '2'. stack=[1,2]
digit='2': '2' >= '2'. push '2'. stack=[1,2,2]
digit='1': '2' > '1'. pop '2'. k=0. k=0, stop. push '1'. stack=[1,2,1]
digit='9': k=0, no pops. push '9'. stack=[1,2,1,9]

k=0, no end removal.
Result: "1219"
```

**Complexity:** O(n) time, O(n) space.

**Edge cases:**
- `k >= num.length()`: return "0"
- All digits increasing (e.g., "12345"): remove last k digits
- All digits decreasing (e.g., "54321"): remove first k digits

---

#### LC 316 — Remove Duplicate Letters / LC 1081 — Smallest Subsequence of Distinct Characters (Medium)

**Problem:** Given a string, remove duplicate letters so that every letter appears exactly once. The result must be the smallest in lexicographic order among all possible results.

**Key insight:** Greedy + monotonic stack. Maintain an increasing stack of characters. When a smaller character arrives, pop larger characters IF they appear later in the string (so we won't lose them permanently).

**Data structures needed:**
1. `count[]`: remaining frequency of each character (decremented as we process)
2. `inStack[]`: whether a character is already in the stack (avoid duplicates)

```java
public String removeDuplicateLetters(String s) {
    int[] count = new int[26];
    boolean[] inStack = new boolean[26];
    
    // Count frequency of each character
    for (char c : s.toCharArray()) {
        count[c - 'a']++;
    }
    
    Deque<Character> stack = new ArrayDeque<>();
    
    for (char c : s.toCharArray()) {
        count[c - 'a']--; // we've seen this character, decrement remaining count
        
        if (inStack[c - 'a']) continue; // already in result, skip
        
        // Pop larger characters if they appear later (count > 0)
        while (!stack.isEmpty() && stack.peek() > c && count[stack.peek() - 'a'] > 0) {
            inStack[stack.pop() - 'a'] = false;
        }
        
        stack.push(c);
        inStack[c - 'a'] = true;
    }
    
    // Build result (stack is LIFO, reverse it)
    StringBuilder sb = new StringBuilder();
    for (char c : stack) {
        sb.append(c);
    }
    return sb.reverse().toString();
}
```

**Trace on `s="bcabc"`:**

```
Initial count: b=2, c=2, a=1

Process 'b': count[b]=1. b not in stack. stack empty. push 'b'. inStack[b]=true.
             stack=[b]
Process 'c': count[c]=1. c not in stack. 'b' < 'c', stop. push 'c'. inStack[c]=true.
             stack=[b,c]
Process 'a': count[a]=0. a not in stack.
             'c' > 'a' AND count[c]=1 > 0 → pop 'c'. inStack[c]=false.
             'b' > 'a' AND count[b]=1 > 0 → pop 'b'. inStack[b]=false.
             stack empty. push 'a'. inStack[a]=true.
             stack=[a]
Process 'b': count[b]=0. b not in stack. 'a' < 'b', stop. push 'b'. inStack[b]=true.
             stack=[a,b]
Process 'c': count[c]=0. c not in stack. 'b' < 'c', stop. push 'c'. inStack[c]=true.
             stack=[a,b,c]

Result (reversed): "abc"
```

**Why `count[stack.peek() - 'a'] > 0` matters:** We can only pop a character if it appears again later. If `count == 0`, this is the last occurrence, and we must keep it.

**Complexity:** O(n) time, O(1) space (stack has at most 26 characters).

---

### Category D: Monotonic Deque

---

#### LC 239 — Sliding Window Maximum (Hard)

**Cross-reference:** This problem was introduced in Topic 1 (Sliding Window) as an advanced application. The full explanation of the monotonic deque is here.

**Problem:** Given an array `nums` and window size `k`, return the maximum of each window as it slides from left to right.

**Why not a heap?** A max-heap gives O(n log k). The monotonic deque gives O(n). For large k, the deque is significantly faster.

**Why not a sorted set?** A TreeMap gives O(n log k) and handles duplicates, but the deque is simpler and faster.

**Full solution (see Template 4 above for the complete code):**

```java
public int[] maxSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> deque = new ArrayDeque<>(); // stores indices, decreasing values
    
    for (int i = 0; i < n; i++) {
        // Remove expired indices from front
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }
        
        // Remove smaller elements from back (they can never be the max)
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        
        deque.offerLast(i);
        
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    
    return result;
}
```

**Why store indices instead of values?** Because we need to check if the front element has expired (fallen outside the window). We can't check expiration with just values.

**Complexity:** O(n) time, O(k) space.

**Comparison with heap approach:**

```java
// Heap approach: O(n log k)
public int[] maxSlidingWindowHeap(int[] nums, int k) {
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> b[0] - a[0]);
    int[] result = new int[nums.length - k + 1];
    
    for (int i = 0; i < nums.length; i++) {
        maxHeap.offer(new int[]{nums[i], i});
        // Lazy deletion: remove expired elements from top
        while (maxHeap.peek()[1] < i - k + 1) {
            maxHeap.poll();
        }
        if (i >= k - 1) {
            result[i - k + 1] = maxHeap.peek()[0];
        }
    }
    return result;
}
```

The heap approach is valid and easier to remember. Use the deque approach when the interviewer asks for optimal O(n) time.

---

#### LC 862 — Shortest Subarray with Sum at Least K (Hard)

**Cross-reference:** Topic 5 (Prefix Sum) introduced prefix sums. This problem combines prefix sums with a monotonic deque.

**Problem:** Given an integer array `nums` and integer `k`, return the length of the shortest non-empty subarray with sum at least `k`. Return -1 if no such subarray exists.

**Why this is hard:** `nums` can contain negative numbers, so the sliding window approach from Topic 1 doesn't work (the window isn't monotone). We need a different strategy.

**Key insight:** Build prefix sum array `P` where `P[i] = nums[0] + ... + nums[i-1]`. A subarray `nums[j..i-1]` has sum `P[i] - P[j]`. We want the shortest `i - j` where `P[i] - P[j] >= k`, i.e., `P[j] <= P[i] - k`.

Use a monotonic increasing deque on prefix sums:
- For each `i`, check the front of the deque: if `P[i] - P[deque.front] >= k`, record the length and remove from front (we want the shortest, so once we find a valid `j`, larger `i` values will only give longer subarrays for the same `j`).
- Remove from back while `P[deque.back] >= P[i]` (if a later index has a smaller prefix sum, the earlier one is useless as a left boundary).

```java
public int shortestSubarray(int[] nums, int k) {
    int n = nums.length;
    long[] P = new long[n + 1]; // prefix sums (use long to avoid overflow)
    for (int i = 0; i < n; i++) {
        P[i + 1] = P[i] + nums[i];
    }
    
    int result = Integer.MAX_VALUE;
    Deque<Integer> deque = new ArrayDeque<>(); // stores indices into P, increasing P values
    
    for (int i = 0; i <= n; i++) {
        // Check if front of deque gives a valid subarray
        while (!deque.isEmpty() && P[i] - P[deque.peekFirst()] >= k) {
            result = Math.min(result, i - deque.pollFirst());
        }
        
        // Maintain increasing order: remove back elements with P >= P[i]
        while (!deque.isEmpty() && P[deque.peekLast()] >= P[i]) {
            deque.pollLast();
        }
        
        deque.offerLast(i);
    }
    
    return result == Integer.MAX_VALUE ? -1 : result;
}
```

**Why remove from back when `P[back] >= P[i]`?** If `P[j] >= P[i]` and `j < i`, then for any future index `r`, `P[r] - P[i] >= P[r] - P[j]`. So `i` is always a better left boundary than `j` (same or larger sum, shorter subarray). `j` can be discarded.

**Complexity:** O(n) time, O(n) space.

---

## Common Mistakes and Edge Cases

### Mistake Table

| Mistake | Problem | Fix |
|---------|---------|-----|
| Storing values instead of indices | Can't calculate distances or widths | Always store indices; look up values as `arr[stack.peek()]` |
| Wrong monotonic direction | Getting next greater when you need next smaller | Decreasing stack → next greater. Increasing stack → next smaller. |
| Forgetting elements left in stack | Missing answers for elements with no next greater/smaller | After the main loop, process remaining stack elements (answer = -1 or use sentinel) |
| Off-by-one in histogram width | `width = i - stack.peek()` instead of `i - stack.peek() - 1` | Width formula: `right_exclusive - left_exclusive - 1`. The sentinel at -1 handles the left edge. |
| Using `>` vs `>=` in comparison | Double-counting duplicates in LC 907 | Use strict `>` on one side and `>=` on the other to handle duplicates consistently |
| Not handling empty stack after pop | NullPointerException in LC 42 stack approach | Check `stack.isEmpty()` after each pop before accessing `stack.peek()` |
| Forgetting the circular array trick | LC 503: only processing n elements | Process `2*n` elements with `idx = i % n`; only push during first n iterations |

### Edge Cases

**All increasing array `[1, 2, 3, 4, 5]`:**
- Next greater: all -1 (nothing gets popped until end)
- Largest rectangle: the last bar triggers no pops; the sentinel at index n flushes everything
- Make sure your sentinel handles this

**All decreasing array `[5, 4, 3, 2, 1]`:**
- Next greater: all -1 (nothing gets popped)
- Largest rectangle: each new bar pops all previous bars; the widths are all 1

**All equal array `[3, 3, 3, 3]`:**
- Next greater with strict `<`: all -1 (equal elements don't trigger pops)
- Next greater with `<=`: each element's next greater is the next element
- Be explicit about whether "greater" means strictly greater or greater-or-equal

**Single element `[5]`:**
- Stack starts and ends with one element
- Sentinel at index n handles the flush correctly

**Circular array (LC 503):**
- The `2*n` trick works because any element's next greater must appear within one full cycle
- Don't push during the second pass (indices n to 2n-1)

**Negative numbers (LC 862):**
- Prefix sums can decrease, which is why the sliding window approach fails
- The monotonic deque on prefix sums handles this correctly

---

## Pattern Comparison

### Monotonic Stack vs Brute Force

For "next greater element" on array of size n:

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Brute force (nested loops) | O(n²) | O(1) | For each element, scan right until finding greater |
| Monotonic stack | O(n) | O(n) | Each element pushed/popped at most once |

The brute force is fine for n ≤ 1000. For n ≤ 10^5 or 10^6 (typical FAANG constraints), you need the stack.

### Monotonic Stack vs Two Pointers for LC 42 (Trapping Rain Water)

Both solve the same problem in O(n) time.

```
Two-pointer approach (Topic 2):
- O(1) space
- Intuition: for each column, water level = min(maxLeft, maxRight) - height
- Two pointers converge from both ends
- Simpler code, better space complexity

Stack approach (this document):
- O(n) space
- Intuition: process layer by layer, calculate water in each "valley"
- More complex code
- Shows stack mastery
```

In an interview, if asked for O(1) space, use two pointers. If the interviewer wants to see a stack-based solution, use this approach. Knowing both demonstrates depth.

### Monotonic Deque vs Heap for LC 239 (Sliding Window Maximum)

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Monotonic deque | O(n) | O(k) | Optimal; harder to implement |
| Max-heap with lazy deletion | O(n log k) | O(k) | Easier to implement; acceptable in most interviews |
| Segment tree | O(n log n) | O(n) | Overkill for this problem |
| Brute force | O(nk) | O(1) | Only for very small k |

For k close to n, the deque is significantly faster. For small k, the heap is fine. In a FAANG interview, implement the deque if you can; fall back to the heap if you're under time pressure.

### When to Use Stack vs Deque

Use a **stack** (single-ended) when:
- You only need next/previous greater/smaller (no window expiration)
- Elements are processed once and never need to be removed from the front
- LC 84, LC 739, LC 402, LC 316, LC 907

Use a **deque** (double-ended) when:
- You have a sliding window and elements expire from the front
- You need both front removal (expiration) and back removal (monotonic maintenance)
- LC 239, LC 862

---

## Quick Reference Cheat Sheet

### Memory Aid

```
Next Greater  → Monotonically DECREASING stack (pop when current > top)
Next Smaller  → Monotonically INCREASING stack (pop when current < top)
Previous Greater → Same as Next Greater, but read answer BEFORE pushing
Previous Smaller → Same as Next Smaller, but read answer BEFORE pushing
Sliding Window Max → Monotonically DECREASING deque
Sliding Window Min → Monotonically INCREASING deque
```

### Template Selector

```
"next greater element"          → Template 1 (decreasing stack)
"next smaller element"          → Template 2 (increasing stack)
"previous greater/smaller"      → Template 3 (read peek before push)
"sliding window max/min"        → Template 4 (deque)
"largest rectangle"             → Template 2 + sentinel trick
"remove k digits"               → Template 2 (increasing stack, greedy)
"remove duplicate letters"      → Template 2 + frequency + visited tracking
"sum of subarray minimums"      → Template 2 + left/right boundary counting
```

### Width Calculation Formula (Histogram Problems)

When popping index `j` at position `i` with stack top `left` after pop:

```
width = i - left - 1

where:
  i    = current index (right boundary, exclusive)
  left = stack.peek() after pop (left boundary, exclusive)
         = -1 if stack is empty (use sentinel)
  -1   = because both boundaries are exclusive
```

**Example:** If left=2, popped=5, right=8:
- The rectangle spans indices 3, 4, 5, 6, 7 (between 2 and 8, exclusive)
- Width = 8 - 2 - 1 = 5 ✓

### Complexity Summary

| Problem | Time | Space |
|---------|------|-------|
| Next Greater Element | O(n) | O(n) |
| Daily Temperatures | O(n) | O(n) |
| Largest Rectangle in Histogram | O(n) | O(n) |
| Maximal Rectangle | O(rows × cols) | O(cols) |
| Trapping Rain Water (stack) | O(n) | O(n) |
| Sum of Subarray Minimums | O(n) | O(n) |
| Remove K Digits | O(n) | O(n) |
| Remove Duplicate Letters | O(n) | O(1) |
| Sliding Window Maximum | O(n) | O(k) |
| Shortest Subarray Sum >= K | O(n) | O(n) |

### Common Pitfalls at a Glance

```
1. Store INDICES, not values (except LC 496 where values are unique)
2. Decreasing stack for next GREATER, increasing for next SMALLER
3. Use sentinel (-1 or virtual 0-height bar) to handle edge cases
4. Width = right - left - 1 (both boundaries exclusive)
5. Handle duplicates: strict > on one side, >= on the other
6. Circular arrays: process 2*n, only push during first n
7. Deque for sliding windows, stack for static arrays
```

---

## Practice Roadmap

### Week 1: Foundation (Easy + Medium, 20 min each)

**Goal:** Internalize the "pop and record" pattern. After this week, you should be able to write Template 1 and Template 2 from memory.

| Problem | Difficulty | Focus |
|---------|-----------|-------|
| LC 496 — Next Greater Element I | Easy | Foundational template, HashMap lookup |
| LC 739 — Daily Temperatures | Medium | Distance calculation (`i - prevIdx`) |
| LC 901 — Online Stock Span | Medium | Previous greater, online/streaming variant |
| LC 503 — Next Greater Element II | Medium | Circular array trick (`2*n`, modulo) |

**Week 1 checkpoint:** Can you write the next greater element template without looking? Can you explain why it's O(n)?

### Week 2: Intermediate (Medium, 30 min each)

**Goal:** Apply the pattern to less obvious problems. After this week, you should recognize "monotonic stack" in disguise.

| Problem | Difficulty | Focus |
|---------|-----------|-------|
| LC 402 — Remove K Digits | Medium | Greedy + increasing stack |
| LC 316 — Remove Duplicate Letters | Medium | Stack + frequency + visited |
| LC 907 — Sum of Subarray Minimums | Medium | Left/right boundary counting |
| LC 239 — Sliding Window Maximum | Hard | Monotonic deque, window expiration |

**Week 2 checkpoint:** Can you explain why LC 907 uses strict `>` on one side and `>=` on the other? Can you implement the deque solution for LC 239?

### Week 3: Hard Problems (40 min each)

**Goal:** Master the hardest problems. After this week, LC 84 should feel routine.

| Problem | Difficulty | Focus |
|---------|-----------|-------|
| LC 84 — Largest Rectangle in Histogram | Hard | THE classic problem, sentinel trick, width formula |
| LC 85 — Maximal Rectangle | Hard | Reduction to LC 84, row-by-row histogram |
| LC 42 — Trapping Rain Water (stack) | Hard | Valley detection, contrast with two-pointer |
| LC 862 — Shortest Subarray Sum >= K | Hard | Prefix sum + monotonic deque |

**Week 3 checkpoint:** Can you solve LC 84 in under 15 minutes? Can you explain the sentinel trick and the width formula without hesitation?

### Total: ~12 problems over 3 weeks

**After completing the roadmap:**
- Revisit LC 84 and LC 239 without looking at notes
- Practice explaining the O(n) amortized argument out loud
- Try LC 1019 (Next Greater Node in Linked List) as a bonus problem

### Interview Preparation Notes

**For LC 84 specifically:** This problem comes up at Google and Amazon more than almost any other hard problem. Practice the dry run until you can trace through a 6-element array on a whiteboard in under 5 minutes. The sentinel trick and the width formula are the two things interviewers check.

**For LC 239:** Bloomberg and Goldman Sachs ask this frequently. Know both the deque approach (O(n)) and the heap approach (O(n log k)). Start with the heap if you're nervous; it's easier to implement correctly under pressure.

**For LC 42:** This problem has three valid approaches (two-pointer, dynamic programming, stack). Knowing all three and being able to compare them is impressive. The stack approach is the hardest to implement but shows the deepest understanding.

**The pattern recognition test:** In an interview, if you see "next greater," "next smaller," "previous greater," "previous smaller," or "sliding window max/min," say "this looks like a monotonic stack/deque problem" within the first 30 seconds. Then walk through which variant applies. This immediately signals pattern recognition to the interviewer.

---

## Amortized Analysis: Why the Inner While Loop is O(n)

This comes up in interviews. Interviewers sometimes challenge the O(n) claim because of the nested loop structure. Here's the rigorous argument.

### The Accounting Method

Assign each element a "credit" of 2 operations when it's pushed:
- 1 credit for the push itself
- 1 credit saved for the eventual pop

Every element is pushed exactly once: n pushes total.
Every element is popped at most once: at most n pops total.

Total operations = pushes + pops ≤ n + n = 2n = O(n).

The inner `while` loop can run many times for a single `i`, but across ALL iterations of the outer loop, the total number of pops is bounded by n. This is the amortized argument.

### Concrete Example

```
Array: [5, 4, 3, 2, 1, 6]

i=0: push 0. 1 push.
i=1: push 1. 1 push.
i=2: push 2. 1 push.
i=3: push 3. 1 push.
i=4: push 4. 1 push.
i=5: pop 4, pop 3, pop 2, pop 1, pop 0. 5 pops. push 5. 1 push.

Total pushes: 6 (one per element)
Total pops: 5 (at most one per element)
Total operations: 11 = O(n)
```

The worst case for the inner loop is when the last element is the largest (triggers n-1 pops). But even then, total operations = 2n - 1 = O(n).

### Why This Matters in Interviews

When you write the template and the interviewer asks "isn't this O(n²)?", say:

"The inner while loop looks quadratic, but each element is pushed exactly once and popped at most once. Over the entire traversal, total push + pop operations = O(2n) = O(n). This is the same amortized analysis as dynamic array resizing."

That answer, delivered confidently, is worth more than fumbling through the code.

---

## LC 962 — Maximum Width Ramp (Medium)

**Companies:** Amazon, Google

**Problem:** Given an integer array `nums`, a ramp is a pair `(i, j)` where `i < j` and `nums[i] <= nums[j]`. The width of the ramp is `j - i`. Return the maximum width of a ramp, or 0 if no ramp exists.

**Why it's interesting:** This problem uses a two-phase monotonic stack approach. It's not the standard "next greater" template, but it uses the same underlying idea.

**Phase 1:** Build a decreasing stack of candidate left endpoints. We only care about indices where the value is strictly decreasing (if `nums[i] >= nums[j]` for `i < j`, then `j` is always a better left endpoint than `i` for any right endpoint).

**Phase 2:** Scan from right to left. For each right endpoint `j`, pop from the stack while `nums[stack.peek()] <= nums[j]`. The popped index is a valid left endpoint, and `j - stack.peek()` is the width.

```java
public int maxWidthRamp(int[] nums) {
    int n = nums.length;
    Deque<Integer> stack = new ArrayDeque<>();
    
    // Phase 1: Build decreasing stack of candidate left endpoints
    for (int i = 0; i < n; i++) {
        if (stack.isEmpty() || nums[stack.peek()] > nums[i]) {
            stack.push(i);
        }
    }
    
    // Phase 2: Scan right to left, find maximum width ramp
    int maxWidth = 0;
    for (int j = n - 1; j >= 0; j--) {
        while (!stack.isEmpty() && nums[stack.peek()] <= nums[j]) {
            maxWidth = Math.max(maxWidth, j - stack.peek());
            stack.pop();
        }
    }
    
    return maxWidth;
}
```

**Trace on `[6, 0, 8, 2, 1, 5]`:**

```
Phase 1 (build decreasing stack):
i=0, nums[0]=6: stack empty, push 0. stack=[0]
i=1, nums[1]=0: nums[0]=6 > 0, push 1. stack=[0,1]
i=2, nums[2]=8: nums[1]=0 <= 8, don't push (0 is better left endpoint than 2 for any j)
i=3, nums[3]=2: nums[1]=0 <= 2, don't push
i=4, nums[4]=1: nums[1]=0 <= 1, don't push
i=5, nums[5]=5: nums[1]=0 <= 5, don't push

Stack after phase 1: [0, 1] (indices with values [6, 0])

Phase 2 (scan right to left):
j=5, nums[5]=5: nums[1]=0 <= 5. pop 1. width=5-1=4. maxWidth=4.
                nums[0]=6 > 5. stop.
j=4, nums[4]=1: nums[0]=6 > 1. stop.
j=3, nums[3]=2: nums[0]=6 > 2. stop.
j=2, nums[2]=8: nums[0]=6 <= 8. pop 0. width=2-0=2. maxWidth=max(4,2)=4.
                stack empty. stop.
j=1, j=0: stack empty.

Final: 4
```

**Why scan right to left in phase 2?** We want the maximum width. For each left endpoint in the stack, we want the rightmost valid right endpoint. Scanning right to left ensures we find the rightmost `j` first.

**Complexity:** O(n) time, O(n) space.

---

## Interview Follow-Up Questions

These are questions interviewers commonly ask after you solve the main problem. Prepare answers for all of them.

### After LC 739 (Daily Temperatures)

**Q: Can you solve this in O(1) space?**
A: No. We need to store indices of "waiting" elements. The stack is necessary. The O(n) space is optimal for this problem.

**Q: What if temperatures can repeat?**
A: The template handles duplicates correctly. We use strict `<` (not `<=`), so equal temperatures don't trigger a pop. An element waits until a strictly warmer day.

**Q: What if the array is sorted in decreasing order?**
A: Every element gets pushed and never popped (no warmer day exists). The stack grows to size n. All answers are 0. The algorithm still runs in O(n).

### After LC 84 (Largest Rectangle in Histogram)

**Q: What if all bars have the same height?**
A: The sentinel at index n (height 0) flushes all bars. The last bar popped has `left = -1` (sentinel), so `width = n - (-1) - 1 = n`. Area = height × n, which is correct.

**Q: Can you solve this without a stack?**
A: Yes, with a divide-and-conquer approach: find the minimum bar, compute the rectangle spanning the full range, then recurse on left and right halves. This is O(n log n) average, O(n²) worst case. The stack approach is strictly better.

**Q: What if bar widths are not all 1?**
A: Store (height, width) pairs instead of just indices. When calculating area, use the accumulated width instead of `i - left - 1`. This is a common follow-up at Google.

**Q: How does this extend to LC 85 (Maximal Rectangle)?**
A: Build a histogram for each row of the matrix. For row `r`, `heights[c]` = number of consecutive 1s ending at row `r` in column `c`. Apply LC 84 on each row's histogram. Total time: O(rows × cols).

### After LC 239 (Sliding Window Maximum)

**Q: What if k = 1?**
A: Every element is its own window maximum. The deque always has exactly one element. Result = the original array.

**Q: What if k = n?**
A: One window covering the entire array. The deque processes all elements and the front is the global maximum.

**Q: Can you find both the maximum AND minimum of each window simultaneously?**
A: Yes. Maintain two deques: one monotonically decreasing (for max) and one monotonically increasing (for min). Process both in the same loop. O(n) time, O(k) space.

**Q: What about the median of each window?**
A: The deque approach doesn't extend to medians. Use two heaps (max-heap for lower half, min-heap for upper half) with lazy deletion. O(n log k) time.

### After LC 402 (Remove K Digits)

**Q: What if you want the largest number instead of smallest?**
A: Use a monotonically decreasing stack instead of increasing. Pop when the current digit is larger than the top. Remove from the end if k > 0 after the loop.

**Q: What if k = 0?**
A: Return the original number unchanged. The while loop never executes.

**Q: What if k >= num.length()?**
A: Return "0". You'd remove all digits.

### After LC 316 (Remove Duplicate Letters)

**Q: What if characters can repeat more than twice?**
A: The frequency count handles any number of repetitions. The algorithm is correct regardless of how many times each character appears.

**Q: Why is the result unique?**
A: The greedy choice at each step (pop larger characters if they appear later) is provably optimal. Any deviation from this greedy choice produces a lexicographically larger result.

---

## Variations and Extensions

### Variation 1: Next Greater Element with Distance Constraint

**Problem:** Find the next greater element within a distance of `d` (i.e., only look at the next `d` elements).

**Approach:** Same decreasing stack, but when popping, only record the answer if `i - poppedIdx <= d`. Elements that are too far away don't count.

```java
public int[] nextGreaterWithDistance(int[] arr, int d) {
    int n = arr.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>();
    
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && arr[stack.peek()] < arr[i]) {
            int poppedIdx = stack.peek();
            if (i - poppedIdx <= d) {
                result[stack.pop()] = arr[i];
            } else {
                break; // stack is ordered, so all remaining are even farther
            }
        }
        stack.push(i);
    }
    return result;
}
```

### Variation 2: Count of Subarrays with Max Equal to Element

**Problem:** For each element, count how many subarrays have that element as their maximum.

**Approach:** Same as LC 907 (Sum of Subarray Minimums) but with a decreasing stack. For each element at index `i`:
- `left[i]` = distance to previous greater element (strictly)
- `right[i]` = distance to next greater or equal element

Count = `left[i] * right[i]`.

This is the building block for problems like "sum of subarray maximums."

### Variation 3: Monotonic Stack on 2D Grid

**Problem:** Given a 2D grid, for each cell find the nearest cell in the same row with a greater value.

**Approach:** Apply the next greater element template independently on each row. O(rows × cols) total.

This is the same reduction used in LC 85 (Maximal Rectangle), just applied differently.

### Variation 4: Online Next Greater Element

**Problem:** Elements arrive one at a time (streaming). For each new element, report all previous elements for which this is the next greater.

**Approach:** This is exactly LC 901 (Online Stock Span) generalized. Maintain a stack. When a new element arrives, pop all smaller elements and report them. The stack persists across calls.

```java
class OnlineNextGreater {
    private Deque<int[]> stack; // [value, original_index]
    private int currentIndex;
    
    public OnlineNextGreater() {
        stack = new ArrayDeque<>();
        currentIndex = 0;
    }
    
    // Returns list of [original_index, next_greater_value] pairs
    public List<int[]> addElement(int val) {
        List<int[]> answered = new ArrayList<>();
        while (!stack.isEmpty() && stack.peek()[0] < val) {
            int[] popped = stack.pop();
            answered.add(new int[]{popped[1], val});
        }
        stack.push(new int[]{val, currentIndex++});
        return answered;
    }
}
```

---

## Debugging Monotonic Stack Code

When your solution gives wrong answers, check these in order:

### Step 1: Verify the monotonic direction

Print the stack contents after each push. For a decreasing stack, values should decrease from bottom to top. If you see an increase, your comparison is wrong.

```java
// Debug helper
private void printStack(Deque<Integer> stack, int[] arr) {
    List<Integer> vals = new ArrayList<>();
    for (int idx : stack) vals.add(arr[idx]);
    System.out.println("Stack (top to bottom): " + vals);
}
```

### Step 2: Check the width formula

For histogram problems, print `height`, `left`, `right`, and `width` for each pop:

```java
while (stack.peek() != -1 && heights[stack.peek()] >= currHeight) {
    int height = heights[stack.pop()];
    int left = stack.peek();
    int width = i - left - 1;
    System.out.printf("Pop: height=%d, left=%d, right=%d, width=%d, area=%d%n",
                      height, left, i, width, height * width);
    maxArea = Math.max(maxArea, height * width);
}
```

### Step 3: Check sentinel handling

For LC 84, verify that the sentinel at index -1 is in the stack before the loop starts, and that the virtual bar at index n (height 0) is processed.

### Step 4: Check duplicate handling

For problems where duplicates matter (LC 907, LC 316), trace through an array with repeated values. Verify that the strict vs non-strict comparison is correct.

### Step 5: Check the circular array trick

For LC 503, verify that you're only pushing during the first n iterations (`if (i < n) stack.push(idx)`). If you push during the second pass, you'll get wrong answers.

---

## Java-Specific Implementation Notes

### ArrayDeque vs LinkedList

Always use `ArrayDeque` for stack and deque operations in Java. It's faster than `LinkedList` due to better cache locality and no node allocation overhead.

```java
// Preferred
Deque<Integer> stack = new ArrayDeque<>();

// Avoid for performance-critical code
Deque<Integer> stack = new LinkedList<>();
```

### Stack class in Java

Don't use `java.util.Stack`. It extends `Vector`, which is synchronized and slower. Use `ArrayDeque` with `push`/`pop`/`peek` methods instead.

```java
// Wrong (legacy, synchronized, slow)
Stack<Integer> stack = new Stack<>();

// Correct
Deque<Integer> stack = new ArrayDeque<>();
stack.push(x);    // pushes to front (top)
stack.pop();      // removes from front (top)
stack.peek();     // reads front (top) without removing
```

### Deque method naming

Java's `Deque` interface has two sets of methods: one that throws exceptions and one that returns null/false. For competitive programming, use the exception-throwing versions (they fail fast on bugs):

```java
// Stack operations (front = top)
stack.push(x);      // addFirst(x)
stack.pop();        // removeFirst()
stack.peek();       // peekFirst()

// Deque operations (explicit front/back)
deque.offerFirst(x);  // add to front
deque.offerLast(x);   // add to back
deque.pollFirst();    // remove from front
deque.pollLast();     // remove from back
deque.peekFirst();    // read front
deque.peekLast();     // read back
```

### Iterating a Deque

`ArrayDeque` iterates from front to back (top to bottom for a stack). This matters when building the result string in LC 402 and LC 316.

```java
// Iterates front to back
for (char c : deque) {
    sb.append(c);
}

// To iterate back to front, convert to array first
Object[] arr = deque.toArray();
for (int i = arr.length - 1; i >= 0; i--) {
    // process arr[i]
}
```

### Integer overflow in LC 907

The sum of subarray minimums can overflow `int`. Use `long` for the accumulator and cast carefully:

```java
long result = 0;
// ...
result = (result + (long) arr[j] * left * right) % MOD;
// Cast arr[j] to long BEFORE multiplying to prevent overflow
```

---

## Connecting the Dots: How Problems Build on Each Other

Understanding the dependency graph helps you study in the right order.

```
LC 496 (Next Greater I)
    └── LC 503 (Next Greater II — circular variant)
    └── LC 739 (Daily Temperatures — distance variant)
        └── LC 901 (Stock Span — previous greater, online)
        └── LC 1019 (Next Greater in Linked List)

LC 739 (Daily Temperatures)
    └── LC 84 (Largest Rectangle — uses next/prev smaller)
        └── LC 85 (Maximal Rectangle — reduces to LC 84)
        └── LC 42 (Trapping Rain Water — valley detection)
        └── LC 907 (Sum of Subarray Mins — boundary counting)

LC 239 (Sliding Window Maximum — deque)
    └── LC 862 (Shortest Subarray Sum >= K — deque + prefix sum)
    └── LC 1438 (Longest Subarray with Abs Diff <= Limit — two deques)

LC 402 (Remove K Digits)
    └── LC 316 (Remove Duplicate Letters — adds frequency tracking)
    └── LC 321 (Create Maximum Number — combines two stacks)
```

**Study order:** LC 496 → LC 739 → LC 901 → LC 503 → LC 84 → LC 85 → LC 42 → LC 907 → LC 402 → LC 316 → LC 239 → LC 862

Each problem in this sequence introduces exactly one new concept on top of the previous one. Don't skip ahead.

---

## Summary: The Five Core Insights

If you remember nothing else from this document, remember these five things:

**1. Every element is pushed once and popped at most once.**
This is why the algorithm is O(n) despite the nested loop. Say this in every interview.

**2. Decreasing stack for next greater. Increasing stack for next smaller.**
The direction of the stack determines what you find. Flip the comparison, flip the answer.

**3. Store indices, not values.**
You need indices to calculate distances (LC 739), widths (LC 84), and window expiration (LC 239).

**4. When you pop, you get three things simultaneously.**
The popped element, its right boundary (current element), and its left boundary (new stack top). LC 84 and LC 907 both exploit all three.

**5. The sentinel trick eliminates edge cases.**
Push -1 before the loop (left sentinel) and process a virtual element at the end (right sentinel). This handles "extends to the edge" cases without special-casing.

Master these five insights and you can reconstruct any solution in this document from scratch.

---

*Document 7 of 20 — FAANG DSA Prep Series*

*Previous: Topic 6 (HashMap and Frequency Counting)*
*Next: Topic 8 (Trees — BFS/DFS)*
