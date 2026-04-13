# Topic 12: Heaps & Priority Queues

**Complexity at a glance:** O(log n) insert/remove, O(1) peek at min/max.

In Java, `PriorityQueue` is a **min-heap by default**. That single fact trips up more candidates than any algorithm detail. Heaps solve a specific class of problems elegantly: anything where you repeatedly need the smallest or largest element from a changing dataset. Sorting gives you everything sorted once. A heap gives you the extreme element, always, as the data changes.

**This pattern solves:**
- Top-K problems (K largest, K most frequent, K closest)
- K-way merge (merge K sorted lists/arrays)
- Streaming median (two-heap trick)
- Scheduling and greedy problems (task scheduler, meeting rooms)
- Any problem needing repeated access to the extreme element

**Top companies:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5 Intuition](#eli5-intuition)
3. [Java PriorityQueue API Reference](#java-priorityqueue-api-reference)
4. [The Four Patterns](#the-four-patterns)
5. [Templates](#templates)
6. [Real-World Applications](#real-world-applications)
7. [Problems by Category](#problems-by-category)
   - [Category A: Top-K Elements](#category-a-top-k-elements)
   - [Category B: K-Way Merge](#category-b-k-way-merge)
   - [Category C: Two Heaps / Median](#category-c-two-heaps--median)
   - [Category D: Scheduling / Greedy](#category-d-scheduling--greedy)
   - [Category E: Design](#category-e-design)
8. [Common Mistakes](#common-mistakes)
9. [Heap vs Other Data Structures](#heap-vs-other-data-structures)
10. [Cheat Sheet](#cheat-sheet)
11. [Study Roadmap](#study-roadmap)

---

## Core Concept

### What is a Heap?

A heap is a complete binary tree with one invariant: every parent is smaller than its children (min-heap) or larger (max-heap). This invariant is maintained through "heapify up" on insert and "heapify down" on remove.

The tree is stored as an array. For element at index `i`:
- Left child: `2*i + 1`
- Right child: `2*i + 2`
- Parent: `(i - 1) / 2`

```
Min-Heap example:
        1
       / \
      3   2
     / \ / \
    7  4 5  6

Array: [1, 3, 2, 7, 4, 5, 6]
```

The root is always the minimum (min-heap) or maximum (max-heap). You can read it in O(1). Inserting or removing costs O(log n) because you bubble up or down at most the height of the tree.

### Min-Heap vs Max-Heap in Java

**Java's `PriorityQueue` is a MIN-HEAP by default.** `poll()` returns the smallest element.

```java
// Min-heap (default) — poll() returns smallest
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// Max-heap option 1 — using Collections.reverseOrder()
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

// Max-heap option 2 — using lambda comparator
// WARNING: (a, b) -> b - a can OVERFLOW for large integers!
// If b = Integer.MAX_VALUE and a = Integer.MIN_VALUE, b - a overflows.
PriorityQueue<Integer> maxHeapUnsafe = new PriorityQueue<>((a, b) -> b - a); // AVOID

// Max-heap option 3 — SAFE version
PriorityQueue<Integer> maxHeapSafe = new PriorityQueue<>((a, b) -> Integer.compare(b, a));
```

**The overflow trap:** `(a, b) -> b - a` is a common shorthand but it's wrong when values can be large. `Integer.compare(b, a)` is always safe. Use it.

### Key Operations

| Operation | Method | Complexity | Notes |
|-----------|--------|------------|-------|
| Insert | `offer(e)` or `add(e)` | O(log n) | `offer` preferred (returns false on failure vs exception) |
| Remove min/max | `poll()` | O(log n) | Returns null if empty |
| Peek min/max | `peek()` | O(1) | Returns null if empty |
| Size | `size()` | O(1) | |
| Contains | `contains(e)` | O(n) | Linear scan! Not a heap strength |
| Remove specific | `remove(e)` | O(n) | Linear scan to find, then O(log n) to fix |
| Build from collection | `new PriorityQueue<>(collection)` | O(n) | Heapify is O(n), not O(n log n) |

**Critical:** `contains()` and `remove(specific element)` are O(n). If you need fast arbitrary removal, use a `TreeMap` or `TreeSet` instead.

---

## ELI5 Intuition

### The VIP Club Line

Imagine a nightclub with a VIP line. Normal lines are first-come-first-served. The VIP line works differently: the most important person always gets in next, regardless of when they arrived.

The bouncer (the heap) doesn't sort the entire line. He just always knows who's most important right now. When someone new joins, he quickly figures out where they rank. When the most important person leaves, he quickly figures out who's next.

That's a heap. O(1) to see who's most important. O(log n) to add someone or remove the most important person.

### Why Min-Heap for Top-K Largest? (The Counterintuitive Part)

This trips everyone up. You want the K largest elements. Instinct says: use a max-heap. Wrong.

Think of it this way: you're keeping a "hall of fame" of the K best scores. You need to know when to kick someone out. You kick out the worst score in your hall of fame when a better score arrives. So you need fast access to the worst score in your current top-K. That's the minimum of your top-K set.

**Min-heap of size K for top-K largest:**
- The top of the heap is the smallest of your current K candidates
- When a new element arrives: if it's larger than the top, pop the top and push the new element
- At the end, the heap contains exactly the K largest elements

### Two Heaps for Streaming Median

Split all numbers into two groups: the lower half and the upper half.

- Lower half: stored in a max-heap (you want the largest of the lower half at the top)
- Upper half: stored in a min-heap (you want the smallest of the upper half at the top)

Keep them balanced (sizes differ by at most 1). The median is:
- If equal sizes: average of both tops
- If one is larger: the top of the larger heap

```
Numbers seen so far: [1, 3, 5, 7, 9]

Lower half (max-heap): [1, 3, 5]  -> top = 5
Upper half (min-heap): [7, 9]     -> top = 7

Median = 5 (odd count, lower half has one more)
```

---

## Java PriorityQueue API Reference

```java
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.Comparator;

// Construction
PriorityQueue<Integer> pq = new PriorityQueue<>();                          // min-heap, default capacity 11
PriorityQueue<Integer> pq = new PriorityQueue<>(initialCapacity);          // min-heap, custom capacity
PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder()); // max-heap
PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> Integer.compare(b, a)); // max-heap, safe
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);     // sort by first element (safe if values small)
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0])); // sort by first element, safe

// From existing collection — O(n) heapify
PriorityQueue<Integer> pq = new PriorityQueue<>(Arrays.asList(3, 1, 4, 1, 5));

// Core operations
pq.offer(element);    // insert, O(log n), returns true/false
pq.add(element);      // insert, O(log n), throws exception on failure
pq.poll();            // remove and return min, O(log n), returns null if empty
pq.remove();          // remove and return min, O(log n), throws NoSuchElementException if empty
pq.peek();            // view min without removing, O(1), returns null if empty
pq.element();         // view min without removing, O(1), throws NoSuchElementException if empty

// Utility
pq.size();            // O(1)
pq.isEmpty();         // O(1)
pq.contains(e);       // O(n) — linear scan
pq.remove(e);         // O(n) to find + O(log n) to fix
pq.clear();           // O(n)
pq.toArray();         // O(n), NOT sorted
pq.iterator();        // does NOT iterate in sorted order

// Custom objects
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
    if (a[0] != b[0]) return Integer.compare(a[0], b[0]); // sort by first element
    return Integer.compare(a[1], b[1]);                    // then by second
});

// With Comparable
class Task implements Comparable<Task> {
    int priority;
    String name;
    
    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority); // min-heap by priority
    }
}
PriorityQueue<Task> taskQueue = new PriorityQueue<>();
```

**Iteration note:** `for (int x : pq)` does NOT iterate in heap order. If you need sorted output, repeatedly `poll()`.

---

## The Four Patterns

### Pattern 1: Top-K Elements

**When to use:** "Find K largest/smallest/most frequent/closest elements."

**Key insight:** Use a min-heap of size K for top-K largest. Use a max-heap of size K for top-K smallest.

**Why min-heap for top-K largest?**
You maintain a window of the K best candidates. The heap's top is the weakest candidate in your window. When a new element arrives, compare it to the weakest. If it's better, evict the weakest and add the new one.

```
Finding top-3 largest from [3, 1, 4, 1, 5, 9, 2, 6]:

Min-heap (size 3):
After 3:     [3]
After 1:     [1, 3]
After 4:     [1, 3, 4]
After 1:     1 <= peek(1), skip. Heap: [1, 3, 4]
After 5:     5 > peek(1), poll 1, offer 5. Heap: [3, 4, 5]
After 9:     9 > peek(3), poll 3, offer 9. Heap: [4, 5, 9]
After 2:     2 <= peek(4), skip. Heap: [4, 5, 9]
After 6:     6 > peek(4), poll 4, offer 6. Heap: [5, 6, 9]

Result: {5, 6, 9} — the 3 largest.
```

**Time:** O(n log k). **Space:** O(k).

### Pattern 2: K-Way Merge

**When to use:** "Merge K sorted lists/arrays." "Find Kth smallest across K sorted arrays."

**Key insight:** Put the first element of each list into a min-heap. Always poll the smallest, then add the next element from that same list.

```
Merge 3 sorted lists:
List 0: [1, 4, 7]
List 1: [2, 5, 8]
List 2: [3, 6, 9]

Heap stores: (value, listIndex, elementIndex)

Initial heap: [(1,0,0), (2,1,0), (3,2,0)]

Poll (1,0,0) -> output 1, add (4,0,1). Heap: [(2,1,0), (3,2,0), (4,0,1)]
Poll (2,1,0) -> output 2, add (5,1,1). Heap: [(3,2,0), (4,0,1), (5,1,1)]
Poll (3,2,0) -> output 3, add (6,2,1). Heap: [(4,0,1), (5,1,1), (6,2,1)]
...and so on.
```

**Time:** O(n log k) where n is total elements. **Space:** O(k).

### Pattern 3: Two Heaps (Streaming Median)

**When to use:** "Find median of a stream." "Sliding window median."

**Key insight:** Two heaps partition the data. Max-heap holds the lower half, min-heap holds the upper half. Keep them balanced.

**Invariant:** `maxHeap.size() == minHeap.size()` or `maxHeap.size() == minHeap.size() + 1`.

**Add element algorithm:**
1. If new element <= maxHeap.peek(), add to maxHeap. Else add to minHeap.
2. Rebalance: if sizes differ by more than 1, move top of larger heap to smaller heap.

**Get median:**
- Equal sizes: `(maxHeap.peek() + minHeap.peek()) / 2.0`
- maxHeap larger: `maxHeap.peek()`

### Pattern 4: Scheduling / Greedy with Heap

**When to use:** "Schedule tasks to minimize time." "Assign tasks by frequency." "Process jobs by deadline."

**Key insight:** Use a max-heap ordered by frequency/priority. Greedily pick the highest-priority task that's available.

For task scheduler (LC 621): always pick the most frequent remaining task. Use a max-heap of frequencies. After picking k tasks (one "round"), re-add them.

---

## Templates

### Template 1: Top-K Elements

```java
// Top-K Largest Elements
// Uses min-heap of size k — smallest of the k largest sits at top
public int[] topKLargest(int[] nums, int k) {
    // Min-heap: poll() removes the smallest
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    
    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll(); // remove the smallest — it's not in top-k
        }
    }
    
    // Heap now contains exactly the k largest elements
    int[] result = new int[k];
    for (int i = k - 1; i >= 0; i--) {
        result[i] = minHeap.poll();
    }
    return result;
}

// Top-K Smallest Elements
// Uses max-heap of size k — largest of the k smallest sits at top
public int[] topKSmallest(int[] nums, int k) {
    // Max-heap: poll() removes the largest
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    
    for (int num : nums) {
        maxHeap.offer(num);
        if (maxHeap.size() > k) {
            maxHeap.poll(); // remove the largest — it's not in bottom-k
        }
    }
    
    int[] result = new int[k];
    for (int i = k - 1; i >= 0; i--) {
        result[i] = maxHeap.poll();
    }
    return result;
}

// Kth Largest Element (single element)
public int findKthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll();
        }
    }
    return minHeap.peek(); // top of min-heap is the kth largest
}
```

### Template 2: K-Way Merge

```java
// Merge K Sorted Arrays
// Each heap entry: int[] {value, arrayIndex, elementIndex}
public int[] mergeKSortedArrays(int[][] arrays) {
    // Min-heap ordered by value
    PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
    
    int totalSize = 0;
    
    // Add first element of each array
    for (int i = 0; i < arrays.length; i++) {
        if (arrays[i].length > 0) {
            minHeap.offer(new int[]{arrays[i][0], i, 0});
            totalSize += arrays[i].length;
        }
    }
    
    int[] result = new int[totalSize];
    int idx = 0;
    
    while (!minHeap.isEmpty()) {
        int[] curr = minHeap.poll();
        int val = curr[0], arrIdx = curr[1], elemIdx = curr[2];
        
        result[idx++] = val;
        
        // Add next element from the same array
        if (elemIdx + 1 < arrays[arrIdx].length) {
            minHeap.offer(new int[]{arrays[arrIdx][elemIdx + 1], arrIdx, elemIdx + 1});
        }
    }
    
    return result;
}

// Find Kth Smallest Across K Sorted Arrays
public int kthSmallest(int[][] arrays, int k) {
    PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
    
    for (int i = 0; i < arrays.length; i++) {
        if (arrays[i].length > 0) {
            minHeap.offer(new int[]{arrays[i][0], i, 0});
        }
    }
    
    int count = 0;
    while (!minHeap.isEmpty()) {
        int[] curr = minHeap.poll();
        count++;
        
        if (count == k) return curr[0];
        
        int arrIdx = curr[1], elemIdx = curr[2];
        if (elemIdx + 1 < arrays[arrIdx].length) {
            minHeap.offer(new int[]{arrays[arrIdx][elemIdx + 1], arrIdx, elemIdx + 1});
        }
    }
    
    return -1; // k out of range
}
```

### Template 3: Two Heaps for Streaming Median

```java
class MedianFinder {
    // maxHeap holds the lower half — top is the largest of the lower half
    private PriorityQueue<Integer> maxHeap;
    // minHeap holds the upper half — top is the smallest of the upper half
    private PriorityQueue<Integer> minHeap;
    
    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder()); // max-heap
        minHeap = new PriorityQueue<>();                           // min-heap
    }
    
    public void addNum(int num) {
        // Step 1: Route to correct heap
        if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
            maxHeap.offer(num);
        } else {
            minHeap.offer(num);
        }
        
        // Step 2: Rebalance — maxHeap can have at most 1 more element than minHeap
        if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }
    
    public double findMedian() {
        if (maxHeap.size() == minHeap.size()) {
            return (maxHeap.peek() + (double) minHeap.peek()) / 2.0;
        }
        return maxHeap.peek(); // maxHeap always has the extra element
    }
}
```

### Template 4: Greedy Scheduling with Heap

```java
// Task Scheduler pattern
// Given tasks with frequencies, find minimum time to complete all tasks
// with cooldown n between same tasks
public int leastInterval(char[] tasks, int n) {
    // Count frequencies
    int[] freq = new int[26];
    for (char task : tasks) {
        freq[task - 'A']++;
    }
    
    // Max-heap of frequencies — always pick most frequent task
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    for (int f : freq) {
        if (f > 0) maxHeap.offer(f);
    }
    
    int time = 0;
    
    while (!maxHeap.isEmpty()) {
        // Try to fill one "cycle" of n+1 slots
        List<Integer> temp = new ArrayList<>();
        int slots = n + 1;
        
        while (slots > 0 && !maxHeap.isEmpty()) {
            int f = maxHeap.poll();
            f--;
            if (f > 0) temp.add(f);
            slots--;
            time++;
        }
        
        // Re-add remaining tasks
        maxHeap.addAll(temp);
        
        // If heap not empty, we had idle time to fill the cycle
        if (!maxHeap.isEmpty()) {
            time += slots; // idle slots
        }
    }
    
    return time;
}

// Meeting Rooms II pattern
// Given intervals, find minimum number of rooms needed
public int minMeetingRooms(int[][] intervals) {
    if (intervals.length == 0) return 0;
    
    // Sort by start time
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
    
    // Min-heap of end times — top is the earliest ending meeting
    PriorityQueue<Integer> endTimes = new PriorityQueue<>();
    
    for (int[] interval : intervals) {
        // If earliest ending meeting ends before this one starts, reuse that room
        if (!endTimes.isEmpty() && endTimes.peek() <= interval[0]) {
            endTimes.poll(); // free up the room
        }
        endTimes.offer(interval[1]); // assign room, track end time
    }
    
    return endTimes.size(); // number of rooms in use
}
```

---

## Real-World Applications

**OS Process Scheduling:** The Linux kernel uses a priority queue (red-black tree in practice) to schedule processes. Each process has a priority; the scheduler always runs the highest-priority runnable process. This is exactly the heap pattern.

**Dijkstra's Shortest Path:** Topic 16 covers this in depth. The core of Dijkstra is a min-heap of (distance, node) pairs. Always process the closest unvisited node. Without a heap, Dijkstra is O(V^2). With a heap, it's O((V + E) log V).

**Event-Driven Simulation:** Simulating a bank, hospital, or network. Events have timestamps. A min-heap ordered by timestamp ensures you always process the next event in chronological order.

**Database Query Optimization:** When a query has `ORDER BY col LIMIT k`, a smart database engine doesn't sort all rows. It maintains a heap of size k, scanning once. This is O(n log k) instead of O(n log n).

**Load Balancing:** Assign incoming requests to the server with the fewest active connections. A min-heap of (activeConnections, serverId) lets you find the least-loaded server in O(log n).

**Huffman Coding:** Build an optimal prefix-free code for compression. Repeatedly merge the two lowest-frequency symbols. A min-heap makes each merge O(log n). Total: O(n log n) to build the Huffman tree.

**Stream Processing:** In real-time analytics (Kafka, Flink), you often need the top-K items from a stream without storing everything. A heap of size K does this with O(log k) per element.

---

## Problems by Category

---

## Category A: Top-K Elements

### LC 215 — Kth Largest Element in an Array

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

**Problem:** Given an integer array `nums` and an integer `k`, return the kth largest element in the array. Note: kth largest means kth largest in sorted order, not kth distinct.

**Example:**
```
Input: nums = [3,2,1,5,6,4], k = 2
Output: 5

Input: nums = [3,2,3,1,2,4,5,5,6], k = 4
Output: 4
```

**Approach 1: Min-Heap of Size K**

Maintain a min-heap of size k. The top of the heap is always the kth largest seen so far.

```java
public int findKthLargest(int[] nums, int k) {
    // Min-heap: top is the smallest of our k candidates
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    
    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll(); // evict the smallest — not in top-k
        }
    }
    
    // Top of heap is the kth largest
    return minHeap.peek();
}
```

**Dry Run — nums = [3,2,1,5,6,4], k = 2:**

```
Process 3: heap = [3]
Process 2: heap = [2, 3]
Process 1: heap = [1, 3, 2] -> size > 2, poll 1 -> heap = [2, 3]
Process 5: heap = [2, 3, 5] -> size > 2, poll 2 -> heap = [3, 5]
Process 6: heap = [3, 5, 6] -> size > 2, poll 3 -> heap = [5, 6]
Process 4: heap = [4, 6, 5] -> size > 2, poll 4 -> heap = [5, 6]

peek() = 5. Answer: 5. Correct.
```

**Time:** O(n log k). **Space:** O(k).

**Approach 2: QuickSelect**

QuickSelect is a partition-based algorithm. Like QuickSort, but we only recurse into one partition. Average O(n), worst case O(n^2).

**Key insight:** After partitioning around a pivot, the pivot is at its final sorted position. If pivot is at index `n - k` (0-indexed from left), we found our answer. If pivot index is too small, recurse right. If too large, recurse left.

```java
public int findKthLargest(int[] nums, int k) {
    // We want the kth largest = element at index (n - k) in sorted order
    return quickSelect(nums, 0, nums.length - 1, nums.length - k);
}

private int quickSelect(int[] nums, int left, int right, int targetIdx) {
    if (left == right) return nums[left];
    
    // Partition and get pivot's final index
    int pivotIdx = partition(nums, left, right);
    
    if (pivotIdx == targetIdx) {
        return nums[pivotIdx];
    } else if (pivotIdx < targetIdx) {
        return quickSelect(nums, pivotIdx + 1, right, targetIdx);
    } else {
        return quickSelect(nums, left, pivotIdx - 1, targetIdx);
    }
}

private int partition(int[] nums, int left, int right) {
    // Use rightmost element as pivot (simple version)
    int pivot = nums[right];
    int i = left; // i tracks where the next smaller element goes
    
    for (int j = left; j < right; j++) {
        if (nums[j] <= pivot) {
            swap(nums, i, j);
            i++;
        }
    }
    
    swap(nums, i, right); // place pivot in its correct position
    return i;
}

private void swap(int[] nums, int i, int j) {
    int temp = nums[i];
    nums[i] = nums[j];
    nums[j] = temp;
}
```

**Dry Run — nums = [3,2,1,5,6,4], k = 2, targetIdx = 4:**

```
partition([3,2,1,5,6,4], 0, 5):
  pivot = 4, i = 0
  j=0: nums[0]=3 <= 4, swap(0,0), i=1. Array: [3,2,1,5,6,4]
  j=1: nums[1]=2 <= 4, swap(1,1), i=2. Array: [3,2,1,5,6,4]
  j=2: nums[2]=1 <= 4, swap(2,2), i=3. Array: [3,2,1,5,6,4]
  j=3: nums[3]=5 > 4, skip.
  j=4: nums[4]=6 > 4, skip.
  swap(3, 5). Array: [3,2,1,4,6,5]. pivotIdx = 3.

3 < targetIdx(4), recurse right: quickSelect([3,2,1,4,6,5], 4, 5, 4)

partition([3,2,1,4,6,5], 4, 5):
  pivot = 5, i = 4
  j=4: nums[4]=6 > 5, skip.
  swap(4, 5). Array: [3,2,1,4,5,6]. pivotIdx = 4.

4 == targetIdx(4). Return nums[4] = 5. Answer: 5. Correct.
```

**Time:** O(n) average, O(n^2) worst case. **Space:** O(1) extra (O(log n) stack).

**When to use which:**
- Heap: when k is small relative to n, or when you need to process a stream
- QuickSelect: when you have all data upfront and want average O(n)
- In interviews: mention both, implement heap first (cleaner), then quickselect if asked

---

### LC 347 — Top K Frequent Elements

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Cross-reference:** Topic 6 (HashMap & Frequency Counting) covers the bucket sort approach.

**Problem:** Given an integer array `nums` and an integer `k`, return the `k` most frequent elements.

**Example:**
```
Input: nums = [1,1,1,2,2,3], k = 2
Output: [1,2]
```

**Approach 1: Heap (O(n log k))**

```java
public int[] topKFrequent(int[] nums, int k) {
    // Count frequencies
    Map<Integer, Integer> freq = new HashMap<>();
    for (int num : nums) {
        freq.put(num, freq.getOrDefault(num, 0) + 1);
    }
    
    // Min-heap ordered by frequency — top is least frequent of our k candidates
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(
        (a, b) -> Integer.compare(freq.get(a), freq.get(b))
    );
    
    for (int num : freq.keySet()) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll(); // evict least frequent
        }
    }
    
    int[] result = new int[k];
    for (int i = k - 1; i >= 0; i--) {
        result[i] = minHeap.poll();
    }
    return result;
}
```

**Approach 2: Bucket Sort (O(n)) — see Topic 6**

```java
public int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int num : nums) {
        freq.put(num, freq.getOrDefault(num, 0) + 1);
    }
    
    // Bucket: index = frequency, value = list of numbers with that frequency
    List<Integer>[] buckets = new List[nums.length + 1];
    for (int num : freq.keySet()) {
        int f = freq.get(num);
        if (buckets[f] == null) buckets[f] = new ArrayList<>();
        buckets[f].add(num);
    }
    
    int[] result = new int[k];
    int idx = 0;
    for (int f = buckets.length - 1; f >= 0 && idx < k; f--) {
        if (buckets[f] != null) {
            for (int num : buckets[f]) {
                result[idx++] = num;
                if (idx == k) break;
            }
        }
    }
    return result;
}
```

**Time:** Heap O(n log k), Bucket O(n). **Space:** O(n) both.

**Interview note:** Bucket sort is O(n) but only works when frequencies are bounded (they are, by n). Heap is more general. Know both.

---

### LC 973 — K Closest Points to Origin

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given an array of points, return the k closest points to the origin (0, 0). Distance is Euclidean: sqrt(x^2 + y^2). You don't need to sort the result.

**Example:**
```
Input: points = [[1,3],[-2,2]], k = 1
Output: [[-2,2]]
Explanation: sqrt(1^2 + 3^2) = sqrt(10), sqrt((-2)^2 + 2^2) = sqrt(8). Closest is [-2,2].
```

**Key insight:** Compare x^2 + y^2 directly — no need for sqrt (monotone transformation).

```java
public int[][] kClosest(int[][] points, int k) {
    // Max-heap of size k — top is the farthest of our k closest candidates
    // When a closer point arrives, evict the farthest
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
        (a, b) -> Integer.compare(b[0] * b[0] + b[1] * b[1], a[0] * a[0] + a[1] * a[1])
    );
    
    for (int[] point : points) {
        maxHeap.offer(point);
        if (maxHeap.size() > k) {
            maxHeap.poll(); // evict the farthest point
        }
    }
    
    int[][] result = new int[k][2];
    for (int i = 0; i < k; i++) {
        result[i] = maxHeap.poll();
    }
    return result;
}
```

**Note:** This uses a MAX-heap of size k because we want the k smallest distances. The max-heap's top is the largest distance in our current k candidates. We evict it when a closer point arrives.

**Time:** O(n log k). **Space:** O(k).

**Alternative:** QuickSelect on distance, O(n) average.

---

### LC 703 — Kth Largest Element in a Stream

**Companies:** Amazon, Google, Microsoft

**Problem:** Design a class that finds the kth largest element in a stream. Implement `KthLargest(int k, int[] nums)` and `int add(int val)`.

**Example:**
```
KthLargest kthLargest = new KthLargest(3, [4, 5, 8, 2]);
kthLargest.add(3);  // returns 4
kthLargest.add(5);  // returns 5
kthLargest.add(10); // returns 5
kthLargest.add(9);  // returns 8
kthLargest.add(4);  // returns 8
```

**Key insight:** Maintain a min-heap of size k. The top is always the kth largest seen so far.

```java
class KthLargest {
    private PriorityQueue<Integer> minHeap;
    private int k;
    
    public KthLargest(int k, int[] nums) {
        this.k = k;
        this.minHeap = new PriorityQueue<>();
        
        for (int num : nums) {
            add(num);
        }
    }
    
    public int add(int val) {
        minHeap.offer(val);
        if (minHeap.size() > k) {
            minHeap.poll(); // keep only k largest
        }
        return minHeap.peek(); // kth largest is at top
    }
}
```

**Time:** O(log k) per add. **Space:** O(k).

---

## Category B: K-Way Merge

### LC 23 — Merge K Sorted Lists

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

**Cross-reference:** Topic 9 (Linked List Patterns) covers the divide-and-conquer approach.

**Problem:** Merge k sorted linked lists and return it as one sorted list.

**Example:**
```
Input: lists = [[1,4,5],[1,3,4],[2,6]]
Output: [1,1,2,3,4,4,5,6]
```

**Approach: Min-Heap**

Put the head of each list into a min-heap. Always poll the smallest node, add it to result, then push that node's next into the heap.

```java
public ListNode mergeKLists(ListNode[] lists) {
    // Min-heap ordered by node value
    PriorityQueue<ListNode> minHeap = new PriorityQueue<>(
        (a, b) -> Integer.compare(a.val, b.val)
    );
    
    // Add head of each non-null list
    for (ListNode node : lists) {
        if (node != null) {
            minHeap.offer(node);
        }
    }
    
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    
    while (!minHeap.isEmpty()) {
        ListNode smallest = minHeap.poll();
        curr.next = smallest;
        curr = curr.next;
        
        // Add next node from the same list
        if (smallest.next != null) {
            minHeap.offer(smallest.next);
        }
    }
    
    return dummy.next;
}
```

**Dry Run — lists = [[1,4,5],[1,3,4],[2,6]]:**

```
Initial heap: [1(list0), 1(list1), 2(list2)]

Poll 1(list0), add 4(list0). Output: 1. Heap: [1(list1), 2(list2), 4(list0)]
Poll 1(list1), add 3(list1). Output: 1,1. Heap: [2(list2), 3(list1), 4(list0)]
Poll 2(list2), add 6(list2). Output: 1,1,2. Heap: [3(list1), 4(list0), 6(list2)]
Poll 3(list1), add 4(list1). Output: 1,1,2,3. Heap: [4(list0), 4(list1), 6(list2)]
Poll 4(list0), add 5(list0). Output: 1,1,2,3,4. Heap: [4(list1), 5(list0), 6(list2)]
Poll 4(list1), no next. Output: 1,1,2,3,4,4. Heap: [5(list0), 6(list2)]
Poll 5(list0), no next. Output: 1,1,2,3,4,4,5. Heap: [6(list2)]
Poll 6(list2), no next. Output: 1,1,2,3,4,4,5,6. Heap: []

Result: [1,1,2,3,4,4,5,6]. Correct.
```

**Time:** O(n log k) where n = total nodes, k = number of lists. **Space:** O(k).

**Alternative:** Divide and conquer — merge pairs of lists repeatedly. Also O(n log k). See Topic 9.

---

### LC 373 — Find K Pairs with Smallest Sums

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given two sorted arrays `nums1` and `nums2`, return the k pairs (u, v) with the smallest sums. A pair (u, v) consists of one element from each array.

**Example:**
```
Input: nums1 = [1,7,11], nums2 = [2,4,6], k = 3
Output: [[1,2],[1,4],[1,6]]
```

**Key insight:** This is K-way merge. Think of it as k sorted "lists" where list i starts with (nums1[i], nums2[0]) and continues with (nums1[i], nums2[1]), etc. But we only need to start with the first element of nums1 (since nums1 is sorted, pairs starting with nums1[0] are always smallest).

```java
public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
    List<List<Integer>> result = new ArrayList<>();
    if (nums1.length == 0 || nums2.length == 0) return result;
    
    // Min-heap: [sum, i, j] where i indexes nums1, j indexes nums2
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(
        (a, b) -> Integer.compare(a[0], b[0])
    );
    
    // Seed with (nums1[i], nums2[0]) for all i
    // But we only need min(k, nums1.length) seeds — no point seeding more than k
    for (int i = 0; i < Math.min(k, nums1.length); i++) {
        minHeap.offer(new int[]{nums1[i] + nums2[0], i, 0});
    }
    
    while (!minHeap.isEmpty() && result.size() < k) {
        int[] curr = minHeap.poll();
        int i = curr[1], j = curr[2];
        
        result.add(Arrays.asList(nums1[i], nums2[j]));
        
        // Move to next element in nums2 for this nums1[i]
        if (j + 1 < nums2.length) {
            minHeap.offer(new int[]{nums1[i] + nums2[j + 1], i, j + 1});
        }
    }
    
    return result;
}
```

**Time:** O(k log k). **Space:** O(k).

---

### LC 378 — Kth Smallest Element in a Sorted Matrix

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Cross-reference:** Topic 4 (Binary Search) covers the binary search on value approach.

**Problem:** Given an n x n matrix where each row and column is sorted in ascending order, find the kth smallest element.

**Example:**
```
Input: matrix = [[1,5,9],[10,11,13],[12,13,15]], k = 8
Output: 13
```

**Approach 1: K-Way Merge with Heap**

Treat each row as a sorted list. Use K-way merge to find the kth element.

```java
public int kthSmallest(int[][] matrix, int k) {
    int n = matrix.length;
    
    // Min-heap: [value, row, col]
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(
        (a, b) -> Integer.compare(a[0], b[0])
    );
    
    // Add first element of each row
    for (int i = 0; i < n; i++) {
        minHeap.offer(new int[]{matrix[i][0], i, 0});
    }
    
    int count = 0;
    int result = 0;
    
    while (!minHeap.isEmpty()) {
        int[] curr = minHeap.poll();
        result = curr[0];
        count++;
        
        if (count == k) return result;
        
        int row = curr[1], col = curr[2];
        if (col + 1 < n) {
            minHeap.offer(new int[]{matrix[row][col + 1], row, col + 1});
        }
    }
    
    return result;
}
```

**Time:** O(k log n). **Space:** O(n).

**Approach 2: Binary Search on Value — see Topic 4**

Binary search on the value range [matrix[0][0], matrix[n-1][n-1]]. For each mid, count elements <= mid. O(n log(max-min)).

---

## Category C: Two Heaps / Median

### LC 295 — Find Median from Data Stream

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

**Problem:** Design a data structure that supports adding integers from a data stream and finding the median.

**Example:**
```
MedianFinder medianFinder = new MedianFinder();
medianFinder.addNum(1);    // arr = [1]
medianFinder.findMedian(); // return 1.0
medianFinder.addNum(2);    // arr = [1, 2]
medianFinder.findMedian(); // return 1.5
medianFinder.addNum(3);    // arr = [1, 2, 3]
medianFinder.findMedian(); // return 2.0
```

**The Two-Heap Approach**

Split numbers into two halves:
- `maxHeap` (lower half): max-heap, top = largest of lower half
- `minHeap` (upper half): min-heap, top = smallest of upper half

**Invariant:** `maxHeap.size() == minHeap.size()` or `maxHeap.size() == minHeap.size() + 1`

```java
class MedianFinder {
    private PriorityQueue<Integer> maxHeap; // lower half
    private PriorityQueue<Integer> minHeap; // upper half
    
    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }
    
    public void addNum(int num) {
        // Always add to maxHeap first
        maxHeap.offer(num);
        
        // Ensure maxHeap's top <= minHeap's top (maintain partition property)
        // If maxHeap's top is larger than minHeap's top, move it over
        if (!minHeap.isEmpty() && maxHeap.peek() > minHeap.peek()) {
            minHeap.offer(maxHeap.poll());
        }
        
        // Rebalance sizes
        if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }
    
    public double findMedian() {
        if (maxHeap.size() == minHeap.size()) {
            return (maxHeap.peek() + (double) minHeap.peek()) / 2.0;
        }
        return (double) maxHeap.peek();
    }
}
```

**Complete Dry Run — addNum sequence: [1, 2, 3, 4, 5]:**

```
State: maxHeap=[], minHeap=[]

addNum(1):
  offer 1 to maxHeap. maxHeap=[1], minHeap=[]
  minHeap empty, skip cross-check.
  maxHeap.size(1) == minHeap.size(0) + 1. Balanced.
  State: maxHeap=[1], minHeap=[]
  findMedian() -> maxHeap.size > minHeap.size -> return 1.0

addNum(2):
  offer 2 to maxHeap. maxHeap=[1,2] (heap: top=2), minHeap=[]
  minHeap empty, skip cross-check.
  maxHeap.size(2) > minHeap.size(0) + 1. Move top of maxHeap to minHeap.
    poll 2 from maxHeap, offer to minHeap.
  State: maxHeap=[1], minHeap=[2]
  findMedian() -> equal sizes -> (1 + 2) / 2.0 = 1.5

addNum(3):
  offer 3 to maxHeap. maxHeap=[1,3] (heap: top=3), minHeap=[2]
  maxHeap.peek()=3 > minHeap.peek()=2. Move 3 to minHeap.
    poll 3 from maxHeap, offer to minHeap.
  State: maxHeap=[1], minHeap=[2,3] (heap: top=2)
  minHeap.size(2) > maxHeap.size(1). Move top of minHeap to maxHeap.
    poll 2 from minHeap, offer to maxHeap.
  State: maxHeap=[1,2] (heap: top=2), minHeap=[3]
  findMedian() -> maxHeap.size(2) > minHeap.size(1) -> return maxHeap.peek() = 2.0

addNum(4):
  offer 4 to maxHeap. maxHeap=[1,2,4] (heap: top=4), minHeap=[3]
  maxHeap.peek()=4 > minHeap.peek()=3. Move 4 to minHeap.
    poll 4 from maxHeap, offer to minHeap.
  State: maxHeap=[1,2] (heap: top=2), minHeap=[3,4] (heap: top=3)
  Sizes equal. Balanced.
  findMedian() -> equal sizes -> (2 + 3) / 2.0 = 2.5

addNum(5):
  offer 5 to maxHeap. maxHeap=[1,2,5] (heap: top=5), minHeap=[3,4]
  maxHeap.peek()=5 > minHeap.peek()=3. Move 5 to minHeap.
    poll 5 from maxHeap, offer to minHeap.
  State: maxHeap=[1,2] (heap: top=2), minHeap=[3,4,5] (heap: top=3)
  minHeap.size(3) > maxHeap.size(2). Move top of minHeap to maxHeap.
    poll 3 from minHeap, offer to maxHeap.
  State: maxHeap=[1,2,3] (heap: top=3), minHeap=[4,5] (heap: top=4)
  findMedian() -> maxHeap.size(3) > minHeap.size(2) -> return maxHeap.peek() = 3.0

Final verification:
  Sorted: [1,2,3,4,5]. Median = 3. Correct.
```

**Time:** O(log n) per addNum, O(1) per findMedian. **Space:** O(n).

**Why this works:** The max-heap always holds the lower half, min-heap the upper half. The partition property (maxHeap.top <= minHeap.top) ensures no lower-half element is larger than any upper-half element. The size invariant ensures the median is always accessible at the top of one or both heaps.

---

### LC 480 — Sliding Window Median

**Cross-reference:** Topic 1 (Sliding Window)

**Problem:** Given an array and window size k, return the median of each window.

This extends LC 295 with the added complexity of removing elements from the middle of a heap (O(n) operation). The standard approach uses two heaps with lazy deletion, or a `TreeMap` for O(log n) removal.

```java
// Approach: Two heaps with lazy deletion
// Track elements to be removed; skip them when they reach the top
public double[] medianSlidingWindow(int[] nums, int k) {
    double[] result = new double[nums.length - k + 1];
    
    // Max-heap for lower half, min-heap for upper half
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    Map<Integer, Integer> toRemove = new HashMap<>(); // lazy deletion map
    
    // Initialize with first window
    for (int i = 0; i < k; i++) {
        maxHeap.offer(nums[i]);
    }
    // Move upper half to minHeap
    for (int i = 0; i < k / 2; i++) {
        minHeap.offer(maxHeap.poll());
    }
    
    result[0] = getMedian(maxHeap, minHeap, k);
    
    for (int i = k; i < nums.length; i++) {
        int outgoing = nums[i - k];
        int incoming = nums[i];
        
        // Add incoming
        if (incoming <= maxHeap.peek()) {
            maxHeap.offer(incoming);
        } else {
            minHeap.offer(incoming);
        }
        
        // Mark outgoing for lazy removal
        toRemove.put(outgoing, toRemove.getOrDefault(outgoing, 0) + 1);
        
        // Rebalance
        if (outgoing <= maxHeap.peek()) {
            // outgoing was in maxHeap
            if (incoming > maxHeap.peek()) {
                // incoming went to minHeap, net: minHeap gained, maxHeap lost
                maxHeap.offer(minHeap.poll());
            }
        } else {
            // outgoing was in minHeap
            if (incoming <= maxHeap.peek()) {
                // incoming went to maxHeap, net: maxHeap gained, minHeap lost
                minHeap.offer(maxHeap.poll());
            }
        }
        
        // Lazy clean tops
        while (!maxHeap.isEmpty() && toRemove.getOrDefault(maxHeap.peek(), 0) > 0) {
            toRemove.put(maxHeap.peek(), toRemove.get(maxHeap.peek()) - 1);
            maxHeap.poll();
        }
        while (!minHeap.isEmpty() && toRemove.getOrDefault(minHeap.peek(), 0) > 0) {
            toRemove.put(minHeap.peek(), toRemove.get(minHeap.peek()) - 1);
            minHeap.poll();
        }
        
        result[i - k + 1] = getMedian(maxHeap, minHeap, k);
    }
    
    return result;
}

private double getMedian(PriorityQueue<Integer> maxHeap, PriorityQueue<Integer> minHeap, int k) {
    if (k % 2 == 1) return maxHeap.peek();
    return (maxHeap.peek() + (double) minHeap.peek()) / 2.0;
}
```

**Time:** O(n log k). **Space:** O(k).

---

## Category D: Scheduling / Greedy

### LC 621 — Task Scheduler

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given a list of tasks (characters) and a cooldown `n`, find the minimum number of intervals to finish all tasks. Same tasks must be at least `n` intervals apart. You can insert idle intervals.

**Example:**
```
Input: tasks = ["A","A","A","B","B","B"], n = 2
Output: 8
Explanation: A -> B -> idle -> A -> B -> idle -> A -> B
```

**Key insight:** The most frequent task determines the minimum time. Greedily schedule the most frequent available task in each slot.

```java
public int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char task : tasks) {
        freq[task - 'A']++;
    }
    
    // Max-heap of frequencies
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    for (int f : freq) {
        if (f > 0) maxHeap.offer(f);
    }
    
    int time = 0;
    
    while (!maxHeap.isEmpty()) {
        // Fill one cycle of n+1 slots
        List<Integer> temp = new ArrayList<>();
        int slots = n + 1;
        
        while (slots > 0 && !maxHeap.isEmpty()) {
            int f = maxHeap.poll();
            f--;
            if (f > 0) temp.add(f);
            slots--;
            time++;
        }
        
        maxHeap.addAll(temp);
        
        // If more tasks remain, we had idle slots
        if (!maxHeap.isEmpty()) {
            time += slots;
        }
    }
    
    return time;
}
```

**Dry Run — tasks = [A,A,A,B,B,B], n = 2:**

```
freq: A=3, B=3
maxHeap: [3, 3]

Cycle 1 (slots=3):
  Poll 3(A), f=2, add to temp. time=1, slots=2.
  Poll 3(B), f=2, add to temp. time=2, slots=1.
  Heap empty, slots=1 remaining.
  maxHeap.addAll([2,2]). maxHeap=[2,2].
  Heap not empty, time += 1 (idle). time=3.

Cycle 2 (slots=3):
  Poll 2(A), f=1, add to temp. time=4, slots=2.
  Poll 2(B), f=1, add to temp. time=5, slots=1.
  Heap empty, slots=1 remaining.
  maxHeap.addAll([1,1]). maxHeap=[1,1].
  Heap not empty, time += 1 (idle). time=6.

Cycle 3 (slots=3):
  Poll 1(A), f=0, don't add. time=7, slots=2.
  Poll 1(B), f=0, don't add. time=8, slots=1.
  Heap empty, slots=1 remaining.
  maxHeap empty. No idle time.

Return 8. Correct.
```

**Time:** O(n * tasks.length) in worst case, but effectively O(tasks.length) since n is bounded. **Space:** O(1) (26 characters).

**Alternative O(1) formula:** `max(tasks.length, (maxFreq - 1) * (n + 1) + countOfMaxFreq)`. But the heap approach is more intuitive and generalizes better.

---

### LC 767 — Reorganize String

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given a string `s`, rearrange characters so no two adjacent characters are the same. Return any valid arrangement, or empty string if impossible.

**Example:**
```
Input: s = "aab"
Output: "aba"

Input: s = "aaab"
Output: ""
```

**Key insight:** Greedy — always place the most frequent character that's different from the last placed character.

```java
public String reorganizeString(String s) {
    int[] freq = new int[26];
    for (char c : s.toCharArray()) {
        freq[c - 'a']++;
    }
    
    // Max-heap of [frequency, character]
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
        (a, b) -> Integer.compare(b[0], a[0])
    );
    for (int i = 0; i < 26; i++) {
        if (freq[i] > 0) {
            maxHeap.offer(new int[]{freq[i], i});
        }
    }
    
    StringBuilder sb = new StringBuilder();
    
    while (maxHeap.size() >= 2) {
        // Take top two most frequent characters
        int[] first = maxHeap.poll();
        int[] second = maxHeap.poll();
        
        sb.append((char) ('a' + first[1]));
        sb.append((char) ('a' + second[1]));
        
        first[0]--;
        second[0]--;
        
        if (first[0] > 0) maxHeap.offer(first);
        if (second[0] > 0) maxHeap.offer(second);
    }
    
    // One character left
    if (!maxHeap.isEmpty()) {
        int[] last = maxHeap.poll();
        if (last[0] > 1) return ""; // can't place without adjacency
        sb.append((char) ('a' + last[1]));
    }
    
    return sb.toString();
}
```

**Time:** O(n log 26) = O(n). **Space:** O(26) = O(1).

**Impossibility check:** If any character appears more than `(n + 1) / 2` times, it's impossible.

---

### LC 253 — Meeting Rooms II (Premium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given an array of meeting time intervals, find the minimum number of conference rooms required.

**Example:**
```
Input: intervals = [[0,30],[5,10],[15,20]]
Output: 2
```

**Key insight:** Sort by start time. Use a min-heap of end times. For each meeting, if the earliest-ending meeting ends before this one starts, reuse that room. Otherwise, open a new room.

```java
public int minMeetingRooms(int[][] intervals) {
    if (intervals.length == 0) return 0;
    
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
    
    // Min-heap of end times — top is the earliest ending meeting
    PriorityQueue<Integer> endTimes = new PriorityQueue<>();
    
    for (int[] interval : intervals) {
        if (!endTimes.isEmpty() && endTimes.peek() <= interval[0]) {
            endTimes.poll(); // this room is free, reuse it
        }
        endTimes.offer(interval[1]); // assign a room (new or reused)
    }
    
    return endTimes.size(); // number of rooms currently occupied
}
```

**Dry Run — intervals = [[0,30],[5,10],[15,20]]:**

```
Sorted: [[0,30],[5,10],[15,20]]
endTimes = []

Process [0,30]: heap empty, offer 30. endTimes=[30]. Rooms=1.
Process [5,10]: peek=30 > 5, can't reuse. Offer 10. endTimes=[10,30]. Rooms=2.
Process [15,20]: peek=10 <= 15, reuse! Poll 10, offer 20. endTimes=[20,30]. Rooms=2.

Return 2. Correct.
```

**Time:** O(n log n). **Space:** O(n).

---

## Category E: Design

### LC 355 — Design Twitter

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Design a simplified Twitter with: `postTweet(userId, tweetId)`, `getNewsFeed(userId)` (10 most recent tweets from user and followees), `follow(followerId, followeeId)`, `unfollow(followerId, followeeId)`.

**Key insight:** `getNewsFeed` is a K-way merge problem. Each user has a sorted (by time) list of tweets. Merge them and take the top 10.

```java
class Twitter {
    private int timestamp;
    private Map<Integer, List<int[]>> tweets;    // userId -> list of [timestamp, tweetId]
    private Map<Integer, Set<Integer>> following; // userId -> set of followeeIds
    
    public Twitter() {
        timestamp = 0;
        tweets = new HashMap<>();
        following = new HashMap<>();
    }
    
    public void postTweet(int userId, int tweetId) {
        tweets.computeIfAbsent(userId, k -> new ArrayList<>())
              .add(new int[]{timestamp++, tweetId});
    }
    
    public List<Integer> getNewsFeed(int userId) {
        // Max-heap: [timestamp, tweetId, userId, tweetIndex]
        // We want most recent first, so max-heap by timestamp
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(b[0], a[0])
        );
        
        // Collect all users to include (self + followees)
        Set<Integer> users = new HashSet<>();
        users.add(userId);
        if (following.containsKey(userId)) {
            users.addAll(following.get(userId));
        }
        
        // Add the most recent tweet from each user
        for (int uid : users) {
            List<int[]> userTweets = tweets.get(uid);
            if (userTweets != null && !userTweets.isEmpty()) {
                int idx = userTweets.size() - 1; // most recent
                int[] tweet = userTweets.get(idx);
                maxHeap.offer(new int[]{tweet[0], tweet[1], uid, idx});
            }
        }
        
        List<Integer> feed = new ArrayList<>();
        while (!maxHeap.isEmpty() && feed.size() < 10) {
            int[] curr = maxHeap.poll();
            feed.add(curr[1]); // tweetId
            
            int uid = curr[2], idx = curr[3];
            if (idx > 0) {
                // Add the previous tweet from the same user
                int[] prevTweet = tweets.get(uid).get(idx - 1);
                maxHeap.offer(new int[]{prevTweet[0], prevTweet[1], uid, idx - 1});
            }
        }
        
        return feed;
    }
    
    public void follow(int followerId, int followeeId) {
        following.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
    }
    
    public void unfollow(int followerId, int followeeId) {
        if (following.containsKey(followerId)) {
            following.get(followerId).remove(followeeId);
        }
    }
}
```

**Time:** `postTweet` O(1), `getNewsFeed` O(u log u + 10 log u) where u = number of users followed, `follow/unfollow` O(1). **Space:** O(total tweets + follow relationships).

---

### LC 502 — IPO

**Companies:** Amazon, Google, Goldman Sachs

**Problem:** You have `w` initial capital. You can complete at most `k` projects. Each project has a profit and a capital requirement. Maximize your final capital.

**Example:**
```
Input: k = 2, w = 0, profits = [1,2,3], capital = [0,1,1]
Output: 4
Explanation: Start with 0. Do project 0 (capital=0, profit=1). Now have 1.
             Do project 2 (capital=1, profit=3). Now have 4.
```

**Key insight:** Greedy with two heaps.
1. Min-heap of (capital, profit) — sorted by capital requirement
2. Max-heap of profits — for projects we can currently afford

At each step: unlock all projects we can afford (move from min-heap to max-heap), then pick the most profitable one.

```java
public int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
    int n = profits.length;
    
    // Min-heap by capital requirement
    PriorityQueue<int[]> minCapHeap = new PriorityQueue<>(
        (a, b) -> Integer.compare(a[0], b[0])
    );
    
    // Max-heap by profit (projects we can afford)
    PriorityQueue<Integer> maxProfitHeap = new PriorityQueue<>(Collections.reverseOrder());
    
    for (int i = 0; i < n; i++) {
        minCapHeap.offer(new int[]{capital[i], profits[i]});
    }
    
    for (int i = 0; i < k; i++) {
        // Unlock all projects we can currently afford
        while (!minCapHeap.isEmpty() && minCapHeap.peek()[0] <= w) {
            maxProfitHeap.offer(minCapHeap.poll()[1]);
        }
        
        // No affordable projects
        if (maxProfitHeap.isEmpty()) break;
        
        // Pick the most profitable affordable project
        w += maxProfitHeap.poll();
    }
    
    return w;
}
```

**Dry Run — k=2, w=0, profits=[1,2,3], capital=[0,1,1]:**

```
minCapHeap: [(0,1), (1,2), (1,3)]
maxProfitHeap: []

Round 1:
  Unlock: (0,1) -> capital=0 <= w=0. Move profit=1 to maxProfitHeap.
  (1,2) -> capital=1 > w=0. Stop.
  maxProfitHeap: [1]
  Pick profit=1. w = 0 + 1 = 1.

Round 2:
  Unlock: (1,2) -> capital=1 <= w=1. Move profit=2 to maxProfitHeap.
  (1,3) -> capital=1 <= w=1. Move profit=3 to maxProfitHeap.
  maxProfitHeap: [3, 2]
  Pick profit=3. w = 1 + 3 = 4.

Return 4. Correct.
```

**Time:** O(n log n + k log n). **Space:** O(n).

---

### LC 1046 — Last Stone Weight

**Companies:** Amazon, Google, Microsoft

**Problem:** Given stones with weights, smash the two heaviest stones together. If equal, both destroyed. If unequal, the difference remains. Return the weight of the last stone (or 0).

```java
public int lastStoneWeight(int[] stones) {
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    for (int stone : stones) {
        maxHeap.offer(stone);
    }
    
    while (maxHeap.size() > 1) {
        int first = maxHeap.poll();
        int second = maxHeap.poll();
        
        if (first != second) {
            maxHeap.offer(first - second);
        }
        // If equal, both destroyed — don't add anything
    }
    
    return maxHeap.isEmpty() ? 0 : maxHeap.peek();
}
```

**Time:** O(n log n). **Space:** O(n).

---

## Common Mistakes

### 1. Integer Overflow in Comparators

```java
// WRONG — can overflow!
// If a = Integer.MIN_VALUE and b = Integer.MAX_VALUE:
// b - a = Integer.MAX_VALUE - Integer.MIN_VALUE = overflow (wraps to negative)
PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> b - a);

// CORRECT — always safe
PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> Integer.compare(b, a));

// Also correct
PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
```

This is a subtle bug that passes most test cases but fails on edge cases with extreme values. Interviewers at Google and Amazon specifically test for this.

### 2. Forgetting PriorityQueue is Min-Heap

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(5);
pq.offer(1);
pq.offer(3);
pq.poll(); // returns 1, NOT 5!
```

Every time you create a `PriorityQueue`, ask yourself: "Do I want the smallest or largest element first?" If largest, add `Collections.reverseOrder()` or a custom comparator.

### 3. Using Min-Heap When You Need Max-Heap for Top-K

The counterintuitive part: for top-K largest, use a MIN-heap. For top-K smallest, use a MAX-heap.

```java
// WRONG: Using max-heap for top-K largest
// This gives you the largest element, but you can't efficiently maintain size k
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

// CORRECT: Min-heap of size k for top-K largest
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
for (int num : nums) {
    minHeap.offer(num);
    if (minHeap.size() > k) minHeap.poll(); // evict smallest
}
// minHeap now contains the k largest elements
```

### 4. O(n) Removal from Middle of Heap

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
// ...
pq.remove(specificElement); // O(n) — linear scan to find it!
pq.contains(specificElement); // O(n) — linear scan!
```

If you need fast arbitrary removal, use `TreeMap` or `TreeSet` (O(log n) for everything). Or use lazy deletion: mark elements as removed and skip them when they reach the top.

### 5. Iterating PriorityQueue in Sorted Order

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(3); pq.offer(1); pq.offer(2);

// WRONG: does NOT iterate in sorted order
for (int x : pq) {
    System.out.print(x + " "); // might print: 1 3 2 (heap order, not sorted)
}

// CORRECT: poll repeatedly
while (!pq.isEmpty()) {
    System.out.print(pq.poll() + " "); // prints: 1 2 3
}
```

### 6. Comparing Objects with ==

```java
// WRONG: comparing Integer objects with ==
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(1000);
if (pq.peek() == 1000) { ... } // might fail! Integer cache only covers -128 to 127

// CORRECT: use equals() or unbox
if (pq.peek().equals(1000)) { ... }
if (pq.peek() == 1000) { ... } // works only for small values due to Integer cache
```

### 7. Not Handling Empty Heap

```java
// WRONG: NullPointerException if heap is empty
int top = pq.peek(); // returns null if empty, then unboxing throws NPE

// CORRECT: check first
if (!pq.isEmpty()) {
    int top = pq.peek();
}
// Or use conditional
Integer top = pq.peek(); // null if empty
```

### 8. Modifying Elements in the Heap

Java's `PriorityQueue` doesn't re-heapify when you modify an element's value. If you store mutable objects and change their comparison key, the heap order breaks silently.

```java
// WRONG: modifying an object that's already in the heap
int[] entry = {5, 0}; // [value, index]
pq.offer(entry);
entry[0] = 10; // heap doesn't know! Order is now wrong.

// CORRECT: remove, modify, re-add
pq.remove(entry);
entry[0] = 10;
pq.offer(entry);
// But remove() is O(n)! For frequent updates, use TreeMap instead.
```

---

## Heap vs Other Data Structures

### Heap vs TreeMap / TreeSet

| Feature | PriorityQueue (Heap) | TreeMap / TreeSet |
|---------|---------------------|-------------------|
| Access min/max | O(1) peek | O(log n) first()/last() |
| Insert | O(log n) | O(log n) |
| Remove min/max | O(log n) | O(log n) |
| Remove arbitrary | O(n) | O(log n) |
| Access kth element | O(k log n) | O(log n) with navigable methods |
| Contains | O(n) | O(log n) |
| Duplicates | Yes | TreeSet: No. TreeMap: keys unique |
| Memory | Lower (array-based) | Higher (node-based) |

**Use heap when:** You only need the min or max, and you don't need arbitrary removal.

**Use TreeMap/TreeSet when:** You need arbitrary removal, range queries, or access to any rank.

For sliding window median (LC 480), `TreeMap` is often cleaner than two heaps with lazy deletion because it supports O(log n) removal.

```java
// TreeMap approach for problems needing arbitrary removal
TreeMap<Integer, Integer> lower = new TreeMap<>(); // value -> count
TreeMap<Integer, Integer> upper = new TreeMap<>();
```

### Heap vs Sorting

| Scenario | Heap | Sort |
|----------|------|------|
| All data available | O(n log k) for top-k | O(n log n) |
| Streaming data | O(log k) per element | Can't sort a stream |
| Need all elements sorted | O(n log n) | O(n log n) |
| Need only min/max | O(n) build, O(1) access | O(n log n) |

**Use heap when:** Data arrives as a stream, or you only need top-k (not all sorted).

**Use sort when:** You need all elements in order, or the problem is a one-time operation on all data.

### Heap vs Bucket Sort for Top-K

For LC 347 (Top K Frequent Elements):
- Heap: O(n log k) — works for any values
- Bucket sort: O(n) — works when frequencies are bounded by n

```java
// Bucket sort is O(n) but requires bounded values
// Heap is O(n log k) but works for any values
// In interviews: mention both, implement the one that fits constraints
```

### When to Use Each

```
Problem type                          -> Data structure
─────────────────────────────────────────────────────────
Streaming min/max                     -> Heap
Top-K from stream                     -> Heap of size k
Merge K sorted sequences              -> Heap (K-way merge)
Streaming median                      -> Two heaps
Sliding window median                 -> Two heaps + lazy deletion, or TreeMap
Arbitrary removal + min/max           -> TreeMap/TreeSet
Range queries                         -> TreeMap
Dijkstra's shortest path              -> Heap (Topic 16)
Prim's MST                            -> Heap
Huffman coding                        -> Heap
```

---

## Cheat Sheet

### Java PriorityQueue Quick Reference

```java
// Min-heap (default)
PriorityQueue<Integer> minPQ = new PriorityQueue<>();

// Max-heap
PriorityQueue<Integer> maxPQ = new PriorityQueue<>(Collections.reverseOrder());

// Custom comparator (SAFE — no overflow)
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));

// Core operations
pq.offer(x);      // insert O(log n)
pq.poll();        // remove+return min O(log n), null if empty
pq.peek();        // view min O(1), null if empty
pq.size();        // O(1)
pq.isEmpty();     // O(1)
```

### Pattern Recognition

```
"K largest/smallest/frequent/closest"  -> Top-K pattern (heap of size k)
"Merge K sorted"                        -> K-way merge (heap with list tracking)
"Median from stream"                    -> Two heaps
"Sliding window median"                 -> Two heaps + lazy deletion
"Task scheduler / cooldown"             -> Greedy + max-heap of frequencies
"Meeting rooms / intervals"             -> Sort + min-heap of end times
"Maximize capital / greedy selection"   -> Two heaps (unlock + pick best)
```

### Complexity Summary

| Pattern | Time | Space |
|---------|------|-------|
| Top-K | O(n log k) | O(k) |
| K-way merge | O(n log k) | O(k) |
| Two heaps (median) | O(log n) per op | O(n) |
| Build heap from array | O(n) | O(1) |
| Heap sort | O(n log n) | O(1) |

### Comparator Patterns

```java
// Sort by first element ascending (safe)
(a, b) -> Integer.compare(a[0], b[0])

// Sort by first element descending (safe)
(a, b) -> Integer.compare(b[0], a[0])

// Sort by frequency (from map)
(a, b) -> Integer.compare(freq.get(a), freq.get(b))

// Sort by distance from origin
(a, b) -> Integer.compare(a[0]*a[0] + a[1]*a[1], b[0]*b[0] + b[1]*b[1])

// Multi-key sort
(a, b) -> {
    if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
    return Integer.compare(a[1], b[1]);
}

// NEVER use subtraction for large integers
// (a, b) -> b - a  // WRONG — can overflow
// (a, b) -> a - b  // WRONG — can overflow
```

---

## Study Roadmap

### Week 1: Foundation (Days 1-5)

**Day 1-2: Core Mechanics**
- Understand min-heap vs max-heap
- Practice creating PriorityQueue with custom comparators
- Solve LC 703 (Kth Largest in Stream) — simplest heap problem
- Solve LC 1046 (Last Stone Weight) — max-heap basics

**Day 3-4: Top-K Pattern**
- Study Template 1 carefully — understand WHY min-heap for top-K largest
- Solve LC 215 (Kth Largest) — implement BOTH heap and quickselect
- Solve LC 973 (K Closest Points) — note: max-heap for top-K smallest distances

**Day 5: Top-K Frequency**
- Solve LC 347 (Top K Frequent) — heap approach first, then bucket sort
- Review the heap vs bucket sort tradeoff

### Week 2: Advanced Patterns (Days 6-10)

**Day 6-7: K-Way Merge**
- Study Template 2 — understand the (value, listIdx, elemIdx) pattern
- Solve LC 23 (Merge K Sorted Lists) — the canonical K-way merge
- Solve LC 373 (K Pairs Smallest Sums) — K-way merge variant

**Day 8-9: Two Heaps**
- Study Template 3 — trace through the dry run manually
- Solve LC 295 (Find Median from Data Stream) — the flagship problem
- Understand the invariant: maxHeap.size() == minHeap.size() or +1

**Day 10: Scheduling**
- Solve LC 253 (Meeting Rooms II) — sort + min-heap of end times
- Solve LC 621 (Task Scheduler) — greedy + max-heap of frequencies

### Week 3: Hard Problems + Design (Days 11-14)

**Day 11: Hard Scheduling**
- Solve LC 767 (Reorganize String) — greedy with heap
- Solve LC 502 (IPO) — two heaps, greedy unlock pattern

**Day 12: Matrix + Design**
- Solve LC 378 (Kth Smallest in Matrix) — K-way merge on matrix
- Solve LC 355 (Design Twitter) — K-way merge for news feed

**Day 13: Review + Edge Cases**
- Re-solve LC 215 from scratch (both approaches)
- Re-solve LC 295 from scratch
- Practice writing comparators without overflow

**Day 14: Mock Interview**
- Pick 2 problems you haven't solved recently
- Time yourself: 20 minutes per problem
- Focus on: recognizing the pattern, choosing the right heap type, handling edge cases

### Problem Priority

**Must solve (core patterns):**
1. LC 703 — Kth Largest in Stream (warm-up)
2. LC 215 — Kth Largest (both approaches)
3. LC 295 — Find Median from Data Stream (two heaps)
4. LC 23 — Merge K Sorted Lists (K-way merge)
5. LC 621 — Task Scheduler (scheduling)
6. LC 253 — Meeting Rooms II (interval scheduling)

**Should solve (pattern variants):**
7. LC 347 — Top K Frequent
8. LC 973 — K Closest Points
9. LC 373 — K Pairs Smallest Sums
10. LC 502 — IPO

**Nice to solve (design + hard):**
11. LC 355 — Design Twitter
12. LC 767 — Reorganize String
13. LC 378 — Kth Smallest in Matrix
14. LC 1046 — Last Stone Weight

### Interview Tips

**Pattern recognition (say this out loud):**
- "I need repeated access to the min/max of a changing set — heap."
- "I need the K largest — min-heap of size K."
- "I need to merge K sorted sequences — K-way merge with heap."
- "I need streaming median — two heaps."

**Before coding:**
1. Confirm: min-heap or max-heap?
2. Confirm: what goes in the heap? (value only, or value + metadata?)
3. Confirm: what's the comparator? Write it out, check for overflow.

**Common follow-ups:**
- "What if the data is a stream?" — heap handles this naturally
- "Can you do better than O(n log k)?" — bucket sort if values bounded
- "What if k changes dynamically?" — rebuild heap or use different structure
- "What about memory constraints?" — heap of size k uses O(k) space

---

*Document 12 of 20 | Prerequisites: Topic 6 (HashMap), Topic 9 (Linked Lists) | Next: Topic 13 (Graphs - BFS/DFS)*
