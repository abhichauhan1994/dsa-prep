# Topic 11: Trees — BFS (Level-Order Traversal)

> **Series position:** Document 11 of 20
> **Difficulty range:** Easy to Hard
> **Interview frequency:** BFS tree problems appear in ~20% of FAANG interviews. Level-order variants are among the most commonly asked tree questions.
> **Top companies:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5](#eli5)
3. [When to Use BFS vs DFS](#when-to-use-bfs-vs-dfs)
4. [Core Templates in Java](#core-templates-in-java)
5. [Real-World Applications](#real-world-applications)
6. [Problem Categories and Solutions](#problem-categories-and-solutions)
7. [Common Mistakes](#common-mistakes)
8. [Pattern Comparison: DFS vs BFS](#pattern-comparison-dfs-vs-bfs)
9. [Quick Reference Cheat Sheet](#quick-reference-cheat-sheet)
10. [Practice Roadmap](#practice-roadmap)

---

## Core Concept

### What BFS Does

BFS (Breadth-First Search) processes a tree level by level, visiting all nodes at depth `d` before visiting any node at depth `d+1`. It uses a **queue** (FIFO) to maintain the order of processing.

```
Tree used throughout this document:

            1
          /   \
         2     3
        / \   / \
       4   5 6   7
      /
     8

Level 0: [1]
Level 1: [2, 3]
Level 2: [4, 5, 6, 7]
Level 3: [8]
```

The queue at each step:

```
Start:    [1]
After 1:  [2, 3]         (added 1's children)
After 2:  [3, 4, 5]      (added 2's children)
After 3:  [4, 5, 6, 7]   (added 3's children)
After 4:  [5, 6, 7, 8]   (added 4's children)
After 5:  [6, 7, 8]      (5 has no children)
After 6:  [7, 8]         (6 has no children)
After 7:  [8]            (7 has no children)
After 8:  []             (8 has no children, done)
```

### The "Snapshot the Size" Technique

The naive BFS loop processes one node at a time. That's fine for simple traversal, but most BFS tree problems need you to know **which level you're on**. The solution is to snapshot the queue size at the start of each level:

```java
while (!queue.isEmpty()) {
    int size = queue.size();  // snapshot: how many nodes are on THIS level
    for (int i = 0; i < size; i++) {
        TreeNode node = queue.poll();
        // process node — it belongs to the current level
        if (node.left != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
    // after the inner loop, all nodes from this level are processed
    // the queue now contains exactly the next level's nodes
}
```

This is the backbone of every BFS tree problem. The inner `for` loop processes exactly one level. After it finishes, the queue holds exactly the next level. This pattern appears in LC 102, 103, 107, 199, 515, 637, 662, and more.

Why does it work? When you enter the inner loop, `queue.size()` is the count of nodes added by the previous level. You process exactly that many nodes, and each one may add 0, 1, or 2 children. Those children form the next level. By the time `i == size`, you've processed the entire current level and the queue contains only the next level.

### BFS and Shortest Path

BFS finds the **shortest path** in an unweighted graph. In a tree, this means BFS finds the minimum depth (shallowest leaf) faster than DFS. When BFS reaches the first leaf node, that's guaranteed to be the minimum depth — no need to explore further. DFS, by contrast, must traverse the entire tree to be sure it found the shallowest leaf.

This property extends to graph BFS (Topic 14). The same queue-based level processing that works on trees works on general graphs.

---

## ELI5

You're reading names on a family tree. You want to read every name, but you want to read all the names on one row before moving to the next row.

You start with just the great-great-grandparent at the top. You write their name on a list. Then you look at their children and add both of them to the list. Now you cross off the great-great-grandparent and read the two children. For each of them, you add their children to the list. You keep going: read everyone on the current row, add their kids to the list, move to the next row.

The list is your queue. You always add to the back and read from the front. That's BFS.

The trick for "how many people are on this row?" is to check how long the list is before you start reading. If there are 4 people on the list when you start a row, you read exactly 4 people, then stop. Everyone added during those 4 reads belongs to the next row.

---

## When to Use BFS vs DFS

### Reach for BFS when the problem mentions:

```
"level order"                → BFS, snapshot size pattern
"level by level"             → BFS
"row by row"                 → BFS
"minimum depth"              → BFS (returns at first leaf, faster than DFS)
"right side view"            → BFS, take last node per level
"zigzag"                     → BFS with alternating direction flag
"average of each level"      → BFS, sum per level
"largest value in each row"  → BFS, max per level
"connect nodes at same level"→ BFS with next pointer trick
"width of tree"              → BFS with index tracking
"check completeness"         → BFS, null detection
"nodes at distance K"        → BFS from target (after parent-pointer setup)
```

### Reach for DFS when the problem mentions:

```
"path sum"                   → DFS top-down
"max/min depth"              → DFS bottom-up (or BFS for min depth)
"validate BST"               → DFS with bounds
"lowest common ancestor"     → DFS postorder
"serialize/deserialize"      → DFS preorder
"diameter"                   → DFS bottom-up, global variable
"balanced tree"              → DFS bottom-up height
"subtree"                    → DFS recursive containment
```

### The Decision Rule

> Path/depth/structure problems: DFS.
> Level/layer/breadth problems: BFS.

When in doubt, ask: "Does the answer depend on which level a node is on?" If yes, BFS. If the answer depends on the path from root to a node, DFS.

---

## Core Templates in Java

### TreeNode Definition

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}
```

---

### Template 1: Standard Level-Order Traversal

Returns `List<List<Integer>>` — the most common BFS return type.

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();              // snapshot current level size
        List<Integer> level = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        result.add(level);
    }

    return result;
}
```

**Complexity:** O(n) time, O(n) space (queue holds at most one full level, which is O(n) for a complete tree).

**Key decisions:**
- `LinkedList` implements `Queue`. `ArrayDeque` is faster in practice but both work.
- Always check `root == null` before offering to the queue.
- The `size` snapshot is taken before the inner loop, not inside it.

---

### Template 2: Level-Order with State Tracking

For problems that need extra state per level: zigzag direction, right-side view, minimum depth, level averages.

```java
public List<List<Integer>> levelOrderWithState(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    boolean leftToRight = true;   // for zigzag
    int depth = 0;                // for depth tracking

    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();
        depth++;

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();

            // --- State-dependent processing ---
            // For zigzag: add to front or back based on direction
            if (leftToRight) {
                level.add(node.val);
            } else {
                level.add(0, node.val);  // add to front for reverse
            }

            // For right-side view: only keep last node (i == size - 1)
            // For min depth: check if node is a leaf (no children)
            // For average: accumulate sum, divide by size after inner loop

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        leftToRight = !leftToRight;  // flip direction for next level
        result.add(level);
    }

    return result;
}
```

**The pattern:** All state variables (`leftToRight`, `depth`, running sums) are updated once per level, outside the inner loop. The inner loop only processes nodes.

---

### Template 3: BFS with Next Pointer

For LC 116 and LC 117 — connecting nodes at the same level using a `next` pointer.

```java
// Node definition for next-pointer problems
class Node {
    int val;
    Node left;
    Node right;
    Node next;  // points to the next node on the same level, null if rightmost
}
```

**Approach A: Standard BFS with queue (O(n) space)**

```java
public Node connect(Node root) {
    if (root == null) return null;

    Queue<Node> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();

        for (int i = 0; i < size; i++) {
            Node node = queue.poll();

            // Connect to next node in queue, but only within this level
            if (i < size - 1) {
                node.next = queue.peek();
            }
            // Last node in level: next stays null (default)

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
    }

    return root;
}
```

**Approach B: O(1) space using previously-set next pointers (LC 116 optimal)**

This is the elegant solution that interviewers love. The key insight: once you've connected level `k`, you can use those `next` pointers to traverse level `k` without a queue, and while traversing level `k`, you connect level `k+1`.

```java
public Node connectO1Space(Node root) {
    if (root == null) return null;

    Node levelStart = root;  // first node of the current level

    while (levelStart != null) {
        Node curr = levelStart;       // traverse current level using next pointers
        Node nextLevelStart = null;   // first node of next level
        Node nextLevelPrev = null;    // last connected node on next level

        while (curr != null) {
            // Process left child
            if (curr.left != null) {
                if (nextLevelPrev != null) {
                    nextLevelPrev.next = curr.left;
                } else {
                    nextLevelStart = curr.left;
                }
                nextLevelPrev = curr.left;
            }
            // Process right child
            if (curr.right != null) {
                if (nextLevelPrev != null) {
                    nextLevelPrev.next = curr.right;
                } else {
                    nextLevelStart = curr.right;
                }
                nextLevelPrev = curr.right;
            }
            curr = curr.next;  // move to next node on current level (already connected!)
        }

        levelStart = nextLevelStart;  // drop down to next level
    }

    return root;
}
```

This works for both perfect trees (LC 116) and general trees (LC 117). The O(1) space comes from reusing the `next` pointers you just set on the current level to traverse it, instead of a queue.

---

## Real-World Applications

**Social network levels of connection.** "Find all friends of friends" is BFS. Level 1 is your direct friends, level 2 is their friends, and so on. LinkedIn's "2nd degree connections" is literally BFS level 2.

**Web crawler.** A crawler starts at a seed URL (root), fetches all links on that page (level 1), then fetches all links on those pages (level 2). BFS ensures you explore nearby pages before distant ones, which is useful for staying within a domain.

**Org chart processing.** Processing an org chart level by level — all VPs before all directors, all directors before all managers — is BFS. Useful for batch operations like sending announcements to each management tier.

**Circuit board signal propagation.** When a signal enters a circuit, it propagates to all directly connected components first, then to components one hop away. BFS models this naturally.

**Network broadcast.** A router broadcasting a packet to all nodes in a network uses BFS. The packet reaches all nodes at distance 1 first, then distance 2, and so on.

---

## Problem Categories and Solutions

### Category A: Basic Level Order

---

#### LC 102 — Binary Tree Level Order Traversal

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty:** Medium
**Pattern:** Standard BFS with snapshot size

**Problem:** Given the root of a binary tree, return the level-order traversal of its nodes' values as `List<List<Integer>>`.

**Approach:** Classic BFS. Snapshot `queue.size()` at the start of each level. Collect all nodes in the inner loop, then add the level list to the result.

**Complete Dry Run:**

```
Tree:
        3
       / \
      9  20
        /  \
       15   7

Initial state:
  queue = [3]
  result = []

--- Level 0 ---
  size = 1
  i=0: poll 3, level=[3], offer 9, offer 20
  queue = [9, 20]
  result = [[3]]

--- Level 1 ---
  size = 2
  i=0: poll 9, level=[9], 9 has no children
  i=1: poll 20, level=[9,20], offer 15, offer 7
  queue = [15, 7]
  result = [[3], [9, 20]]

--- Level 2 ---
  size = 2
  i=0: poll 15, level=[15], 15 has no children
  i=1: poll 7, level=[15,7], 7 has no children
  queue = []
  result = [[3], [9, 20], [15, 7]]

queue is empty, return [[3], [9, 20], [15, 7]]
```

**Solution:**

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        result.add(level);
    }

    return result;
}
```

**Complexity:** O(n) time, O(n) space.

**Edge cases:**
- Empty tree: return empty list (handled by null check).
- Single node: one level with one element.
- Skewed tree (linked list shape): n levels, each with one element.

---

#### LC 107 — Binary Tree Level Order Traversal II

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium
**Pattern:** Standard BFS, reverse result at end

**Problem:** Same as LC 102 but return the levels in bottom-up order (leaves first, root last).

**Approach:** Run standard BFS. At the end, reverse the result list. Don't try to insert at the front during BFS — that's O(n²). One `Collections.reverse()` at the end is O(n).

```java
public List<List<Integer>> levelOrderBottom(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        result.add(level);
    }

    Collections.reverse(result);  // O(n) — do this once, not inside the loop
    return result;
}
```

**Complexity:** O(n) time, O(n) space.

**Common mistake:** Adding each level to the front of the result (`result.add(0, level)`) inside the BFS loop. This is O(n) per insertion, making the total O(n²). Always reverse at the end.

---

#### LC 637 — Average of Levels in Binary Tree

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Easy
**Pattern:** BFS, accumulate sum per level

**Problem:** Return a list of the average value of nodes on each level.

**Approach:** BFS with a running sum. After the inner loop, divide by `size` to get the average.

```java
public List<Double> averageOfLevels(TreeNode root) {
    List<Double> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();
        double sum = 0;

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            sum += node.val;
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        result.add(sum / size);  // average for this level
    }

    return result;
}
```

**Complexity:** O(n) time, O(n) space.

**Note:** Use `double sum` not `int sum`. Node values can be large and a level can have many nodes. Integer overflow is possible if you accumulate in an `int`.

---

### Category B: Level-Order Variants

---

#### LC 103 — Binary Tree Zigzag Level Order Traversal

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** BFS with alternating direction flag

**Problem:** Return the level-order traversal but alternate direction each level: left-to-right for level 0, right-to-left for level 1, left-to-right for level 2, and so on.

**Approach:** BFS with a boolean `leftToRight`. When `leftToRight` is false, add each node to the front of the level list instead of the back. Flip the flag after each level.

```
Tree:
        3
       / \
      9  20
        /  \
       15   7

Level 0 (L→R): [3]
Level 1 (R→L): [20, 9]
Level 2 (L→R): [15, 7]
```

```java
public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    boolean leftToRight = true;

    while (!queue.isEmpty()) {
        int size = queue.size();
        LinkedList<Integer> level = new LinkedList<>();  // LinkedList for addFirst

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();

            if (leftToRight) {
                level.addLast(node.val);
            } else {
                level.addFirst(node.val);  // prepend for reverse order
            }

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        result.add(level);
        leftToRight = !leftToRight;
    }

    return result;
}
```

**Complexity:** O(n) time, O(n) space.

**Why `LinkedList<Integer>` for the level?** `addFirst()` is O(1) on a `LinkedList`. On an `ArrayList`, `add(0, val)` is O(n) because it shifts elements. For large levels, this matters.

**Alternative:** Use a `Deque<Integer>` as the level container. Same O(1) addFirst/addLast.

**Common mistake:** Reversing the queue traversal order instead of the output order. The queue always processes left-to-right. Only the output list changes direction.

---

#### LC 199 — Binary Tree Right Side View

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty:** Medium
**Pattern:** BFS, take last node per level

**Problem:** Imagine standing to the right of the tree. Return the values of the nodes you can see (the rightmost node at each level).

**Approach:** BFS. For each level, the last node polled from the queue is the rightmost node. Add it to the result.

```
Tree:
        1
       / \
      2   3
       \   \
        5   4

Right side view: [1, 3, 4]
  Level 0: last node = 1
  Level 1: last node = 3
  Level 2: last node = 4
```

```java
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();

            if (i == size - 1) {
                result.add(node.val);  // last node on this level
            }

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
    }

    return result;
}
```

**Complexity:** O(n) time, O(n) space.

**Tricky case:** A node is visible from the right if no other node at the same level is to its right. This is not always the rightmost child — it's the rightmost node at that depth.

```
        1
       /
      2
       \
        3

Right side view: [1, 2, 3]
  Level 0: 1
  Level 1: 2 (only node on this level)
  Level 2: 3 (only node on this level)
```

Node 3 is visible even though it's a right child of a left child. BFS handles this correctly because it processes all nodes at each depth.

**Left side view:** Same pattern, but take `i == 0` (first node per level) instead of `i == size - 1`.

---

#### LC 515 — Find Largest Value in Each Tree Row

**Companies:** Amazon, Google, Meta
**Difficulty:** Medium
**Pattern:** BFS, track max per level

**Problem:** Return a list of the largest value in each row of a binary tree.

**Approach:** BFS. For each level, track the running maximum. After the inner loop, add the max to the result.

```java
public List<Integer> largestValues(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();
        int levelMax = Integer.MIN_VALUE;

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            levelMax = Math.max(levelMax, node.val);

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        result.add(levelMax);
    }

    return result;
}
```

**Complexity:** O(n) time, O(n) space.

**Note:** Initialize `levelMax` to `Integer.MIN_VALUE`, not 0. Node values can be negative.

---

#### LC 662 — Maximum Width of Binary Tree

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** BFS with index tracking, integer overflow prevention

**Problem:** Return the maximum width of the binary tree. Width of a level is the number of nodes between the leftmost and rightmost non-null nodes (including null nodes in between).

**Key insight:** Assign indices to nodes like a heap array. If a node has index `i`, its left child has index `2*i` and its right child has index `2*i + 1`. The width of a level is `(rightmost index - leftmost index + 1)`.

```
Tree:
        1          index: 1
       / \
      3   2        indices: 2, 3
     / \   \
    5   3   9      indices: 4, 5, 6, 7

