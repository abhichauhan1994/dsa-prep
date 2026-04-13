# DSA Mastery Roadmap — The Complete Plan

> Based on 20 topic documents (40,224 lines, ~300 problems, ~80 templates)
> Time commitment: 1.5-2 hours/day
> Target: Identify and solve ANY DSA problem with pattern maturity

---

## The Honest Numbers

| Metric | Count |
|--------|-------|
| Topics | 20 |
| Problems (with full solutions) | ~300 |
| Must-solve problems (core set) | ~150 |
| Templates to memorize | ~80 |
| Estimated calendar time | **20-24 weeks (5-6 months)** |
| Daily commitment | 1.5-2 hours |
| Total hours | ~250-300 hours |

**Why 5-6 months and not 3?** Because retention requires spaced repetition. Solving a problem once is worthless if you can't solve it again 3 weeks later. This plan builds in review cycles — they're not optional, they're what separate "I've seen 300 problems" from "I can solve any problem."

---

## The 4-Phase Structure

```
Phase 1: Foundation Patterns     (Weeks 1-8)    — Topics 1-6
Phase 2: Core Data Structures    (Weeks 9-14)   — Topics 7-12
Phase 3: Advanced Patterns       (Weeks 15-20)  — Topics 13-20
Phase 4: Integration & Mastery   (Weeks 21-24)  — Review, mocks, weak spots
```

---

## How Each Day Should Look (1.5-2 hours)

### Day structure when learning a NEW topic:

```
[0:00 - 0:20]  READ the document section (concept, ELI5, templates)
[0:20 - 0:30]  WRITE the template from memory on paper/whiteboard (no peeking)
[0:30 - 1:15]  SOLVE 1-2 problems (easy first, then medium)
               - Set a timer: 15 min easy, 25 min medium, 35 min hard
               - If stuck after timer: read the HINT only, try 5 more minutes
               - If still stuck: read the solution, THEN close it and re-solve from scratch
[1:15 - 1:30]  REVIEW: Write down in your own words:
               - What pattern did this use?
               - What was the key insight?
               - What would I miss if I saw this cold?
[1:30 - 2:00]  REVISE 1 problem from a previous topic (spaced repetition)
```

### Day structure when REVIEWING (every Saturday):

```
[0:00 - 0:30]  Write ALL templates learned this week from memory
[0:30 - 1:30]  Re-solve 3-4 problems from the week WITHOUT looking at notes
[1:30 - 2:00]  Identify weak spots, mark problems for re-attempt next week
```

---

## Phase 1: Foundation Patterns (Weeks 1-8)

These 6 topics cover ~60% of all interview questions. They must be AUTOMATIC.

### Week 1-2: Sliding Window (Topic 1)

**Read:** Sections 1-5 of `01-sliding-window.md` (concepts, templates)

| Day | Problem | Time | What to focus on |
|-----|---------|------|------------------|
| 1 | LC 643 (Easy) | 15 min | Fixed window basics |
| 2 | LC 1456 (Easy) | 15 min | Fixed window + char tracking |
| 3 | LC 3 (Medium) | 25 min | Variable window + HashSet |
| 4 | LC 209 (Medium) | 25 min | Shortest window (inverted logic!) |
| 5 | LC 424 (Medium) | 25 min | The maxFreq trick |
| 6 | LC 567 (Medium) | 25 min | Frequency map window |
| 7 | **REVIEW DAY** | 2 hrs | Re-solve 3 problems cold, write templates |
| 8 | LC 438 (Medium) | 25 min | Collect all anagram positions |
| 9 | LC 930 (Medium) | 30 min | "Exactly K" = atMost(K) - atMost(K-1) |
| 10 | LC 76 (Hard) | 40 min | Minimum window substring |
| 11 | LC 239 (Hard) | 40 min | Monotonic deque (preview Topic 7) |
| 12 | LC 992 (Hard) | 40 min | Exactly K distinct |
| 13 | LC 1004 (Medium) | 25 min | Binary array + window |
| 14 | **REVIEW DAY** | 2 hrs | Re-solve LC 76, 239 cold. Write all 5 templates. |

