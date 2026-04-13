# Topic 9: Linked List Patterns

Document 9 of 20 in the FAANG DSA Prep series.

---

## Overview

Linked list problems test pointer manipulation under pressure. You can't index into a node. You can't look ahead without traversing. Every operation requires you to hold multiple references simultaneously and update them in exactly the right order, or you lose your list.

Topic 3 (Fast & Slow Pointers) already covered cycle detection, midpoint finding, and the two-pointer gap technique. This document covers the remaining linked list patterns:

- **In-place reversal** (full list, partial, k-groups)
- **Merge operations** (two sorted lists, k sorted lists)
- **Partitioning and rearrangement**
- **Dummy node technique**
- **Deep copy with random pointers**

Master these five patterns and you can handle every linked list problem that appears in FAANG interviews.

**Top companies asking linked list problems:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

---

## Core Concept

### Singly vs Doubly Linked Lists

Singly linked lists appear in 90%+ of interview problems. Each node has a `val` and a `next` pointer. That's it. You can only traverse forward.

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}
```

Doubly linked lists (with a `prev` pointer) appear mainly in LRU Cache (Topic 6, LC 146) and flatten problems. When you see "doubly linked list" in an interview, the interviewer usually wants you to implement it from scratch or use it as part of a larger data structure.

**The key constraint of singly linked lists:** you can't go backward. This forces you to plan ahead. Before you change `curr.next`, you must save `curr.next` somewhere, or you've lost the rest of your list forever.

### The Dummy Node Trick

The dummy node is the single most useful technique in linked list problems. Create a fake head node before your actual list. Build your result by attaching nodes after the dummy. Return `dummy.next` at the end.

Why it works: the first node in a linked list is always a special case. Without a dummy, you need separate logic for "inserting at head" vs "inserting in the middle." With a dummy, every insertion is a middle insertion. The code becomes uniform.

```java
ListNode dummy = new ListNode(0);
ListNode curr = dummy;
// ... build list by setting curr.next = someNode, then curr = curr.next
return dummy.next;
```

Use a dummy node whenever:
- You might insert/delete at the head
- You're building a new list from scratch
- You're merging multiple lists

### Five Core Patterns

1. **In-Place Reversal** — reverse full list, reverse between positions, reverse in k-groups
2. **Merge / Weave** — merge two sorted lists, merge k sorted lists
3. **Partition / Rearrange** — partition around a value, odd-even rearrangement
4. **Dummy Node** — simplify head manipulation in any operation
5. **Copy / Clone** — deep copy with random pointers

### The Universal Interview Tip: DRAW IT

Every linked list problem becomes clearer with a diagram. Before writing a single line of code, draw boxes for nodes and arrows for pointers. Then trace through your algorithm step by step on paper. This catches off-by-one errors and lost-reference bugs before they happen.

```
Before reversal:
[1] -> [2] -> [3] -> [4] -> null

After reversal:
[4] -> [3] -> [2] -> [1] -> null
```

---

## ELI5

**Reversal:** Imagine a line of people holding hands, each person facing the back of the person in front. You want them all to turn around. You go person by person: you tell each one to let go of the person in front and grab the person behind instead. After you've done everyone, the line faces the other way.

**Merge two sorted lists:** Two single-file lines, each sorted by height (shortest in front). You want to combine them into one sorted line. You look at the front of both lines, pick whoever is shorter, and add them to your new line. Repeat until both lines are empty.

**Dummy node:** You put a fake person at the very front of the line before you start building it. This fake person has no real role, they just make sure you always have "someone in front" to attach the next real person to. When you're done, you skip the fake person and return everyone else.

---

## When to Use

### Green Flags (linked list pattern applies)

- "reverse linked list"
- "merge sorted lists" / "merge k sorted lists"
- "remove nth from end"
- "swap pairs" / "swap nodes"
- "reverse in k-group"
- "partition list"
- "odd even linked list"
- "add two numbers" (where numbers are stored as linked lists)
- "copy list with random pointer"
- "flatten" (multilevel or binary tree to list)
- "intersection of two linked lists"
- "remove linked list elements"

### Red Flags (linked list is wrong tool)

- Need random access by index: use an array
- Need sorted order with efficient insert/delete: use a TreeMap or balanced BST
- Need O(1) lookup by key: use a HashMap
- Problem says "array" or "string" explicitly: don't convert to linked list

---

## Core Templates in Java

### Template 1: Full List Reversal (Iterative)

The three-pointer dance. This is the foundation of every reversal problem.

```java
public ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;

    while (curr != null) {
        ListNode next = curr.next;  // SAVE next before breaking the link
        curr.next = prev;           // reverse the pointer
        prev = curr;                // advance prev
        curr = next;                // advance curr
    }

    return prev;  // prev is now the new head
}
```

**Pointer-by-pointer trace on [1 -> 2 -> 3 -> null]:**

```
Initial:
  prev = null
  curr = [1]

Iteration 1:
  next = [2]          (save next)
  curr.next = null    (1 now points to null)
  prev = [1]          (prev advances)
  curr = [2]          (curr advances)
  State: null <- [1]   [2] -> [3] -> null

Iteration 2:
  next = [3]          (save next)
  curr.next = [1]     (2 now points to 1)
  prev = [2]          (prev advances)
  curr = [3]          (curr advances)
  State: null <- [1] <- [2]   [3] -> null

Iteration 3:
  next = null         (save next)
  curr.next = [2]     (3 now points to 2)
  prev = [3]          (prev advances)
  curr = null         (curr advances, loop ends)
  State: null <- [1] <- [2] <- [3]

Return prev = [3]
Result: [3] -> [2] -> [1] -> null
```

**Time:** O(n) | **Space:** O(1)

### Template 1b: Full List Reversal (Recursive)

Useful to know for interviews that ask "can you do it recursively?"

```java
public ListNode reverseListRecursive(ListNode head) {
    // Base case: empty list or single node
    if (head == null || head.next == null) return head;

    // Recurse on the rest of the list
    ListNode newHead = reverseListRecursive(head.next);

    // head.next is now the tail of the reversed sublist
    // Make it point back to head
    head.next.next = head;
    head.next = null;

    return newHead;
}
```

**Trace on [1 -> 2 -> 3]:**
```
reverseListRecursive(1)
  reverseListRecursive(2)
    reverseListRecursive(3)
      returns 3 (base case, 3.next = null)
    newHead = 3
    2.next.next = 2  =>  3.next = 2
    2.next = null
    returns 3
  newHead = 3
  1.next.next = 1  =>  2.next = 1
  1.next = null
  returns 3