Level 0: width = 1 - 1 + 1 = 1
Level 1: width = 3 - 2 + 1 = 2
Level 2: width = 7 - 4 + 1 = 4
```

**Integer overflow problem:** For a deep tree, indices can grow to `2^depth`. At depth 32, that's 4 billion — overflows `int`. Use `long` for indices, or normalize by subtracting the leftmost index at each level.

**Normalization trick:** At the start of each level, subtract the leftmost index from all indices. This keeps indices small (starting from 0 each level) without changing the width calculation.

```java
public int widthOfBinaryTree(TreeNode root) {
    if (root == null) return 0;

    // Queue stores pairs: [node, index]
    Queue<long[]> queue = new LinkedList<>();
    queue.offer(new long[]{0, 1});  // [nodeRef encoded, index] — use a wrapper

    // Better: use a separate queue for nodes and indices
    Queue<TreeNode> nodeQueue = new LinkedList<>();
    Queue<Long> indexQueue = new LinkedList<>();
    nodeQueue.offer(root);
    indexQueue.offer(1L);

    int maxWidth = 0;

    while (!nodeQueue.isEmpty()) {
        int size = nodeQueue.size();
        long levelStart = indexQueue.peek();  // leftmost index on this level
        long first = 0, last = 0;

        for (int i = 0; i < size; i++) {
            TreeNode node = nodeQueue.poll();
            long idx = indexQueue.poll() - levelStart;  // normalize to prevent overflow

            if (i == 0) first = idx;
            if (i == size - 1) last = idx;

            if (node.left != null) {
                nodeQueue.offer(node.left);
                indexQueue.offer(idx * 2);
            }
            if (node.right != null) {
                nodeQueue.offer(node.right);
                indexQueue.offer(idx * 2 + 1);
            }
        }

        maxWidth = (int) Math.max(maxWidth, last - first + 1);
    }

    return maxWidth;
}
```

**Complexity:** O(n) time, O(n) space.

**Why normalization works:** Width is `last - first + 1`. If you subtract the same constant (the leftmost index) from both `first` and `last`, the difference doesn't change. But the absolute values stay small, preventing overflow.

**Alternative:** Use a single `Queue<int[]>` where each element is `{nodeHashCode, index}`. But storing the node reference directly is cleaner. In Java, you can use a `Pair` class or a simple wrapper.

**Cleaner implementation using a Pair:**

```java
public int widthOfBinaryTree(TreeNode root) {
    if (root == null) return 0;

    // Use Deque of int[] where [0] = node reference (via map), [1] = index
    // Simpler: just use two parallel queues
    Deque<TreeNode> nodes = new ArrayDeque<>();
    Deque<Long> indices = new ArrayDeque<>();
    nodes.offer(root);
    indices.offer(0L);

    int maxWidth = 0;

    while (!nodes.isEmpty()) {
        int size = nodes.size();
        long offset = indices.peekFirst();  // normalize by subtracting first index
        long first = 0, last = 0;

        for (int i = 0; i < size; i++) {
            TreeNode node = nodes.pollFirst();
            long idx = indices.pollFirst() - offset;

            if (i == 0) first = idx;
            last = idx;

            if (node.left != null) {
                nodes.offerLast(node.left);
                indices.offerLast(idx * 2);
            }
            if (node.right != null) {
                nodes.offerLast(node.right);
                indices.offerLast(idx * 2 + 1);
            }
        }

        maxWidth = (int) Math.max(maxWidth, last - first + 1);
    }

    return maxWidth;
}
```

---

### Category C: Minimum/Structural

---

#### LC 111 — Minimum Depth of Binary Tree

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Easy
**Pattern:** BFS, return at first leaf

**Problem:** Return the minimum depth of a binary tree. Minimum depth is the number of nodes along the shortest path from the root to the nearest leaf.

**Why BFS beats DFS here:**

DFS must traverse the entire tree. Even if it finds a leaf at depth 2, it can't return yet — there might be a leaf at depth 1 somewhere else (there isn't in a valid tree, but DFS doesn't know that without checking). DFS computes the minimum over all leaves, which requires visiting all leaves.

BFS processes level by level. The first leaf it encounters is guaranteed to be at the minimum depth. It can return immediately without processing any more nodes.

```
Tree:
        2
         \
          3
           \
            4
             \
              5
               \
                6

