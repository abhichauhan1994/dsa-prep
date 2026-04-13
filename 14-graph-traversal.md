# Topic 14: Graph Traversal (BFS & DFS)

> **Series position:** Document 14 of 20
> **Difficulty range:** Easy to Hard
> **Interview frequency:** Graphs appear in ~25% of FAANG interviews. Google overweights graphs to ~35%. Grid problems alone account for a significant chunk of Amazon and Meta rounds.
> **Top companies:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5](#eli5)
3. [Core Templates in Java](#core-templates-in-java)
4. [Real-World Applications](#real-world-applications)
5. [Problem Categories and Solutions](#problem-categories-and-solutions)
6. [Common Mistakes](#common-mistakes)
7. [Pattern Comparison](#pattern-comparison)
8. [Quick Reference Cheat Sheet](#quick-reference-cheat-sheet)
9. [Practice Roadmap](#practice-roadmap)

---

## Core Concept

### What Is a Graph?

A graph is a collection of **nodes** (vertices) connected by **edges**. Unlike trees (covered in Topics 10 and 11), graphs can have cycles, disconnected components, and edges pointing in any direction.

```
Key terminology:
- Node / Vertex: a single element in the graph
- Edge: a connection between two nodes
- Directed graph: edges have direction (A -> B does not imply B -> A)
- Undirected graph: edges are bidirectional (A -- B means both can reach each other)
- Weighted graph: edges carry a cost/distance value
- Unweighted graph: all edges are equal (BFS gives shortest path here)
- Cycle: a path that starts and ends at the same node
- Connected component: a maximal set of nodes where every node is reachable from every other
- Degree: number of edges connected to a node
```

The critical difference from trees: **graphs have cycles**. This means you MUST track which nodes you've already visited, or you'll loop forever.

---

### Graph Representation

Three common ways to represent a graph. Know all three; use adjacency list by default.

#### 1. Adjacency List (default for interviews)

```java
// Undirected graph with 5 nodes
Map<Integer, List<Integer>> graph = new HashMap<>();
graph.put(0, Arrays.asList(1, 2));
graph.put(1, Arrays.asList(0, 3));
graph.put(2, Arrays.asList(0, 4));
graph.put(3, Arrays.asList(1));
graph.put(4, Arrays.asList(2));

// Or build from edge list:
int[][] edges = {{0,1},{0,2},{1,3},{2,4}};
Map<Integer, List<Integer>> adj = new HashMap<>();
for (int[] e : edges) {
    adj.computeIfAbsent(e[0], k -> new ArrayList<>()).add(e[1]);
    adj.computeIfAbsent(e[1], k -> new ArrayList<>()).add(e[0]); // omit for directed
}
```

Space: O(V + E). Lookup neighbors: O(degree). Best for sparse graphs (most interview problems).

#### 2. Adjacency Matrix

```java
int n = 5;
int[][] matrix = new int[n][n];
matrix[0][1] = 1;
matrix[1][0] = 1; // undirected
matrix[0][2] = 1;
matrix[2][0] = 1;
// matrix[i][j] == 1 means edge exists between i and j
```

Space: O(V^2). Lookup edge existence: O(1). Best for dense graphs or when you need fast edge queries. Rarely used in interviews unless the problem gives you a matrix directly.

#### 3. Edge List

```java
int[][] edges = {{0,1},{0,2},{1,3},{2,4}};
// edges[i] = {u, v} means there's an edge from u to v
```

Space: O(E). Used in Union-Find problems (Topic 17) and Kruskal's algorithm. Not useful for traversal directly.

---

### BFS: Breadth-First Search

BFS explores nodes level by level, visiting all neighbors of the current node before going deeper. It uses a **queue** (FIFO).

**When to use BFS:**
- Shortest path in an **unweighted** graph (guaranteed optimal)
- Level-by-level processing
- Multi-source spreading (rotten oranges, distance from gates)
- Finding if a path exists (though DFS works too)

**Core mechanics:**
1. Add the start node to the queue and mark it visited
2. While the queue is not empty: poll a node, process it, add all unvisited neighbors to the queue and mark them visited
3. Mark visited **when you add to queue**, not when you poll (prevents duplicates)

```
BFS on this graph starting from node 0:
0 -- 1 -- 3
|         |
2 -- 4 ---+

Queue:  [0]          visited: {0}
Poll 0, add 1,2:
Queue:  [1, 2]       visited: {0,1,2}
Poll 1, add 3:
Queue:  [2, 3]       visited: {0,1,2,3}
Poll 2, add 4:
Queue:  [3, 4]       visited: {0,1,2,3,4}
Poll 3, 4 already visited:
Queue:  [4]
Poll 4, done.

BFS order: 0, 1, 2, 3, 4
Shortest path from 0 to 3: 0 -> 1 -> 3 (distance 2)
```

---

### DFS: Depth-First Search

DFS explores as deep as possible along one path before backtracking. It uses a **stack** (or recursion, which uses the call stack).

**When to use DFS:**
- Detecting cycles
- Finding connected components
- Checking if a path exists
- Topological sort (Topic 15)
- Exploring all possible paths (backtracking, Topic 13)

**Core mechanics:**
1. Visit the start node, mark it visited
2. Recursively visit each unvisited neighbor
3. Backtrack when no unvisited neighbors remain

```
DFS on the same graph starting from node 0:
0 -- 1 -- 3
|         |
2 -- 4 ---+

Visit 0, go to neighbor 1
  Visit 1, go to neighbor 3
    Visit 3, go to neighbor 4
      Visit 4, go to neighbor 2
        Visit 2, go to neighbor 0 (already visited), backtrack
      Backtrack from 4
    Backtrack from 3
  Backtrack from 1
Backtrack from 0

DFS order: 0, 1, 3, 4, 2
```

---

### Grid as Implicit Graph

Many interview problems give you a 2D grid instead of an explicit graph. Each cell `(r, c)` is a node. Its neighbors are the 4 adjacent cells (up, down, left, right). You don't need to build an adjacency list; the grid itself encodes the structure.

```java
// Standard 4-directional movement pattern — memorize this
int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

// Bounds check
boolean inBounds(int r, int c, int rows, int cols) {
    return r >= 0 && r < rows && c >= 0 && c < cols;
}

// Visiting neighbors
for (int[] d : dirs) {
    int nr = r + d[0];
    int nc = c + d[1];
    if (inBounds(nr, nc, rows, cols) && !visited[nr][nc]) {
        // process neighbor
    }
}
```

For 8-directional (diagonals included), add `{1,1},{1,-1},{-1,1},{-1,-1}` to `dirs`.

---

### Multi-Source BFS

Standard BFS starts from a single source. Multi-source BFS starts from **multiple sources simultaneously** by adding all of them to the queue before the main loop begins. This is equivalent to adding a virtual "super-source" node connected to all real sources.

**When to use:** Any problem asking for minimum distance from ANY of several sources (rotten oranges, distance from nearest gate, nearest 0 in a matrix).

```java
// Multi-source BFS skeleton
Queue<int[]> queue = new LinkedList<>();
boolean[][] visited = new boolean[rows][cols];

// Add ALL sources first
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {
        if (isSource(grid[r][c])) {
            queue.offer(new int[]{r, c});
            visited[r][c] = true;
        }
    }
}

// Then run standard BFS
int steps = 0;
while (!queue.isEmpty()) {
    int size = queue.size();
    for (int i = 0; i < size; i++) {
        int[] curr = queue.poll();
        for (int[] d : dirs) {
            int nr = curr[0] + d[0];
            int nc = curr[1] + d[1];
            if (inBounds(nr, nc) && !visited[nr][nc]) {
                visited[nr][nc] = true;
                queue.offer(new int[]{nr, nc});
            }
        }
    }
    steps++;
}
```

---

### Cycle Detection

**Undirected graphs:** Track the parent node during DFS. If you reach a visited node that isn't your parent, there's a cycle. Alternatively, use Union-Find (Topic 17).

**Directed graphs:** Use 3-color DFS (WHITE/GRAY/BLACK):
- WHITE (0): not visited
- GRAY (1): currently in the DFS call stack (being processed)
- BLACK (2): fully processed

If you reach a GRAY node during DFS, you've found a back edge, which means a cycle.

```java
int[] color = new int[n]; // 0=WHITE, 1=GRAY, 2=BLACK

boolean hasCycle(int node, Map<Integer, List<Integer>> graph) {
    color[node] = 1; // GRAY
    for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
        if (color[neighbor] == 1) return true;  // back edge = cycle
        if (color[neighbor] == 0 && hasCycle(neighbor, graph)) return true;
    }
    color[node] = 2; // BLACK
    return false;
}
```

This is the foundation of Course Schedule (LC 207), covered in Topic 15.

---

## ELI5

A graph is a map of cities and roads. BFS explores all neighboring cities first, like a ripple spreading outward from a stone dropped in water. Every city at distance 1 gets visited before any city at distance 2. That's why BFS finds the shortest path.

DFS goes down one road as far as possible before coming back. It's like exploring a maze by always turning left until you hit a dead end, then backtracking to the last junction.

The big difference from trees (Topics 10 and 11): roads can form loops. If you don't remember which cities you've already visited, you'll drive in circles forever. The `visited` set is your GPS memory.

---

## Core Templates in Java

### Template 1: BFS on Adjacency List

Finds shortest path (in hops) from a source node. Works for any unweighted graph.

```java
// BFS: shortest path from src to dst in an unweighted graph
// Returns -1 if no path exists
public int bfs(Map<Integer, List<Integer>> graph, int src, int dst, int n) {
    if (src == dst) return 0;

    boolean[] visited = new boolean[n];
    Queue<Integer> queue = new LinkedList<>();

    visited[src] = true;
    queue.offer(src);
    int distance = 0;

    while (!queue.isEmpty()) {
        int size = queue.size(); // snapshot current level
        distance++;

        for (int i = 0; i < size; i++) {
            int node = queue.poll();

            for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                if (neighbor == dst) return distance;
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                }
            }
        }
    }

    return -1; // no path found
}
```

**Key points:**
- Mark visited when you **add to queue**, not when you poll. If you mark on poll, the same node can be added multiple times before it's processed.
- The `size` snapshot separates levels. Without it, you can't track distance.
- `graph.getOrDefault(node, new ArrayList<>())` handles nodes with no outgoing edges.

---

### Template 2: DFS on Adjacency List (Recursive + Iterative)

#### Recursive DFS

```java
// DFS: check if dst is reachable from src
public boolean dfsRecursive(Map<Integer, List<Integer>> graph,
                             int src, int dst, boolean[] visited) {
    if (src == dst) return true;
    visited[src] = true;

    for (int neighbor : graph.getOrDefault(src, new ArrayList<>())) {
        if (!visited[neighbor]) {
            if (dfsRecursive(graph, neighbor, dst, visited)) return true;
        }
    }

    return false;
}

// Caller:
boolean[] visited = new boolean[n];
boolean reachable = dfsRecursive(graph, src, dst, visited);
```

#### Iterative DFS (explicit stack)

```java
// DFS: iterative version using explicit stack
public boolean dfsIterative(Map<Integer, List<Integer>> graph,
                              int src, int dst, int n) {
    boolean[] visited = new boolean[n];
    Deque<Integer> stack = new ArrayDeque<>();

    visited[src] = true;
    stack.push(src);

    while (!stack.isEmpty()) {
        int node = stack.pop();
        if (node == dst) return true;

        for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
            if (!visited[neighbor]) {
                visited[neighbor] = true;
                stack.push(neighbor);
            }
        }
    }

    return false;
}
```

**Recursive vs iterative:** Recursive is cleaner and easier to write under pressure. Use iterative if the graph is very deep and you're worried about stack overflow (rare in interviews, but good to know).

#### DFS for Connected Components

```java
// Count connected components in an undirected graph
public int countComponents(int n, int[][] edges) {
    Map<Integer, List<Integer>> graph = new HashMap<>();
    for (int i = 0; i < n; i++) graph.put(i, new ArrayList<>());
    for (int[] e : edges) {
        graph.get(e[0]).add(e[1]);
        graph.get(e[1]).add(e[0]);
    }

    boolean[] visited = new boolean[n];
    int components = 0;

    for (int i = 0; i < n; i++) {
        if (!visited[i]) {
            dfs(graph, i, visited);
            components++;
        }
    }

    return components;
}

private void dfs(Map<Integer, List<Integer>> graph, int node, boolean[] visited) {
    visited[node] = true;
    for (int neighbor : graph.get(node)) {
        if (!visited[neighbor]) dfs(graph, neighbor, visited);
    }
}
```

The outer loop handles **disconnected graphs**. Without it, you'd only explore the component containing node 0.

---

### Template 3: Grid BFS

BFS on a 2D grid. Finds shortest path or minimum steps.

```java
// Grid BFS: find shortest path from (sr, sc) to (er, ec)
// Returns -1 if no path exists
public int gridBFS(int[][] grid, int sr, int sc, int er, int ec) {
    int rows = grid.length, cols = grid[0].length;
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

    boolean[][] visited = new boolean[rows][cols];
    Queue<int[]> queue = new LinkedList<>();

    visited[sr][sc] = true;
    queue.offer(new int[]{sr, sc});
    int steps = 0;

    while (!queue.isEmpty()) {
        int size = queue.size();

        for (int i = 0; i < size; i++) {
            int[] curr = queue.poll();
            int r = curr[0], c = curr[1];

            if (r == er && c == ec) return steps;

            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !visited[nr][nc] && grid[nr][nc] != WALL) {
                    visited[nr][nc] = true;
                    queue.offer(new int[]{nr, nc});
                }
            }
        }

        steps++;
    }

    return -1;
}
```

**Common variation:** Instead of a separate `visited` array, some solutions modify the grid in-place (e.g., set `grid[r][c] = 0` after visiting). This saves space but mutates input. Prefer a separate `visited` array unless the problem explicitly allows mutation.

---

### Template 4: Grid DFS

DFS on a 2D grid. Used for flood fill, island area, connected component exploration.

```java
// Grid DFS: explore connected region starting from (r, c)
// Returns the size of the connected component
int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

public int gridDFS(int[][] grid, int r, int c, boolean[][] visited) {
    int rows = grid.length, cols = grid[0].length;

    // Base cases: out of bounds, already visited, or not part of component
    if (r < 0 || r >= rows || c < 0 || c >= cols) return 0;
    if (visited[r][c]) return 0;
    if (grid[r][c] == 0) return 0; // 0 = water/wall, 1 = land

    visited[r][c] = true;
    int area = 1;

    for (int[] d : dirs) {
        area += gridDFS(grid, r + d[0], c + d[1], visited);
    }

    return area;
}

// Count islands (connected components of 1s)
public int countIslands(int[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    boolean[][] visited = new boolean[rows][cols];
    int islands = 0;

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == 1 && !visited[r][c]) {
                gridDFS(grid, r, c, visited);
                islands++;
            }
        }
    }

    return islands;
}
```

---

### Template 5: Multi-Source BFS

All sources enter the queue simultaneously. The BFS then spreads outward from all of them at once.

```java
// Multi-source BFS: compute minimum distance from any source cell
// Sources are cells where grid[r][c] == SOURCE_VALUE
public int[][] multiSourceBFS(int[][] grid, int sourceValue) {
    int rows = grid.length, cols = grid[0].length;
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

    int[][] dist = new int[rows][cols];
    for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);

    Queue<int[]> queue = new LinkedList<>();

    // Step 1: seed all sources
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == sourceValue) {
                dist[r][c] = 0;
                queue.offer(new int[]{r, c});
            }
        }
    }

    // Step 2: standard BFS from all sources simultaneously
    while (!queue.isEmpty()) {
        int[] curr = queue.poll();
        int r = curr[0], c = curr[1];

        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                    && dist[nr][nc] == Integer.MAX_VALUE) {
                dist[nr][nc] = dist[r][c] + 1;
                queue.offer(new int[]{nr, nc});
            }
        }
    }

    return dist;
}
```

**Why this works:** Because all sources start at distance 0 simultaneously, the BFS naturally computes the minimum distance from the nearest source to every other cell. No need to run BFS from each source separately and take the minimum.

---

## Real-World Applications

**Social networks (BFS):** Friend recommendations on LinkedIn or Facebook use BFS. Your 2nd-degree connections are the neighbors of your neighbors. BFS finds them in one level expansion.

**Google Maps routing (BFS/Dijkstra):** For unweighted road networks (all roads equal), BFS finds the fewest-turns route. For weighted networks (different travel times), Dijkstra's algorithm (Topic 16) takes over. The graph structure is the same; only the algorithm changes.

**Network reachability:** Can server A communicate with server B given the current firewall rules? Model servers as nodes, allowed connections as directed edges, run DFS/BFS from A.

**Garbage collection (DFS):** The JVM's mark phase in mark-and-sweep GC is a DFS from all GC roots (stack variables, static fields). Any object not reached is unreachable and can be collected. This is literally connected component detection.

**Web crawling:** Google's crawler starts from seed URLs (multi-source BFS), follows links (edges), and marks visited pages (visited set). BFS ensures pages close to seeds are crawled first.

**Dependency resolution (DFS + topological sort):** When `npm install` resolves package dependencies, it builds a directed graph and runs DFS to find a valid installation order. Cycles mean circular dependencies (an error). This is Topic 15.

**Flood fill (DFS/BFS):** The paint bucket tool in image editors is literally grid DFS/BFS. Click a pixel, find all connected pixels of the same color, recolor them.

---

## Problem Categories and Solutions

---

### Category A: Grid BFS/DFS

---

#### LC 200 — Number of Islands

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs
**Difficulty:** Medium
**Pattern:** Grid DFS/BFS, connected components

**Problem:** Given a 2D grid of `'1'` (land) and `'0'` (water), count the number of islands. An island is surrounded by water and formed by connecting adjacent lands horizontally or vertically.

**Approach:** Iterate every cell. When you find an unvisited `'1'`, increment the island count and run DFS/BFS to mark the entire island as visited.

**Dry Run (Grid Walk-Through):**

```
Input grid:
  0   1   2   3   4
0 [1] [1] [1] [0] [0]
1 [1] [1] [0] [0] [0]
2 [0] [0] [0] [1] [1]
3 [0] [0] [0] [1] [1]

Step 1: r=0, c=0 → grid[0][0]='1', not visited → islands=1
  DFS from (0,0):
    Visit (0,0), mark visited
    Neighbors: right=(0,1)='1', down=(1,0)='1'
    DFS from (0,1):
      Visit (0,1), mark visited
      Neighbors: right=(0,2)='1', down=(1,1)='1', left=(0,0)=visited
      DFS from (0,2):
        Visit (0,2), mark visited
        Neighbors: right=(0,3)='0', down=(1,2)='0', left=(0,1)=visited
        No more unvisited land neighbors. Return.
      DFS from (1,1):
        Visit (1,1), mark visited
        Neighbors: right=(1,2)='0', down=(2,1)='0', left=(1,0)='1', up=(0,1)=visited
        DFS from (1,0):
          Visit (1,0), mark visited
          Neighbors: right=(1,1)=visited, down=(2,0)='0', up=(0,0)=visited
          No more. Return.
        Return.
      Return.
    Return.
  Island 1 fully explored: cells (0,0),(0,1),(0,2),(1,0),(1,1)

Step 2: r=0, c=1 → visited, skip
Step 3: r=0, c=2 → visited, skip
Step 4: r=0, c=3 → '0', skip
...continue scanning...
Step 5: r=2, c=3 → grid[2][3]='1', not visited → islands=2
  DFS from (2,3):
    Visit (2,3), mark visited
    Neighbors: right=(2,4)='1', down=(3,3)='1'
    DFS from (2,4): visit, neighbors (3,4)='1'
      DFS from (3,4): visit, neighbors (3,3)='1'
        DFS from (3,3): visit, all neighbors visited or water. Return.
      Return.
    Return.
  Island 2 fully explored: cells (2,3),(2,4),(3,3),(3,4)

Final answer: 2
```

**Solution:**

```java
class Solution {
    private int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

    public int numIslands(char[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int islands = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '1' && !visited[r][c]) {
                    dfs(grid, r, c, visited, rows, cols);
                    islands++;
                }
            }
        }

        return islands;
    }

    private void dfs(char[][] grid, int r, int c,
                     boolean[][] visited, int rows, int cols) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return;
        if (visited[r][c] || grid[r][c] == '0') return;

        visited[r][c] = true;

        for (int[] d : dirs) {
            dfs(grid, r + d[0], c + d[1], visited, rows, cols);
        }
    }
}
```

**Complexity:** Time O(m * n), Space O(m * n) for visited array + recursion stack.

**BFS variant** (same complexity, avoids deep recursion):

```java
private void bfs(char[][] grid, int sr, int sc,
                 boolean[][] visited, int rows, int cols) {
    Queue<int[]> queue = new LinkedList<>();
    visited[sr][sc] = true;
    queue.offer(new int[]{sr, sc});

    while (!queue.isEmpty()) {
        int[] curr = queue.poll();
        for (int[] d : dirs) {
            int nr = curr[0] + d[0];
            int nc = curr[1] + d[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                    && !visited[nr][nc] && grid[nr][nc] == '1') {
                visited[nr][nc] = true;
                queue.offer(new int[]{nr, nc});
            }
        }
    }
}
```

---

#### LC 695 — Max Area of Island

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** Grid DFS, return value accumulation

**Problem:** Find the maximum area of an island in a binary grid (1=land, 0=water).

**Key insight:** Same structure as LC 200, but DFS returns the area of each island instead of just marking it visited.

```java
class Solution {
    private int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

    public int maxAreaOfIsland(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int maxArea = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1 && !visited[r][c]) {
                    int area = dfs(grid, r, c, visited, rows, cols);
                    maxArea = Math.max(maxArea, area);
                }
            }
        }

        return maxArea;
    }

    private int dfs(int[][] grid, int r, int c,
                    boolean[][] visited, int rows, int cols) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return 0;
        if (visited[r][c] || grid[r][c] == 0) return 0;

        visited[r][c] = true;
        int area = 1;

        for (int[] d : dirs) {
            area += dfs(grid, r + d[0], c + d[1], visited, rows, cols);
        }

        return area;
    }
}
```

**Complexity:** Time O(m * n), Space O(m * n).

---

#### LC 733 — Flood Fill

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Easy
**Pattern:** Grid DFS, color replacement

**Problem:** Given an image (2D array), a starting pixel `(sr, sc)`, and a new color, perform a flood fill. Change the color of the starting pixel and all connected pixels of the same original color.

**Edge case:** If the starting pixel already has the new color, return immediately (otherwise you'll loop).

```java
class Solution {
    private int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int originalColor = image[sr][sc];
        if (originalColor == color) return image; // already the target color

        dfs(image, sr, sc, originalColor, color);
        return image;
    }

    private void dfs(int[][] image, int r, int c, int original, int newColor) {
        if (r < 0 || r >= image.length || c < 0 || c >= image[0].length) return;
        if (image[r][c] != original) return; // different color or already filled

        image[r][c] = newColor; // fill in-place (no separate visited needed)

        for (int[] d : dirs) {
            dfs(image, r + d[0], c + d[1], original, newColor);
        }
    }
}
```

**Note:** Here we modify the grid in-place instead of using a `visited` array. This works because changing the color to `newColor` prevents revisiting (the check `image[r][c] != original` catches it). This is acceptable when the problem says you can modify the input.

**Complexity:** Time O(m * n), Space O(m * n) recursion stack.

---

#### LC 130 — Surrounded Regions

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** Grid DFS from borders, inverse marking

**Problem:** Given a board of `'X'` and `'O'`, capture all regions of `'O'` that are completely surrounded by `'X'`. A region is NOT captured if any `'O'` in it is on the border.

**Key insight:** Instead of finding surrounded regions directly, find the SAFE regions (connected to the border) and mark everything else.

**Approach:**
1. DFS from every `'O'` on the border, mark all connected `'O'`s as `'S'` (safe)
2. Scan the entire board: `'O'` becomes `'X'` (captured), `'S'` becomes `'O'` (restored)

```java
class Solution {
    private int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    private int rows, cols;

    public void solve(char[][] board) {
        rows = board.length;
        cols = board[0].length;

        // Mark all border-connected 'O's as safe
        for (int r = 0; r < rows; r++) {
            if (board[r][0] == 'O') dfs(board, r, 0);
            if (board[r][cols-1] == 'O') dfs(board, r, cols-1);
        }
        for (int c = 0; c < cols; c++) {
            if (board[0][c] == 'O') dfs(board, 0, c);
            if (board[rows-1][c] == 'O') dfs(board, rows-1, c);
        }

        // Flip: 'O' -> 'X' (captured), 'S' -> 'O' (safe, restore)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == 'O') board[r][c] = 'X';
                else if (board[r][c] == 'S') board[r][c] = 'O';
            }
        }
    }

    private void dfs(char[][] board, int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return;
        if (board[r][c] != 'O') return;

        board[r][c] = 'S'; // mark as safe

        for (int[] d : dirs) {
            dfs(board, r + d[0], c + d[1]);
        }
    }
}
```

**Complexity:** Time O(m * n), Space O(m * n).

---

#### LC 417 — Pacific Atlantic Water Flow

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium
**Pattern:** Reverse multi-source DFS/BFS from both oceans

**Problem:** Given a grid of heights, water flows from a cell to adjacent cells with equal or lower height. Find all cells from which water can flow to BOTH the Pacific (top/left border) and Atlantic (bottom/right border) oceans.

**Key insight:** Instead of simulating water flowing down from each cell (expensive), reverse the direction: flow UP from the ocean borders. A cell can reach the Pacific if it's reachable from the Pacific border going uphill (height >= current).

```java
class Solution {
    private int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    private int rows, cols;

    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        rows = heights.length;
        cols = heights[0].length;

        boolean[][] pacific = new boolean[rows][cols];
        boolean[][] atlantic = new boolean[rows][cols];

        // BFS from Pacific borders (top row + left col)
        Queue<int[]> pacQueue = new LinkedList<>();
        Queue<int[]> atlQueue = new LinkedList<>();

        for (int r = 0; r < rows; r++) {
            pacific[r][0] = true;
            pacQueue.offer(new int[]{r, 0});
            atlantic[r][cols-1] = true;
            atlQueue.offer(new int[]{r, cols-1});
        }
        for (int c = 0; c < cols; c++) {
            pacific[0][c] = true;
            pacQueue.offer(new int[]{0, c});
            atlantic[rows-1][c] = true;
            atlQueue.offer(new int[]{rows-1, c});
        }

        bfs(heights, pacQueue, pacific);
        bfs(heights, atlQueue, atlantic);

        List<List<Integer>> result = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (pacific[r][c] && atlantic[r][c]) {
                    result.add(Arrays.asList(r, c));
                }
            }
        }

        return result;
    }

    private void bfs(int[][] heights, Queue<int[]> queue, boolean[][] visited) {
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0], c = curr[1];

            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !visited[nr][nc]
                        && heights[nr][nc] >= heights[r][c]) { // uphill flow
                    visited[nr][nc] = true;
                    queue.offer(new int[]{nr, nc});
                }
            }
        }
    }
}
```

**Complexity:** Time O(m * n), Space O(m * n).

---

### Category B: Multi-Source BFS

---

#### LC 994 — Rotting Oranges

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty:** Medium
**Pattern:** Multi-source BFS, time simulation

**Problem:** A grid contains `0` (empty), `1` (fresh orange), `2` (rotten orange). Every minute, a rotten orange infects all adjacent fresh oranges. Return the minimum minutes until no fresh oranges remain, or `-1` if impossible.

**Approach:** Multi-source BFS. All rotten oranges start at time 0. BFS spreads infection level by level, where each level = 1 minute.

**Multi-Source BFS Trace:**

```
Input:
  0   1   2
0 [2] [1] [1]
1 [1] [1] [0]
2 [0] [1] [1]

Initial state:
  Rotten: (0,0)
  Fresh: (0,1),(0,2),(1,0),(1,1),(2,1),(2,2)
  Queue: [(0,0)]  time=0

Minute 1 (process level with time=0):
  Poll (0,0), infect neighbors:
    (0,1): fresh → rotten, add to queue
    (1,0): fresh → rotten, add to queue
  Queue after: [(0,1,t=1),(1,0,t=1)]
  Fresh remaining: (0,2),(1,1),(2,1),(2,2)

Minute 2 (process level with time=1):
  Poll (0,1), infect neighbors:
    (0,0): already rotten, skip
    (0,2): fresh → rotten, add to queue
    (1,1): fresh → rotten, add to queue
  Poll (1,0), infect neighbors:
    (0,0): already rotten, skip
    (1,1): already queued, skip
    (2,0): empty (0), skip
  Queue after: [(0,2,t=2),(1,1,t=2)]
  Fresh remaining: (2,1),(2,2)

Minute 3 (process level with time=2):
  Poll (0,2), infect neighbors:
    (0,1): already rotten, skip
    (1,2): empty (0), skip
  Poll (1,1), infect neighbors:
    (0,1): already rotten, skip
    (1,0): already rotten, skip
    (2,1): fresh → rotten, add to queue
  Queue after: [(2,1,t=3)]
  Fresh remaining: (2,2)

Minute 4 (process level with time=3):
  Poll (2,1), infect neighbors:
    (1,1): already rotten, skip
    (2,0): empty (0), skip
    (2,2): fresh → rotten, add to queue
  Queue after: [(2,2,t=4)]
  Fresh remaining: none

Minute 5 (process level with time=4):
  Poll (2,2), no fresh neighbors.
  Queue empty.

All fresh oranges rotted. Answer: 4
```

**Solution:**

```java
class Solution {
    public int orangesRotting(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

        Queue<int[]> queue = new LinkedList<>();
        int fresh = 0;

        // Seed all rotten oranges, count fresh
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) {
                    queue.offer(new int[]{r, c});
                } else if (grid[r][c] == 1) {
                    fresh++;
                }
            }
        }

        if (fresh == 0) return 0; // no fresh oranges to rot

        int minutes = 0;

        while (!queue.isEmpty() && fresh > 0) {
            int size = queue.size();
            minutes++;

            for (int i = 0; i < size; i++) {
                int[] curr = queue.poll();
                for (int[] d : dirs) {
                    int nr = curr[0] + d[0];
                    int nc = curr[1] + d[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                            && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2; // mark rotten (in-place visited)
                        fresh--;
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }
        }

        return fresh == 0 ? minutes : -1;
    }
}
```

**Complexity:** Time O(m * n), Space O(m * n).

**Why multi-source?** If you ran BFS from each rotten orange separately and took the max, you'd get the wrong answer. The infection spreads simultaneously from all rotten oranges. Multi-source BFS models this correctly.

---

#### LC 542 — 01 Matrix

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium
**Pattern:** Multi-source BFS from all 0s

**Problem:** Given a binary matrix, return a matrix where each cell contains the distance to the nearest `0`.

**Key insight:** Instead of BFS from each `1` to find the nearest `0` (expensive), run multi-source BFS from ALL `0`s simultaneously. Each `1` gets its distance when the BFS wave first reaches it.

```java
class Solution {
    public int[][] updateMatrix(int[][] mat) {
        int rows = mat.length, cols = mat[0].length;
        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
        int[][] dist = new int[rows][cols];
        Queue<int[]> queue = new LinkedList<>();

        // Seed all 0s, mark 1s as unvisited (MAX_VALUE)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (mat[r][c] == 0) {
                    dist[r][c] = 0;
                    queue.offer(new int[]{r, c});
                } else {
                    dist[r][c] = Integer.MAX_VALUE;
                }
            }
        }

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0], c = curr[1];

            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && dist[nr][nc] == Integer.MAX_VALUE) {
                    dist[nr][nc] = dist[r][c] + 1;
                    queue.offer(new int[]{nr, nc});
                }
            }
        }

        return dist;
    }
}
```

**Complexity:** Time O(m * n), Space O(m * n).

---

#### LC 286 — Walls and Gates (Premium)

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium
**Pattern:** Multi-source BFS from all gates

**Problem:** Fill each empty room with the distance to its nearest gate. `INF` = empty room, `-1` = wall, `0` = gate.

**Approach:** Multi-source BFS from all gates (cells with value `0`). BFS naturally fills each room with the minimum distance.

```java
class Solution {
    public void wallsAndGates(int[][] rooms) {
        int rows = rooms.length, cols = rooms[0].length;
        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
        int INF = Integer.MAX_VALUE;

        Queue<int[]> queue = new LinkedList<>();

        // Seed all gates
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (rooms[r][c] == 0) {
                    queue.offer(new int[]{r, c});
                }
            }
        }

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0], c = curr[1];

            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && rooms[nr][nc] == INF) {
                    rooms[nr][nc] = rooms[r][c] + 1;
                    queue.offer(new int[]{nr, nc});
                }
            }
        }
    }
}
```

**Complexity:** Time O(m * n), Space O(m * n).

---

### Category C: Graph BFS

---

#### LC 127 — Word Ladder

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty:** Hard
**Pattern:** BFS on implicit graph (words as nodes, one-letter-diff as edges)

**Problem:** Given a `beginWord`, `endWord`, and a `wordList`, find the length of the shortest transformation sequence from `beginWord` to `endWord`, where each step changes exactly one letter and each intermediate word must be in `wordList`.

**Key insight:** Model as a graph where each word is a node. Two words are connected if they differ by exactly one letter. BFS finds the shortest path (fewest transformations).

**Optimization:** Instead of comparing every pair of words (O(n^2 * L)), for each word generate all possible one-letter variations and check if they're in the word set.

```java
class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(beginWord);
        visited.add(beginWord);
        int steps = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                String word = queue.poll();

                // Try changing each character
                char[] chars = word.toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    char original = chars[j];

                    for (char ch = 'a'; ch <= 'z'; ch++) {
                        if (ch == original) continue;
                        chars[j] = ch;
                        String next = new String(chars);

                        if (next.equals(endWord)) return steps + 1;

                        if (wordSet.contains(next) && !visited.contains(next)) {
                            visited.add(next);
                            queue.offer(next);
                        }
                    }

                    chars[j] = original; // restore
                }
            }

            steps++;
        }

        return 0; // no path found
    }
}
```

**Complexity:** Time O(M^2 * N) where M = word length, N = number of words. Space O(M * N).

**Why BFS and not DFS?** BFS guarantees the shortest path. DFS might find A path but not the shortest one.

---

#### LC 133 — Clone Graph

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** BFS/DFS with HashMap to track cloned nodes

**Problem:** Given a reference to a node in a connected undirected graph, return a deep copy of the graph.

**Key insight:** Use a `HashMap<Node, Node>` mapping original nodes to their clones. This serves as both the visited set and the clone registry.

```java
class Solution {
    public Node cloneGraph(Node node) {
        if (node == null) return null;

        Map<Node, Node> cloneMap = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();

        cloneMap.put(node, new Node(node.val));
        queue.offer(node);

        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            Node currClone = cloneMap.get(curr);

            for (Node neighbor : curr.neighbors) {
                if (!cloneMap.containsKey(neighbor)) {
                    cloneMap.put(neighbor, new Node(neighbor.val));
                    queue.offer(neighbor);
                }
                currClone.neighbors.add(cloneMap.get(neighbor));
            }
        }

        return cloneMap.get(node);
    }
}
```

**Complexity:** Time O(V + E), Space O(V).

---

#### LC 841 — Keys and Rooms

**Companies:** Amazon, Google, Microsoft
**Difficulty:** Medium
**Pattern:** DFS/BFS reachability

**Problem:** There are `n` rooms numbered `0` to `n-1`. Room `0` is unlocked. Each room contains keys to other rooms. Can you visit all rooms?

**Approach:** Model as a directed graph. Room `i` has edges to all rooms whose keys it contains. DFS/BFS from room 0, check if all rooms are visited.

```java
class Solution {
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        int n = rooms.size();
        boolean[] visited = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();

        visited[0] = true;
        stack.push(0);

        while (!stack.isEmpty()) {
            int room = stack.pop();
            for (int key : rooms.get(room)) {
                if (!visited[key]) {
                    visited[key] = true;
                    stack.push(key);
                }
            }
        }

        for (boolean v : visited) {
            if (!v) return false;
        }

        return true;
    }
}
```

**Complexity:** Time O(V + E), Space O(V).

---

### Category D: Connected Components

---

#### LC 547 — Number of Provinces

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** Connected components in adjacency matrix

**Problem:** Given an `n x n` adjacency matrix `isConnected` where `isConnected[i][j] = 1` means cities `i` and `j` are directly connected, find the number of provinces (connected components).

```java
class Solution {
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        boolean[] visited = new boolean[n];
        int provinces = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(isConnected, i, visited, n);
                provinces++;
            }
        }

        return provinces;
    }

    private void dfs(int[][] isConnected, int city, boolean[] visited, int n) {
        visited[city] = true;
        for (int j = 0; j < n; j++) {
            if (isConnected[city][j] == 1 && !visited[j]) {
                dfs(isConnected, j, visited, n);
            }
        }
    }
}
```

**Complexity:** Time O(n^2), Space O(n).

**Note:** This problem can also be solved with Union-Find (Topic 17), which is often cleaner for pure connected-component counting.

---

#### LC 323 — Number of Connected Components (Premium)

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Medium
**Pattern:** Connected components in edge list

**Problem:** Given `n` nodes and a list of undirected edges, count the number of connected components.

```java
class Solution {
    public int countComponents(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        for (int[] e : edges) {
            graph.get(e[0]).add(e[1]);
            graph.get(e[1]).add(e[0]);
        }

        boolean[] visited = new boolean[n];
        int components = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(graph, i, visited);
                components++;
            }
        }

        return components;
    }

    private void dfs(List<List<Integer>> graph, int node, boolean[] visited) {
        visited[node] = true;
        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) dfs(graph, neighbor, visited);
        }
    }
}
```

**Complexity:** Time O(V + E), Space O(V + E).

---

#### LC 1466 — Reorder Routes to Make All Paths Lead to the City Zero

**Companies:** Amazon, Google, Microsoft
**Difficulty:** Medium
**Pattern:** DFS on directed graph, count edges to reverse

**Problem:** There are `n` cities connected by `n-1` roads forming a tree. Roads are directed. Find the minimum number of edges to reverse so that every city can reach city 0.

**Key insight:** Build an undirected version of the graph but tag each edge with its original direction. DFS from city 0. For each edge you traverse in the ORIGINAL direction (away from 0), you need to reverse it.

```java
class Solution {
    public int minReorder(int n, int[][] connections) {
        // Build adjacency list: {neighbor, cost}
        // cost=1 means original direction (needs reversal if traversed away from 0)
        // cost=0 means reverse direction (already points toward 0)
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());

        for (int[] c : connections) {
            graph.get(c[0]).add(new int[]{c[1], 1}); // original direction
            graph.get(c[1]).add(new int[]{c[0], 0}); // reverse direction
        }

        boolean[] visited = new boolean[n];
        int[] count = {0};
        dfs(graph, 0, visited, count);
        return count[0];
    }

    private void dfs(List<List<int[]>> graph, int node,
                     boolean[] visited, int[] count) {
        visited[node] = true;
        for (int[] edge : graph.get(node)) {
            int neighbor = edge[0], cost = edge[1];
            if (!visited[neighbor]) {
                count[0] += cost; // cost=1 means we need to reverse this edge
                dfs(graph, neighbor, visited, count);
            }
        }
    }
}
```

**Complexity:** Time O(V + E), Space O(V + E).

---

### Category E: Cycle Detection / Safety

---

#### LC 802 — Find Eventual Safe States

**Companies:** Amazon, Google, Meta
**Difficulty:** Medium
**Pattern:** DFS with 3-color cycle detection

**Problem:** A node is "safe" if every path starting from it eventually leads to a terminal node (no outgoing edges). Find all safe nodes.

**Key insight:** A node is unsafe if it's part of a cycle or leads to a cycle. Use 3-color DFS: UNVISITED (0), VISITING (1), SAFE (2). If during DFS you reach a VISITING node, there's a cycle.

```java
class Solution {
    public List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        int[] state = new int[n]; // 0=unvisited, 1=visiting, 2=safe
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (isSafe(graph, i, state)) result.add(i);
        }

        return result;
    }

    private boolean isSafe(int[][] graph, int node, int[] state) {
        if (state[node] == 1) return false; // currently visiting = cycle
        if (state[node] == 2) return true;  // already confirmed safe

        state[node] = 1; // mark as visiting

        for (int neighbor : graph[node]) {
            if (!isSafe(graph, neighbor, state)) {
                return false; // cycle found downstream
            }
        }

        state[node] = 2; // mark as safe
        return true;
    }
}
```

**Complexity:** Time O(V + E), Space O(V).

---

#### LC 207 — Course Schedule (Preview of Topic 15)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple
**Difficulty:** Medium
**Pattern:** Cycle detection in directed graph (topological sort prerequisite)

**Problem:** There are `numCourses` courses. `prerequisites[i] = [a, b]` means you must take course `b` before course `a`. Can you finish all courses?

**Key insight:** This is equivalent to asking: does the directed graph of prerequisites contain a cycle? If yes, it's impossible to finish all courses.

**This problem is covered in depth in Topic 15 (Topological Sort).** The solution here uses the same 3-color DFS from LC 802.

```java
class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
        for (int[] p : prerequisites) graph.get(p[1]).add(p[0]);

        int[] state = new int[numCourses]; // 0=unvisited, 1=visiting, 2=done

        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0 && hasCycle(graph, i, state)) return false;
        }

        return true;
    }

    private boolean hasCycle(List<List<Integer>> graph, int node, int[] state) {
        state[node] = 1; // visiting

        for (int neighbor : graph.get(node)) {
            if (state[neighbor] == 1) return true;  // back edge = cycle
            if (state[neighbor] == 0 && hasCycle(graph, neighbor, state)) return true;
        }

        state[node] = 2; // done
        return false;
    }
}
```

**Complexity:** Time O(V + E), Space O(V + E).

**Cross-reference:** Topic 15 covers Kahn's algorithm (BFS-based topological sort) and the full course schedule variants (LC 210, LC 630).

---

## Common Mistakes

### 1. Forgetting the visited set

The most common mistake. Without tracking visited nodes, BFS/DFS will loop forever on any graph with a cycle.

```java
// WRONG: no visited tracking
void dfs(Map<Integer, List<Integer>> graph, int node) {
    for (int neighbor : graph.get(node)) {
        dfs(graph, neighbor); // infinite loop if there's a cycle!
    }
}

// CORRECT:
void dfs(Map<Integer, List<Integer>> graph, int node, boolean[] visited) {
    visited[node] = true;
    for (int neighbor : graph.get(node)) {
        if (!visited[neighbor]) dfs(graph, neighbor, visited);
    }
}
```

Trees don't need a visited set because they're acyclic. The moment you move to general graphs, you need it.

### 2. Using BFS on a weighted graph

BFS finds the shortest path only in **unweighted** graphs (or graphs where all edges have equal weight). If edges have different weights, BFS gives wrong answers. Use Dijkstra's algorithm (Topic 16).

```
Graph: A --(1)--> B --(1)--> C
       A --(10)--> C

BFS from A: shortest path to C = A->B->C (2 hops)
But weighted shortest path = A->C (cost 10) vs A->B->C (cost 2)
In this case BFS is accidentally correct, but:

Graph: A --(1)--> B --(1)--> C
       A --(1.5)--> C

BFS says A->C is 1 hop (shorter). Weighted says A->B->C costs 2, A->C costs 1.5.
BFS would return 1 hop but the actual shortest weighted path is A->C.
These are different questions. BFS answers "fewest edges", Dijkstra answers "minimum cost".
```

### 3. Marking visited on poll instead of on offer

```java
// WRONG: mark visited when you poll
while (!queue.isEmpty()) {
    int node = queue.poll();
    visited[node] = true; // too late! node may have been added multiple times
    for (int neighbor : ...) {
        if (!visited[neighbor]) queue.offer(neighbor);
    }
}

// CORRECT: mark visited when you add to queue
visited[src] = true;
queue.offer(src);
while (!queue.isEmpty()) {
    int node = queue.poll();
    for (int neighbor : ...) {
        if (!visited[neighbor]) {
            visited[neighbor] = true; // mark here
            queue.offer(neighbor);
        }
    }
}
```

If you mark on poll, the same node can be added to the queue multiple times before it's processed, causing redundant work and potentially wrong distance calculations.

### 4. Not handling disconnected graphs

If the graph has multiple connected components, a single DFS/BFS from one node won't visit all nodes. Always wrap your traversal in a loop over all nodes.

```java
// WRONG: only explores one component
dfs(graph, 0, visited);

// CORRECT: handles disconnected graphs
for (int i = 0; i < n; i++) {
    if (!visited[i]) {
        dfs(graph, i, visited);
        components++;
    }
}
```

### 5. Wrong bounds check in grid problems

```java
// WRONG: check bounds after accessing grid (ArrayIndexOutOfBoundsException)
if (grid[nr][nc] == 1 && nr >= 0 && nr < rows && nc >= 0 && nc < cols)

// CORRECT: check bounds first (short-circuit evaluation)
if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1)
```

Java evaluates `&&` left to right and short-circuits. Always put bounds checks before array access.

### 6. Modifying the grid as visited without restoring

Some problems require you to restore the grid after DFS (e.g., when exploring multiple paths). If you mark cells as visited by modifying the grid and don't restore, subsequent explorations will see incorrect state.

```java
// If you need to restore (e.g., backtracking problems):
grid[r][c] = '#'; // mark visited
dfs(grid, r, c, ...);
grid[r][c] = '1'; // restore

// If you don't need to restore (e.g., counting islands):
grid[r][c] = '0'; // mark visited, no need to restore
```

### 7. Using DFS for shortest path

DFS does NOT guarantee shortest path. It finds A path, not the shortest one. Always use BFS for shortest path in unweighted graphs.

---

## Pattern Comparison

### BFS vs DFS Decision Table

| Question | Use |
|---|---|
| Shortest path (unweighted)? | BFS |
| Minimum steps/hops? | BFS |
| Level-by-level processing? | BFS |
| Multiple sources spreading simultaneously? | Multi-source BFS |
| Does a path exist? | Either (DFS simpler) |
| Connected components? | Either (DFS simpler) |
| Cycle detection (directed)? | DFS (3-color) |
| Cycle detection (undirected)? | DFS (parent tracking) or Union-Find |
| Topological sort? | DFS (post-order) or BFS (Kahn's) |
| All possible paths? | DFS (backtracking) |
| Weighted shortest path? | Dijkstra (Topic 16) |

### Tree Traversal (Topics 10-11) vs Graph Traversal

| Aspect | Tree (Topics 10-11) | Graph (This document) |
|---|---|---|
| Cycles | None (acyclic by definition) | Possible |
| Visited set needed? | No | Yes, always |
| Connectivity | Always connected (from root) | May be disconnected |
| Parent | Always one parent | May have multiple or none |
| Representation | TreeNode with left/right | Adjacency list/matrix |
| BFS | Level-order, snapshot size | Same, but need visited set |
| DFS | Pre/in/post-order | Reachability, components, cycles |

The key insight: trees ARE graphs (specifically, connected acyclic undirected graphs). Everything you learned in Topics 10-11 applies here, with the addition of cycle handling and disconnected component handling.

### Graph BFS vs Dijkstra (Topic 16)

| Aspect | Graph BFS | Dijkstra |
|---|---|---|
| Edge weights | All equal (unweighted) | Arbitrary non-negative |
| Data structure | Queue (FIFO) | Priority Queue (min-heap) |
| Time complexity | O(V + E) | O((V + E) log V) |
| Shortest path? | Yes (by hop count) | Yes (by total weight) |
| When to use | Unweighted graphs, grid problems | Weighted graphs, road networks |

If you use BFS on a weighted graph, you'll get the path with fewest edges, not the path with minimum total weight. These are different things.

### Grid DFS vs Union-Find (Topic 17)

| Aspect | Grid DFS | Union-Find |
|---|---|---|
| Use case | Explore shape, area, flood fill | Count components, check connectivity |
| Dynamic updates? | No (static graph) | Yes (add edges online) |
| Code complexity | Simple recursion | Slightly more setup |
| Space | O(m * n) recursion stack | O(m * n) parent array |
| When to prefer | When you need to explore/modify cells | When you only need component counts |

For pure "count islands" problems, both work. Union-Find shines when edges are added dynamically (e.g., LC 305 Number of Islands II).

---

## Quick Reference Cheat Sheet

### Graph Representations

```java
// Adjacency list from edge list (undirected)
Map<Integer, List<Integer>> graph = new HashMap<>();
for (int[] e : edges) {
    graph.computeIfAbsent(e[0], k -> new ArrayList<>()).add(e[1]);
    graph.computeIfAbsent(e[1], k -> new ArrayList<>()).add(e[0]);
}

// Adjacency list (directed)
for (int[] e : edges) {
    graph.computeIfAbsent(e[0], k -> new ArrayList<>()).add(e[1]);
    // no reverse edge for directed
}
```

### BFS Skeleton

```java
boolean[] visited = new boolean[n];
Queue<Integer> queue = new LinkedList<>();
visited[src] = true;
queue.offer(src);
int dist = 0;

while (!queue.isEmpty()) {
    int size = queue.size();
    for (int i = 0; i < size; i++) {
        int node = queue.poll();
        // process node at distance 'dist'
        for (int neighbor : graph.getOrDefault(node, List.of())) {
            if (!visited[neighbor]) {
                visited[neighbor] = true;
                queue.offer(neighbor);
            }
        }
    }
    dist++;
}
```

### DFS Skeleton

```java
boolean[] visited = new boolean[n];

void dfs(int node) {
    visited[node] = true;
    for (int neighbor : graph.getOrDefault(node, List.of())) {
        if (!visited[neighbor]) dfs(neighbor);
    }
}

// Handle disconnected:
for (int i = 0; i < n; i++) {
    if (!visited[i]) dfs(i);
}
```

### Grid Traversal

```java
int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
boolean[][] visited = new boolean[rows][cols];

// BFS
Queue<int[]> q = new LinkedList<>();
visited[sr][sc] = true;
q.offer(new int[]{sr, sc});
while (!q.isEmpty()) {
    int[] cur = q.poll();
    for (int[] d : dirs) {
        int nr = cur[0]+d[0], nc = cur[1]+d[1];
        if (nr>=0 && nr<rows && nc>=0 && nc<cols && !visited[nr][nc]) {
            visited[nr][nc] = true;
            q.offer(new int[]{nr, nc});
        }
    }
}

// DFS
void dfs(int r, int c) {
    if (r<0||r>=rows||c<0||c>=cols||visited[r][c]) return;
    visited[r][c] = true;
    for (int[] d : dirs) dfs(r+d[0], c+d[1]);
}
```

### Multi-Source BFS

```java
Queue<int[]> q = new LinkedList<>();
// Add ALL sources first
for (int r = 0; r < rows; r++)
    for (int c = 0; c < cols; c++)
        if (isSource(r, c)) { visited[r][c]=true; q.offer(new int[]{r,c}); }
// Then standard BFS
while (!q.isEmpty()) { /* standard BFS */ }
```

### 3-Color Cycle Detection (Directed)

```java
int[] state = new int[n]; // 0=unvisited, 1=visiting, 2=done

boolean hasCycle(int node) {
    state[node] = 1;
    for (int nb : graph.get(node)) {
        if (state[nb] == 1) return true;
        if (state[nb] == 0 && hasCycle(nb)) return true;
    }
    state[node] = 2;
    return false;
}
```

### Problem Quick Reference

| LC | Title | Pattern | Difficulty |
|---|---|---|---|
| 200 | Number of Islands | Grid DFS/BFS | Medium |
| 695 | Max Area of Island | Grid DFS, return area | Medium |
| 733 | Flood Fill | Grid DFS, in-place | Easy |
| 130 | Surrounded Regions | Border DFS, inverse mark | Medium |
| 417 | Pacific Atlantic | Reverse multi-source BFS | Medium |
| 994 | Rotting Oranges | Multi-source BFS | Medium |
| 542 | 01 Matrix | Multi-source BFS from 0s | Medium |
| 286 | Walls and Gates | Multi-source BFS from gates | Medium |
| 127 | Word Ladder | BFS on implicit graph | Hard |
| 133 | Clone Graph | BFS + HashMap | Medium |
| 841 | Keys and Rooms | DFS reachability | Medium |
| 547 | Number of Provinces | DFS on adj matrix | Medium |
| 323 | Connected Components | DFS on edge list | Medium |
| 1466 | Reorder Routes | DFS, edge direction cost | Medium |
| 802 | Find Eventual Safe States | 3-color DFS | Medium |
| 207 | Course Schedule | 3-color DFS, cycle detect | Medium |

---

## Practice Roadmap

### Week 1: Grid Fundamentals (Days 1-3)

Master the grid-as-graph pattern. These are the most common graph problems in interviews.

**Day 1:**
- LC 733 (Flood Fill) — Easy warm-up, understand in-place DFS
- LC 200 (Number of Islands) — The canonical grid problem, do it 3 times

**Day 2:**
- LC 695 (Max Area of Island) — Same as 200 but return area
- LC 130 (Surrounded Regions) — Inverse thinking, border DFS

**Day 3:**
- LC 994 (Rotting Oranges) — Multi-source BFS, trace through manually
- LC 542 (01 Matrix) — Multi-source BFS from 0s

**Goal:** Write grid BFS and DFS from memory. Know the `dirs` pattern cold.

---

### Week 2: Graph BFS and Components (Days 4-7)

Move from grids to explicit graphs.

**Day 4:**
- LC 547 (Number of Provinces) — Connected components on adj matrix
- LC 323 (Connected Components) — Same on edge list

**Day 5:**
- LC 133 (Clone Graph) — BFS + HashMap, tricky but important
- LC 841 (Keys and Rooms) — Simple DFS reachability

**Day 6:**
- LC 286 (Walls and Gates) — Multi-source BFS, similar to 994
- LC 417 (Pacific Atlantic) — Reverse BFS from two borders

**Day 7:**
- LC 127 (Word Ladder) — Hard, BFS on implicit graph
- Review all week 1 problems without looking at solutions

---

### Week 3: Cycle Detection and Advanced Patterns (Days 8-11)

**Day 8:**
- LC 207 (Course Schedule) — 3-color DFS, cycle detection
- LC 802 (Find Eventual Safe States) — Same pattern, different framing

**Day 9:**
- LC 1466 (Reorder Routes) — DFS with edge direction tracking
- Review: when to use BFS vs DFS (decision table)

**Day 10:**
- Revisit LC 127 (Word Ladder) — optimize the neighbor generation
- LC 417 (Pacific Atlantic) — try DFS variant

**Day 11:**
- Mock interview: pick 2 random problems from the list, solve under 45 minutes each

---

### Week 4: Speed and Variations (Days 12-14)

**Day 12:**
- Timed practice: LC 200, 994, 207 — solve each in under 20 minutes
- Focus on clean code, not just correctness

**Day 13:**
- Revisit any problems you struggled with
- Practice explaining your approach out loud (BFS vs DFS choice, why visited set)

**Day 14:**
- Full mock: 2 graph problems back to back, 45 minutes each
- Review Topic 15 (Topological Sort) — builds directly on this document
- Preview Topic 16 (Dijkstra) — weighted graph extension of BFS

---

### Progression Path

```
Topic 10 (Tree DFS) -> Topic 11 (Tree BFS) -> Topic 14 (Graph BFS/DFS) [HERE]
                                                        |
                                                        v
                                              Topic 15 (Topological Sort)
                                                        |
                                                        v
                                              Topic 16 (Dijkstra / Weighted Graphs)
                                                        |
                                                        v
                                              Topic 17 (Union-Find)
```

Graph traversal is the foundation. Every advanced graph algorithm (Dijkstra, Bellman-Ford, topological sort, minimum spanning tree) is built on top of BFS or DFS. Get these patterns automatic before moving on.

---

### Interview Checklist

Before submitting any graph solution, verify:

- [ ] Do I have a `visited` set/array? (Always needed for graphs)
- [ ] Am I marking visited when I **add to queue** (BFS) or when I **enter the function** (DFS)?
- [ ] Did I handle disconnected components? (Outer loop over all nodes)
- [ ] For grid problems: are my bounds checks BEFORE array access?
- [ ] Am I using BFS for shortest path? (Not DFS)
- [ ] For weighted graphs: am I using Dijkstra instead of BFS?
- [ ] For cycle detection in directed graphs: am I using 3-color DFS?
- [ ] For multi-source problems: did I seed ALL sources before starting BFS?