Result: 3 -> 2 -> 1 -> null
```

**Time:** O(n) | **Space:** O(n) call stack

The iterative version is preferred in interviews because it's O(1) space and avoids stack overflow on very long lists.

---

### Template 2: Reverse Between Positions m and n

Find the node just before position m. Reverse the sublist from m to n. Reconnect.

```java
public ListNode reverseBetween(ListNode head, int left, int right) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode prev = dummy;

    // Step 1: advance prev to the node just before position 'left'
    for (int i = 1; i < left; i++) {
        prev = prev.next;
    }

    // Step 2: curr is now at position 'left'
    ListNode curr = prev.next;

    // Step 3: reverse (right - left) times
    for (int i = 0; i < right - left; i++) {
        ListNode next = curr.next;
        curr.next = next.next;
        next.next = prev.next;
        prev.next = next;
    }

    return dummy.next;
}
```

**Trace on [1 -> 2 -> 3 -> 4 -> 5], left=2, right=4:**

```
After Step 1: prev = [1], curr = [2]
dummy -> [1] -> [2] -> [3] -> [4] -> [5] -> null

Iteration 1 (i=0):
  next = [3]
  curr.next = [4]       =>  [2] -> [4]
  next.next = prev.next =>  [3] -> [2]
  prev.next = next      =>  [1] -> [3]
  State: dummy -> [1] -> [3] -> [2] -> [4] -> [5]

Iteration 2 (i=1):
  next = [4]
  curr.next = [5]       =>  [2] -> [5]
  next.next = prev.next =>  [4] -> [3]
  prev.next = next      =>  [1] -> [4]
  State: dummy -> [1] -> [4] -> [3] -> [2] -> [5]

Return dummy.next = [1] -> [4] -> [3] -> [2] -> [5]
```

**Time:** O(n) | **Space:** O(1)

---

### Template 3: Merge Two Sorted Lists

Dummy node + compare heads. Classic.

```java
public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;

    while (list1 != null && list2 != null) {
        if (list1.val <= list2.val) {
            curr.next = list1;
            list1 = list1.next;
        } else {
            curr.next = list2;
            list2 = list2.next;
        }
        curr = curr.next;
    }

    // Attach the remaining non-null list
    curr.next = (list1 != null) ? list1 : list2;

    return dummy.next;
}
```

**Trace on [1->3->5] and [2->4->6]:**
```
dummy -> ?
curr = dummy

Round 1: 1 <= 2, attach [1], list1=[3], curr=[1]
Round 2: 3 > 2,  attach [2], list2=[4], curr=[2]
Round 3: 3 <= 4, attach [3], list1=[5], curr=[3]
Round 4: 5 > 4,  attach [4], list2=[6], curr=[4]
Round 5: 5 <= 6, attach [5], list1=null, curr=[5]
Loop ends (list1 is null)

curr.next = list2 = [6]
Result: dummy -> [1] -> [2] -> [3] -> [4] -> [5] -> [6]
Return dummy.next = [1] -> [2] -> [3] -> [4] -> [5] -> [6]
```

**Time:** O(m + n) | **Space:** O(1)

---

### Template 4: Dummy Node Pattern (General)

Use this whenever you're building a new list or might modify the head.

```java
// General pattern
ListNode dummy = new ListNode(0);
dummy.next = head;  // optional: attach existing list
ListNode curr = dummy;

while (/* condition */) {
    // ... process nodes
    // attach nodes: curr.next = someNode
    // advance: curr = curr.next
}

return dummy.next;
```

**When to use dummy node:**
- Building a result list from scratch (merge, partition)
- Deleting nodes that might be the head
- Inserting nodes that might become the new head
- Any time you'd otherwise need `if (head == null)` special cases

---

### Template 5: K-Group Reversal

Count k nodes, reverse the group, recurse or iterate for the next group.

```java
public ListNode reverseKGroup(ListNode head, int k) {
    // Check if there are k nodes remaining
    ListNode check = head;
    for (int i = 0; i < k; i++) {
        if (check == null) return head;  // fewer than k nodes, don't reverse
        check = check.next;
    }

    // Reverse k nodes starting from head
    ListNode prev = null;
    ListNode curr = head;
    for (int i = 0; i < k; i++) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }

    // head is now the tail of the reversed group
    // curr is the start of the next group
    head.next = reverseKGroup(curr, k);

    return prev;  // prev is the new head of this group
}
```

**Trace on [1->2->3->4->5], k=2:**
```
Group 1: [1,2] -> reverse -> [2->1], head=1 (now tail), curr=[3]
  1.next = reverseKGroup([3,4,5], 2)

Group 2: [3,4] -> reverse -> [4->3], head=3 (now tail), curr=[5]
  3.next = reverseKGroup([5], 2)

Group 3: only 1 node, k=2, return [5] as-is

Reconnect: 3.next = [5]  =>  [4->3->5]
Reconnect: 1.next = [4]  =>  [2->1->4->3->5]