DFS: must traverse all 5 nodes to find the only leaf (6) at depth 5.
BFS: same — no choice here since there's only one leaf.

Tree:
        1
       / \
      2   3
     /
    4

DFS: explores 1→2→4 (depth 3), then 1→3 (depth 2). Returns min(3, 2) = 2.
     Must visit all 4 nodes.

BFS: Level 0: [1]. Level 1: [2, 3]. Node 3 is a leaf! Return depth 2.
     Visits only 3 nodes (1, 2, 3). Never visits 4.
```

For a balanced tree with n nodes, BFS stops at depth `log(n)` when it finds the first leaf. DFS visits all n nodes. The difference is O(log n) vs O(n).

```java
public int minDepth(TreeNode root) {
    if (root == null) return 0;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    int depth = 0;

    while (!queue.isEmpty()) {
        int size = queue.size();
        depth++;

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();

            // A leaf has no children — this is the minimum depth
            if (node.left == null && node.right == null) {
                return depth;  // early return — BFS guarantees this is minimum
            }

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
    }

    return depth;
}
```

**Complexity:** O(n) worst case (skewed tree), O(log n) average case for balanced tree.

**Critical edge case:** A node with only one child is NOT a leaf. Don't return early for nodes with one child.

```
        1
       /
      2

Minimum depth = 2, not 1.
Node 1 has a left child, so it's not a leaf.
Node 2 has no children, so it's the leaf at depth 2.
```

The condition `node.left == null && node.right == null` correctly handles this.

---

#### LC 958 — Check Completeness of a Binary Tree

**Companies:** Amazon, Google, Meta
**Difficulty:** Medium
**Pattern:** BFS, null detection

**Problem:** Given the root of a binary tree, determine if it is a complete binary tree. A complete binary tree has all levels fully filled except possibly the last, which is filled from left to right.

**Key insight:** In a complete binary tree, once you encounter a null node during BFS, all subsequent nodes must also be null. If you see a non-null node after a null, the tree is not complete.

```
Complete:
        1
       / \
      2   3
     / \  /
    4   5 6

