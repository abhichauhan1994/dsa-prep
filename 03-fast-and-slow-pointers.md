# Topic 3: Fast & Slow Pointers (Floyd's Tortoise and Hare)

> **Series Note:** This is document 3 of 20 in the FAANG DSA Prep series. Topic 2 (Two Pointers) briefly introduced fast/slow as Template 3. This document is the complete deep-dive.

---

## Overview

Fast & Slow Pointers is a specialized sub-pattern of Two Pointers where two pointers traverse a sequence at different speeds. One pointer (the "tortoise") moves one step at a time. The other (the "hare") moves two steps. This speed differential creates three powerful capabilities:

1. **Cycle detection** — if a cycle exists, the fast pointer will eventually lap the slow pointer and they'll meet inside the cycle.
2. **Midpoint finding** — when the fast pointer reaches the end of a linear sequence, the slow pointer is exactly at the middle.
3. **Cycle entry detection** — after finding a meeting point, a second phase locates the exact node where the cycle begins.

The pattern extends beyond linked lists. Any deterministic function that maps a finite set to itself must eventually repeat a value (Pigeonhole Principle). This insight makes Floyd's algorithm applicable to number sequences (LC 202), array-as-graph problems (LC 287), and pseudorandom number analysis.

**Top companies asking these problems:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Oracle

---

## Table of Contents