Result: [2->1->4->3->5]
```

**Time:** O(n) | **Space:** O(n/k) recursion stack

---

## Real-World Applications

### 1. Undo History in Text Editors

Every state of a document is a node. Undo walks backward through the list. Redo walks forward. This is why editors have a "max undo levels" setting: they cap the list length to save memory.

### 2. OS Memory Management (Free Lists)

When the OS allocates and frees memory blocks, it maintains a linked list of free blocks. Each free block contains a pointer to the next free block. Allocation removes a node from the list. Deallocation inserts a node back. This is a real linked list running in your kernel right now.

### 3. LRU Cache (Topic 6, LC 146)

The LRU Cache uses a doubly linked list + HashMap. The list maintains access order: most recently used at the head, least recently used at the tail. When the cache is full, you evict the tail node. The HashMap gives O(1) lookup of any node so you can move it to the head in O(1).

### 4. Polynomial Arithmetic

A polynomial like `3x^4 + 2x^2 + 7` can be stored as a linked list where each node holds a coefficient and exponent. Adding two polynomials is exactly like merging two sorted lists (sorted by exponent).

### 5. Music Playlist

A doubly linked list where each node is a song. `next` goes to the next track, `prev` goes to the previous track. Shuffle mode randomly relinks nodes. This is why "previous track" in music players sometimes behaves unexpectedly after shuffle.

### 6. Browser History

Forward/backward navigation is a doubly linked list. The current page is your position in the list. Clicking a new link truncates everything after your current position and appends the new page. This is why you lose your forward history when you navigate to a new page.

---

## Problem Categories and Solutions

---

### Category A: Reversal

---

#### LC 206 — Reverse Linked List (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Reverse a singly linked list.

**Approach:** Three-pointer iterative. See Template 1 above for the full trace.

```java
class Solution {
    public ListNode reverseList(ListNode head) {
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

**Dry Run on [1 -> 2 -> 3 -> 4 -> 5]:**

```
Step 0: prev=null, curr=[1]

Step 1:
  next = [2]
  [1].next = null
  prev = [1]
  curr = [2]
  List state: null <- 1   2 -> 3 -> 4 -> 5 -> null

Step 2:
  next = [3]
  [2].next = [1]
  prev = [2]
  curr = [3]
  List state: null <- 1 <- 2   3 -> 4 -> 5 -> null

Step 3:
  next = [4]
  [3].next = [2]
  prev = [3]
  curr = [4]
  List state: null <- 1 <- 2 <- 3   4 -> 5 -> null

Step 4:
  next = [5]
  [4].next = [3]
  prev = [4]
  curr = [5]
  List state: null <- 1 <- 2 <- 3 <- 4   5 -> null

Step 5:
  next = null
  [5].next = [4]
  prev = [5]
  curr = null
  List state: null <- 1 <- 2 <- 3 <- 4 <- 5

Loop ends (curr == null)
Return prev = [5]
Final: 5 -> 4 -> 3 -> 2 -> 1 -> null
```

**Edge cases:**
- Empty list: `head == null`, loop never runs, returns `null`
- Single node: `curr = [1]`, one iteration, `[1].next = null` (already null), returns `[1]`

**Time:** O(n) | **Space:** O(1)

**Interview note:** This is the most fundamental linked list problem. If you can't do this in under 3 minutes, practice until you can. Interviewers use this as a warmup and expect it to be automatic.

---

#### LC 92 — Reverse Linked List II (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Reverse the nodes of the list from position `left` to position `right`. Return the modified list.

**Approach:** Find the node just before `left`. Then use the "insert at front" trick to reverse the sublist in one pass.

```java
class Solution {
    public ListNode reverseBetween(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;

        // Move prev to node just before position 'left'
        for (int i = 1; i < left; i++) {
            prev = prev.next;
        }

        ListNode curr = prev.next;

        // Reverse by repeatedly moving curr.next to front of sublist
        for (int i = 0; i < right - left; i++) {
            ListNode next = curr.next;
            curr.next = next.next;
            next.next = prev.next;
            prev.next = next;
        }

        return dummy.next;
    }
}
```

**Trace on [1->2->3->4->5], left=2, right=4:**

```
dummy -> 1 -> 2 -> 3 -> 4 -> 5 -> null
prev = [1], curr = [2]

i=0:
  next = [3]
  curr.next = [4]       (2 skips 3, points to 4)
  next.next = prev.next (3 points to what prev pointed to, which is 2)
  prev.next = next      (1 now points to 3)
  State: dummy -> 1 -> 3 -> 2 -> 4 -> 5

i=1:
  next = [4]
  curr.next = [5]       (2 skips 4, points to 5)
  next.next = prev.next (4 points to what prev pointed to, which is 3)
  prev.next = next      (1 now points to 4)
  State: dummy -> 1 -> 4 -> 3 -> 2 -> 5

Return dummy.next = 1 -> 4 -> 3 -> 2 -> 5
```

**Edge cases:**
- `left == right`: no reversal needed, loop runs 0 times
- `left == 1`: dummy node handles this, prev stays at dummy

**Time:** O(n) | **Space:** O(1)

---

#### LC 24 — Swap Nodes in Pairs (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Swap every two adjacent nodes. Return the modified list.

**Approach:** Dummy node + swap pairs iteratively.

```java
class Solution {
    public ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;

        while (prev.next != null && prev.next.next != null) {
            ListNode first = prev.next;
            ListNode second = prev.next.next;

            // Swap
            first.next = second.next;
            second.next = first;
            prev.next = second;

            // Move prev forward by 2
            prev = first;
        }

        return dummy.next;
    }
}
```

**Trace on [1->2->3->4]:**

```
dummy -> 1 -> 2 -> 3 -> 4 -> null
prev = dummy

Iteration 1:
  first = [1], second = [2]
  first.next = [3]    (1 points to 3)
  second.next = [1]   (2 points to 1)
  prev.next = [2]     (dummy points to 2)
  prev = [1]          (advance prev to first, which is now tail of pair)
  State: dummy -> 2 -> 1 -> 3 -> 4

Iteration 2:
  first = [3], second = [4]
  first.next = null   (3 points to null)
  second.next = [3]   (4 points to 3)
  prev.next = [4]     (1 points to 4)
  prev = [3]
  State: dummy -> 2 -> 1 -> 4 -> 3

Return dummy.next = 2 -> 1 -> 4 -> 3
```

**Edge cases:**
- Odd length: last node stays in place (loop condition checks for two nodes)
- Empty or single node: loop never runs

**Time:** O(n) | **Space:** O(1)

---

#### LC 25 — Reverse Nodes in k-Group (Hard)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Reverse the nodes of the list k at a time. If the number of nodes is not a multiple of k, leave the remaining nodes as-is.

**Approach:** See Template 5 above. Count k nodes to confirm a full group exists, reverse the group, recurse on the rest.

```java
class Solution {
    public ListNode reverseKGroup(ListNode head, int k) {
        // Check if k nodes exist
        ListNode check = head;
        for (int i = 0; i < k; i++) {
            if (check == null) return head;
            check = check.next;
        }

        // Reverse k nodes
        ListNode prev = null;
        ListNode curr = head;
        for (int i = 0; i < k; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        // head is now the tail of the reversed group
        // Recursively reverse the rest and connect
        head.next = reverseKGroup(curr, k);

        return prev;
    }
}
```

**Iterative version (avoids recursion stack):**

```java
class Solution {
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode groupPrev = dummy;

        while (true) {
            // Find the kth node from groupPrev
            ListNode kth = getKth(groupPrev, k);
            if (kth == null) break;

            ListNode groupNext = kth.next;

            // Reverse the group
            ListNode prev = groupNext;
            ListNode curr = groupPrev.next;
            while (curr != groupNext) {
                ListNode next = curr.next;
                curr.next = prev;
                prev = curr;
                curr = next;
            }

            // Connect with previous part
            ListNode tmp = groupPrev.next;
            groupPrev.next = kth;
            groupPrev = tmp;
        }

        return dummy.next;
    }

    private ListNode getKth(ListNode curr, int k) {
        while (curr != null && k > 0) {
            curr = curr.next;
            k--;
        }
        return curr;
    }
}
```

**Time:** O(n) | **Space:** O(n/k) recursive, O(1) iterative

**Interview note:** The hard part is reconnecting the groups correctly. Draw it out. The tail of each reversed group must connect to the head of the next reversed group.

---

### Category B: Merge

---

#### LC 21 — Merge Two Sorted Lists (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Merge two sorted linked lists and return the merged list.

**Approach:** Dummy node + compare heads. See Template 3 above.

```java
class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                curr.next = list1;
                list1 = list1.next;
            } else {
                curr.next = list2;
                list2 = list2.next;
            }
            curr = curr.next;
        }

        curr.next = (list1 != null) ? list1 : list2;

        return dummy.next;
    }
}
```

**Edge cases:**
- One or both lists empty: the while loop doesn't run, `curr.next` attaches the non-null list (or null if both empty)
- Lists of different lengths: the `curr.next = list1 != null ? list1 : list2` handles the leftover

**Time:** O(m + n) | **Space:** O(1)

---

#### LC 23 — Merge K Sorted Lists (Hard)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, Uber

**Problem:** Merge k sorted linked lists and return the merged sorted list.

**Approach 1: Min-Heap (Priority Queue)**

Add the head of each list to a min-heap. Repeatedly extract the minimum, add it to the result, and push that node's `next` into the heap.

```java
class Solution {
    public ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> heap = new PriorityQueue<>(
            (a, b) -> a.val - b.val
        );

        // Add all non-null heads to the heap
        for (ListNode node : lists) {
            if (node != null) heap.offer(node);
        }

        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;

        while (!heap.isEmpty()) {
            ListNode node = heap.poll();
            curr.next = node;
            curr = curr.next;

            if (node.next != null) {
                heap.offer(node.next);
            }
        }

        return dummy.next;
    }
}
```

**Trace on [[1->4->5], [1->3->4], [2->6]]:**

```
Initial heap: {1(list1), 1(list2), 2(list3)}

Poll 1(list1), push 4(list1). Result: 1
Heap: {1(list2), 2(list3), 4(list1)}

Poll 1(list2), push 3(list2). Result: 1->1
Heap: {2(list3), 3(list2), 4(list1)}

Poll 2(list3), push 6(list3). Result: 1->1->2
Heap: {3(list2), 4(list1), 6(list3)}

Poll 3(list2), push 4(list2). Result: 1->1->2->3
Heap: {4(list1), 4(list2), 6(list3)}

Poll 4(list1), push 5(list1). Result: 1->1->2->3->4
Heap: {4(list2), 5(list1), 6(list3)}

Poll 4(list2), no next. Result: 1->1->2->3->4->4
Heap: {5(list1), 6(list3)}

Poll 5(list1), no next. Result: 1->1->2->3->4->4->5
Heap: {6(list3)}

Poll 6(list3), no next. Result: 1->1->2->3->4->4->5->6
Heap: empty

Return dummy.next
```

**Time:** O(n log k) where n = total nodes, k = number of lists
**Space:** O(k) for the heap

**Approach 2: Divide and Conquer**

Pair up lists and merge pairs. Repeat until one list remains. This is the merge sort approach applied to lists.

```java
class Solution {
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        return mergeRange(lists, 0, lists.length - 1);
    }

    private ListNode mergeRange(ListNode[] lists, int left, int right) {
        if (left == right) return lists[left];

        int mid = left + (right - left) / 2;
        ListNode l1 = mergeRange(lists, left, mid);
        ListNode l2 = mergeRange(lists, mid + 1, right);
        return mergeTwoLists(l1, l2);
    }

    private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
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

**Why divide and conquer is often preferred:**
- Same O(n log k) time complexity
- O(log k) space (recursion stack) vs O(k) for heap
- No heap overhead, simpler to reason about
- Each node is processed O(log k) times (once per merge level)

**Comparison:**

| Approach | Time | Space | Notes |
|---|---|---|---|
| Sequential merge | O(nk) | O(1) | Naive, too slow |
| Min-Heap | O(n log k) | O(k) | Good, heap overhead |
| Divide & Conquer | O(n log k) | O(log k) | Best overall |

**Interview note:** Know both approaches. Start with divide-and-conquer if you're comfortable with recursion. The heap approach is more intuitive to explain step-by-step.

---

#### LC 2 — Add Two Numbers (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe

**Problem:** Two non-empty linked lists represent two non-negative integers in reverse order (each node contains a single digit). Add the two numbers and return the sum as a linked list.

**Approach:** Simulate grade-school addition. Process both lists simultaneously, tracking carry.

```java
class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        int carry = 0;

        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;

            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }

            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }

            carry = sum / 10;
            curr.next = new ListNode(sum % 10);
            curr = curr.next;
        }

        return dummy.next;
    }
}
```

**Trace on l1=[2->4->3] (342) and l2=[5->6->4] (465):**

```
Round 1: sum = 0 + 2 + 5 = 7, carry = 0, node = 7
Round 2: sum = 0 + 4 + 6 = 10, carry = 1, node = 0
Round 3: sum = 1 + 3 + 4 = 8, carry = 0, node = 8
Loop ends (both null, carry = 0)