BFS order: 1, 2, 3, 4, 5, 6, null, null, null
After first null, all remaining are null. Complete.

Not complete:
        1
       / \
      2   3
     /     \
    4       5

BFS order: 1, 2, 3, 4, null, null, 5
After null (right child of 2), we see 5 (right child of 3). Not complete.
```

```java
public boolean isCompleteTree(TreeNode root) {
    if (root == null) return true;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    boolean seenNull = false;

    while (!queue.isEmpty()) {
        TreeNode node = queue.poll();

        if (node == null) {
            seenNull = true;
        } else {
            if (seenNull) return false;  // non-null after null: not complete
            queue.offer(node.left);   // offer even if null
            queue.offer(node.right);  // offer even if null
        }
    }

    return true;
}
```

**Note:** This solution offers `null` nodes to the queue (unlike most BFS solutions). This is intentional — it lets us detect the null-then-non-null pattern. The queue will contain nulls, and we check for them when polling.

**Complexity:** O(n) time, O(n) space.

---

#### LC 429 — N-ary Tree Level Order Traversal

**Companies:** Amazon, Google, Microsoft
**Difficulty:** Medium
**Pattern:** BFS, iterate over children list

**Problem:** Given an n-ary tree (each node can have any number of children), return its level-order traversal.

```java
// N-ary tree node definition
class Node {
    int val;
    List<Node> children;
    Node(int val) {
        this.val = val;
        this.children = new ArrayList<>();
    }
}

public List<List<Integer>> levelOrder(Node root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<Node> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Node node = queue.poll();
            level.add(node.val);

            // Add all children (not just left/right)
            for (Node child : node.children) {
                if (child != null) queue.offer(child);
            }
        }

        result.add(level);
    }

    return result;
}
```

**Complexity:** O(n) time, O(n) space.

**The only difference from binary tree BFS:** Instead of `if (node.left != null)` and `if (node.right != null)`, you iterate over `node.children`. The snapshot-size pattern is identical.

---

### Category D: Next Pointer

---

#### LC 116 — Populating Next Right Pointers in Each Node

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** BFS with next pointer, O(1) space solution

**Problem:** Given a perfect binary tree (all leaves at the same level, every parent has two children), populate each node's `next` pointer to point to its next right node. If there is no next right node, set `next` to null.

**Approach 1: Standard BFS (O(n) space)**

```java
public Node connect(Node root) {
    if (root == null) return null;

    Queue<Node> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();

        for (int i = 0; i < size; i++) {
            Node node = queue.poll();

            // Connect to the next node in the queue, but only within this level
            if (i < size - 1) {
                node.next = queue.peek();
            }

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
    }

    return root;
}
```

**Approach 2: O(1) space using previously-set next pointers**

This is the solution interviewers want for LC 116. The key insight:

> Once you've connected all nodes on level `k`, you can traverse level `k` using those `next` pointers. While traversing level `k`, you connect level `k+1`. No queue needed.

For a perfect binary tree, connecting level `k+1` from level `k` is straightforward:
- For each node `curr` on level `k`:
  - `curr.left.next = curr.right` (left child connects to right child of same parent)
  - `curr.right.next = curr.next.left` (right child connects to left child of next parent, if `curr.next` exists)

```
Level k:    A → B → C → null
            |   |   |
Level k+1: AL AR BL BR CL CR

Connections to make:
  AL.next = AR  (A.left.next = A.right)
  AR.next = BL  (A.right.next = A.next.left = B.left)
  BL.next = BR  (B.left.next = B.right)
  BR.next = CL  (B.right.next = B.next.left = C.left)
  CL.next = CR  (C.left.next = C.right)
  CR.next = null (C.next is null, so no connection)
