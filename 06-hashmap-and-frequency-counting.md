# Topic 6: HashMap & Frequency Counting

> Document 6 of 20 in the FAANG DSA Prep series.

**Connection to previous topics:** HashMap appeared as a supporting tool in Topic 1 (sliding window frequency maps for character counts) and Topic 5 (prefix sum + HashMap for subarray counting). This document focuses on problems where HashMap IS the primary technique, not a helper.

**Top companies asking these problems:** Google, Amazon, Meta, Microsoft, Bloomberg, Apple, Adobe, Goldman Sachs

---

## Overview

HashMap provides O(1) average-case lookup, insert, and delete. That single property unlocks a family of patterns that would otherwise require O(n log n) sorting or O(n²) brute force.

This document is not about how HashMap works internally (hash functions, load factors, collision resolution). It's about the five interview patterns built on top of it:

1. **Frequency Counting** — count occurrences of each element
2. **Two-Sum / Complement Lookup** — store what you've seen, check for what you need
3. **Grouping / Bucketing** — map a canonical key to a list of elements
4. **HashSet Existence Check** — O(1) "have I seen this before?"
5. **Index Mapping** — store first or last occurrence of a value

Master these five patterns and you can solve roughly 80% of HashMap interview problems on sight.

---

## Table of Contents