**Checkpoint:** You can write all 5 sliding window templates from memory. You can identify "sliding window" within 30 seconds of reading a problem.

---

### Week 3-4: Two Pointers (Topic 2)

**Read:** `02-two-pointers.md` — focus on the 4 sub-patterns

| Day | Problem | Time | What to focus on |
|-----|---------|------|------------------|
| 1 | LC 344 (Easy) | 10 min | Opposite direction — simplest |
| 2 | LC 125 (Easy) | 15 min | Palindrome + char filtering |
| 3 | LC 26 (Easy) | 15 min | Read/Write pointer |
| 4 | LC 283 (Easy) | 15 min | Move zeros — read/write variant |
| 5 | LC 167 (Medium) | 20 min | Canonical pair sum |
| 6 | LC 11 (Medium) | 25 min | Container With Most Water — greedy insight |
| 7 | **REVIEW DAY** | 2 hrs | Templates + re-solve |
| 8 | LC 75 (Medium) | 30 min | Dutch National Flag — 3 pointers |
| 9 | LC 15 (Medium) | 30 min | 3Sum — duplicate handling! |
| 10 | LC 88 (Easy) | 20 min | Merge from END |
| 11 | LC 42 (Hard) | 40 min | Trapping Rain Water |
| 12 | LC 977 (Easy) | 15 min | Squares of sorted array |
| 13 | LC 680 (Easy) | 20 min | Valid Palindrome II |
| 14 | **REVIEW DAY** | 2 hrs | Re-solve LC 15, 42. Sliding Window vs Two Pointers decision. |

**Checkpoint:** You know when to use opposite-direction, read/write, merge, or fix+sweep. You can handle duplicate skipping in 3Sum without hesitation.

---

### Week 5: Fast & Slow Pointers (Topic 3)

**Read:** `03-fast-and-slow-pointers.md`

| Day | Problem | Time | Focus |
|-----|---------|------|-------|
| 1 | LC 141 (Easy) | 15 min | Basic cycle detection |
| 2 | LC 876 (Easy) | 15 min | Find middle |
| 3 | LC 142 (Medium) | 30 min | Floyd's Phase 2 — understand the MATH |
| 4 | LC 202 (Easy) | 20 min | Happy Number — functional graph cycle |
| 5 | LC 234 (Easy) | 25 min | Palindrome Linked List — composite problem |
| 6 | LC 287 (Medium) | 35 min | Find Duplicate — array as linked list! |
| 7 | **REVIEW DAY** | 2 hrs | Explain Floyd's Phase 2 proof from memory |

**Checkpoint:** You can explain the Floyd's Phase 2 math proof without notes. You can map an array into a functional graph.

---

### Week 6: Binary Search (Topic 4)

**Read:** `04-binary-search.md` — the THREE templates are critical

| Day | Problem | Time | Focus |
|-----|---------|------|-------|
| 1 | LC 704 (Easy) | 15 min | Template 1: `while (left <= right)` |
| 2 | LC 35 (Easy) | 15 min | Template 2: lower bound |
| 3 | LC 34 (Medium) | 25 min | First/last position — use both bounds |
| 4 | LC 33 (Medium) | 30 min | Rotated sorted array — which half is sorted? |
| 5 | LC 153 (Medium) | 25 min | Find minimum in rotated |
| 6 | LC 875 (Medium) | 30 min | Template 3: Search on answer! |
| 7 | **REVIEW DAY** | 2 hrs | Write all 3 templates. Re-solve LC 33, 875. |
| 8 | LC 1011 (Medium) | 30 min | Ship packages — same as Koko |
| 9 | LC 162 (Medium) | 25 min | Peak element — binary search on unsorted! |
| 10 | LC 69 (Easy) | 15 min | Sqrt — search on answer |
| 11 | LC 4 (Hard) | 45 min | Median of two sorted arrays |
| 12 | LC 410 (Hard) | 40 min | Split Array Largest Sum |
| 13 | LC 378 (Medium) | 30 min | Kth smallest in matrix |
| 14 | **REVIEW DAY** | 2 hrs | Template selector: "when do I use which?" |