```

```java
public Node connectO1(Node root) {
    if (root == null) return null;

    Node levelStart = root;

    while (levelStart.left != null) {  // while there's a next level
        Node curr = levelStart;

        while (curr != null) {
            // Connect left child to right child (same parent)
            curr.left.next = curr.right;

            // Connect right child to left child of next parent
            if (curr.next != null) {
                curr.right.next = curr.next.left;
            }

            curr = curr.next;  // move right on current level using next pointer
        }

        levelStart = levelStart.left;  // drop to next level
    }

    return root;
}
```

**Complexity:** O(n) time, O(1) space.

**Why this only works for perfect trees:** The O(1) solution relies on `curr.left` and `curr.right` always existing. In a perfect tree, every non-leaf has exactly two children. For general trees (LC 117), you need a more complex approach.

**Dry run on a small perfect tree:**

```
Initial:
        1
       / \
      2   3
     / \ / \
    4  5 6  7

All next pointers are null initially.

--- Level 0 (levelStart = 1) ---
curr = 1:
  1.left.next = 1.right  →  2.next = 3
  1.next is null, skip right-to-left connection
  curr = 1.next = null
levelStart = 1.left = 2

--- Level 1 (levelStart = 2) ---
curr = 2:
  2.left.next = 2.right  →  4.next = 5
  2.next = 3, so 2.right.next = 2.next.left  →  5.next = 6
  curr = 2.next = 3
curr = 3:
  3.left.next = 3.right  →  6.next = 7
  3.next is null, skip
  curr = 3.next = null
levelStart = 2.left = 4

--- Level 2 (levelStart = 4) ---
4.left is null, while condition fails, stop.

Final next pointers:
  2.next = 3
  4.next = 5, 5.next = 6, 6.next = 7
```

---

#### LC 117 — Populating Next Right Pointers in Each Node II

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium
**Pattern:** BFS with next pointer, general tree

**Problem:** Same as LC 116 but the tree is not necessarily perfect. Some nodes may have only one child or no children.

**Approach 1: Standard BFS (same as LC 116 Approach 1)**

The queue-based BFS solution from LC 116 works unchanged for general trees. The `queue.peek()` trick handles any tree structure.

**Approach 2: O(1) space for general trees**

For general trees, you can't assume `curr.left` and `curr.right` exist. You need to find the first non-null child of any node on the current level to start the next level.

```java
public Node connectGeneral(Node root) {
    if (root == null) return null;

    Node levelStart = root;

    while (levelStart != null) {
        Node curr = levelStart;
        Node nextLevelDummy = new Node(0);  // dummy head for next level's linked list
        Node nextLevelTail = nextLevelDummy;

        while (curr != null) {
            if (curr.left != null) {
                nextLevelTail.next = curr.left;
                nextLevelTail = curr.left;
            }
            if (curr.right != null) {
                nextLevelTail.next = curr.right;
                nextLevelTail = curr.right;
            }
            curr = curr.next;
        }

        levelStart = nextLevelDummy.next;  // first node of next level
    }

    return root;
}
```

**The dummy node trick:** Instead of tracking `nextLevelStart` and `nextLevelPrev` separately (as in Template 3), use a dummy head node. Append all children to the dummy's linked list. After processing the current level, `dummy.next` is the first node of the next level.

**Complexity:** O(n) time, O(1) space.

**Why this is harder than LC 116:** In LC 116, every node has exactly two children, so you always know where the next level starts (it's `levelStart.left`). In LC 117, a node might have no children, so you need to scan forward to find the first child.

---

## Common Mistakes

### 1. Not Snapshotting queue.size() at Level Start

**Wrong:**
```java
while (!queue.isEmpty()) {
    for (int i = 0; i < queue.size(); i++) {  // BUG: queue.size() changes during loop
        TreeNode node = queue.poll();
        // ...
        if (node.left != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
}
```

**Why it's wrong:** `queue.size()` is evaluated on every iteration of the for loop. As you add children, the size grows, so the loop runs more iterations than intended. You end up processing nodes from the next level in the current level's loop.

**Right:**
```java
int size = queue.size();  // snapshot BEFORE the loop
for (int i = 0; i < size; i++) {
    // ...
}
```

---

### 2. Using BFS for Maximum Depth (Unnecessary)

BFS can compute max depth (count the number of levels), but DFS is simpler and uses less code. BFS must traverse the entire tree to count all levels. DFS does the same but with a cleaner recursive structure.

Use BFS for **minimum** depth (early return at first leaf). Use DFS for **maximum** depth.

---

### 3. Confusing Minimum Depth with Maximum Depth

**Wrong minimum depth:**
```java
// This computes maximum depth, not minimum
public int minDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(minDepth(root.left), minDepth(root.right));  // WRONG
}
```

**Also wrong:**
```java
// This fails for nodes with one child
public int minDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.min(minDepth(root.left), minDepth(root.right));  // WRONG for one-child nodes
}
```

For a node with only a left child, `minDepth(root.right)` returns 0, making the minimum 1. But the node is not a leaf — you must go deeper.

**Correct DFS minimum depth:**
```java
public int minDepth(TreeNode root) {
    if (root == null) return 0;
    if (root.left == null) return 1 + minDepth(root.right);   // only right subtree
    if (root.right == null) return 1 + minDepth(root.left);   // only left subtree
    return 1 + Math.min(minDepth(root.left), minDepth(root.right));  // both subtrees
}
```

But BFS is still better for minimum depth in practice (early termination).

---

### 4. Offering Null Nodes to the Queue (Usually Wrong)

Most BFS solutions should only offer non-null nodes:

```java
if (node.left != null) queue.offer(node.left);   // correct
if (node.right != null) queue.offer(node.right); // correct
```

The exception is LC 958 (Check Completeness), where you intentionally offer null nodes to detect the null-then-non-null pattern.

If you accidentally offer null nodes in other problems, you'll get a NullPointerException when you try to access `node.val` or `node.left` on the polled null.

---

### 5. Integer Overflow in LC 662 (Maximum Width)

Node indices grow as `2^depth`. At depth 31, `2^31` overflows a signed 32-bit integer. Always use `long` for indices in LC 662.

```java
// Wrong: int overflow at depth > 30
int leftIdx = 2 * parentIdx;

