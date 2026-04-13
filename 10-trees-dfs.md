# Topic 10: Trees — DFS (Depth-First Search)

> **Series position:** Document 10 of 20
> **Difficulty range:** Easy to Hard
> **Interview frequency:** Trees appear in ~25% of FAANG interviews. DFS is the primary traversal technique.
> **Top companies:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, Uber

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5](#eli5)
3. [When to Use](#when-to-use)
4. [Core Templates in Java](#core-templates-in-java)
5. [Real-World Applications](#real-world-applications)
6. [Problem Categories and Solutions](#problem-categories-and-solutions)
7. [Common Mistakes](#common-mistakes)
8. [Pattern Comparison](#pattern-comparison)
9. [Quick Reference](#quick-reference)
10. [Practice Roadmap](#practice-roadmap)

---

## Core Concept

### Binary Tree Basics

A binary tree is a hierarchical data structure where each node has at most two children, called `left` and `right`.

```
Key terminology:
- Node: a single element with a value and optional left/right children
- Root: the topmost node (no parent)
- Leaf: a node with no children (left == null && right == null)
- Height: longest path from a node down to a leaf
- Depth: distance from root to a given node
- Subtree: a node and all its descendants
```

Standard Java node definition used throughout this document:

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}
```

### The Three DFS Traversal Orders

DFS explores as deep as possible before backtracking. The three orders differ only in *when* you process the current node relative to its children.

```
Tree used for examples:
        4
       / \
      2   6
     / \ / \
    1  3 5  7
```

**Inorder (Left - Root - Right):**
```
Visit left subtree, then current node, then right subtree.
Result: 1, 2, 3, 4, 5, 6, 7

Critical property: Inorder traversal of a BST produces a sorted sequence.
Use when: BST problems, finding kth smallest, validating BST.
```

**Preorder (Root - Left - Right):**
```
Visit current node, then left subtree, then right subtree.
Result: 4, 2, 1, 3, 6, 5, 7

Critical property: Preorder captures the tree structure — root always comes first.
Use when: Serialization, tree construction, copying a tree.
```

**Postorder (Left - Right - Root):**
```
Visit left subtree, then right subtree, then current node.
Result: 1, 3, 2, 5, 7, 6, 4

Critical property: Children are processed before their parent (bottom-up).
Use when: Deletion, computing height/diameter, any problem needing subtree info before parent.
```

### Recursive vs Iterative DFS

**Recursive DFS** uses the call stack implicitly. Clean, concise, and natural for tree problems. Risk: stack overflow on very deep (unbalanced) trees.

**Iterative DFS** uses an explicit `Deque<TreeNode>` (stack). Avoids stack overflow. Required when the interviewer asks for O(1) extra space or when the tree is pathologically deep.

Rule of thumb: start with recursive. Switch to iterative if asked, or if the tree could be a degenerate linked list.

### The Two Core Patterns

These two patterns appear in almost every tree DFS problem. Recognizing which one applies is the key skill.

---

**Pattern 1: Top-Down (pass information DOWN via parameters)**

You carry information from parent to child. The function signature includes extra parameters that accumulate state as you go deeper.

```
When to use:
- You need context from ancestors (e.g., current path sum, min/max bounds)
- The answer is computed at the leaves or during descent
- Examples: Path Sum, Validate BST, Path Sum II
```

Skeleton:
```java
void dfs(TreeNode node, int currentSum, List<Integer> path) {
    if (node == null) return;
    // use info passed from parent
    currentSum += node.val;
    path.add(node.val);
    // recurse, passing updated info down
    dfs(node.left, currentSum, path);
    dfs(node.right, currentSum, path);
    path.remove(path.size() - 1); // backtrack
}
```

---

**Pattern 2: Bottom-Up (return information UP via return value)**

Each call computes something about its subtree and returns it to the parent. The parent combines results from both children.

```
When to use:
- You need information from subtrees to compute the answer at a node
- The answer depends on both left and right subtrees
- Examples: Max Depth, Diameter, Balanced Tree, Max Path Sum
```

Skeleton:
```java
int dfs(TreeNode node) {
    if (node == null) return 0; // base case returns a neutral value
    int left = dfs(node.left);   // get info from left subtree
    int right = dfs(node.right); // get info from right subtree
    // combine and return to parent
    return 1 + Math.max(left, right);
}
```

---

**Pattern 3: Global Variable for Cross-Branch Answers**

Some problems require comparing paths that span both left and right subtrees (like diameter or max path sum). The recursive function returns a single-branch value, but a class-level variable tracks the best answer seen so far.

```
When to use:
- The optimal answer might "bend" through a node (use both left and right children)
- The return value and the answer are different things
- Examples: Diameter of Binary Tree, Binary Tree Maximum Path Sum
```

Skeleton:
```java
int answer = 0; // class-level

int dfs(TreeNode node) {
    if (node == null) return 0;
    int left = dfs(node.left);
    int right = dfs(node.right);
    answer = Math.max(answer, left + right); // update global with both branches
    return Math.max(left, right);            // return only one branch to parent
}
```

This distinction between "what I return" and "what I update globally" is the hardest concept in tree DFS. LC 124 and LC 543 both hinge on it.

---

## ELI5

**The maze analogy:** Imagine exploring a maze by always going as deep as possible before backtracking. You pick a direction, keep going until you hit a dead end, then retrace your steps to the last fork and try a different path. DFS on a tree works exactly this way — you go all the way down the left side before touching the right side.

**The book analogy:** Reading a book's table of contents recursively. You open Chapter 1, then go into Section 1.1, then into 1.1.1, read it, come back to 1.1.2, read it, come back to 1.2, and so on. You finish all of Chapter 1 before starting Chapter 2. That's preorder DFS.

**The "report to manager" analogy for bottom-up:** Every employee computes their team's stats and reports to their manager. The manager combines reports from all direct reports and sends a summary up. The CEO (root) gets the final combined answer. This is postorder / bottom-up DFS.

---

## When to Use

### Green Flags (reach for DFS)

```
"binary tree"           → almost always DFS
"BST"                   → DFS with sorted property
"path sum"              → top-down DFS with running sum
"max depth / height"    → bottom-up DFS returning height
"validate BST"          → top-down DFS with min/max bounds
"lowest common ancestor"→ bottom-up postorder DFS
"serialize/deserialize" → preorder DFS with null markers
"inorder/preorder/postorder" → direct traversal
"diameter"              → bottom-up, global variable pattern
"balanced tree"         → bottom-up height check
"subtree"               → recursive containment
"construct tree from traversals" → preorder + inorder reconstruction
"flatten to linked list"→ reverse preorder or Morris
```

### Red Flags (don't use DFS, use BFS instead)

```
"level order traversal"     → BFS (Topic 11)
"minimum depth"             → BFS (finds shallowest leaf faster)
"shortest path in tree"     → BFS
"nodes at distance K"       → BFS from target node
"right side view"           → BFS (last node per level)
"zigzag level order"        → BFS with direction flag
```

### The Decision Rule

> If the problem cares about **depth/path/structure**, use DFS.
> If the problem cares about **levels/layers/breadth**, use BFS.

---

## Core Templates in Java

### Template 1: Recursive DFS — All Three Traversal Orders

```java
// Inorder: Left -> Root -> Right
// On a BST, this produces sorted output.
public List<Integer> inorder(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    inorderHelper(root, result);
    return result;
}

private void inorderHelper(TreeNode node, List<Integer> result) {
    if (node == null) return;          // base case
    inorderHelper(node.left, result);  // go left
    result.add(node.val);              // process current
    inorderHelper(node.right, result); // go right
}

// Preorder: Root -> Left -> Right
// Captures tree structure; root always first.
private void preorderHelper(TreeNode node, List<Integer> result) {
    if (node == null) return;
    result.add(node.val);               // process current FIRST
    preorderHelper(node.left, result);
    preorderHelper(node.right, result);
}

// Postorder: Left -> Right -> Root
// Children processed before parent; used for deletion, height.
private void postorderHelper(TreeNode node, List<Integer> result) {
    if (node == null) return;
    postorderHelper(node.left, result);
    postorderHelper(node.right, result);
    result.add(node.val);               // process current LAST
}
```

**Tree trace for inorder on the example tree:**
```
        4
       / \
      2   6
     / \ / \
    1  3 5  7

Call stack (→ means recurse into):
inorder(4)
  → inorder(2)
      → inorder(1)
          → inorder(null) return
          add 1
          → inorder(null) return
      add 2
      → inorder(3)
          → inorder(null) return
          add 3
          → inorder(null) return
  add 4
  → inorder(6)
      → inorder(5)
          add 5
      add 6
      → inorder(7)
          add 7

Result: [1, 2, 3, 4, 5, 6, 7]  ← sorted, because it's a BST
```

---

### Template 2: Iterative DFS with Explicit Stack

Iterative inorder is the most commonly asked iterative traversal. It's trickier than preorder because you can't process the node when you first visit it.

```java
// Iterative Inorder — critical for BST problems
// Pattern: go left as far as possible, process, then go right
public List<Integer> inorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode curr = root;

    while (curr != null || !stack.isEmpty()) {
        // Push all left nodes onto stack
        while (curr != null) {
            stack.push(curr);
            curr = curr.left;
        }
        // Process the leftmost unprocessed node
        curr = stack.pop();
        result.add(curr.val);
        // Move to right subtree
        curr = curr.right;
    }
    return result;
}

// Iterative Preorder — simpler, push right then left
public List<Integer> preorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;
    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);

    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        result.add(node.val);
        // Push right first so left is processed first (LIFO)
        if (node.right != null) stack.push(node.right);
        if (node.left != null) stack.push(node.left);
    }
    return result;
}

// Iterative Postorder — reverse of modified preorder
// Trick: do Root->Right->Left, then reverse the result
public List<Integer> postorderIterative(TreeNode root) {
    LinkedList<Integer> result = new LinkedList<>();
    if (root == null) return result;
    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);

    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        result.addFirst(node.val); // add to front = reverse
        if (node.left != null) stack.push(node.left);
        if (node.right != null) stack.push(node.right);
    }
    return result;
}
```

**Why iterative inorder is harder:** In recursive inorder, the call stack handles "remembering where you came from." In iterative, you must manually push nodes onto the stack before going left, then pop them when you're ready to process.

---

### Template 3: Return-Value Pattern (Bottom-Up)

Used for computing properties of the tree: height, balance, validity, count.

```java
// Generic bottom-up template
// The function returns some computed value about the subtree rooted at node.
int dfs(TreeNode node) {
    // Base case: null node returns a neutral/sentinel value
    if (node == null) return 0;

    // Recurse on children first (postorder)
    int leftResult = dfs(node.left);
    int rightResult = dfs(node.right);

    // Combine results and return to parent
    return combine(leftResult, rightResult, node.val);
}

// Example: Max Depth
int maxDepth(TreeNode node) {
    if (node == null) return 0;
    int left = maxDepth(node.left);
    int right = maxDepth(node.right);
    return 1 + Math.max(left, right);
}

// Example: Is Balanced (returns -1 for unbalanced, height otherwise)
int checkHeight(TreeNode node) {
    if (node == null) return 0;
    int left = checkHeight(node.left);
    if (left == -1) return -1;          // short-circuit: already unbalanced
    int right = checkHeight(node.right);
    if (right == -1) return -1;
    if (Math.abs(left - right) > 1) return -1; // this node is unbalanced
    return 1 + Math.max(left, right);   // return height to parent
}
```

---

### Template 4: Path Tracking (Top-Down with Backtracking)

Used when you need to collect or check paths from root to leaf.

```java
// Find all root-to-leaf paths with a given sum
public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
    List<List<Integer>> result = new ArrayList<>();
    dfs(root, targetSum, new ArrayList<>(), result);
    return result;
}

private void dfs(TreeNode node, int remaining,
                 List<Integer> path, List<List<Integer>> result) {
    if (node == null) return;

    path.add(node.val);           // choose: add current node to path
    remaining -= node.val;

    // Check if we've reached a leaf with the right sum
    if (node.left == null && node.right == null && remaining == 0) {
        result.add(new ArrayList<>(path)); // snapshot the path
    }

    dfs(node.left, remaining, path, result);   // explore left
    dfs(node.right, remaining, path, result);  // explore right

    path.remove(path.size() - 1); // unchoose: backtrack
}
```

**The backtracking step is critical.** Without `path.remove(...)`, the path list accumulates nodes from all branches. You add before recursing, remove after — this is the standard backtracking pattern.

---

### Template 5: BST Search, Insert, Delete

BST operations exploit the sorted property: left subtree < node < right subtree.

```java
// BST Search
TreeNode search(TreeNode root, int target) {
    if (root == null || root.val == target) return root;
    if (target < root.val) return search(root.left, target);
    return search(root.right, target);
}

// BST Insert
TreeNode insert(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);
    if (val < root.val) root.left = insert(root.left, val);
    else if (val > root.val) root.right = insert(root.right, val);
    return root; // return root to maintain tree structure
}