Result: 7 -> 0 -> 8 (represents 807 = 342 + 465)
```

**Edge cases:**
- Different lengths: the `if (l1 != null)` checks handle this
- Final carry: `|| carry != 0` in the while condition handles a carry after both lists are exhausted (e.g., 999 + 1 = 1000)

**Time:** O(max(m, n)) | **Space:** O(max(m, n)) for the result

---

### Category C: Structural Manipulation

---

#### LC 19 — Remove Nth Node From End of List (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Remove the nth node from the end of the list and return the head.

**Approach:** Two-pointer gap technique (cross-reference Topic 3). Create a gap of n between fast and slow pointers. When fast reaches the end, slow is at the node just before the target.

```java
class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode fast = dummy;
        ListNode slow = dummy;

        // Advance fast by n+1 steps (so slow stops one before target)
        for (int i = 0; i <= n; i++) {
            fast = fast.next;
        }

        // Move both until fast reaches end
        while (fast != null) {
            fast = fast.next;
            slow = slow.next;
        }

        // slow.next is the node to remove
        slow.next = slow.next.next;

        return dummy.next;
    }
}
```

**Trace on [1->2->3->4->5], n=2:**

```
dummy -> 1 -> 2 -> 3 -> 4 -> 5 -> null

After advancing fast by n+1=3 steps:
  fast = [3], slow = dummy

Move both until fast == null:
  fast=[4], slow=[1]
  fast=[5], slow=[2]
  fast=null, slow=[3]

slow.next = slow.next.next  =>  [3].next = [5]
Result: dummy -> 1 -> 2 -> 3 -> 5
Return dummy.next = 1 -> 2 -> 3 -> 5
```

**Why dummy node matters here:** If n equals the list length, we're removing the head. Without a dummy, `slow` would be null when we try to do `slow.next = slow.next.next`. With a dummy, `slow` is the dummy node and `slow.next` is the head, so the deletion works uniformly.

**Time:** O(n) | **Space:** O(1)

---

#### LC 328 — Odd Even Linked List (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Group all nodes at odd positions together, then all nodes at even positions. Return the reordered list. Position is 1-indexed.

**Approach:** Two pointers: one for odd-position nodes, one for even-position nodes. Weave them separately, then connect odd tail to even head.

```java
class Solution {
    public ListNode oddEvenList(ListNode head) {
        if (head == null) return null;

        ListNode odd = head;
        ListNode even = head.next;
        ListNode evenHead = even;  // save even head for reconnection

        while (even != null && even.next != null) {
            odd.next = even.next;   // odd skips even, points to next odd
            odd = odd.next;
            even.next = odd.next;   // even skips odd, points to next even
            even = even.next;
        }

        odd.next = evenHead;  // connect odd tail to even head

        return head;
    }
}
```

**Trace on [1->2->3->4->5]:**

```
odd = [1], even = [2], evenHead = [2]

Iteration 1:
  odd.next = [3]    (1 -> 3)
  odd = [3]
  even.next = [4]   (2 -> 4)
  even = [4]
  State: 1->3->5->null  and  2->4->null (conceptually)