1. [Core Concept](#1-core-concept)
2. [ELI5 Explanations](#2-eli5-explanations)
3. [When to Use — Recognition Signals](#3-when-to-use--recognition-signals)
4. [Core Templates in Java](#4-core-templates-in-java)
5. [Real-World Applications](#5-real-world-applications)
6. [Problem Categories and Solutions](#6-problem-categories-and-solutions)
   - [Category A: Frequency Counting](#category-a-frequency-counting-6-problems)
   - [Category B: Index / Position Mapping](#category-b-index--position-mapping-4-problems)
   - [Category C: Grouping and Pattern Matching](#category-c-grouping-and-pattern-matching-3-problems)
   - [Category D: Advanced](#category-d-advanced-4-problems)
7. [Common Mistakes and Edge Cases](#7-common-mistakes-and-edge-cases)
8. [Pattern Comparison](#8-pattern-comparison)
9. [Quick Reference Cheat Sheet](#9-quick-reference-cheat-sheet)
10. [Practice Roadmap](#10-practice-roadmap)

---

## 1. Core Concept

### The Five Patterns

#### Pattern 1: Frequency Counting

Build a `Map<element, count>` by iterating the input once. Then query the map for counts.

```
Input: [3, 1, 4, 1, 5, 9, 2, 6, 5, 3]
Map:   {3:2, 1:2, 4:1, 5:2, 9:1, 2:1, 6:1}
```

Use this when the problem asks about counts, frequencies, duplicates, majority elements, or "top K" elements.

#### Pattern 2: Two-Sum / Complement Lookup

For each element `x`, check if `target - x` already exists in the map. If yes, you found your pair. If no, store `x` in the map for future elements to find.

```
target = 9, array = [2, 7, 11, 15]
Process 2: need 7, not in map. Store {2: index 0}
Process 7: need 2, found at index 0. Return [0, 1].
```

The key insight: instead of checking every pair (O(n²)), you flip the question. "Does the complement of what I need already exist?" One pass, O(n).

#### Pattern 3: Grouping / Bucketing

Map a canonical key to a list of elements that share that key. The canonical key is some normalized form of the element.

```
Group anagrams: "eat", "tea", "tan", "ate", "nat", "bat"
Canonical key = sorted characters:
  "aet" -> ["eat", "tea", "ate"]
  "ant" -> ["tan", "nat"]
  "abt" -> ["bat"]
```

Use `computeIfAbsent` to avoid null checks when building the lists.

#### Pattern 4: HashSet Existence Check

A HashSet is a HashMap where you only care about keys, not values. O(1) membership test.

```
Set<Integer> seen = new HashSet<>();
for (int x : array) {
    if (seen.contains(x)) // duplicate found
    seen.add(x);
}
```

Use this when you need "have I seen this?" without caring about counts or positions.

#### Pattern 5: Index Mapping

Store the first (or last) index where a value appeared. Useful for "longest subarray" problems and duplicate detection within a window.

```
Map<Integer, Integer> firstSeen = new HashMap<>();
firstSeen.put(value, index);
// Later: int prevIndex = firstSeen.get(value);
```

---

### Java Implementation Choices

#### HashMap vs HashSet vs TreeMap vs LinkedHashMap

| Class | Use When | Ordering | Null Keys |
|---|---|---|---|
| `HashMap<K,V>` | Need key-value pairs, O(1) ops | None | 1 null key allowed |
| `HashSet<E>` | Only need membership test | None | 1 null allowed |
| `TreeMap<K,V>` | Need sorted keys, O(log n) ops | Sorted by key | No null keys |
| `LinkedHashMap<K,V>` | Need insertion-order iteration | Insertion order | 1 null key allowed |

**Interview rule of thumb:**
- Default to `HashMap` unless you need ordering.
- Use `TreeMap` when the problem requires "find the smallest/largest key" or range queries.
- Use `LinkedHashMap` for LRU Cache (maintains access order with `accessOrder=true`).
- Use `HashSet` when you only need existence, not counts.

#### Modern Java API You Must Know

```java
// getOrDefault — avoid null checks
int count = map.getOrDefault(key, 0);

// merge — increment counter cleanly
map.merge(key, 1, Integer::sum);
// Equivalent to: map.put(key, map.getOrDefault(key, 0) + 1)

// computeIfAbsent — create list on first encounter
map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
// Equivalent to:
// if (!map.containsKey(key)) map.put(key, new ArrayList<>());
// map.get(key).add(value);

// entrySet iteration — when you need both key and value
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    String key = entry.getKey();
    int val = entry.getValue();
}

// putIfAbsent — only insert if key doesn't exist
map.putIfAbsent(key, defaultValue);

// getOrDefault with compute
map.compute(key, (k, v) -> v == null ? 1 : v + 1);
```

**Prefer `merge()` over `getOrDefault + put`** for frequency counting. It's cleaner and avoids the double-lookup.

---

## 2. ELI5 Explanations

### Frequency Counting: The Candy Jar Problem

Imagine you have a pile of mixed candy — red, blue, green, yellow. You want to know how many of each color you have.

You grab a jar for each color and start sorting. Every time you pick up a red candy, you drop it in the red jar. Blue goes in blue. At the end, you count each jar.

That's frequency counting. The jars are your HashMap. The candy colors are your keys. The count in each jar is your value.

The HashMap lets you find any jar instantly, no matter how many jars you have. You don't search through them — you just say "red jar" and your hand goes straight to it.

### Two-Sum: The Dance Partner Problem

A hundred people walk into a dance. Each person has a number on their shirt. You want to find two people whose numbers add up to 100.

Naive approach: every person walks up to every other person and checks. That's 100 × 99 / 2 = 4,950 conversations. Slow.

Smart approach: each person walks in, writes "I need someone with number X" on a sticky note and posts it on the board. Before posting, they check the board for their own number.

Person with 30 walks in. Checks board for 70. Not there. Posts "need 70."
Person with 70 walks in. Checks board for 30. Found it! Done.

The board is your HashMap. You store what you've seen. Each new person checks if their complement is already there.

### Grouping: The Mailroom Problem

You work in a mailroom. Hundreds of packages arrive, each with an address. You need to sort them into bins so the delivery driver can grab all packages for one address at once.

You don't sort the packages by address alphabetically (that would be O(n log n)). You just read each address and drop the package in the right bin. If the bin doesn't exist yet, you create it.

That's grouping with `computeIfAbsent`. The address is your key. The bin is your list. One pass through all packages, O(n).

---

## 3. When to Use — Recognition Signals

### Green Flags (HashMap is likely the right tool)

- "Find a pair with sum equal to target" → Two-Sum pattern
- "Count the frequency of each element" → Frequency counting
- "Group anagrams together" → Grouping with canonical key
- "Find the first non-repeating character" → Frequency + index mapping
- "Check if two strings are anagrams" → Frequency comparison
- "Find duplicates in an array" → HashSet existence check
- "Longest consecutive sequence" → HashSet + sequence start detection
- "Top K frequent elements" → Frequency counting + heap or bucket sort
- "Isomorphic strings" or "word pattern" → Bidirectional mapping
- "Design a data structure with O(1) insert/delete/getRandom" → HashMap + array
- "LRU Cache" → LinkedHashMap or HashMap + doubly linked list
- Any problem where O(1) lookup would reduce O(n²) to O(n)

### Red Flags (HashMap is probably not the right tool)

- Input is already sorted → binary search or two pointers are likely better
- Need to find elements in a range → TreeMap or segment tree
- Space is severely constrained → in-place approaches (cyclic sort, bit manipulation)
- Need to maintain sorted order dynamically → TreeMap or TreeSet
- Problem involves intervals → sort + sweep line
- Characters are bounded (only lowercase letters) → `int[26]` is faster and simpler than HashMap

### The Space-Time Tradeoff

HashMap solutions almost always trade O(n) extra space for O(n) time instead of O(n²) time. In interviews, this is almost always the right trade. If an interviewer asks "can you do it without extra space?", that's a signal to think about in-place approaches (sorting, two pointers, cyclic sort).

---

## 4. Core Templates in Java

### Template 1: Frequency Counter

```java
/**
 * Frequency Counter Template
 * Use: count occurrences of each element
 * Time: O(n), Space: O(n) or O(k) where k = distinct elements
 */
public Map<Integer, Integer> buildFrequencyMap(int[] nums) {
    Map<Integer, Integer> freq = new HashMap<>();
    
    for (int num : nums) {
        // Option A: merge() — cleanest, modern Java
        freq.merge(num, 1, Integer::sum);
        
        // Option B: getOrDefault — explicit, readable
        // freq.put(num, freq.getOrDefault(num, 0) + 1);
        
        // Option C: compute — flexible for complex logic
        // freq.compute(num, (k, v) -> v == null ? 1 : v + 1);
    }
    
    return freq;
}
```

**Execution trace for `[3, 1, 4, 1, 5, 9, 2, 6, 5, 3]`:**

```
Process 3: map = {3:1}
Process 1: map = {3:1, 1:1}
Process 4: map = {3:1, 1:1, 4:1}
Process 1: map = {3:1, 1:2, 4:1}
Process 5: map = {3:1, 1:2, 4:1, 5:1}
Process 9: map = {3:1, 1:2, 4:1, 5:1, 9:1}
Process 2: map = {3:1, 1:2, 4:1, 5:1, 9:1, 2:1}
Process 6: map = {3:1, 1:2, 4:1, 5:1, 9:1, 2:1, 6:1}
Process 5: map = {3:1, 1:2, 4:1, 5:2, 9:1, 2:1, 6:1}
Process 3: map = {3:2, 1:2, 4:1, 5:2, 9:1, 2:1, 6:1}
```

**When to prefer `int[]` over HashMap:** If elements are bounded characters (lowercase letters: `int[26]`, ASCII: `int[128]`), use an array. It's faster (no hashing overhead), uses less memory, and avoids boxing/unboxing. See Template 5.

---

### Template 2: Two-Sum Complement Lookup

```java
/**
 * Two-Sum Complement Lookup Template
 * Use: find pair/triplet with target sum, check if complement exists
 * Time: O(n), Space: O(n)
 */
public int[] twoSum(int[] nums, int target) {
    // Map: value -> index (for returning indices)
    // Use Map<Integer, Integer> when you need the index
    // Use Set<Integer> when you only need existence
    Map<Integer, Integer> seen = new HashMap<>();
    
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        
        if (seen.containsKey(complement)) {
            return new int[]{seen.get(complement), i};
        }
        
        // Store AFTER checking to avoid using same element twice
        seen.put(nums[i], i);
    }
    
    return new int[]{-1, -1}; // no solution found
}
```

**Critical detail:** Store the current element AFTER checking for its complement. This prevents using the same element twice (e.g., target=6, element=3: you don't want to match 3 with itself).

**Variant — existence only (no index needed):**

```java
public boolean hasPairWithSum(int[] nums, int target) {
    Set<Integer> seen = new HashSet<>();
    for (int num : nums) {
        if (seen.contains(target - num)) return true;
        seen.add(num);
    }
    return false;
}
```

---

### Template 3: Grouping / Bucketing

```java
/**
 * Grouping / Bucketing Template
 * Use: group elements by some canonical key
 * Time: O(n * cost_of_key_computation), Space: O(n)
 */
public Map<String, List<String>> groupByKey(String[] items) {
    Map<String, List<String>> groups = new HashMap<>();
    
    for (String item : items) {
        String key = computeCanonicalKey(item); // e.g., sort chars, freq array
        
        // computeIfAbsent: create list only if key not present, then add
        groups.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
    }
    
    return groups;
}

// Example canonical key: sorted characters (for anagram grouping)
private String sortedKey(String s) {
    char[] chars = s.toCharArray();
    Arrays.sort(chars);
    return new String(chars);
}

// Example canonical key: frequency array as string (faster for long strings)
private String freqKey(String s) {
    int[] freq = new int[26];
    for (char c : s.toCharArray()) freq[c - 'a']++;
    return Arrays.toString(freq); // "[1,0,0,1,1,0,...]"
}
```

**Why `computeIfAbsent` over manual null check:**

```java
// Verbose, two map lookups
if (!groups.containsKey(key)) {
    groups.put(key, new ArrayList<>());
}
groups.get(key).add(item);

// Clean, one map lookup
groups.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
```

---

### Template 4: HashSet Existence Check

```java
/**
 * HashSet Existence Check Template
 * Use: O(1) membership test, duplicate detection, "have I seen this?"
 * Time: O(n), Space: O(n)
 */

// Pattern A: Detect any duplicate
public boolean containsDuplicate(int[] nums) {
    Set<Integer> seen = new HashSet<>();
    for (int num : nums) {
        if (!seen.add(num)) return true; // add() returns false if already present
    }
    return false;
}

// Pattern B: Check if element exists in a reference set
public boolean isSubset(int[] subset, int[] superset) {
    Set<Integer> superSet = new HashSet<>();
    for (int x : superset) superSet.add(x);
    
    for (int x : subset) {
        if (!superSet.contains(x)) return false;
    }
    return true;
}

// Pattern C: Find elements NOT in a set (complement)
public List<Integer> findMissing(int[] nums, int n) {
    Set<Integer> present = new HashSet<>();
    for (int num : nums) present.add(num);
    
    List<Integer> missing = new ArrayList<>();
    for (int i = 1; i <= n; i++) {
        if (!present.contains(i)) missing.add(i);
    }
    return missing;
}
```

**Note on `add()` return value:** `Set.add(element)` returns `false` if the element was already present. This lets you detect duplicates in one line without a separate `contains` check.

---

### Template 5: Character Frequency Array (Bounded Alphabet Optimization)

```java
/**
 * Character Frequency Array Template
 * Use: when input is bounded characters (lowercase letters, ASCII)
 * Faster than HashMap: no hashing, no boxing, cache-friendly
 * Time: O(n), Space: O(1) — fixed-size array, not input-dependent
 */

// For lowercase letters only (a-z)
public int[] charFrequency26(String s) {
    int[] freq = new int[26];
    for (char c : s.toCharArray()) {
        freq[c - 'a']++;
    }
    return freq;
}

// For full ASCII (0-127)
public int[] charFrequency128(String s) {
    int[] freq = new int[128];
    for (char c : s.toCharArray()) {
        freq[c]++;
    }
    return freq;
}

// Compare two frequency arrays (for anagram check)
public boolean sameFrequency(String s, String t) {
    if (s.length() != t.length()) return false;
    int[] freq = new int[26];
    for (char c : s.toCharArray()) freq[c - 'a']++;
    for (char c : t.toCharArray()) freq[c - 'a']--;
    for (int count : freq) {
        if (count != 0) return false;
    }
    return true;
}

// Sliding window with frequency array (for substring problems)
public boolean containsAnagram(String s, String p) {
    if (p.length() > s.length()) return false;
    int[] need = new int[26];
    int[] window = new int[26];
    
    for (char c : p.toCharArray()) need[c - 'a']++;
    
    for (int i = 0; i < s.length(); i++) {
        window[s.charAt(i) - 'a']++;
        if (i >= p.length()) {
            window[s.charAt(i - p.length()) - 'a']--;
        }
        if (Arrays.equals(window, need)) return true;
    }
    return false;
}
```

**When to use `int[26]` vs `HashMap<Character, Integer>`:**

| Criterion | `int[26]` | `HashMap` |
|---|---|---|
| Input constraint | Lowercase letters only | Any characters / Unicode |
| Speed | Faster (array access) | Slower (hashing + boxing) |
| Space | O(1) fixed | O(k) distinct chars |
| Code clarity | Slightly more verbose | More readable |
| Interview signal | Shows optimization awareness | Default choice |

If the problem says "lowercase English letters," use `int[26]`. Mention it explicitly in the interview — it shows you think about constants.

---

## 5. Real-World Applications

### 1. Database Hash Indexes and Redis

Every relational database uses hash indexes for equality lookups (`WHERE id = 42`). The index is a HashMap from column value to row location. Redis is essentially a distributed HashMap — every `GET key` and `SET key value` is a HashMap operation. The O(1) average case is why Redis can handle millions of operations per second.

### 2. Web Caching (CDN, Browser Cache, API Response Cache)

A CDN maps URLs to cached responses. When a request arrives, the CDN checks its HashMap: if the URL is a key, return the cached value. If not, fetch from origin and store. Browser caches work identically. API gateways cache responses by request hash. The HashMap lookup is what makes cache hits O(1) regardless of cache size.

### 3. Compiler Symbol Tables

When a compiler processes source code, it maintains a symbol table — a HashMap from variable/function names to their types, memory locations, and scope information. Every time you write `int x = 5;`, the compiler inserts `x` into the symbol table. Every time you reference `x`, it looks it up. Without O(1) lookup, compilation would be dramatically slower.

### 4. Spell Checkers

A spell checker loads a dictionary into a HashSet at startup. Checking if a word is spelled correctly is a single `contains()` call. The entire dictionary (hundreds of thousands of words) is checked in O(1). This is why spell checking feels instantaneous even in large documents.

### 5. Network Packet Deduplication

Network routers and firewalls use HashSets to track recently seen packet IDs. When a packet arrives, the router checks if its ID is in the set. If yes, it's a duplicate (retransmission) and gets dropped. If no, it's processed and the ID is added. This prevents duplicate processing without scanning all previous packets.

### 6. Consistent Hashing for Load Balancing

Distributed systems (Cassandra, DynamoDB, Memcached) use consistent hashing to distribute data across nodes. Each server is mapped to a position on a hash ring. Each key is hashed to a position, and the request goes to the nearest server clockwise. When a server is added or removed, only a fraction of keys need to be remapped. This is why adding a node to a cluster doesn't cause a full data reshuffle.

---

## 6. Problem Categories and Solutions

---

### Category A: Frequency Counting (6 Problems)

---

#### Problem 1: Two Sum (LC 1)

**Difficulty:** Easy  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe, Goldman Sachs, Uber (200+ companies — the most asked LeetCode problem)

**Problem:** Given an integer array `nums` and an integer `target`, return indices of the two numbers that add up to `target`. Exactly one solution exists. Cannot use the same element twice.

**Hint:** For each element, check if its complement (`target - element`) has already been seen. Store elements as you go.

**Solution:**

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        // Map: value -> index
        Map<Integer, Integer> seen = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            
            // Check if complement was seen before
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i};
            }
            
            // Store current element AFTER checking
            // (prevents using same element twice)
            seen.put(nums[i], i);
        }
        
        // Problem guarantees exactly one solution, so this is unreachable
        throw new IllegalArgumentException("No solution found");
    }
}
```

**Dry Run — `nums = [2, 7, 11, 15]`, `target = 9`:**

```
i=0: num=2, complement=9-2=7
     seen = {} → 7 not found
     seen = {2:0}

i=1: num=7, complement=9-7=2
     seen = {2:0} → 2 found at index 0!
     return [0, 1]
```

**Dry Run — `nums = [3, 2, 4]`, `target = 6`:**

```
i=0: num=3, complement=6-3=3
     seen = {} → 3 not found
     seen = {3:0}

i=1: num=2, complement=6-2=4
     seen = {3:0} → 4 not found
     seen = {3:0, 2:1}

i=2: num=4, complement=6-4=2
     seen = {3:0, 2:1} → 2 found at index 1!
     return [1, 2]
```

**Dry Run — `nums = [3, 3]`, `target = 6`:**

```
i=0: num=3, complement=6-3=3
     seen = {} → 3 not found
     seen = {3:0}

i=1: num=3, complement=6-3=3
     seen = {3:0} → 3 found at index 0!
     return [0, 1]
```

This last case is why you store AFTER checking. If you stored first, index 0 would overwrite itself and you'd return [1, 1] (same element twice).

**Time:** O(n) — one pass  
**Space:** O(n) — map stores up to n elements

**Key insight:** Flip the question. Instead of "do any two elements sum to target?", ask "does the complement of this element exist in what I've already seen?" One pass instead of two nested loops.

---

#### Problem 2: Group Anagrams (LC 49)

**Difficulty:** Medium  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe, Goldman Sachs, Uber

**Problem:** Given an array of strings, group the anagrams together. Return the groups in any order.

**Hint:** Two strings are anagrams if and only if they have the same character frequencies. Use that as the grouping key.

**Solution — Approach 1: Sorted String as Key**

```java
class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();
        
        for (String s : strs) {
            // Canonical key: sort the characters
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            
            // Add to the group for this key
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        
        return new ArrayList<>(groups.values());
    }
}
```

**Time:** O(n * k log k) where n = number of strings, k = max string length  
**Space:** O(n * k)

**Solution — Approach 2: Frequency Array as Key (Better for long strings)**

```java
class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();
        
        for (String s : strs) {
            // Canonical key: frequency array serialized as string
            int[] freq = new int[26];
            for (char c : s.toCharArray()) {
                freq[c - 'a']++;
            }
            // Arrays.toString gives "[1,0,0,1,1,0,...]" — unique per frequency profile
            String key = Arrays.toString(freq);
            
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        
        return new ArrayList<>(groups.values());
    }
}
```

**Time:** O(n * k) — no sorting, just counting  
**Space:** O(n * k)

**When to prefer Approach 2:** When strings are long (k is large), O(k) counting beats O(k log k) sorting. For short strings (k ≤ 10), both are fine.

**Trace for `["eat","tea","tan","ate","nat","bat"]`:**

```
"eat" → sorted: "aet" → groups: {"aet": ["eat"]}
"tea" → sorted: "aet" → groups: {"aet": ["eat","tea"]}
"tan" → sorted: "ant" → groups: {"aet": ["eat","tea"], "ant": ["tan"]}
"ate" → sorted: "aet" → groups: {"aet": ["eat","tea","ate"], "ant": ["tan"]}
"nat" → sorted: "ant" → groups: {"aet": ["eat","tea","ate"], "ant": ["tan","nat"]}
"bat" → sorted: "abt" → groups: {"aet": [...], "ant": [...], "abt": ["bat"]}

Result: [["eat","tea","ate"], ["tan","nat"], ["bat"]]
```

**Key insight:** The canonical key is the invariant that all anagrams share. Sorted characters work. Frequency array works. Both are valid — mention both in the interview and explain the tradeoff.

---

#### Problem 3: Top K Frequent Elements (LC 347)

**Difficulty:** Medium  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Adobe

**Problem:** Given an integer array `nums` and an integer `k`, return the `k` most frequent elements. The answer is guaranteed to be unique.

**Hint:** Count frequencies first. Then find the top k by frequency. Two approaches: min-heap of size k (O(n log k)), or bucket sort by frequency (O(n)).

**Solution — Approach 1: Min-Heap (O(n log k))**

```java
class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        // Step 1: Build frequency map
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) {
            freq.merge(num, 1, Integer::sum);
        }
        
        // Step 2: Min-heap of size k, ordered by frequency
        // Min-heap: smallest frequency at top, so we can evict it
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(
            (a, b) -> freq.get(a) - freq.get(b)
        );
        
        for (int num : freq.keySet()) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll(); // remove least frequent
            }
        }
        
        // Step 3: Extract results
        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            result[i] = minHeap.poll();
        }
        return result;
    }
}
```

**Time:** O(n log k) — building freq map O(n), heap operations O(n log k)  
**Space:** O(n + k)

**Solution — Approach 2: Bucket Sort (O(n)) — Optimal**

```java
class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        // Step 1: Build frequency map
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) {
            freq.merge(num, 1, Integer::sum);
        }
        
        // Step 2: Bucket sort — bucket[i] = list of numbers with frequency i
        // Max possible frequency is nums.length
        List<Integer>[] buckets = new List[nums.length + 1];
        for (int num : freq.keySet()) {
            int f = freq.get(num);
            if (buckets[f] == null) buckets[f] = new ArrayList<>();
            buckets[f].add(num);
        }
        
        // Step 3: Collect top k from highest frequency buckets
        int[] result = new int[k];
        int idx = 0;
        for (int f = buckets.length - 1; f >= 0 && idx < k; f--) {
            if (buckets[f] != null) {
                for (int num : buckets[f]) {
                    result[idx++] = num;
                    if (idx == k) break;
                }
            }
        }
        return result;
    }
}
```

**Time:** O(n) — all steps are linear  
**Space:** O(n)

**When to use which:**
- Heap approach: when k is much smaller than n, or when you need a general "top k" solution
- Bucket sort: when you need optimal O(n) and the frequency range is bounded by n

**Key insight for bucket sort:** The maximum frequency any element can have is `n` (if all elements are the same). So you can create `n+1` buckets indexed by frequency. Then scan from the highest bucket down, collecting elements until you have k.

---

#### Problem 4: Valid Anagram (LC 242)

**Difficulty:** Easy  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given two strings `s` and `t`, return `true` if `t` is an anagram of `s`, and `false` otherwise.

**Hint:** Two strings are anagrams if they have identical character frequencies. Count one, decrement with the other, check all zeros.

**Solution:**

```java
class Solution {
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        
        // Use int[26] since problem specifies lowercase English letters
        int[] freq = new int[26];
        