// BST Delete — three cases:
// 1. Node is a leaf: just remove it
// 2. Node has one child: replace with that child
// 3. Node has two children: replace with inorder successor (smallest in right subtree)
TreeNode delete(TreeNode root, int key) {
    if (root == null) return null;
    if (key < root.val) {
        root.left = delete(root.left, key);
    } else if (key > root.val) {
        root.right = delete(root.right, key);
    } else {
        // Found the node to delete
        if (root.left == null) return root.right;  // case 1 or 2
        if (root.right == null) return root.left;  // case 2
        // Case 3: find inorder successor (min of right subtree)
        TreeNode successor = findMin(root.right);
        root.val = successor.val;                  // copy successor's value
        root.right = delete(root.right, successor.val); // delete successor
    }
    return root;
}

TreeNode findMin(TreeNode node) {
    while (node.left != null) node = node.left;
    return node;
}

// Validate BST: pass min/max bounds down
// Use Long to handle Integer.MIN_VALUE and Integer.MAX_VALUE edge cases
boolean isValidBST(TreeNode root) {
    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

boolean validate(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return validate(node.left, min, node.val) &&
           validate(node.right, node.val, max);
}
```

---

### Template 6: Morris Traversal (O(1) Space Inorder)

Advanced technique. Mention in interviews to show depth of knowledge, but only implement if asked.

```java
// Morris Inorder: O(n) time, O(1) space
// Idea: temporarily thread right pointers to create a path back to the parent
public List<Integer> morrisInorder(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    TreeNode curr = root;

    while (curr != null) {
        if (curr.left == null) {
            // No left child: process current, move right
            result.add(curr.val);
            curr = curr.right;
        } else {
            // Find inorder predecessor (rightmost node in left subtree)
            TreeNode pred = curr.left;
            while (pred.right != null && pred.right != curr) {
                pred = pred.right;
            }

            if (pred.right == null) {
                // First visit: create thread, go left
                pred.right = curr;
                curr = curr.left;
            } else {
                // Second visit: remove thread, process current, go right
                pred.right = null;
                result.add(curr.val);
                curr = curr.right;
            }
        }
    }
    return result;
}
```

**When to mention Morris:** If the interviewer asks for O(1) space traversal, or if you want to impress. Don't lead with it — it's hard to explain under pressure.

---

## Real-World Applications

**1. File System Traversal**
Every `find` command or recursive directory listing is DFS on a tree. The file system is literally a tree where directories are internal nodes and files are leaves. Postorder DFS is used for deletion (delete children before parent).

**2. DOM Tree Traversal in Browsers**
The browser's Document Object Model is a tree. JavaScript's `querySelectorAll` and event bubbling both use DFS. CSS specificity calculations traverse the DOM tree recursively.

**3. Compiler Abstract Syntax Trees**
When a compiler parses `a + b * c`, it builds an AST. Code generation, type checking, and optimization all use postorder DFS on this tree. The expression `a + b * c` becomes a tree where `*` is deeper than `+`, and postorder evaluation gives the correct result.

**4. Database B-Tree and B+Tree Operations**
Database indexes use B-Trees. Range queries traverse the tree using a variant of inorder DFS. The sorted property of BSTs generalizes to B-Trees, which is why inorder traversal is so fundamental.

**5. Game AI Decision Trees (Minimax)**
Chess engines and game AIs build a tree of possible moves. Minimax with alpha-beta pruning is DFS on this decision tree. The bottom-up evaluation (leaves score positions, parents pick min or max) is exactly the return-value pattern.

**6. XML and JSON Parsing**
Recursive descent parsers process nested structures using DFS. An XML document is a tree; parsing it means recursively processing each element's children before returning to the parent. This is postorder DFS.

---

## Problem Categories and Solutions

### Category A: Basic DFS and Properties

---

#### LC 104 — Maximum Depth of Binary Tree (Easy)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Find the maximum depth (number of nodes along the longest root-to-leaf path).

**Pattern:** Bottom-up return-value. Height of a node = 1 + max(left height, right height).

```java
public int maxDepth(TreeNode root) {
    if (root == null) return 0;
    int left = maxDepth(root.left);
    int right = maxDepth(root.right);
    return 1 + Math.max(left, right);
}
```

**Complexity:** O(n) time, O(h) space where h is height (O(n) worst case for skewed tree, O(log n) for balanced).

**Iterative version (BFS-style, counts levels):**
```java
public int maxDepthIterative(TreeNode root) {
    if (root == null) return 0;
    Deque<TreeNode> queue = new ArrayDeque<>();
    queue.offer(root);
    int depth = 0;
    while (!queue.isEmpty()) {
        int size = queue.size();
        depth++;
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
    }
    return depth;
}
```

---

#### LC 226 — Invert Binary Tree (Easy)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Mirror a binary tree (swap left and right children at every node).

**Pattern:** Postorder DFS. Invert children first, then swap. (Or preorder — swap first, then recurse. Both work.)

**DRY RUN:**
```
Input:
        4
       / \
      2   7
     / \ / \
    1  3 6  9

Step 1: invert(4)
  → invert(2)
      → invert(1): leaf, swap nulls, return node(1)
      → invert(3): leaf, swap nulls, return node(3)
      swap: node(2).left = node(3), node(2).right = node(1)
      return node(2) [now: 2 with left=3, right=1]
  → invert(7)
      → invert(6): leaf, return node(6)
      → invert(9): leaf, return node(9)
      swap: node(7).left = node(9), node(7).right = node(6)
      return node(7) [now: 7 with left=9, right=6]
  swap: node(4).left = node(7), node(4).right = node(2)

Output:
        4
       / \
      7   2
     / \ / \
    9  6 3  1
```

```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;

    // Recurse on children first (postorder)
    TreeNode left = invertTree(root.left);
    TreeNode right = invertTree(root.right);

    // Swap
    root.left = right;
    root.right = left;

    return root;
}
```

**Iterative version:**
```java
public TreeNode invertTreeIterative(TreeNode root) {
    if (root == null) return null;
    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);
    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        // Swap children
        TreeNode temp = node.left;
        node.left = node.right;
        node.right = temp;
        if (node.left != null) stack.push(node.left);
        if (node.right != null) stack.push(node.right);
    }
    return root;
}
```

**Complexity:** O(n) time, O(h) space.

---

#### LC 100 — Same Tree (Easy)
**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Check if two binary trees are structurally identical with the same node values.

**Pattern:** Simultaneous DFS on both trees. Three cases: both null (true), one null (false), values differ (false).

```java
public boolean isSameTree(TreeNode p, TreeNode q) {
    // Both null: same
    if (p == null && q == null) return true;
    // One null, one not: different
    if (p == null || q == null) return false;
    // Values differ: different
    if (p.val != q.val) return false;
    // Recurse on both subtrees
    return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
}
```

**Complexity:** O(n) time where n is the smaller tree, O(h) space.

**Key insight:** The order of null checks matters. Check both-null before either-null, or you'll get a NullPointerException.

---

#### LC 101 — Symmetric Tree (Easy)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Check if a binary tree is a mirror of itself (symmetric around its center).

**Pattern:** Compare left and right subtrees simultaneously, but mirrored. The left subtree's left child mirrors the right subtree's right child.

```java
public boolean isSymmetric(TreeNode root) {
    if (root == null) return true;
    return isMirror(root.left, root.right);
}