**Checkpoint:** You know the difference between `left <= right` and `left < right`. You can recognize "search on answer" problems within 60 seconds.

---

### Week 7: Prefix Sum (Topic 5)

**Read:** `05-prefix-sum.md`

| Day | Problem | Time | Focus |
|-----|---------|------|-------|
| 1 | LC 303 (Easy) | 15 min | Build prefix array, query O(1) |
| 2 | LC 724 (Easy) | 15 min | Pivot index |
| 3 | LC 560 (Medium) | 30 min | Prefix Sum + HashMap — THE key problem |
| 4 | LC 238 (Medium) | 25 min | Product except self — O(1) space trick |
| 5 | LC 525 (Medium) | 30 min | Longest equal 0s and 1s |
| 6 | LC 1094 (Medium) | 25 min | Car Pooling — difference array |
| 7 | **REVIEW DAY** | 2 hrs | When Prefix Sum vs Sliding Window? |
| 8 | LC 523 (Medium) | 30 min | Continuous subarray sum — mod trick |
| 9 | LC 974 (Medium) | 30 min | Handle negative mod in Java! |
| 10 | LC 304 (Medium) | 30 min | 2D prefix sum |
| 11 | LC 437 (Medium) | 35 min | Path Sum III — prefix sum on TREE |
| 12 | LC 528 (Medium) | 30 min | Random pick with weight |
| 13 | **REVIEW DAY** | 2 hrs | Write the HashMap initialization. 2D formula. |

**Checkpoint:** You never forget `map.put(0, 1)`. You know when to use sliding window vs prefix sum.

---

### Week 8: HashMap & Frequency Counting (Topic 6)

**Read:** `06-hashmap-and-frequency-counting.md`

| Day | Problem | Time | Focus |
|-----|---------|------|-------|
| 1 | LC 1 (Easy) | 15 min | Two Sum — complement lookup |
| 2 | LC 242 (Easy) | 15 min | Valid Anagram — int[26] |
| 3 | LC 49 (Medium) | 25 min | Group Anagrams — grouping pattern |
| 4 | LC 347 (Medium) | 30 min | Top K — heap AND bucket sort |
| 5 | LC 128 (Medium) | 30 min | Longest Consecutive — O(n) trick |
| 6 | LC 146 (Medium) | 40 min | LRU Cache — HashMap + DLL |
| 7 | **REVIEW DAY** | 2 hrs | Re-implement LRU Cache cold. Merge/computeIfAbsent API. |

**Phase 1 Checkpoint (Week 8 end):** You have 6 pattern families internalized. ~80 problems solved. You can identify which of the 6 patterns applies to any array/string problem within 1 minute.

---

## Phase 2: Core Data Structures (Weeks 9-14)

### Week 9: Monotonic Stack & Queue (Topic 7)

| Day | Key Problems | Focus |
|-----|-------------|-------|
| 1-2 | LC 496, 739 | Next greater element — the foundation |
| 3 | LC 901 | Stock Span — previous greater |
| 4-5 | LC 84 | Largest Rectangle in Histogram — THE problem |
| 6 | LC 402 | Remove K Digits — greedy + stack |
| 7 | **REVIEW** | Re-solve LC 84 cold |

### Week 10: Stacks, Queues & Linked Lists (Topics 8-9)