1. [Core Concept](#1-core-concept)
2. [ELI5 — Plain English Explanations](#2-eli5--plain-english-explanations)
3. [When to Use — Recognition Signals](#3-when-to-use--recognition-signals)
4. [Core Templates in Java](#4-core-templates-in-java)
   - [Template 1: Cycle Detection (boolean)](#template-1-cycle-detection-boolean)
   - [Template 2: Find Middle of Linked List](#template-2-find-middle-of-linked-list)
   - [Template 3: Find Cycle Entry Point (Floyd's Phase 2)](#template-3-find-cycle-entry-point-floyds-phase-2)
   - [Template 4: Cycle Detection on Functional Graphs](#template-4-cycle-detection-on-functional-graphs)
5. [Real-World Applications](#5-real-world-applications)
6. [Problem Categories & Solutions](#6-problem-categories--solutions)
   - [Category A: Linked List Cycle Problems](#category-a-linked-list-cycle-problems)
   - [Category B: Finding Middle / Structural Problems](#category-b-finding-middle--structural-problems)
   - [Category C: Advanced / Mathematical](#category-c-advanced--mathematical)
7. [Common Mistakes & Edge Cases](#7-common-mistakes--edge-cases)
8. [Pattern Comparison](#8-pattern-comparison)
9. [Quick Reference Cheat Sheet](#9-quick-reference-cheat-sheet)
10. [Practice Roadmap](#10-practice-roadmap)

---

## 1. Core Concept

### What is Fast & Slow Pointers?

Two pointers start at the same position (usually the head of a linked list or index 0 of an array). On each iteration:
- `slow` advances by 1 step: `slow = slow.next`
- `fast` advances by 2 steps: `fast = fast.next.next`

This is NOT the same as the opposite-ends Two Pointers from Topic 2. Both pointers move in the **same direction**. The difference is speed, not direction.

### The Three Main Applications

#### Application 1: Cycle Detection

If a linked list has a cycle, the fast pointer will eventually "lap" the slow pointer and they'll meet at the same node. If there's no cycle, the fast pointer reaches `null` first.

**Invariant:** In a cyclic structure, fast gains exactly 1 step on slow per iteration. Since the cycle is finite, they must meet within at most `cycle_length` iterations after slow enters the cycle.

#### Application 2: Finding the Middle

For a linear (non-cyclic) linked list of length `n`:
- After `n/2` iterations, `fast` has moved `n` steps (at the end)
- `slow` has moved `n/2` steps (at the middle)

No need to count the length first. One pass finds the middle.

#### Application 3: Finding the Cycle Entry Point

After detecting a cycle (fast and slow meet at some node inside the cycle), you can find the exact node where the cycle begins. This is Floyd's Phase 2.

Reset one pointer to `head`. Keep the other at the meeting point. Move both at speed 1. They meet at the cycle entry node.

---

### The Mathematical Proof

This is the part most candidates skip. Don't skip it. Understanding the math means you can reconstruct the algorithm from scratch under pressure.

**Setup:**
- Let `a` = distance from head to the cycle entry node
- Let `b` = distance from cycle entry to the meeting point (where fast and slow first meet)
- Let `c` = total length of the cycle

**Phase 1 Analysis (why they meet):**

When slow enters the cycle, fast is already somewhere inside it. From that point, fast gains 1 step on slow per iteration. Since the cycle has length `c`, fast will catch slow within at most `c` iterations. They're guaranteed to meet.

More precisely, when slow has traveled distance `a + b` (it entered the cycle and moved `b` steps in), fast has traveled `2(a + b)` total steps. Fast has completed some number of full cycles plus `b` steps into the cycle:

```
fast distance = a + k*c + b   (for some integer k >= 1)
slow distance = a + b

Since fast = 2 * slow:
a + k*c + b = 2(a + b)
k*c = a + b
a = k*c - b
```

**Phase 2 Analysis (why resetting finds the entry):**

From the equation `a = k*c - b`:

- One pointer starts at `head`, distance `a` from the cycle entry.
- The other pointer starts at the meeting point, which is `b` steps into the cycle, meaning it's `c - b` steps away from the cycle entry (going forward in the cycle).

Since `a = k*c - b = (k-1)*c + (c - b)`, the pointer at the meeting point will travel `(k-1)` full cycles plus `c - b` steps to reach the entry. The pointer from head travels exactly `a = (k-1)*c + (c-b)` steps to reach the entry.

**They arrive at the cycle entry at the same time.** Both pointers moving at speed 1 from their respective starting positions will meet exactly at the cycle entry node.

**Concrete example:**

```
List: 1 -> 2 -> 3 -> 4 -> 5 -> 3 (cycle back to node 3)
                     ^
                     cycle entry (index 2, 0-based)

a = 2 (head to node 3: steps through 1, 2)
c = 3 (cycle: 3 -> 4 -> 5 -> back to 3)
```

Phase 1 trace:
```
Start: slow=1, fast=1
Step 1: slow=2, fast=3
Step 2: slow=3, fast=5
Step 3: slow=4, fast=4  <-- they meet at node 4
```

Meeting point is node 4, which is `b=1` step into the cycle (cycle starts at node 3).

Check: `a = k*c - b` → `2 = 1*3 - 1 = 2`. Correct.

Phase 2 trace:
```
Reset slow to head (node 1). fast stays at node 4.
Step 1: slow=2, fast=5
Step 2: slow=3, fast=3  <-- they meet at node 3 (cycle entry)
```

---

### Extension to Functional Graphs

The linked list is just one instance of a more general structure: a **functional graph**, where each node has exactly one outgoing edge (its "next" value). Any deterministic function `f: S -> S` on a finite set `S` creates a functional graph. Repeated application of `f` starting from any element must eventually cycle.

This is why Floyd's algorithm works on:
- **Happy Number (LC 202):** `f(n) = sum of squares of digits of n`. The sequence `n, f(n), f(f(n)), ...` must eventually cycle or reach 1.
- **Find Duplicate Number (LC 287):** Treat the array as `f(i) = nums[i]`. The duplicate creates a node with two incoming edges, which is exactly the cycle entry.

---

## 2. ELI5 — Plain English Explanations

### Cycle Detection

Imagine two runners on a circular track. One runs twice as fast as the other. No matter where they start on the loop, the fast runner will always lap the slow runner and they'll meet again. If the track is NOT a loop (it has a dead end), the fast runner just reaches the end first and you know there's no cycle.

The key insight: on a circular track, the fast runner can never "skip over" the slow runner permanently. Every lap, the gap closes by one position. Eventually the gap is zero.

### Finding the Middle

Two people start at the same place on a road. One takes 1 step at a time, the other takes 2 steps. When the fast person reaches the end of the road, the slow person is exactly halfway. Like a buddy system for finding the midpoint without knowing the road's total length in advance.

### Cycle Entry Detection

After the two runners meet on the circular track, one goes back to the starting line. Now both run at the same speed. The place where they meet again is exactly where the track starts to loop. The math works out because the distance from the start to the loop entry equals the distance from the meeting point to the loop entry (accounting for full laps).

---

## 3. When to Use — Recognition Signals

### Green Flags (reach for this pattern)

| Signal in problem statement | What it maps to |
|----------------------------|-----------------|
| "linked list has a cycle" | Template 1 (cycle detection) |
| "detect a loop" | Template 1 |
| "find the start of the cycle" | Template 3 (Floyd's Phase 2) |
| "find the middle of a linked list" | Template 2 |
| "happy number" | Template 4 (functional graph) |
| "sequence eventually repeats" | Template 4 |
| "find the duplicate number" (array [1,n] with n+1 elements) | Template 4 (array as graph) |
| "linked list palindrome check" | Template 2 + reverse |
| "reorder linked list" | Template 2 + reverse + merge |
| "sort linked list" | Template 2 (for merge sort split) |
| "remove nth node from end" | Two-pointer with N-gap (related) |

### Red Flags (don't use this pattern)

- **Need to know which specific elements are in the cycle:** Use a HashMap to track visited nodes. Fast/slow only tells you IF a cycle exists and WHERE it starts, not which nodes are part of it.
- **Need the cycle length directly:** Floyd's doesn't give you length immediately. You'd need to detect the meeting point, then count steps until you return to it. Sometimes a simple visited-set is cleaner.
- **Array values don't form a valid function:** For Template 4, every index must map to a valid index (values in range [0, n-1] or [1, n]). If values can be out of range, the "next pointer" interpretation breaks.
- **Need to find ALL cycles in a graph:** Floyd's finds one cycle in a functional graph. For general graphs with multiple cycles, use DFS with coloring (white/gray/black).
- **The sequence is not deterministic:** Floyd's requires that `f(x)` always produces the same output for the same input. Random or external-state-dependent functions don't work.

---

## 4. Core Templates in Java

### Template 1: Cycle Detection (boolean)

**Problem:** Does this linked list have a cycle?

**Key invariants:**
- `slow` moves 1 step per iteration
- `fast` moves 2 steps per iteration
- If `fast == slow` at any point (after start), there's a cycle
- If `fast` or `fast.next` becomes `null`, there's no cycle

**The critical null check:** You need TWO null checks before advancing fast: `fast != null` AND `fast.next != null`. If you only check `fast != null`, then `fast.next.next` will throw NPE when `fast` is the last node.

```java
public class Solution {
    public boolean hasCycle(ListNode head) {
        // Edge case: empty list or single node with no self-loop
        if (head == null || head.next == null) {
            return false;
        }

        ListNode slow = head;
        ListNode fast = head;

        // TWO null checks: fast != null guards fast.next access
        //                  fast.next != null guards fast.next.next access
        while (fast != null && fast.next != null) {
            slow = slow.next;           // 1 step
            fast = fast.next.next;      // 2 steps

            if (slow == fast) {         // pointer equality, not value equality
                return true;
            }
        }

        // fast reached null -> no cycle
        return false;
    }
}
```

**Step-by-step trace (cyclic list: 1->2->3->4->2):**

```
Initial:  slow=1, fast=1
Iter 1:   slow=2, fast=3   (slow: 1->2, fast: 1->2->3)
Iter 2:   slow=3, fast=2   (slow: 2->3, fast: 3->4->2)
Iter 3:   slow=4, fast=4   (slow: 3->4, fast: 2->3->4)
slow == fast -> return true
```

**Step-by-step trace (linear list: 1->2->3->null):**

```
Initial:  slow=1, fast=1
Iter 1:   slow=2, fast=3
Iter 2:   slow=3, fast=null  (fast.next was null, so fast.next.next would NPE)
           -> loop condition fails (fast.next == null)
return false
```

**Time:** O(n) — in the worst case (no cycle), fast traverses the whole list. With a cycle, they meet within O(n) steps.
**Space:** O(1) — two pointers only.

---

### Template 2: Find Middle of Linked List

**Problem:** Find the middle node of a linked list.

**Two variants exist, and the difference matters for problems that use the middle as a split point.**

#### Variant A: Right-middle (for even-length lists, returns the second middle)

Loop condition: `while (fast != null && fast.next != null)`

```java
public ListNode findMiddle(ListNode head) {
    if (head == null) return null;

    ListNode slow = head;
    ListNode fast = head;

    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }

    // slow is at the middle (right-middle for even length)
    return slow;
}
```

**Trace for odd length (1->2->3->4->5):**
```
Initial:  slow=1, fast=1
Iter 1:   slow=2, fast=3
Iter 2:   slow=3, fast=5
Iter 3:   fast.next == null -> loop exits
slow = 3 (correct middle)
```

**Trace for even length (1->2->3->4):**
```
Initial:  slow=1, fast=1
Iter 1:   slow=2, fast=3
Iter 2:   slow=3, fast=null (fast.next.next = 4.next = null, so fast becomes null)
           Wait: fast=3, fast.next=4, fast.next.next=null -> fast = null
           Loop condition: fast != null -> false -> exit
slow = 3 (second middle, right-middle)
```

Actually let's be precise:
```
After iter 1: slow=2, fast=3
Check: fast(3) != null AND fast.next(4) != null -> true
Iter 2: slow=3, fast=4.next=null? No: fast = fast.next.next = 3.next.next = 4.next = null
After iter 2: slow=3, fast=null
Check: fast(null) != null -> false -> exit
slow = 3 (right-middle of [1,2,3,4])
```

#### Variant B: Left-middle (for even-length lists, returns the first middle)

Loop condition: `while (fast.next != null && fast.next.next != null)`

```java
public ListNode findMiddleLeft(ListNode head) {
    if (head == null) return null;

    ListNode slow = head;
    ListNode fast = head;

    // fast.next and fast.next.next must both be non-null
    // This stops one iteration earlier for even-length lists
    while (fast.next != null && fast.next.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }

    // slow is at the left-middle
    return slow;
}
```

**Trace for even length (1->2->3->4):**
```
Initial:  slow=1, fast=1
Check: fast.next(2) != null AND fast.next.next(3) != null -> true
Iter 1:   slow=2, fast=3
Check: fast.next(4) != null AND fast.next.next(null) != null -> false -> exit
slow = 2 (left-middle of [1,2,3,4])
```

**Which variant to use:**

| Use case | Variant | Why |
|----------|---------|-----|
| LC 876 (just find middle) | A (right-middle) | Problem asks for second middle on even |
| LC 234 (palindrome check) | B (left-middle) | Split: first half ends at slow, second half starts at slow.next |
| LC 143 (reorder list) | B (left-middle) | Same split logic |
| LC 148 (sort list) | B (left-middle) | Need clean split into two halves |

**The rule of thumb:** If you need to split the list into two halves and process them separately, use Variant B (left-middle) so the first half ends at `slow` and the second half starts at `slow.next`. Then set `slow.next = null` to sever the connection.

**Time:** O(n)
**Space:** O(1)

---

### Template 3: Find Cycle Entry Point (Floyd's Phase 2)

**Problem:** Given a linked list with a cycle, find the node where the cycle begins.

**Two phases:**
1. Phase 1: Detect the cycle (find meeting point)
2. Phase 2: Find the entry (reset one pointer to head, move both at speed 1)

```java
public ListNode detectCycle(ListNode head) {
    if (head == null || head.next == null) return null;

    ListNode slow = head;
    ListNode fast = head;

    // Phase 1: Find meeting point inside the cycle
    boolean hasCycle = false;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;

        if (slow == fast) {
            hasCycle = true;
            break;
        }
    }

    if (!hasCycle) return null;

    // Phase 2: Find cycle entry
    // Reset slow to head. Keep fast at meeting point.
    // Move both at speed 1. They meet at the cycle entry.
    slow = head;
    while (slow != fast) {
        slow = slow.next;
        fast = fast.next;   // speed 1, not 2
    }

    return slow; // or fast, they're the same node
}
```

**Why Phase 2 works (recap from Section 1):**

After Phase 1, the meeting point is `b` steps into the cycle. The cycle entry is `c - b` steps ahead from the meeting point (going forward). We proved that `a = k*c - b`, which means the distance from head to the entry equals the distance from the meeting point to the entry (modulo full cycle laps).

So a pointer starting at head and a pointer starting at the meeting point, both moving at speed 1, will arrive at the cycle entry simultaneously.

**Detailed dry run:**

```
List: 3 -> 1 -> 0 -> -4 -> (back to 1)
Nodes: [3, 1, 0, -4]
Cycle: -4.next = 1 (index 1)

a = 1 (head to cycle entry: 3 -> 1)
c = 3 (cycle: 1 -> 0 -> -4 -> back to 1)
```

Phase 1:
```
Initial:  slow=3, fast=3
Iter 1:   slow=1, fast=0    (slow: 3->1, fast: 3->1->0)
Iter 2:   slow=0, fast=1    (slow: 1->0, fast: 0->-4->1)
Iter 3:   slow=-4, fast=-4  (slow: 0->-4, fast: 1->0->-4)
Meeting point: -4
```

Check math: `b = 2` (cycle entry is 1, meeting point -4 is 2 steps into cycle: 1->0->-4)
`a = k*c - b = 1*3 - 2 = 1`. Correct (a=1).

Phase 2:
```
slow = head = 3, fast = -4
Iter 1: slow=1, fast=1   (slow: 3->1, fast: -4->1)
slow == fast -> return node 1
```

Cycle entry is node with value 1. Correct.

**Time:** O(n) — Phase 1 is O(n), Phase 2 is O(a) which is at most O(n).
**Space:** O(1)

---

### Template 4: Cycle Detection on Functional Graphs

**Problem:** Detect cycles in a sequence defined by a function `f(x)`, not a linked list.

**Key insight:** If `f: S -> S` maps a finite set to itself, then the sequence `x, f(x), f(f(x)), ...` must eventually repeat. Treat `x` as a "node" and `f(x)` as its "next pointer."

```java
// Generic structure for functional graph cycle detection
// Used for: Happy Number (LC 202), Find Duplicate (LC 287)

public boolean hasCycleInSequence(int start) {
    int slow = start;
    int fast = start;

    do {
        slow = next(slow);          // 1 application of f
        fast = next(next(fast));    // 2 applications of f

        if (slow == fast) {
            return true; // or find entry point
        }
    } while (fast != TERMINAL && next(fast) != TERMINAL);

    return false;
}

// For Happy Number: next(n) = sum of squares of digits
private int sumOfSquares(int n) {
    int sum = 0;
    while (n > 0) {
        int digit = n % 10;
        sum += digit * digit;
        n /= 10;
    }
    return sum;
}

// For Find Duplicate (LC 287): next(i) = nums[i]
// The array index is the "node", the value is the "next pointer"
```

**For finding the cycle entry in a functional graph:**

```java
// Phase 1: find meeting point
int slow = start;
int fast = start;
do {
    slow = f(slow);
    fast = f(f(fast));
} while (slow != fast);

// Phase 2: find entry
slow = start;
while (slow != fast) {
    slow = f(slow);
    fast = f(fast);
}
// slow == fast == cycle entry
return slow;
```

**Important:** The `do-while` loop (or advancing before the first comparison) is necessary here because `slow` and `fast` both start at the same value. A `while (slow != fast)` check at the top would exit immediately.

**Time:** O(lambda + mu) where `mu` is the distance to the cycle start and `lambda` is the cycle length. In practice O(n).
**Space:** O(1)

---

## 5. Real-World Applications

### 1. Deadlock Detection in Operating Systems

OS resource allocation graphs model threads and resources as nodes, with edges representing "holds" and "waits-for" relationships. A deadlock occurs when threads form a circular wait: A waits for B, B waits for C, C waits for A. Floyd's-style traversal on the wait-for graph detects this cycle without storing the entire graph state. Modern OS kernels (Linux, Windows) use variants of this for deadlock detection in mutex and semaphore systems.

### 2. Infinite Loop Detection in Programs

Static analysis tools (like those in compilers and linters) and runtime debuggers detect infinite loops by checking whether program state repeats. The tortoise-and-hare approach checks for state cycles without storing all visited states in memory. This is particularly useful for embedded systems with limited RAM where a HashSet of visited states would be prohibitively expensive.

### 3. Pseudorandom Number Generator (PRNG) Period Detection

PRNGs are deterministic functions that cycle. A linear congruential generator `x_{n+1} = (a * x_n + c) mod m` is a classic example. Floyd's algorithm finds the cycle length (period) of a PRNG, which is critical for cryptographic analysis. A short period means the PRNG is predictable and insecure. Pollard's Rho algorithm for integer factorization uses Floyd's cycle detection on a pseudorandom sequence `x_{n+1} = x_n^2 + c mod n` to find factors of large numbers. This is one of the most practically important applications of the algorithm.

### 4. Linked List Operations in Memory Management

Garbage collectors traverse object reference graphs to find live objects. In reference-counted GC systems (Python, Swift ARC), objects that reference each other in a cycle never reach a reference count of zero, causing memory leaks. Detecting these reference cycles requires cycle detection on the object graph. CPython's cyclic garbage collector uses a mark-and-sweep approach, but the cycle detection problem is the same one Floyd's algorithm solves.

### 5. Network Routing Loop Detection

TTL (Time to Live) in IP packets is a crude cycle detector: each router decrements TTL, and when it hits zero, the packet is dropped. This prevents packets from looping forever but doesn't identify the loop. More sophisticated routing protocols (like OSPF's loop-free alternate paths) use graph-theoretic cycle detection to find routing loops in network topologies before they cause problems.

### 6. Iterator Invalidation Detection

Some data structure libraries use sentinel-based cycle detection to verify iterator validity. If an iterator's internal linked list has been corrupted (e.g., a node's next pointer was overwritten), a cycle may have been introduced. A fast/slow traversal can detect this corruption without knowing the expected list length.

---

## 6. Problem Categories & Solutions

---

### Category A: Linked List Cycle Problems

---

#### LC 141 — Linked List Cycle (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Oracle, TCS

**Problem:** Given the head of a linked list, determine if the list has a cycle.

**Approach:** Template 1 directly. No modifications needed.

**Solution:**

```java
public class Solution {
    public boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) return false;

        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) return true;
        }

        return false;
    }
}
```

**Complexity:** Time O(n), Space O(1)

**Edge cases:**
- `head == null` → false
- Single node, no self-loop (`head.next == null`) → false
- Single node pointing to itself → fast = head.next.next = head, slow = head.next = head → they meet on first iteration → true

**Why not use HashSet?** HashSet works (O(n) space, O(n) time) but wastes memory. Fast/slow achieves the same result in O(1) space. In interviews, always mention both approaches and explain why fast/slow is preferred.

---

#### LC 142 — Linked List Cycle II (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Oracle, American Express

**Problem:** Given the head of a linked list with a cycle, return the node where the cycle begins. Return `null` if no cycle.

**Approach:** Template 3 (Floyd's Phase 1 + Phase 2).

**Solution:**

```java
public class Solution {
    public ListNode detectCycle(ListNode head) {
        if (head == null || head.next == null) return null;

        ListNode slow = head;
        ListNode fast = head;

        // Phase 1: Detect cycle
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                // Phase 2: Find entry point
                slow = head;
                while (slow != fast) {
                    slow = slow.next;
                    fast = fast.next; // speed 1 now
                }
                return slow;
            }
        }

        return null; // no cycle
    }
}
```

**Dry run with diagram:**

```
List: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> (back to 3)

Nodes by position:
pos 0: val=1
pos 1: val=2
pos 2: val=3  <-- cycle entry (a=2)
pos 3: val=4
pos 4: val=5
pos 5: val=6  -> next = node at pos 2

Cycle length c = 4 (3->4->5->6->back to 3)
```

Phase 1 trace:
```
Initial:  slow=1(pos0), fast=1(pos0)
Iter 1:   slow=2(pos1), fast=3(pos2)
Iter 2:   slow=3(pos2), fast=5(pos4)
Iter 3:   slow=4(pos3), fast=4(pos3)  <-- MEET at pos3 (val=4)
```

Meeting point: pos3, which is `b=1` step into the cycle (cycle starts at pos2).

Math check: `a = k*c - b = 1*4 - 1 = 3`? But `a=2`. Let me recount.

Actually: `a=2`, `b=1`, `c=4`. Check: `a = k*c - b` → `2 = k*4 - 1` → `k*4 = 3`? That doesn't work with integer k.

Let me retrace more carefully:

```
Cycle: 3(pos2) -> 4(pos3) -> 5(pos4) -> 6(pos5) -> 3(pos2)
c = 4

Phase 1:
slow travels: 1->2->3->4->5->6->3->4 (8 steps? No, let's count iterations)

Initial: slow=pos0, fast=pos0
Iter 1: slow=pos1, fast=pos2
Iter 2: slow=pos2, fast=pos4
Iter 3: slow=pos3, fast=pos2  (fast: pos4->pos5->pos2, two steps)
Iter 4: slow=pos4, fast=pos4  (slow: pos3->pos4, fast: pos2->pos3->pos4)
MEET at pos4 (val=5)
```

Meeting point: pos4, `b=2` steps into cycle (cycle entry pos2, then pos3, then pos4).

Math: `a=2`, `b=2`, `c=4`. Check: `a = k*c - b` → `2 = 1*4 - 2 = 2`. Correct.

Phase 2:
```
slow = head = pos0 (val=1)
fast = pos4 (val=5)

Iter 1: slow=pos1(val=2), fast=pos5(val=6)
Iter 2: slow=pos2(val=3), fast=pos2(val=3)  (fast: pos5->pos2, one step in cycle)
MEET at pos2 (val=3) = cycle entry
```

**Complexity:** Time O(n), Space O(1)

**Common mistake:** In Phase 2, forgetting to change fast's speed from 2 to 1. If you keep fast at speed 2, the math breaks and they won't meet at the entry.

---

#### LC 202 — Happy Number (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe, Airbnb, JPMorgan

**Problem:** A happy number is defined by the process: starting with any positive integer, replace the number by the sum of the squares of its digits, and repeat until the number equals 1 (happy) or loops endlessly in a cycle that does not include 1 (not happy). Return true if the number is happy.

**Key insight:** The sequence either reaches 1 or enters a cycle. This is exactly cycle detection on a functional graph where `f(n) = sum of squares of digits`.

**Approach 1: HashSet (intuitive)**

```java
public class Solution {
    public boolean isHappy(int n) {
        Set<Integer> seen = new HashSet<>();

        while (n != 1) {
            if (seen.contains(n)) return false; // cycle detected
            seen.add(n);
            n = sumOfSquares(n);
        }

        return true;
    }

    private int sumOfSquares(int n) {
        int sum = 0;
        while (n > 0) {
            int digit = n % 10;
            sum += digit * digit;
            n /= 10;
        }
        return sum;
    }
}
```

**Approach 2: Floyd's Fast/Slow (O(1) space)**

```java
public class Solution {
    public boolean isHappy(int n) {
        int slow = n;
        int fast = n;

        do {
            slow = sumOfSquares(slow);
            fast = sumOfSquares(sumOfSquares(fast));
        } while (slow != fast);

        // If they meet at 1, it's a happy number
        // If they meet at any other cycle, it's not
        return slow == 1;
    }

    private int sumOfSquares(int n) {
        int sum = 0;
        while (n > 0) {
            int digit = n % 10;
            sum += digit * digit;
            n /= 10;
        }
        return sum;
    }
}
```

**Why `do-while`?** Both slow and fast start at `n`. A `while (slow != fast)` check at the top would exit immediately since they're equal. The `do-while` ensures at least one iteration before checking.

**Trace for n=19:**
```
19 -> 1^2 + 9^2 = 1 + 81 = 82
82 -> 8^2 + 2^2 = 64 + 4 = 68
68 -> 6^2 + 8^2 = 36 + 64 = 100
100 -> 1^2 + 0^2 + 0^2 = 1
-> reaches 1, return true
```

**Trace for n=2 (not happy):**
```
2 -> 4 -> 16 -> 37 -> 58 -> 89 -> 145 -> 42 -> 20 -> 4 (cycle!)
```

The cycle for unhappy numbers always passes through 4. Floyd's will detect this cycle and since the meeting point won't be 1, return false.

**Which approach to use in interviews?** Mention both. HashSet is more readable. Floyd's demonstrates deeper understanding. If the interviewer asks about space optimization, switch to Floyd's.

**Complexity:** Time O(log n) per step (digit extraction), O(log n) total steps before cycle. Space O(1) for Floyd's, O(log n) for HashSet.

---

#### LC 457 — Circular Array Loop (Medium)

**Companies:** Amazon, Google, Microsoft

**Problem:** You have a circular array `nums` of positive and negative integers. A cycle exists if you can start at some index and follow the jumps (each element tells you how many steps to move forward or backward) and return to the starting index. The cycle must have more than one element and all movements must be in the same direction (all positive or all negative).

**Approach:** For each starting index, run fast/slow pointer detection. The "next" function is `next(i) = ((i + nums[i]) % n + n) % n` (modular arithmetic to handle wrapping and negative values).

```java
public class Solution {
    public boolean circularArrayLoop(int[] nums) {
        int n = nums.length;

        for (int i = 0; i < n; i++) {
            // Skip if this index was already determined to be non-cyclic
            if (nums[i] == 0) continue;

            int slow = i;
            int fast = i;

            // All movements must be in the same direction
            // Check: nums[slow] and nums[fast] must have the same sign
            do {
                slow = getNext(nums, slow);
                fast = getNext(nums, fast);
                if (fast == -1) break; // direction change detected
                fast = getNext(nums, fast);
                if (fast == -1) break;
            } while (slow != fast);

            if (fast != -1 && slow == fast) {
                return true;
            }

            // Mark all nodes in this path as non-cyclic (optimization)
            // Set to 0 to skip in future iterations
            int j = i;
            while (nums[j] != 0 && isSameDirection(nums, i, j)) {
                int next = getNext(nums, j);
                nums[j] = 0;
                j = next;
            }
        }

        return false;
    }

    private int getNext(int[] nums, int i) {
        int n = nums.length;
        boolean positive = nums[i] > 0;
        int next = ((i + nums[i]) % n + n) % n;

        // Cycle of length 1 is not valid
        if (next == i) return -1;

        // Direction must be consistent
        if (positive != (nums[next] > 0)) return -1;

        return next;
    }

    private boolean isSameDirection(int[] nums, int i, int j) {
        return (nums[i] > 0) == (nums[j] > 0);
    }
}
```

**Key differences from standard cycle detection:**
1. The "next" function can return -1 (invalid) if direction changes or self-loop detected.
2. Must verify all movements in the cycle are in the same direction.
3. Optimization: mark visited non-cyclic nodes as 0 to avoid reprocessing.

**Complexity:** Time O(n), Space O(1)

---

### Category B: Finding Middle / Structural Problems

---

#### LC 876 — Middle of the Linked List (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given the head of a singly linked list, return the middle node. If there are two middle nodes, return the second middle node.

**Approach:** Template 2, Variant A (right-middle).

```java
public class Solution {
    public ListNode middleNode(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }
}
```

**Trace for [1,2,3,4,5] (odd length):**
```
Initial:  slow=1, fast=1
Iter 1:   slow=2, fast=3
Iter 2:   slow=3, fast=5
Iter 3:   fast.next == null -> exit
Return: slow=3 (correct middle)
```

**Trace for [1,2,3,4,5,6] (even length):**
```
Initial:  slow=1, fast=1
Iter 1:   slow=2, fast=3
Iter 2:   slow=3, fast=5
Iter 3:   slow=4, fast=null (5.next.next = 6.next = null)
           Actually: fast=5, fast.next=6, fast.next.next=null -> fast=null
           Loop check: fast(null) != null -> false -> exit
Return: slow=4 (second middle, correct per problem statement)
```

Wait, let me retrace iter 3:
```
After iter 2: slow=3, fast=5
Check: fast(5) != null AND fast.next(6) != null -> true
Iter 3: slow=4, fast = 5.next.next = 6.next = null
After iter 3: slow=4, fast=null
Check: fast(null) != null -> false -> exit
Return: slow=4
```

For [1,2,3,4,5,6], the two middles are 3 and 4. The problem asks for the second middle (4). Variant A returns 4. Correct.

**Complexity:** Time O(n), Space O(1)

---

#### LC 234 — Palindrome Linked List (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe, Oracle

**Problem:** Given the head of a singly linked list, return true if it's a palindrome.

**Approach:** Three steps:
1. Find the middle (Template 2, Variant B — left-middle)
2. Reverse the second half
3. Compare first half with reversed second half
4. (Optional) Restore the list

```java
public class Solution {
    public boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) return true;

        // Step 1: Find left-middle
        ListNode slow = head;
        ListNode fast = head;

        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        // slow is at left-middle

        // Step 2: Reverse second half
        // slow.next is the start of the second half
        ListNode secondHalf = reverseList(slow.next);
        slow.next = null; // sever the connection (optional but clean)

        // Step 3: Compare
        ListNode p1 = head;
        ListNode p2 = secondHalf;
        boolean isPalin = true;

        while (p2 != null) { // second half may be shorter by 1 for odd length
            if (p1.val != p2.val) {
                isPalin = false;
                break;
            }
            p1 = p1.next;
            p2 = p2.next;
        }

        // Step 4: Restore (good practice, especially if list must remain intact)
        slow.next = reverseList(secondHalf);

        return isPalin;
    }

    private ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        return prev;
    }
}
```

**Trace for [1,2,2,1]:**
```
Find middle (Variant B):
Initial: slow=1, fast=1
Check: fast.next(2) != null AND fast.next.next(2) != null -> true
Iter 1: slow=2(pos1), fast=2(pos2)
Check: fast.next(1) != null AND fast.next.next(null) != null -> false -> exit
slow = 2(pos1) = left-middle

Second half starts at slow.next = 2(pos2)
Reverse [2,1] -> [1,2]

Compare:
p1=1(pos0), p2=1(reversed pos3): 1==1 ok
p1=2(pos1), p2=2(reversed pos2): 2==2 ok
p2=null -> exit
return true
```

**Trace for [1,2,3,2,1]:**
```
Find middle (Variant B):
Initial: slow=1, fast=1
Iter 1: slow=2, fast=3
Check: fast.next(2) != null AND fast.next.next(1) != null -> true
Iter 2: slow=3, fast=1(last)
Check: fast.next(null) != null -> false -> exit
slow = 3 (middle of odd-length list)

Second half starts at slow.next = 2
Reverse [2,1] -> [1,2]

Compare:
p1=1, p2=1: ok
p1=2, p2=2: ok
p2=null -> exit
return true
```

**Why Variant B here?** For [1,2,2,1], Variant A would give slow=2(pos2) as the middle. Then second half = [1], and first half = [1,2,2]. Comparing [1,2,2] with [1] would only check the first element and return true incorrectly. Variant B gives slow=2(pos1), second half=[2,1], first half=[1,2]. Comparing [1,2] with reversed [1,2] works correctly.

**Complexity:** Time O(n), Space O(1)

---

#### LC 143 — Reorder List (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given the head of a singly linked list `L0 -> L1 -> ... -> Ln-1 -> Ln`, reorder it to `L0 -> Ln -> L1 -> Ln-1 -> L2 -> Ln-2 -> ...`. Do it in-place.

**Approach:** Three steps:
1. Find the middle (Template 2, Variant B)
2. Reverse the second half
3. Merge the two halves by interleaving

```java
public class Solution {
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) return;

        // Step 1: Find left-middle
        ListNode slow = head;
        ListNode fast = head;

        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Step 2: Reverse second half
        ListNode secondHalf = reverseList(slow.next);
        slow.next = null; // sever first half from second

        // Step 3: Interleave merge
        ListNode first = head;
        ListNode second = secondHalf;

        while (second != null) {
            ListNode firstNext = first.next;
            ListNode secondNext = second.next;

            first.next = second;
            second.next = firstNext;

            first = firstNext;
            second = secondNext;
        }
    }

    private ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        return prev;
    }
}
```

**Trace for [1,2,3,4,5]:**
```
Find middle (Variant B): slow=2(pos1)
First half: [1,2], Second half starts at 3: [3,4,5]
Reverse [3,4,5] -> [5,4,3]

Interleave:
first=1, second=5
  1.next=5, 5.next=2, advance: first=2, second=4
first=2, second=4
  2.next=4, 4.next=3, advance: first=3, second=3
first=3, second=3
  3.next=3, 3.next=3 (self-loop? No: second=3(original pos2), first=3(original pos2))
  
Wait, let me retrace with actual node identities:
Nodes: A(1) -> B(2) -> C(3) -> D(4) -> E(5)

Find middle: slow=B (left-middle of [A,B,C,D,E])
First half: A->B, B.next=null
Second half: C->D->E
Reverse second: E->D->C

Interleave:
first=A, second=E
  firstNext=B, secondNext=D
  A.next=E, E.next=B
  first=B, second=D

first=B, second=D
  firstNext=null (B.next was set to null), secondNext=C
  B.next=D, D.next=null
  first=null, second=C

second=C, but first=null -> loop exits (while second != null is true, but first is null)
```

Hmm, the loop condition should be `while (second != null)` but we also need `first != null`. Let me check: for odd-length lists, the first half is shorter. After the last interleave, `first` becomes null but `second` still has one node. The loop exits because... actually `second` is not null yet.

The fix: the loop should be `while (first != null && second != null)`. For odd-length lists, the first half has `floor(n/2)` nodes and the second half has `ceil(n/2)` nodes after reversal. The extra node in the second half just stays at the end naturally.

```java
// Corrected merge loop:
while (first != null && second != null) {
    ListNode firstNext = first.next;
    ListNode secondNext = second.next;

    first.next = second;
    if (firstNext != null) second.next = firstNext;

    first = firstNext;
    second = secondNext;
}
```

Result for [1,2,3,4,5]: A->E->B->D->C = [1,5,2,4,3]. Correct.

**Complexity:** Time O(n), Space O(1)

---

#### LC 148 — Sort List (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given the head of a linked list, return the list sorted in ascending order.

**Approach:** Merge sort. Use fast/slow to find the middle (split point), recursively sort each half, then merge.

```java
public class Solution {
    public ListNode sortList(ListNode head) {
        // Base case: empty or single node
        if (head == null || head.next == null) return head;

        // Find middle and split
        ListNode mid = findMiddle(head);
        ListNode rightHead = mid.next;
        mid.next = null; // sever

        // Recursively sort both halves
        ListNode left = sortList(head);
        ListNode right = sortList(rightHead);

        // Merge sorted halves
        return merge(left, right);
    }

    private ListNode findMiddle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        // Variant B: left-middle, so split is even
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }

    private ListNode merge(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;

        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                curr.next = l1;
                l1 = l1.next;
            } else {
                curr.next = l2;
                l2 = l2.next;
            }
            curr = curr.next;
        }

        curr.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }
}
```

**Why fast/slow for merge sort?** You need to split the list into two halves without knowing the length. Fast/slow does this in one pass. The alternative (count length, then walk to n/2) requires two passes.

**Complexity:** Time O(n log n), Space O(log n) for recursion stack. The iterative bottom-up merge sort achieves O(1) space but is significantly more complex to implement.

---

#### LC 19 — Remove Nth Node From End of List (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given the head of a linked list, remove the nth node from the end of the list and return its head.

**Approach:** Two pointers with an N-gap. This is not strictly fast/slow (both move at speed 1), but it's the same-direction two-pointer technique from Topic 2. Advance `fast` by N steps first, then move both until `fast` reaches the end. `slow` will be just before the node to delete.

```java
public class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode slow = dummy;
        ListNode fast = dummy;

        // Advance fast by n+1 steps (so slow stops one before the target)
        for (int i = 0; i <= n; i++) {
            fast = fast.next;
        }

        // Move both until fast reaches null
        while (fast != null) {
            slow = slow.next;
            fast = fast.next;
        }

        // slow.next is the node to delete
        slow.next = slow.next.next;

        return dummy.next;
    }
}
```

**Why dummy node?** If the node to delete is the head (n equals the list length), `slow` needs to be "before" the head. The dummy node handles this edge case cleanly.

**Trace for [1,2,3,4,5], n=2:**
```
dummy -> 1 -> 2 -> 3 -> 4 -> 5 -> null

Advance fast by n+1=3 steps:
fast: dummy -> 1 -> 2 -> 3

Move both until fast=null:
slow=dummy, fast=3
Iter 1: slow=1, fast=4
Iter 2: slow=2, fast=5
Iter 3: slow=3, fast=null -> exit

slow.next = 4, slow.next.next = 5
slow.next = 5 (delete node 4)

Result: 1 -> 2 -> 3 -> 5
```

Node 4 is the 2nd from the end. Correct.

**Complexity:** Time O(n), Space O(1)

---

### Category C: Advanced / Mathematical

---

#### LC 287 — Find the Duplicate Number (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

**Problem:** Given an array `nums` containing `n+1` integers where each integer is in the range `[1, n]`, there is exactly one repeated number. Find it. You must not modify the array and use only O(1) extra space.

**This is the most conceptually challenging application of Floyd's algorithm. Read carefully.**

**The key insight:** Treat the array as a linked list where the value at each index is the "next pointer."

- Index 0 points to `nums[0]`
- Index 1 points to `nums[1]`
- Index `i` points to `nums[i]`

Since all values are in `[1, n]` and there are `n+1` indices (0 through n), every index maps to a valid index. The duplicate value means two different indices point to the same index. This is exactly the condition that creates a cycle in a linked list (two nodes pointing to the same "next" node).

The duplicate number is the cycle entry point.

**Why index 0 is always the "head" (outside the cycle):**
- Values are in `[1, n]`, so `nums[i] >= 1` for all `i`.
- Index 0 is never pointed to by any other index (since no value equals 0).
- So index 0 is always the entry point into the functional graph, and it's always outside the cycle.

**Detailed dry run:**

```
nums = [1, 3, 4, 2, 2]
Indices: 0  1  2  3  4

Mapping (index -> nums[index]):
0 -> 1
1 -> 3
2 -> 4
3 -> 2
4 -> 2

Draw as linked list starting from index 0:
0 -> 1 -> 3 -> 2 -> 4 -> 2 (cycle! index 4 and index 3 both point to index 2)
                    ^
                    cycle entry = index 2

The duplicate number is 2 (the value at the cycle entry index).
```

Visual:
```
0 -> 1 -> 3 -> 2 -> 4
               ^         |
               |_________|
               (4 points back to 2)
```

Phase 1 (find meeting point):
```
slow = 0, fast = 0

Iter 1: slow = nums[0] = 1
        fast = nums[nums[0]] = nums[1] = 3
        slow=1, fast=3

Iter 2: slow = nums[1] = 3
        fast = nums[nums[3]] = nums[2] = 4
        slow=3, fast=4

Iter 3: slow = nums[3] = 2
        fast = nums[nums[4]] = nums[2] = 4
        slow=2, fast=4

Iter 4: slow = nums[2] = 4
        fast = nums[nums[4]] = nums[2] = 4
        slow=4, fast=4  <-- MEET
```

Meeting point: index 4.

Phase 2 (find cycle entry):
```
slow = 0 (reset to start)
fast = 4 (stays at meeting point)

Iter 1: slow = nums[0] = 1
        fast = nums[4] = 2
        slow=1, fast=2

Iter 2: slow = nums[1] = 3
        fast = nums[2] = 4
        slow=3, fast=4

Iter 3: slow = nums[3] = 2
        fast = nums[4] = 2
        slow=2, fast=2  <-- MEET at index 2
```

Cycle entry is index 2. The duplicate number is `nums[2] = 4`? No wait.

The cycle entry is the INDEX where the cycle begins. The duplicate VALUE is the value that two indices point to. Index 3 and index 4 both have value 2, meaning they both "point to" index 2. So index 2 is the cycle entry, and the duplicate number is 2 (the value that appears twice in the array, which is also the index of the cycle entry).

Actually, the duplicate number equals the cycle entry index. Here, cycle entry = index 2, and the duplicate number = 2. They're the same.

**Why?** The duplicate value `d` appears at two positions in the array. Both positions have `nums[i] = d`, meaning both "point to" index `d`. So index `d` has two incoming edges, making it the cycle entry. The cycle entry index equals the duplicate value.

**Second example:**

```
nums = [3, 1, 3, 4, 2]
Indices: 0  1  2  3  4

Mapping:
0 -> 3
1 -> 1  (self-loop! but this is a valid cycle of length 1... wait)
2 -> 3
3 -> 4
4 -> 2

Starting from 0: 0 -> 3 -> 4 -> 2 -> 3 (cycle!)
                              ^
                              cycle entry = index 3

Duplicate = 3 (value 3 appears at indices 0 and 2, both pointing to index 3)
```

Phase 1:
```
slow=0, fast=0
Iter 1: slow=nums[0]=3, fast=nums[nums[0]]=nums[3]=4
Iter 2: slow=nums[3]=4, fast=nums[nums[4]]=nums[2]=3
Iter 3: slow=nums[4]=2, fast=nums[nums[3]]=nums[4]=2
slow=2, fast=2 -> MEET at index 2
```

Phase 2:
```
slow=0, fast=2
Iter 1: slow=nums[0]=3, fast=nums[2]=3
slow=3, fast=3 -> MEET at index 3
```

Cycle entry = 3. Duplicate = 3. Correct.

**Solution:**

```java
public class Solution {
    public int findDuplicate(int[] nums) {
        int slow = 0;
        int fast = 0;

        // Phase 1: Find meeting point
        // "next" of index i is nums[i]
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);

        // Phase 2: Find cycle entry (= duplicate number)
        slow = 0;
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }

        return slow; // or fast, same value
    }
}
```

**Why `do-while` in Phase 1?** Both start at index 0. If we check `slow != fast` first, we'd exit immediately. We need at least one step before comparing.

**Why this satisfies the constraints?**
- No array modification (we only read values)
- O(1) extra space (just two integer pointers)
- O(n) time

**Alternative approaches (and why they're worse for this problem):**
- Sorting: O(n log n) time, O(1) space, but modifies the array (or needs a copy)
- HashSet: O(n) time, O(n) space — violates the space constraint
- Bit manipulation / sum formula: only works if there's exactly one duplicate and it appears exactly twice

**Complexity:** Time O(n), Space O(1)

---

#### LC 2095 — Delete the Middle Node of a Linked List (Medium)

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given the head of a linked list, delete the middle node and return the modified head. The middle node is the `floor(n/2)`-th node (0-indexed).

**Approach:** Find the node just before the middle using a modified fast/slow, then delete the middle.

```java
public class Solution {
    public ListNode deleteMiddle(ListNode head) {
        // Edge case: single node, delete it
        if (head == null || head.next == null) return null;

        ListNode slow = head;
        ListNode fast = head.next.next; // start fast 2 ahead so slow stops before middle

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // slow is now one node before the middle
        slow.next = slow.next.next;

        return head;
    }
}
```

**Why start fast at `head.next.next`?** We want `slow` to stop one node BEFORE the middle, not at the middle. By giving fast a 2-step head start, slow ends up one position earlier than the standard middle-finding algorithm.

**Trace for [1,2,3,4,5]:**
```
Middle is floor(5/2) = 2nd node (0-indexed) = node with value 3.
We want slow to stop at node 2 (value 2).

Initial: slow=1(pos0), fast=4(pos3)  [fast starts at head.next.next = pos2? No: head.next=2, head.next.next=3]

Wait: head=1, head.next=2, head.next.next=3
Initial: slow=1(pos0), fast=3(pos2)

Check: fast(3) != null AND fast.next(4) != null -> true
Iter 1: slow=2(pos1), fast=5(pos4)
Check: fast(5) != null AND fast.next(null) != null -> false -> exit

slow = 2(pos1)
slow.next = 3(pos2), slow.next.next = 4(pos3)
slow.next = 4

Result: [1,2,4,5] (node 3 deleted)
```

Middle of [1,2,3,4,5] is index 2 (value 3). Deleted. Correct.

**Complexity:** Time O(n), Space O(1)

---

#### LC 61 — Rotate List (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given the head of a linked list, rotate the list to the right by `k` places.

**Approach:** Find the length, compute the effective rotation (`k % length`), find the new tail (at position `length - k - 1`), and reconnect.

Fast/slow isn't the primary technique here, but finding the tail and length efficiently uses a single traversal.

```java
public class Solution {
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null || k == 0) return head;

        // Find length and tail
        int length = 1;
        ListNode tail = head;
        while (tail.next != null) {
            tail = tail.next;
            length++;
        }

        // Effective rotation
        k = k % length;
        if (k == 0) return head;

        // Find new tail: (length - k - 1) steps from head
        ListNode newTail = head;
        for (int i = 0; i < length - k - 1; i++) {
            newTail = newTail.next;
        }

        // New head is newTail.next
        ListNode newHead = newTail.next;

        // Reconnect
        newTail.next = null;
        tail.next = head;

        return newHead;
    }
}
```

**Trace for [1,2,3,4,5], k=2:**
```
length=5, tail=5
k = 2 % 5 = 2
New tail at position 5-2-1=2 (0-indexed): node with value 3

newTail=3, newHead=4
3.next=null, 5.next=1

Result: 4->5->1->2->3
```

Rotating [1,2,3,4,5] right by 2: last 2 elements [4,5] move to front. Result [4,5,1,2,3]. Correct.

**Complexity:** Time O(n), Space O(1)

---

## 7. Common Mistakes & Edge Cases

### Common Mistakes

| Mistake | Why it happens | How to fix |
|---------|---------------|------------|
| NullPointerException on `fast.next` | Only checking `fast != null` before accessing `fast.next.next` | Always use TWO null checks: `while (fast != null && fast.next != null)` |
| Wrong middle for even-length list | Using Variant A when Variant B is needed (or vice versa) | Variant A (`fast != null && fast.next != null`) gives right-middle. Variant B (`fast.next != null && fast.next.next != null`) gives left-middle. Choose based on whether you need to split the list. |
| Forgetting Phase 2 of Floyd's | Only detecting the cycle, not finding the entry | After meeting, reset one pointer to head, move both at speed 1 |
| Keeping fast at speed 2 in Phase 2 | Muscle memory from Phase 1 | Phase 2 explicitly uses `fast = fast.next` (speed 1) |
| Modifying list without restoring | Palindrome check reverses half the list permanently | Reverse again after comparison if the list must remain intact |
| Applying Floyd's to non-functional graphs | Array values out of range, or multiple "next" pointers | Floyd's requires every element to map to exactly one valid "next" element |
| Off-by-one in "Nth from end" | Advancing fast N times instead of N+1 | Advance fast N+1 times (or use a dummy head and advance N times) so slow stops one before the target |
| Using `while (slow != fast)` in Phase 1 | Both start equal, loop exits immediately | Use `do-while` or advance both once before the loop check |
| Not handling self-loop (cycle of length 1) | `fast.next.next == fast` but `fast.next == fast` too | Check `if (slow == fast)` after advancing, not before |
| Comparing node values instead of references | `slow.val == fast.val` instead of `slow == fast` | Cycle detection requires reference equality (`==`), not value equality |

### Edge Cases to Always Test

**Empty list (`head == null`):**
- Cycle detection: return false
- Find middle: return null
- Find cycle entry: return null

**Single node, no self-loop (`head.next == null`):**
- Cycle detection: return false (fast.next is null, loop doesn't execute)
- Find middle: return head

**Single node pointing to itself (`head.next == head`):**
- Cycle detection: fast = head.next.next = head, slow = head.next = head. After one iteration, slow == fast. Return true.
- Find cycle entry: should return head

**Cycle at the very beginning (head is the cycle entry):**
- `a = 0`. Phase 2: slow starts at head, fast at meeting point. They meet immediately at head (after 0 steps for slow, `c` steps for fast which is a full cycle). Actually they meet after `c - b` steps for fast and `c - b` steps for slow... but slow starts at head which IS the entry. They meet at head.

**Cycle at the very end (tail points back to some middle node):**
- Standard case, no special handling needed.

**Even vs odd length (affects middle calculation):**
- Always trace through both cases when implementing Template 2.

**List of length 2:**
- `[1, 2]` with no cycle: fast = head.next.next = null after one step. Loop exits.
- `[1, 2]` with cycle (2.next = 1): fast = 1 after one step, slow = 2. fast = 2 after second step, slow = 1. They don't meet yet... actually:
  ```
  Initial: slow=1, fast=1
  Iter 1: slow=2, fast=1 (fast: 1->2->1)
  Iter 2: slow=1, fast=1 (slow: 2->1, fast: 1->2->1)
  slow==fast -> cycle detected
  ```

**k > length in rotate list:**
- Always compute `k = k % length` first. If `k == 0` after mod, return head unchanged.

---

## 8. Pattern Comparison

### Fast & Slow vs Other Patterns from This Series

| Aspect | Fast & Slow Pointers | Two Pointers (Opposite Ends) | Sliding Window |
|--------|---------------------|------------------------------|----------------|
| Direction | Same direction, different speed | Opposite ends, converge toward center | Same direction, same speed (window expands/contracts) |
| Primary data structure | Linked lists, functional graphs | Sorted arrays, strings | Arrays, strings |
| Key use cases | Cycles, midpoints, functional graph cycles | Pairs summing to target, palindromes, container problems | Subarray/substring with constraint |
| Space complexity | O(1) | O(1) | O(1) to O(k) for window contents |
| Requires sorted input | No | Often yes (for two-sum style) | No |
| Pointer movement | Asymmetric (1 step vs 2 steps) | Symmetric (both move 1 step) | Asymmetric (right expands, left contracts) |
| Termination condition | Pointers meet (cycle) or fast reaches null | Pointers cross or meet | Right pointer reaches end |

*Reference: Topic 1 (Sliding Window) and Topic 2 (Two Pointers) for the other patterns.*

### Fast & Slow vs HashSet for Cycle Detection

| | Fast & Slow | HashSet |
|--|------------|---------|
| Time | O(n) | O(n) |
| Space | O(1) | O(n) |
| Finds cycle entry | Yes (Phase 2) | Yes (first repeated element) |
| Code complexity | Higher | Lower |
| When to prefer | Space-constrained, or interviewer asks for O(1) space | Readability matters, space not constrained |

In interviews, always mention both. Implement HashSet first if you're unsure, then optimize to fast/slow.

### Fast & Slow vs Two-Pass (Count Length, Then Find Middle)

| | Fast & Slow | Two-Pass |
|--|------------|---------|
| Passes | 1 | 2 |
| Space | O(1) | O(1) |
| Code complexity | Slightly higher | Simpler |
| When to prefer | Single-pass requirement, or list is being modified | Clarity matters, two passes acceptable |

### Fast & Slow (LC 287) vs Other Approaches for Find Duplicate

| Approach | Time | Space | Modifies array | Notes |
|----------|------|-------|----------------|-------|
| Floyd's (fast/slow) | O(n) | O(1) | No | Optimal, but requires understanding functional graphs |
| Sort + scan | O(n log n) | O(1) or O(n) | Yes (in-place sort) | Simpler but modifies array |
| HashSet | O(n) | O(n) | No | Simple but uses extra space |
| Binary search on value | O(n log n) | O(1) | No | Count elements <= mid, use pigeonhole |
| Bit manipulation | O(n) | O(1) | No | Only works if duplicate appears exactly twice |

Floyd's is the only approach that achieves O(n) time, O(1) space, and doesn't modify the array.

---

## 9. Quick Reference Cheat Sheet

### Decision Flowchart

```
Problem involves a linked list or sequence?
    |
    +-- "Has a cycle?" or "Detect loop"
    |       -> Template 1 (Cycle Detection)
    |
    +-- "Find cycle start" or "Where does cycle begin"
    |       -> Template 3 (Floyd's Phase 1 + Phase 2)
    |
    +-- "Find middle" or "Split list in half"
    |       -> Template 2 (Find Middle)
    |       -> Variant A if problem asks for right-middle
    |       -> Variant B if you need to split the list
    |
    +-- "Happy number" or "sequence repeats" or "find duplicate in [1,n] array"
            -> Template 4 (Functional Graph)
            -> If need entry point: add Phase 2
```

### Template Selector

| Problem says... | Use |
|----------------|-----|
| "linked list has a cycle" | Template 1 |
| "detect loop" | Template 1 |
| "find where cycle begins" | Template 3 |
| "find cycle entry" | Template 3 |
| "find middle of linked list" | Template 2 |
| "palindrome linked list" | Template 2 (Variant B) + reverse |
| "reorder list" | Template 2 (Variant B) + reverse + merge |
| "sort linked list" | Template 2 (Variant B) + merge sort |
| "happy number" | Template 4 |
| "find duplicate in array [1,n]" | Template 4 + Phase 2 |
| "circular array loop" | Template 4 with custom next() |

### Null-Check Patterns (the #1 source of bugs)

```java
// CORRECT: Two null checks before advancing fast
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
}

// WRONG: Only one null check (NPE when fast is last node)
while (fast != null) {
    fast = fast.next.next; // NPE if fast.next == null
}

// CORRECT: Variant B middle-finding
while (fast.next != null && fast.next.next != null) {
    // Note: fast itself is never null here (starts at head, never set to null)
    // But fast.next or fast.next.next might be null
}

// CORRECT: do-while for functional graphs (both start equal)
do {
    slow = f(slow);
    fast = f(f(fast));
} while (slow != fast);

// WRONG: while for functional graphs (exits immediately)
while (slow != fast) { // slow == fast at start -> never enters loop
    ...
}
```

### Time/Space Complexity Summary

| Problem | Time | Space | Template |
|---------|------|-------|---------|
| LC 141 — Linked List Cycle | O(n) | O(1) | 1 |
| LC 142 — Linked List Cycle II | O(n) | O(1) | 3 |
| LC 202 — Happy Number | O(log n) per step | O(1) | 4 |
| LC 457 — Circular Array Loop | O(n) | O(1) | 4 |
| LC 876 — Middle of Linked List | O(n) | O(1) | 2 |
| LC 234 — Palindrome Linked List | O(n) | O(1) | 2 + reverse |
| LC 143 — Reorder List | O(n) | O(1) | 2 + reverse + merge |
| LC 148 — Sort List | O(n log n) | O(log n) | 2 + merge sort |
| LC 19 — Remove Nth From End | O(n) | O(1) | 2-pointer gap |
| LC 287 — Find Duplicate Number | O(n) | O(1) | 4 + Phase 2 |
| LC 2095 — Delete Middle Node | O(n) | O(1) | 2 (modified) |
| LC 61 — Rotate List | O(n) | O(1) | length + reconnect |

### Key Formulas

```
Floyd's meeting point math:
  a = distance from head to cycle entry
  b = distance from cycle entry to meeting point
  c = cycle length
  
  At meeting: a = k*c - b  (for some integer k >= 1)
  
  Consequence: pointer from head and pointer from meeting point
               both moving at speed 1 meet at cycle entry

Middle finding:
  Variant A: while(fast != null && fast.next != null)
             -> right-middle (second middle for even length)
  
  Variant B: while(fast.next != null && fast.next.next != null)
             -> left-middle (first middle for even length)

Array as linked list (LC 287):
  next(i) = nums[i]
  Duplicate value = cycle entry index
  Start traversal from index 0 (always outside cycle since values >= 1)
```

---

## 10. Practice Roadmap

### Week 1 — Foundation (Easy problems, 15 min each)

| Problem | Key concept | Template |
|---------|------------|---------|
| LC 141 — Linked List Cycle | Basic cycle detection | 1 |
| LC 876 — Middle of Linked List | Basic middle finding | 2 |
| LC 202 — Happy Number | Functional graph cycle | 4 |
| LC 234 — Palindrome Linked List | Middle + reverse + compare | 2 + reverse |

**Goal:** Get the null-check patterns automatic. You should be able to write Template 1 and Template 2 from memory without thinking about it.

**After each problem:** Write the solution without looking at notes. If you can't, review the template and try again the next day.

---

### Week 2 — Core Medium (25 min each)

| Problem | Key concept | Template |
|---------|------------|---------|
| LC 142 — Linked List Cycle II | Floyd's Phase 2, math proof | 3 |
| LC 19 — Remove Nth From End | Two-pointer gap | 2-pointer gap |
| LC 143 — Reorder List | Middle + reverse + interleave | 2 + reverse + merge |
| LC 2095 — Delete Middle Node | Modified middle finding | 2 (modified) |

**Goal:** Understand Floyd's Phase 2 math well enough to explain it to an interviewer. Practice the composite problems (234, 143) until the three-step structure is automatic.

**Focus area:** LC 142. If you can explain WHY Phase 2 works (not just that it works), you'll stand out in interviews.

---

### Week 3 — Advanced (30 min each)

| Problem | Key concept | Template |
|---------|------------|---------|
| LC 287 — Find Duplicate Number | Array as functional graph | 4 + Phase 2 |
| LC 148 — Sort List | Merge sort with fast/slow split | 2 + merge sort |
| LC 61 — Rotate List | Length + reconnect | length traversal |
| LC 457 — Circular Array Loop | Custom next() with direction check | 4 (custom) |

**Goal:** LC 287 is the hardest conceptual leap. Spend extra time on the dry run until you can draw the array-as-linked-list mapping from scratch. LC 148 tests whether you can combine fast/slow with a classic algorithm.

---

### Total: 12 problems over 3 weeks

**Mastery check:** You've mastered this pattern when you can:
1. Write all four templates from memory
2. Explain Floyd's Phase 2 math proof without notes
3. Draw the array-as-linked-list mapping for LC 287 from scratch
4. Identify which template to use within 30 seconds of reading a problem
5. Handle all edge cases (null, single node, self-loop, even/odd length) without prompting

---

*Next: Topic 4 will cover Merge Intervals. The fast/slow pattern appears again in Topic 8 (Linked List operations) as a building block for more complex problems.*