private boolean isMirror(TreeNode left, TreeNode right) {
    if (left == null && right == null) return true;
    if (left == null || right == null) return false;
    if (left.val != right.val) return false;
    // Key: left.left mirrors right.right, left.right mirrors right.left
    return isMirror(left.left, right.right) &&
           isMirror(left.right, right.left);
}
```

**Iterative version using a queue:**
```java
public boolean isSymmetricIterative(TreeNode root) {
    if (root == null) return true;
    Deque<TreeNode> queue = new ArrayDeque<>();
    queue.offer(root.left);
    queue.offer(root.right);
    while (!queue.isEmpty()) {
        TreeNode left = queue.poll();
        TreeNode right = queue.poll();
        if (left == null && right == null) continue;
        if (left == null || right == null) return false;
        if (left.val != right.val) return false;
        queue.offer(left.left);
        queue.offer(right.right);
        queue.offer(left.right);
        queue.offer(right.left);
    }
    return true;
}
```

**Complexity:** O(n) time, O(h) space.

---

#### LC 110 — Balanced Binary Tree (Easy)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Determine if a binary tree is height-balanced (every node's left and right subtrees differ in height by at most 1).

**Naive O(n^2) approach:** Call `height()` at every node. Height itself is O(n), called n times = O(n^2).

**Optimal O(n) approach:** Combine height computation and balance check in one pass. Return -1 as a sentinel for "unbalanced."

```java
// O(n^2) naive — DON'T use this in interviews
public boolean isBalancedNaive(TreeNode root) {
    if (root == null) return true;
    int leftH = height(root.left);
    int rightH = height(root.right);
    if (Math.abs(leftH - rightH) > 1) return false;
    return isBalancedNaive(root.left) && isBalancedNaive(root.right);
}