        // Increment for s, decrement for t
        for (int i = 0; i < s.length(); i++) {
            freq[s.charAt(i) - 'a']++;
            freq[t.charAt(i) - 'a']--;
        }
        
        // All counts must be zero for anagram
        for (int count : freq) {
            if (count != 0) return false;
        }
        return true;
    }
}
```

**Time:** O(n)  
**Space:** O(1) — fixed-size array

**Follow-up: What if the input contains Unicode characters?**

```java
public boolean isAnagramUnicode(String s, String t) {
    if (s.length() != t.length()) return false;
    
    Map<Character, Integer> freq = new HashMap<>();
    for (char c : s.toCharArray()) freq.merge(c, 1, Integer::sum);
    for (char c : t.toCharArray()) {
        freq.merge(c, -1, Integer::sum);
        if (freq.get(c) < 0) return false; // t has more of this char than s
    }
    return true;
}
```

**Key insight:** The `int[26]` trick is O(1) space because the array size doesn't depend on input size. Always mention this optimization when the problem specifies lowercase letters.

---

#### Problem 5: Ransom Note (LC 383)

**Difficulty:** Easy  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given two strings `ransomNote` and `magazine`, return `true` if `ransomNote` can be constructed using the letters from `magazine`. Each letter in `magazine` can only be used once.

**Hint:** Count available letters in magazine. Check if ransomNote's requirements can be met.

**Solution:**

```java
class Solution {
    public boolean canConstruct(String ransomNote, String magazine) {
        // Count available letters in magazine
        int[] available = new int[26];
        for (char c : magazine.toCharArray()) {
            available[c - 'a']++;
        }
        
        // Check if ransomNote can be built
        for (char c : ransomNote.toCharArray()) {
            available[c - 'a']--;
            if (available[c - 'a'] < 0) {
                return false; // not enough of this letter
            }
        }
        return true;
    }
}
```

**Time:** O(m + r) where m = magazine length, r = ransomNote length  
**Space:** O(1) — fixed-size array

**Key insight:** Early termination — return false as soon as any letter goes negative. No need to process the rest of ransomNote.

---

#### Problem 6: Majority Element (LC 169)

**Difficulty:** Easy  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given an array `nums` of size `n`, return the majority element. The majority element appears more than `n/2` times. It always exists.

**Hint:** Count frequencies. The element with count > n/2 is the answer. But there's an O(1) space solution: Boyer-Moore Voting.

**Solution — Approach 1: HashMap (O(n) space)**

```java
class Solution {
    public int majorityElement(int[] nums) {
        Map<Integer, Integer> freq = new HashMap<>();
        int threshold = nums.length / 2;
        
        for (int num : nums) {
            int count = freq.merge(num, 1, Integer::sum);
            if (count > threshold) return num;
        }
        
        throw new IllegalStateException("No majority element"); // unreachable
    }
}
```

**Solution — Approach 2: Boyer-Moore Voting (O(1) space) — Preferred**

```java
class Solution {
    public int majorityElement(int[] nums) {
        int candidate = nums[0];
        int count = 1;
        
        for (int i = 1; i < nums.length; i++) {
            if (count == 0) {
                candidate = nums[i];
                count = 1;
            } else if (nums[i] == candidate) {
                count++;
            } else {
                count--;
            }
        }
        
        // candidate is guaranteed to be majority element
        // (problem states it always exists)
        return candidate;
    }
}
```

**Boyer-Moore trace for `[2, 2, 1, 1, 1, 2, 2]`:**

```
candidate=2, count=1
i=1: 2==candidate → count=2
i=2: 1!=candidate → count=1
i=3: 1!=candidate → count=0
i=4: count==0 → candidate=1, count=1
i=5: 2!=candidate → count=0
i=6: count==0 → candidate=2, count=1