Iteration 2:
  odd.next = [5]    (3 -> 5)
  odd = [5]
  even.next = null  (4 -> null)
  even = null
  Loop ends

odd.next = evenHead = [2]
Result: 1 -> 3 -> 5 -> 2 -> 4 -> null
```

**Time:** O(n) | **Space:** O(1)

---

#### LC 86 — Partition List (Medium)

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Partition a linked list around value x such that all nodes less than x come before nodes greater than or equal to x. Preserve the original relative order.

**Approach:** Two dummy nodes. Build two separate lists: one for nodes < x, one for nodes >= x. Connect them at the end.

```java
class Solution {
    public ListNode partition(ListNode head, int x) {
        ListNode lessHead = new ListNode(0);   // dummy for < x list
        ListNode greaterHead = new ListNode(0); // dummy for >= x list

        ListNode less = lessHead;
        ListNode greater = greaterHead;

        while (head != null) {
            if (head.val < x) {
                less.next = head;
                less = less.next;
            } else {
                greater.next = head;
                greater = greater.next;
            }
            head = head.next;
        }

        // CRITICAL: set tail of greater list to null to avoid cycles
        greater.next = null;

        // Connect the two lists
        less.next = greaterHead.next;

        return lessHead.next;
    }
}
```

**Trace on [1->4->3->2->5->2], x=3:**

```
Process [1]: 1 < 3, add to less. less: dummy->1
Process [4]: 4 >= 3, add to greater. greater: dummy->4
Process [3]: 3 >= 3, add to greater. greater: dummy->4->3
Process [2]: 2 < 3, add to less. less: dummy->1->2
Process [5]: 5 >= 3, add to greater. greater: dummy->4->3->5
Process [2]: 2 < 3, add to less. less: dummy->1->2->2

greater.next = null  (5.next = null, prevents cycle)
less.next = greaterHead.next = [4]

Result: 1 -> 2 -> 2 -> 4 -> 3 -> 5
```

**Why `greater.next = null` is critical:** The last node in the greater list still has its original `next` pointer from the input list. If you don't null it out, you create a cycle or attach garbage to the end of your result.

**Time:** O(n) | **Space:** O(1)

---

#### LC 160 — Intersection of Two Linked Lists (Easy)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Find the node where two singly linked lists intersect. Return null if no intersection.

**Approach:** Two-pointer trick. Both pointers traverse both lists. When one reaches the end, redirect it to the head of the other list. They meet at the intersection (or both reach null simultaneously if no intersection).

```java
class Solution {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode a = headA;
        ListNode b = headB;

        while (a != b) {
            a = (a == null) ? headB : a.next;
            b = (b == null) ? headA : b.next;
        }

        return a;  // either the intersection node or null
    }
}
```

**Why this works:**

If list A has length `a` and list B has length `b`, and the shared tail has length `c`:
- Pointer A travels: `(a - c) + c + (b - c) = a + b - c`
- Pointer B travels: `(b - c) + c + (a - c) = a + b - c`

Both pointers travel the same total distance. They arrive at the intersection simultaneously.

If there's no intersection, both pointers reach null at the same time (after traveling `a + b` steps total).

**Alternative approach (length difference):**

```java
class Solution {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        int lenA = length(headA);
        int lenB = length(headB);

        while (lenA > lenB) { headA = headA.next; lenA--; }
        while (lenB > lenA) { headB = headB.next; lenB--; }

        while (headA != headB) {
            headA = headA.next;
            headB = headB.next;
        }

        return headA;
    }

    private int length(ListNode head) {
        int len = 0;
        while (head != null) { head = head.next; len++; }
        return len;
    }
}
```

The two-pointer approach is cleaner and preferred in interviews.

**Time:** O(m + n) | **Space:** O(1)

---

#### LC 203 — Remove Linked List Elements (Easy)

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Remove all nodes with value equal to `val`.

**Approach:** Dummy node makes head deletion trivial.

```java
class Solution {
    public ListNode removeElements(ListNode head, int val) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode curr = dummy;

        while (curr.next != null) {
            if (curr.next.val == val) {
                curr.next = curr.next.next;  // skip the node
            } else {
                curr = curr.next;
            }
        }

        return dummy.next;
    }
}
```

**Note:** Don't advance `curr` when you delete a node. The new `curr.next` might also need deletion.

**Time:** O(n) | **Space:** O(1)

---

### Category D: Advanced

---

#### LC 138 — Copy List with Random Pointer (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** A linked list where each node has a `next` pointer and a `random` pointer (which can point to any node or null). Create a deep copy.

**Approach 1: HashMap (cross-reference Topic 6)**

Map each original node to its copy. Then set `next` and `random` pointers using the map.

```java
class Solution {
    public Node copyRandomList(Node head) {
        if (head == null) return null;

        Map<Node, Node> map = new HashMap<>();

        // First pass: create all copy nodes
        Node curr = head;
        while (curr != null) {
            map.put(curr, new Node(curr.val));
            curr = curr.next;
        }

        // Second pass: set next and random pointers
        curr = head;
        while (curr != null) {
            map.get(curr).next = map.get(curr.next);
            map.get(curr).random = map.get(curr.random);
            curr = curr.next;
        }

        return map.get(head);
    }
}
```

**Time:** O(n) | **Space:** O(n) for the HashMap

**Approach 2: Interleaving (O(1) space)**

Weave copies into the original list, set random pointers, then separate.

```java
class Solution {
    public Node copyRandomList(Node head) {
        if (head == null) return null;

        // Step 1: Interleave copies
        // 1 -> 1' -> 2 -> 2' -> 3 -> 3' -> null
        Node curr = head;
        while (curr != null) {
            Node copy = new Node(curr.val);
            copy.next = curr.next;
            curr.next = copy;
            curr = copy.next;
        }

        // Step 2: Set random pointers for copies
        curr = head;
        while (curr != null) {
            if (curr.random != null) {
                curr.next.random = curr.random.next;
            }
            curr = curr.next.next;
        }

        // Step 3: Separate the two lists
        Node dummy = new Node(0);
        Node copyCurr = dummy;
        curr = head;
        while (curr != null) {
            copyCurr.next = curr.next;
            curr.next = curr.next.next;
            copyCurr = copyCurr.next;
            curr = curr.next;
        }

        return dummy.next;
    }
}
```

**Time:** O(n) | **Space:** O(1)

**Interview note:** The HashMap approach is easier to explain and implement correctly under pressure. Start with that. Mention the O(1) space approach as a follow-up if the interviewer asks.

---

#### LC 445 — Add Two Numbers II (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Same as LC 2, but the digits are stored in forward order (most significant digit first).

**Approach 1: Reverse both lists, add, reverse result**

```java
class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        l1 = reverse(l1);
        l2 = reverse(l2);

        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        int carry = 0;

        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) { sum += l1.val; l1 = l1.next; }
            if (l2 != null) { sum += l2.val; l2 = l2.next; }
            carry = sum / 10;
            curr.next = new ListNode(sum % 10);
            curr = curr.next;
        }

        return reverse(dummy.next);
    }

    private ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }
}
```

**Approach 2: Stack-based (no mutation of input)**

```java
class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        Deque<Integer> stack1 = new ArrayDeque<>();
        Deque<Integer> stack2 = new ArrayDeque<>();

        while (l1 != null) { stack1.push(l1.val); l1 = l1.next; }
        while (l2 != null) { stack2.push(l2.val); l2 = l2.next; }

        int carry = 0;
        ListNode curr = null;

        while (!stack1.isEmpty() || !stack2.isEmpty() || carry != 0) {
            int sum = carry;
            if (!stack1.isEmpty()) sum += stack1.pop();
            if (!stack2.isEmpty()) sum += stack2.pop();
            carry = sum / 10;

            ListNode node = new ListNode(sum % 10);
            node.next = curr;
            curr = node;
        }

        return curr;
    }
}
```

The stack approach builds the result in reverse by prepending each new node, so no final reversal is needed.

**Time:** O(m + n) | **Space:** O(m + n) for stacks

---

#### LC 114 — Flatten Binary Tree to Linked List (Medium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Flatten a binary tree to a linked list in-place using the tree's right pointers. The list should follow preorder traversal order.

**Approach 1: Morris-like (O(1) space)**

For each node with a left child, find the rightmost node of the left subtree. Connect it to the current node's right child. Move the left subtree to the right. Set left to null.

```java
class Solution {
    public void flatten(TreeNode root) {
        TreeNode curr = root;

        while (curr != null) {
            if (curr.left != null) {
                // Find rightmost node of left subtree
                TreeNode rightmost = curr.left;
                while (rightmost.right != null) {
                    rightmost = rightmost.right;
                }

                // Connect rightmost to curr's right subtree
                rightmost.right = curr.right;

                // Move left subtree to right
                curr.right = curr.left;
                curr.left = null;
            }

            curr = curr.right;
        }
    }
}
```

**Approach 2: Stack-based preorder**

```java
class Solution {
    public void flatten(TreeNode root) {
        if (root == null) return;

        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();

            if (node.right != null) stack.push(node.right);
            if (node.left != null) stack.push(node.left);

            if (!stack.isEmpty()) {
                node.right = stack.peek();
            }
            node.left = null;
        }
    }
}
```

**Approach 3: Reverse postorder (right, left, root)**

```java
class Solution {
    private TreeNode prev = null;