int height(TreeNode node) {
    if (node == null) return 0;
    return 1 + Math.max(height(node.left), height(node.right));
}

// O(n) optimal — use this
public boolean isBalanced(TreeNode root) {
    return checkHeight(root) != -1;
}

private int checkHeight(TreeNode node) {
    if (node == null) return 0;

    int left = checkHeight(node.left);
    if (left == -1) return -1;  // short-circuit: left subtree unbalanced

    int right = checkHeight(node.right);
    if (right == -1) return -1; // short-circuit: right subtree unbalanced

    if (Math.abs(left - right) > 1) return -1; // this node unbalanced

    return 1 + Math.max(left, right); // return height to parent
}
```

**Why O(n) works:** Each node is visited exactly once. The -1 sentinel propagates up immediately, short-circuiting further computation.

**Complexity:** O(n) time, O(h) space.

---

### Category B: Path Problems

---

#### LC 112 — Path Sum (Easy)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Does a root-to-leaf path exist where the sum of node values equals `targetSum`?

**Pattern:** Top-down DFS. Subtract current node's value from target as you descend. At a leaf, check if remaining equals zero.

```java
public boolean hasPathSum(TreeNode root, int targetSum) {
    if (root == null) return false;

    // At a leaf: check if this node's value completes the sum
    if (root.left == null && root.right == null) {
        return root.val == targetSum;
    }

    // Recurse with reduced target
    int remaining = targetSum - root.val;
    return hasPathSum(root.left, remaining) || hasPathSum(root.right, remaining);
}
```

**Common mistake:** Checking `if (root == null) return targetSum == 0`. This is wrong because it would return true for an empty tree with targetSum=0, and it doesn't enforce the "root-to-leaf" requirement (a path must end at a leaf, not an internal node).

**Complexity:** O(n) time, O(h) space.

---

#### LC 113 — Path Sum II (Medium)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Find all root-to-leaf paths where the sum equals `targetSum`. Return all such paths.

**Pattern:** Top-down DFS with backtracking. This is Template 4 directly.

```java
public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
    List<List<Integer>> result = new ArrayList<>();
    dfs(root, targetSum, new ArrayList<>(), result);
    return result;
}

private void dfs(TreeNode node, int remaining,
                 List<Integer> path, List<List<Integer>> result) {
    if (node == null) return;

    path.add(node.val);
    remaining -= node.val;

    // Leaf check
    if (node.left == null && node.right == null && remaining == 0) {
        result.add(new ArrayList<>(path)); // MUST copy, not add reference
    }

    dfs(node.left, remaining, path, result);
    dfs(node.right, remaining, path, result);

    path.remove(path.size() - 1); // backtrack
}
```

**Critical:** `result.add(new ArrayList<>(path))` — you must copy the path. If you add `path` directly, all entries in `result` will point to the same list, which will be empty after backtracking.

**Complexity:** O(n^2) time in worst case (copying paths), O(n) space for recursion stack.

---

#### LC 124 — Binary Tree Maximum Path Sum (Hard)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

**Problem:** Find the maximum sum of any path in the tree. A path can start and end at any node, but must follow parent-child connections. It doesn't need to pass through the root.

**This is one of the hardest tree problems.** The key difficulty: a path can "bend" through a node, using both its left and right children. But when you return a value to the parent, you can only extend the path in one direction.

**The "return vs update" distinction:**
- What you **return** to the parent: the best single-branch gain from this node (can only go left OR right, not both)
- What you **update globally**: the best path through this node (can use both left AND right)

**DRY RUN:**
```
Tree:
       -10
       /  \
      9   20
         /  \
        15   7

dfs(-10):
  dfs(9):
    dfs(null) = 0
    dfs(null) = 0
    left_gain = max(0, 0) = 0
    right_gain = max(0, 0) = 0
    path_through_9 = 9 + 0 + 0 = 9
    answer = max(-inf, 9) = 9
    return 9 + max(0, 0) = 9

  dfs(20):
    dfs(15):
      left_gain = 0, right_gain = 0
      path_through_15 = 15
      answer = max(9, 15) = 15
      return 15

    dfs(7):
      left_gain = 0, right_gain = 0
      path_through_7 = 7
      answer = max(15, 7) = 15
      return 7

    left_gain = max(0, 15) = 15
    right_gain = max(0, 7) = 7
    path_through_20 = 20 + 15 + 7 = 42
    answer = max(15, 42) = 42
    return 20 + max(15, 7) = 35  ← only one branch returned to parent

  left_gain = max(0, 9) = 9
  right_gain = max(0, 35) = 35
  path_through_-10 = -10 + 9 + 35 = 34
  answer = max(42, 34) = 42  ← 42 is the answer

Final answer: 42  (path: 15 -> 20 -> 7)
```

```java
class Solution {
    int maxSum = Integer.MIN_VALUE; // global variable

    public int maxPathSum(TreeNode root) {
        dfs(root);
        return maxSum;
    }

    // Returns the maximum gain from this node going in ONE direction
    // (either left or right, not both — because parent can only extend one way)
    private int dfs(TreeNode node) {
        if (node == null) return 0;

        // max(0, ...) means we ignore negative contributions
        int leftGain = Math.max(0, dfs(node.left));
        int rightGain = Math.max(0, dfs(node.right));

        // Path through this node uses BOTH branches
        int pathThroughNode = node.val + leftGain + rightGain;

        // Update global answer
        maxSum = Math.max(maxSum, pathThroughNode);

        // Return to parent: only ONE branch (the better one)
        return node.val + Math.max(leftGain, rightGain);
    }
}
```

**Why `Math.max(0, dfs(...))`?** If a subtree has a negative sum, we're better off not including it. Taking max with 0 means "skip this subtree if it hurts."

**Why `Integer.MIN_VALUE` not `0`?** The answer could be negative (e.g., a tree with all negative values). The path must include at least one node.

**Complexity:** O(n) time, O(h) space.

---

#### LC 437 — Path Sum III (Medium)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Count paths in the tree (not necessarily root-to-leaf) where the sum equals `targetSum`. Paths must go downward (parent to child).

**Pattern:** Prefix sum on tree (cross-reference Topic 5). As you DFS, maintain a running prefix sum. At each node, check how many previous prefix sums equal `currentSum - targetSum`.

```java
class Solution {
    int count = 0;

    public int pathSum(TreeNode root, int targetSum) {
        Map<Long, Integer> prefixSums = new HashMap<>();
        prefixSums.put(0L, 1); // empty path has sum 0
        dfs(root, 0L, targetSum, prefixSums);
        return count;
    }