Result: 2 ✓ (appears 4 times out of 7)
```

**Why Boyer-Moore works:** The majority element appears more than n/2 times. Every time it "cancels" with a non-majority element, the majority element still has more remaining occurrences. The last surviving candidate must be the majority element.

**Time:** O(n), **Space:** O(1)

**Interview note:** Always mention both approaches. Start with HashMap (obvious), then offer Boyer-Moore as the O(1) space optimization. This shows depth.

---

### Category B: Index / Position Mapping (4 Problems)

---

#### Problem 7: Contains Duplicate (LC 217)

**Difficulty:** Easy  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given an integer array `nums`, return `true` if any value appears at least twice.

**Solution:**

```java
class Solution {
    public boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int num : nums) {
            if (!seen.add(num)) return true; // add() returns false if already present
        }
        return false;
    }
}
```

**Time:** O(n), **Space:** O(n)

**Key insight:** `Set.add()` returns `false` if the element was already in the set. This is cleaner than `contains()` + `add()` (two operations vs one).

**Alternative — sorting (O(1) space, O(n log n) time):**

```java
public boolean containsDuplicateSort(int[] nums) {
    Arrays.sort(nums);
    for (int i = 1; i < nums.length; i++) {
        if (nums[i] == nums[i-1]) return true;
    }
    return false;
}
```

Use HashSet when space is not a concern. Use sorting when the interviewer asks for O(1) extra space.

---

#### Problem 8: Contains Duplicate II (LC 219)

**Difficulty:** Easy  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given an integer array `nums` and an integer `k`, return `true` if there are two distinct indices `i` and `j` such that `nums[i] == nums[j]` and `|i - j| <= k`.

**Hint:** Store the most recent index of each value. When you see a duplicate, check if the distance is within k.

**Solution:**

```java
class Solution {
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        // Map: value -> most recent index
        Map<Integer, Integer> lastSeen = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            if (lastSeen.containsKey(nums[i])) {
                int prevIndex = lastSeen.get(nums[i]);
                if (i - prevIndex <= k) return true;
            }
            // Update to most recent index
            lastSeen.put(nums[i], i);
        }
        return false;
    }
}
```

**Time:** O(n), **Space:** O(min(n, k)) — at most k+1 entries in the map at any time

**Alternative — sliding window with HashSet:**

```java
public boolean containsNearbyDuplicateSet(int[] nums, int k) {
    Set<Integer> window = new HashSet<>();
    for (int i = 0; i < nums.length; i++) {
        if (window.contains(nums[i])) return true;
        window.add(nums[i]);
        if (window.size() > k) {
            window.remove(nums[i - k]); // shrink window
        }
    }
    return false;
}
```

The sliding window approach maintains a window of exactly k elements. Cleaner space bound: O(k).

---

#### Problem 9: First Unique Character in a String (LC 387)

**Difficulty:** Easy  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs

**Problem:** Given a string `s`, find the first non-repeating character and return its index. Return -1 if none exists.

**Hint:** Two passes: first count frequencies, then find the first character with count 1.

**Solution:**

```java
class Solution {
    public int firstUniqChar(String s) {
        // Pass 1: count frequencies
        int[] freq = new int[26];
        for (char c : s.toCharArray()) {
            freq[c - 'a']++;
        }
        
        // Pass 2: find first character with frequency 1
        for (int i = 0; i < s.length(); i++) {
            if (freq[s.charAt(i) - 'a'] == 1) return i;
        }
        
        return -1;
    }
}
```

**Time:** O(n), **Space:** O(1)

**Why two passes?** You can't determine if a character is unique until you've seen the entire string. The first pass builds the complete frequency picture. The second pass finds the first character that appears exactly once.

**Follow-up: What if the string is a stream (can't do two passes)?**

Use a `LinkedHashMap` to maintain insertion order:

```java
public int firstUniqCharStream(String s) {
    Map<Character, Integer> freq = new LinkedHashMap<>();
    for (char c : s.toCharArray()) {
        freq.merge(c, 1, Integer::sum);
    }
    // LinkedHashMap iterates in insertion order
    for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
        if (entry.getValue() == 1) {
            return s.indexOf(entry.getKey());
        }
    }
    return -1;
}
```

---

#### Problem 10: Isomorphic Strings (LC 205)

**Difficulty:** Easy  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given two strings `s` and `t`, determine if they are isomorphic. Two strings are isomorphic if the characters in `s` can be replaced to get `t`. No two characters may map to the same character, but a character may map to itself.

**Hint:** Need bidirectional mapping. `s[i]` must always map to the same `t[i]`, AND `t[i]` must always map to the same `s[i]`.

**Solution:**

```java
class Solution {
    public boolean isIsomorphic(String s, String t) {
        // Two maps: s->t mapping and t->s mapping
        Map<Character, Character> sToT = new HashMap<>();
        Map<Character, Character> tToS = new HashMap<>();
        
        for (int i = 0; i < s.length(); i++) {
            char sc = s.charAt(i);
            char tc = t.charAt(i);
            
            // Check s->t mapping
            if (sToT.containsKey(sc)) {
                if (sToT.get(sc) != tc) return false; // inconsistent mapping
            } else {
                sToT.put(sc, tc);
            }
            
            // Check t->s mapping (prevents two s-chars mapping to same t-char)
            if (tToS.containsKey(tc)) {
                if (tToS.get(tc) != sc) return false;
            } else {
                tToS.put(tc, sc);
            }
        }
        return true;
    }
}
```

**Why bidirectional?** Consider `s = "ab"`, `t = "aa"`. With only s→t mapping: `a→a`, `b→a`. This would pass a one-directional check but is not isomorphic (two different chars map to the same char). The t→s map catches this: `a` would need to map to both `a` and `b`.

**Time:** O(n), **Space:** O(1) — at most 256 distinct ASCII characters

**Trace for `s = "egg"`, `t = "add"`:**

```
i=0: sc='e', tc='a'
     sToT: {} → add e→a. sToT={e:a}
     tToS: {} → add a→e. tToS={a:e}