// Right: use long
long leftIdx = 2L * parentIdx;
```

Also normalize indices at each level (subtract the leftmost index) to keep values small even for deep trees.

---

### 6. Wrong Zigzag Direction

The zigzag starts left-to-right at level 0 (the root). Level 1 is right-to-left. Level 2 is left-to-right again.

A common mistake is starting with `leftToRight = false`, which reverses the entire output.

Another mistake: reversing the order you add children to the queue. The queue always processes left-to-right. Only the output list changes direction.

---

### 7. Off-by-One in Right Side View

The right side view takes the last node at each level. The last node has index `i == size - 1` in the inner loop.

```java
if (i == size - 1) result.add(node.val);  // correct
if (i == size) result.add(node.val);      // wrong: never true (i goes 0 to size-1)
```

---

## Pattern Comparison: DFS vs BFS

### Side-by-Side

| Aspect | DFS | BFS |
|--------|-----|-----|
| Data structure | Stack (call stack or explicit) | Queue |
| Order | Depth-first (go deep before wide) | Breadth-first (go wide before deep) |
| Space | O(h) where h = height | O(w) where w = max width |
| Best for | Path problems, depth, structure | Level problems, shortest path |
| Minimum depth | O(n) — must check all leaves | O(log n) avg — stops at first leaf |
| Level grouping | Requires extra depth parameter | Natural — inner loop = one level |
| Implementation | Recursive (clean) or iterative | Always iterative (queue) |

### Space Complexity Comparison

DFS uses O(h) space where h is the tree height. For a balanced tree, h = O(log n). For a skewed tree (linked list), h = O(n).

BFS uses O(w) space where w is the maximum width. For a complete binary tree, the last level has n/2 nodes, so w = O(n). For a skewed tree, w = O(1).

So for balanced trees: DFS uses O(log n) space, BFS uses O(n) space. DFS wins on space.
For skewed trees: DFS uses O(n) space, BFS uses O(1) space. BFS wins on space.

In practice, most interview trees are balanced or near-balanced, so DFS is more space-efficient.

### When BFS is Strictly Better

1. **Minimum depth:** BFS returns at the first leaf. DFS must visit all leaves.
2. **Level-order output:** BFS naturally groups nodes by level. DFS requires passing depth as a parameter and grouping by depth in a map.
3. **Connecting nodes at the same level:** BFS processes nodes in level order, making it natural to connect adjacent nodes.
4. **Shortest path in unweighted graphs:** BFS guarantees the shortest path. DFS does not.

### When DFS is Strictly Better

1. **Path sum problems:** DFS carries the running sum down the path. BFS would need to store the sum with each node in the queue.
2. **Tree structure problems (diameter, balanced):** These require combining results from both subtrees, which is natural in DFS recursion.
3. **BST problems:** Inorder DFS on a BST produces sorted output. BFS doesn't have this property.
4. **Serialization:** Preorder DFS with null markers is the standard serialization format.

### The One-Line Rule

> If the problem asks "what's on level X?" or "what's the shortest path?", use BFS.
> If the problem asks "what's the path from root to X?" or "what's the structure of the subtree?", use DFS.

---

## Quick Reference Cheat Sheet

### BFS Skeleton

```java
Queue<TreeNode> queue = new LinkedList<>();
queue.offer(root);