    private void dfs(TreeNode node, long currentSum, int target,
                     Map<Long, Integer> prefixSums) {
        if (node == null) return;

        currentSum += node.val;

        // How many paths ending here have sum == target?
        count += prefixSums.getOrDefault(currentSum - target, 0);

        // Add current prefix sum to map
        prefixSums.put(currentSum, prefixSums.getOrDefault(currentSum, 0) + 1);

        dfs(node.left, currentSum, target, prefixSums);
        dfs(node.right, currentSum, target, prefixSums);

        // Backtrack: remove current prefix sum when leaving this node
        prefixSums.put(currentSum, prefixSums.get(currentSum) - 1);
    }
}
```

**Why `long` for currentSum?** Node values can be up to 10^9, and paths can have up to 1000 nodes, so the sum can overflow `int`.

**The backtracking on the map:** When you leave a node, you remove its prefix sum from the map. This ensures the map only contains prefix sums on the current root-to-node path, not from sibling branches.

**Complexity:** O(n) time, O(n) space for the map.

---

### Category C: BST-Specific

---

#### LC 98 — Validate Binary Search Tree (Medium)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Determine if a binary tree is a valid BST. A valid BST satisfies: left subtree values < node value < right subtree values, for every node.

**Common wrong approach:** Only check `node.left.val < node.val < node.right.val`. This fails for:
```
    5
   / \
  1   4
     / \
    3   6
```
Node 4's children (3, 6) satisfy local BST property, but 3 < 5 violates the global constraint.

**Correct approach:** Pass min and max bounds down. Every node must satisfy `min < node.val < max`.

**DRY RUN:**
```
Tree:
    5
   / \
  1   4
     / \
    3   6

validate(5, -inf, +inf):
  5 is in (-inf, +inf) ✓
  validate(1, -inf, 5):
    1 is in (-inf, 5) ✓
    validate(null, -inf, 1): true
    validate(null, 1, 5): true
    return true
  validate(4, 5, +inf):
    4 is NOT in (5, +inf) ✗
    return false

Result: false ✓ (correct — 4 < 5 violates BST)
```

```java
public boolean isValidBST(TreeNode root) {
    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

private boolean validate(TreeNode node, long min, long max) {
    if (node == null) return true;
    // Node value must be strictly between min and max
    if (node.val <= min || node.val >= max) return false;
    return validate(node.left, min, node.val) &&
           validate(node.right, node.val, max);
}
```

**Why `Long.MIN_VALUE` and `Long.MAX_VALUE`?** If you use `Integer.MIN_VALUE` and `Integer.MAX_VALUE`, a node with value `Integer.MIN_VALUE` would fail the check `node.val <= min` even though it's valid. Using `long` bounds avoids this edge case entirely.

**Alternative: Inorder traversal approach**
```java
// Inorder of valid BST must be strictly increasing
public boolean isValidBSTInorder(TreeNode root) {
    long prev = Long.MIN_VALUE;
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode curr = root;
    while (curr != null || !stack.isEmpty()) {
        while (curr != null) {
            stack.push(curr);
            curr = curr.left;
        }
        curr = stack.pop();
        if (curr.val <= prev) return false; // not strictly increasing
        prev = curr.val;
        curr = curr.right;
    }
    return true;
}
```

**Complexity:** O(n) time, O(h) space.

---

#### LC 230 — Kth Smallest Element in BST (Medium)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Find the kth smallest element in a BST (1-indexed).

**Pattern:** Inorder traversal of BST gives sorted order. The kth element visited is the answer.

```java
class Solution {
    int count = 0;
    int result = 0;

    public int kthSmallest(TreeNode root, int k) {
        inorder(root, k);
        return result;
    }

    private void inorder(TreeNode node, int k) {
        if (node == null) return;
        inorder(node.left, k);
        count++;
        if (count == k) {
            result = node.val;
            return; // early exit
        }
        inorder(node.right, k);
    }
}
```

**Iterative version (preferred for early exit):**
```java
public int kthSmallestIterative(TreeNode root, int k) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode curr = root;
    int count = 0;

    while (curr != null || !stack.isEmpty()) {
        while (curr != null) {
            stack.push(curr);
            curr = curr.left;
        }
        curr = stack.pop();
        count++;
        if (count == k) return curr.val;
        curr = curr.right;
    }
    return -1; // k > number of nodes
}
```

**Follow-up (frequently asked):** If the BST is frequently modified (insert/delete) and you need to find kth smallest repeatedly, augment each node with a `leftCount` field (number of nodes in left subtree). Then kth smallest is O(h) per query.

**Complexity:** O(n) time worst case, O(k) average for balanced BST, O(h) space.

---

#### LC 235 — Lowest Common Ancestor of BST (Medium)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Find the LCA of two nodes p and q in a BST.

**Pattern:** Exploit BST sorted property. If both p and q are less than root, LCA is in left subtree. If both are greater, LCA is in right subtree. Otherwise, root is the LCA.

```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null) return null;

    if (p.val < root.val && q.val < root.val) {
        return lowestCommonAncestor(root.left, p, q);
    }
    if (p.val > root.val && q.val > root.val) {
        return lowestCommonAncestor(root.right, p, q);
    }
    // One is on each side, or one equals root: root is LCA
    return root;
}
```

**Iterative version:**
```java
public TreeNode lowestCommonAncestorIterative(TreeNode root, TreeNode p, TreeNode q) {
    TreeNode curr = root;
    while (curr != null) {
        if (p.val < curr.val && q.val < curr.val) {
            curr = curr.left;
        } else if (p.val > curr.val && q.val > curr.val) {
            curr = curr.right;
        } else {
            return curr;
        }
    }
    return null;
}
```

**Complexity:** O(h) time (O(log n) for balanced BST, O(n) worst case), O(1) space for iterative.

**Contrast with LC 236:** LC 235 uses BST property for O(h). LC 236 works on any binary tree and requires O(n).

---

#### LC 108 — Convert Sorted Array to BST (Easy)
**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given a sorted array, construct a height-balanced BST.

**Pattern:** Binary search thinking. The middle element becomes the root (ensures balance). Recursively build left subtree from left half, right subtree from right half.

```java
public TreeNode sortedArrayToBST(int[] nums) {
    return build(nums, 0, nums.length - 1);
}

private TreeNode build(int[] nums, int left, int right) {
    if (left > right) return null;

    int mid = left + (right - left) / 2; // avoid overflow
    TreeNode node = new TreeNode(nums[mid]);
    node.left = build(nums, left, mid - 1);
    node.right = build(nums, mid + 1, right);
    return node;
}
```

**Why mid = left + (right - left) / 2?** Avoids integer overflow when left and right are large. Same pattern as binary search.

**Complexity:** O(n) time, O(log n) space for recursion stack.

---

### Category D: Structure and Construction

---

#### LC 543 — Diameter of Binary Tree (Easy)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Find the length of the longest path between any two nodes. The path may or may not pass through the root.

**Pattern:** Global variable pattern. The diameter through a node = left height + right height. Return height to parent, update diameter globally.

```java
class Solution {
    int diameter = 0;

    public int diameterOfBinaryTree(TreeNode root) {
        height(root);
        return diameter;
    }

    private int height(TreeNode node) {
        if (node == null) return 0;
        int left = height(node.left);
        int right = height(node.right);
        // Diameter through this node = left + right
        diameter = Math.max(diameter, left + right);
        // Return height to parent
        return 1 + Math.max(left, right);
    }
}
```

**This is the simpler version of LC 124.** The same "return height, update diameter globally" pattern appears in both. Master this one first.

**Complexity:** O(n) time, O(h) space.

---

#### LC 236 — Lowest Common Ancestor of Binary Tree (Medium)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, Uber

**Problem:** Find the LCA of two nodes p and q in a binary tree (not necessarily a BST).

**Pattern:** Postorder DFS. A node is the LCA if: (1) it equals p or q, or (2) p is in one subtree and q is in the other.

**DRY RUN:**
```
Tree:
        3
       / \
      5   1
     / \ / \
    6  2 0  8
      / \
     7   4