i=1: sc='g', tc='d'
     sToT: {e:a} → g not present, add g→d. sToT={e:a, g:d}
     tToS: {a:e} → d not present, add d→g. tToS={a:e, d:g}

i=2: sc='g', tc='d'
     sToT: g→d, tc=d ✓
     tToS: d→g, sc=g ✓

Result: true ✓
```

---

### Category C: Grouping and Pattern Matching (3 Problems)

---

#### Problem 11: Word Pattern (LC 290)

**Difficulty:** Easy  
**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given a pattern string and a string `s`, find if `s` follows the same pattern. Each letter in pattern maps to exactly one word in `s`, and each word maps to exactly one letter.

**Hint:** Same as isomorphic strings but between characters and words. Bidirectional mapping required.

**Solution:**

```java
class Solution {
    public boolean wordPattern(String pattern, String s) {
        String[] words = s.split(" ");
        
        if (pattern.length() != words.length) return false;
        
        Map<Character, String> charToWord = new HashMap<>();
        Map<String, Character> wordToChar = new HashMap<>();
        
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            String word = words[i];
            
            // Check char->word mapping
            if (charToWord.containsKey(c)) {
                if (!charToWord.get(c).equals(word)) return false;
            } else {
                charToWord.put(c, word);
            }
            
            // Check word->char mapping
            if (wordToChar.containsKey(word)) {
                if (wordToChar.get(word) != c) return false;
            } else {
                wordToChar.put(word, c);
            }
        }
        return true;
    }
}
```

**Time:** O(n) where n = length of pattern  
**Space:** O(n)

**Edge case:** `pattern = "ab"`, `s = "dog dog"`. Without the word→char map, `a→dog` and `b→dog` would both pass the char→word check. The word→char map catches that `dog` can't map to both `a` and `b`.

---

#### Problem 12: Longest Consecutive Sequence (LC 128)

**Difficulty:** Medium  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, Uber

**Problem:** Given an unsorted array of integers `nums`, return the length of the longest consecutive elements sequence. Must run in O(n).

**Hint:** Put all numbers in a HashSet. For each number, only start counting if it's the beginning of a sequence (i.e., `num - 1` is NOT in the set). Then count forward.

**Solution:**

```java
class Solution {
    public int longestConsecutive(int[] nums) {
        // Step 1: Put all numbers in a HashSet for O(1) lookup
        Set<Integer> numSet = new HashSet<>();
        for (int num : nums) numSet.add(num);
        
        int maxLength = 0;
        
        for (int num : numSet) {
            // Only start counting from the beginning of a sequence
            // A number is a sequence start if num-1 is NOT in the set
            if (!numSet.contains(num - 1)) {
                int currentNum = num;
                int currentLength = 1;
                
                // Count forward
                while (numSet.contains(currentNum + 1)) {
                    currentNum++;
                    currentLength++;
                }
                
                maxLength = Math.max(maxLength, currentLength);
            }
        }
        
        return maxLength;
    }
}
```

**Dry Run — `nums = [100, 4, 200, 1, 3, 2]`:**

```
Step 1: numSet = {100, 4, 200, 1, 3, 2}

Iterate over numSet (order may vary, but result is same):

num=100: 99 not in set → start of sequence
         100 → 101? No. Length=1. maxLength=1

num=4:   3 IS in set → not a start, skip

num=200: 199 not in set → start of sequence
         200 → 201? No. Length=1. maxLength=1

num=1:   0 not in set → start of sequence
         1 → 2? Yes. currentNum=2, length=2
         2 → 3? Yes. currentNum=3, length=3
         3 → 4? Yes. currentNum=4, length=4
         4 → 5? No. Length=4. maxLength=4

num=3:   2 IS in set → not a start, skip

num=2:   1 IS in set → not a start, skip

Result: 4 (sequence: 1, 2, 3, 4)
```

**Why is this O(n) and not O(n²)?**

The key is the "sequence start" check: `if (!numSet.contains(num - 1))`. This ensures the inner `while` loop only runs for sequence starts. Each number is visited at most twice: once in the outer loop (skipped if not a start), and once in the inner while loop (as part of a sequence). Total work across all iterations: O(n).

Without this optimization, you'd start counting from every number, and the inner loop could run O(n) times for each element, giving O(n²).

**Time:** O(n) — each element processed at most twice  
**Space:** O(n) — HashSet

---

#### Problem 13: LRU Cache (LC 146)

**Difficulty:** Medium  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, Uber

**Problem:** Design a data structure that follows the Least Recently Used (LRU) cache eviction policy. Implement `LRUCache(int capacity)`, `int get(int key)`, and `void put(int key, int value)`. Both operations must run in O(1) average time.

**Hint:** HashMap for O(1) lookup. Doubly linked list for O(1) insertion and deletion (to track recency order). The HashMap stores key → node, so you can find any node in O(1) and then remove/move it in O(1).

**Solution — Complete Production-Quality Implementation:**

```java
class LRUCache {
    
    // Doubly linked list node
    private static class Node {
        int key;
        int value;
        Node prev;
        Node next;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    
    private final int capacity;
    private final Map<Integer, Node> cache; // key -> node
    
    // Dummy head and tail to avoid null checks
    // head.next = most recently used
    // tail.prev = least recently used
    private final Node head; // dummy head
    private final Node tail; // dummy tail
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        
        // Initialize dummy nodes
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }
    
    public int get(int key) {
        if (!cache.containsKey(key)) return -1;
        
        Node node = cache.get(key);
        // Move to front (most recently used)
        moveToFront(node);
        return node.value;
    }
    
    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            // Update existing node
            Node node = cache.get(key);
            node.value = value;
            moveToFront(node);
        } else {
            // Insert new node
            Node newNode = new Node(key, value);
            cache.put(key, newNode);
            addToFront(newNode);
            
            // Evict LRU if over capacity
            if (cache.size() > capacity) {
                Node lru = tail.prev; // least recently used
                removeNode(lru);
                cache.remove(lru.key);
            }
        }
    }
    
    // Remove a node from the doubly linked list
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    // Add a node right after the dummy head (most recently used position)
    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
    
    // Move an existing node to the front
    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }
}
```

**Dry Run — capacity=2:**

```
put(1, 1):
  New node (1,1). addToFront.
  List: head ↔ [1,1] ↔ tail
  Cache: {1: node(1,1)}

