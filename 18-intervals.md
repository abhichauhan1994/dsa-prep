# Topic 18: Intervals

> **Series position:** Document 18 of 20
> **Difficulty range:** Easy to Hard
> **Interview frequency:** Interval problems appear in ~20% of FAANG interviews. Amazon and Google ask merge/insert variants constantly. Meta favors scheduling problems. Bloomberg asks calendar and booking variants. Google asks advanced sweep-line problems.
> **Top companies:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, Uber
> **Prerequisites:** Topic 2 (Two Pointers), Topic 5 (Prefix Sum / Difference Arrays), Topic 12 (Heaps & Priority Queues)

---

## Table of Contents

1. [Core Concept](#core-concept)
2. [ELI5 Intuition](#eli5-intuition)
3. [Core Templates in Java](#core-templates-in-java)
4. [Real-World Applications](#real-world-applications)
5. [Problem Categories and Solutions](#problem-categories-and-solutions)
   - [Category A: Merge and Insert](#category-a-merge-and-insert)
   - [Category B: Scheduling and Overlap](#category-b-scheduling-and-overlap)
   - [Category C: Intersection and Gap](#category-c-intersection-and-gap)
   - [Category D: Advanced](#category-d-advanced)
6. [Common Mistakes](#common-mistakes)
7. [Algorithm Comparison](#algorithm-comparison)
8. [Quick Reference Cheat Sheet](#quick-reference-cheat-sheet)
9. [Practice Roadmap](#practice-roadmap)

---

## Core Concept

### What Interval Problems Are

An interval is a range `[start, end]` representing a contiguous span — of time, space, or any ordered domain. Interval problems ask you to reason about how these ranges relate to each other: do they overlap? Can they be merged? How many overlap at once? Where are the gaps?

The universal first step for almost every interval problem: **sort by start time**.

Once sorted, you can process intervals left to right with a single pass, making decisions locally at each step. Without sorting, you'd need to compare every pair — O(n²). With sorting, most interval problems reduce to O(n log n) dominated by the sort.

---

### The Three Core Operations

**1. Merge Overlapping Intervals**

After sorting by start, walk through intervals. Maintain a "current" interval. If the next interval overlaps with current (its start ≤ current's end), extend current's end to max(current.end, next.end). Otherwise, finalize current and start a new one.

```
Overlap condition: next.start <= current.end
(Note: <= not <, because [1,3] and [3,5] share point 3 — they touch)
```

**2. Insert Interval into Sorted List**

Three phases: (1) add all intervals that end before the new interval starts, (2) merge all intervals that overlap with the new interval, (3) add all remaining intervals. The merge phase extends the new interval's boundaries.

**3. Sweep Line (Event-Based)**

Convert each interval `[s, e]` into two events: `(s, +1)` for start and `(e, -1)` for end. Sort all events by time. Sweep through, maintaining a running count. The maximum count at any point is the maximum number of overlapping intervals.

This is the generalization of the difference array technique from Topic 5.

---

### Overlap Condition — Get This Right

Two intervals `a = [a.start, a.end]` and `b = [b.start, b.end]` overlap if and only if:

```
a.start < b.end  AND  b.start < a.end
```

This is the **strict** overlap condition (excludes touching). For problems where touching counts as overlap (like Merge Intervals), use:

```
a.start <= b.end  AND  b.start <= a.end
```

Equivalently, they do NOT overlap if:
```
a.end < b.start  OR  b.end < a.start
```

Memorize the non-overlap condition — it's easier to negate: "one ends before the other starts."

---

### Connection to Other Topics

**Topic 5 (Prefix Sum / Difference Arrays):** The sweep line is a generalization of the difference array. When you have range updates `[l, r] += 1`, the difference array technique is exactly a sweep line. Car Pooling (LC 1094) is solved with a difference array. See Category D.

**Topic 2 (Two Pointers):** Interval List Intersections (LC 986) uses two pointers on two sorted interval lists — advance the pointer whose interval ends first. Classic two-pointer merge pattern.

**Topic 12 (Heaps & Priority Queues):** Meeting Rooms II (LC 253) uses a min-heap of end times. When a new meeting starts, check if the earliest-ending meeting has already ended. If yes, reuse that room (pop and push new end). If no, add a new room (just push). The heap size at the end is the answer.

---

## ELI5 Intuition

Imagine you're scheduling meetings in a conference room. You have a list of meetings, each with a start time and end time.

**Merge Intervals:** Sort all meetings by start time. Walk through them. If the next meeting starts before the current one ends, they overlap — merge them into one big block (the block ends at whichever meeting ends later). If the next meeting starts after the current one ends, the current block is done; start a new block.

**Insert Interval:** You have a sorted schedule and need to add a new meeting. Skip all meetings that end before your new one starts (they're fine as-is). Merge your new meeting with any meetings it overlaps. Then keep the rest as-is.

**Sweep Line:** Instead of thinking about intervals, think about events. Every meeting start is a "+1 person enters the room" event. Every meeting end is a "-1 person leaves the room" event. Sort all events by time and sweep through, counting people. The peak count is the maximum number of rooms you need simultaneously.

**The key insight:** Sorting by start time converts a 2D problem (comparing all pairs of intervals) into a 1D problem (a single left-to-right sweep).

---

## Core Templates in Java

### Template 1: Merge Intervals

```java
/**
 * Merge all overlapping intervals.
 * Input: [[1,3],[2,6],[8,10],[15,18]]
 * Output: [[1,6],[8,10],[15,18]]
 *
 * Time: O(n log n) — dominated by sort
 * Space: O(n) — output list
 */
public int[][] merge(int[][] intervals) {
    if (intervals.length <= 1) return intervals;

    // Step 1: Sort by start time
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

    List<int[]> result = new ArrayList<>();
    int[] current = intervals[0];  // current interval being built

    for (int i = 1; i < intervals.length; i++) {
        int[] next = intervals[i];

        if (next[0] <= current[1]) {
            // Overlap: extend current's end if needed
            current[1] = Math.max(current[1], next[1]);
        } else {
            // No overlap: finalize current, start new
            result.add(current);
            current = next;
        }
    }

    result.add(current);  // don't forget the last interval
    return result.toArray(new int[result.size()][]);
}
```

**Key decisions:**
- `next[0] <= current[1]`: use `<=` because touching intervals (e.g., [1,3] and [3,5]) should merge
- `Math.max(current[1], next[1])`: necessary because a large interval can contain a smaller one (e.g., [1,10] followed by [2,5])
- Add `current` after the loop: the last interval is never added inside the loop

---

### Template 2: Insert Interval

```java
/**
 * Insert a new interval into a sorted, non-overlapping list.
 * Input: intervals=[[1,3],[6,9]], newInterval=[2,5]
 * Output: [[1,5],[6,9]]
 *
 * Time: O(n)
 * Space: O(n)
 */
public int[][] insert(int[][] intervals, int[] newInterval) {
    List<int[]> result = new ArrayList<>();
    int i = 0;
    int n = intervals.length;

    // Phase 1: Add all intervals that end before newInterval starts
    // These intervals come entirely before newInterval — no overlap possible
    while (i < n && intervals[i][1] < newInterval[0]) {
        result.add(intervals[i]);
        i++;
    }

    // Phase 2: Merge all overlapping intervals into newInterval
    // An interval overlaps if it starts before newInterval ends
    while (i < n && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
        newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
        i++;
    }
    result.add(newInterval);  // add the merged interval

    // Phase 3: Add all remaining intervals (they start after newInterval ends)
    while (i < n) {
        result.add(intervals[i]);
        i++;
    }

    return result.toArray(new int[result.size()][]);
}
```

**Key decisions:**
- Phase 1 condition: `intervals[i][1] < newInterval[0]` — interval ends strictly before new starts
- Phase 2 condition: `intervals[i][0] <= newInterval[1]` — interval starts at or before new ends
- Mutate `newInterval` in place during merge — no need for a separate variable

---

### Template 3: Sweep Line / Event-Based

```java
/**
 * Find the maximum number of overlapping intervals at any point.
 * Equivalent to: minimum number of rooms needed for all meetings.
 *
 * Time: O(n log n)
 * Space: O(n)
 */
public int maxOverlap(int[][] intervals) {
    // Create events: +1 at start, -1 at end
    // Use 2*n array of [time, type] where type: 0=end, 1=start
    // Sorting trick: at same time, process ends before starts
    // (a room freed at time 5 can be reused by a meeting starting at 5)
    int n = intervals.length;
    int[][] events = new int[2 * n][2];

    for (int i = 0; i < n; i++) {
        events[2 * i]     = new int[]{intervals[i][0], 1};   // start event
        events[2 * i + 1] = new int[]{intervals[i][1], -1};  // end event
    }

    // Sort by time; at same time, end (-1) before start (+1)
    Arrays.sort(events, (a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);

    int maxRooms = 0;
    int currentRooms = 0;

    for (int[] event : events) {
        currentRooms += event[1];
        maxRooms = Math.max(maxRooms, currentRooms);
    }

    return maxRooms;
}

/**
 * Alternative: Difference Array approach (see Topic 5)
 * Works when coordinates are bounded (e.g., 0 to 1000)
 *
 * Time: O(n + maxCoord)
 * Space: O(maxCoord)
 */
public int maxOverlapDiffArray(int[][] intervals, int maxCoord) {
    int[] diff = new int[maxCoord + 2];

    for (int[] interval : intervals) {
        diff[interval[0]]++;
        diff[interval[1] + 1]--;  // +1 because interval is inclusive
    }

    int maxOverlap = 0;
    int running = 0;
    for (int i = 0; i <= maxCoord; i++) {
        running += diff[i];
        maxOverlap = Math.max(maxOverlap, running);
    }

    return maxOverlap;
}

/**
 * Min-Heap approach for Meeting Rooms II
 * Heap stores end times of ongoing meetings.
 * When a new meeting starts, check if earliest-ending meeting is done.
 *
 * Time: O(n log n)
 * Space: O(n)
 */
public int minMeetingRooms(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);  // sort by start

    // Min-heap of end times
    PriorityQueue<Integer> heap = new PriorityQueue<>();

    for (int[] interval : intervals) {
        // If earliest-ending meeting ends before this one starts, reuse room
        if (!heap.isEmpty() && heap.peek() <= interval[0]) {
            heap.poll();  // free that room
        }
        heap.offer(interval[1]);  // assign room, track end time
    }

    return heap.size();  // rooms still occupied = total rooms needed
}
```

---

## Real-World Applications

**Calendar Scheduling:** Google Calendar merges overlapping events for display. When you add a new event, the system checks for conflicts using insert-interval logic. Free/busy queries use sweep line to find available slots.

**Database Range Queries:** Temporal databases store records with validity intervals `[valid_from, valid_to]`. Queries like "find all records valid during [t1, t2]" are interval intersection problems. Index structures like interval trees optimize these queries.

**Network Bandwidth Allocation:** Each network flow has a duration `[start, end]` and bandwidth requirement. The maximum simultaneous bandwidth needed is the maximum overlap problem. ISPs use this to provision capacity.

**Genomics — Gene Overlap Regions:** Genes are annotated as intervals on chromosomes. Finding overlapping genes, computing coverage depth (how many reads cover each base), and identifying regulatory regions all use interval algorithms. Tools like BEDTools implement these operations at scale.

**Video Editing Timeline:** A video editor's timeline is a collection of clips (intervals). Merging clips, finding gaps, and computing total coverage are all interval operations. Non-linear editors like Premiere Pro use interval trees internally.

**Hotel Booking Systems:** Each booking is an interval `[check_in, check_out]`. The minimum number of rooms needed is the maximum overlap problem. Availability queries are interval intersection problems. Overbooking detection is overlap detection.

---

## Problem Categories and Solutions

---

### Category A: Merge and Insert

---

#### LC 56 — Merge Intervals

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, Uber

**Problem:** Given an array of intervals, merge all overlapping intervals and return the result.

**Input:** `[[1,3],[2,6],[8,10],[15,18]]`
**Output:** `[[1,6],[8,10],[15,18]]`

**Key insight:** Sort by start. Walk through maintaining a "current" interval. Extend if overlap, finalize if not.

---

**Dry Run — Step by Step**

Input: `[[1,3],[2,6],[8,10],[15,18]]`

**Step 1: Sort by start time**
Already sorted: `[[1,3],[2,6],[8,10],[15,18]]`

**Step 2: Initialize**
```
current = [1,3]
result  = []
i = 1
```

**Step 3: Process [2,6]**
```
next = [2,6]
next[0]=2 <= current[1]=3?  YES → overlap
current[1] = max(3, 6) = 6
current is now [1,6]

State: current=[1,6], result=[]
```

**Step 4: Process [8,10]**
```
next = [8,10]
next[0]=8 <= current[1]=6?  NO → no overlap
Finalize current: result.add([1,6])
current = [8,10]

State: current=[8,10], result=[[1,6]]
```

**Step 5: Process [15,18]**
```
next = [15,18]
next[0]=15 <= current[1]=10?  NO → no overlap
Finalize current: result.add([8,10])
current = [15,18]

State: current=[15,18], result=[[1,6],[8,10]]
```

**Step 6: After loop**
```
result.add(current=[15,18])
Final result: [[1,6],[8,10],[15,18]]
```

**Tricky case — containment:** `[[1,10],[2,5],[3,7]]`
```
Sort: [[1,10],[2,5],[3,7]]
current = [1,10]
next=[2,5]: 2<=10 → current[1]=max(10,5)=10  (containment! don't shrink end)
next=[3,7]: 3<=10 → current[1]=max(10,7)=10
After loop: add [1,10]
Result: [[1,10]]
```
The `Math.max` is critical — without it, a contained interval would incorrectly shrink the merged result.

```java
public int[][] merge(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

    List<int[]> result = new ArrayList<>();
    int[] current = intervals[0];

    for (int i = 1; i < intervals.length; i++) {
        if (intervals[i][0] <= current[1]) {
            current[1] = Math.max(current[1], intervals[i][1]);
        } else {
            result.add(current);
            current = intervals[i];
        }
    }

    result.add(current);
    return result.toArray(new int[result.size()][]);
}
```

**Complexity:** Time O(n log n), Space O(n)

**Edge cases:**
- Single interval: returns as-is
- All intervals overlap: returns one merged interval
- No intervals overlap: returns original (sorted)
- Containment: `[1,10]` contains `[2,5]` — `Math.max` handles this

---

#### LC 57 — Insert Interval

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given a sorted list of non-overlapping intervals and a new interval, insert the new interval and merge if necessary.

**Input:** `intervals=[[1,3],[6,9]], newInterval=[2,5]`
**Output:** `[[1,5],[6,9]]`

**Key insight:** Three-phase scan. Phase 1: copy intervals ending before new starts. Phase 2: merge all overlapping intervals into new. Phase 3: copy remaining.

```java
public int[][] insert(int[][] intervals, int[] newInterval) {
    List<int[]> result = new ArrayList<>();
    int i = 0, n = intervals.length;

    // Phase 1: intervals ending before newInterval starts
    while (i < n && intervals[i][1] < newInterval[0]) {
        result.add(intervals[i++]);
    }

    // Phase 2: merge overlapping intervals
    while (i < n && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
        newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
        i++;
    }
    result.add(newInterval);

    // Phase 3: remaining intervals
    while (i < n) {
        result.add(intervals[i++]);
    }

    return result.toArray(new int[result.size()][]);
}
```

**Complexity:** Time O(n), Space O(n)

**Dry run:** `intervals=[[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval=[4,8]`
```
Phase 1: [1,2] ends at 2 < 4 → add. [3,5] ends at 5 >= 4 → stop.
  result=[[1,2]], i=1

Phase 2: [3,5] starts at 3 <= 8 → merge. newInterval=[min(4,3),max(8,5)]=[3,8]
         [6,7] starts at 6 <= 8 → merge. newInterval=[min(3,6),max(8,7)]=[3,8]
         [8,10] starts at 8 <= 8 → merge. newInterval=[min(3,8),max(8,10)]=[3,10]
         [12,16] starts at 12 > 8 → stop.
  result=[[1,2],[3,10]], i=4

Phase 3: [12,16] → add.
  result=[[1,2],[3,10],[12,16]]
```

**Edge cases:**
- New interval before all: only Phase 3 runs
- New interval after all: only Phase 1 runs
- New interval contains all: Phase 2 merges everything

---

#### LC 1288 — Remove Covered Intervals

**Companies:** Amazon, Google, Microsoft

**Problem:** Given a list of intervals, remove all intervals that are covered by another interval. Return the number of remaining intervals.

An interval `[a,b]` is covered by `[c,d]` if `c <= a` and `b <= d`.

**Input:** `[[1,4],[3,6],[2,8]]`
**Output:** `2` (intervals [1,4] and [3,6] are covered by [2,8])

**Key insight:** Sort by start ascending, then by end descending (for equal starts). Walk through tracking the maximum end seen so far. If current interval's end <= maxEnd, it's covered.

Why sort end descending for equal starts? If two intervals start at the same point, the longer one covers the shorter one. By putting the longer one first, we set maxEnd high and the shorter one gets correctly identified as covered.

```java
public int removeCoveredIntervals(int[][] intervals) {
    // Sort: start ascending, end descending for ties
    Arrays.sort(intervals, (a, b) -> a[0] != b[0] ? a[0] - b[0] : b[1] - a[1]);

    int count = 0;
    int maxEnd = 0;

    for (int[] interval : intervals) {
        if (interval[1] > maxEnd) {
            // Not covered — this interval extends beyond anything seen
            count++;
            maxEnd = interval[1];
        }
        // else: interval[1] <= maxEnd → covered by a previous interval
    }

    return count;
}
```

**Complexity:** Time O(n log n), Space O(1)

**Dry run:** `[[1,4],[3,6],[2,8]]`
```
Sort: [[1,4],[2,8],[3,6]]
  (start asc; for equal starts, end desc — no ties here)

i=0: [1,4], maxEnd=0. 4>0 → count=1, maxEnd=4
i=1: [2,8], maxEnd=4. 8>4 → count=2, maxEnd=8
i=2: [3,6], maxEnd=8. 6<=8 → covered, skip

Result: 2
```

---

### Category B: Scheduling and Overlap

---

#### LC 252 — Meeting Rooms (Premium)

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given an array of meeting time intervals, determine if a person could attend all meetings (no two meetings overlap).

**Input:** `[[0,30],[5,10],[15,20]]`
**Output:** `false` (meetings [0,30] and [5,10] overlap)

**Key insight:** Sort by start. Check if any adjacent pair overlaps: `intervals[i][0] < intervals[i-1][1]`.

```java
public boolean canAttendMeetings(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

    for (int i = 1; i < intervals.length; i++) {
        // If next meeting starts before previous ends → conflict
        if (intervals[i][0] < intervals[i - 1][1]) {
            return false;
        }
    }

    return true;
}
```

**Complexity:** Time O(n log n), Space O(1)

**Note:** The overlap check is `<` not `<=` here. A meeting ending at 10 and another starting at 10 is fine — they don't overlap (one ends exactly when the other begins).

---

#### LC 253 — Meeting Rooms II (Premium)

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given an array of meeting time intervals, find the minimum number of conference rooms required.

**Input:** `[[0,30],[5,10],[15,20]]`
**Output:** `2`

**Key insight:** Sort by start. Use a min-heap of end times. For each meeting, if the earliest-ending meeting ends before this one starts, reuse that room. Otherwise, allocate a new room. The heap size is the answer.

---

**Step-by-Step Heap Trace**

Input: `[[0,30],[5,10],[15,20]]`

**Sort by start:** `[[0,30],[5,10],[15,20]]` (already sorted)

**Initialize:** `heap = []` (min-heap of end times)

**Process [0,30]:**
```
heap is empty → no room to reuse
heap.offer(30)
heap = [30]
rooms = 1
```

**Process [5,10]:**
```
heap.peek() = 30
30 <= 5?  NO → can't reuse room (meeting at [0,30] still ongoing)
heap.offer(10)
heap = [10, 30]
rooms = 2
```

**Process [15,20]:**
```
heap.peek() = 10
10 <= 15?  YES → meeting ending at 10 is done, reuse its room
heap.poll()  → removes 10
heap.offer(20)
heap = [20, 30]
rooms = 2 (heap size unchanged — we reused a room)
```

**Final answer:** `heap.size() = 2`

**Why this works:** The heap always contains the end times of all currently-occupied rooms. When a new meeting starts, we check if the room that frees up soonest (min end time) is available. If yes, we reuse it (pop old end, push new end — net size unchanged). If no, we need a new room (just push — size increases).

```java
public int minMeetingRooms(int[][] intervals) {
    if (intervals.length == 0) return 0;

    // Sort by start time
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

    // Min-heap: tracks end times of ongoing meetings
    PriorityQueue<Integer> heap = new PriorityQueue<>();

    for (int[] interval : intervals) {
        // If earliest-ending meeting ends at or before this meeting starts
        if (!heap.isEmpty() && heap.peek() <= interval[0]) {
            heap.poll();  // free that room
        }
        heap.offer(interval[1]);  // occupy a room until interval[1]
    }

    return heap.size();
}
```

**Complexity:** Time O(n log n), Space O(n)

**Second trace — all overlap:** `[[1,5],[2,6],[3,7]]`
```
Sort: [[1,5],[2,6],[3,7]]

[1,5]: heap=[], offer(5). heap=[5]
[2,6]: peek=5, 5<=2? NO. offer(6). heap=[5,6]
[3,7]: peek=5, 5<=3? NO. offer(7). heap=[5,6,7]

Answer: 3 (all three meetings overlap)
```

**Third trace — sequential:** `[[1,2],[3,4],[5,6]]`
```
Sort: [[1,2],[3,4],[5,6]]

[1,2]: heap=[], offer(2). heap=[2]
[3,4]: peek=2, 2<=3? YES. poll(). offer(4). heap=[4]
[5,6]: peek=4, 4<=5? YES. poll(). offer(6). heap=[6]

Answer: 1 (one room suffices)
```

**Alternative: Sweep Line approach**
```java
public int minMeetingRoomsSweep(int[][] intervals) {
    int n = intervals.length;
    int[] starts = new int[n];
    int[] ends = new int[n];

    for (int i = 0; i < n; i++) {
        starts[i] = intervals[i][0];
        ends[i] = intervals[i][1];
    }

    Arrays.sort(starts);
    Arrays.sort(ends);

    int rooms = 0, endPtr = 0;

    for (int i = 0; i < n; i++) {
        if (starts[i] < ends[endPtr]) {
            rooms++;  // need a new room
        } else {
            endPtr++;  // reuse a room (a meeting ended)
        }
    }

    return rooms;
}
```

This two-pointer sweep is elegant: sort starts and ends separately. For each start, if it's before the earliest end, we need a new room. Otherwise, a room freed up.

---

#### LC 435 — Non-overlapping Intervals

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given an array of intervals, find the minimum number of intervals to remove to make the rest non-overlapping.

**Input:** `[[1,2],[2,3],[3,4],[1,3]]`
**Output:** `1` (remove [1,3])

**Key insight:** Greedy. Sort by end time. Keep an interval if it doesn't overlap with the last kept interval. The number of removed intervals = total - kept.

Why sort by end? We want to keep as many intervals as possible. Greedily keeping the interval that ends earliest leaves the most room for future intervals — classic activity selection problem.

```java
public int eraseOverlapIntervals(int[][] intervals) {
    if (intervals.length == 0) return 0;

    // Sort by end time (greedy: keep intervals ending earliest)
    Arrays.sort(intervals, (a, b) -> a[1] - b[1]);

    int kept = 1;
    int lastEnd = intervals[0][1];

    for (int i = 1; i < intervals.length; i++) {
        if (intervals[i][0] >= lastEnd) {
            // No overlap with last kept interval → keep this one
            kept++;
            lastEnd = intervals[i][1];
        }
        // else: overlap → skip (remove) this interval
    }

    return intervals.length - kept;
}
```

**Complexity:** Time O(n log n), Space O(1)

**Dry run:** `[[1,2],[2,3],[3,4],[1,3]]`
```
Sort by end: [[1,2],[2,3],[1,3],[3,4]]

kept=1, lastEnd=2

[2,3]: start=2 >= lastEnd=2? YES → keep. kept=2, lastEnd=3
[1,3]: start=1 >= lastEnd=3? NO → remove
[3,4]: start=3 >= lastEnd=3? YES → keep. kept=3, lastEnd=4

Removed = 4 - 3 = 1
```

**Why >= not >?** Two intervals touching at a point (e.g., [1,2] and [2,3]) do NOT overlap — they share only a boundary point. So `start >= lastEnd` means "no overlap."

---

#### LC 452 — Minimum Number of Arrows to Burst Balloons

**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Balloons are represented as intervals on a horizontal axis. An arrow shot at position x bursts all balloons where `x_start <= x <= x_end`. Find the minimum number of arrows to burst all balloons.

**Input:** `[[10,16],[2,8],[1,6],[7,12]]`
**Output:** `2`

**Key insight:** Same greedy as LC 435. Sort by end. An arrow at the end of the first balloon bursts all overlapping balloons. When a balloon doesn't overlap with the current arrow position, shoot a new arrow.

```java
public int findMinArrowShots(int[][] points) {
    if (points.length == 0) return 0;

    // Sort by end position
    Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));
    // Use Integer.compare to avoid overflow with large values

    int arrows = 1;
    int arrowPos = points[0][1];  // shoot at end of first balloon

    for (int i = 1; i < points.length; i++) {
        if (points[i][0] > arrowPos) {
            // This balloon starts after current arrow → need new arrow
            arrows++;
            arrowPos = points[i][1];
        }
        // else: current arrow bursts this balloon too
    }

    return arrows;
}
```

**Complexity:** Time O(n log n), Space O(1)

**Dry run:** `[[10,16],[2,8],[1,6],[7,12]]`
```
Sort by end: [[1,6],[2,8],[7,12],[10,16]]

arrows=1, arrowPos=6

[2,8]: start=2 > arrowPos=6? NO → arrow at 6 bursts [2,8] too
[7,12]: start=7 > arrowPos=6? YES → new arrow. arrows=2, arrowPos=12
[10,16]: start=10 > arrowPos=12? NO → arrow at 12 bursts [10,16] too

Answer: 2
```

**Difference from LC 435:** Here the overlap condition is `>` not `>=`. Two balloons touching at a point (e.g., [1,6] and [6,10]) CAN be burst by one arrow at position 6. So we only need a new arrow when `start > arrowPos` (strictly greater).

**Integer overflow warning:** Balloon coordinates can be up to 2^31 - 1. Use `Integer.compare(a[1], b[1])` instead of `a[1] - b[1]` in the comparator to avoid overflow.

---

### Category C: Intersection and Gap

---

#### LC 986 — Interval List Intersections

**Companies:** Amazon, Google, Meta, Microsoft, DoorDash, Uber

**Cross-reference:** Topic 2 (Two Pointers) — this is a two-pointer merge on two sorted lists.

**Problem:** Given two lists of sorted, non-overlapping intervals, return their intersection.

**Input:** `A=[[0,2],[5,10],[13,23],[24,25]]`, `B=[[1,5],[8,12],[15,24],[25,26]]`
**Output:** `[[1,2],[5,5],[8,10],[15,23],[24,24],[25,25]]`

**Key insight:** Two pointers, one per list. At each step, compute the intersection of the current pair. Advance the pointer whose interval ends first (it can't intersect with anything further in the other list).

```java
public int[][] intervalIntersection(int[][] firstList, int[][] secondList) {
    List<int[]> result = new ArrayList<>();
    int i = 0, j = 0;

    while (i < firstList.length && j < secondList.length) {
        // Intersection: max of starts, min of ends
        int lo = Math.max(firstList[i][0], secondList[j][0]);
        int hi = Math.min(firstList[i][1], secondList[j][1]);

        if (lo <= hi) {
            // Valid intersection (lo <= hi means they overlap)
            result.add(new int[]{lo, hi});
        }

        // Advance the pointer whose interval ends first
        if (firstList[i][1] < secondList[j][1]) {
            i++;
        } else {
            j++;
        }
    }

    return result.toArray(new int[result.size()][]);
}
```

**Complexity:** Time O(m + n), Space O(m + n)

**Why advance the one ending first?** The interval ending first cannot intersect with any future interval in the other list (since those start at or after the current interval in the other list, which already ends after our current interval). So we're done with it.

**Dry run (partial):** `A=[[0,2],[5,10]]`, `B=[[1,5],[8,12]]`
```
i=0, j=0: A=[0,2], B=[1,5]
  lo=max(0,1)=1, hi=min(2,5)=2. 1<=2 → add [1,2]
  A ends at 2 < B ends at 5 → i++

i=1, j=0: A=[5,10], B=[1,5]
  lo=max(5,1)=5, hi=min(10,5)=5. 5<=5 → add [5,5]
  A ends at 10 > B ends at 5 → j++

i=1, j=1: A=[5,10], B=[8,12]
  lo=max(5,8)=8, hi=min(10,12)=10. 8<=10 → add [8,10]
  A ends at 10 < B ends at 12 → i++

i=2: out of bounds → stop
Result: [[1,2],[5,5],[8,10]]
```

---

#### LC 759 — Employee Free Time (Premium)

**Companies:** Amazon, Google, Meta

**Problem:** Given a list of employees, each with a list of non-overlapping working intervals (sorted), find the list of finite intervals representing the free time common to all employees.

**Input:** `schedule = [[[1,3],[6,7]],[[2,4]],[[2,5],[9,12]]]`
**Output:** `[[5,6],[7,9]]`

**Key insight:** Flatten all intervals into one list, sort by start, then find gaps between merged intervals. The gaps are the free time.

```java
public List<Interval> employeeFreeTime(List<List<Interval>> schedule) {
    // Flatten all intervals
    List<int[]> all = new ArrayList<>();
    for (List<Interval> employee : schedule) {
        for (Interval interval : employee) {
            all.add(new int[]{interval.start, interval.end});
        }
    }

    // Sort by start
    all.sort((a, b) -> a[0] - b[0]);

    List<Interval> result = new ArrayList<>();
    int[] current = all.get(0);

    for (int i = 1; i < all.size(); i++) {
        int[] next = all.get(i);
        if (next[0] <= current[1]) {
            // Overlap: extend current
            current[1] = Math.max(current[1], next[1]);
        } else {
            // Gap found: [current.end, next.start] is free time
            result.add(new Interval(current[1], next[0]));
            current = next;
        }
    }

    return result;
}
```

**Complexity:** Time O(n log n) where n = total intervals, Space O(n)

**Key observation:** We don't need to track which employee owns which interval. Free time is simply the gaps in the merged union of all working intervals. Flatten, sort, merge, find gaps.

**Alternative using PriorityQueue (K-way merge style):**
```java
public List<Interval> employeeFreeTimeHeap(List<List<Interval>> schedule) {
    // Min-heap: [start, end, employeeIdx, intervalIdx]
    PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

    for (int i = 0; i < schedule.size(); i++) {
        heap.offer(new int[]{schedule.get(i).get(0).start,
                             schedule.get(i).get(0).end, i, 0});
    }

    List<Interval> result = new ArrayList<>();
    int prevEnd = heap.peek()[0];  // start from earliest interval

    while (!heap.isEmpty()) {
        int[] curr = heap.poll();
        int start = curr[0], end = curr[1];
        int ei = curr[2], ii = curr[3];

        if (start > prevEnd) {
            result.add(new Interval(prevEnd, start));
        }
        prevEnd = Math.max(prevEnd, end);

        // Add next interval from same employee
        if (ii + 1 < schedule.get(ei).size()) {
            Interval next = schedule.get(ei).get(ii + 1);
            heap.offer(new int[]{next.start, next.end, ei, ii + 1});
        }
    }

    return result;
}
```

The heap approach is useful when intervals are already sorted per employee and you want to avoid flattening (e.g., streaming data).

---

#### LC 763 — Partition Labels

**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** A string is partitioned into as many parts as possible so that each letter appears in at most one part. Return a list of sizes of these parts.

**Input:** `s = "ababcbacadefegdehijhklij"`
**Output:** `[9,7,8]`

**Key insight:** This is an interval problem in disguise. For each character, find its first and last occurrence — that's its interval. Then merge all character intervals. Each merged interval is one partition.

```java
public List<Integer> partitionLabels(String s) {
    // Step 1: Find last occurrence of each character
    int[] last = new int[26];
    for (int i = 0; i < s.length(); i++) {
        last[s.charAt(i) - 'a'] = i;
    }

    // Step 2: Greedy interval merge
    List<Integer> result = new ArrayList<>();
    int start = 0, end = 0;

    for (int i = 0; i < s.length(); i++) {
        // Extend current partition to include last occurrence of s[i]
        end = Math.max(end, last[s.charAt(i) - 'a']);

        if (i == end) {
            // Reached the end of current partition
            result.add(end - start + 1);
            start = i + 1;
        }
    }

    return result;
}
```

**Complexity:** Time O(n), Space O(1) (26-char alphabet)

**Dry run:** `s = "ababcbacadefegdehijhklij"`
```
Last occurrences:
a→8, b→5, c→7, d→14, e→15, f→11, g→13, h→19, i→22, j→23, k→20, l→21

i=0 (a): end=max(0,8)=8
i=1 (b): end=max(8,5)=8
i=2 (a): end=max(8,8)=8
i=3 (b): end=max(8,5)=8
i=4 (c): end=max(8,7)=8
i=5 (b): end=max(8,5)=8
i=6 (a): end=max(8,8)=8
i=7 (c): end=max(8,7)=8
i=8 (a): end=max(8,8)=8. i==end → partition size=8-0+1=9, start=9

i=9 (d): end=max(9,14)=14
...
i=15 (e): end=max(14,15)=15. i==end → partition size=15-9+1=7, start=16

i=16 (h): end=max(16,19)=19
...
i=23 (j): end=max(22,23)=23. i==end → partition size=23-16+1=8, start=24

Result: [9,7,8]
```

**Why this works:** We're essentially merging character intervals. The "end" variable tracks the rightmost boundary of the current merged interval. When we reach that boundary, we've found a complete partition.

---

### Category D: Advanced

---

#### LC 1094 — Car Pooling

**Companies:** Amazon, Google, Microsoft, Uber

**Cross-reference:** Topic 5 (Difference Arrays) — this is a direct application of the difference array technique.

**Problem:** A car has `capacity` seats. Given trips `[numPassengers, from, to]`, determine if all trips can be completed without exceeding capacity at any point.

**Input:** `trips=[[2,1,5],[3,3,7]], capacity=4`
**Output:** `false` (at stop 3, 2+3=5 passengers > capacity 4)

**Key insight:** Use a difference array. At each `from`, add `numPassengers`. At each `to`, subtract `numPassengers`. Compute prefix sum and check if any point exceeds capacity.

```java
public boolean carPooling(int[][] trips, int capacity) {
    // Stops are in range [0, 1000] per constraints
    int[] diff = new int[1001];

    for (int[] trip : trips) {
        int passengers = trip[0];
        int from = trip[1];
        int to = trip[2];

        diff[from] += passengers;   // passengers board at 'from'
        diff[to] -= passengers;     // passengers leave at 'to'
        // Note: passengers leave AT 'to', so we subtract at 'to' (not to+1)
        // because they're not in the car during the 'to' stop
    }

    // Compute prefix sum and check capacity
    int current = 0;
    for (int i = 0; i <= 1000; i++) {
        current += diff[i];
        if (current > capacity) return false;
    }

    return true;
}
```

**Complexity:** Time O(n + maxStop), Space O(maxStop)

**Why subtract at `to` not `to+1`?** Passengers leave at the destination stop, so they're not in the car at that stop. The problem says passengers travel from `from` to `to` — they're in the car during `[from, to)`.

**Alternative: Sort events**
```java
public boolean carPoolingEvents(int[][] trips, int capacity) {
    // Create events: [stop, +passengers or -passengers]
    List<int[]> events = new ArrayList<>();
    for (int[] trip : trips) {
        events.add(new int[]{trip[1], trip[0]});   // board
        events.add(new int[]{trip[2], -trip[0]});  // leave
    }

    // Sort by stop; at same stop, process departures before arrivals
    events.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);

    int current = 0;
    for (int[] event : events) {
        current += event[1];
        if (current > capacity) return false;
    }

    return true;
}
```

---

#### LC 1851 — Minimum Interval to Include Each Query

**Companies:** Google, Amazon

**Problem:** Given intervals and queries, for each query find the size of the smallest interval that contains the query point. Size = end - start + 1.

**Input:** `intervals=[[1,4],[2,4],[3,6],[4,4]], queries=[2,3,4,5]`
**Output:** `[3,3,1,4]`

**Key insight:** Offline processing. Sort both intervals and queries. Use a min-heap of `(size, end)` for active intervals. For each query (in sorted order), add all intervals starting at or before the query, then remove intervals that have ended. The heap top is the smallest active interval.

```java
public int[] minInterval(int[][] intervals, int[] queries) {
    int n = queries.length;

    // Sort intervals by start
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

    // Sort queries but keep original indices for output
    Integer[] queryIdx = new Integer[n];
    for (int i = 0; i < n; i++) queryIdx[i] = i;
    Arrays.sort(queryIdx, (a, b) -> queries[a] - queries[b]);

    // Min-heap: [size, end] — smallest size at top
    PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

    int[] result = new int[n];
    int i = 0;  // pointer into sorted intervals

    for (int qi : queryIdx) {
        int q = queries[qi];

        // Add all intervals starting at or before q
        while (i < intervals.length && intervals[i][0] <= q) {
            int size = intervals[i][1] - intervals[i][0] + 1;
            heap.offer(new int[]{size, intervals[i][1]});
            i++;
        }

        // Remove intervals that have ended before q
        while (!heap.isEmpty() && heap.peek()[1] < q) {
            heap.poll();
        }

        // Smallest active interval
        result[qi] = heap.isEmpty() ? -1 : heap.peek()[0];
    }

    return result;
}
```

**Complexity:** Time O((n + m) log n) where n = intervals, m = queries, Space O(n + m)

**Why offline?** We need to process queries in sorted order to efficiently add/remove intervals. But the output must be in original query order — hence the `queryIdx` indirection.

**Dry run:** `intervals=[[1,4],[2,4],[3,6],[4,4]], queries=[2,3,4,5]`
```
Sort intervals by start: [[1,4],[2,4],[3,6],[4,4]]
Sort queries: [2,3,4,5] (already sorted), queryIdx=[0,1,2,3]

q=2 (qi=0):
  Add [1,4]: size=4, heap=[(4,4)]
  Add [2,4]: size=3, heap=[(3,4),(4,4)]
  Remove ended: heap.peek()=(3,4), end=4>=2 → stop
  result[0] = 3

q=3 (qi=1):
  Add [3,6]: size=4, heap=[(3,4),(4,4),(4,6)]
  Remove ended: heap.peek()=(3,4), end=4>=3 → stop
  result[1] = 3

q=4 (qi=2):
  Add [4,4]: size=1, heap=[(1,4),(3,4),(4,4),(4,6)]
  Remove ended: heap.peek()=(1,4), end=4>=4 → stop
  result[2] = 1

q=5 (qi=3):
  No new intervals (all start <= 5 already added)
  Remove ended: heap.peek()=(1,4), end=4<5 → poll
                heap.peek()=(3,4), end=4<5 → poll
                heap.peek()=(4,4), end=4<5 → poll
                heap.peek()=(4,6), end=6>=5 → stop
  result[3] = 4

Output: [3,3,1,4]
```

---

## Common Mistakes

### 1. Not Sorting First

The most common mistake. Without sorting by start time, you cannot make local decisions — you'd need to compare every pair.

```java
// WRONG: processing unsorted intervals
for (int[] interval : intervals) {
    // Can't determine overlap without knowing what came before
}

// CORRECT: always sort first
Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
```

**Exception:** If the problem guarantees sorted input (LC 57, LC 986), you can skip the sort.

---

### 2. Wrong Overlap Condition

The overlap condition depends on whether touching counts as overlap.

```java
// Touching intervals [1,3] and [3,5]:
// - For MERGE: they should merge → use <=
if (next[0] <= current[1])  // [1,3] and [3,5] → merge to [1,5]

// - For MEETING ROOMS: a meeting ending at 3 and starting at 3 is fine → use <
if (intervals[i][0] < intervals[i-1][1])  // overlap = conflict

// - For ARROWS: arrow at 3 bursts both [1,3] and [3,5] → use >
if (points[i][0] > arrowPos)  // need new arrow only if strictly after
```

**Rule of thumb:**
- Merge intervals: `<=` (touching merges)
- Meeting rooms conflict: `<` (touching is fine)
- Arrows: `>` (touching means same arrow works)

---

### 3. Mutating Input Array

When the problem says "do not modify input" or when you need the original later:

```java
// WRONG: modifying input intervals directly
intervals[i][1] = Math.max(intervals[i][1], intervals[j][1]);

// CORRECT: work with a copy or use a separate variable
int[] current = Arrays.copyOf(intervals[0], 2);
// or
int[] current = new int[]{intervals[0][0], intervals[0][1]};
```

In LC 57 (Insert Interval), mutating `newInterval` is fine because it's a parameter you own. But mutating elements of `intervals` is risky.

---

### 4. Forgetting the Last Interval

In merge-style loops, the last interval is never added inside the loop (because the loop only adds when it finds a non-overlapping next interval):

```java
for (int i = 1; i < intervals.length; i++) {
    if (overlap) {
        extend current;
    } else {
        result.add(current);  // adds previous, not current
        current = intervals[i];
    }
}
result.add(current);  // CRITICAL: add the last interval
```

---

### 5. Single-Point Intervals

An interval `[x, x]` is valid and represents a single point. Make sure your overlap condition handles it:

```java
// [3,3] and [3,5]: do they overlap?
// next[0]=3 <= current[1]=3 → YES, they overlap (merge to [3,5])
// This is correct behavior for merge intervals
```

---

### 6. Integer Overflow in Comparators

When interval coordinates can be large (up to Integer.MAX_VALUE), subtraction in comparators overflows:

```java
// WRONG: can overflow if a[1] is very negative and b[1] is very positive
Arrays.sort(points, (a, b) -> a[1] - b[1]);

// CORRECT: use Integer.compare
Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));
```

LC 452 (Arrows) explicitly has coordinates up to 2^31 - 1. Always use `Integer.compare` for safety.

---

### 7. Off-by-One in Difference Arrays

In Car Pooling, passengers leave AT the destination, not after:

```java
// WRONG: subtracting at to+1 means passengers are counted at 'to'
diff[trip[2] + 1] -= trip[0];

// CORRECT: subtract at 'to' because passengers leave at that stop
diff[trip[2]] -= trip[0];
```

Read the problem carefully: "from stop A to stop B" — are they in the car at stop B or not?

---

### 8. Not Handling Empty Input

```java
// Always guard against empty input
if (intervals == null || intervals.length == 0) return new int[0][];
```

---

### 9. Wrong Sort for Remove Covered Intervals

LC 1288 requires sorting by start ascending, end descending for ties. A common mistake is sorting only by start:

```java
// WRONG: only sort by start
Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
// Problem: [2,4] and [2,8] — [2,4] comes first, maxEnd=4
// Then [2,8]: 8>4 → counted as non-covered. But [2,4] IS covered by [2,8]!

// CORRECT: sort by start asc, end desc for ties
Arrays.sort(intervals, (a, b) -> a[0] != b[0] ? a[0] - b[0] : b[1] - a[1]);
// Now [2,8] comes before [2,4]. maxEnd=8. Then [2,4]: 4<=8 → correctly covered.
```

---

### 10. Heap Condition in Meeting Rooms II

The condition for reusing a room is `heap.peek() <= interval[0]`, not `< interval[0]`:

```java
// WRONG: strict less than
if (!heap.isEmpty() && heap.peek() < interval[0]) {
    heap.poll();
}
// Problem: meeting ending at 5 and new meeting starting at 5 — they don't overlap.
// The room IS free at time 5. We should reuse it.

// CORRECT: less than or equal
if (!heap.isEmpty() && heap.peek() <= interval[0]) {
    heap.poll();
}
```

A meeting ending at time T means the room is free starting at time T. A new meeting starting at time T can use that room.

---

## Algorithm Comparison

### Sort + Merge vs Sweep Line vs Difference Array vs Heap

| Approach | When to Use | Time | Space | Example Problems |
|---|---|---|---|---|
| Sort + Merge | Merge/simplify overlapping intervals | O(n log n) | O(n) | LC 56, 1288 |
| Sort + Greedy | Minimize/maximize count of intervals | O(n log n) | O(1) | LC 435, 452, 252 |
| Insert (3-phase) | Insert into sorted non-overlapping list | O(n) | O(n) | LC 57 |
| Two Pointers | Intersect two sorted interval lists | O(m+n) | O(1) | LC 986 |
| Sweep Line (events) | Count max overlaps, find free time | O(n log n) | O(n) | LC 253 (alt), 759 |
| Difference Array | Range updates with bounded coordinates | O(n+K) | O(K) | LC 1094 |
| Min-Heap | Scheduling with room reuse | O(n log n) | O(n) | LC 253, 1851 |
| Offline + Heap | Queries on intervals | O((n+m) log n) | O(n+m) | LC 1851 |

---

### When to Use Each

**Sort + Merge (LC 56 style):**
- You need to produce a simplified list of non-overlapping intervals
- You're combining/reducing intervals
- Signal: "merge all overlapping intervals"

**Sort + Greedy (LC 435, 452 style):**
- You want to keep/remove as few intervals as possible
- Greedy choice: always pick the interval ending earliest
- Signal: "minimum number to remove/add"

**Insert (LC 57 style):**
- Input is already sorted and non-overlapping
- You're adding one new interval
- Signal: "insert interval into sorted list"

**Two Pointers (LC 986 style):**
- Two sorted interval lists, find their intersection
- Signal: "intersection of two interval lists"

**Sweep Line:**
- You need to know how many intervals overlap at any point
- You need to find gaps (free time)
- Signal: "maximum overlap", "free time", "minimum rooms"

**Difference Array (LC 1094 style):**
- Coordinates are bounded (small range)
- Many range updates, then one scan
- Signal: "range updates", bounded coordinates, "capacity at each point"

**Min-Heap (LC 253 style):**
- You need to track which intervals are "active" as you sweep
- You need to reuse resources (rooms, workers)
- Signal: "minimum rooms/workers needed"

**Offline + Heap (LC 1851 style):**
- Queries on intervals that can be answered after sorting
- Need to efficiently add/remove intervals as query point moves
- Signal: "for each query, find the [smallest/largest/count] interval containing it"

---

### Sweep Line vs Difference Array

Both solve "count overlaps at each point." The difference is in constraints:

**Difference Array:**
- Coordinates are bounded (e.g., 0 to 1000)
- O(maxCoord) space — fine when maxCoord is small
- Simpler to implement
- Cannot handle floating-point or very large coordinates

**Sweep Line (event-based):**
- Works for any coordinate range
- O(n) space — only stores events, not all coordinates
- Handles floating-point coordinates
- More flexible (can handle open/closed intervals differently)

**Rule:** If coordinates are bounded and small, use difference array. Otherwise, use sweep line.

---

### Heap vs Sweep Line for Meeting Rooms II

Both solve LC 253 in O(n log n). The heap approach is more general:

**Heap approach:**
- Tracks which specific room ends when
- Can be extended to assign meetings to specific rooms
- Useful when you need to know the actual assignment, not just the count

**Sweep line (two-pointer) approach:**
- Only counts rooms, doesn't track assignments
- Slightly simpler code
- Cannot tell you which room each meeting goes in

**In interviews:** The heap approach is more commonly expected because it demonstrates understanding of the greedy choice (reuse the room that frees up soonest).

---

## Quick Reference Cheat Sheet

### Overlap Conditions

```
Intervals [a,b] and [c,d]:

Overlap (strict):    a < d AND c < b
Overlap (touching):  a <= d AND c <= b
No overlap:          b < c OR d < a  (one ends before other starts)
Contains:            a <= c AND d <= b  ([a,b] contains [c,d])
```

### Sort Strategies

```
Sort by start (most common):
  Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

Sort by end (greedy: activity selection):
  Arrays.sort(intervals, (a, b) -> a[1] - b[1]);

Sort by start, break ties by end descending (remove covered):
  Arrays.sort(intervals, (a, b) -> a[0] != b[0] ? a[0] - b[0] : b[1] - a[1]);

Safe sort (avoid overflow):
  Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
```

### Template Skeletons

```java
// Merge intervals
Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
int[] cur = intervals[0];
for (int i = 1; i < intervals.length; i++) {
    if (intervals[i][0] <= cur[1]) cur[1] = Math.max(cur[1], intervals[i][1]);
    else { result.add(cur); cur = intervals[i]; }
}
result.add(cur);

// Insert interval (3 phases)
while (i < n && intervals[i][1] < newInterval[0]) result.add(intervals[i++]);
while (i < n && intervals[i][0] <= newInterval[1]) {
    newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
    newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
    i++;
}
result.add(newInterval);
while (i < n) result.add(intervals[i++]);

// Meeting rooms II (heap)
Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
PriorityQueue<Integer> heap = new PriorityQueue<>();
for (int[] iv : intervals) {
    if (!heap.isEmpty() && heap.peek() <= iv[0]) heap.poll();
    heap.offer(iv[1]);
}
return heap.size();

// Sweep line (max overlap)
// events: [time, +1 or -1]
Arrays.sort(events, (a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);
int max = 0, cur = 0;
for (int[] e : events) { cur += e[1]; max = Math.max(max, cur); }

// Difference array (bounded coords)
int[] diff = new int[MAX + 1];
for (int[] iv : intervals) { diff[iv[0]]++; diff[iv[1]]--; }
int cur = 0, max = 0;
for (int x : diff) { cur += x; max = Math.max(max, cur); }
```

### Problem → Approach Map

```
LC 56  Merge Intervals              → Sort by start + merge
LC 57  Insert Interval              → 3-phase insert
LC 252 Meeting Rooms                → Sort by start + check adjacent
LC 253 Meeting Rooms II             → Sort by start + min-heap of end times
LC 435 Non-overlapping Intervals    → Sort by end + greedy keep
LC 452 Minimum Arrows               → Sort by end + greedy shoot
LC 986 Interval List Intersections  → Two pointers on two sorted lists
LC 759 Employee Free Time           → Flatten + merge + find gaps
LC 763 Partition Labels             → Last occurrence array + greedy
LC 1094 Car Pooling                 → Difference array
LC 1288 Remove Covered Intervals    → Sort (start asc, end desc) + track maxEnd
LC 1851 Min Interval per Query      → Offline sort + min-heap
```

### Complexity Summary

```
Problem                     Time        Space
LC 56  Merge                O(n log n)  O(n)
LC 57  Insert               O(n)        O(n)
LC 252 Meeting Rooms        O(n log n)  O(1)
LC 253 Meeting Rooms II     O(n log n)  O(n)
LC 435 Non-overlapping      O(n log n)  O(1)
LC 452 Min Arrows           O(n log n)  O(1)
LC 986 Intersections        O(m+n)      O(m+n)
LC 759 Employee Free Time   O(n log n)  O(n)
LC 763 Partition Labels     O(n)        O(1)
LC 1094 Car Pooling         O(n+K)      O(K)
LC 1288 Remove Covered      O(n log n)  O(1)
LC 1851 Min Interval/Query  O((n+m)logn) O(n+m)
```

---

## Practice Roadmap

### Week 1 — Foundation (Days 1–7)

**Day 1–2: Core merge pattern**
- LC 56 (Merge Intervals) — implement from scratch, trace through examples
- LC 57 (Insert Interval) — implement 3-phase approach
- Goal: internalize the sort-then-sweep pattern

**Day 3–4: Scheduling**
- LC 252 (Meeting Rooms) — warm-up, should be fast
- LC 253 (Meeting Rooms II) — implement heap approach, trace through 3 examples
- Goal: understand when heap is needed vs simple greedy

**Day 5–6: Greedy variants**
- LC 435 (Non-overlapping Intervals) — sort by end, greedy keep
- LC 452 (Minimum Arrows) — same pattern, different overlap condition
- Goal: recognize activity selection pattern, understand < vs <= vs >

**Day 7: Review**
- Re-implement LC 56 and LC 253 without looking at notes
- Time yourself: LC 56 should take < 10 minutes, LC 253 < 15 minutes

---

### Week 2 — Advanced (Days 8–14)

**Day 8–9: Intersection and gaps**
- LC 986 (Interval List Intersections) — two-pointer pattern (cross-ref Topic 2)
- LC 759 (Employee Free Time) — flatten + merge + find gaps
- Goal: recognize two-pointer on sorted interval lists

**Day 10–11: Disguised interval problems**
- LC 763 (Partition Labels) — recognize as interval merge
- LC 1288 (Remove Covered Intervals) — sort trick for containment
- Goal: identify interval problems that don't look like interval problems

**Day 12–13: Advanced techniques**
- LC 1094 (Car Pooling) — difference array (cross-ref Topic 5)
- LC 1851 (Min Interval per Query) — offline processing + heap
- Goal: know when to use difference array vs sweep line vs heap

**Day 14: Mock interview**
- Pick 2 problems randomly from the list
- Solve under timed conditions (20 min each)
- Verbalize your approach before coding

---

### Problem Difficulty Progression

```
Easy:    LC 252 (Meeting Rooms)
Medium:  LC 56, 57, 435, 452, 986, 763, 1094, 1288
Hard:    LC 253, 759, 1851
```

### Must-Solve Before Any FAANG Interview

1. LC 56 — Merge Intervals (appears at every company)
2. LC 57 — Insert Interval (Google, Amazon favorite)
3. LC 253 — Meeting Rooms II (heap approach, very common)
4. LC 435 — Non-overlapping Intervals (greedy pattern)
5. LC 986 — Interval List Intersections (two-pointer pattern)

### Red Flags in Your Solution

- You're not sorting first → wrong approach
- Your merge condition uses `<` instead of `<=` → off-by-one
- You forgot to add the last interval after the loop → missing output
- Your comparator uses subtraction with large values → overflow
- You're using O(n²) nested loops → missed the sort-then-sweep insight

---

### Interview Tips

**When you see an interval problem:**
1. Ask: are the intervals sorted? (changes your approach)
2. Ask: do touching intervals count as overlapping? (changes your condition)
3. State: "I'll sort by start time first" — this signals you know the pattern
4. For counting overlaps: mention both heap and sweep line, then pick one

**Explaining your approach:**
- "After sorting by start, I can process intervals left to right with a single pass"
- "I maintain a current interval and extend it when I find an overlap"
- "The heap tracks end times of active meetings — when a new meeting starts, I check if the earliest-ending meeting has freed up"

**Common follow-up questions:**
- "What if intervals can have the same start time?" → sort by start, break ties by end
- "What if the input is a stream?" → can't sort; use a different data structure (interval tree, segment tree)
- "What if you need to find the actual assignment (which meeting goes in which room)?" → heap approach, track room IDs

---

### Pattern Recognition Guide

Interval problems often appear in disguise. Here are the signals to look for:

**"Merge" signals:**
- "Combine overlapping ranges"
- "Simplify a list of ranges"
- "Find the total coverage"
- "Remove redundant intervals"

**"Count overlaps" signals:**
- "Minimum rooms/workers/resources needed"
- "Maximum concurrent events"
- "Peak usage at any point"
- "Can all tasks be completed simultaneously?"

**"Greedy keep/remove" signals:**
- "Minimum number to remove to make non-overlapping"
- "Maximum number of non-overlapping intervals"
- "Minimum arrows/shots to cover all"
- Activity selection problem variants

**"Insert" signals:**
- "Add a new event to an existing schedule"
- "Merge a new range into a sorted list"
- Input is explicitly stated to be sorted and non-overlapping

**"Intersection" signals:**
- "Common availability between two people/systems"
- "Overlap between two sorted lists"
- "Shared time slots"

**"Disguised interval" signals:**
- Problems about characters in strings with first/last occurrence → character intervals
- Problems about ranges on a number line → interval merge/sweep
- Problems about booking/scheduling with start/end → meeting rooms variants

---

### Interval Tree — When Sorted Arrays Aren't Enough

The templates in this document assume static input (sort once, process once). For dynamic scenarios — where intervals are inserted and deleted, and you need to answer "which intervals contain point x?" — you need an **interval tree**.

An interval tree is a balanced BST augmented with the maximum end value in each subtree. It supports:
- Insert interval: O(log n)
- Delete interval: O(log n)
- Query "all intervals containing point x": O(log n + k) where k = results

Java doesn't have a built-in interval tree. In interviews, if asked about dynamic intervals, mention TreeMap-based approaches or that you'd use an interval tree for production.

**TreeMap approach for point stabbing queries:**
```java
// Find all intervals containing point q
// Requires intervals sorted by start; use TreeMap<start, end>
TreeMap<Integer, Integer> map = new TreeMap<>();

// Insert interval [s, e]
map.put(s, Math.max(map.getOrDefault(s, 0), e));

// Query: all intervals containing point q
// All intervals with start <= q AND end >= q
for (Map.Entry<Integer, Integer> entry : map.headMap(q + 1, true).entrySet()) {
    if (entry.getValue() >= q) {
        // This interval [entry.getKey(), entry.getValue()] contains q
    }
}
```

This is O(n) worst case for the query but works for many interview scenarios.

---

### Coordinate Compression

When interval coordinates are large (up to 10^9) but there are few distinct values (n intervals = 2n endpoints), coordinate compression reduces the problem to a bounded range.

```java
// Coordinate compression for sweep line
int[] coords = new int[2 * n];
for (int i = 0; i < n; i++) {
    coords[2 * i]     = intervals[i][0];
    coords[2 * i + 1] = intervals[i][1];
}
Arrays.sort(coords);
// Remove duplicates
int[] unique = Arrays.stream(coords).distinct().toArray();

// Map original coordinate to compressed index
Map<Integer, Integer> compress = new HashMap<>();
for (int i = 0; i < unique.length; i++) {
    compress.put(unique[i], i);
}

// Now use compressed indices in difference array
int[] diff = new int[unique.length + 1];
for (int[] interval : intervals) {
    diff[compress.get(interval[0])]++;
    diff[compress.get(interval[1]) + 1]--;
}
```

This converts an O(maxCoord) difference array into O(n) space. Useful when coordinates are large but sparse.

---

### Segment Tree for Range Queries (Advanced)

For problems requiring both range updates and range queries on intervals, a segment tree with lazy propagation is the right tool. This goes beyond typical FAANG interview scope but appears in competitive programming and some Google/Meta hard problems.

**When you'd need it:**
- "Update all intervals in range [l, r]" + "query max/sum in range [l, r]"
- "Count intervals covering each point" with dynamic updates
- Problems where difference array + prefix sum isn't enough because you need point queries after each update

The segment tree approach is O(log n) per update and query, vs O(n) for a naive scan after each difference array update.

---

*Document 18 of 20 — Next: Topic 19 (Tries)*
*Cross-references: Topic 2 (Two Pointers) for LC 986, Topic 5 (Prefix Sum/Difference Arrays) for LC 1094, Topic 12 (Heaps) for LC 253 and LC 1851*