    public void flatten(TreeNode root) {
        if (root == null) return;
        flatten(root.right);
        flatten(root.left);
        root.right = prev;
        root.left = null;
        prev = root;
    }
}
```

**Time:** O(n) for all approaches | **Space:** O(1) Morris, O(n) stack/recursion

---

## Common Mistakes and Edge Cases

### Mistake 1: Losing the Next Reference During Reversal

**Wrong:**
```java
curr.next = prev;  // you just lost curr.next!
prev = curr;
curr = curr.next;  // curr.next is now prev, not the original next
```

**Right:**
```java
ListNode next = curr.next;  // SAVE FIRST
curr.next = prev;
prev = curr;
curr = next;
```

This is the most common linked list bug. Save `next` before you change any pointers.

### Mistake 2: Not Using a Dummy Node

Without a dummy node, you need special handling for head insertion/deletion:

```java
// Without dummy: messy
if (head == null || head.val == val) {
    head = head.next;
} else {
    // ... normal deletion
}

// With dummy: uniform
ListNode dummy = new ListNode(0);
dummy.next = head;
ListNode curr = dummy;
while (curr.next != null) {
    if (curr.next.val == val) curr.next = curr.next.next;
    else curr = curr.next;
}
return dummy.next;
```

### Mistake 3: Off-by-One in Reverse Between m and n

The loop runs `right - left` times, not `right - left + 1`. The first node (at position `left`) is already in place as `curr`. You only need to move `right - left` nodes in front of it.

### Mistake 4: Not Nulling the Tail After Partition

After building two separate lists and connecting them, the tail of the second list still points to whatever it pointed to in the original list. Always set `greater.next = null` (or equivalent) before connecting.

```java
// WRONG: creates cycle or garbage tail
less.next = greaterHead.next;

// RIGHT: null the tail first
greater.next = null;
less.next = greaterHead.next;
```

### Mistake 5: Not Handling Unequal Lengths in Merge/Add

In LC 2 (Add Two Numbers), the loop condition must be:
```java
while (l1 != null || l2 != null || carry != 0)
```
Not `&&`. If you use `&&`, you stop when the shorter list ends and lose the remaining digits.

### Edge Cases to Always Test

| Edge Case | What to Check |
|---|---|
| Empty list (`head == null`) | Return null immediately or handle in loop |
| Single node | Reversal returns same node; merge returns that node |
| Two nodes | Reversal swaps them; test both orderings |
| Already sorted | Merge should still work correctly |
| All same values | Partition, removal should handle correctly |
| n equals list length | Remove nth from end removes the head |
| k equals list length | k-group reversal reverses entire list |
| Lists of different lengths | Merge, add two numbers |

---

## Pattern Comparison

### Linked List Reversal vs Array Reversal

**Array reversal:** swap elements using two pointers from both ends.
```java
// Array: O(1) space, simple swap
int left = 0, right = arr.length - 1;
while (left < right) {
    int tmp = arr[left];
    arr[left++] = arr[right];
    arr[right--] = tmp;
}
```

**Linked list reversal:** can't swap values easily (nodes might be complex objects), so you reverse the pointers instead. Three-pointer dance.

The key difference: arrays give you random access, so you can swap from both ends. Linked lists only give you forward traversal, so you reverse pointers as you go.

### Merge K Lists: Three Approaches Compared

**Sequential merge (naive):**
- Merge list 1 and 2, then merge result with list 3, etc.
- Time: O(nk) because each merge touches all previously merged nodes
- Space: O(1)
- Don't use this in interviews

**Min-Heap:**
- Always extract the global minimum
- Time: O(n log k)
- Space: O(k) for the heap
- Intuitive, easy to explain

**Divide and Conquer:**
- Pair up lists, merge pairs, repeat
- Time: O(n log k)
- Space: O(log k) recursion stack
- More elegant, mirrors merge sort

Both heap and divide-and-conquer are acceptable. Divide-and-conquer is slightly better on space.

### Fast/Slow (Topic 3) vs Gap Technique for Nth from End

**Fast/Slow (Topic 3):** used for cycle detection and finding the midpoint. Both pointers start at head. Fast moves 2x speed.

**Gap technique (LC 19):** used for finding the nth node from the end. Fast pointer gets an n-step head start. Both move at the same speed. When fast reaches the end, slow is at the target.

They're related but distinct. The gap technique is a special case of the two-pointer approach where the gap is fixed rather than the speed ratio.

### Linked List vs Array: When to Use Each

| Situation | Prefer |
|---|---|
| Frequent insertions/deletions at arbitrary positions | Linked List |
| Random access by index | Array |
| Memory is fragmented (can't allocate contiguous block) | Linked List |
| Cache performance matters | Array (better locality) |
| Size is unknown and changes frequently | Linked List |
| Binary search needed | Array |
| Implementing a queue with O(1) enqueue/dequeue | Linked List |
| Implementing a stack | Either (array is simpler) |

In interviews, the problem statement tells you which to use. If it says "linked list," use a linked list. If it says "array," use an array. Don't convert between them unless the problem requires it.

---

## Quick Reference

### Template Selector

```
What does the problem ask?
│
├── Reverse something?
│   ├── Whole list → Template 1 (three-pointer)
│   ├── Part of list → Template 2 (reverse between m and n)
│   └── k at a time → Template 5 (k-group reversal)
│
├── Merge sorted lists?
│   ├── Two lists → Template 3 (dummy + compare heads)
│   └── K lists → Heap or Divide-and-Conquer
│
├── Remove/insert nodes?
│   └── Template 4 (dummy node pattern)
│
├── Partition/rearrange?
│   └── Two dummy lists, reconnect (LC 86, LC 328)
│
└── Deep copy?
    └── HashMap approach (LC 138)