while (!queue.isEmpty()) {
    int size = queue.size();  // ALWAYS snapshot before inner loop

    for (int i = 0; i < size; i++) {
        TreeNode node = queue.poll();
        // process node
        if (node.left != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
    // after inner loop: one full level processed
}
```

### Problem-to-Pattern Map

| Problem | Key Addition to Skeleton |
|---------|--------------------------|
| LC 102 Level Order | Collect `node.val` per level into a list |
| LC 107 Bottom-Up | Same as 102, then `Collections.reverse(result)` |
| LC 637 Averages | Accumulate `sum`, divide by `size` after inner loop |
| LC 103 Zigzag | `LinkedList` level, `addFirst` or `addLast` based on flag |
| LC 199 Right Side View | `if (i == size - 1) result.add(node.val)` |
| LC 515 Largest per Row | Track `levelMax = Integer.MIN_VALUE`, update in inner loop |
| LC 111 Min Depth | Return `depth` when `node.left == null && node.right == null` |
| LC 116/117 Next Pointer | `if (i < size - 1) node.next = queue.peek()` |
| LC 662 Width | Two parallel queues: nodes + `long` indices, normalize each level |
| LC 958 Completeness | Offer nulls to queue, return false if non-null after null |
| LC 429 N-ary | `for (Node child : node.children) queue.offer(child)` |

### Complexity Summary

| Problem | Time | Space |
|---------|------|-------|
| LC 102, 107, 637, 103, 199, 515 | O(n) | O(n) |
| LC 111 Min Depth | O(n) worst, O(log n) avg | O(n) worst, O(log n) avg |
| LC 116 Next Pointer (O(1) space) | O(n) | O(1) |
| LC 117 Next Pointer (O(1) space) | O(n) | O(1) |
| LC 662 Width | O(n) | O(n) |
| LC 958 Completeness | O(n) | O(n) |
| LC 429 N-ary | O(n) | O(n) |

### Common Patterns at a Glance

```
Level grouping:     int size = queue.size() before inner loop
Zigzag:             LinkedList level + addFirst/addLast + boolean flag
Right side view:    if (i == size - 1) take node
Min depth:          return depth when leaf found (no children)
Width:              two queues (nodes + long indices), normalize each level
Completeness:       offer nulls, return false on non-null after null
Next pointer O(1):  use curr.next to traverse current level, connect next level
```

---

## Practice Roadmap

### Week 1: Foundation (Days 1-4)

**Goal:** Master the snapshot-size pattern and basic level-order variants.

**Day 1:** LC 102 — Level Order Traversal
- Write the solution from scratch without looking at notes.
- Dry run on a 3-level tree.
- Verify: does your solution handle null root? Single node? Skewed tree?

**Day 2:** LC 107 + LC 637
- LC 107: Level order bottom-up. Write it, then ask: why is `Collections.reverse()` at the end better than `result.add(0, level)` inside the loop?
- LC 637: Averages. Focus on using `double sum` and dividing by `size` after the inner loop.

**Day 3:** LC 103 — Zigzag
- Understand why you use `LinkedList<Integer>` for the level (O(1) addFirst).
- Trace through a 3-level tree manually, alternating directions.

**Day 4:** LC 199 — Right Side View
- Write it. Then write the left side view variant (take `i == 0`).
- Think about: what if the tree has a node only on the left at some level? Does your solution still work?

---

### Week 2: Variants and Structural (Days 5-8)

**Goal:** Handle structural problems and the minimum depth insight.

**Day 5:** LC 111 — Minimum Depth
- Write the BFS solution with early return.
- Write the DFS solution (handling one-child nodes correctly).
- Convince yourself BFS is faster for balanced trees.

**Day 6:** LC 515 + LC 429
- LC 515: Largest value per row. Straightforward — focus on `Integer.MIN_VALUE` initialization.
- LC 429: N-ary tree. The only change is iterating over `node.children`.

**Day 7:** LC 958 — Check Completeness
- This one is tricky. Understand why you offer null nodes to the queue.
- Trace through a complete tree and an incomplete tree.

**Day 8:** Review Week 1 and 2
- Re-solve LC 102 and LC 103 from memory.
- Time yourself: can you write LC 102 in under 5 minutes?

---

### Week 3: Hard Variants (Days 9-12)

**Goal:** Master index tracking and next-pointer problems.

**Day 9:** LC 116 — Next Right Pointers
- Write the BFS solution first (O(n) space).
- Then write the O(1) space solution.
- Dry run the O(1) solution on a 3-level perfect tree.

**Day 10:** LC 117 — Next Right Pointers II
- Start from the LC 116 O(1) solution. What breaks for general trees?
- Implement the dummy node approach.

**Day 11:** LC 662 — Maximum Width
- This is the hardest BFS tree problem in this set.
- Understand the index assignment (heap-style: left = 2i, right = 2i+1).
- Understand why overflow happens and why normalization fixes it.
- Trace through a tree with a gap (null node in the middle of a level).

**Day 12:** Mock interview
- Pick 2 problems at random from this set.
- Solve them under time pressure (20 minutes each).
- Explain your approach out loud as you code.

---

### Problem Difficulty Ordering

```
Easy:    LC 637, LC 515, LC 111, LC 107
Medium:  LC 102, LC 103, LC 199, LC 429, LC 116, LC 117, LC 958
Hard:    LC 662
```

### Must-Know for FAANG

If you only have time for 5 problems, do these:

1. **LC 102** — The foundation. Every other BFS tree problem builds on this.
2. **LC 103** — Zigzag. Amazon and Google love this one.
3. **LC 199** — Right side view. Appears at almost every company.
4. **LC 116** — Next pointer with O(1) space. Tests deep understanding.
5. **LC 662** — Width with index tracking. Tests overflow awareness.

---

### Connection to Graph BFS (Topic 14 Preview)

The BFS pattern you've learned here transfers directly to graph problems. The only differences:

1. Trees have no cycles. Graphs can have cycles, so you need a `visited` set.
2. Trees have a root. Graphs may need BFS from multiple sources simultaneously (multi-source BFS).
3. Trees have parent-child relationships. Graphs have arbitrary edges.

The core loop is identical:

```java
// Graph BFS (preview)
Queue<Integer> queue = new LinkedList<>();
Set<Integer> visited = new HashSet<>();
queue.offer(start);
visited.add(start);

while (!queue.isEmpty()) {
    int size = queue.size();  // same snapshot pattern

    for (int i = 0; i < size; i++) {
        int node = queue.poll();
        // process node

        for (int neighbor : graph.get(node)) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.offer(neighbor);
            }
        }
    }
}
```

The `visited` set replaces the null check. Everything else is the same.

---

## Interview Talking Points

These are the things interviewers specifically listen for. Saying them out loud signals mastery.

### On the snapshot pattern

"I'm snapshotting `queue.size()` before the inner loop. This is critical — if I read `queue.size()` inside the loop condition, it changes as I add children, and I'd mix nodes from different levels. The snapshot gives me a fixed count for exactly one level."

### On BFS vs DFS choice

"This problem asks for the minimum depth, so I'm using BFS. BFS processes level by level and returns the moment it finds the first leaf — that's guaranteed to be the shallowest leaf. DFS would have to visit every leaf to find the minimum, which is O(n). BFS is O(log n) on a balanced tree."

### On the O(1) space solution for LC 116

"The key insight is that once I've connected level k, I can traverse level k using those next pointers — no queue needed. While traversing level k, I connect level k+1. I'm reusing the structure I just built to avoid allocating a queue."

### On integer overflow in LC 662

"Node indices grow as 2^depth. At depth 31, that's 2 billion — overflows a 32-bit int. I'm using `long` for indices. I also normalize at each level by subtracting the leftmost index. This keeps values small without changing the width calculation, since width is a difference."

### On null handling

"I only offer non-null nodes to the queue. If I accidentally offer null, I'll get a NullPointerException when I try to access `node.val` in the next iteration. The exception is LC 958, where I intentionally offer nulls to detect the null-then-non-null pattern that indicates an incomplete tree."

---

## Recognizing BFS Problems in Disguise

Some problems don't say "level order" but are still BFS problems. Here's how to recognize them.

### Signal words that mean BFS

```
"level by level"         → BFS
"row by row"             → BFS
"layer by layer"         → BFS
"minimum steps"          → BFS (shortest path)
"minimum distance"       → BFS
"nearest"                → BFS
"closest"                → BFS
"shortest path"          → BFS
"fewest operations"      → BFS
"connect nodes"          → BFS with next pointer
"same level"             → BFS
"same depth"             → BFS
"width"                  → BFS with index tracking
"complete binary tree"   → BFS (null detection)
```

### Problems that look like DFS but are BFS

**"Find the minimum depth of a binary tree"**
Sounds like a depth problem (DFS), but BFS is optimal because it returns at the first leaf.

**"Right side view of a binary tree"**
Sounds like a structural problem (DFS), but BFS naturally gives you the last node per level.

**"Check if a binary tree is complete"**
Sounds like a structural check (DFS), but BFS with null detection is the cleanest approach.

**"Connect nodes at the same level"**
Sounds like a pointer manipulation problem, but BFS processes nodes in level order, making connections natural.

### The disguise test

Ask yourself: "Does the answer depend on which level a node is on, or on the horizontal position of a node?" If yes, it's BFS. If the answer depends on the path from root to a node, it's DFS.

---

## Detailed Complexity Analysis

### Why BFS space is O(n) in the worst case

The queue holds at most one full level at a time. For a complete binary tree with n nodes, the last level has `ceil(n/2)` nodes. So the queue can hold up to n/2 nodes simultaneously, which is O(n).

For a skewed tree (all nodes in a single chain), each level has exactly one node. The queue never holds more than 1 node. Space is O(1).

For a balanced binary tree, the widest level has O(n) nodes (the last level). Space is O(n).

### Why DFS space is O(h)

DFS uses the call stack (or an explicit stack). The maximum stack depth equals the tree height h. For a balanced tree, h = O(log n). For a skewed tree, h = O(n).

### Space comparison table

```
Tree type          | BFS space | DFS space
-------------------|-----------|----------
Balanced           | O(n)      | O(log n)
Complete           | O(n)      | O(log n)
Skewed (chain)     | O(1)      | O(n)
Perfect            | O(n)      | O(log n)
```

For most interview trees (balanced or near-balanced), DFS uses less space. But BFS is still preferred when the problem requires level-order processing.

### Time complexity is always O(n)

Both BFS and DFS visit every node exactly once. Time is O(n) for both, regardless of tree shape.

The exception: BFS for minimum depth can be O(log n) on a balanced tree because it returns early at the first leaf. DFS for minimum depth is always O(n).

---

## Extended Problem Notes

### LC 102 — Why the inner loop is the key abstraction

The inner `for` loop is the fundamental unit of BFS tree processing. Everything else is built on top of it:

- **LC 107:** Same inner loop, reverse the outer list at the end.
- **LC 637:** Same inner loop, accumulate sum and divide by size.
- **LC 103:** Same inner loop, use `addFirst` or `addLast` based on a flag.
- **LC 199:** Same inner loop, take the last node.
- **LC 515:** Same inner loop, track the maximum.
- **LC 111:** Same inner loop, return early when a leaf is found.

Once you internalize LC 102, every other BFS tree problem is a small modification.

### LC 103 — Why not reverse the queue traversal order?

A common wrong approach: for right-to-left levels, add children in reverse order (right first, then left) to the queue. This doesn't work.

```
Tree:
        1
       / \
      2   3
     / \ / \
    4  5 6  7