put(2, 2):
  New node (2,2). addToFront.
  List: head ↔ [2,2] ↔ [1,1] ↔ tail
  Cache: {1: node(1,1), 2: node(2,2)}

get(1):
  Found node(1,1). moveToFront.
  List: head ↔ [1,1] ↔ [2,2] ↔ tail
  Return 1.

put(3, 3):
  New node (3,3). addToFront.
  List: head ↔ [3,3] ↔ [1,1] ↔ [2,2] ↔ tail
  Size=3 > capacity=2. Evict tail.prev = node(2,2).
  removeNode(2,2). cache.remove(2).
  List: head ↔ [3,3] ↔ [1,1] ↔ tail
  Cache: {1: node(1,1), 3: node(3,3)}

get(2):
  Not in cache. Return -1. ✓

put(4, 4):
  New node (4,4). addToFront.
  List: head ↔ [4,4] ↔ [3,3] ↔ [1,1] ↔ tail
  Size=3 > capacity=2. Evict tail.prev = node(1,1).
  removeNode(1,1). cache.remove(1).
  List: head ↔ [4,4] ↔ [3,3] ↔ tail
  Cache: {3: node(3,3), 4: node(4,4)}

get(1): -1 ✓
get(3): 3 ✓ (moveToFront: head ↔ [3,3] ↔ [4,4] ↔ tail)
get(4): 4 ✓
```

**Why dummy head and tail?** They eliminate edge cases. Without them, inserting into an empty list or removing the only node requires special handling. With dummies, `addToFront` and `removeNode` always have valid `prev` and `next` pointers to work with.

**Why not use `LinkedHashMap`?** You can, and it's shorter code. But interviewers at Meta/Amazon/Google almost always want the explicit HashMap + doubly linked list implementation. It demonstrates you understand the underlying mechanism. Mention `LinkedHashMap` as an alternative, then implement the full version.

**LinkedHashMap shortcut (mention but don't use as primary):**

```java
class LRUCacheShortcut extends LinkedHashMap<Integer, Integer> {
    private final int capacity;
    
    public LRUCacheShortcut(int capacity) {
        super(capacity, 0.75f, true); // accessOrder=true
        this.capacity = capacity;
    }
    
    public int get(int key) {
        return super.getOrDefault(key, -1);
    }
    
    public void put(int key, int value) {
        super.put(key, value);
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity;
    }
}
```

**Time:** O(1) for both `get` and `put`  
**Space:** O(capacity)

---

### Category D: Advanced (4 Problems)

---

#### Problem 14: Insert Delete GetRandom O(1) (LC 380)

**Difficulty:** Medium  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple, Goldman Sachs, Uber

**Problem:** Implement `RandomizedSet` with O(1) average time for `insert(val)`, `remove(val)`, and `getRandom()`. `getRandom` must return each element with equal probability.

**Hint:** HashMap alone can't do O(1) random access. ArrayList alone can't do O(1) remove by value. Combine them: ArrayList for random access, HashMap for O(1) index lookup.

**Solution:**

```java
class RandomizedSet {
    private final List<Integer> list;           // for O(1) random access
    private final Map<Integer, Integer> indexMap; // value -> index in list
    private final Random random;
    
    public RandomizedSet() {
        list = new ArrayList<>();
        indexMap = new HashMap<>();
        random = new Random();
    }
    
    public boolean insert(int val) {
        if (indexMap.containsKey(val)) return false;
        
        list.add(val);
        indexMap.put(val, list.size() - 1);
        return true;
    }
    
    public boolean remove(int val) {
        if (!indexMap.containsKey(val)) return false;
        
        // Swap val with the last element, then remove last
        int idx = indexMap.get(val);
        int lastVal = list.get(list.size() - 1);
        
        // Move last element to idx
        list.set(idx, lastVal);
        indexMap.put(lastVal, idx);
        
        // Remove last element
        list.remove(list.size() - 1);
        indexMap.remove(val);
        
        return true;
    }
    
    public int getRandom() {
        return list.get(random.nextInt(list.size()));
    }
}
```

**The swap trick for O(1) remove:** Removing from the middle of an ArrayList is O(n) (shifts elements). The trick: swap the element to remove with the last element, then remove the last element (O(1)). Update the HashMap to reflect the moved element's new index.

**Trace for remove(2) when list = [1, 2, 3, 4]:**

```
Before: list=[1,2,3,4], indexMap={1:0, 2:1, 3:2, 4:3}

idx = indexMap.get(2) = 1
lastVal = list.get(3) = 4

list.set(1, 4) → list=[1,4,3,4]
indexMap.put(4, 1) → indexMap={1:0, 2:1, 3:2, 4:1}

list.remove(3) → list=[1,4,3]
indexMap.remove(2) → indexMap={1:0, 4:1, 3:2}

After: list=[1,4,3], indexMap={1:0, 4:1, 3:2} ✓
```

**Edge case:** When removing the last element, `lastVal == val`. The swap is a no-op, but the code still works correctly because `indexMap.put(lastVal, idx)` just updates the same key, and then `indexMap.remove(val)` removes it.

**Time:** O(1) average for all operations  
**Space:** O(n)

---

#### Problem 15: First Missing Positive (LC 41)

**Difficulty:** Hard  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** Given an unsorted integer array `nums`, return the smallest missing positive integer. Must run in O(n) time and O(1) extra space.

**Hint:** The answer must be in range [1, n+1]. Use the array itself as a hash map: place each number `x` at index `x-1` (cyclic sort). Then scan for the first index where `nums[i] != i+1`.

**Solution — In-Place Hashing (Cyclic Sort):**

```java
class Solution {
    public int firstMissingPositive(int[] nums) {
        int n = nums.length;
        
        // Step 1: Place each number in its "correct" position
        // nums[i] should be i+1, so number x should be at index x-1
        for (int i = 0; i < n; i++) {
            // Keep swapping until nums[i] is in correct position or out of range
            while (nums[i] > 0 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) {
                // Swap nums[i] with nums[nums[i]-1]
                int correctIdx = nums[i] - 1;
                int temp = nums[correctIdx];
                nums[correctIdx] = nums[i];
                nums[i] = temp;
            }
        }
        
        // Step 2: Find first position where nums[i] != i+1
        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) return i + 1;
        }
        
        // All positions 1..n are filled, so answer is n+1
        return n + 1;
    }
}
```

**Trace for `nums = [3, 4, -1, 1]`:**

```
n=4. Correct state: [1,2,3,4]

i=0: nums[0]=3. correctIdx=2. nums[2]=-1 ≠ 3. Swap.
     nums=[−1,4,3,1]. nums[0]=−1. Out of range [1,4]. Stop.

i=1: nums[1]=4. correctIdx=3. nums[3]=1 ≠ 4. Swap.
     nums=[−1,1,3,4]. nums[1]=1. correctIdx=0. nums[0]=−1 ≠ 1. Swap.
     nums=[1,−1,3,4]. nums[1]=−1. Out of range. Stop.

i=2: nums[2]=3. correctIdx=2. nums[2]=3 == 3. Already correct. Stop.

i=3: nums[3]=4. correctIdx=3. nums[3]=4 == 4. Already correct. Stop.

After step 1: nums=[1,−1,3,4]