| Day | Key Problems | Focus |
|-----|-------------|-------|
| 1 | LC 20 | Valid Parentheses |
| 2 | LC 155 | Min Stack |
| 3 | LC 394, 227 | Decode String, Calculator II |
| 4 | LC 206 | Reverse Linked List — the 3-pointer dance |
| 5 | LC 21, 23 | Merge sorted lists — dummy node + heap |
| 6 | LC 25 | Reverse in k-groups |
| 7 | **REVIEW** | Write reversal and dummy node templates |

### Week 11-12: Trees — DFS & BFS (Topics 10-11)

| Day | Key Problems | Focus |
|-----|-------------|-------|
| 1 | LC 104, 226 | Max depth, Invert tree |
| 2 | LC 98 | Validate BST — bounds propagation |
| 3 | LC 236 | LCA — the classic post-order |
| 4 | LC 124 | Max Path Sum — return vs global (HARD) |
| 5 | LC 297 | Serialize/Deserialize |
| 6 | LC 102, 103 | Level-order, Zigzag |
| 7 | **REVIEW** | Top-down vs bottom-up decision |
| 8 | LC 199, 116 | Right Side View, Next Pointer |
| 9 | LC 105 | Construct from Preorder + Inorder |
| 10 | LC 543, 110 | Diameter, Balanced |
| 11 | LC 230, 437 | Kth Smallest BST, Path Sum III |
| 12 | LC 111, 662 | Min Depth (BFS!), Max Width |
| 13-14 | **REVIEW** | DFS vs BFS decision table. All tree templates. |

### Week 13-14: Heaps (Topic 12) + Phase 2 Review

| Day | Key Problems | Focus |
|-----|-------------|-------|
| 1 | LC 215 | Kth Largest — heap + quickselect |
| 2 | LC 295 | Find Median — two heaps |
| 3 | LC 621 | Task Scheduler |
| 4 | LC 973 | K Closest Points |
| 5 | LC 253 | Meeting Rooms II |
| 6-7 | **REVIEW** | Re-solve hardest problem from each of Topics 7-12 |

**Phase 2 Checkpoint (Week 14 end):** ~150 problems solved. You can handle stack, queue, linked list, tree, and heap problems confidently. You know BFS vs DFS.

---

## Phase 3: Advanced Patterns (Weeks 15-20)

### Week 15: Backtracking (Topic 13)

| Day | Key Problems | Focus |
|-----|-------------|-------|
| 1 | LC 78 | Subsets — draw the decision tree |
| 2 | LC 46 | Permutations — used[] array |
| 3 | LC 39, 40 | Combination Sum — reuse vs no reuse |
| 4 | LC 22 | Generate Parentheses |
| 5 | LC 51 | N-Queens |
| 6 | LC 79 | Word Search — grid backtracking |
| 7 | **REVIEW** | Duplicate handling: when `i > start` vs `!used[i-1]` |

### Week 16-17: Graphs (Topics 14-16)

| Day | Key Problems | Focus |
|-----|-------------|-------|
| 1 | LC 200 | Number of Islands — grid DFS |
| 2 | LC 994 | Rotting Oranges — multi-source BFS |
| 3 | LC 133, 547 | Clone Graph, Provinces |
| 4 | LC 127 | Word Ladder — BFS for shortest |
| 5 | LC 207, 210 | Course Schedule I & II — topo sort |
| 6 | LC 269 | Alien Dictionary — build graph from constraints |
| 7 | **REVIEW** | BFS vs DFS vs Topo Sort decision |
| 8 | LC 743 | Network Delay — Dijkstra |
| 9 | LC 787 | Cheapest Flights K Stops — modified BFS |
| 10 | LC 684 | Redundant Connection — Union-Find |
| 11 | LC 721 | Accounts Merge — Union-Find |
| 12 | LC 1584 | Min Cost Connect — Kruskal's |
| 13-14 | **REVIEW** | Graph representation, algorithm selection |

### Week 18-19: Intervals & Greedy (Topics 18-19)