Find LCA(5, 1):

dfs(3):
  dfs(5):
    dfs(6):
      dfs(null) = null
      dfs(null) = null
      6 != 5, 6 != 1, left=null, right=null → return null
    dfs(2):
      dfs(7): return null (7 != 5, 7 != 1)
      dfs(4): return null (4 != 5, 4 != 1)
      2 != 5, 2 != 1, left=null, right=null → return null
    node(5) == p → return node(5)  ← found p!

  dfs(1):
    dfs(0): return null
    dfs(8): return null
    node(1) == q → return node(1)  ← found q!

  At node(3): left = node(5), right = node(1)
  Both non-null → node(3) is the LCA
  return node(3)

Result: node(3) ✓

Find LCA(5, 4):

dfs(3):
  dfs(5):
    dfs(6): return null
    dfs(2):
      dfs(7): return null
      dfs(4): node(4) == q → return node(4)
      At node(2): left=null, right=node(4) → return node(4)
    At node(5): node(5) == p → return node(5)
    (We return node(5) because the node itself matches p,
     even though we found q in its subtree)

  dfs(1): return null (neither 5 nor 4 in this subtree)

  At node(3): left = node(5), right = null → return node(5)

Result: node(5) ✓ (5 is ancestor of 4, so LCA is 5)
```

```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    // Base case: null or found p or q
    if (root == null || root == p || root == q) return root;

    // Search both subtrees
    TreeNode left = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);

    // If both sides found something: this node is the LCA
    if (left != null && right != null) return root;

    // Otherwise return whichever side found something
    return left != null ? left : right;
}
```

**Why this works:**
- If a node equals p or q, return it immediately. We don't need to search deeper because the LCA is either this node or an ancestor.
- If both left and right return non-null, p and q are in different subtrees, so the current node is the LCA.
- If only one side returns non-null, both p and q are in that subtree (or one of them is the current node's ancestor).

**Complexity:** O(n) time, O(h) space.

---

#### LC 105 — Construct Binary Tree from Preorder and Inorder Traversal (Medium)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given preorder and inorder traversal arrays, reconstruct the binary tree.

**Key insight:**
- Preorder: first element is always the root
- Inorder: root splits the array into left subtree (elements to the left) and right subtree (elements to the right)

```
preorder = [3, 9, 20, 15, 7]
inorder  = [9, 3, 15, 20, 7]

Step 1: preorder[0] = 3 is root
Step 2: Find 3 in inorder: index 1
        Left subtree: inorder[0..0] = [9], size = 1
        Right subtree: inorder[2..4] = [15, 20, 7], size = 3
Step 3: Left subtree preorder: preorder[1..1] = [9]
        Right subtree preorder: preorder[2..4] = [20, 15, 7]
Step 4: Recurse
```

```java
class Solution {
    Map<Integer, Integer> inorderIndex = new HashMap<>();

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        // Build index map for O(1) lookup
        for (int i = 0; i < inorder.length; i++) {
            inorderIndex.put(inorder[i], i);
        }
        return build(preorder, 0, preorder.length - 1, 0, inorder.length - 1);
    }

    private TreeNode build(int[] preorder, int preStart, int preEnd,
                           int inStart, int inEnd) {
        if (preStart > preEnd) return null;

        int rootVal = preorder[preStart];
        TreeNode root = new TreeNode(rootVal);

        int inRoot = inorderIndex.get(rootVal);
        int leftSize = inRoot - inStart;

        root.left = build(preorder,
                          preStart + 1, preStart + leftSize,
                          inStart, inRoot - 1);
        root.right = build(preorder,
                           preStart + leftSize + 1, preEnd,
                           inRoot + 1, inEnd);
        return root;
    }
}
```

**Why HashMap?** Without it, finding the root in inorder is O(n) per call, making the total O(n^2). With the map, each lookup is O(1), giving O(n) total.

**Complexity:** O(n) time, O(n) space for the map.

---

#### LC 297 — Serialize and Deserialize Binary Tree (Hard)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Uber

**Problem:** Design an algorithm to serialize a binary tree to a string and deserialize it back.

**Pattern:** Preorder DFS with null markers. Preorder is ideal because the root comes first, making reconstruction straightforward.

```java
public class Codec {
    private static final String NULL = "null";
    private static final String SEP = ",";

    // Serialize: preorder DFS, use "null" for null nodes
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append(NULL).append(SEP);
            return;
        }
        sb.append(node.val).append(SEP);
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    // Deserialize: consume tokens in preorder
    public TreeNode deserialize(String data) {
        Deque<String> tokens = new ArrayDeque<>(Arrays.asList(data.split(SEP)));
        return deserializeHelper(tokens);
    }

    private TreeNode deserializeHelper(Deque<String> tokens) {
        String token = tokens.poll();
        if (NULL.equals(token)) return null;
        TreeNode node = new TreeNode(Integer.parseInt(token));
        node.left = deserializeHelper(tokens);
        node.right = deserializeHelper(tokens);
        return node;
    }
}
```

**Example:**
```
Tree:
    1
   / \
  2   3
     / \
    4   5

Serialized: "1,2,null,null,3,4,null,null,5,null,null,"

Deserialization trace:
poll "1" → create node(1)
  poll "2" → create node(2)
    poll "null" → return null (left of 2)
    poll "null" → return null (right of 2)
  node(2) complete
  poll "3" → create node(3)
    poll "4" → create node(4)
      poll "null" → null
      poll "null" → null
    node(4) complete
    poll "5" → create node(5)
      poll "null" → null
      poll "null" → null
    node(5) complete
  node(3) complete
node(1) complete
```

**Why preorder and not inorder?** Inorder traversal alone doesn't uniquely identify a tree (you need both inorder and preorder). Preorder with null markers is self-contained.

**Complexity:** O(n) time and space for both serialize and deserialize.

---

### Category E: Traversal Variants

---

#### LC 94 — Binary Tree Inorder Traversal (Easy)
**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Return inorder traversal of a binary tree.

Both recursive and iterative solutions are expected. The iterative version is the important one (see Template 2).

```java
// Recursive
public List<Integer> inorderTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    inorder(root, result);
    return result;
}

private void inorder(TreeNode node, List<Integer> result) {
    if (node == null) return;
    inorder(node.left, result);
    result.add(node.val);
    inorder(node.right, result);
}

// Iterative (the important one)
public List<Integer> inorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode curr = root;
    while (curr != null || !stack.isEmpty()) {
        while (curr != null) {
            stack.push(curr);
            curr = curr.left;
        }
        curr = stack.pop();
        result.add(curr.val);
        curr = curr.right;
    }
    return result;
}
```

**Complexity:** O(n) time, O(h) space.

---

#### LC 114 — Flatten Binary Tree to Linked List (Medium)
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Flatten a binary tree to a linked list in-place, using the right pointers. The order should match preorder traversal.

**Approach 1: Reverse preorder (right, left, root)**
Process nodes in reverse preorder. Maintain a `prev` pointer. Each node's right becomes `prev`, left becomes null.

```java
class Solution {
    TreeNode prev = null;

