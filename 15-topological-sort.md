# Topic 15: Topological Sort

**Top Companies**: Google, Amazon, Meta, Microsoft, Bloomberg, Apple  
**Difficulty Range**: Medium to Hard  
**Prerequisites**: Topic 14 (Graph Traversal), BFS, DFS, HashMap  
**Document**: 15 of 20

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5](#eli5)
3. [Templates](#templates)
4. [Real-World Applications](#real-world-applications)
5. [Problems](#problems)
6. [Common Mistakes](#common-mistakes)
7. [Comparison](#comparison)
8. [Cheat Sheet + Roadmap](#cheat-sheet--roadmap)

---

## Core Concept

Topological sort orders the vertices of a **Directed Acyclic Graph (DAG)** such that for every directed edge `u → v`, vertex `u` appears before vertex `v` in the ordering.

The key constraint: **the graph must be a DAG**. If a cycle exists, no valid topological ordering is possible because you'd need A before B, B before C, and C before A simultaneously.

### Why DAG?

```
Valid DAG:          Cycle (invalid):
A → B → D          A → B
A → C → D          B → C
                   C → A  ← cycle! no valid order
```

### Two Algorithms

**Kahn's Algorithm (BFS-based)**

Works by repeatedly removing nodes with no incoming edges (in-degree = 0).

Steps:
1. Compute in-degree for every node
2. Add all nodes with in-degree 0 to a queue
3. While queue is not empty:
   - Dequeue node `u`, add to result
   - For each neighbor `v` of `u`: decrement `inDegree[v]`
   - If `inDegree[v]` becomes 0, enqueue `v`
4. If result size != total nodes, a cycle exists

**DFS-based (Reverse Post-Order)**

Works by doing DFS and pushing nodes to a stack *after* all their descendants are processed.

Steps:
1. For each unvisited node, run DFS
2. After visiting all neighbors of node `u`, push `u` to stack
3. Reverse the stack = topological order
4. Cycle detection: if DFS encounters a node currently being processed (GRAY), a cycle exists

### Cycle Detection

**Kahn's**: If the result list doesn't contain all `n` nodes after processing, a cycle exists. Simple and clean.

**DFS**: Use three colors:
- `WHITE (0)`: not visited
- `GRAY (1)`: currently being processed (in recursion stack)
- `BLACK (2)`: fully processed

If DFS visits a GRAY node, you've found a back edge = cycle.

### Complexity

| Algorithm | Time | Space |
|-----------|------|-------|
| Kahn's    | O(V + E) | O(V + E) |
| DFS-based | O(V + E) | O(V + E) |

Both are optimal. The difference is in what they're better suited for.

---

## ELI5

You're deciding what order to take college courses. Some courses have prerequisites. You can only take a course if all its prerequisites are done.

For example:
- Math 101 has no prerequisites
- Physics 201 requires Math 101
- Engineering 301 requires Physics 201 and Math 101

A valid order: Math 101 → Physics 201 → Engineering 301.

Topological sort gives you *a* valid order (there may be many). If there's a circular dependency (A requires B, B requires A), no valid order exists. That's a cycle, and topological sort detects it.

Kahn's algorithm is like: "Who can start right now? (no prerequisites left). Let them go. Now who can start? Repeat."

DFS is like: "Follow a chain all the way to the end, then work backwards."

---

## Templates

### Template 1: Kahn's Algorithm (BFS-based)

```java
import java.util.*;

public class KahnsTopologicalSort {
    
    /**
     * Returns topological order of nodes 0..n-1.
     * Returns empty list if cycle detected.
     * 
     * @param n number of nodes
     * @param edges list of [u, v] meaning u -> v
     */
    public List<Integer> topologicalSort(int n, int[][] edges) {
        // Step 1: Build adjacency list and compute in-degrees
        List<List<Integer>> adj = new ArrayList<>();
        int[] inDegree = new int[n];
        
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1];
            adj.get(u).add(v);
            inDegree[v]++;
        }
        
        // Step 2: Enqueue all nodes with in-degree 0
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }
        
        // Step 3: BFS - process nodes, decrement neighbors' in-degrees
        List<Integer> result = new ArrayList<>();
        
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);
            
            for (int neighbor : adj.get(node)) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        
        // Step 4: Cycle check
        if (result.size() != n) {
            return new ArrayList<>(); // cycle detected
        }
        
        return result;
    }
    
    // Variant: when nodes are labeled (not 0..n-1)
    public List<String> topologicalSortLabeled(
            Map<String, List<String>> adj,
            Set<String> allNodes) {
        
        Map<String, Integer> inDegree = new HashMap<>();
        for (String node : allNodes) {
            inDegree.put(node, 0);
        }
        
        for (String u : adj.keySet()) {
            for (String v : adj.get(u)) {
                inDegree.put(v, inDegree.get(v) + 1);
            }
        }
        
        Queue<String> queue = new LinkedList<>();
        for (String node : allNodes) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
            }
        }
        
        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String node = queue.poll();
            result.add(node);
            
            for (String neighbor : adj.getOrDefault(node, new ArrayList<>())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        
        if (result.size() != allNodes.size()) {
            return new ArrayList<>(); // cycle
        }
        
        return result;
    }
}
```

**Key points for Kahn's:**
- `inDegree[v]++` when you see edge `u → v`
- Start with all nodes where `inDegree == 0`
- After processing a node, decrement its neighbors' in-degrees
- Cycle check: `result.size() != n`

---

### Template 2: DFS-based Topological Sort

```java
import java.util.*;

public class DFSTopologicalSort {
    
    // Color constants
    private static final int WHITE = 0; // not visited
    private static final int GRAY  = 1; // in current DFS path (recursion stack)
    private static final int BLACK = 2; // fully processed
    
    private int[] color;
    private List<List<Integer>> adj;
    private Deque<Integer> stack;
    private boolean hasCycle;
    
    /**
     * Returns topological order. Returns empty list if cycle detected.
     */
    public List<Integer> topologicalSort(int n, int[][] edges) {
        adj = new ArrayList<>();
        color = new int[n];
        stack = new ArrayDeque<>();
        hasCycle = false;
        
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
        }
        
        // Arrays.fill(color, WHITE) -- already 0 by default
        
        for (int i = 0; i < n; i++) {
            if (color[i] == WHITE) {
                dfs(i);
                if (hasCycle) return new ArrayList<>();
            }
        }
        
        // Stack contains reverse post-order = topological order
        List<Integer> result = new ArrayList<>(stack);
        return result;
    }
    
    private void dfs(int node) {
        if (hasCycle) return;
        
        color[node] = GRAY; // mark as in-progress
        
        for (int neighbor : adj.get(node)) {
            if (color[neighbor] == GRAY) {
                // Back edge found: cycle!
                hasCycle = true;
                return;
            }
            if (color[neighbor] == WHITE) {
                dfs(neighbor);
            }
            // BLACK neighbors: already processed, skip
        }
        
        color[node] = BLACK; // fully processed
        stack.push(node);    // push AFTER all neighbors processed
    }
    
    // Iterative DFS version (avoids stack overflow for large graphs)
    public List<Integer> topologicalSortIterative(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] color = new int[n];
        Deque<Integer> result = new ArrayDeque<>();
        
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        
        // Track iterator position for each node
        int[] iterIdx = new int[n];
        Deque<Integer> callStack = new ArrayDeque<>();
        
        for (int start = 0; start < n; start++) {
            if (color[start] != WHITE) continue;
            
            callStack.push(start);
            color[start] = GRAY;
            
            while (!callStack.isEmpty()) {
                int node = callStack.peek();
                List<Integer> neighbors = adj.get(node);
                
                if (iterIdx[node] < neighbors.size()) {
                    int next = neighbors.get(iterIdx[node]++);
                    if (color[next] == GRAY) return new ArrayList<>(); // cycle
                    if (color[next] == WHITE) {
                        color[next] = GRAY;
                        callStack.push(next);
                    }
                } else {
                    // All neighbors processed
                    color[node] = BLACK;
                    callStack.pop();
                    result.push(node);
                }
            }
        }
        
        return new ArrayList<>(result);
    }
}
```

**Key points for DFS:**
- Push to stack *after* all neighbors are processed (post-order)
- GRAY = currently in recursion stack. GRAY → GRAY = cycle
- The stack naturally gives reverse post-order = topological order
- Handle disconnected components by looping over all nodes

---

### Template 3: Cycle Detection Only

```java
import java.util.*;

public class CycleDetection {
    
    // Kahn's-based cycle detection (cleaner for undirected-like problems)
    public boolean hasCycleKahns(int n, int[][] edges) {
        int[] inDegree = new int[n];
        List<List<Integer>> adj = new ArrayList<>();
        
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            inDegree[e[1]]++;
        }
        
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }
        
        int processed = 0;
        while (!queue.isEmpty()) {
            int node = queue.poll();
            processed++;
            for (int neighbor : adj.get(node)) {
                if (--inDegree[neighbor] == 0) queue.offer(neighbor);
            }
        }
        
        return processed != n; // true if cycle exists
    }
    
    // DFS-based cycle detection
    public boolean hasCycleDFS(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] color = new int[n]; // 0=WHITE, 1=GRAY, 2=BLACK
        
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        
        for (int i = 0; i < n; i++) {
            if (color[i] == 0 && dfsHasCycle(i, adj, color)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean dfsHasCycle(int node, List<List<Integer>> adj, int[] color) {
        color[node] = 1; // GRAY
        
        for (int neighbor : adj.get(node)) {
            if (color[neighbor] == 1) return true; // back edge
            if (color[neighbor] == 0 && dfsHasCycle(neighbor, adj, color)) {
                return true;
            }
        }
        
        color[node] = 2; // BLACK
        return false;
    }
    
    // For undirected graphs (different logic - track parent)
    public boolean hasCycleUndirected(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        boolean[] visited = new boolean[n];
        
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        
        for (int i = 0; i < n; i++) {
            if (!visited[i] && dfsUndirected(i, -1, adj, visited)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean dfsUndirected(int node, int parent, 
                                   List<List<Integer>> adj, boolean[] visited) {
        visited[node] = true;
        for (int neighbor : adj.get(node)) {
            if (!visited[neighbor]) {
                if (dfsUndirected(neighbor, node, adj, visited)) return true;
            } else if (neighbor != parent) {
                return true; // back edge (not to parent)
            }
        }
        return false;
    }
}
```

---

## Real-World Applications

### Build Systems

Make, Gradle, Maven all use topological sort to determine build order. If module A depends on module B and C, and B depends on C, the build order is: C → B → A. A cycle in dependencies (A depends on B, B depends on A) is a build error.

```
Maven dependency resolution:
  app → spring-web → spring-core
  app → spring-data → spring-core
  
  Topo order: spring-core → spring-web → spring-data → app
```

### Package Managers

npm, pip, cargo all resolve installation order using topological sort. When you run `npm install`, it builds a dependency graph and installs packages in topological order so dependencies are always installed before the packages that need them.

### Course Scheduling

Universities use this to validate curriculum. If CS 401 requires CS 301, and CS 301 requires CS 201, the valid enrollment sequence is CS 201 → CS 301 → CS 401. A circular prerequisite (A requires B, B requires A) is flagged as a catalog error.

### Spreadsheet Recalculation

Excel and Google Sheets recalculate cells in topological order. If cell C3 = A1 + B2, then A1 and B2 must be computed before C3. A circular reference (A1 depends on C3, C3 depends on A1) is an error.

### CI/CD Pipeline Ordering

GitHub Actions, Jenkins pipelines define job dependencies. "deploy" runs after "test", "test" runs after "build". The pipeline executor uses topological sort to determine which jobs can run in parallel and which must wait.

### Database Migration Ordering

Database migration tools (Flyway, Liquibase) apply migrations in topological order when migrations have dependencies. Migration V3 that adds a foreign key must run after V2 that creates the referenced table.

---

## Problems

### Category A: Course Schedule Problems

---

#### LC 207 - Course Schedule

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg, Apple  
**Difficulty**: Medium  
**Pattern**: Kahn's cycle detection

**Problem**: Given `numCourses` and `prerequisites` array where `[a, b]` means "to take course a, you must first take course b", return `true` if you can finish all courses.

**Translation**: Can you take all courses? = Does the prerequisite graph have a cycle?

**Step-by-Step Kahn's Trace**

Input: `numCourses = 4`, `prerequisites = [[1,0],[2,0],[3,1],[3,2]]`

This means:
- Course 1 requires Course 0
- Course 2 requires Course 0
- Course 3 requires Course 1
- Course 3 requires Course 2

**Graph (edges go from prerequisite to course):**
```
0 → 1
0 → 2
1 → 3
2 → 3
```

**Step 1: Compute in-degrees**
```
Node 0: in-degree = 0  (no prerequisites)
Node 1: in-degree = 1  (requires 0)
Node 2: in-degree = 1  (requires 0)
Node 3: in-degree = 2  (requires 1 and 2)
```

**Step 2: Initialize queue with in-degree 0 nodes**
```
Queue: [0]
Result: []
inDegree: [0, 1, 1, 2]
```

**Step 3: Process queue**

Iteration 1: Dequeue 0
```
Process node 0
  Neighbor 1: inDegree[1] = 1-1 = 0 → enqueue 1
  Neighbor 2: inDegree[2] = 1-1 = 0 → enqueue 2
Queue: [1, 2]
Result: [0]
inDegree: [0, 0, 0, 2]
```

Iteration 2: Dequeue 1
```
Process node 1
  Neighbor 3: inDegree[3] = 2-1 = 1 → not 0, don't enqueue
Queue: [2]
Result: [0, 1]
inDegree: [0, 0, 0, 1]
```

Iteration 3: Dequeue 2
```
Process node 2
  Neighbor 3: inDegree[3] = 1-1 = 0 → enqueue 3
Queue: [3]
Result: [0, 1, 2]
inDegree: [0, 0, 0, 0]
```

Iteration 4: Dequeue 3
```
Process node 3
  No neighbors
Queue: []
Result: [0, 1, 2, 3]
```

**Step 4: Check**
```
result.size() = 4 == numCourses = 4 → NO CYCLE → return true
```

**Now with a cycle**: `prerequisites = [[1,0],[0,1]]`
```
Graph: 0 → 1, 1 → 0
inDegree: [1, 1]
Queue: [] (no node with in-degree 0!)
Result: []
result.size() = 0 != 2 → CYCLE → return false
```

**Java Solution:**

```java
class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // Build adjacency list: prereq -> course
        List<List<Integer>> adj = new ArrayList<>();
        int[] inDegree = new int[numCourses];
        
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }
        
        // prerequisites[i] = [course, prereq]
        // Edge: prereq -> course
        for (int[] pre : prerequisites) {
            int course = pre[0], prereq = pre[1];
            adj.get(prereq).add(course);
            inDegree[course]++;
        }
        
        // Kahn's BFS
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }
        
        int processed = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            processed++;
            
            for (int next : adj.get(course)) {
                if (--inDegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }
        
        return processed == numCourses;
    }
}
```

**DFS Solution:**

```java
class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        
        for (int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]);
        }
        
        int[] color = new int[numCourses]; // 0=white, 1=gray, 2=black
        
        for (int i = 0; i < numCourses; i++) {
            if (color[i] == 0 && hasCycle(i, adj, color)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean hasCycle(int node, List<List<Integer>> adj, int[] color) {
        color[node] = 1; // gray: in progress
        
        for (int next : adj.get(node)) {
            if (color[next] == 1) return true;  // back edge = cycle
            if (color[next] == 0 && hasCycle(next, adj, color)) return true;
        }
        
        color[node] = 2; // black: done
        return false;
    }
}
```

**Complexity**: Time O(V + E), Space O(V + E)

---

#### LC 210 - Course Schedule II

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg, Apple  
**Difficulty**: Medium  
**Pattern**: Kahn's returning actual order

**Problem**: Same as 207 but return the actual order to take courses. Return empty array if impossible.

**Key difference from 207**: Store the order, not just count.

```java
class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] inDegree = new int[numCourses];
        
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        
        for (int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]);
            inDegree[pre[0]]++;
        }
        
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }
        
        int[] order = new int[numCourses];
        int idx = 0;
        
        while (!queue.isEmpty()) {
            int course = queue.poll();
            order[idx++] = course;
            
            for (int next : adj.get(course)) {
                if (--inDegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }
        
        // If we processed all courses, return order; else cycle exists
        return idx == numCourses ? order : new int[0];
    }
}
```

**DFS Solution:**

```java
class Solution {
    private int[] color;
    private List<List<Integer>> adj;
    private int[] result;
    private int idx;
    private boolean cycle;
    
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        adj = new ArrayList<>();
        color = new int[numCourses];
        result = new int[numCourses];
        idx = numCourses - 1; // fill from end (reverse post-order)
        cycle = false;
        
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        for (int[] pre : prerequisites) adj.get(pre[1]).add(pre[0]);
        
        for (int i = 0; i < numCourses; i++) {
            if (color[i] == 0) dfs(i);
            if (cycle) return new int[0];
        }
        
        return result;
    }
    
    private void dfs(int node) {
        if (cycle) return;
        color[node] = 1;
        
        for (int next : adj.get(node)) {
            if (color[next] == 1) { cycle = true; return; }
            if (color[next] == 0) dfs(next);
        }
        
        color[node] = 2;
        result[idx--] = node; // post-order: add after all descendants
    }
}
```

**Complexity**: Time O(V + E), Space O(V + E)

---

#### LC 1136 - Parallel Courses (Premium)

**Companies**: Amazon, Google, Microsoft  
**Difficulty**: Medium  
**Pattern**: Kahn's + level counting (BFS levels = semesters)

**Problem**: `n` courses, `relations[i] = [prevCourse, nextCourse]`. Each semester you can take any number of courses as long as prerequisites are met. Return minimum number of semesters to finish all courses. Return -1 if impossible.

**Key insight**: This is Kahn's BFS where each BFS level = one semester. Count the levels.

```java
class Solution {
    public int minimumSemesters(int n, int[][] relations) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] inDegree = new int[n + 1]; // 1-indexed
        
        for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
        
        for (int[] rel : relations) {
            adj.get(rel[0]).add(rel[1]);
            inDegree[rel[1]]++;
        }
        
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 1; i <= n; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }
        
        int semesters = 0;
        int coursesCompleted = 0;
        
        while (!queue.isEmpty()) {
            int size = queue.size(); // all courses this semester
            semesters++;
            
            for (int i = 0; i < size; i++) {
                int course = queue.poll();
                coursesCompleted++;
                
                for (int next : adj.get(course)) {
                    if (--inDegree[next] == 0) {
                        queue.offer(next);
                    }
                }
            }
        }
        
        return coursesCompleted == n ? semesters : -1;
    }
}
```

**Why level-by-level BFS?** Each BFS level represents courses that can be taken simultaneously (all their prerequisites are done). The number of levels = minimum semesters.

**Complexity**: Time O(V + E), Space O(V + E)

---

### Category B: Order Reconstruction

---

#### LC 269 - Alien Dictionary (Premium)

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg  
**Difficulty**: Hard  
**Pattern**: Build graph from word comparisons, then topological sort

**Problem**: Given a sorted list of words in an alien language, determine the order of characters in the alien alphabet. Return any valid ordering. Return "" if invalid (cycle or prefix violation).

**This is the hardest part: building the graph correctly.**

**Graph Construction Logic**

Compare adjacent words character by character. The first position where they differ tells you the ordering of those two characters.

**Example**: `words = ["wrt", "wrf", "er", "ett", "rftt"]`

Compare word[0] and word[1]: "wrt" vs "wrf"
```
w == w  (skip)
r == r  (skip)
t != f  → t comes before f in alien alphabet → edge: t → f
```

Compare word[1] and word[2]: "wrf" vs "er"
```
w != e  → w comes before e → edge: w → e
```

Compare word[2] and word[3]: "er" vs "ett"
```
e == e  (skip)
r != t  → r comes before t → edge: r → t
```

Compare word[3] and word[4]: "ett" vs "rftt"
```
e != r  → e comes before r → edge: e → r
```

**Graph built:**
```
t → f
w → e
r → t
e → r
```

**Topological sort of {w, r, t, f, e}:**
```
inDegree: w=0, r=1(from e), t=1(from r), f=1(from t), e=1(from w)
Queue: [w]

Process w: neighbor e → inDegree[e]=0 → enqueue e
Queue: [e], result: [w]

Process e: neighbor r → inDegree[r]=0 → enqueue r
Queue: [r], result: [w, e]

Process r: neighbor t → inDegree[t]=0 → enqueue t
Queue: [t], result: [w, e, r]

Process t: neighbor f → inDegree[f]=0 → enqueue f
Queue: [f], result: [w, e, r, t]

Process f: no neighbors
Queue: [], result: [w, e, r, t, f]
```

**Answer**: "wertf"

**Invalid case (prefix violation)**: `["abc", "ab"]`
- "abc" comes before "ab" in the list
- But "ab" is a prefix of "abc"
- This means "abc" < "ab" which is impossible in any valid ordering
- Return ""

**Java Solution:**

```java
class Solution {
    public String alienOrder(String[] words) {
        // Step 1: Collect all unique characters
        Map<Character, Set<Character>> adj = new HashMap<>();
        Map<Character, Integer> inDegree = new HashMap<>();
        
        // Initialize: every character has in-degree 0
        for (String word : words) {
            for (char c : word.toCharArray()) {
                adj.putIfAbsent(c, new HashSet<>());
                inDegree.putIfAbsent(c, 0);
            }
        }
        
        // Step 2: Build graph by comparing adjacent words
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i];
            String w2 = words[i + 1];
            int minLen = Math.min(w1.length(), w2.length());
            
            // Prefix violation check: "abc" before "ab" is invalid
            if (w1.length() > w2.length() && w1.startsWith(w2)) {
                return "";
            }
            
            // Find first differing character
            for (int j = 0; j < minLen; j++) {
                char c1 = w1.charAt(j);
                char c2 = w2.charAt(j);
                
                if (c1 != c2) {
                    // c1 comes before c2: edge c1 → c2
                    if (!adj.get(c1).contains(c2)) {
                        adj.get(c1).add(c2);
                        inDegree.put(c2, inDegree.get(c2) + 1);
                    }
                    break; // only first difference matters!
                }
            }
        }
        
        // Step 3: Kahn's topological sort
        Queue<Character> queue = new LinkedList<>();
        for (char c : inDegree.keySet()) {
            if (inDegree.get(c) == 0) queue.offer(c);
        }
        
        StringBuilder result = new StringBuilder();
        while (!queue.isEmpty()) {
            char c = queue.poll();
            result.append(c);
            
            for (char next : adj.get(c)) {
                inDegree.put(next, inDegree.get(next) - 1);
                if (inDegree.get(next) == 0) queue.offer(next);
            }
        }
        
        // Cycle check
        if (result.length() != inDegree.size()) return "";
        
        return result.toString();
    }
}
```

**Critical edge cases:**
1. Prefix violation: `["abc", "ab"]` → return ""
2. Duplicate adjacent words: `["abc", "abc"]` → no edge, valid
3. Single word: return all characters in any order
4. Characters that appear in words but have no ordering constraints: still include them

**DFS Solution:**

```java
class Solution {
    private Map<Character, List<Character>> adj;
    private Map<Character, Integer> color; // 0=white, 1=gray, 2=black
    private StringBuilder result;
    
    public String alienOrder(String[] words) {
        adj = new HashMap<>();
        color = new HashMap<>();
        result = new StringBuilder();
        
        for (String word : words) {
            for (char c : word.toCharArray()) {
                adj.putIfAbsent(c, new ArrayList<>());
                color.putIfAbsent(c, 0);
            }
        }
        
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i], w2 = words[i + 1];
            if (w1.length() > w2.length() && w1.startsWith(w2)) return "";
            
            int minLen = Math.min(w1.length(), w2.length());
            for (int j = 0; j < minLen; j++) {
                if (w1.charAt(j) != w2.charAt(j)) {
                    adj.get(w1.charAt(j)).add(w2.charAt(j));
                    break;
                }
            }
        }
        
        for (char c : adj.keySet()) {
            if (color.get(c) == 0 && dfs(c)) return "";
        }
        
        return result.reverse().toString();
    }
    
    private boolean dfs(char c) {
        color.put(c, 1);
        for (char next : adj.get(c)) {
            if (color.get(next) == 1) return true; // cycle
            if (color.get(next) == 0 && dfs(next)) return true;
        }
        color.put(c, 2);
        result.append(c); // post-order
        return false;
    }
}
```

**Complexity**: Time O(C) where C = total characters across all words, Space O(1) since at most 26 characters

---

#### LC 444 - Sequence Reconstruction (Premium)

**Companies**: Amazon, Google  
**Difficulty**: Medium  
**Pattern**: Verify unique topological order

**Problem**: Given `nums` (a permutation of 1..n) and `sequences` (subsequences), determine if `nums` is the only shortest supersequence that can be reconstructed from `sequences`.

**Key insight**: The topological order must be unique. This means at every step, the queue must have exactly one element (no ambiguity).

```java
class Solution {
    public boolean sequenceReconstruction(int[] nums, int[][] sequences) {
        int n = nums.length;
        Map<Integer, Set<Integer>> adj = new HashMap<>();
        int[] inDegree = new int[n + 1];
        Set<Integer> allNums = new HashSet<>();
        
        for (int num : nums) allNums.add(num);
        for (int i = 1; i <= n; i++) adj.put(i, new HashSet<>());
        
        for (int[] seq : sequences) {
            for (int num : seq) {
                if (num < 1 || num > n) return false;
            }
            for (int i = 0; i < seq.length - 1; i++) {
                int u = seq[i], v = seq[i + 1];
                if (!adj.get(u).contains(v)) {
                    adj.get(u).add(v);
                    inDegree[v]++;
                }
            }
        }
        
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 1; i <= n; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }
        
        int idx = 0;
        while (!queue.isEmpty()) {
            // Unique order requires exactly one choice at each step
            if (queue.size() > 1) return false;
            
            int node = queue.poll();
            if (nums[idx++] != node) return false;
            
            for (int next : adj.get(node)) {
                if (--inDegree[next] == 0) queue.offer(next);
            }
        }
        
        return idx == n;
    }
}
```

**Complexity**: Time O(V + E), Space O(V + E)

---

#### LC 2115 - Find All Possible Recipes from Given Supplies

**Companies**: Amazon, Google  
**Difficulty**: Medium  
**Pattern**: Kahn's with mixed node types (ingredients + recipes)

**Problem**: You have `supplies` (ingredients you start with), `recipes` (what you can make), and `ingredients[i]` (what recipe i needs). A recipe can be an ingredient for another recipe. Return all recipes you can make.

**Key insight**: Treat both recipes and ingredients as nodes. Supplies have in-degree 0 initially.

```java
class Solution {
    public List<String> findAllRecipes(String[] recipes, List<List<String>> ingredients, 
                                        String[] supplies) {
        Map<String, List<String>> adj = new HashMap<>(); // ingredient -> recipes that need it
        Map<String, Integer> inDegree = new HashMap<>();
        
        // Initialize in-degrees for all recipes
        for (String recipe : recipes) {
            inDegree.put(recipe, 0);
        }
        
        // Build graph: ingredient -> recipe
        for (int i = 0; i < recipes.length; i++) {
            for (String ingredient : ingredients.get(i)) {
                adj.computeIfAbsent(ingredient, k -> new ArrayList<>()).add(recipes[i]);
                inDegree.put(recipes[i], inDegree.get(recipes[i]) + 1);
            }
        }
        
        // Start with all supplies (they have no prerequisites)
        Queue<String> queue = new LinkedList<>();
        for (String supply : supplies) {
            queue.offer(supply);
        }
        
        List<String> result = new ArrayList<>();
        
        while (!queue.isEmpty()) {
            String item = queue.poll();
            
            // If this item is a recipe, we can make it
            if (inDegree.containsKey(item)) {
                result.add(item);
            }
            
            // This item can now be used as ingredient for other recipes
            for (String recipe : adj.getOrDefault(item, new ArrayList<>())) {
                inDegree.put(recipe, inDegree.get(recipe) - 1);
                if (inDegree.get(recipe) == 0) {
                    queue.offer(recipe);
                }
            }
        }
        
        return result;
    }
}
```

**Complexity**: Time O(V + E), Space O(V + E)

---

### Category C: Advanced Problems

---

#### LC 310 - Minimum Height Trees

**Companies**: Amazon, Google, Meta, Microsoft  
**Difficulty**: Medium  
**Pattern**: Topological sort from leaves inward (reverse Kahn's)

**Problem**: Given a tree with `n` nodes, find all roots that minimize the height of the tree.

**Key insight**: The answer is always 1 or 2 nodes at the center of the tree. Repeatedly remove leaf nodes (degree 1) until 1 or 2 nodes remain. This is topological sort on an undirected tree, peeling from outside in.

```java
class Solution {
    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        if (n == 1) return Collections.singletonList(0);
        
        List<Set<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new HashSet<>());
        
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }
        
        // Start with all leaves (degree 1)
        List<Integer> leaves = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (adj.get(i).size() == 1) leaves.add(i);
        }
        
        int remaining = n;
        
        // Peel leaves layer by layer until 1 or 2 nodes remain
        while (remaining > 2) {
            remaining -= leaves.size();
            List<Integer> newLeaves = new ArrayList<>();
            
            for (int leaf : leaves) {
                int neighbor = adj.get(leaf).iterator().next();
                adj.get(neighbor).remove(leaf);
                if (adj.get(neighbor).size() == 1) {
                    newLeaves.add(neighbor);
                }
            }
            
            leaves = newLeaves;
        }
        
        return leaves;
    }
}
```

**Why this works**: The center of a tree minimizes the maximum distance to any leaf. By peeling leaves iteratively, we converge to the center.

**Complexity**: Time O(V), Space O(V)

---

#### LC 802 - Find Eventual Safe States

**Companies**: Amazon, Google, Meta  
**Difficulty**: Medium  
**Pattern**: Reverse graph + Kahn's, or DFS with color states

**Problem**: In a directed graph, a node is "safe" if every path from it eventually leads to a terminal node (no outgoing edges). Return all safe nodes in sorted order.

**Approach 1: Reverse graph + Kahn's**

Reverse all edges. Terminal nodes (originally no outgoing edges) become sources (in-degree 0 in reversed graph). Run Kahn's on reversed graph. All processed nodes are safe.

```java
class Solution {
    public List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        List<List<Integer>> reverseAdj = new ArrayList<>();
        int[] outDegree = new int[n]; // out-degree in original = in-degree in reverse
        
        for (int i = 0; i < n; i++) reverseAdj.add(new ArrayList<>());
        
        for (int u = 0; u < n; u++) {
            for (int v : graph[u]) {
                reverseAdj.get(v).add(u); // reverse edge
                outDegree[u]++;
            }
        }
        
        // Terminal nodes have out-degree 0 in original = in-degree 0 in reverse
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (outDegree[i] == 0) queue.offer(i);
        }
        
        boolean[] safe = new boolean[n];
        while (!queue.isEmpty()) {
            int node = queue.poll();
            safe[node] = true;
            
            for (int prev : reverseAdj.get(node)) {
                outDegree[prev]--;
                if (outDegree[prev] == 0) queue.offer(prev);
            }
        }
        
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (safe[i]) result.add(i);
        }
        return result;
    }
}
```

**Approach 2: DFS with colors**

A node is safe if it's not part of a cycle and doesn't lead to a cycle.

```java
class Solution {
    public List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n]; // 0=white, 1=gray, 2=black(safe), 3=unsafe
        
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (isSafe(i, graph, color)) result.add(i);
        }
        return result;
    }
    
    private boolean isSafe(int node, int[][] graph, int[] color) {
        if (color[node] == 2) return true;  // known safe
        if (color[node] == 1 || color[node] == 3) return false; // cycle or unsafe
        
        color[node] = 1; // gray: in progress
        
        for (int next : graph[node]) {
            if (!isSafe(next, graph, color)) {
                color[node] = 3; // mark as unsafe
                return false;
            }
        }
        
        color[node] = 2; // safe
        return true;
    }
}
```

**Complexity**: Time O(V + E), Space O(V)

---

#### LC 329 - Longest Increasing Path in Matrix

**Companies**: Amazon, Google, Meta, Microsoft, Bloomberg  
**Difficulty**: Hard  
**Pattern**: Topological sort + DP (implicit DAG from matrix)

**Problem**: Given an integer matrix, find the length of the longest increasing path. You can move in 4 directions. You cannot move diagonally or outside the boundary.

**Key insight**: The matrix defines an implicit DAG. Cell (r,c) has an edge to neighbor (nr,nc) if `matrix[nr][nc] > matrix[r][c]`. Since edges only go from smaller to larger values, there are no cycles. Apply topological sort (Kahn's) and track the longest path.

```java
class Solution {
    private static final int[][] DIRS = {{0,1},{0,-1},{1,0},{-1,0}};
    
    public int longestIncreasingPath(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[][] inDegree = new int[m][n];
        
        // Compute in-degrees: how many smaller neighbors does each cell have?
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                for (int[] d : DIRS) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr >= 0 && nr < m && nc >= 0 && nc < n 
                        && matrix[nr][nc] < matrix[r][c]) {
                        inDegree[r][c]++;
                    }
                }
            }
        }
        
        // Start with cells that have no smaller neighbors (local minima)
        Queue<int[]> queue = new LinkedList<>();
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (inDegree[r][c] == 0) queue.offer(new int[]{r, c});
            }
        }
        
        int pathLength = 0;
        
        // BFS level by level: each level = one step in the path
        while (!queue.isEmpty()) {
            int size = queue.size();
            pathLength++;
            
            for (int i = 0; i < size; i++) {
                int[] cell = queue.poll();
                int r = cell[0], c = cell[1];
                
                for (int[] d : DIRS) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr >= 0 && nr < m && nc >= 0 && nc < n 
                        && matrix[nr][nc] > matrix[r][c]) {
                        if (--inDegree[nr][nc] == 0) {
                            queue.offer(new int[]{nr, nc});
                        }
                    }
                }
            }
        }
        
        return pathLength;
    }
    
    // Alternative: DFS + memoization (more intuitive)
    private int[][] memo;
    
    public int longestIncreasingPathDFS(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        memo = new int[m][n];
        int result = 0;
        
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                result = Math.max(result, dfs(matrix, r, c, m, n));
            }
        }
        return result;
    }
    
    private int dfs(int[][] matrix, int r, int c, int m, int n) {
        if (memo[r][c] != 0) return memo[r][c];
        
        int best = 1;
        for (int[] d : DIRS) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nr < m && nc >= 0 && nc < n 
                && matrix[nr][nc] > matrix[r][c]) {
                best = Math.max(best, 1 + dfs(matrix, nr, nc, m, n));
            }
        }
        
        memo[r][c] = best;
        return best;
    }
}
```

**Complexity**: Time O(m*n), Space O(m*n)

---

#### LC 1203 - Sort Items by Groups Respecting Dependencies

**Companies**: Google, Amazon  
**Difficulty**: Hard  
**Pattern**: Two-level topological sort (sort groups, then items within groups)

**Problem**: `n` items, `m` groups. Each item belongs to a group (-1 means no group). `beforeItems[i]` lists items that must come before item `i`. Return a valid ordering respecting both item dependencies and group ordering.

**Key insight**: Run topological sort twice:
1. Sort groups (treating each group as a node)
2. Sort items within each group

Items with group -1 each get their own unique group.

```java
class Solution {
    public int[] sortItems(int n, int m, int[] group, int[][] beforeItems) {
        // Assign unique group IDs to ungrouped items
        for (int i = 0; i < n; i++) {
            if (group[i] == -1) {
                group[i] = m++;
            }
        }
        
        // Build two graphs: item-level and group-level
        List<List<Integer>> itemAdj = new ArrayList<>();
        List<List<Integer>> groupAdj = new ArrayList<>();
        int[] itemInDegree = new int[n];
        int[] groupInDegree = new int[m];
        
        for (int i = 0; i < n; i++) itemAdj.add(new ArrayList<>());
        for (int i = 0; i < m; i++) groupAdj.add(new ArrayList<>());
        
        for (int i = 0; i < n; i++) {
            for (int prev : beforeItems[i]) {
                // Item-level edge: prev -> i
                itemAdj.get(prev).add(i);
                itemInDegree[i]++;
                
                // Group-level edge (only if different groups)
                if (group[prev] != group[i]) {
                    groupAdj.get(group[prev]).add(group[i]);
                    groupInDegree[group[i]]++;
                }
            }
        }
        
        // Topological sort of items
        List<Integer> itemOrder = topoSort(n, itemAdj, itemInDegree);
        if (itemOrder.isEmpty()) return new int[0];
        
        // Topological sort of groups
        List<Integer> groupOrder = topoSort(m, groupAdj, groupInDegree);
        if (groupOrder.isEmpty()) return new int[0];
        
        // Group items by their group
        Map<Integer, List<Integer>> groupToItems = new HashMap<>();
        for (int item : itemOrder) {
            groupToItems.computeIfAbsent(group[item], k -> new ArrayList<>()).add(item);
        }
        
        // Build final result: for each group in group order, add its items
        int[] result = new int[n];
        int idx = 0;
        for (int g : groupOrder) {
            for (int item : groupToItems.getOrDefault(g, new ArrayList<>())) {
                result[idx++] = item;
            }
        }
        
        return result;
    }
    
    private List<Integer> topoSort(int n, List<List<Integer>> adj, int[] inDegree) {
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }
        
        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);
            for (int next : adj.get(node)) {
                if (--inDegree[next] == 0) queue.offer(next);
            }
        }
        
        return result.size() == n ? result : new ArrayList<>();
    }
}
```

**Complexity**: Time O(V + E), Space O(V + E)

---

## Common Mistakes

### 1. Forgetting Disconnected Components

**Wrong:**
```java
// Only starts DFS from node 0 — misses disconnected nodes
dfs(0, adj, color);
```

**Right:**
```java
// Loop over ALL nodes
for (int i = 0; i < n; i++) {
    if (color[i] == WHITE) {
        dfs(i, adj, color);
    }
}
```

Kahn's handles this automatically since you initialize the queue with ALL nodes of in-degree 0. But DFS requires explicit iteration.

---

### 2. Not Returning Empty on Cycle Detection

**Wrong:**
```java
public int[] findOrder(int numCourses, int[][] prerequisites) {
    // ... Kahn's algorithm ...
    return order; // returns partial order even if cycle exists!
}
```

**Right:**
```java
return idx == numCourses ? order : new int[0];
```

Always check `result.size() == n` (Kahn's) or `hasCycle` flag (DFS) before returning.

---

### 3. Building the Graph Wrong for Alien Dictionary

**Wrong (reversed edges):**
```java
// This says c2 comes before c1, which is backwards
adj.get(c2).add(c1);
inDegree[c1]++;
```

**Right:**
```java
// c1 comes before c2: edge c1 → c2
adj.get(c1).add(c2);
inDegree[c2]++;
```

Also wrong: not breaking after the first differing character.

```java
// WRONG: processes all differing characters
for (int j = 0; j < minLen; j++) {
    if (w1.charAt(j) != w2.charAt(j)) {
        adj.get(w1.charAt(j)).add(w2.charAt(j));
        // missing break!
    }
}
```

Only the first differing character gives ordering information. Characters after that are irrelevant.

---

### 4. Missing the Prefix Violation in Alien Dictionary

```java
// WRONG: doesn't check prefix violation
for (int i = 0; i < words.length - 1; i++) {
    // ... compare characters ...
}
```

**Right:**
```java
if (w1.length() > w2.length() && w1.startsWith(w2)) {
    return ""; // invalid: longer word before its prefix
}
```

If "abc" appears before "ab" in the sorted list, that's impossible in any valid ordering.

---

### 5. Confusing In-Degree with Out-Degree

In-degree of node `v` = number of edges pointing *into* `v`.

For edge `u → v`:
- `inDegree[v]++` (v has one more incoming edge)
- NOT `inDegree[u]++`

Kahn's starts with nodes where `inDegree == 0` (no prerequisites). If you accidentally track out-degree, you'll start with the wrong nodes.

---

### 6. Not Handling Duplicate Edges in Alien Dictionary

If two adjacent words give the same ordering constraint, adding the edge twice inflates in-degree.

```java
// Use Set to avoid duplicate edges
Map<Character, Set<Character>> adj = new HashMap<>();

// Only add edge if not already present
if (!adj.get(c1).contains(c2)) {
    adj.get(c1).add(c2);
    inDegree.put(c2, inDegree.get(c2) + 1);
}
```

---

### 7. Off-by-One in 1-Indexed Problems

LC 1136 uses 1-indexed courses. Using `new int[n]` instead of `new int[n+1]` causes ArrayIndexOutOfBoundsException.

```java
// WRONG for 1-indexed
int[] inDegree = new int[n];

// RIGHT for 1-indexed
int[] inDegree = new int[n + 1];
```

---

## Comparison

### Kahn's vs DFS-based

| Aspect | Kahn's (BFS) | DFS-based |
|--------|-------------|-----------|
| Approach | Remove nodes with in-degree 0 iteratively | Post-order DFS, push to stack |
| Cycle detection | `result.size() != n` | GRAY → GRAY back edge |
| Order produced | BFS-level order (breadth-first) | Reverse post-order |
| Parallel levels | Natural (each BFS level = one "wave") | Not directly |
| Implementation | Slightly more code (in-degree array) | Slightly less code |
| Stack overflow risk | No (uses queue) | Yes for large graphs (use iterative) |
| Preferred for | Cycle detection, parallel scheduling | When DFS is already in use |

**When to use Kahn's:**
- You need to detect cycles cleanly
- You need to count "levels" or "waves" (like minimum semesters)
- The problem naturally involves processing nodes with no remaining dependencies

**When to use DFS:**
- You're already doing DFS for other reasons
- You need reverse post-order specifically
- The problem involves checking reachability alongside ordering

### Topological Sort vs Regular BFS/DFS (Topic 14)

| Aspect | Regular BFS/DFS | Topological Sort |
|--------|----------------|-----------------|
| Graph type | Any graph | DAG only |
| Purpose | Traversal, shortest path, connectivity | Ordering with dependencies |
| Cycle handling | Can handle cycles | Requires no cycles (or detects them) |
| Output | Visited nodes, distances | Ordered sequence |
| In-degree tracking | Not needed | Core to Kahn's |

Topological sort is a specialized application of BFS/DFS. Kahn's is BFS with in-degree tracking. DFS-based is DFS with post-order collection.

The key distinction: regular BFS/DFS answers "can I reach X from Y?" Topological sort answers "in what order should I process these nodes given their dependencies?"

---

## Cheat Sheet + Roadmap

### Quick Reference

**Kahn's Algorithm (5 lines of logic):**
```
1. Build adj list + compute inDegree[]
2. Queue all nodes with inDegree == 0
3. While queue not empty: poll node, add to result, decrement neighbors' inDegree, enqueue if 0
4. If result.size() != n → cycle
5. Return result
```

**DFS Topological Sort (4 lines of logic):**
```
1. For each WHITE node, run DFS
2. In DFS: mark GRAY on entry, mark BLACK + push to stack on exit
3. If you visit a GRAY node → cycle
4. Stack contents (top to bottom) = topological order
```

**Alien Dictionary graph building:**
```
For each adjacent pair (w1, w2):
  - Check prefix violation: w1.startsWith(w2) && w1.length() > w2.length() → invalid
  - Find first j where w1[j] != w2[j]
  - Add edge w1[j] → w2[j] (if not already present)
  - Break immediately
```

**Cycle detection summary:**
```
Kahn's: result.size() != n
DFS:    visiting a GRAY node
```

**Common patterns:**
```
"Can you complete all tasks?"          → Kahn's cycle detection (LC 207)
"Return valid order"                   → Kahn's return result (LC 210)
"Minimum time/semesters"               → Kahn's level counting (LC 1136)
"Reconstruct order from sorted words"  → Build graph + topo sort (LC 269)
"Longest path in matrix"               → Implicit DAG + DFS memo (LC 329)
"Safe nodes"                           → Reverse graph + Kahn's (LC 802)
```

### Problem Patterns by Difficulty

**Easy entry points:**
- LC 207: Pure cycle detection, clean Kahn's
- LC 210: Same as 207 but return the order

**Medium:**
- LC 1136: Level-counting BFS
- LC 802: Reverse graph insight
- LC 2115: Mixed node types
- LC 310: Leaf-peeling (reverse Kahn's)
- LC 444: Unique ordering verification

**Hard:**
- LC 269: Graph construction from word comparisons
- LC 329: Implicit DAG from matrix
- LC 1203: Two-level topological sort

### 2-Week Study Roadmap

**Week 1: Foundation**

Day 1-2: Master Kahn's algorithm
- Implement from scratch without looking at notes
- Solve LC 207 (Kahn's)
- Solve LC 210 (return order)

Day 3-4: Master DFS-based
- Implement WHITE/GRAY/BLACK coloring from scratch
- Solve LC 207 again with DFS
- Solve LC 210 again with DFS

Day 5-6: Apply to real problems
- LC 1136 (level counting)
- LC 802 (reverse graph)
- LC 2115 (mixed nodes)

Day 7: Review and consolidate
- Re-solve LC 207 and 210 from memory
- Write out both templates without notes

**Week 2: Advanced**

Day 8-9: Alien Dictionary (LC 269)
- Spend time on graph construction
- Trace through examples manually
- Implement both Kahn's and DFS versions

Day 10-11: Matrix problems
- LC 329 (implicit DAG)
- LC 310 (leaf peeling)

Day 12-13: Hard problems
- LC 1203 (two-level sort)
- LC 444 (unique ordering)

Day 14: Mock interview
- Pick 2 problems randomly from the list
- Solve under 45-minute time limit
- Explain your approach out loud

### Interview Decision Tree

```
Is the graph directed?
  No → Not topological sort (use Union-Find or BFS/DFS)
  Yes → Is there a cycle?
    Detect cycle → Kahn's (result.size() != n)
    Need ordering → Kahn's or DFS
      Need parallel levels? → Kahn's with level counting
      Need reverse post-order? → DFS
      Need to verify unique order? → Kahn's, check queue.size() == 1 always
```

### Complexity Summary

| Problem | Time | Space | Key Insight |
|---------|------|-------|-------------|
| LC 207 | O(V+E) | O(V+E) | Cycle = can't finish |
| LC 210 | O(V+E) | O(V+E) | Store order in result array |
| LC 269 | O(C) | O(1) | Build graph from word diffs |
| LC 310 | O(V) | O(V) | Peel leaves until center |
| LC 802 | O(V+E) | O(V+E) | Reverse graph + Kahn's |
| LC 1136 | O(V+E) | O(V+E) | BFS levels = semesters |
| LC 2115 | O(V+E) | O(V+E) | Supplies = initial queue |
| LC 1203 | O(V+E) | O(V+E) | Two separate topo sorts |
| LC 329 | O(mn) | O(mn) | Implicit DAG from matrix |
| LC 444 | O(V+E) | O(V+E) | Queue size must stay 1 |

### Key Invariants to Remember

1. **Kahn's processes exactly `n` nodes if no cycle exists.** If fewer, a cycle exists.

2. **DFS: push to stack AFTER all neighbors are processed.** Not before, not during.

3. **GRAY means "currently in recursion stack."** Not just "visited." BLACK means "done."

4. **Alien Dictionary: only the first differing character matters.** Break after finding it.

5. **Prefix violation in Alien Dictionary is a separate check** from the character comparison loop.

6. **For disconnected graphs, loop over all nodes** in DFS-based approach. Kahn's handles it automatically.

7. **In-degree tracks incoming edges.** For edge `u → v`, increment `inDegree[v]`, not `inDegree[u]`.

8. **Level-counting BFS: process all nodes at current level before moving to next.** Use `size = queue.size()` at the start of each level.

---

*Next: Topic 16 — Union-Find (Disjoint Set Union)*
