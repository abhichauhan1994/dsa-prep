# Topic 16: Shortest Path Algorithms

> **Series position:** Document 16 of 20
> **Difficulty range:** Medium to Hard
> **Interview frequency:** Shortest path problems appear in ~20% of FAANG interviews. Google and Amazon weight them heavily. Bloomberg asks network-routing variants frequently. Meta favors graph-as-equation problems (LC 399).
> **Top companies:** Google, Amazon, Meta, Microsoft, Bloomberg
> **Prerequisites:** Topic 12 (Heaps & Priority Queues), Topic 14 (Graph Traversal BFS/DFS)

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5 Intuition](#eli5-intuition)
3. [Core Templates in Java](#core-templates-in-java)
4. [Real-World Applications](#real-world-applications)
5. [Problem Categories and Solutions](#problem-categories-and-solutions)
   - [Category A: Standard Dijkstra](#category-a-standard-dijkstra)
   - [Category B: Constrained Shortest Path](#category-b-constrained-shortest-path)
   - [Category C: Graph as Equation](#category-c-graph-as-equation)
   - [Category D: Advanced Variants](#category-d-advanced-variants)
6. [Common Mistakes](#common-mistakes)
7. [Algorithm Comparison](#algorithm-comparison)
8. [Quick Reference Cheat Sheet](#quick-reference-cheat-sheet)
9. [Practice Roadmap](#practice-roadmap)

---

## Core Concept

### The Shortest Path Problem

Given a graph and a source node, find the minimum-cost path to every other node (or to a specific target). "Cost" depends on the graph type:

- **Unweighted graph:** cost = number of edges. BFS gives the answer directly.
- **Weighted graph, non-negative edges:** cost = sum of edge weights. Dijkstra's algorithm.
- **Weighted graph, negative edges allowed:** Bellman-Ford.
- **All pairs shortest path:** Floyd-Warshall (rarely asked in interviews, but worth knowing exists).

The key insight connecting all three: they all relax edges. "Relaxing" an edge (u, v, w) means: if `dist[u] + w < dist[v]`, update `dist[v] = dist[u] + w`. The algorithms differ only in the *order* they process nodes and edges.

---

### BFS for Unweighted Graphs (Recap from Topic 14)

BFS explores nodes level by level. In an unweighted graph, "level" equals distance from the source. The first time BFS reaches a node, it has found the shortest path to it.

```
Time:  O(V + E)
Space: O(V)
Works: Unweighted graphs only
```

```java
// BFS shortest path — unweighted graph
int[] bfsShortestPath(Map<Integer, List<Integer>> graph, int src, int n) {
    int[] dist = new int[n];
    Arrays.fill(dist, -1);
    dist[src] = 0;

    Queue<Integer> queue = new LinkedList<>();
    queue.offer(src);

    while (!queue.isEmpty()) {
        int node = queue.poll();
        for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
            if (dist[neighbor] == -1) {
                dist[neighbor] = dist[node] + 1;
                queue.offer(neighbor);
            }
        }
    }
    return dist;
}
```

This is covered in depth in Topic 14. The rest of this document focuses on weighted graphs.

---

### Dijkstra's Algorithm

Dijkstra solves single-source shortest path on graphs with **non-negative edge weights**.

**The core idea:** Dijkstra is BFS where the FIFO queue is replaced by a min-heap (priority queue). Instead of processing nodes in arrival order, you always process the node with the smallest known distance first. This greedy choice works because once you've settled a node (processed it from the heap), you've found its true shortest distance. No future path can improve it, since all edge weights are non-negative.

```
Time:  O((V + E) log V) with binary heap
Space: O(V + E)
Works: Weighted graphs with non-negative edges
```

**The algorithm step by step:**

1. Initialize `dist[src] = 0`, all others = `Integer.MAX_VALUE`.
2. Push `(0, src)` into a min-heap (ordered by distance).
3. While the heap is not empty:
   a. Poll `(d, node)` — the node with smallest known distance.
   b. **Lazy deletion check:** if `d > dist[node]`, skip. This node was already settled with a better distance.
   c. For each neighbor `(next, weight)` of `node`:
      - If `dist[node] + weight < dist[next]`, update `dist[next]` and push `(dist[next], next)` to the heap.
4. Return `dist[]`.

**Why lazy deletion?** Java's `PriorityQueue` doesn't support efficient decrease-key. When you find a better path to a node, you push a new entry rather than updating the old one. The old (stale) entry stays in the heap. The lazy deletion check at step 3b discards it when it's eventually polled.

**Connection to Topic 12 (Heaps):** Dijkstra's entire correctness depends on the min-heap property. The heap guarantees you always process the globally closest unvisited node. Without it, the greedy argument breaks. This is why Dijkstra is sometimes described as "BFS with a priority queue." The structure is identical; only the queue type changes.

---

### Bellman-Ford Algorithm

Bellman-Ford solves single-source shortest path on graphs that **may have negative edge weights**.

**The core idea:** Relax every edge V-1 times. After k relaxations, `dist[v]` holds the shortest path using at most k edges. Since any simple path in a V-node graph uses at most V-1 edges, V-1 rounds is sufficient.

```
Time:  O(V * E)
Space: O(V)
Works: Weighted graphs including negative edges
Bonus: Can detect negative cycles (run one more round; if any dist updates, a negative cycle exists)
```

**Why V-1 rounds?** A shortest path visits at most V nodes, so it has at most V-1 edges. Each round of relaxation "extends" the shortest path by one more edge. After V-1 rounds, all shortest paths are found.

**Negative cycle detection:** If you run a V-th round and any distance still decreases, there's a negative cycle reachable from the source. Shortest path is undefined in that case (you can keep going around the cycle to decrease cost infinitely).

---

### Floyd-Warshall (Brief Mention)

All-pairs shortest path. O(V^3) time, O(V^2) space. Uses dynamic programming: `dp[i][j][k]` = shortest path from i to j using only nodes 0..k as intermediates.

```java
// Floyd-Warshall skeleton
int[][] dist = new int[n][n];
// initialize dist[i][j] from edge list, dist[i][i] = 0, others = INF

for (int k = 0; k < n; k++)
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            if (dist[i][k] != INF && dist[k][j] != INF)
                dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
```

Rarely asked directly in interviews, but LC 1334 (Find the City) uses it.

---

### When to Use Which Algorithm

```
Graph type                          Algorithm
-----------------------------------------
Unweighted                          BFS
Weighted, non-negative weights      Dijkstra
Weighted, negative weights          Bellman-Ford
All-pairs shortest path             Floyd-Warshall
Weighted, heuristic available       A* (game dev, not typical FAANG)
Constrained (k stops, fuel limit)   Modified Dijkstra or BFS with state
```

The decision is almost always: "Are there negative weights?" If no, Dijkstra. If yes, Bellman-Ford. If the graph is unweighted, BFS.

---

## ELI5 Intuition

**BFS:** You're dropping a stone in a pond. Ripples spread outward in perfect circles. Every node at distance 1 gets hit first, then distance 2, then 3. The first time a ripple reaches a node, that's the shortest path.

**Dijkstra:** Same pond, but some paths are muddy (slow) and some are paved (fast). You don't expand in perfect circles anymore. Instead, you always send the next ripple along the fastest available path. You keep a list of "candidate next cities" sorted by how long it took to reach them. You always visit the closest one first. From each city, you check: "Did I just find a faster route to any of its neighbors?" If yes, update and add to the candidate list.

The key: once you've visited a city (pulled it from the priority queue), you're done with it. No future path can be shorter, because all roads have non-negative cost. Adding more road can only make things longer or equal, never shorter.

**Bellman-Ford:** You don't trust the roads. Some might have negative tolls (they pay you to drive through). You can't use Dijkstra's greedy trick anymore. Instead, you just brute-force it: go through every single road V-1 times, updating distances each time. Slow, but guaranteed correct even with negative weights.

---

## Core Templates in Java

### Template 1: Dijkstra's with PriorityQueue (Standard)

This is the template you'll use for 80% of shortest path problems. Memorize it cold.

```java
import java.util.*;

public class Dijkstra {

    /**
     * Standard Dijkstra's algorithm.
     *
     * @param n     number of nodes (0-indexed, nodes 0..n-1)
     * @param graph adjacency list: graph[u] = list of {v, weight}
     * @param src   source node
     * @return dist[] where dist[i] = shortest distance from src to i,
     *         or Integer.MAX_VALUE if unreachable
     */
    public int[] dijkstra(int n, List<int[]>[] graph, int src) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // Min-heap: [distance, node]
        // PriorityQueue orders by first element (distance) ascending
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, src});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0];
            int node = curr[1];

            // LAZY DELETION: skip if we already found a better path to this node
            // This handles stale entries left in the heap from earlier updates
            if (d > dist[node]) continue;

            // Process all neighbors
            for (int[] edge : graph[node]) {
                int next = edge[0];
                int weight = edge[1];

                // Relaxation step
                if (dist[node] + weight < dist[next]) {
                    dist[next] = dist[node] + weight;
                    pq.offer(new int[]{dist[next], next});
                }
            }
        }

        return dist;
    }

    // Helper: build adjacency list from edge list
    // edges[i] = {u, v, weight} for directed graph
    @SuppressWarnings("unchecked")
    public List<int[]>[] buildGraph(int n, int[][] edges) {
        List<int[]>[] graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int[] e : edges) {
            graph[e[0]].add(new int[]{e[1], e[2]});
            // For undirected: graph[e[1]].add(new int[]{e[0], e[2]});
        }
        return graph;
    }
}
```

**Critical notes on this template:**

1. `(a, b) -> a[0] - b[0]` is safe here because distances are non-negative (no overflow risk). If distances could be large, use `Integer.compare(a[0], b[0])`.
2. The `if (d > dist[node]) continue;` line is the lazy deletion. Without it, you process stale heap entries and waste time (but the algorithm still produces correct results; it's a performance optimization, not a correctness fix).
3. `dist[src] = 0` before pushing to the heap. Don't forget this.
4. `Integer.MAX_VALUE` as initial distance. Be careful: `Integer.MAX_VALUE + anything` overflows. The check `dist[node] + weight < dist[next]` is safe because we skip nodes where `dist[node] == Integer.MAX_VALUE` (they'd never be polled first in a connected graph, but add a guard if needed).

---

### Template 2: Bellman-Ford

```java
public class BellmanFord {

    /**
     * Bellman-Ford algorithm.
     *
     * @param n     number of nodes (1-indexed in this version, nodes 1..n)
     * @param edges edge list: edges[i] = {u, v, weight}
     * @param src   source node
     * @return dist[] where dist[i] = shortest distance from src to i,
     *         or Integer.MAX_VALUE if unreachable.
     *         Returns null if a negative cycle is detected.
     */
    public int[] bellmanFord(int n, int[][] edges, int src) {
        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // Relax all edges V-1 times
        for (int i = 0; i < n - 1; i++) {
            boolean updated = false; // early termination optimization
            for (int[] edge : edges) {
                int u = edge[0], v = edge[1], w = edge[2];
                // Guard against overflow: only relax if u is reachable
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    updated = true;
                }
            }
            if (!updated) break; // converged early
        }

        // Negative cycle detection: run one more round
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1], w = edge[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                return null; // negative cycle detected
            }
        }

        return dist;
    }
}
```

**When to use Bellman-Ford in interviews:**

- Problem explicitly mentions negative weights.
- LC 787 (Cheapest Flights Within K Stops) — modified Bellman-Ford where you run exactly K+1 rounds instead of V-1.
- Any problem asking to detect negative cycles.

**The K-stops variant of Bellman-Ford** (used in LC 787):

```java
// Modified Bellman-Ford for "at most K stops" (K+1 edges)
public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;

    // Run exactly k+1 rounds (k stops = k+1 edges)
    for (int i = 0; i <= k; i++) {
        // CRITICAL: copy dist before this round to avoid using updates from same round
        int[] temp = dist.clone();
        for (int[] flight : flights) {
            int u = flight[0], v = flight[1], price = flight[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + price < temp[v]) {
                temp[v] = dist[u] + price;
            }
        }
        dist = temp;
    }

    return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
}
```

The `temp = dist.clone()` is crucial. Without it, a single round might chain multiple relaxations (A->B->C in one pass), effectively using more than one edge per round. The clone ensures each round only extends paths by exactly one edge.

---

### Template 3: Modified Dijkstra with State (Constraints)

Many interview problems add constraints: "at most K stops", "fuel limit", "toll roads". The trick is to encode the constraint into the state. Instead of `dist[node]`, you track `dist[node][constraint_value]`.

```java
/**
 * Modified Dijkstra with an extra state dimension.
 *
 * Example: shortest path with at most K stops.
 * State: (cost, node, stops_remaining)
 * dist[node][stops] = min cost to reach node with exactly 'stops' stops used
 */
public int dijkstraWithStops(int n, List<int[]>[] graph, int src, int dst, int maxStops) {
    // dist[node][stops_used] = min cost
    int[][] dist = new int[n][maxStops + 2];
    for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
    dist[src][0] = 0;

    // Min-heap: [cost, node, stops_used]
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
    pq.offer(new int[]{0, src, 0});

    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int cost = curr[0], node = curr[1], stops = curr[2];

        if (node == dst) return cost; // found destination

        if (stops > maxStops) continue; // exceeded stop limit

        // Lazy deletion on 2D state
        if (cost > dist[node][stops]) continue;

        for (int[] edge : graph[node]) {
            int next = edge[0], weight = edge[1];
            int newCost = cost + weight;
            int newStops = stops + 1;

            if (newStops <= maxStops + 1 && newCost < dist[next][newStops]) {
                dist[next][newStops] = newCost;
                pq.offer(new int[]{newCost, next, newStops});
            }
        }
    }

    return -1; // unreachable
}
```

**The general pattern for constrained Dijkstra:**

```
State = (cost, node, constraint_value)
dist[node][constraint_value] = min cost to reach node with this constraint value
Heap ordered by cost
Transition: update constraint_value based on problem rules
```

Common constraint dimensions:
- Stops used (LC 787)
- Fuel remaining (LC 505 variant)
- Number of obstacles removed (LC 1293)
- Time of day (for time-dependent graphs)

---

## Real-World Applications

**Google Maps / GPS navigation:** Dijkstra (or A* for faster performance) finds the shortest driving route. Edge weights are travel times. The graph has millions of nodes (road intersections) and edges (road segments). Real systems use bidirectional Dijkstra and contraction hierarchies for speed, but the core algorithm is the same.

**Network routing (OSPF protocol):** Open Shortest Path First is a routing protocol used by internet routers. Each router runs Dijkstra on its local view of the network topology. Edge weights are link costs (bandwidth, latency). This is textbook Dijkstra at internet scale.

**CDN path optimization:** Content delivery networks route requests to the nearest server. Shortest path algorithms minimize latency. Edge weights are network latency measurements.

**Game pathfinding (A*):** A* is Dijkstra with a heuristic. Instead of ordering by `dist[node]`, it orders by `dist[node] + heuristic(node, target)`. The heuristic (usually Euclidean distance) guides the search toward the target, making it faster in practice. A* is the standard in game engines for NPC movement.

**Airline route planning:** Finding the cheapest or fastest route between cities with layovers. LC 787 is a direct model of this. Edge weights are ticket prices or flight times.

**Social network analysis:** Finding the shortest chain of connections between two people (six degrees of separation). Unweighted BFS for hop count, Dijkstra if connections have weights (e.g., interaction frequency).

---

## Problem Categories and Solutions

---

### Category A: Standard Dijkstra

---

#### LC 743 — Network Delay Time

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** Standard Dijkstra, single source, find max of all shortest paths

**Problem:** Given a network of n nodes and directed weighted edges `times[i] = [u, v, w]`, find the minimum time for a signal sent from node `k` to reach all nodes. Return -1 if not all nodes are reachable.

**Key insight:** Run Dijkstra from source k. The answer is `max(dist[])`. If any node is unreachable (`dist[i] == Integer.MAX_VALUE`), return -1.

**Step-by-step Dijkstra trace on example:**

```
Input: times = [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2
Nodes: 1, 2, 3, 4 (1-indexed)
Edges: 2->1 (weight 1), 2->3 (weight 1), 3->4 (weight 1)

Initial state:
  dist = [INF, INF, 0, INF, INF]  (index 0 unused, 1-indexed)
  heap = [(0, 2)]

--- Step 1: Poll (0, 2) ---
  d=0, node=2
  Lazy check: 0 == dist[2]=0, proceed
  Neighbors of 2:
    -> node 1, weight 1: dist[2]+1=1 < dist[1]=INF  => dist[1]=1, push (1,1)
    -> node 3, weight 1: dist[2]+1=1 < dist[3]=INF  => dist[3]=1, push (1,3)
  dist = [INF, 1, 0, 1, INF]
  heap = [(1,1), (1,3)]

--- Step 2: Poll (1, 1) ---
  d=1, node=1
  Lazy check: 1 == dist[1]=1, proceed
  Neighbors of 1: (none in this graph)
  dist = [INF, 1, 0, 1, INF]
  heap = [(1,3)]

--- Step 3: Poll (1, 3) ---
  d=1, node=3
  Lazy check: 1 == dist[3]=1, proceed
  Neighbors of 3:
    -> node 4, weight 1: dist[3]+1=2 < dist[4]=INF  => dist[4]=2, push (2,4)
  dist = [INF, 1, 0, 1, 2]
  heap = [(2,4)]

--- Step 4: Poll (2, 4) ---
  d=2, node=4
  Lazy check: 2 == dist[4]=2, proceed
  Neighbors of 4: (none)
  dist = [INF, 1, 0, 1, 2]
  heap = []

Heap empty. Done.
dist[1..4] = [1, 0, 1, 2]
Answer = max(1, 0, 1, 2) = 2
```

**Solution:**

```java
class Solution {
    public int networkDelayTime(int[][] times, int n, int k) {
        // Build adjacency list (1-indexed nodes)
        List<int[]>[] graph = new List[n + 1];
        for (int i = 0; i <= n; i++) graph[i] = new ArrayList<>();
        for (int[] t : times) {
            graph[t[0]].add(new int[]{t[1], t[2]});
        }

        // Dijkstra from source k
        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, k});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0], node = curr[1];

            if (d > dist[node]) continue; // lazy deletion

            for (int[] edge : graph[node]) {
                int next = edge[0], weight = edge[1];
                if (dist[node] + weight < dist[next]) {
                    dist[next] = dist[node] + weight;
                    pq.offer(new int[]{dist[next], next});
                }
            }
        }

        // Find max distance across all nodes
        int maxDist = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) return -1;
            maxDist = Math.max(maxDist, dist[i]);
        }
        return maxDist;
    }
}
```

**Complexity:** Time O((V+E) log V), Space O(V+E)

**Edge cases:**
- Single node: return 0.
- Disconnected graph: return -1.
- Self-loops: handled naturally (lazy deletion skips them).

---

#### LC 1514 — Path with Maximum Probability

**Companies:** Amazon, Google
**Difficulty:** Medium
**Pattern:** Modified Dijkstra — maximize probability instead of minimize distance

**Problem:** Given an undirected weighted graph where edge weights are probabilities (0 to 1), find the path from `start` to `end` with maximum probability of success.

**Key insight:** Dijkstra works for maximization too. Flip the comparison: instead of `dist[u] + w < dist[v]`, use `prob[u] * w > prob[v]`. Use a max-heap (reverse comparator). Initialize `prob[start] = 1.0`, all others = 0.0.

**Why this works:** Probabilities multiply along a path. The greedy argument still holds: once you've settled a node with the maximum probability, no other path can improve it (all probabilities are between 0 and 1, so multiplying by more edges can only decrease or maintain the probability).

```java
class Solution {
    public double maxProbability(int n, int[][] edges, double[] succProb,
                                  int start, int end) {
        // Build adjacency list with probabilities
        List<double[]>[] graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int i = 0; i < edges.length; i++) {
            int u = edges[i][0], v = edges[i][1];
            double p = succProb[i];
            graph[u].add(new double[]{v, p});
            graph[v].add(new double[]{u, p}); // undirected
        }

        double[] prob = new double[n];
        prob[start] = 1.0;

        // Max-heap: [probability, node] — largest probability first
        PriorityQueue<double[]> pq = new PriorityQueue<>((a, b) -> Double.compare(b[0], a[0]));
        pq.offer(new double[]{1.0, start});

        while (!pq.isEmpty()) {
            double[] curr = pq.poll();
            double p = curr[0];
            int node = (int) curr[1];

            if (node == end) return p; // early exit

            if (p < prob[node]) continue; // lazy deletion

            for (double[] edge : graph[node]) {
                int next = (int) edge[0];
                double edgeProb = edge[1];
                double newProb = prob[node] * edgeProb;
                if (newProb > prob[next]) {
                    prob[next] = newProb;
                    pq.offer(new double[]{newProb, next});
                }
            }
        }

        return 0.0; // end unreachable
    }
}
```

**Complexity:** Time O((V+E) log V), Space O(V+E)

**The pattern to remember:** Dijkstra works for any "shortest path" problem where the path cost is monotonically non-decreasing as you add edges. Probability multiplication satisfies this (probabilities are <= 1, so the product never increases). Just flip the comparator and the relaxation condition.

---

#### LC 778 — Swim in Rising Water

**Companies:** Amazon, Google, Microsoft
**Difficulty:** Hard
**Pattern:** Dijkstra on grid — minimize the maximum value along the path

**Problem:** Given an n x n grid where `grid[i][j]` is the elevation, find the minimum time T such that there's a path from (0,0) to (n-1,n-1) where all cells on the path have elevation <= T.

**Key insight:** This is a "minimax path" problem. The cost of a path is the maximum elevation encountered. Use Dijkstra where `dist[i][j]` = minimum possible maximum elevation to reach cell (i,j). The relaxation becomes: `newCost = max(dist[r][c], grid[nr][nc])`.

```java
class Solution {
    public int swimInWater(int[][] grid) {
        int n = grid.length;
        int[][] dist = new int[n][n];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
        dist[0][0] = grid[0][0];

        // Min-heap: [cost, row, col]
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{grid[0][0], 0, 0});

        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int cost = curr[0], r = curr[1], c = curr[2];

            if (r == n-1 && c == n-1) return cost; // reached destination

            if (cost > dist[r][c]) continue; // lazy deletion

            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nr >= n || nc < 0 || nc >= n) continue;

                // Cost to reach (nr,nc) = max of current cost and cell elevation
                int newCost = Math.max(cost, grid[nr][nc]);
                if (newCost < dist[nr][nc]) {
                    dist[nr][nc] = newCost;
                    pq.offer(new int[]{newCost, nr, nc});
                }
            }
        }

        return dist[n-1][n-1];
    }
}
```

**Complexity:** Time O(n^2 log n), Space O(n^2)

**Alternative approach:** Binary search on T + BFS/DFS to check reachability. O(n^2 log n) same complexity, but Dijkstra is cleaner.

---

### Category B: Constrained Shortest Path

---

#### LC 787 — Cheapest Flights Within K Stops

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** Modified Bellman-Ford (K+1 rounds) or BFS with state

**Problem:** Find the cheapest flight from `src` to `dst` with at most `k` stops. Return -1 if no such route exists.

**Why not standard Dijkstra?** Standard Dijkstra finds the globally cheapest path, ignoring the stop constraint. A path with fewer stops might be more expensive but valid; a cheaper path might use too many stops. You need to track stops as part of the state.

**Approach 1: Modified Bellman-Ford (cleaner for this problem)**

Run exactly k+1 rounds of Bellman-Ford (k stops = k+1 edges). Use a copy of the distance array each round to prevent chaining within a single round.

```java
class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // k stops = k+1 edges = k+1 rounds of relaxation
        for (int i = 0; i <= k; i++) {
            int[] temp = dist.clone(); // snapshot before this round
            for (int[] flight : flights) {
                int u = flight[0], v = flight[1], price = flight[2];
                if (dist[u] != Integer.MAX_VALUE && dist[u] + price < temp[v]) {
                    temp[v] = dist[u] + price;
                }
            }
            dist = temp;
        }

        return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
    }
}
```

**Approach 2: BFS with state (level-by-level, k+1 levels)**

```java
class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        // Build adjacency list
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int[] f : flights) {
            graph.computeIfAbsent(f[0], x -> new ArrayList<>()).add(new int[]{f[1], f[2]});
        }

        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{src, 0}); // [node, cost]

        int stops = 0;
        while (!queue.isEmpty() && stops <= k) {
            int size = queue.size();
            int[] temp = dist.clone(); // snapshot for this level
            for (int i = 0; i < size; i++) {
                int[] curr = queue.poll();
                int node = curr[0], cost = curr[1];
                for (int[] edge : graph.getOrDefault(node, new ArrayList<>())) {
                    int next = edge[0], price = edge[1];
                    if (cost + price < temp[next]) {
                        temp[next] = cost + price;
                        queue.offer(new int[]{next, temp[next]});
                    }
                }
            }
            dist = temp;
            stops++;
        }

        return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
    }
}
```

**Complexity:** Time O(k * E), Space O(n)

**Which approach to use in interviews?** The Bellman-Ford approach is shorter and cleaner. Mention both to show depth.

---

#### LC 1631 — Path With Minimum Effort

**Companies:** Amazon, Google, Microsoft
**Difficulty:** Medium
**Pattern:** Dijkstra on grid — minimize maximum absolute difference along path

**Problem:** Find a path from (0,0) to (m-1,n-1) that minimizes the maximum absolute difference in heights between consecutive cells.

**Key insight:** Same minimax pattern as LC 778. `dist[r][c]` = minimum possible maximum effort to reach (r,c). Relaxation: `newEffort = max(currentEffort, abs(grid[r][c] - grid[nr][nc]))`.

```java
class Solution {
    public int minimumEffortPath(int[][] heights) {
        int m = heights.length, n = heights[0].length;
        int[][] dist = new int[m][n];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
        dist[0][0] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, 0, 0}); // [effort, row, col]

        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int effort = curr[0], r = curr[1], c = curr[2];

            if (r == m-1 && c == n-1) return effort;

            if (effort > dist[r][c]) continue;

            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue;

                int newEffort = Math.max(effort, Math.abs(heights[r][c] - heights[nr][nc]));
                if (newEffort < dist[nr][nc]) {
                    dist[nr][nc] = newEffort;
                    pq.offer(new int[]{newEffort, nr, nc});
                }
            }
        }

        return 0;
    }
}
```

**Complexity:** Time O(m*n log(m*n)), Space O(m*n)

**Alternative:** Binary search on effort + BFS/DFS. Same complexity, but Dijkstra is one pass.

---

#### LC 505 — The Maze II (Premium)

**Companies:** Amazon, Google
**Difficulty:** Medium
**Pattern:** Dijkstra on grid with rolling ball mechanics

**Problem:** A ball rolls in a maze until it hits a wall. Find the shortest distance (number of steps) from start to destination.

**Key insight:** The ball doesn't stop at every cell. It rolls until hitting a wall. So neighbors aren't adjacent cells; they're the cells where the ball stops after rolling in each direction. Use Dijkstra where each "edge" is a roll in one direction, and the weight is the number of cells rolled.

```java
class Solution {
    public int shortestDistance(int[][] maze, int[] start, int[] destination) {
        int m = maze.length, n = maze[0].length;
        int[][] dist = new int[m][n];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
        dist[start[0]][start[1]] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, start[0], start[1]});

        int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0], r = curr[1], c = curr[2];

            if (r == destination[0] && c == destination[1]) return d;
            if (d > dist[r][c]) continue;

            for (int[] dir : dirs) {
                int nr = r, nc = c, steps = 0;
                // Roll until hitting a wall
                while (nr + dir[0] >= 0 && nr + dir[0] < m &&
                       nc + dir[1] >= 0 && nc + dir[1] < n &&
                       maze[nr + dir[0]][nc + dir[1]] == 0) {
                    nr += dir[0];
                    nc += dir[1];
                    steps++;
                }
                int newDist = dist[r][c] + steps;
                if (newDist < dist[nr][nc]) {
                    dist[nr][nc] = newDist;
                    pq.offer(new int[]{newDist, nr, nc});
                }
            }
        }

        return -1;
    }
}
```

**Complexity:** Time O(m*n * max(m,n) * log(m*n)), Space O(m*n)

---

### Category C: Graph as Equation

---

#### LC 399 — Evaluate Division

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg
**Difficulty:** Medium
**Pattern:** Weighted graph where edges represent ratios; BFS/DFS to find path product

**Problem:** Given equations like `a/b = 2.0` and queries like `a/c`, find the value of each query. Return -1.0 if the answer doesn't exist.

**Key insight:** Model as a weighted directed graph. If `a/b = 2.0`, add edge `a -> b` with weight 2.0 and `b -> a` with weight 0.5. To answer `a/c`, find a path from `a` to `c` and multiply the edge weights. This is BFS/DFS on a weighted graph.

**Why this is a shortest path problem:** You're finding a path in a weighted graph and computing a product along the path. BFS works here (any path gives the correct answer since the graph is consistent). Dijkstra isn't needed because you're not minimizing; you're just finding any path.

```java
class Solution {
    public double[] calcEquation(List<List<String>> equations, double[] values,
                                  List<List<String>> queries) {
        // Build weighted graph
        Map<String, Map<String, Double>> graph = new HashMap<>();
        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            double val = values[i];
            graph.computeIfAbsent(a, k -> new HashMap<>()).put(b, val);
            graph.computeIfAbsent(b, k -> new HashMap<>()).put(a, 1.0 / val);
        }

        double[] results = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String src = queries.get(i).get(0);
            String dst = queries.get(i).get(1);
            results[i] = bfs(graph, src, dst);
        }
        return results;
    }

    private double bfs(Map<String, Map<String, Double>> graph, String src, String dst) {
        if (!graph.containsKey(src) || !graph.containsKey(dst)) return -1.0;
        if (src.equals(dst)) return 1.0;

        Set<String> visited = new HashSet<>();
        Queue<double[]> queue = new LinkedList<>(); // [node_as_index, product]
        // Use a queue of [node, accumulated_product]
        Queue<Object[]> q = new LinkedList<>();
        q.offer(new Object[]{src, 1.0});
        visited.add(src);

        while (!q.isEmpty()) {
            Object[] curr = q.poll();
            String node = (String) curr[0];
            double product = (double) curr[1];

            if (node.equals(dst)) return product;

            for (Map.Entry<String, Double> entry : graph.get(node).entrySet()) {
                String next = entry.getKey();
                double weight = entry.getValue();
                if (!visited.contains(next)) {
                    visited.add(next);
                    q.offer(new Object[]{next, product * weight});
                }
            }
        }

        return -1.0;
    }
}
```

**Complexity:** Time O(Q * (V+E)) where Q = number of queries, Space O(V+E)

**Alternative:** Union-Find with weighted edges. More complex but O(alpha(n)) per query after preprocessing.

---

#### LC 332 — Reconstruct Itinerary

**Companies:** Amazon, Google, Meta, Microsoft
**Difficulty:** Hard
**Pattern:** Eulerian path in directed graph (Hierholzer's algorithm)

**Problem:** Given a list of airline tickets, reconstruct the itinerary starting from "JFK" using all tickets exactly once. Return the lexicographically smallest valid itinerary.

**Key insight:** This is an Eulerian path problem (visit every edge exactly once). Use Hierholzer's algorithm: DFS with a stack, add nodes to result in reverse order when backtracking.

**Why it's in this topic:** It's a graph traversal problem where you're finding a path through all edges. The "shortest" aspect is lexicographic ordering, not distance. But it appears in shortest path interview sets because it tests graph path reconstruction.

```java
class Solution {
    Map<String, PriorityQueue<String>> graph = new HashMap<>();
    List<String> result = new LinkedList<>();

    public List<String> findItinerary(List<List<String>> tickets) {
        // Build adjacency list with min-heap for lexicographic order
        for (List<String> ticket : tickets) {
            graph.computeIfAbsent(ticket.get(0), k -> new PriorityQueue<>())
                 .offer(ticket.get(1));
        }
        dfs("JFK");
        return result;
    }

    private void dfs(String airport) {
        PriorityQueue<String> neighbors = graph.get(airport);
        while (neighbors != null && !neighbors.isEmpty()) {
            dfs(neighbors.poll());
        }
        // Add to front — we're building the path in reverse
        ((LinkedList<String>) result).addFirst(airport);
    }
}
```

**Complexity:** Time O(E log E) due to priority queue operations, Space O(E)

**The trick:** Standard DFS would fail on graphs with dead ends before all edges are used. Hierholzer's algorithm handles this by adding nodes to the result only when backtracking (no more outgoing edges). The result is built in reverse.

---

### Category D: Advanced Variants

---

#### LC 882 — Reachable Nodes In Subdivided Graph

**Companies:** Google, Amazon
**Difficulty:** Hard
**Pattern:** Dijkstra on a conceptually subdivided graph

**Problem:** Each edge `(u, v, cnt)` has `cnt` new nodes inserted between u and v. Find the total number of nodes reachable from node 0 within `maxMoves` steps.

**Key insight:** Don't actually build the subdivided graph (it could be huge). Use Dijkstra on the original graph to find `dist[node]` = minimum moves to reach each original node. Then count:
1. Original nodes where `dist[node] <= maxMoves`.
2. For each edge (u, v, cnt): nodes on this edge reachable from u = `min(cnt, max(0, maxMoves - dist[u]))`, from v = `min(cnt, max(0, maxMoves - dist[v]))`. Total reachable on this edge = `min(cnt, from_u + from_v)`.

```java
class Solution {
    public int reachableNodes(int[][] edges, int maxMoves, int n) {
        // Build adjacency list
        List<int[]>[] graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int[] e : edges) {
            graph[e[0]].add(new int[]{e[1], e[2]});
            graph[e[1]].add(new int[]{e[0], e[2]});
        }

        // Dijkstra from node 0
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[0] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, 0});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0], node = curr[1];
            if (d > dist[node]) continue;
            for (int[] edge : graph[node]) {
                int next = edge[0], cnt = edge[1];
                // Cost to traverse this edge = cnt+1 steps
                if (dist[node] + cnt + 1 < dist[next]) {
                    dist[next] = dist[node] + cnt + 1;
                    pq.offer(new int[]{dist[next], next});
                }
            }
        }

        int result = 0;

        // Count reachable original nodes
        for (int i = 0; i < n; i++) {
            if (dist[i] <= maxMoves) result++;
        }

        // Count reachable subdivided nodes on each edge
        for (int[] e : edges) {
            int u = e[0], v = e[1], cnt = e[2];
            int fromU = dist[u] <= maxMoves ? Math.min(cnt, maxMoves - dist[u]) : 0;
            int fromV = dist[v] <= maxMoves ? Math.min(cnt, maxMoves - dist[v]) : 0;
            result += Math.min(cnt, fromU + fromV);
        }

        return result;
    }
}
```

**Complexity:** Time O((V+E) log V), Space O(V+E)

---

#### LC 1334 — Find the City With Smallest Number of Neighbors at a Threshold Distance

**Companies:** Amazon, Google
**Difficulty:** Medium
**Pattern:** Floyd-Warshall all-pairs shortest path

**Problem:** Find the city with the fewest number of cities reachable within `distanceThreshold`. Ties broken by largest city index.

**Key insight:** All-pairs shortest path. Floyd-Warshall is O(n^3) and straightforward. With n <= 100, it's fast enough.

```java
class Solution {
    public int findTheCity(int n, int[][] edges, int distanceThreshold) {
        int INF = Integer.MAX_VALUE / 2; // avoid overflow in addition
        int[][] dist = new int[n][n];

        // Initialize
        for (int[] row : dist) Arrays.fill(row, INF);
        for (int i = 0; i < n; i++) dist[i][i] = 0;
        for (int[] e : edges) {
            dist[e[0]][e[1]] = e[2];
            dist[e[1]][e[0]] = e[2]; // undirected
        }

        // Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        int resultCity = -1, minNeighbors = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (i != j && dist[i][j] <= distanceThreshold) count++;
            }
            // Use >= to prefer larger index on tie
            if (count <= minNeighbors) {
                minNeighbors = count;
                resultCity = i;
            }
        }

        return resultCity;
    }
}
```

**Complexity:** Time O(n^3), Space O(n^2)

**Why not Dijkstra here?** You need all-pairs, not single-source. Running Dijkstra n times gives O(n * (V+E) log V). For dense graphs, Floyd-Warshall's O(n^3) is comparable and simpler to code.

---

## Common Mistakes

### 1. Using Dijkstra with Negative Weights

```java
// WRONG: Dijkstra on graph with negative edges
// The greedy argument breaks: a node settled from the heap might later
// be reachable via a cheaper path through a negative edge.

// Example where Dijkstra fails:
// Nodes: 0, 1, 2
// Edges: 0->1 (weight 1), 0->2 (weight 3), 1->2 (weight -3)
// Dijkstra settles node 1 first (dist=1), then node 2 (dist=3).
// But the true shortest path to 2 is 0->1->2 = 1 + (-3) = -2.
// Dijkstra misses this because it settled node 2 before processing node 1's negative edge.

// FIX: Use Bellman-Ford for graphs with negative weights.
```

### 2. Not Using Lazy Deletion (Performance Issue)

```java
// Without lazy deletion, stale heap entries are processed:
while (!pq.isEmpty()) {
    int[] curr = pq.poll();
    int d = curr[0], node = curr[1];

    // MISSING: if (d > dist[node]) continue;

    for (int[] edge : graph[node]) {
        // This processes the same node multiple times with stale distances
        // Still correct, but O(E log E) instead of O((V+E) log V)
    }
}

// With lazy deletion (correct and efficient):
while (!pq.isEmpty()) {
    int[] curr = pq.poll();
    int d = curr[0], node = curr[1];

    if (d > dist[node]) continue; // skip stale entries

    for (int[] edge : graph[node]) {
        // Each node processed at most once
    }
}
```

### 3. Integer Overflow with MAX_VALUE

```java
// WRONG: overflow when adding to Integer.MAX_VALUE
int[] dist = new int[n];
Arrays.fill(dist, Integer.MAX_VALUE);

// Later:
if (dist[node] + weight < dist[next]) { // OVERFLOW if dist[node] == MAX_VALUE
    dist[next] = dist[node] + weight;
}

// FIX option 1: guard before adding
if (dist[node] != Integer.MAX_VALUE && dist[node] + weight < dist[next]) {
    dist[next] = dist[node] + weight;
}

// FIX option 2: use a safe large value instead of MAX_VALUE
Arrays.fill(dist, (int) 1e9); // 10^9 is large enough for most problems
// Then dist[node] + weight won't overflow int range
```

### 4. Forgetting to Initialize dist[source] = 0

```java
// WRONG: source not initialized
int[] dist = new int[n];
Arrays.fill(dist, Integer.MAX_VALUE);
// dist[src] is still MAX_VALUE!
pq.offer(new int[]{0, src}); // pushed with distance 0, but dist[src] = MAX_VALUE

// When we poll (0, src) and check neighbors:
// dist[src] + weight = MAX_VALUE + weight = overflow!

// FIX: always set dist[src] = 0 before the loop
dist[src] = 0;
pq.offer(new int[]{0, src});
```

### 5. Wrong Comparator for PriorityQueue

```java
// WRONG: max-heap when you need min-heap
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> b[0] - a[0]); // max-heap!
// Dijkstra needs min-heap to always process the closest node first

// CORRECT: min-heap
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);

// SAFER: use Integer.compare to avoid overflow
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
```

### 6. Not Cloning dist Array in Bellman-Ford K-Stops Variant

```java
// WRONG: modifying dist in-place during a round
for (int i = 0; i <= k; i++) {
    for (int[] flight : flights) {
        int u = flight[0], v = flight[1], price = flight[2];
        if (dist[u] != Integer.MAX_VALUE && dist[u] + price < dist[v]) {
            dist[v] = dist[u] + price; // WRONG: uses updated dist[u] from same round
        }
    }
}
// This allows chaining A->B->C in one round, using 2 edges instead of 1.
// The path might use more than k+1 edges.

// CORRECT: clone before each round
for (int i = 0; i <= k; i++) {
    int[] temp = dist.clone();
    for (int[] flight : flights) {
        int u = flight[0], v = flight[1], price = flight[2];
        if (dist[u] != Integer.MAX_VALUE && dist[u] + price < temp[v]) {
            temp[v] = dist[u] + price;
        }
    }
    dist = temp;
}
```

### 7. Treating Directed Graph as Undirected (or Vice Versa)

```java
// For directed graphs (like LC 743 Network Delay Time):
graph[e[0]].add(new int[]{e[1], e[2]}); // only one direction

// For undirected graphs (like LC 1631 Minimum Effort):
graph[e[0]].add(new int[]{e[1], e[2]});
graph[e[1]].add(new int[]{e[0], e[2]}); // both directions

// Read the problem carefully. "Network" usually means directed.
// "Path between two points" usually means undirected.
```

---

## Algorithm Comparison

### Decision Table: Which Algorithm to Use

```
Condition                                    Algorithm
----------------------------------------------------------
Unweighted graph                             BFS
Weighted, all non-negative weights           Dijkstra
Weighted, negative weights possible          Bellman-Ford
Need to detect negative cycles               Bellman-Ford
All-pairs shortest path                      Floyd-Warshall
Constrained path (k stops, fuel, etc.)       Modified Dijkstra or BFS with state
Maximize probability / minimize max          Modified Dijkstra (flip comparator)
Heuristic available (game pathfinding)       A* (Dijkstra + heuristic)
Dense graph, small n (n <= 100-200)          Floyd-Warshall or Dijkstra with matrix
Sparse graph, large n                        Dijkstra with adjacency list
```

### BFS vs Dijkstra vs Bellman-Ford

| Property | BFS | Dijkstra | Bellman-Ford |
|---|---|---|---|
| Graph type | Unweighted | Weighted, non-negative | Weighted, any |
| Time complexity | O(V+E) | O((V+E) log V) | O(V*E) |
| Space complexity | O(V) | O(V+E) | O(V) |
| Negative weights | No | No | Yes |
| Negative cycle detection | No | No | Yes |
| Data structure | Queue (FIFO) | Priority Queue (min-heap) | Edge list |
| Greedy? | Yes (by level) | Yes (by distance) | No (exhaustive) |
| Early termination | Yes | Yes | No (must run V-1 rounds) |

### Dijkstra vs A*

Both use a priority queue. The difference is what they prioritize:

- **Dijkstra:** Priority = `dist[node]` (actual cost from source). Explores in all directions equally.
- **A*:** Priority = `dist[node] + heuristic(node, target)`. Explores toward the target first.

A* is faster in practice when a good heuristic exists (e.g., Euclidean distance for grid problems). But it requires the heuristic to be admissible (never overestimates). In interviews, Dijkstra is almost always sufficient. A* is mentioned in system design or game dev contexts.

### Connection to Topic 12 (Heaps)

Dijkstra's correctness depends entirely on the min-heap property. The algorithm's invariant is: "when we poll a node from the heap, we've found its true shortest distance." This holds because:

1. The heap always gives us the minimum-distance node.
2. All edge weights are non-negative, so no future path can improve on the current minimum.

If you replace the min-heap with a max-heap, you get the longest path (in a DAG). If you replace it with a FIFO queue, you get BFS (unweighted shortest path). The heap is the only thing that changes between these algorithms.

From Topic 12: `PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0])` creates a min-heap ordered by the first element. This is the exact comparator used in every Dijkstra implementation.

### Connection to Topic 14 (Graph Traversal)

BFS from Topic 14 is the foundation. Dijkstra is BFS with one change: the queue becomes a priority queue. The visited/settled set logic is the same. The graph representation (adjacency list) is the same. The relaxation step is new.

```
BFS:       Queue<Integer>  — process in arrival order
Dijkstra:  PriorityQueue   — process in distance order
```

Everything else is identical. If you understand BFS, you understand Dijkstra's structure. The priority queue is the only conceptual addition.

---

## Quick Reference Cheat Sheet

### Dijkstra Template (Memorize This)

```java
// Dijkstra — single source shortest path, non-negative weights
// graph[u] = list of {v, weight}
int[] dijkstra(int n, List<int[]>[] graph, int src) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;

    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
    pq.offer(new int[]{0, src});

    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int d = curr[0], node = curr[1];
        if (d > dist[node]) continue;          // lazy deletion
        for (int[] edge : graph[node]) {
            int next = edge[0], w = edge[1];
            if (dist[node] + w < dist[next]) {
                dist[next] = dist[node] + w;
                pq.offer(new int[]{dist[next], next});
            }
        }
    }
    return dist;
}
```

### Bellman-Ford Template (K-Stops Variant)

```java
// Bellman-Ford — k+1 rounds for "at most k stops"
int[] bellmanFordKStops(int n, int[][] edges, int src, int k) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;

    for (int i = 0; i <= k; i++) {
        int[] temp = dist.clone();             // snapshot — prevent chaining
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + w < temp[v]) {
                temp[v] = dist[u] + w;
            }
        }
        dist = temp;
    }
    return dist;
}
```

### Floyd-Warshall Template

```java
// Floyd-Warshall — all-pairs shortest path
// dist[i][j] initialized from edges, dist[i][i] = 0, others = INF/2
void floydWarshall(int[][] dist, int n) {
    for (int k = 0; k < n; k++)
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
}
```

### Key Patterns at a Glance

```
Standard Dijkstra:
  dist[node] = min cost to reach node
  Relax: if dist[u] + w < dist[v], update dist[v]

Minimax Dijkstra (LC 778, 1631):
  dist[node] = min possible maximum along path
  Relax: newCost = max(dist[u], edge_cost); if newCost < dist[v], update

Max probability Dijkstra (LC 1514):
  prob[node] = max probability to reach node
  Relax: newProb = prob[u] * edge_prob; if newProb > prob[v], update
  Use max-heap

Constrained Dijkstra (LC 787):
  State = (cost, node, constraint)
  dist[node][constraint] = min cost with this constraint value
  Or use Bellman-Ford with exactly k rounds

Graph as equation (LC 399):
  BFS/DFS on weighted graph
  Accumulate product along path
```

### Complexity Summary

```
Algorithm       Time              Space    Negative weights?
Dijkstra        O((V+E) log V)    O(V+E)   No
Bellman-Ford    O(V*E)            O(V)     Yes
Floyd-Warshall  O(V^3)            O(V^2)   Yes
BFS             O(V+E)            O(V)     Unweighted only
```

### Common Pitfalls Checklist

Before submitting any shortest path solution:

- [ ] `dist[src] = 0` before the loop
- [ ] Lazy deletion: `if (d > dist[node]) continue;`
- [ ] Overflow guard: `dist[node] != Integer.MAX_VALUE` before adding
- [ ] Correct graph direction (directed vs undirected)
- [ ] Bellman-Ford K-stops: `temp = dist.clone()` before each round
- [ ] Correct comparator: min-heap for Dijkstra, max-heap for max-probability

---

## Practice Roadmap

### Week 1: Foundation

**Day 1-2: Dijkstra's core**
- Read this document's Core Concept section
- Implement Dijkstra from scratch without looking at the template
- LC 743 (Network Delay Time) — trace through the dry run above, then code it
- LC 1514 (Path with Maximum Probability) — practice flipping the comparator

**Day 3-4: Grid Dijkstra**
- LC 1631 (Path With Minimum Effort) — minimax pattern
- LC 778 (Swim in Rising Water) — same minimax, harder constraints
- Notice: both use `max(currentCost, edgeCost)` as the relaxation

**Day 5: Bellman-Ford**
- Implement standard Bellman-Ford
- LC 787 (Cheapest Flights Within K Stops) — the K-stops variant
- Understand why `dist.clone()` is necessary

**Day 6-7: Review and consolidate**
- Re-implement Dijkstra and Bellman-Ford from memory
- LC 505 (The Maze II) — rolling ball mechanics
- Review the decision table: when to use which algorithm

### Week 2: Advanced Problems

**Day 8-9: Graph as equation**
- LC 399 (Evaluate Division) — weighted graph of ratios
- LC 332 (Reconstruct Itinerary) — Eulerian path, Hierholzer's algorithm

**Day 10-11: Advanced Dijkstra**
- LC 882 (Reachable Nodes In Subdivided Graph) — conceptual graph
- LC 1334 (Find the City) — Floyd-Warshall

**Day 12-13: Mixed practice**
- Pick 2-3 problems from the list and solve without hints
- Time yourself: Dijkstra problems should take 15-20 minutes
- Focus on getting the template right before optimizing

**Day 14: Interview simulation**
- Solve LC 743 in 15 minutes (it's the most common Dijkstra problem)
- Solve LC 787 in 20 minutes
- Practice explaining the lazy deletion pattern out loud

### Problem Priority for FAANG Interviews

**Must solve (appear most frequently):**
1. LC 743 — Network Delay Time (standard Dijkstra, asked at all 5 companies)
2. LC 787 — Cheapest Flights Within K Stops (constrained, asked at all 5 companies)
3. LC 1631 — Path With Minimum Effort (grid Dijkstra, Amazon/Google/Microsoft)
4. LC 399 — Evaluate Division (graph as equation, all 5 companies)

**High value:**
5. LC 1514 — Path with Maximum Probability (modified Dijkstra)
6. LC 778 — Swim in Rising Water (minimax)
7. LC 332 — Reconstruct Itinerary (Eulerian path)

**Good to know:**
8. LC 505 — The Maze II (rolling ball)
9. LC 1334 — Find the City (Floyd-Warshall)
10. LC 882 — Reachable Nodes (advanced)

### Interview Tips

**When the interviewer says "shortest path":**
1. Ask: weighted or unweighted?
2. Ask: any negative weights?
3. Ask: single source or all pairs?
4. Ask: any constraints (k stops, fuel, etc.)?

These four questions determine your algorithm choice before you write a single line.

**Explaining Dijkstra in an interview:**
"I'll use Dijkstra's algorithm. It's essentially BFS but with a priority queue instead of a regular queue. I initialize all distances to infinity except the source which is zero. I always process the node with the smallest known distance first. When I process a node, I check if I've found a shorter path to any of its neighbors. The lazy deletion pattern handles stale heap entries efficiently."

**Common follow-up questions:**
- "Why does Dijkstra fail with negative weights?" — The greedy argument breaks. A settled node might be reachable via a cheaper path through a negative edge discovered later.
- "What's the time complexity?" — O((V+E) log V) with a binary heap. The log V factor comes from heap operations.
- "How would you handle negative weights?" — Bellman-Ford. Run V-1 rounds of edge relaxation.
- "What if you need the actual path, not just the distance?" — Track a `prev[]` array. When you update `dist[next]`, set `prev[next] = node`. Reconstruct by following prev from destination to source.

### Path Reconstruction (Bonus)

Most problems only ask for the distance. But if asked for the actual path:

```java
// Add to Dijkstra: track previous node
int[] prev = new int[n];
Arrays.fill(prev, -1);

// In the relaxation step:
if (dist[node] + weight < dist[next]) {
    dist[next] = dist[node] + weight;
    prev[next] = node;  // track where we came from
    pq.offer(new int[]{dist[next], next});
}

// Reconstruct path from src to dst:
List<Integer> path = new ArrayList<>();
for (int at = dst; at != -1; at = prev[at]) {
    path.add(at);
}
Collections.reverse(path);
// path now contains src -> ... -> dst
```

### Extended Dry Run: LC 743 with a More Complex Graph

Let's trace Dijkstra on a graph where lazy deletion actually fires, so you can see it in action.

```
Graph (directed, 1-indexed, 5 nodes):
  1 -> 2 (weight 2)
  1 -> 3 (weight 4)
  2 -> 3 (weight 1)
  2 -> 4 (weight 7)
  3 -> 4 (weight 3)
  3 -> 5 (weight 5)
  4 -> 5 (weight 1)

Source: node 1
```

```
Initial:
  dist = [INF, 0, INF, INF, INF, INF]   (index 0 unused)
  heap = [(0, 1)]

--- Poll (0, 1) ---
  d=0, node=1. Lazy check: 0 == dist[1]=0. Proceed.
  Neighbors:
    1->2 w=2: 0+2=2 < INF => dist[2]=2, push (2,2)
    1->3 w=4: 0+4=4 < INF => dist[3]=4, push (4,3)
  dist = [INF, 0, 2, 4, INF, INF]
  heap = [(2,2), (4,3)]

--- Poll (2, 2) ---
  d=2, node=2. Lazy check: 2 == dist[2]=2. Proceed.
  Neighbors:
    2->3 w=1: 2+1=3 < dist[3]=4 => dist[3]=3, push (3,3)
    2->4 w=7: 2+7=9 < INF => dist[4]=9, push (9,4)
  dist = [INF, 0, 2, 3, 9, INF]
  heap = [(3,3), (4,3), (9,4)]
  Note: (4,3) is now stale — dist[3] was updated to 3.

--- Poll (3, 3) ---
  d=3, node=3. Lazy check: 3 == dist[3]=3. Proceed.
  Neighbors:
    3->4 w=3: 3+3=6 < dist[4]=9 => dist[4]=6, push (6,4)
    3->5 w=5: 3+5=8 < INF => dist[5]=8, push (8,5)
  dist = [INF, 0, 2, 3, 6, 8]
  heap = [(4,3), (6,4), (8,5), (9,4)]
  Note: (4,3) and (9,4) are both stale.

--- Poll (4, 3) ---
  d=4, node=3. Lazy check: 4 > dist[3]=3. SKIP (lazy deletion fires!)
  heap = [(6,4), (8,5), (9,4)]

--- Poll (6, 4) ---
  d=6, node=4. Lazy check: 6 == dist[4]=6. Proceed.
  Neighbors:
    4->5 w=1: 6+1=7 < dist[5]=8 => dist[5]=7, push (7,5)
  dist = [INF, 0, 2, 3, 6, 7]
  heap = [(7,5), (8,5), (9,4)]
  Note: (8,5) is now stale.

--- Poll (7, 5) ---
  d=7, node=5. Lazy check: 7 == dist[5]=7. Proceed.
  Neighbors: (none)
  heap = [(8,5), (9,4)]

--- Poll (8, 5) ---
  d=8, node=5. Lazy check: 8 > dist[5]=7. SKIP (lazy deletion fires again!)
  heap = [(9,4)]

--- Poll (9, 4) ---
  d=9, node=4. Lazy check: 9 > dist[4]=6. SKIP (lazy deletion fires!)
  heap = []

Done.
dist[1..5] = [0, 2, 3, 6, 7]
Answer for LC 743 = max(0, 2, 3, 6, 7) = 7
```

This trace shows lazy deletion firing three times: for stale entries (4,3), (8,5), and (9,4). Without lazy deletion, those entries would be processed unnecessarily. The algorithm still produces the correct answer either way, but lazy deletion avoids redundant work.

---

### Dijkstra Correctness Proof (Intuition)

Understanding *why* Dijkstra is correct helps you remember it and explain it in interviews.

**Claim:** When a node is polled from the min-heap, `dist[node]` is its true shortest distance.

**Proof by induction:**

*Base case:* The source node is polled first with distance 0. This is trivially correct.

*Inductive step:* Assume all previously polled nodes have their true shortest distances. When we poll node `u` with distance `d`:

- `d = dist[u]` (we skip stale entries via lazy deletion).
- Any path to `u` that goes through an unpolled node `v` has cost >= `dist[v]` >= `d` (because `v` is in the heap with distance >= `d`, otherwise it would have been polled before `u`).
- Since all edge weights are non-negative, extending any path through `v` can only increase the cost.
- Therefore, no unprocessed path can improve on `d`.

**Why this breaks with negative weights:** If edge (v, u) has negative weight, then a path through `v` could have cost `dist[v] + negative_weight < d`, even though `dist[v] >= d`. The non-negativity assumption is essential.

---

### Building the Graph: Common Patterns

Different problems give you the graph in different formats. Here's how to handle each:

**Format 1: Edge list `int[][] edges` where `edges[i] = {u, v, weight}`**

```java
// Directed graph
List<int[]>[] graph = new List[n];
for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
for (int[] e : edges) {
    graph[e[0]].add(new int[]{e[1], e[2]});
}

// Undirected graph — add both directions
for (int[] e : edges) {
    graph[e[0]].add(new int[]{e[1], e[2]});
    graph[e[1]].add(new int[]{e[0], e[2]});
}
```

**Format 2: Grid `int[][] grid` where movement cost = cell value or height difference**

```java
// Grid as graph — 4-directional movement
int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
// Neighbors of (r, c):
for (int[] d : dirs) {
    int nr = r + d[0], nc = c + d[1];
    if (nr >= 0 && nr < m && nc >= 0 && nc < n) {
        int weight = Math.abs(grid[r][c] - grid[nr][nc]); // or grid[nr][nc], etc.
        // add to heap as (weight, nr, nc)
    }
}
```

**Format 3: Implicit graph (generate neighbors on the fly)**

```java
// Example: word ladder, state machine
// No explicit adjacency list — generate neighbors during BFS/Dijkstra
while (!pq.isEmpty()) {
    int[] curr = pq.poll();
    // Generate neighbors based on problem rules
    for (String next : generateNeighbors(curr)) {
        int cost = computeCost(curr, next);
        if (cost < dist.getOrDefault(next, Integer.MAX_VALUE)) {
            dist.put(next, cost);
            pq.offer(new int[]{cost, next}); // adapt for string keys
        }
    }
}
```

**Format 4: Adjacency matrix `int[][] matrix` where `matrix[i][j] = weight` (0 = no edge)**

```java
// Convert to adjacency list for Dijkstra
List<int[]>[] graph = new List[n];
for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
        if (matrix[i][j] != 0) {
            graph[i].add(new int[]{j, matrix[i][j]});
        }
    }
}
```

---

### Dijkstra with HashMap for String Nodes

Some problems (like LC 399) use string node identifiers. Adapt the template:

```java
// Dijkstra with string nodes
Map<String, Integer> dist = new HashMap<>();
PriorityQueue<Object[]> pq = new PriorityQueue<>((a, b) -> (int)a[0] - (int)b[0]);

dist.put(src, 0);
pq.offer(new Object[]{0, src});

while (!pq.isEmpty()) {
    Object[] curr = pq.poll();
    int d = (int) curr[0];
    String node = (String) curr[1];

    if (d > dist.getOrDefault(node, Integer.MAX_VALUE)) continue;

    for (Map.Entry<String, Integer> entry : graph.getOrDefault(node, new HashMap<>()).entrySet()) {
        String next = entry.getKey();
        int weight = entry.getValue();
        int newDist = d + weight;
        if (newDist < dist.getOrDefault(next, Integer.MAX_VALUE)) {
            dist.put(next, newDist);
            pq.offer(new Object[]{newDist, next});
        }
    }
}
```

The structure is identical. Only the node type changes from `int` to `String`, and `int[]` becomes `Object[]` (or use a custom class).

---

### Negative Weight Edges: Why Dijkstra Fails (Concrete Example)

```
Graph:
  0 -> 1 (weight 4)
  0 -> 2 (weight 1)
  2 -> 1 (weight -3)

True shortest paths from 0:
  dist[0] = 0
  dist[1] = min(4, 1 + (-3)) = min(4, -2) = -2
  dist[2] = 1

Dijkstra trace:
  Initial: dist = [0, INF, INF], heap = [(0,0)]

  Poll (0, 0):
    0->1 w=4: dist[1]=4, push (4,1)
    0->2 w=1: dist[2]=1, push (1,2)
    heap = [(1,2), (4,1)]

  Poll (1, 2):
    2->1 w=-3: dist[2]+(-3) = 1+(-3) = -2 < dist[1]=4
    dist[1] = -2, push (-2, 1)
    heap = [(-2,1), (4,1)]

  Poll (-2, 1):
    No outgoing edges from 1.
    heap = [(4,1)]

  Poll (4, 1):
    d=4 > dist[1]=-2. SKIP (lazy deletion).
    heap = []

  Final dist = [0, -2, 1]
```

Wait — in this case Dijkstra actually gets the right answer! That's because the negative edge (2->1) was discovered before node 1 was settled. Dijkstra can sometimes work with negative edges by accident.

**The case where it truly fails:**

```
Graph:
  0 -> 1 (weight 2)
  0 -> 2 (weight 5)
  1 -> 2 (weight -10)

True shortest path from 0 to 2: 0->1->2 = 2 + (-10) = -8

Dijkstra trace:
  Initial: dist = [0, INF, INF], heap = [(0,0)]

  Poll (0, 0):
    0->1 w=2: dist[1]=2, push (2,1)
    0->2 w=5: dist[2]=5, push (5,2)
    heap = [(2,1), (5,2)]

  Poll (2, 1):
    1->2 w=-10: dist[1]+(-10) = 2+(-10) = -8 < dist[2]=5
    dist[2] = -8, push (-8, 2)
    heap = [(-8,2), (5,2)]

  Poll (-8, 2):
    No outgoing edges.
    heap = [(5,2)]

  Poll (5, 2):
    d=5 > dist[2]=-8. SKIP.

  Final dist = [0, 2, -8]  -- CORRECT in this case too!
```

Hmm, Dijkstra still gets it right here. The real failure case requires the negative edge to be discovered *after* the destination is settled:

```
Graph:
  0 -> 2 (weight 1)
  0 -> 1 (weight 3)
  1 -> 2 (weight -5)

True shortest path from 0 to 2: 0->1->2 = 3 + (-5) = -2

Dijkstra trace:
  Initial: dist = [0, INF, INF], heap = [(0,0)]

  Poll (0, 0):
    0->2 w=1: dist[2]=1, push (1,2)
    0->1 w=3: dist[1]=3, push (3,1)
    heap = [(1,2), (3,1)]

  Poll (1, 2):
    No outgoing edges from 2.
    heap = [(3,1)]

  Poll (3, 1):
    1->2 w=-5: dist[1]+(-5) = 3+(-5) = -2 < dist[2]=1
    dist[2] = -2, push (-2, 2)
    heap = [(-2,2)]

  Poll (-2, 2):
    d=-2 == dist[2]=-2. Proceed. No outgoing edges.

  Final dist = [0, 3, -2]  -- CORRECT again!
```

The truth is: Dijkstra with lazy deletion *can* handle some negative edges correctly, because lazy deletion allows re-processing. But it's not guaranteed. The standard Dijkstra (with a visited/settled set that prevents re-processing) will fail. In interviews, always say "Dijkstra requires non-negative weights" — it's the safe, correct answer.

**The definitive failure case (with settled set):**

```java
// Dijkstra WITH settled set (common textbook version)
Set<Integer> settled = new HashSet<>();
while (!pq.isEmpty()) {
    int[] curr = pq.poll();
    int d = curr[0], node = curr[1];
    if (settled.contains(node)) continue; // settled set instead of lazy deletion
    settled.add(node);
    // ... process neighbors
}

// On the graph: 0->2 (w=1), 0->1 (w=3), 1->2 (w=-5)
// Node 2 gets settled with dist=1 before node 1 is processed.
// When node 1 is processed, it tries to update dist[2] to -2,
// but node 2 is already settled — the update is ignored.
// Final dist[2] = 1, but true answer is -2. WRONG.
```

This is why the settled-set version of Dijkstra is strictly incorrect with negative weights, while the lazy-deletion version might accidentally work in some cases. Use Bellman-Ford when negative weights are possible.

---

### Comparing Dijkstra Implementations: Lazy Deletion vs Settled Set

Two common implementations of Dijkstra:

**Version 1: Lazy deletion (recommended for interviews)**

```java
// Lazy deletion — no separate visited set
while (!pq.isEmpty()) {
    int[] curr = pq.poll();
    int d = curr[0], node = curr[1];
    if (d > dist[node]) continue; // skip stale entries
    for (int[] edge : graph[node]) {
        // relax edges
    }
}
```

Pros: Simpler code, handles re-processing naturally, works correctly with the standard Dijkstra guarantee.
Cons: Heap may contain O(E) entries instead of O(V).

**Version 2: Settled set**

```java
// Settled set — explicit visited tracking
boolean[] settled = new boolean[n];
while (!pq.isEmpty()) {
    int[] curr = pq.poll();
    int d = curr[0], node = curr[1];
    if (settled[node]) continue;
    settled[node] = true;
    for (int[] edge : graph[node]) {
        // relax edges
    }
}
```

Pros: Each node processed exactly once, slightly cleaner semantics.
Cons: Fails with negative weights (as shown above). Slightly more code.

**In interviews:** Use lazy deletion. It's what most competitive programmers use, it's what LeetCode solutions use, and it's what interviewers expect. The settled-set version is more common in textbooks but less practical.

---

*Next: Topic 17 — Minimum Spanning Trees (Prim's, Kruskal's, Union-Find applications)*

*Previous: Topic 15 — Topological Sort*

*Related: Topic 12 (Heaps & Priority Queues) — the data structure powering Dijkstra. Topic 14 (Graph Traversal) — BFS foundation that Dijkstra extends.*