Level 1 should be [3, 2] (right-to-left).

Wrong approach: add right child first, then left child.
Queue after processing level 0: [3, 2]
Processing level 1: poll 3, poll 2. Level = [3, 2]. Correct so far.

But now the queue has: [6, 7, 4, 5] (3's children first, then 2's children).
Level 2 should be [4, 5, 6, 7] (left-to-right).
Processing level 2: poll 6, poll 7, poll 4, poll 5. Level = [6, 7, 4, 5]. WRONG.
```

The correct approach: always add children left-to-right. Only change the output order using `addFirst`/`addLast`.

### LC 662 — Dry run with a gap

```
Tree:
        1
       / \
      3   2
     /     \
    5       9
   / \
  6   7

Indices (heap-style):
        1 (idx=1)
       / \
      3   2 (idx=2, 3)
     /     \
    5       9 (idx=4, 7)
   / \
  6   7 (idx=8, 9)

Level 0: [1], indices=[1], width = 1-1+1 = 1
Level 1: [3,2], indices=[2,3], width = 3-2+1 = 2
Level 2: [5,9], indices=[4,7], width = 7-4+1 = 4
Level 3: [6,7], indices=[8,9], width = 9-8+1 = 2

Maximum width = 4 (level 2, even though node at index 5 and 6 are null)
```

The width counts null nodes between the leftmost and rightmost non-null nodes. That's why level 2 has width 4 even though only 2 nodes exist.

Normalization at level 2: offset = 4 (leftmost index). Normalized indices: 5→1, 9→3. Width = 3-0+1 = 4. Same result, no overflow.

### LC 958 — Why offer null nodes?

The standard BFS pattern skips null children. LC 958 breaks this rule intentionally.

The completeness condition is: in BFS order, once you see a null, all subsequent nodes must also be null. To check this, you need to see the nulls in BFS order. So you offer null children to the queue.

```java
// Standard BFS: skip nulls
if (node.left != null) queue.offer(node.left);
if (node.right != null) queue.offer(node.right);

// LC 958: offer nulls too
queue.offer(node.left);   // may be null
queue.offer(node.right);  // may be null
```

When you poll a null, set `seenNull = true`. If you then poll a non-null, return false.

### LC 116 vs LC 117 — The key difference

LC 116 (perfect tree): Every non-leaf has exactly two children. You can always do `curr.left.next = curr.right` without null checks. The O(1) space solution is clean.

LC 117 (general tree): A node might have 0, 1, or 2 children. You can't assume `curr.left` or `curr.right` exist. The O(1) space solution needs the dummy node trick to handle arbitrary child configurations.

The queue-based BFS solution (O(n) space) works for both without modification. The `queue.peek()` trick handles any tree structure because you're just connecting to whatever is next in the queue.

---

## Patterns That Combine BFS with Other Techniques

### BFS + HashMap (Nodes at Distance K — LC 863)

Some problems require BFS from a non-root node. The trick: first build a parent pointer map using DFS, then run BFS from the target node treating the tree as an undirected graph.

```java
// Step 1: Build parent map with DFS
Map<TreeNode, TreeNode> parent = new HashMap<>();
void buildParentMap(TreeNode node, TreeNode par) {
    if (node == null) return;
    parent.put(node, par);
    buildParentMap(node.left, node);
    buildParentMap(node.right, node);
}

// Step 2: BFS from target, treating tree as undirected graph
Queue<TreeNode> queue = new LinkedList<>();
Set<TreeNode> visited = new HashSet<>();
queue.offer(target);
visited.add(target);
int dist = 0;

while (!queue.isEmpty()) {
    if (dist == k) {
        // all nodes in queue are at distance k
        break;
    }
    int size = queue.size();
    for (int i = 0; i < size; i++) {
        TreeNode node = queue.poll();
        // explore left, right, and parent
        if (node.left != null && !visited.contains(node.left)) {
            visited.add(node.left);
            queue.offer(node.left);
        }
        if (node.right != null && !visited.contains(node.right)) {
            visited.add(node.right);
            queue.offer(node.right);
        }
        TreeNode par = parent.get(node);
        if (par != null && !visited.contains(par)) {
            visited.add(par);
            queue.offer(par);
        }
    }
    dist++;
}
```

This pattern — DFS to build parent pointers, then BFS from a non-root node — appears in several FAANG problems.

### BFS + Serialization

BFS order is the natural serialization format for complete binary trees (used in heap arrays). For general trees, preorder DFS with null markers is more common. But BFS serialization is useful when you need to reconstruct a tree from its level-order representation.

```java
// BFS serialization
String serialize(TreeNode root) {
    if (root == null) return "null";
    StringBuilder sb = new StringBuilder();
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        TreeNode node = queue.poll();
        if (node == null) {
            sb.append("null,");
        } else {
            sb.append(node.val).append(",");
            queue.offer(node.left);   // offer nulls for completeness
            queue.offer(node.right);
        }
    }
    return sb.toString();
}
```

---

## Final Notes

### The one thing to remember

If you forget everything else, remember this: `int size = queue.size()` before the inner loop. That single line is the difference between a correct BFS tree solution and a broken one.

### The progression

1. LC 102 teaches you the pattern.
2. LC 103, 199, 637, 515 teach you to modify the pattern.
3. LC 111 teaches you early termination.
4. LC 116, 117 teach you to eliminate the queue entirely.
5. LC 662 teaches you index tracking and overflow prevention.
6. LC 958 teaches you to use nulls as signals.

Each problem adds one new idea on top of the previous ones. Master them in order.

### What interviewers actually test

At FAANG, BFS tree problems test three things:

1. **Do you know the snapshot pattern?** (LC 102, 103, 199)
2. **Do you understand why BFS is better than DFS for minimum depth?** (LC 111)
3. **Can you eliminate the queue for O(1) space?** (LC 116)

If you can answer all three clearly and code them correctly, you pass the BFS section of any tree interview.

---

*Document 11 of 20. Next: Topic 12 — Tries.*