    public void flatten(TreeNode root) {
        if (root == null) return;
        flatten(root.right);  // process right first
        flatten(root.left);   // then left
        root.right = prev;    // current node's right = previously processed node
        root.left = null;
        prev = root;          // update prev
    }
}
```

**Approach 2: Iterative with stack**
```java
public void flattenIterative(TreeNode root) {
    if (root == null) return;
    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);
    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        if (node.right != null) stack.push(node.right);
        if (node.left != null) stack.push(node.left);
        if (!stack.isEmpty()) {
            node.right = stack.peek(); // link to next preorder node
        }
        node.left = null;
    }
}
```

**Approach 3: Morris-like O(1) space**
```java
public void flattenMorris(TreeNode root) {
    TreeNode curr = root;
    while (curr != null) {
        if (curr.left != null) {
            // Find rightmost node of left subtree
            TreeNode rightmost = curr.left;
            while (rightmost.right != null) rightmost = rightmost.right;
            // Connect rightmost to curr's right subtree
            rightmost.right = curr.right;
            // Move left subtree to right
            curr.right = curr.left;
            curr.left = null;
        }
        curr = curr.right;
    }
}
```

**Complexity:** O(n) time, O(h) space for recursive/iterative, O(1) for Morris approach.

---

#### LC 572 — Subtree of Another Tree (Easy)
**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given trees `root` and `subRoot`, check if `subRoot` is a subtree of `root`.

**Pattern:** For each node in `root`, check if the subtree rooted there is identical to `subRoot`. Uses LC 100 (Same Tree) as a helper.

```java
public boolean isSubtree(TreeNode root, TreeNode subRoot) {
    if (root == null) return false;
    if (isSameTree(root, subRoot)) return true;
    return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
}

private boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    if (p.val != q.val) return false;
    return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
}
```

**Complexity:** O(m * n) time where m and n are sizes of the two trees. O(m) space.

**Optimization:** Serialize both trees and use string matching (KMP or Z-algorithm) for O(m + n). Mention this as a follow-up.

---

## Common Mistakes

### 1. Confusing Top-Down and Bottom-Up

**Top-down** passes information as parameters. **Bottom-up** returns information as values. Mixing them up leads to incorrect solutions.

```
Wrong: trying to compute height by passing it as a parameter
Right: return height from each call, combine at parent

Wrong: trying to validate BST by returning min/max from subtrees
Right: pass min/max bounds down as parameters
```

### 2. LC 98: Integer Bounds Trap

```java
// WRONG: fails when node.val == Integer.MIN_VALUE
boolean validate(TreeNode node, int min, int max) {
    if (node.val <= min || node.val >= max) return false; // Integer.MIN_VALUE <= Integer.MIN_VALUE is true!
    ...
}

// CORRECT: use Long bounds
boolean validate(TreeNode node, long min, long max) {
    if (node.val <= min || node.val >= max) return false;
    ...
}
```

### 3. LC 124: Return vs Update Confusion

The function returns the best single-branch gain (for the parent to extend). It updates the global answer with the best two-branch path (which can't be extended further up).

```java
// WRONG: returning the two-branch sum to parent
return node.val + leftGain + rightGain; // parent can't extend this

// CORRECT: return single branch, update global with two branches
maxSum = Math.max(maxSum, node.val + leftGain + rightGain); // global update
return node.val + Math.max(leftGain, rightGain);            // single branch to parent
```

### 4. Null Node Base Case

Every recursive tree function needs a null check. Forgetting it causes NullPointerException.

```java
// WRONG: no null check
int height(TreeNode node) {
    return 1 + Math.max(height(node.left), height(node.right)); // NPE when node is null
}

// CORRECT
int height(TreeNode node) {
    if (node == null) return 0;
    return 1 + Math.max(height(node.left), height(node.right));
}
```

### 5. Path List Not Copied

In LC 113 and similar problems, you must copy the path when adding to results.

```java
// WRONG: adds reference to the same list
result.add(path); // all entries in result will be empty after backtracking

// CORRECT: snapshot the current state
result.add(new ArrayList<>(path));
```

### 6. Stack Overflow on Skewed Trees

A tree with n nodes in a straight line (degenerate/skewed) has height n. Recursive DFS uses O(n) stack space, which can cause stack overflow for n = 10^5.

```
Mitigation:
- Use iterative DFS for production code
- In interviews, mention this as a trade-off
- Java default stack size is ~512KB, handles ~5000-10000 recursive calls
```

### 7. Leaf Check vs Null Check

"Root-to-leaf" problems require checking at leaves, not at null nodes.

```java
// WRONG for path sum: returns true for empty tree with targetSum=0
if (node == null) return targetSum == 0;

// CORRECT: check at leaf
if (node.left == null && node.right == null) return node.val == targetSum;
if (node == null) return false;
```

### 8. BST Inorder Strictly Increasing

A valid BST requires strictly increasing inorder traversal (no duplicates, unless the problem specifies otherwise).

```java
// WRONG: allows equal values
if (curr.val <= prev) return false; // this is correct for strict BST

// Check the problem statement: some BSTs allow duplicates
// If duplicates go to the right: left < node <= right
```

---

## Pattern Comparison

### DFS vs BFS on Trees

| Aspect | DFS | BFS |
|--------|-----|-----|
| Data structure | Stack (implicit or explicit) | Queue |
| Space | O(h) — height of tree | O(w) — width of tree |
| Best for | Path problems, depth, structure | Level problems, shortest path |
| Worst case space | O(n) for skewed tree | O(n) for complete tree (last level) |
| Natural for | Recursion | Iteration |

**Rule:** If the problem mentions "level", "layer", "row", or "breadth", use BFS. Everything else is usually DFS.

### Recursive vs Iterative DFS

| Aspect | Recursive | Iterative |
|--------|-----------|-----------|
| Code clarity | High | Medium |
| Stack overflow risk | Yes (deep trees) | No |
| Space | O(h) call stack | O(h) explicit stack |
| Early exit | Harder (need flag or exception) | Easy (break from loop) |
| Interview preference | Start here | Mention as follow-up |

### Tree DFS vs Graph DFS

| Aspect | Tree DFS | Graph DFS |
|--------|----------|-----------|
| Cycles | None (trees are acyclic) | Possible |
| Visited set | Not needed | Required |
| Starting point | Always root | Any node |
| Parent tracking | Not needed | Sometimes needed |
| Complexity | O(n) always | O(V + E) |

Trees are a special case of graphs. The absence of cycles is what makes tree DFS simpler — you never revisit a node.

### Top-Down vs Bottom-Up

| Aspect | Top-Down | Bottom-Up |
|--------|----------|-----------|
| Information flow | Parent to child (parameters) | Child to parent (return value) |
| When to use | Need ancestor context | Need subtree info |
| Examples | Path Sum, Validate BST | Height, Diameter, Balance |
| Backtracking | Often needed (path problems) | Not needed |
| Global variable | Rarely | Often (for cross-branch answers) |

---

## Quick Reference

### Template Selector

```
Path problem (root-to-leaf sum, collect paths)?
  → Template 4 (path tracking with backtracking)

BST operation (search, insert, validate, kth smallest)?
  → Template 5 (BST with sorted property)

Height, diameter, balance, any subtree property?
  → Template 3 (return-value bottom-up)

Simple traversal (inorder/preorder/postorder)?
  → Template 1 (recursive) or Template 2 (iterative)

