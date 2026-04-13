# Topic 17: Union-Find (Disjoint Set Union)

> **Series position:** Document 17 of 20
> **Difficulty range:** Medium to Hard
> **Interview frequency:** Union-Find problems appear in ~15% of FAANG interviews. Amazon and Google ask connectivity and MST problems frequently. Meta favors accounts-merge style problems. Bloomberg asks network-monitoring variants.
> **Top companies:** Google, Amazon, Meta, Microsoft, Bloomberg
> **Prerequisites:** Topic 14 (Graph Traversal BFS/DFS), Topic 15 (Topological Sort)

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5 Intuition](#eli5-intuition)
3. [Core Templates in Java](#core-templates-in-java)
4. [Real-World Applications](#real-world-applications)
5. [Problem Categories and Solutions](#problem-categories-and-solutions)
   - [Category A: Connected Components](#category-a-connected-components)
   - [Category B: Cycle Detection and Redundancy](#category-b-cycle-detection-and-redundancy)
   - [Category C: Merging Groups](#category-c-merging-groups)
   - [Category D: MST and Advanced Variants](#category-d-mst-and-advanced-variants)
6. [Common Mistakes](#common-mistakes)
7. [Algorithm Comparison](#algorithm-comparison)
8. [Quick Reference Cheat Sheet](#quick-reference-cheat-sheet)
9. [Practice Roadmap](#practice-roadmap)

---

## Core Concept

### What Union-Find Solves

Union-Find (also called Disjoint Set Union, or DSU) is a data structure that tracks a collection of elements partitioned into non-overlapping groups. It supports two operations efficiently:

- **find(x):** Which group does element x belong to? Returns a representative (root) of x's group.
- **union(x, y):** Merge the groups containing x and y into one group.

The key question Union-Find answers: **"Are x and y in the same connected component?"**

This sounds simple, but the power is in *when* you use it. Union-Find shines when edges arrive one at a time and you need to answer connectivity queries online (as edges come in). BFS and DFS recompute from scratch each time. Union-Find maintains state incrementally.

---

### When to Use Union-Find vs BFS/DFS

**Use Union-Find when:**
- Edges are given one by one and you need to answer "are these connected?" after each addition
- You need to count connected components as edges are added
- You need to detect a cycle in an undirected graph
- You're building a Minimum Spanning Tree (Kruskal's algorithm)
- The graph is dynamic (edges added, not removed)

**Use BFS/DFS when:**
- The graph is static and you need a one-time traversal
- You need the actual path between nodes (Union-Find only tells you *if* connected, not *how*)
- You need to enumerate all nodes in a component
- The graph has directed edges (Union-Find is inherently undirected)

**The mental model:** BFS/DFS is like exploring a city with a map. Union-Find is like maintaining a phone book of "who's in the same group" that you update as new connections are discovered.

---

### The parent[] Array

The entire data structure lives in a single array: `parent[]`.

- `parent[i]` stores the parent of node i in a tree structure.
- Initially, every node is its own parent: `parent[i] = i`. Each node is its own group.
- When two groups merge, one root becomes the child of the other root.

```
Initial state (5 nodes, 5 separate groups):
parent = [0, 1, 2, 3, 4]
         (each node points to itself)

After union(0, 1):
parent = [0, 0, 2, 3, 4]
         (1's parent is now 0; group {0,1})

After union(2, 3):
parent = [0, 0, 2, 2, 4]
         (3's parent is now 2; group {2,3})

After union(0, 2):
parent = [0, 0, 0, 2, 4]
         (2's parent is now 0; group {0,1,2,3})
```

**find(x)** follows the parent chain until it reaches a node that is its own parent (the root). That root is the representative of x's group.

**Two nodes are in the same group if and only if find(x) == find(y).**

---

### Path Compression

Without optimization, find(x) can take O(n) time if the tree degenerates into a chain. Path compression fixes this.

After finding the root, path compression makes every node on the path point directly to the root. The next call to find() on any of those nodes takes O(1).

```
Before path compression (find(4)):
4 -> 3 -> 2 -> 1 -> 0 (root)

After path compression:
4 -> 0
3 -> 0
2 -> 0
1 -> 0
(all point directly to root)
```

Implementation: one-line recursive trick.

```java
int find(int x) {
    if (parent[x] != x) {
        parent[x] = find(parent[x]); // path compression
    }
    return parent[x];
}
```

---

### Union by Rank (or Size)

Without rank/size tracking, repeated unions can create tall trees. Union by rank ensures the shorter tree always attaches under the taller one, keeping trees flat.

- **Rank:** an upper bound on tree height. Start at 0. When two trees of equal rank merge, the new root's rank increases by 1.
- **Size:** actual number of nodes. Smaller tree attaches under larger tree.

Both work. Size is slightly more intuitive and gives tighter bounds in practice. Rank is more common in textbooks.

```java
void union(int x, int y) {
    int rootX = find(x);
    int rootY = find(y);
    if (rootX == rootY) return; // already same group

    // union by rank: attach smaller rank under larger rank
    if (rank[rootX] < rank[rootY]) {
        parent[rootX] = rootY;
    } else if (rank[rootX] > rank[rootY]) {
        parent[rootY] = rootX;
    } else {
        parent[rootY] = rootX;
        rank[rootX]++;
    }
}
```

---

### Time Complexity

With both path compression and union by rank:

- **find():** O(α(n)) amortized, where α is the inverse Ackermann function
- **union():** O(α(n)) amortized
- **α(n)** grows so slowly it's effectively constant for all practical inputs (α(n) ≤ 4 for n ≤ 10^600)

Without path compression: O(log n) per operation.
Without union by rank: O(log n) per operation.
Without either: O(n) per operation (degenerate chain).

**In interviews, say: "Both operations are effectively O(1) amortized with path compression and union by rank."**

---

### Union by Size vs Union by Rank

Both strategies keep trees flat. The difference is subtle.

**Union by rank** tracks an upper bound on tree height. When two trees of equal rank merge, the new root's rank increments. When ranks differ, the lower-rank tree attaches under the higher-rank tree. Rank never decreases.

```
rank 0: single node
rank 1: tree of height at most 1 (two nodes)
rank 2: tree of height at most 2 (at least 4 nodes)
rank k: tree with at least 2^k nodes
```

**Union by size** tracks the actual number of nodes. The smaller tree always attaches under the larger tree.

```java
// Union by size (alternative to rank)
void union(int x, int y) {
    int rx = find(x), ry = find(y);
    if (rx == ry) return;
    // Attach smaller under larger
    if (size[rx] < size[ry]) { int t = rx; rx = ry; ry = t; }
    parent[ry] = rx;
    size[rx] += size[ry];
    components--;
}
```

**Which to use?** In practice, both give O(log n) without path compression and O(α(n)) with it. Union by size is slightly more intuitive (you're literally merging smaller into larger). Union by rank is more common in academic literature. Pick one and stick with it. The standard template in this document uses rank.

---

### Path Compression Variants

**Recursive path compression (used in template):**
```java
int find(int x) {
    if (parent[x] != x) parent[x] = find(parent[x]);
    return parent[x];
}
```
Every node on the path to root points directly to root after the call. This is "full path compression."

**Path halving (alternative):**
```java
int find(int x) {
    while (parent[x] != x) {
        parent[x] = parent[parent[x]]; // point to grandparent
        x = parent[x];
    }
    return x;
}
```
Each node skips one level (points to grandparent instead of parent). Slightly less compression per call but avoids recursion. Same asymptotic complexity.

**Path splitting (alternative):**
```java
int find(int x) {
    while (parent[x] != x) {
        int next = parent[x];
        parent[x] = parent[parent[x]]; // point to grandparent
        x = next;
    }
    return x;
}
```
Similar to path halving. All three variants achieve O(α(n)) amortized.

**For interviews:** Use recursive path compression. It's the most concise and the most recognizable.

---

### Proof Sketch: Why Path Compression + Union by Rank = O(α(n))

The formal proof (by Tarjan, 1975) is complex, but the intuition is:

1. Union by rank ensures no tree has height greater than O(log n).
2. Path compression flattens trees aggressively. After a find() call, all nodes on the path point directly to the root.
3. The combination means that after enough operations, almost every node points directly to its root. Subsequent find() calls are O(1).
4. The amortized cost per operation, averaged over any sequence of m operations on n elements, is O(m * α(n)).

The inverse Ackermann function α(n) is defined as the inverse of the Ackermann function A(k, k). A(4, 4) is a number with 19,729 digits. So α(n) ≤ 4 for any n you'll ever encounter in practice.

---

## ELI5 Intuition

Imagine a school with students split into clubs. Each club has a president. Every student knows who their president is (directly or through a chain of "ask my friend who the president is").

**find(student):** Ask the student who their president is. They might say "ask Alice." Ask Alice. She says "ask Bob." Ask Bob. He says "I'm the president." That's the answer.

**Path compression:** After finding the president, you update everyone in the chain to point directly to the president. Next time you ask any of them, they answer instantly.

**union(club A, club B):** The two presidents meet. One becomes subordinate to the other. Now both clubs share a president.

**Union by rank:** When two clubs merge, the bigger club's president takes charge. This keeps the chain of command short. If you always made the smaller club's president the boss, you'd end up with very long chains.

**The magic:** After enough merges and path compressions, almost everyone points directly to their ultimate president. Queries become instant.

---

## Core Templates in Java

### Template 1: Standard Union-Find (Copy-Paste Ready)

This is the class you use in virtually every Union-Find problem. Memorize it.

```java
class UnionFind {
    private int[] parent;
    private int[] rank;
    private int components; // number of distinct groups

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i; // each node is its own root
            rank[i] = 0;
        }
    }

    // Find root of x with path compression
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // path compression (recursive)
        }
        return parent[x];
    }

    // Union by rank. Returns true if x and y were in different components.
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) return false; // already connected

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        components--;
        return true;
    }

    // Are x and y in the same component?
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    // How many distinct components exist?
    public int getComponents() {
        return components;
    }
}
```

**Usage pattern:**

```java
UnionFind uf = new UnionFind(n);

// Process edges
for (int[] edge : edges) {
    uf.union(edge[0], edge[1]);
}

// Query connectivity
if (uf.connected(a, b)) { ... }

// Count components
int count = uf.getComponents();
```

**Key design decisions in this template:**

1. `components` is decremented only when two *different* roots merge. If `union()` is called on already-connected nodes, `components` stays the same.
2. `union()` returns a boolean. This is useful for cycle detection: if `union()` returns false, the edge being added creates a cycle (both endpoints already connected).
3. Path compression is recursive. An iterative version exists but the recursive one is cleaner for interviews.

---

### Iterative Path Compression (Alternative find)

Some interviewers prefer iterative to avoid stack overflow on very large inputs. Both are correct.

```java
public int find(int x) {
    int root = x;
    // Find root
    while (parent[root] != root) {
        root = parent[root];
    }
    // Path compression: point all nodes on path directly to root
    while (parent[x] != root) {
        int next = parent[x];
        parent[x] = root;
        x = next;
    }
    return root;
}
```

---

### Template 2: Weighted Union-Find (Track Extra Info Per Component)

Sometimes you need to track metadata per component: size, sum, min, max, or a custom value. The pattern is the same: store the metadata indexed by root, and update it during union.

```java
class WeightedUnionFind {
    private int[] parent;
    private int[] rank;
    private int[] size;   // number of nodes in each component
    private int[] minVal; // minimum value in each component
    private int[] maxVal; // maximum value in each component
    private int components;

    public WeightedUnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        size = new int[n];
        minVal = new int[n];
        maxVal = new int[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
            size[i] = 1;
            minVal[i] = i; // or actual value if provided
            maxVal[i] = i;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) return false;

        // Merge smaller into larger (by rank)
        if (rank[rootX] < rank[rootY]) {
            int temp = rootX; rootX = rootY; rootY = temp;
        }
        // rootX becomes the new root
        parent[rootY] = rootX;
        if (rank[rootX] == rank[rootY]) rank[rootX]++;

        // Update metadata for the new root
        size[rootX] += size[rootY];
        minVal[rootX] = Math.min(minVal[rootX], minVal[rootY]);
        maxVal[rootX] = Math.max(maxVal[rootX], maxVal[rootY]);

        components--;
        return true;
    }

    public int getSize(int x) {
        return size[find(x)];
    }

    public int getMin(int x) {
        return minVal[find(x)];
    }

    public int getMax(int x) {
        return maxVal[find(x)];
    }

    public int getComponents() {
        return components;
    }
}
```

**Critical rule:** Always read/write metadata using `find(x)` as the index, not `x` directly. After path compression, `parent[x]` may not be the root. Only the root's metadata is valid.

```java
// WRONG: reading metadata at x directly
int sz = size[x]; // x might not be the root

// CORRECT: read at the root
int sz = size[find(x)]; // always correct
```

---

### Template 3: Union-Find with String Keys (HashMap-based)

When nodes are strings (like email addresses in LC 721), use a HashMap instead of an array.

```java
class StringUnionFind {
    private Map<String, String> parent;
    private Map<String, Integer> rank;

    public StringUnionFind() {
        parent = new HashMap<>();
        rank = new HashMap<>();
    }

    // Add a new node if not already present
    public void add(String x) {
        if (!parent.containsKey(x)) {
            parent.put(x, x);
            rank.put(x, 0);
        }
    }

    public String find(String x) {
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent.get(x)));
        }
        return parent.get(x);
    }

    public void union(String x, String y) {
        String rootX = find(x);
        String rootY = find(y);
        if (rootX.equals(rootY)) return;

        int rankX = rank.get(rootX);
        int rankY = rank.get(rootY);
        if (rankX < rankY) {
            parent.put(rootX, rootY);
        } else if (rankX > rankY) {
            parent.put(rootY, rootX);
        } else {
            parent.put(rootY, rootX);
            rank.put(rootX, rankX + 1);
        }
    }

    public boolean connected(String x, String y) {
        return find(x).equals(find(y));
    }
}
```

---

### Template 4: Union-Find with Rollback (Advanced)

Standard Union-Find with path compression doesn't support rollback (undoing a union). If you need to undo unions (e.g., offline queries with deletions), you must use union by rank *without* path compression. This keeps the tree structure deterministic and reversible.

```java
class RollbackUnionFind {
    private int[] parent;
    private int[] rank;
    private int components;
    // Stack stores (node, oldParent, node, oldRank, rootX, rootY) for rollback
    private Deque<int[]> history;

    public RollbackUnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        history = new ArrayDeque<>();
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    // No path compression! Required for rollback correctness.
    public int find(int x) {
        while (parent[x] != x) x = parent[x];
        return x;
    }

    public boolean union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) {
            history.push(new int[]{-1, -1, -1, -1}); // no-op marker
            return false;
        }
        if (rank[rx] < rank[ry]) { int t = rx; rx = ry; ry = t; }
        // Save state before modifying
        history.push(new int[]{ry, parent[ry], rx, rank[rx]});
        parent[ry] = rx;
        if (rank[rx] == rank[ry]) rank[rx]++;
        components--;
        return true;
    }

    public void rollback() {
        int[] state = history.pop();
        if (state[0] == -1) return; // no-op
        int ry = state[0], oldParentRy = state[1];
        int rx = state[2], oldRankRx = state[3];
        parent[ry] = oldParentRy;
        rank[rx] = oldRankRx;
        components++;
    }

    public boolean connected(int x, int y) { return find(x) == find(y); }
}
```

**When you need this:** Problems with offline queries where you process edges in a specific order and need to "undo" some unions. This is rare in standard FAANG interviews but appears in competitive programming.

**The tradeoff:** Without path compression, find() is O(log n) instead of O(α(n)). But rollback is O(1). For problems requiring rollback, this is the only correct approach.

---

## Real-World Applications

### Social Networks

Facebook's "People You May Know" feature uses connectivity. When you connect with someone, Union-Find merges your social circles. Queries like "are these two users in the same network?" run in near-constant time regardless of network size.

### Kruskal's Minimum Spanning Tree

Kruskal's algorithm builds an MST by sorting all edges by weight and greedily adding the cheapest edge that doesn't create a cycle. Union-Find is the cycle detector: before adding edge (u, v), check `find(u) == find(v)`. If true, skip (cycle). If false, add the edge and call `union(u, v)`.

```
Sort edges by weight: O(E log E)
Process each edge with Union-Find: O(E * α(V)) ≈ O(E)
Total: O(E log E)
```

### Network Connectivity Monitoring

In distributed systems, nodes (servers) go up and down. When a new connection is established between two servers, Union-Find merges their components. Queries like "can server A reach server B?" are answered instantly. This is far cheaper than running BFS across the entire network topology on every query.

### Image Segmentation

In computer vision, pixels are nodes. Two adjacent pixels with similar color values get connected. Union-Find groups them into regions (connected components). Each component is a segment. This runs in O(pixels) time, which is critical for real-time processing.

### Database Transaction Conflict Detection

In databases with optimistic concurrency control, transactions that touch overlapping data sets conflict. Union-Find groups transactions by the data they access. Two transactions conflict if they're in the same component. This is used in conflict-serializable schedule detection.

### Percolation Theory

In physics and materials science, percolation asks: "Does a path exist from the top to the bottom of a grid?" Each cell is a node. Open cells connect to their neighbors. Union-Find answers whether the top row and bottom row are connected. This models fluid flow through porous materials, electrical conductivity, and epidemic spreading.

---

### Online vs Offline Algorithms

Union-Find is an **online** algorithm: it processes each edge as it arrives and can answer queries at any point. This contrasts with **offline** algorithms that require all input upfront.

**Online scenario (Union-Find shines):**
```
t=0: Add edge (1,2). Query: connected(1,3)? -> No
t=1: Add edge (2,3). Query: connected(1,3)? -> Yes
t=2: Add edge (4,5). Query: connected(1,4)? -> No
```

Each query is answered in O(α(n)) without reprocessing previous edges.

**Offline scenario (BFS/DFS is fine):**
```
Given: all edges at once
Query: for each pair (u,v), are they connected?
```

Here you can build the full graph and run BFS/DFS once. No need for Union-Find.

The distinction matters in system design interviews. If an interviewer describes a streaming data scenario ("connections arrive in real time"), Union-Find is the right answer.

---

## Problem Categories and Solutions

### Category A: Connected Components

---

#### LC 547 — Number of Provinces

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium

**Problem:** Given an n x n adjacency matrix `isConnected` where `isConnected[i][j] = 1` means cities i and j are directly connected, return the number of provinces (connected components).

**Approach:** Classic Union-Find. Process every pair (i, j) where `isConnected[i][j] == 1` and call `union(i, j)`. The answer is `uf.getComponents()`.

```java
class Solution {
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        UnionFind uf = new UnionFind(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) { // j = i+1 avoids processing each edge twice
                if (isConnected[i][j] == 1) {
                    uf.union(i, j);
                }
            }
        }

        return uf.getComponents();
    }
}
```

**Complexity:** Time O(n^2 * α(n)), Space O(n).

**Note:** This is also solvable with DFS/BFS (Topic 14). Union-Find is cleaner here because the input is already an adjacency matrix.

---

#### LC 323 — Number of Connected Components in an Undirected Graph (Premium)

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium

**Problem:** Given n nodes (0 to n-1) and a list of undirected edges, return the number of connected components.

**Approach:** Initialize Union-Find with n nodes. Process each edge with `union()`. Return `getComponents()`.

```java
class Solution {
    public int countComponents(int n, int[][] edges) {
        UnionFind uf = new UnionFind(n);

        for (int[] edge : edges) {
            uf.union(edge[0], edge[1]);
        }

        return uf.getComponents();
    }
}
```

**Complexity:** Time O(E * α(n)), Space O(n).

**This is the purest Union-Find problem.** If you understand this, you understand the pattern.

---

#### LC 200 — Number of Islands (Union-Find Approach)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium

**Problem:** Given a 2D grid of '1' (land) and '0' (water), count the number of islands.

**Two approaches side by side:**

**Approach 1: DFS (from Topic 14)**

```java
class Solution {
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int rows = grid.length, cols = grid[0].length;
        int count = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    dfs(grid, r, c);
                }
            }
        }
        return count;
    }

    private void dfs(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') return;
        grid[r][c] = '0'; // mark visited
        dfs(grid, r + 1, c);
        dfs(grid, r - 1, c);
        dfs(grid, r, c + 1);
        dfs(grid, r, c - 1);
    }
}
```

**Approach 2: Union-Find**

```java
class Solution {
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int rows = grid.length, cols = grid[0].length;

        // Initialize UF with only land cells
        // Use rows*cols as a sentinel for water cells
        UnionFind uf = new UnionFind(rows * cols);
        int waterCount = 0;

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '0') {
                    waterCount++;
                    continue;
                }
                // Connect this land cell to adjacent land cells
                for (int[] dir : directions) {
                    int nr = r + dir[0], nc = c + dir[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == '1') {
                        uf.union(r * cols + c, nr * cols + nc);
                    }
                }
            }
        }

        // Total components minus water cells = island count
        return uf.getComponents() - waterCount;
    }
}
```

**Comparison:**

| Aspect | DFS | Union-Find |
|---|---|---|
| Time | O(rows * cols) | O(rows * cols * α(n)) |
| Space | O(rows * cols) call stack | O(rows * cols) parent array |
| Modifies input | Yes (marks visited) | No |
| Dynamic updates | No (recompute from scratch) | Yes (add new land cells) |
| Code simplicity | Simpler | More setup |

**When to prefer Union-Find for islands:** If the problem adds land cells one at a time and asks for the island count after each addition (LC 305 — Number of Islands II), Union-Find is the only efficient approach. DFS would be O(n * rows * cols) total; Union-Find is O(n * α(n)).

**Complexity (UF approach):** Time O(rows * cols * α(n)), Space O(rows * cols).

---

#### LC 1971 — Find if Path Exists in Graph

**Companies:** Amazon, Google, Meta
**Difficulty:** Easy

**Problem:** Given n nodes, a list of undirected edges, a source, and a destination, return true if a path exists from source to destination.

**Approach:** Build Union-Find from all edges. Check `uf.connected(source, destination)`.

```java
class Solution {
    public boolean validPath(int n, int[][] edges, int source, int destination) {
        UnionFind uf = new UnionFind(n);

        for (int[] edge : edges) {
            uf.union(edge[0], edge[1]);
        }

        return uf.connected(source, destination);
    }
}
```

**Complexity:** Time O(E * α(n)), Space O(n).

**Note:** This is also solvable with BFS/DFS. Union-Find is overkill for a single query but demonstrates the pattern cleanly.

---

### Category B: Cycle Detection and Redundancy

---

#### LC 684 — Redundant Connection

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium

**Problem:** Given a tree with n nodes (1 to n) and one extra edge added (making it a graph with exactly one cycle), find and return the redundant edge. If multiple answers exist, return the last one in the input.

**Key insight:** Process edges one by one. The first edge where both endpoints are already connected (same component) is the redundant edge. It creates the cycle.

```java
class Solution {
    public int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        UnionFind uf = new UnionFind(n + 1); // nodes are 1-indexed

        for (int[] edge : edges) {
            // union() returns false if already connected = cycle detected
            if (!uf.union(edge[0], edge[1])) {
                return edge;
            }
        }

        return new int[]{-1, -1}; // should never reach here per problem constraints
    }
}
```

**Complexity:** Time O(n * α(n)), Space O(n).

---

**MANDATORY TRACE: parent[] state after each union**

Input: `edges = [[1,2],[1,3],[2,3]]`

Initial state (nodes 1-3, 1-indexed, parent[0] unused):
```
Index:  0  1  2  3
parent: 0  1  2  3
rank:   0  0  0  0
```

**Process edge [1, 2]:**
- find(1) = 1, find(2) = 2. Different roots.
- rank[1] == rank[2] == 0, so parent[2] = 1, rank[1]++
- union() returns true (no cycle)

```
Index:  0  1  2  3
parent: 0  1  1  3   <-- 2's parent is now 1
rank:   0  1  0  0
```

**Process edge [1, 3]:**
- find(1) = 1, find(3) = 3. Different roots.
- rank[1] = 1 > rank[3] = 0, so parent[3] = 1
- union() returns true (no cycle)

```
Index:  0  1  2  3
parent: 0  1  1  1   <-- 3's parent is now 1
rank:   0  1  0  0
```

**Process edge [2, 3]:**
- find(2): parent[2] = 1, parent[1] = 1. Root = 1.
- find(3): parent[3] = 1, parent[1] = 1. Root = 1.
- find(2) == find(3) == 1. **Same root! Cycle detected.**
- union() returns false.
- **Return [2, 3].**

---

**Second trace with longer chain:**

Input: `edges = [[1,2],[2,3],[3,4],[1,4],[1,5]]`

Initial:
```
parent: [0, 1, 2, 3, 4, 5]
rank:   [0, 0, 0, 0, 0, 0]
```

**Process [1,2]:** find(1)=1, find(2)=2. Merge. parent[2]=1, rank[1]=1.
```
parent: [0, 1, 1, 3, 4, 5]
rank:   [0, 1, 0, 0, 0, 0]
```

**Process [2,3]:** find(2)=1, find(3)=3. Merge. rank[1]=1 > rank[3]=0, so parent[3]=1.
```
parent: [0, 1, 1, 1, 4, 5]
rank:   [0, 1, 0, 0, 0, 0]
```

**Process [3,4]:** find(3)=1, find(4)=4. Merge. rank[1]=1 > rank[4]=0, so parent[4]=1.
```
parent: [0, 1, 1, 1, 1, 5]
rank:   [0, 1, 0, 0, 0, 0]
```

**Process [1,4]:** find(1)=1, find(4)=1 (path: 4->1). Same root! **Cycle detected. Return [1,4].**

Path compression during find(4): parent[4] was already 1, no change needed.

---

#### LC 990 — Satisfiability of Equality Equations

**Companies:** Amazon, Google, Microsoft
**Difficulty:** Medium

**Problem:** Given equations like `["a==b","b!=c","b==c"]`, return true if all equations can be satisfied simultaneously.

**Key insight:** Two-pass approach.
1. First pass: process all `==` equations. Union the two variables.
2. Second pass: process all `!=` equations. If the two variables are in the same component, return false (contradiction).

```java
class Solution {
    public boolean equationsPossible(String[] equations) {
        UnionFind uf = new UnionFind(26); // 26 lowercase letters

        // Pass 1: union all equal pairs
        for (String eq : equations) {
            if (eq.charAt(1) == '=') { // "a==b"
                int x = eq.charAt(0) - 'a';
                int y = eq.charAt(3) - 'a';
                uf.union(x, y);
            }
        }

        // Pass 2: check inequality constraints
        for (String eq : equations) {
            if (eq.charAt(1) == '!') { // "a!=b"
                int x = eq.charAt(0) - 'a';
                int y = eq.charAt(3) - 'a';
                if (uf.connected(x, y)) {
                    return false; // contradiction: x==y AND x!=y
                }
            }
        }

        return true;
    }
}
```

**Complexity:** Time O(n * α(26)) = O(n), Space O(26) = O(1).

**Why two passes?** If you process `!=` before all `==` are processed, you might incorrectly reject a valid assignment. Example: `["a!=b","a==b"]` — if you process `a!=b` first, you'd check connectivity before the union is done.

**Cross-reference:** This problem also appears in Topic 16 (Shortest Path) as a graph-of-equations problem solvable with Bellman-Ford. The Union-Find approach is simpler and faster for this specific variant.

---

### Category C: Merging Groups

---

#### LC 721 — Accounts Merge

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium

**Problem:** Given a list of accounts where each account is `[name, email1, email2, ...]`, merge accounts that share at least one email. Return the merged accounts with emails sorted.

**Key insight:** Emails are the nodes. Two emails in the same account list are connected. Use Union-Find to group emails, then reconstruct accounts.

**Step-by-step approach:**
1. Map each email to an index (for array-based Union-Find).
2. For each account, union all emails in that account together (union each email with the first email in the account).
3. Group emails by their root representative.
4. For each group, find the account name (stored when first seen) and sort the emails.

```java
class Solution {
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        // Map each email to a unique integer index
        Map<String, Integer> emailIndex = new HashMap<>();
        Map<String, String> emailToName = new HashMap<>();
        int idx = 0;

        for (List<String> account : accounts) {
            String name = account.get(0);
            for (int i = 1; i < account.size(); i++) {
                String email = account.get(i);
                if (!emailIndex.containsKey(email)) {
                    emailIndex.put(email, idx++);
                    emailToName.put(email, name);
                }
            }
        }

        // Build Union-Find over email indices
        UnionFind uf = new UnionFind(idx);

        for (List<String> account : accounts) {
            // Union all emails in this account with the first email
            int firstIdx = emailIndex.get(account.get(1));
            for (int i = 2; i < account.size(); i++) {
                uf.union(firstIdx, emailIndex.get(account.get(i)));
            }
        }

        // Group emails by their root
        Map<Integer, List<String>> groups = new HashMap<>();
        for (String email : emailIndex.keySet()) {
            int root = uf.find(emailIndex.get(email));
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(email);
        }

        // Build result
        List<List<String>> result = new ArrayList<>();
        for (List<String> emails : groups.values()) {
            Collections.sort(emails);
            List<String> merged = new ArrayList<>();
            merged.add(emailToName.get(emails.get(0))); // account name
            merged.addAll(emails);
            result.add(merged);
        }

        return result;
    }
}
```

**Complexity:** Time O(n * k * α(n * k)) where n = accounts, k = avg emails per account. Practically O(n * k * log(n * k)) due to sorting. Space O(n * k).

**Why Union-Find over DFS here?** Both work. Union-Find is cleaner because you don't need to build an explicit adjacency list. The "connection" between emails is implicit (same account list), and Union-Find handles it directly.

**Walkthrough with example:**

```
accounts = [
  ["John", "john@mail.com", "john_newyork@mail.com"],
  ["John", "johnsmith@mail.com", "john@mail.com"],
  ["Mary", "mary@mail.com"]
]

Email indices:
  john@mail.com -> 0
  john_newyork@mail.com -> 1
  johnsmith@mail.com -> 2
  mary@mail.com -> 3

After processing account 0 (union 0 and 1):
  parent: [0, 0, 2, 3]  (1's root is 0)

After processing account 1 (union 0 and 2):
  parent: [0, 0, 0, 3]  (2's root is 0)

After processing account 2 (no union needed, single email):
  parent: [0, 0, 0, 3]

Groups by root:
  root 0: [john@mail.com, john_newyork@mail.com, johnsmith@mail.com]
  root 3: [mary@mail.com]

Result:
  ["John", "john@mail.com", "john_newyork@mail.com", "johnsmith@mail.com"]
  ["Mary", "mary@mail.com"]
```

---

#### LC 128 — Longest Consecutive Sequence (Union-Find Approach)

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium
**Cross-reference:** Topic 6 (HashMap and Frequency Counting) covers the O(n) HashSet approach.

**Problem:** Given an unsorted array of integers, find the length of the longest consecutive sequence.

**Union-Find approach:** Map each number to an index. For each number n, if n+1 exists in the array, union them. Track component sizes. The answer is the maximum component size.

```java
class Solution {
    public int longestConsecutive(int[] nums) {
        if (nums.length == 0) return 0;

        Map<Integer, Integer> numToIndex = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (!numToIndex.containsKey(nums[i])) { // handle duplicates
                numToIndex.put(nums[i], numToIndex.size());
            }
        }

        int n = numToIndex.size();
        int[] parent = new int[n];
        int[] size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }

        for (int num : numToIndex.keySet()) {
            if (numToIndex.containsKey(num + 1)) {
                int idx1 = numToIndex.get(num);
                int idx2 = numToIndex.get(num + 1);
                union(parent, size, idx1, idx2);
            }
        }

        int maxSize = 0;
        for (int i = 0; i < n; i++) {
            if (parent[i] == i) { // root node
                maxSize = Math.max(maxSize, size[i]);
            }
        }
        return maxSize;
    }

    private int find(int[] parent, int x) {
        if (parent[x] != x) parent[x] = find(parent, parent[x]);
        return parent[x];
    }

    private void union(int[] parent, int[] size, int x, int y) {
        int rx = find(parent, x), ry = find(parent, y);
        if (rx == ry) return;
        if (size[rx] < size[ry]) { int t = rx; rx = ry; ry = t; }
        parent[ry] = rx;
        size[rx] += size[ry];
    }
}
```

**Complexity:** Time O(n * α(n)), Space O(n).

**Honest comparison:** The HashSet approach from Topic 6 is O(n) time and O(n) space with simpler code. The Union-Find approach is also O(n) amortized but with more overhead. In an interview, the HashSet approach is preferred for this specific problem. The Union-Find approach is worth knowing because it generalizes to "merge consecutive ranges" problems.

---

### Category D: MST and Advanced Variants

---

#### LC 1584 — Min Cost to Connect All Points (Kruskal's Algorithm)

**Companies:** Amazon, Google, Microsoft
**Difficulty:** Medium

**Problem:** Given n points on a 2D plane, connect all points with minimum total Manhattan distance cost. Each connection is an edge. Return the minimum cost.

**This is a Minimum Spanning Tree problem.** Kruskal's algorithm: sort all possible edges by weight, greedily add the cheapest edge that doesn't create a cycle.

```java
class Solution {
    public int minCostConnectPoints(int[][] points) {
        int n = points.length;

        // Generate all edges: O(n^2) edges
        List<int[]> edges = new ArrayList<>(); // [cost, i, j]
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int cost = Math.abs(points[i][0] - points[j][0])
                         + Math.abs(points[i][1] - points[j][1]);
                edges.add(new int[]{cost, i, j});
            }
        }

        // Sort edges by cost
        edges.sort((a, b) -> a[0] - b[0]);

        // Kruskal's: add cheapest edge that doesn't create a cycle
        UnionFind uf = new UnionFind(n);
        int totalCost = 0;
        int edgesUsed = 0;

        for (int[] edge : edges) {
            int cost = edge[0], u = edge[1], v = edge[2];
            if (uf.union(u, v)) { // returns true if they were in different components
                totalCost += cost;
                edgesUsed++;
                if (edgesUsed == n - 1) break; // MST has exactly n-1 edges
            }
        }

        return totalCost;
    }
}
```

**Complexity:** Time O(n^2 log n) for sorting n^2 edges. Space O(n^2) for edge list.

**Why n-1 edges?** A spanning tree on n nodes has exactly n-1 edges. Once you've added n-1 edges without creating a cycle, you have the MST.

**Kruskal's vs Prim's for this problem:**
- Kruskal's: sort all edges, use Union-Find for cycle detection. Better when E is small relative to V^2.
- Prim's: grow the MST from a starting node using a min-heap. Better for dense graphs (like this one with n^2 edges).
- For this specific problem, Prim's with a min-heap runs in O(n^2 log n) too, but with better constants. Both are acceptable in interviews.

---

#### LC 399 — Evaluate Division (Union-Find Variant)

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium
**Cross-reference:** Topic 16 (Shortest Path) covers the Dijkstra/BFS approach.

**Problem:** Given equations like `a/b = 2.0` and queries like `a/c`, compute the result of each query. Return -1.0 if the answer doesn't exist.

**Union-Find approach with weights:** Each variable is a node. The edge weight represents the ratio. For `a/b = k`, we store that `a` and `b` are connected with ratio k. To answer `a/c`, find the path from a to c through their common root and multiply ratios.

This is a **weighted Union-Find** where the weight represents the ratio from a node to its root.

```java
class Solution {
    private Map<String, String> parent;
    private Map<String, Double> weight; // weight[x] = value of x / value of root(x)

    public double[] calcEquation(List<List<String>> equations,
                                  double[] values,
                                  List<List<String>> queries) {
        parent = new HashMap<>();
        weight = new HashMap<>();

        // Initialize
        for (List<String> eq : equations) {
            String a = eq.get(0), b = eq.get(1);
            parent.putIfAbsent(a, a);
            parent.putIfAbsent(b, b);
            weight.putIfAbsent(a, 1.0);
            weight.putIfAbsent(b, 1.0);
        }

        // Union with weights
        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            union(a, b, values[i]);
        }

        // Answer queries
        double[] results = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String a = queries.get(i).get(0);
            String b = queries.get(i).get(1);
            if (!parent.containsKey(a) || !parent.containsKey(b)) {
                results[i] = -1.0;
            } else {
                String rootA = find(a);
                String rootB = find(b);
                if (!rootA.equals(rootB)) {
                    results[i] = -1.0;
                } else {
                    // weight[a] = a/rootA, weight[b] = b/rootB
                    // a/b = (a/rootA) / (b/rootB) = weight[a] / weight[b]
                    results[i] = weight.get(a) / weight.get(b);
                }
            }
        }

        return results;
    }

    private String find(String x) {
        if (!parent.get(x).equals(x)) {
            String root = find(parent.get(x));
            // Update weight: x/root = (x/parent) * (parent/root)
            weight.put(x, weight.get(x) * weight.get(parent.get(x)));
            parent.put(x, root);
        }
        return parent.get(x);
    }

    private void union(String a, String b, double ratio) {
        // ratio = a/b
        String rootA = find(a);
        String rootB = find(b);
        if (rootA.equals(rootB)) return;

        // After find(), weight[a] = a/rootA, weight[b] = b/rootB
        // We want rootA/rootB = (a/rootA)^-1 * (a/b) * (b/rootB)
        //                     = ratio * weight[b] / weight[a]
        parent.put(rootA, rootB);
        weight.put(rootA, ratio * weight.get(b) / weight.get(a));
    }
}
```

**Complexity:** Time O((E + Q) * α(V)), Space O(V) where V = number of unique variables.

**This is the hardest Union-Find variant.** The weight maintenance during path compression is tricky. In interviews, the BFS/DFS approach (Topic 16) is often cleaner to explain. Know both; default to BFS unless the interviewer specifically asks for Union-Find.

**The weight invariant:** After `find(x)`, `weight[x]` always equals `value(x) / value(root(x))`. Path compression must update weights along the path to maintain this invariant.

---

### Bonus: LC 305 — Number of Islands II (Premium, Dynamic)

**Companies:** Google, Amazon
**Difficulty:** Hard

This is the problem that shows why Union-Find is irreplaceable. You're given an m x n grid, initially all water. You receive a list of positions to turn into land, one at a time. After each addition, return the number of islands.

**Why BFS/DFS fails:** After each land addition, you'd need to re-run BFS/DFS on the entire grid. With k additions and m*n grid size, that's O(k * m * n) total. For large inputs, this is too slow.

**Why Union-Find works:** Each land addition is O(α(m*n)). Total: O(k * α(m*n)).

```java
class Solution {
    int[] parent, rank;
    int components;

    public List<Integer> numIslands2(int m, int n, int[][] positions) {
        parent = new int[m * n];
        rank = new int[m * n];
        Arrays.fill(parent, -1); // -1 means water
        components = 0;

        int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}};
        List<Integer> result = new ArrayList<>();

        for (int[] pos : positions) {
            int r = pos[0], c = pos[1];
            int idx = r * n + c;

            if (parent[idx] != -1) {
                // Already land (duplicate position)
                result.add(components);
                continue;
            }

            // Add new land cell
            parent[idx] = idx;
            rank[idx] = 0;
            components++;

            // Connect to adjacent land cells
            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                int nIdx = nr * n + nc;
                if (nr >= 0 && nr < m && nc >= 0 && nc < n && parent[nIdx] != -1) {
                    union(idx, nIdx);
                }
            }

            result.add(components);
        }

        return result;
    }

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    void union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return;
        if (rank[rx] < rank[ry]) { int t = rx; rx = ry; ry = t; }
        parent[ry] = rx;
        if (rank[rx] == rank[ry]) rank[rx]++;
        components--;
    }
}
```

**Complexity:** Time O(k * α(m*n)), Space O(m*n).

This problem is the clearest demonstration of Union-Find's advantage over BFS/DFS for dynamic connectivity.

---

## Common Mistakes

### Mistake 1: Forgetting Path Compression

```java
// WRONG: no path compression
int find(int x) {
    while (parent[x] != x) {
        x = parent[x];
    }
    return x;
}
// This is O(n) per call in the worst case (degenerate chain)

// CORRECT: with path compression
int find(int x) {
    if (parent[x] != x) {
        parent[x] = find(parent[x]);
    }
    return parent[x];
}
```

Without path compression, a sequence of n unions followed by n finds takes O(n^2) total. With path compression, it's O(n * α(n)).

---

### Mistake 2: Not Initializing parent[i] = i

```java
// WRONG: uninitialized parent array
int[] parent = new int[n];
// parent[i] defaults to 0 for all i
// find(3) would return 0 (wrong root)

// CORRECT:
int[] parent = new int[n];
for (int i = 0; i < n; i++) {
    parent[i] = i; // each node is its own root
}
```

This is the most common bug in Union-Find implementations. Every node must start as its own parent.

---

### Mistake 3: Union Without Rank (Linear Chain Problem)

```java
// WRONG: always attach x under y
void union(int x, int y) {
    parent[find(x)] = find(y);
}
// Repeated unions can create a chain: 0->1->2->3->...->n
// find(0) takes O(n) even with path compression on first call

// CORRECT: union by rank
void union(int x, int y) {
    int rx = find(x), ry = find(y);
    if (rx == ry) return;
    if (rank[rx] < rank[ry]) parent[rx] = ry;
    else if (rank[rx] > rank[ry]) parent[ry] = rx;
    else { parent[ry] = rx; rank[rx]++; }
}
```

---

### Mistake 4: Comparing parent[x] == parent[y] Instead of find(x) == find(y)

```java
// WRONG: comparing parents directly
if (parent[x] == parent[y]) { ... }
// This only checks if x and y have the same immediate parent
// Two nodes in the same component may have different immediate parents

// CORRECT: compare roots
if (find(x) == find(y)) { ... }
```

Example: After `union(0,1)` and `union(0,2)`, nodes 1 and 2 are in the same component. But `parent[1] = 0` and `parent[2] = 0`, so `parent[1] == parent[2]` happens to be true here. But after more complex unions, this breaks. Always use `find()`.

---

### Mistake 5: Off-by-One with 1-Indexed Problems

Many graph problems use 1-indexed nodes (nodes 1 to n). Initialize Union-Find with size n+1 and ignore index 0.

```java
// WRONG: size n for 1-indexed nodes
UnionFind uf = new UnionFind(n);
uf.union(1, n); // index n is out of bounds!

// CORRECT:
UnionFind uf = new UnionFind(n + 1); // indices 0..n, use 1..n
```

---

### Mistake 6: Modifying Components Count Incorrectly

```java
// WRONG: always decrement components
void union(int x, int y) {
    parent[find(x)] = find(y);
    components--; // wrong if x and y were already connected!
}

// CORRECT: only decrement when merging different components
boolean union(int x, int y) {
    int rx = find(x), ry = find(y);
    if (rx == ry) return false; // already connected, no change
    // ... merge ...
    components--;
    return true;
}
```

---

### Mistake 7: Reading Metadata at x Instead of find(x)

```java
// WRONG: reading size at node x
int sz = size[x]; // x might not be the root; size[x] is stale

// CORRECT: always read metadata at the root
int sz = size[find(x)];
```

After path compression, `parent[x]` may point directly to the root, but `x` itself is not the root. Metadata is only valid at root nodes.

---

## Algorithm Comparison

### Union-Find vs BFS/DFS for Connectivity

| Criterion | Union-Find | BFS/DFS |
|---|---|---|
| Static graph (all edges known upfront) | Both work | Both work |
| Dynamic graph (edges added over time) | Excellent | Poor (recompute from scratch) |
| Online queries (answer after each edge) | O(α(n)) per query | O(V + E) per query |
| Find actual path between nodes | Cannot | Can |
| Directed graphs | Not applicable | Works |
| Detect cycle (undirected) | O(E * α(V)) | O(V + E) |
| Count components | O(E * α(V)) | O(V + E) |
| Memory | O(V) | O(V + E) for adjacency list |
| Code complexity | Medium (class setup) | Low (recursive DFS) |

---

### When Union-Find Wins

**Scenario: Online connectivity queries**

You have a network of 10 million servers. Connections are established one at a time. After each connection, you need to answer "can server A reach server B?"

- BFS/DFS: O(V + E) per query = O(10M + connections) per query. With 1M queries, that's 10^13 operations.
- Union-Find: O(α(n)) per query ≈ O(1). With 1M queries, that's ~1M operations.

**Scenario: Kruskal's MST**

You need to build an MST. Kruskal's processes edges in sorted order and needs cycle detection after each edge addition.

- BFS/DFS cycle detection: O(V + E) per edge = O(E * (V + E)) total.
- Union-Find cycle detection: O(α(V)) per edge = O(E * α(V)) total.

---

### When BFS/DFS Wins

**Scenario: Find the actual path**

"What is the shortest path from A to B?" Union-Find can tell you *if* a path exists but not *what* the path is. BFS gives you the path.

**Scenario: One-time component enumeration**

"List all nodes in each connected component." BFS/DFS naturally groups nodes as it traverses. Union-Find requires a post-processing step (group nodes by their root).

**Scenario: Directed graphs**

Union-Find is inherently undirected. For directed connectivity (strongly connected components), use Tarjan's or Kosaraju's algorithm (both DFS-based).

**Scenario: Weighted shortest path**

Union-Find has no concept of edge weights for path finding. Dijkstra's (Topic 16) is the right tool.

---

### The Decision Rule

```
Is the graph directed?
  Yes -> BFS/DFS (or Dijkstra/Bellman-Ford for weighted)
  No  -> Is connectivity dynamic (edges added over time)?
           Yes -> Union-Find
           No  -> Do you need the actual path?
                    Yes -> BFS/DFS
                    No  -> Either works; Union-Find is faster for repeated queries
```

---

## Quick Reference Cheat Sheet

### The Standard Template (Memorize This)

```java
class UnionFind {
    int[] parent, rank;
    int components;

    UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    boolean union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return false;
        if (rank[rx] < rank[ry]) { int t = rx; rx = ry; ry = t; }
        parent[ry] = rx;
        if (rank[rx] == rank[ry]) rank[rx]++;
        components--;
        return true;
    }

    boolean connected(int x, int y) { return find(x) == find(y); }
    int getComponents() { return components; }
}
```

---

### Complexity Summary

| Operation | No optimization | Path compression only | Union by rank only | Both |
|---|---|---|---|---|
| find() | O(n) | O(log n) amortized | O(log n) | O(α(n)) |
| union() | O(n) | O(log n) amortized | O(log n) | O(α(n)) |

α(n) = inverse Ackermann. For all practical n, α(n) ≤ 4.

---

### Problem Pattern Recognition

| Problem says... | Use this |
|---|---|
| "Are nodes X and Y connected?" | `uf.connected(x, y)` |
| "Count connected components" | `uf.getComponents()` after all unions |
| "Detect cycle in undirected graph" | `union()` returns false = cycle |
| "Minimum spanning tree" | Kruskal's = sort edges + Union-Find |
| "Merge groups with shared elements" | Union-Find on elements |
| "Edges added one by one, query connectivity" | Union-Find (not BFS/DFS) |

---

### 2D Grid to 1D Index

For grid problems, convert (row, col) to a single index:

```java
int index = row * cols + col;
// Reverse: row = index / cols, col = index % cols
```

---

### Initialization Checklist

Before every Union-Find problem:
- [ ] `parent[i] = i` for all i (not 0, not -1, not uninitialized)
- [ ] `rank[i] = 0` for all i
- [ ] `components = n`
- [ ] If 1-indexed nodes: use `new UnionFind(n + 1)`
- [ ] If string keys: use HashMap-based Union-Find

---

### Common Patterns

**Pattern 1: Count components**
```java
UnionFind uf = new UnionFind(n);
for (int[] edge : edges) uf.union(edge[0], edge[1]);
return uf.getComponents();
```

**Pattern 2: Cycle detection**
```java
for (int[] edge : edges) {
    if (!uf.union(edge[0], edge[1])) return edge; // cycle found
}
```

**Pattern 3: Kruskal's MST**
```java
edges.sort((a, b) -> a[2] - b[2]); // sort by weight
int cost = 0;
for (int[] edge : edges) {
    if (uf.union(edge[0], edge[1])) cost += edge[2];
}
```

**Pattern 4: Two-pass (equality/inequality)**
```java
// Pass 1: process == constraints
for (...) if (isEqual) uf.union(a, b);
// Pass 2: check != constraints
for (...) if (isNotEqual && uf.connected(a, b)) return false;
```

---

## Practice Roadmap

### Week 1: Foundation (Days 1-5)

**Day 1: Understand the data structure**
- Read this document's Core Concept section carefully.
- Implement the standard UnionFind class from scratch without looking at the template.
- Test it manually with 5 nodes and a few unions.

**Day 2: LC 1971 (Easy) + LC 547 (Medium)**
- LC 1971 is the simplest possible Union-Find problem. Get comfortable with the pattern.
- LC 547 is the canonical "count components" problem. Should take under 10 minutes after LC 1971.

**Day 3: LC 323 (Medium) + LC 200 UF approach (Medium)**
- LC 323 is the pure form of the pattern.
- LC 200 with Union-Find: practice the 2D grid to 1D index conversion.

**Day 4: LC 684 (Medium) — cycle detection**
- This is the most important problem for understanding the `union() returns false = cycle` pattern.
- Trace through the parent array manually as shown in this document.
- Aim to solve it without looking at the solution.

**Day 5: LC 990 (Medium) — two-pass pattern**
- Practice the equality/inequality two-pass approach.
- Understand why the order of passes matters.

---

### Week 2: Advanced (Days 6-10)

**Day 6: LC 721 (Medium) — accounts merge**
- This is the most common "merging groups" problem in FAANG interviews.
- Focus on the email-to-index mapping and the reconstruction step.
- Time yourself: target under 25 minutes.

**Day 7: LC 128 (Medium) — UF variant**
- Compare your Union-Find solution with the HashSet solution from Topic 6.
- Understand when each approach is preferable.

**Day 8: LC 1584 (Medium) — Kruskal's MST**
- Implement Kruskal's from scratch.
- Understand the "n-1 edges = MST complete" early termination.

**Day 9: LC 399 (Medium) — weighted Union-Find**
- This is the hardest problem in this set.
- Start with the BFS approach (Topic 16) to understand the problem.
- Then implement the weighted Union-Find approach.
- Focus on the weight invariant during path compression.

**Day 10: Review and timed practice**
- Re-solve LC 684 and LC 721 from scratch, timed.
- Target: LC 684 in under 10 minutes, LC 721 in under 20 minutes.
- If you can't, identify which step is slow and drill it.

---

### Interview Preparation Notes

**What interviewers test:**
1. Can you recognize when Union-Find is the right tool? (vs BFS/DFS)
2. Can you implement the class correctly from memory?
3. Do you handle edge cases: 1-indexed nodes, duplicate edges, disconnected graphs?
4. Can you explain path compression and union by rank without prompting?

**What to say when you recognize a Union-Find problem:**
> "This looks like a connectivity problem where we're adding edges incrementally. I'll use Union-Find with path compression and union by rank, which gives us near-constant time per operation."

**What to say about complexity:**
> "With path compression and union by rank, both find and union are O(α(n)) amortized, where α is the inverse Ackermann function. For all practical purposes, this is O(1)."

**Red flags that will cost you points:**
- Implementing find() without path compression
- Forgetting to initialize parent[i] = i
- Comparing parent[x] == parent[y] instead of find(x) == find(y)
- Not tracking components count when the problem asks for it

---

### Problem List Summary

| # | Problem | Difficulty | Category | Must-Know |
|---|---|---|---|---|
| LC 1971 | Find if Path Exists | Easy | Connected Components | Yes |
| LC 547 | Number of Provinces | Medium | Connected Components | Yes |
| LC 323 | Number of Connected Components | Medium | Connected Components | Yes |
| LC 200 | Number of Islands (UF) | Medium | Connected Components | Yes |
| LC 684 | Redundant Connection | Medium | Cycle Detection | Yes |
| LC 990 | Satisfiability of Equality Equations | Medium | Cycle/Constraints | Yes |
| LC 721 | Accounts Merge | Medium | Merging Groups | Yes |
| LC 128 | Longest Consecutive Sequence | Medium | Merging Groups | No (HashSet preferred) |
| LC 1584 | Min Cost to Connect All Points | Medium | MST / Kruskal's | Yes |
| LC 399 | Evaluate Division | Medium | Weighted UF | No (BFS preferred) |

**Must-know for FAANG:** LC 547, 684, 721, 1584. These four cover every major Union-Find pattern.

---

*Document 17 of 20. Next: Topic 18 — Dynamic Programming Fundamentals.*