Step 2:
i=0: nums[0]=1 == 1 ✓
i=1: nums[1]=−1 ≠ 2 → return 2 ✓
```

**Why O(n) time despite nested loops?** Each number is swapped at most once (into its correct position). Once a number is in its correct position, it's never moved again. Total swaps ≤ n.

**Why the condition `nums[nums[i]-1] != nums[i]`?** Prevents infinite loop on duplicates. If the target position already has the correct value, stop (even if `nums[i]` is a duplicate that can't be placed).

**Time:** O(n), **Space:** O(1)

**Alternative with HashSet (O(n) space, simpler code):**

```java
public int firstMissingPositiveHashSet(int[] nums) {
    Set<Integer> set = new HashSet<>();
    for (int num : nums) set.add(num);
    for (int i = 1; i <= nums.length + 1; i++) {
        if (!set.contains(i)) return i;
    }
    return nums.length + 1; // unreachable
}
```

Mention both. The O(1) space solution is what makes this a Hard problem.

---

#### Problem 16: Copy List with Random Pointer (LC 138)

**Difficulty:** Medium  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Apple

**Problem:** A linked list where each node has a `next` pointer and a `random` pointer (which can point to any node or null). Return a deep copy of the list.

**Hint:** Use a HashMap to map original nodes to their copies. First pass: create all copies. Second pass: set next and random pointers.

**Solution:**

```java
class Solution {
    public Node copyRandomList(Node head) {
        if (head == null) return null;
        
        // Map: original node -> copy node
        Map<Node, Node> nodeMap = new HashMap<>();
        
        // Pass 1: Create all copy nodes
        Node curr = head;
        while (curr != null) {
            nodeMap.put(curr, new Node(curr.val));
            curr = curr.next;
        }
        
        // Pass 2: Set next and random pointers
        curr = head;
        while (curr != null) {
            Node copy = nodeMap.get(curr);
            copy.next = nodeMap.get(curr.next);     // null if curr.next is null
            copy.random = nodeMap.get(curr.random); // null if curr.random is null
            curr = curr.next;
        }
        
        return nodeMap.get(head);
    }
}
```

**Why two passes?** When setting `random` pointers, the target node might not have been created yet (it could be later in the list). The first pass ensures all copies exist before we start wiring them together.

**Why `nodeMap.get(curr.next)` works for null?** `HashMap.get(null)` returns `null`. So if `curr.next` is null, `nodeMap.get(null)` returns null, which is exactly what we want for `copy.next`.

**Time:** O(n), **Space:** O(n)

**O(1) space alternative (interweaving):** Create copies interleaved with originals, set random pointers, then separate. More complex, rarely asked in interviews. Mention it exists if the interviewer asks about space optimization.

---

#### Problem 17: Max Points on a Line (LC 149)

**Difficulty:** Hard  
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Goldman Sachs

**Problem:** Given an array of `points` where `points[i] = [xi, yi]`, return the maximum number of points that lie on the same straight line.

**Hint:** For each point, compute the slope to every other point. Points with the same slope from a fixed anchor are collinear. Use a HashMap with slope as key. Represent slope as a reduced fraction (dy/gcd, dx/gcd) to avoid floating-point precision issues.

**Solution:**

```java
class Solution {
    public int maxPoints(int[][] points) {
        int n = points.length;
        if (n <= 2) return n;
        
        int maxCount = 2;
        
        for (int i = 0; i < n; i++) {
            // For each anchor point, count slopes to all other points
            Map<String, Integer> slopeCount = new HashMap<>();
            
            for (int j = i + 1; j < n; j++) {
                int dx = points[j][0] - points[i][0];
                int dy = points[j][1] - points[i][1];
                
                // Normalize slope as reduced fraction
                String slope = normalizeSlope(dx, dy);
                
                int count = slopeCount.merge(slope, 1, Integer::sum);
                maxCount = Math.max(maxCount, count + 1); // +1 for anchor point
            }
        }
        
        return maxCount;
    }
    
    private String normalizeSlope(int dx, int dy) {
        // Vertical line
        if (dx == 0) return "vertical";
        // Horizontal line
        if (dy == 0) return "horizontal";
        
        // Normalize sign: keep dx positive
        if (dx < 0) {
            dx = -dx;
            dy = -dy;
        }
        
        // Reduce fraction by GCD
        int g = gcd(Math.abs(dx), Math.abs(dy));
        return (dy / g) + "/" + (dx / g);
    }
    
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
```

**Why GCD normalization?** Slopes 2/4 and 1/2 represent the same line. Without reduction, they'd be different keys. GCD reduces both to the canonical form 1/2.

**Why avoid floating-point?** `1.0/3.0` and `2.0/6.0` might not be exactly equal due to floating-point representation. Using integer fractions avoids this entirely.

**Why keep dx positive?** Slope 1/2 and -1/-2 represent the same direction. Normalizing the sign ensures they map to the same key.

**Time:** O(n² log(max_coord)) — O(n²) pairs, O(log max) for GCD  
**Space:** O(n) — slope map for each anchor

---

## 7. Common Mistakes and Edge Cases

### Common Mistakes

| Mistake | Example | Fix |
|---|---|---|
| Using mutable objects as keys | `Map<int[], Integer>` — array identity, not content | Use `String` or `List<Integer>` as key, or implement proper `hashCode`/`equals` |
| `Integer == Integer` for values > 127 | `map.get(key) == 127` works, `== 128` may fail | Always use `.equals()` for Integer comparison: `map.get(key).equals(128)` |
| ConcurrentModificationException | Removing from map while iterating with `for (int k : map.keySet())` | Iterate over a copy: `new HashSet<>(map.keySet())`, or use `Iterator.remove()` |
| Missing `containsKey` check | `map.get(key) + 1` throws NPE if key absent | Use `getOrDefault(key, 0)` or check `containsKey` first |
| HashMap when `int[26]` suffices | Using `Map<Character, Integer>` for lowercase letters | Use `int[26]` — faster, less memory, no boxing |
| Forgetting to update index | In Two-Sum, storing index before checking complement | Store AFTER checking to avoid using same element twice |
| Null keys causing NPE | `map.put(null, value)` works in HashMap but not TreeMap | Be explicit about null handling; avoid null keys in general |
| Modifying value objects in map | `map.get(key).add(item)` — this works, but `map.put(key, new List())` after `computeIfAbsent` is redundant | Understand that `computeIfAbsent` returns the existing or newly created value |

### Edge Cases to Always Test

**Empty input:**
```java
int[] nums = {};
// Most HashMap solutions handle this correctly (loop doesn't execute)
// But check: does your solution return the right default?
```

**All identical elements:**
```java
int[] nums = {5, 5, 5, 5};
// Frequency map: {5: 4}
// Two-Sum: if target=10, should return [0,1] (not [0,0])
```

**Single element:**
```java
int[] nums = {1};
// Longest consecutive: 1
// Two-Sum: no solution (can't use same element twice)
```

**Negative keys:**
```java
int[] nums = {-3, -1, 0, 1, 3};
// HashMap handles negative keys fine
// int[] array approach fails for negatives — must use HashMap
```

**Unicode characters:**
```java
String s = "café"; // contains non-ASCII
// int[26] fails — use Map<Character, Integer>
// int[128] fails for chars > 127 — use Map<Character, Integer>
```

**Integer overflow in slope calculation:**
```java
// dx * dy might overflow int if coordinates are large
// Use long or normalize before multiplying
```

**HashMap collision-heavy input (adversarial):**
```java
// Crafted inputs can cause O(n) per operation in HashMap
// Java 8+ converts collision chains to balanced trees at threshold 8
// In practice, not an interview concern, but worth knowing
```

### The `Integer == Integer` Trap

```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 127);
map.put("b", 128);

// WRONG — may fail for values outside [-128, 127]
if (map.get("a") == 127) { } // works (cached Integer)
if (map.get("b") == 128) { } // may fail (different Integer objects)

// CORRECT — always use .equals() for Integer comparison
if (map.get("a").equals(127)) { } // always works
if (map.get("b").equals(128)) { } // always works
```

Java caches `Integer` objects for values -128 to 127. Outside this range, `==` compares object references, not values. Always use `.equals()` when comparing `Integer` objects from a map.

---

## 8. Pattern Comparison

### When to Use Which Pattern

| Problem Type | HashMap | Two Pointers | Sliding Window | Prefix Sum |
|---|---|---|---|---|
| Two Sum (unsorted) | **Best** O(n) | O(n log n) sort first | N/A | N/A |
| Two Sum (sorted) | O(n) | **Best** O(n), O(1) space | N/A | N/A |
| Contains Duplicate | O(n) | O(n log n) sort first | N/A | N/A |
| Subarray Sum = K | N/A | Only for positives | Only for positives | **Best** O(n) |
| Longest no-repeat substring | O(n) | O(n) | **Best** O(n), cleaner | N/A |
| Group Anagrams | **Best** O(n*k) | N/A | N/A | N/A |
| Longest Consecutive | **Best** O(n) | O(n log n) sort first | N/A | N/A |
| Top K Frequent | **Best** O(n log k) | N/A | N/A | N/A |

### Detailed Comparison for Key Problems

**Two Sum:**
- HashMap: O(n) time, O(n) space. Works on unsorted input. One pass.
- Two Pointers: O(n log n) time (sort), O(1) space. Requires sorted input. Destroys original order (can't return original indices).
- **Choose HashMap** when you need original indices or input is unsorted.
- **Choose Two Pointers** when input is sorted and you only need existence (not indices).

**Contains Duplicate:**
- HashMap/HashSet: O(n) time, O(n) space.
- Sorting: O(n log n) time, O(1) extra space (in-place sort).
- **Choose HashSet** for speed.
- **Choose sorting** if interviewer asks for O(1) extra space.

**Subarray Sum = K:**
- HashMap (prefix sum): O(n) time, O(n) space. Handles negatives.
- Sliding Window: O(n) time, O(1) space. Only works for non-negative numbers.
- **Choose prefix sum + HashMap** for the general case.
- **Choose sliding window** only if all numbers are positive and you want O(1) space.

**Longest Substring Without Repeating Characters:**
- HashMap: O(n) time, O(k) space where k = distinct chars.
- Sliding Window with HashSet: O(n) time, O(k) space.
- Both are equivalent. Sliding window is slightly more idiomatic for this problem.

---

## 9. Quick Reference Cheat Sheet

### Java HashMap API

```java
// Creation
Map<K, V> map = new HashMap<>();
Map<K, V> map = new HashMap<>(initialCapacity); // avoid rehashing