O(1) space traversal?
  → Template 6 (Morris)
```

### Traversal Order Cheat Sheet

```
Inorder   (L-Root-R) = sorted output for BST
                      → use for: kth smallest, validate BST, sorted output

Preorder  (Root-L-R) = root comes first, captures structure
                      → use for: serialization, tree construction, copy tree

Postorder (L-R-Root) = children before parent, bottom-up
                      → use for: deletion, height, diameter, LCA
```

### Complexity Summary

| Problem | Time | Space |
|---------|------|-------|
| Max Depth (LC 104) | O(n) | O(h) |
| Invert Tree (LC 226) | O(n) | O(h) |
| Same Tree (LC 100) | O(n) | O(h) |
| Symmetric Tree (LC 101) | O(n) | O(h) |
| Balanced Tree (LC 110) | O(n) | O(h) |
| Path Sum (LC 112) | O(n) | O(h) |
| Path Sum II (LC 113) | O(n^2) | O(n) |
| Max Path Sum (LC 124) | O(n) | O(h) |
| Path Sum III (LC 437) | O(n) | O(n) |
| Validate BST (LC 98) | O(n) | O(h) |
| Kth Smallest (LC 230) | O(n) | O(h) |
| LCA of BST (LC 235) | O(h) | O(h) |
| Sorted Array to BST (LC 108) | O(n) | O(log n) |
| Diameter (LC 543) | O(n) | O(h) |
| LCA (LC 236) | O(n) | O(h) |
| Construct from Pre+In (LC 105) | O(n) | O(n) |
| Serialize/Deserialize (LC 297) | O(n) | O(n) |
| Inorder Traversal (LC 94) | O(n) | O(h) |
| Flatten to List (LC 114) | O(n) | O(h) |
| Subtree (LC 572) | O(m*n) | O(m) |

*h = height of tree. O(log n) for balanced, O(n) for skewed.*

### Key Formulas

```
Height of tree:       h = O(log n) balanced, O(n) skewed
Nodes at depth d:     2^d (for complete binary tree)
Total nodes:          2^(h+1) - 1 (for complete binary tree)
Inorder of BST:       sorted sequence
Preorder first elem:  always the root
Postorder last elem:  always the root
```

### Interview Decision Tree

```
Is it a tree problem?
├── Does it involve levels/rows? → BFS (Topic 11)
└── Otherwise → DFS
    ├── Need info from ancestors? → Top-down (pass as params)
    ├── Need info from subtrees? → Bottom-up (return value)
    ├── Answer spans both subtrees? → Global variable + bottom-up
    └── Is it a BST? → Exploit sorted property
        ├── Traversal → Inorder
        ├── Search/Insert/Delete → O(h) with BST property
        └── Validate → Pass min/max bounds (use Long)
```

---

## Practice Roadmap

### Week 1: Easy Problems (15 minutes each)

Build intuition for the basic patterns. Every problem here should feel mechanical after practice.

| Problem | Pattern | Key Insight |
|---------|---------|-------------|
| LC 104 — Max Depth | Bottom-up return | 1 + max(left, right) |
| LC 226 — Invert Tree | Postorder swap | Recurse then swap |
| LC 100 — Same Tree | Simultaneous DFS | Both-null check first |
| LC 101 — Symmetric Tree | Mirror DFS | left.left vs right.right |
| LC 110 — Balanced Tree | Bottom-up with sentinel | Return -1 for unbalanced |
| LC 112 — Path Sum | Top-down subtract | Check at leaf, not null |

**Goal:** Solve each in under 15 minutes without hints. If you can't, re-read the template and try again.

### Week 2: Medium Problems (25 minutes each)

These require combining patterns or handling edge cases carefully.

| Problem | Pattern | Key Insight |
|---------|---------|-------------|
| LC 98 — Validate BST | Top-down bounds | Use Long, not Integer |
| LC 230 — Kth Smallest | Inorder + counter | Early exit iterative |
| LC 236 — LCA | Postorder return | Both non-null = LCA |
| LC 543 — Diameter | Global + bottom-up | Return height, update diameter |
| LC 113 — Path Sum II | Backtracking | Copy path before adding |
| LC 437 — Path Sum III | Prefix sum on tree | Backtrack the map too |
| LC 105 — Construct Tree | Preorder + HashMap | leftSize = inRoot - inStart |

**Goal:** Solve each in under 25 minutes. Focus on getting the edge cases right.

### Week 3: Hard and Advanced (35 minutes each)

These are the problems that separate good candidates from great ones.

| Problem | Pattern | Key Insight |
|---------|---------|-------------|
| LC 124 — Max Path Sum | Global + bottom-up | Return single branch, update with both |
| LC 297 — Serialize/Deserialize | Preorder + null markers | Queue for deserialization |
| LC 94 — Inorder (iterative) | Explicit stack | Push left, pop, go right |
| LC 114 — Flatten to List | Reverse preorder | Process right, left, root |
| LC 572 — Subtree | Recursive containment | isSameTree as helper |

**Goal:** Solve each in under 35 minutes. For LC 124, practice the dry run until you can explain the "return vs update" distinction clearly.

### Total: ~20 problems over 3 weeks

After completing this roadmap, you should be able to:
- Identify the correct DFS pattern within 2 minutes of reading a problem
- Write clean Java code without syntax errors
- Explain time and space complexity
- Handle edge cases (null root, single node, skewed tree)
- Discuss trade-offs between recursive and iterative approaches

### Spaced Repetition Schedule

```
Day 1-7:   Week 1 problems (first attempt)
Day 8-14:  Week 2 problems (first attempt)
Day 15-21: Week 3 problems (first attempt)
Day 22-28: Revisit Week 1 problems (should be fast now)
Day 29-35: Revisit Week 2 problems
Day 36-42: Revisit Week 3 problems + mock interviews
```

### Mock Interview Tips

**When you see a tree problem:**
1. Clarify: "Is this a BST or a general binary tree?"
2. Ask about constraints: "Can the tree be empty? Can values be negative?"
3. State your approach before coding: "I'll use bottom-up DFS, returning height from each node and updating a global diameter."
4. Trace through a small example before coding.
5. Code the base case first, then the recursive case.
6. Test with: empty tree, single node, two nodes, the given example.

**Red flags in your own solution:**
- You're passing more than 2-3 parameters → maybe bottom-up is cleaner
- Your function returns void but you need an answer → add a return value or global variable
- You're calling height() inside a loop → you're probably O(n^2), optimize to O(n)
- You forgot to backtrack after recursion → path problems will give wrong answers

---

## Cross-References

- **Topic 5 (Prefix Sum):** LC 437 uses prefix sum on a tree path. The same HashMap technique applies.
- **Topic 11 (BFS):** Level-order traversal, right side view, minimum depth. Use when the problem mentions "level" or "layer."
- **Topic 14 (Graph DFS):** Tree DFS is a special case. Graphs need a visited set; trees don't.
- **Topic 15 (Backtracking):** Path Sum II and similar problems use backtracking on the path list. The "add, recurse, remove" pattern is identical.
- **Topic 16 (Dynamic Programming on Trees):** Tree DP problems like LC 337 (House Robber III) use the return-value pattern with multiple return values (rob vs not-rob).

---

*Document 10 of 20 — Trees DFS. Next: Topic 11 — Trees BFS (Level Order Traversal).*