```

### The Three-Pointer Reversal Diagram

```
Before:
  prev    curr    next
  null -> [1] --> [2] --> [3] --> null

Step 1: Save next
  prev    curr    next
  null    [1]     [2] --> [3] --> null

Step 2: Reverse curr.next
  prev    curr    next
  null <- [1]     [2] --> [3] --> null

Step 3: Advance prev and curr
          prev    curr
  null <- [1]     [2] --> [3] --> null

Repeat...
```

### Dummy Node Checklist

Use a dummy node when:
- [ ] You might delete the head node
- [ ] You might insert before the head
- [ ] You're building a new list from scratch
- [ ] You're merging multiple lists
- [ ] You want uniform code for head and non-head cases

### Complexity Summary

| Problem | Time | Space |
|---|---|---|
| LC 206 Reverse Linked List | O(n) | O(1) |
| LC 92 Reverse Between | O(n) | O(1) |
| LC 24 Swap Pairs | O(n) | O(1) |
| LC 25 Reverse K-Group | O(n) | O(n/k) recursive, O(1) iterative |
| LC 21 Merge Two Sorted | O(m+n) | O(1) |
| LC 23 Merge K Sorted (heap) | O(n log k) | O(k) |
| LC 23 Merge K Sorted (D&C) | O(n log k) | O(log k) |
| LC 2 Add Two Numbers | O(max(m,n)) | O(max(m,n)) |
| LC 19 Remove Nth From End | O(n) | O(1) |
| LC 328 Odd Even Linked List | O(n) | O(1) |
| LC 86 Partition List | O(n) | O(1) |
| LC 160 Intersection | O(m+n) | O(1) |
| LC 203 Remove Elements | O(n) | O(1) |
| LC 138 Copy with Random (map) | O(n) | O(n) |
| LC 138 Copy with Random (interleave) | O(n) | O(1) |
| LC 445 Add Two Numbers II | O(m+n) | O(m+n) |
| LC 114 Flatten Binary Tree | O(n) | O(1) Morris |

---

## Practice Roadmap

### Week 1: Foundations (15 minutes per problem)

These five problems cover the core techniques. Get them to the point where you can write them from memory.

| Problem | Pattern | Key Insight |
|---|---|---|
| LC 206 Reverse Linked List | Three-pointer reversal | Save next before changing pointers |
| LC 21 Merge Two Sorted Lists | Dummy node + compare | Attach remaining list at end |
| LC 203 Remove Linked List Elements | Dummy node | Don't advance curr on deletion |
| LC 160 Intersection of Two Linked Lists | Two-pointer | Both pointers travel same total distance |
| LC 2 Add Two Numbers | Carry simulation | Loop while either list or carry is non-zero |

**Goal:** Each problem in under 15 minutes, no bugs.

### Week 2: Intermediate (25 minutes per problem)

These problems combine multiple techniques or require more careful pointer management.

| Problem | Pattern | Key Insight |
|---|---|---|
| LC 92 Reverse Linked List II | Partial reversal | Insert-at-front trick |
| LC 24 Swap Nodes in Pairs | Pair manipulation | Dummy node, advance by 2 |
| LC 328 Odd Even Linked List | Two-list weave | Save even head before loop |
| LC 86 Partition List | Two dummy lists | Null the greater tail |
| LC 19 Remove Nth From End | Gap technique | Advance fast by n+1 (not n) |

**Goal:** Each problem in under 25 minutes, clean code.

### Week 3: Advanced (35 minutes per problem)

These are the hard problems that appear in senior-level interviews.

| Problem | Pattern | Key Insight |
|---|---|---|
| LC 23 Merge K Sorted Lists | Heap or D&C | Know both approaches |
| LC 25 Reverse Nodes in k-Group | K-group reversal | Check k nodes exist before reversing |
| LC 138 Copy List with Random Pointer | HashMap or interleave | HashMap is safer under pressure |
| LC 445 Add Two Numbers II | Stack or reverse | Stack avoids mutating input |
| LC 114 Flatten Binary Tree | Morris-like | Find rightmost of left subtree |

**Goal:** Each problem in under 35 minutes, able to explain trade-offs.

### Total: ~15 problems over 3 weeks

After completing this roadmap, you'll have seen every major linked list pattern. New linked list problems you encounter will be combinations or variations of what you've already practiced.

---

## Interview Cheat Sheet

### Before You Code

1. Draw the list on paper/whiteboard
2. Identify which pattern applies (reversal, merge, partition, dummy, copy)
3. Decide if you need a dummy node
4. Think about edge cases: empty list, single node, two nodes

### During Coding

1. Save `next` before changing any pointer
2. Use a dummy node if there's any chance you'll modify the head
3. Null out tails after partition operations
4. Test your loop termination condition carefully

### After Coding

1. Trace through your code on the example input
2. Test the edge cases you identified
3. Verify no cycles were accidentally created
4. Check that you're returning the right node (often `dummy.next`, not `dummy`)

### Common Interview Questions About Linked Lists

**"Why use a linked list instead of an array?"**
Linked lists support O(1) insertion and deletion at any position (given a pointer to that position). Arrays require O(n) shifting. The trade-off is O(n) access time vs O(1) for arrays.

**"What's the space complexity of recursive reversal?"**
O(n) due to the call stack. The iterative version is O(1). For very long lists, recursion can cause a stack overflow.

**"Can you detect a cycle in O(1) space?"**
Yes, using Floyd's cycle detection (fast/slow pointers from Topic 3). Fast moves 2 steps, slow moves 1 step. If they meet, there's a cycle.

**"How would you find the middle of a linked list?"**
Fast/slow pointers (Topic 3). When fast reaches the end, slow is at the middle.

---

## Variations and Follow-ups

Interviewers often extend the base problems. Here are the most common follow-ups and how to handle them.

### Follow-ups on LC 206 (Reverse Linked List)

**"Reverse only the even-positioned nodes."**
Separate even-positioned nodes into their own list, reverse that list, then weave it back.

**"Reverse in groups of k, but don't reverse the last group if it has fewer than k nodes."**
This is exactly LC 25. The check at the start of each group handles this.

**"What if the list has a cycle?"**
Detect the cycle first (Topic 3, Floyd's algorithm). Find the cycle entry point. Break the cycle. Then reverse.

### Follow-ups on LC 21 (Merge Two Sorted Lists)

**"Merge without using extra space."**
The standard solution already uses O(1) space. The dummy node is a local variable, not proportional to input size.

**"Merge in descending order."**
Reverse both lists first, then merge normally. Or change the comparison from `<=` to `>=`.

**"What if the lists have duplicates and you want to remove them?"**
After merging, do a second pass: if `curr.val == curr.next.val`, skip `curr.next`.

### Follow-ups on LC 23 (Merge K Sorted Lists)

**"What if k is very large (millions of lists)?"**
The heap approach still works but uses O(k) memory. If k is too large to fit in memory, use external merge sort: process lists in batches.

**"What if each list is very long?"**
Divide-and-conquer is better here because it minimizes the number of times each node is touched (O(log k) times).

**"Can you do it iteratively without a heap?"**
Yes, with divide-and-conquer iteratively: merge pairs in round 1, merge pairs of pairs in round 2, etc.

```java
// Iterative divide-and-conquer
public ListNode mergeKLists(ListNode[] lists) {
    if (lists == null || lists.length == 0) return null;
    int n = lists.length;

    while (n > 1) {
        for (int i = 0; i < n / 2; i++) {
            lists[i] = mergeTwoLists(lists[i], lists[n - 1 - i]);
        }
        n = (n + 1) / 2;
    }

    return lists[0];
}
```

### Follow-ups on LC 138 (Copy List with Random Pointer)

**"What if random pointers can form cycles?"**
The HashMap approach handles this naturally. The interleaving approach also handles it because you're copying structure, not following random pointers during the copy phase.

**"What if you can't use extra memory?"**
Use the interleaving approach (O(1) space). Weave copies into the original, set random pointers, then separate.

### Follow-ups on LC 25 (Reverse Nodes in k-Group)

**"What if you want to reverse alternate groups?"**
Track a boolean flag. Reverse when flag is true, skip when false. Toggle after each group.

**"What if k is larger than the list length?"**
The check at the start returns `head` unchanged. The whole list is treated as a partial group.

---

## Debugging Linked List Code

Linked list bugs are almost always one of three things: a lost reference, a null pointer exception, or an accidental cycle. Here's how to debug each.

### Lost Reference

**Symptom:** Your output list is shorter than expected, or you get null where you expect a node.

**Cause:** You changed a pointer before saving the node it pointed to.

**Fix:** Add `ListNode next = curr.next;` before any line that changes `curr.next`.

**Debug technique:** Add print statements after each pointer change:
```java
ListNode next = curr.next;
curr.next = prev;
System.out.println("curr=" + curr.val + " prev=" + (prev==null?"null":prev.val) + " next=" + (next==null?"null":next.val));
prev = curr;
curr = next;
```

### Null Pointer Exception

**Symptom:** `NullPointerException` at runtime.

**Common causes:**
- Accessing `.next` on a null node
- Not checking `curr != null` before `curr.next`
- Forgetting that `dummy.next` might be null if the list is empty

**Fix pattern:** Always check null before dereferencing:
```java
// Wrong
while (curr.next != null) { ... }  // crashes if curr is null