// Basic operations
map.put(key, value);
map.get(key);                          // returns null if absent
map.getOrDefault(key, defaultValue);   // safe get
map.containsKey(key);
map.containsValue(value);              // O(n) — avoid
map.remove(key);
map.size();
map.isEmpty();

// Modern API (Java 8+)
map.merge(key, 1, Integer::sum);       // increment counter
map.computeIfAbsent(key, k -> new ArrayList<>()).add(item); // grouping
map.putIfAbsent(key, value);           // only insert if absent
map.compute(key, (k, v) -> ...);       // general update

// Iteration
for (K key : map.keySet()) { }
for (V value : map.values()) { }
for (Map.Entry<K, V> entry : map.entrySet()) {
    entry.getKey(); entry.getValue();
}

// Conversion
new ArrayList<>(map.values())          // values to list
new HashSet<>(map.keySet())            // keys to set
```

### Template Selector

```
Problem asks about...
├── counts / frequencies → Frequency Counter (Template 1)
├── pair with target sum → Two-Sum Complement (Template 2)
├── grouping by property → Grouping/Bucketing (Template 3)
├── "have I seen this?" → HashSet Existence (Template 4)
├── bounded chars (a-z) → int[26] array (Template 5)
└── first/last occurrence → Index Mapping (Template 2 variant)
```

### HashMap vs TreeMap vs LinkedHashMap

| Feature | HashMap | TreeMap | LinkedHashMap |
|---|---|---|---|
| Ordering | None | Sorted by key | Insertion order (or access order) |
| `get`/`put` | O(1) avg | O(log n) | O(1) avg |
| Null keys | 1 allowed | Not allowed | 1 allowed |
| Use case | Default | Range queries, floor/ceiling | LRU cache, ordered iteration |
| `firstKey()` / `lastKey()` | No | Yes | No |

### int[] vs HashMap Decision

```
Input characters are...
├── Lowercase letters only (a-z) → int[26]
├── ASCII printable (0-127) → int[128]
├── Any Unicode → Map<Character, Integer>
└── Integers (any range) → Map<Integer, Integer>
```

### Complexity Summary

| Operation | HashMap | HashSet | TreeMap | int[26] |
|---|---|---|---|---|
| Insert | O(1) avg | O(1) avg | O(log n) | O(1) |
| Lookup | O(1) avg | O(1) avg | O(log n) | O(1) |
| Delete | O(1) avg | O(1) avg | O(log n) | O(1) |
| Min/Max | O(n) | O(n) | O(log n) | O(26) = O(1) |
| Iteration | O(n) | O(n) | O(n) sorted | O(26) = O(1) |

### Common Patterns at a Glance

```java
// Frequency count
map.merge(key, 1, Integer::sum);

// Two-Sum check
if (seen.containsKey(target - num)) { /* found */ }
seen.put(num, index);

// Grouping
map.computeIfAbsent(key, k -> new ArrayList<>()).add(item);

// Duplicate detection
if (!seen.add(num)) { /* duplicate */ }

// Sliding window with frequency
freq[s.charAt(right) - 'a']++;
freq[s.charAt(left) - 'a']--;
```

---

## 10. Practice Roadmap

### Week 1: Easy Problems — Build the Foundation

| # | Problem | LC # | Key Pattern | Time |
|---|---|---|---|---|
| 1 | Two Sum | 1 | Two-Sum complement lookup | 20 min |
| 2 | Contains Duplicate | 217 | HashSet existence | 10 min |
| 3 | Valid Anagram | 242 | Frequency comparison | 15 min |
| 4 | Ransom Note | 383 | Frequency counting | 15 min |
| 5 | First Unique Character | 387 | Frequency + index | 15 min |
| 6 | Majority Element | 169 | Frequency + Boyer-Moore | 20 min |

**Week 1 goal:** Internalize the five templates. Every problem here maps directly to one template. If you can't solve any of these in under 20 minutes, re-read the template and trace through the dry run again.

**Focus for Week 1:**
- Write `merge()` and `computeIfAbsent()` from memory
- Practice the "store after check" discipline for Two-Sum
- Know when to use `int[26]` vs `HashMap`

---

### Week 2: Medium Core — Pattern Combinations

| # | Problem | LC # | Key Pattern | Time |
|---|---|---|---|---|
| 7 | Group Anagrams | 49 | Grouping + canonical key | 25 min |
| 8 | Top K Frequent Elements | 347 | Frequency + heap/bucket | 30 min |
| 9 | Longest Consecutive Sequence | 128 | HashSet + sequence start | 30 min |
| 10 | Isomorphic Strings | 205 | Bidirectional mapping | 20 min |
| 11 | Word Pattern | 290 | Bidirectional mapping | 20 min |
| 12 | Contains Duplicate II | 219 | Index mapping + window | 20 min |

**Week 2 goal:** Combine patterns. LC 49 combines frequency counting with grouping. LC 347 combines frequency counting with a secondary data structure. LC 128 uses HashSet in a non-obvious way.

**Focus for Week 2:**
- For LC 49: implement both the sorted-key and freq-array-key approaches
- For LC 347: implement both heap and bucket sort approaches
- For LC 128: be able to explain WHY it's O(n) despite the nested loop

---

### Week 3: Design Problems — System Thinking

| # | Problem | LC # | Key Pattern | Time |
|---|---|---|---|---|
| 13 | LRU Cache | 146 | HashMap + doubly linked list | 45 min |
| 14 | Insert Delete GetRandom O(1) | 380 | HashMap + ArrayList + swap trick | 35 min |
| 15 | Copy List with Random Pointer | 138 | HashMap for node mapping | 30 min |

**Week 3 goal:** Design problems require thinking about invariants and edge cases, not just algorithms. For LC 146, draw the linked list state after each operation before coding.

**Focus for Week 3:**
- LC 146: implement from scratch without looking at notes. This is a top-5 Meta/Amazon design problem.
- LC 380: understand the swap trick deeply — why does it work? What's the edge case when removing the last element?
- LC 138: understand why two passes are necessary

---

### Week 4: Hard Problems — Interview Differentiators

| # | Problem | LC # | Key Pattern | Time |
|---|---|---|---|---|
| 16 | First Missing Positive | 41 | In-place hashing / cyclic sort | 40 min |
| 17 | Max Points on a Line | 149 | HashMap with GCD-normalized slope | 40 min |

**Week 4 goal:** These problems require insight beyond standard templates. LC 41 requires recognizing that the array itself can serve as a hash map. LC 149 requires careful handling of slope representation.

**Focus for Week 4:**
- LC 41: trace through the cyclic sort manually for `[3,4,-1,1]` and `[1,2,0]`
- LC 149: understand why floating-point fails and how GCD fixes it

---

### Total: 17 Problems Over 4 Weeks

**Minimum viable prep (if time-constrained):** LC 1, 49, 347, 128, 146. These five cover the most common patterns and appear at the highest frequency across FAANG interviews.

**If you only have one week:** Do Week 1 + LC 49 + LC 128 + LC 146. That's 9 problems covering every major pattern.

**After completing all 17:** Revisit LC 1 and LC 146 without notes. These two problems alone appear in more FAANG interviews than the other 15 combined.

---

*Document 6 of 20 — FAANG DSA Prep Series*  
*Next: Document 7 — Trees and Tree Traversal*