| Day | Key Problems | Focus |
|-----|-------------|-------|
| 1 | LC 56 | Merge Intervals — sort by start |
| 2 | LC 57, 253 | Insert Interval, Meeting Rooms II |
| 3 | LC 435 | Non-overlapping — greedy by end |
| 4 | LC 55, 45 | Jump Game I & II |
| 5 | LC 134 | Gas Station |
| 6 | LC 135 | Candy — two-pass greedy |
| 7 | **REVIEW** | Greedy vs DP decision |

### Week 19-20: Dynamic Programming (Topic 20) — THE BOSS

DP needs the most time. Allocate 2 full weeks minimum.

| Day | Key Problems | Focus |
|-----|-------------|-------|
| 1 | LC 70, 198 | Climbing Stairs, House Robber — 1D DP |
| 2 | LC 322 | Coin Change — the 4-step framework |
| 3 | LC 300 | LIS — O(n^2) and O(n log n) |
| 4 | LC 62, 64 | Unique Paths, Min Path Sum — 2D DP |
| 5 | LC 1143 | LCS — string DP foundation |
| 6 | LC 72 | Edit Distance — THE string DP problem |
| 7 | **REVIEW** | State definition practice. 4-step framework. |
| 8 | LC 416 | Partition Equal Subset — 0/1 knapsack |
| 9 | LC 518, 494 | Coin Change II, Target Sum |
| 10 | LC 139 | Word Break |
| 11 | LC 5, 516 | Palindromic Substring/Subsequence |
| 12 | LC 121, 309 | Stock problems — state machine DP |
| 13 | LC 312 | Burst Balloons — interval DP |
| 14 | **REVIEW** | Categorize all DP problems by type. Re-solve LC 72, 322. |

**Phase 3 Checkpoint (Week 20 end):** ~250 problems solved. You can identify the pattern family for any problem. You can write the DP state and transition for standard categories.

---

## Phase 4: Integration & Mastery (Weeks 21-24)

This is where pattern RECOGNITION becomes automatic.

### Week 21-22: Mixed Problem Sets (no topic labels)

Solve 3-4 problems per day from a mixed set WITHOUT knowing the topic in advance. This is the real interview simulation.