// Right
while (curr != null && curr.next != null) { ... }
```

### Accidental Cycle

**Symptom:** Your code runs forever (infinite loop) or produces a list that never ends.

**Cause:** You forgot to null out a tail pointer after rearranging nodes.

**Common locations:**
- After `partition()`: the tail of the greater list still points into the original list
- After `reverseKGroup()`: the tail of a reversed group still points to the next group before reconnection
- After any operation that moves nodes between lists

**Fix:** After building each sub-list, explicitly set its tail's `next` to null before connecting it to the next sub-list.

**Debug technique:** Add a cycle check after your operation:
```java
// Quick cycle check (Floyd's)
ListNode slow = result, fast = result;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
    if (slow == fast) {
        System.out.println("CYCLE DETECTED");
        break;
    }
}
```

---

## Complexity Deep Dive

### Why Merge K Lists is O(n log k)

With k lists and n total nodes:

**Heap approach:** Each node is inserted into and extracted from the heap exactly once. Each heap operation is O(log k) because the heap has at most k elements. Total: O(n log k).

**Divide-and-conquer:** Think of it as a binary tree of merges. There are log k levels. At each level, every node participates in exactly one merge. Total work per level: O(n). Total: O(n log k).

**Sequential merge (why it's O(nk)):** Merging list 1 (size n/k) with list 2 (size n/k) costs O(n/k). Merging the result (size 2n/k) with list 3 costs O(3n/k). The total is O(n/k + 2n/k + 3n/k + ... + (k-1)n/k) = O(n/k * k^2/2) = O(nk). This is why sequential merge is too slow.

### Space Complexity of Recursive Reversal

Each recursive call adds a frame to the call stack. For a list of length n, you make n recursive calls before hitting the base case. Each frame holds a constant amount of data (the `head` parameter and the `newHead` return value). Total stack space: O(n).

For a list with 100,000 nodes, this is 100,000 stack frames. Java's default stack size is around 512KB to 1MB. Each frame is roughly 32-64 bytes. So you'd overflow the stack at around 10,000-30,000 nodes. The iterative version has no such limit.

### Why Dummy Node Doesn't Affect Space Complexity

The dummy node is a single `ListNode` object. It's O(1) space regardless of input size. It doesn't scale with n. So adding a dummy node never changes the space complexity of your algorithm.

---

## Linked List in System Design

Linked lists appear in system design interviews too, usually as components of larger systems.

### LRU Cache (LC 146)

The LRU Cache combines a doubly linked list with a HashMap:
- HashMap: O(1) lookup of any node by key
- Doubly linked list: O(1) move-to-front and remove-from-tail

The list maintains access order. The most recently used item is at the head. The least recently used is at the tail. When the cache is full, evict the tail.

This is covered in depth in Topic 6. The linked list part is: maintain a doubly linked list where you can remove any node in O(1) (using the prev pointer) and insert at the head in O(1).

### Message Queue

A linked list is a natural implementation for a FIFO queue. Enqueue at the tail (O(1) with a tail pointer), dequeue from the head (O(1)). Java's `LinkedList` implements `Queue` this way.

### Skip List

A skip list is a probabilistic data structure built on top of linked lists. It has multiple levels of linked lists, each skipping over more elements. This gives O(log n) average search time while maintaining O(1) insert/delete at a given position. Redis uses skip lists for sorted sets.

---

*Cross-references: Topic 3 (Fast & Slow Pointers) for cycle detection, midpoint, and gap technique. Topic 6 (HashMap) for LC 138 deep copy approach.*