**Recommended sources:**
- LeetCode Daily Challenge
- NeetCode 150 (problems you haven't done)
- Blind 75 (fill any gaps)

**The rule:** Before coding, spend 2-3 minutes writing:
1. Which pattern does this use?
2. What template applies?
3. What's the expected time complexity?

If you can't identify the pattern in 3 minutes, that's a gap to fill.

### Week 23-24: Mock Interviews & Weak Spot Drilling

- Do 2-3 timed mock interviews per week (45 min, 2 problems)
- Use Pramp, Interviewing.io, or a friend
- After each mock: identify which pattern you struggled with → re-read that topic document → re-solve 3 problems from that topic

---

## Spaced Repetition Schedule

This is NON-NEGOTIABLE. Without it, you'll forget 70% in 3 weeks.

```
After solving a problem:
  - Review it 1 day later   (re-solve mentally or on paper)
  - Review it 3 days later   (re-solve on computer)
  - Review it 7 days later   (re-solve cold — if you can't, it goes back to Day 1)
  - Review it 21 days later  (final check — if clean, it's mastered)
```

**Practical implementation:** Keep a spreadsheet or use Anki:

| Problem | First Solved | Day+1 | Day+3 | Day+7 | Day+21 | Mastered? |
|---------|-------------|-------|-------|-------|--------|-----------|
| LC 76   | Week 2 Day 5 | OK | OK | Stuck on shrink | Redo | OK after redo |

---

## Roadblocks You WILL Hit (and How to Handle Them)

### Roadblock 1: "I solved it before but can't solve it again"
**Why:** You memorized the solution, not the pattern.
**Fix:** After solving, always write: "What pattern? What was the key insight? How would I recognize this in a different problem?" If you can't answer these, you didn't learn — you copied.

### Roadblock 2: "I know the pattern but can't code it cleanly"
**Why:** Template not in muscle memory yet.
**Fix:** Write the template on paper 3 times on 3 different days. No peeking. If you can't write it from memory, you don't know it.

### Roadblock 3: "DP problems all look different"
**Why:** DP has the most variety. The 4-step framework helps, but experience is required.
**Fix:** For every DP problem, ALWAYS start with brute force recursion. Draw the recursion tree. Identify overlapping subproblems. Add memoization. THEN convert to bottom-up. Never skip steps.

### Roadblock 4: "Graph problems confuse me"
**Why:** Graphs have multiple representations and algorithms. It's hard to know which to use.
**Fix:** Always ask three questions: (1) Directed or undirected? (2) Weighted or unweighted? (3) What am I looking for (shortest path, connectivity, ordering)? The answers map directly to the algorithm. Print the decision table from Topic 14.

### Roadblock 5: "I'm slow — I can't solve mediums in 25 minutes"
**Why:** Normal. Speed comes from pattern recognition, not thinking faster.
**Fix:** Don't time yourself for the first 2 weeks of any topic. Focus on correctness and understanding. Speed will follow. By Phase 4, you should be at 20-25 min for mediums.

### Roadblock 6: "I'm burning out"
**Why:** DSA prep is a grind. 5-6 months is long.
**Fix:** Take 1 full rest day per week (no DSA). If you miss a day, don't try to "catch up" — just continue the next day. Consistency beats intensity. 1.5 hours every day for 5 months beats 8 hours/day for 3 weeks then quitting.

### Roadblock 7: "I can solve problems but freeze in interviews"
**Why:** Interview pressure. No practice explaining your thought process.
**Fix:** Practice talking out loud while solving. Explain to an imaginary interviewer: "I notice this is asking for the longest contiguous substring, and there's a constraint on distinct characters. That's a variable-size sliding window problem. I'll use Template 2 with a frequency map." Do this for EVERY problem starting from Phase 2.

---

## Progress Milestones

| Week | Problems Solved | What You Can Do |
|------|----------------|-----------------|
| 4 | ~50 | Recognize and solve sliding window + two pointer problems |
| 8 | ~90 | Handle all array/string pattern problems confidently |
| 12 | ~140 | Solve tree, linked list, stack, heap problems |
| 16 | ~190 | Handle backtracking and basic graph problems |
| 20 | ~250 | Solve DP, greedy, and advanced graph problems |
| 24 | ~300+ | Pattern recognition is automatic. Ready for interviews. |

---

## The "Am I Ready?" Checklist

Before scheduling interviews, verify ALL of these:

- [ ] I can write all 20 topic templates from memory
- [ ] I can solve any EASY in under 10 minutes
- [ ] I can solve most MEDIUMS in under 25 minutes
- [ ] I can identify the pattern within 2 minutes of reading a problem
- [ ] I can explain my approach before coding (not just code silently)
- [ ] I can handle follow-up questions ("What if the array has negatives?", "Can you do it in O(1) space?")
- [ ] I've done at least 5 mock interviews with a real person
- [ ] I can solve problems from the Blind 75 without hints
- [ ] I know when to use Greedy vs DP vs Backtracking
- [ ] I've solved at least 10 Hard problems across different topics

---

## Quick-Start for Today

If you're reading this and want to start RIGHT NOW:

1. Open `01-sliding-window.md`
2. Read Sections 1-5 (30 minutes)
3. Write Template 1 (Fixed Window) and Template 2 (Variable Window) on paper
4. Solve LC 643 on LeetCode (15 minutes)
5. Solve LC 3 on LeetCode (25 minutes)
6. Write down: "What pattern? What insight? How to recognize?"

That's Day 1. See you tomorrow for LC 209.

---

*This roadmap was generated from 20 topic documents totaling 40,224 lines. Every problem, template, and timeline referenced here has a corresponding detailed explanation in the topic documents.*
