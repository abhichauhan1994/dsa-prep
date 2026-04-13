# Topic 1: Sliding Window

The Sliding Window pattern is one of the most frequently tested patterns in FAANG interviews. It transforms naive O(n*k) brute-force solutions into elegant O(n) algorithms by maintaining a contiguous "window" of elements and updating state incrementally as the window moves, rather than recomputing from scratch each time.

This document covers every variant, every common problem, and every trap you'll encounter. Work through it once, and you'll recognize the pattern instantly in any interview.

---

## Table of Contents

1. [Core Concept](#1-core-concept)
2. [ELI5 — The Intuition](#2-eli5--the-intuition)
3. [When to Use — Recognition Signals](#3-when-to-use--recognition-signals)
4. [Core Templates in Java](#4-core-templates-in-java)
   - [Template 1: Fixed-Size Window](#template-1-fixed-size-window)
   - [Template 2: Variable Window — Find Longest](#template-2-variable-window--find-longest)
   - [Template 3: Variable Window — Find Shortest](#template-3-variable-window--find-shortest)
   - [Template 4: Frequency Map Window](#template-4-frequency-map-window)
   - [Template 5: Exactly K = At Most K minus At Most K-1](#template-5-exactly-k--at-most-k---at-most-k-1)
5. [Real-World Applications](#5-real-world-applications)
6. [Problem Categories and Solutions](#6-problem-categories-and-solutions)
   - [Category A: Fixed-Size Window](#category-a-fixed-size-window)
   - [Category B: Variable Window — Longest/Maximum](#category-b-variable-window--longestmaximum)
   - [Category C: Variable Window — Shortest/Minimum](#category-c-variable-window--shortestminimum)
   - [Category D: Counting / Exactly-K Problems](#category-d-counting--exactly-k-problems)
   - [Category E: Advanced / Hybrid](#category-e-advanced--hybrid)
7. [Common Mistakes and Edge Cases](#7-common-mistakes-and-edge-cases)
8. [Pattern Comparison](#8-pattern-comparison)
9. [Quick Reference Cheat Sheet](#9-quick-reference-cheat-sheet)
10. [Practice Roadmap](#10-practice-roadmap)

---

**Difficulty Distribution:**
- Easy: 14%
- Medium: 60%
- Hard: 26%

**Top Companies:**
- Google: 101 problems
- Amazon: 94 problems
- Meta: 71 problems
- Microsoft: 70 problems
- Bloomberg: 61 problems

---

## 1. Core Concept

### What is Sliding Window?

A sliding window is a subarray or substring of fixed or variable length that moves through a larger array or string from left to right. Instead of recomputing the answer for every possible window from scratch, you maintain the current window's state and update it by:

1. **Adding** the new element entering from the right
2. **Removing** the element leaving from the left

This incremental update is the entire trick. The window "slides" forward, and you never look at the same element twice in the inner loop.

### The Fundamental Idea

Consider finding the maximum sum of any subarray of size k in an array of n elements.

**Brute force:** For each starting index i, sum elements from i to i+k-1. That's O(n*k).

```
arr = [2, 1, 5, 1, 3, 2], k = 3

Window [2,1,5] → sum = 8
Window [1,5,1] → sum = 7   (recomputed 1 and 5 again — wasteful)
Window [5,1,3] → sum = 9   (recomputed 5 and 1 again — wasteful)
Window [1,3,2] → sum = 6
```

**Sliding window:** When you move from window [2,1,5] to [1,5,1], you just subtract 2 (left element leaving) and add 1 (right element entering). One subtraction, one addition. O(n) total.

```
sum = 8
Move right: sum = 8 - arr[0] + arr[3] = 8 - 2 + 1 = 7
Move right: sum = 7 - arr[1] + arr[4] = 7 - 1 + 3 = 9
Move right: sum = 9 - arr[2] + arr[5] = 9 - 5 + 2 = 6
```

### Time Complexity Improvement

| Approach | Time | Space |
|----------|------|-------|
| Brute force (nested loops) | O(n * k) | O(1) |
| Sliding window | O(n) | O(1) to O(k) |

For n = 10^5 and k = 10^4, brute force does 10^9 operations. Sliding window does 10^5. That's the difference between TLE and AC.

### Two Types of Windows

**Fixed-size window:** The window always contains exactly k elements. You slide it one step at a time. The left pointer always trails the right pointer by exactly k-1 positions.

**Variable-size (dynamic) window:** The window expands and contracts based on a condition. You expand by moving the right pointer, and shrink by moving the left pointer when the window becomes invalid (or when you're looking for the minimum valid window).

---

## 2. ELI5 — The Intuition

### Fixed Window: The Train Window

Imagine you're on a train, looking out through a fixed-size window. As the train moves forward, new scenery appears on the right side of your window and old scenery disappears from the left. You never need to look at all the scenery again from the beginning. You just track what changed: one new thing came in, one old thing went out.

That's a fixed sliding window. The window size never changes. You just update your running answer as the window moves.

### Variable Window: The Rubber Band

Now imagine you're holding a rubber band stretched between your two index fingers, both resting on a ruler. Your left finger is the `left` pointer, your right finger is the `right` pointer.

- You want to find the longest stretch of the ruler where some condition holds (say, no repeated numbers).
- You move your right finger to the right, stretching the band, as long as the condition is satisfied.
- The moment the condition breaks, you move your left finger to the right, shrinking the band, until the condition is satisfied again.
- At every valid stretch, you record the length.

The rubber band never jumps backward. Both fingers only move right. That's why it's O(n): each element is added once and removed at most once.

### The "Exactly K" Trick: Two Rubber Bands

Some problems ask for subarrays with exactly K distinct elements. That's hard to handle directly. But here's the insight: "exactly K" = "at most K" minus "at most K-1". So you run the rubber band exercise twice with different constraints and subtract the counts. This is a powerful mathematical trick that converts a hard problem into two easy ones.

---

## 3. When to Use — Recognition Signals

### Green Flags — Use Sliding Window

Read the problem statement carefully. These phrases almost always signal a sliding window:

- "contiguous subarray" or "contiguous substring"
- "longest substring that..."
- "shortest subarray with..."
- "maximum/minimum sum subarray of size k"
- "subarray of size exactly k"
- "at most k distinct characters/elements"
- "permutation of one string in another"
- "anagram in a string"
- "find all windows where..."
- "sliding window of size k"
- "moving average"

The key structural requirement: **the answer must be a contiguous portion of the input**, and **the validity condition can be maintained incrementally** as the window moves.

### Red Flags — Do NOT Use Sliding Window

**Non-contiguous subsequences:** If the problem asks for subsequences (elements don't need to be adjacent), sliding window won't work. Use DP or two pointers on sorted arrays instead.

**Negative numbers in sum problems:** The sliding window works for sum problems because adding an element always increases the sum and removing always decreases it (monotonic property). With negative numbers, this breaks. A window might become valid again after shrinking past a negative element. Use prefix sums + hash map or Kadane's algorithm instead.

**Need all combinations or permutations:** If you need to enumerate all possible subsets or arrangements, use backtracking. Sliding window only finds optimal windows, not all windows.

**Circular arrays without modification:** Standard sliding window assumes linear traversal. Circular arrays need special handling (usually duplicate the array).

**2D problems:** Sliding window extends to 2D (sliding window on rows + prefix sums on columns), but it's a different technique. Don't apply 1D templates directly.

---

## 4. Core Templates in Java

These five templates cover 95% of sliding window problems. Memorize the structure, understand the invariants, and adapt as needed.

---

### Template 1: Fixed-Size Window

**Use when:** The problem specifies a window of exactly size k.

**Key invariant:** `right - left + 1 == k` at all times after the initial window is formed.

```java
public int fixedWindowTemplate(int[] arr, int k) {
    int n = arr.length;
    
    // Edge case: window larger than array
    if (n < k) return -1; // or 0, or throw, depending on problem
    
    int windowSum = 0;
    int result = Integer.MIN_VALUE; // or 0, or MAX_VALUE, depending on problem
    
    // Step 1: Build the first window of size k
    // We process the first k elements before the main loop
    for (int i = 0; i < k; i++) {
        windowSum += arr[i];
    }
    result = windowSum;
    
    // Step 2: Slide the window from position k to n-1
    // At each step: add arr[right], remove arr[right - k]
    for (int right = k; right < n; right++) {
        // Add the new element entering from the right
        windowSum += arr[right];
        
        // Remove the element leaving from the left
        // The element leaving is at index (right - k)
        // Because: left = right - k + 1, so the element just outside is right - k
        windowSum -= arr[right - k];
        
        // Update result (this is problem-specific)
        result = Math.max(result, windowSum);
    }
    
    return result;
}
```

**Execution trace for arr = [2, 1, 5, 1, 3, 2], k = 3:**
```
Initial window: [2,1,5], sum = 8, result = 8
right=3: add arr[3]=1, remove arr[0]=2 → sum=7, result=8
right=4: add arr[4]=3, remove arr[1]=1 → sum=9, result=9
right=5: add arr[5]=2, remove arr[2]=5 → sum=6, result=9
Answer: 9
```

**Alternative: Two-pointer style (same logic, different framing)**

```java
public int fixedWindowTwoPointer(int[] arr, int k) {
    int left = 0, right = 0;
    int windowSum = 0;
    int result = Integer.MIN_VALUE;
    
    while (right < arr.length) {
        // Expand: add element at right
        windowSum += arr[right];
        
        // Window has exactly k elements when right - left + 1 == k
        if (right - left + 1 == k) {
            result = Math.max(result, windowSum);
            
            // Shrink by one to maintain fixed size for next iteration
            windowSum -= arr[left];
            left++;
        }
        
        right++;
    }
    
    return result;
}
```

---

### Template 2: Variable Window — Find Longest

**Use when:** Find the longest/maximum window satisfying some condition.

**Strategy:** Expand right as far as possible. When the window becomes invalid, shrink from the left until it's valid again. Record the window size after every expansion (not after shrinking, because we want the longest valid window).

**Key invariant:** After the shrink loop, the window `[left, right]` is always valid.

```java
public int variableWindowLongest(int[] arr) {
    int left = 0;
    int result = 0;
    
    // State variables — what you track depends on the problem
    // Examples: HashMap<Integer, Integer> freq, int distinctCount, int sum
    Map<Integer, Integer> freq = new HashMap<>();
    
    for (int right = 0; right < arr.length; right++) {
        // Step 1: Expand — add arr[right] to the window state
        freq.merge(arr[right], 1, Integer::sum);
        
        // Step 2: Shrink — while window is INVALID, remove from left
        // The condition here is problem-specific
        while (isInvalid(freq)) {
            // Remove arr[left] from window state
            freq.merge(arr[left], -1, Integer::sum);
            if (freq.get(arr[left]) == 0) freq.remove(arr[left]);
            left++;
        }
        
        // Step 3: Window [left, right] is now valid
        // Update result — window size is right - left + 1
        result = Math.max(result, right - left + 1);
    }
    
    return result;
}

// This method represents your validity condition
private boolean isInvalid(Map<Integer, Integer> freq) {
    // Example: more than 2 distinct elements
    return freq.size() > 2;
}
```

**Why update result AFTER shrinking?**

Because after the while loop, the window is guaranteed to be valid. If you update before shrinking, you might record an invalid window size.

**Why use `while` not `if` for shrinking?**

With `if`, you shrink by at most one element per iteration. But sometimes you need to shrink multiple elements before the window becomes valid again. `while` handles this correctly. Using `if` is a classic bug.

---

### Template 3: Variable Window — Find Shortest

**Use when:** Find the shortest/minimum window satisfying some condition.

**Strategy:** Expand right until the window becomes valid. Then shrink from the left as long as the window remains valid (to minimize the window). Record the window size DURING shrinking (every valid window is a candidate).

**Key difference from Template 2:** You update the result inside the shrink loop, not outside it.

```java
public int variableWindowShortest(int[] arr, int target) {
    int left = 0;
    int result = Integer.MAX_VALUE;
    int windowSum = 0; // or whatever state you're tracking
    
    for (int right = 0; right < arr.length; right++) {
        // Step 1: Expand — add arr[right] to window state
        windowSum += arr[right];
        
        // Step 2: Shrink — WHILE window is VALID, record and shrink
        // (opposite of Template 2: here we shrink while valid)
        while (windowSum >= target) {
            // Update result INSIDE the loop — this window is valid
            result = Math.min(result, right - left + 1);
            
            // Remove arr[left] from window state
            windowSum -= arr[left];
            left++;
        }
    }
    
    return result == Integer.MAX_VALUE ? 0 : result;
}
```

**The mental model:**

- Template 2 (longest): shrink to fix invalidity, then record. "Fix first, then measure."
- Template 3 (shortest): record while valid, then shrink to minimize. "Measure while shrinking."

---

### Template 4: Frequency Map Window (Anagram/Permutation Problems)

**Use when:** You need to find windows that are permutations or anagrams of a pattern string.

**Key idea:** Maintain a frequency map of the pattern. Track how many characters in the window have the "correct" frequency (the `formed` variable). When `formed == required`, the window is a valid anagram.

```java
public List<Integer> frequencyMapWindow(String s, String p) {
    List<Integer> result = new ArrayList<>();
    
    if (s.length() < p.length()) return result;
    
    int k = p.length(); // Fixed window size for anagram problems
    
    // Frequency map for pattern
    int[] pFreq = new int[26];
    for (char c : p.toCharArray()) {
        pFreq[c - 'a']++;
    }
    
    // Frequency map for current window
    int[] wFreq = new int[26];
    
    // How many distinct characters in pattern have non-zero frequency
    int required = 0;
    for (int f : pFreq) if (f > 0) required++;
    
    // How many characters in window currently match pattern frequency
    int formed = 0;
    
    int left = 0;
    
    for (int right = 0; right < s.length(); right++) {
        // Add character at right to window
        char c = s.charAt(right);
        wFreq[c - 'a']++;
        
        // Check if this character's frequency now matches pattern
        if (pFreq[c - 'a'] > 0 && wFreq[c - 'a'] == pFreq[c - 'a']) {
            formed++;
        }
        
        // When window size exceeds k, shrink from left
        if (right - left + 1 > k) {
            char leftChar = s.charAt(left);
            
            // Check if removing this character breaks a match
            if (pFreq[leftChar - 'a'] > 0 && wFreq[leftChar - 'a'] == pFreq[leftChar - 'a']) {
                formed--;
            }
            
            wFreq[leftChar - 'a']--;
            left++;
        }
        
        // If window size is k and all characters match, record
        if (right - left + 1 == k && formed == required) {
            result.add(left);
        }
    }
    
    return result;
}
```

**Why track `formed` instead of just comparing arrays?**

Comparing two frequency arrays takes O(26) = O(1) time, so it's technically fine. But the `formed` counter is cleaner and extends naturally to HashMap-based solutions for larger character sets (Unicode, etc.).

---

### Template 5: Exactly K = At Most K minus At Most K-1

**Use when:** Count subarrays with EXACTLY K of something (distinct elements, odd numbers, etc.).

**The insight:** Directly counting "exactly K" is hard because the window can't maintain a simple valid/invalid state. But "at most K" is easy with Template 2. And:

```
count(exactly K) = count(at most K) - count(at most K-1)
```

```java
public int exactlyK(int[] arr, int k) {
    return atMostK(arr, k) - atMostK(arr, k - 1);
}

private int atMostK(int[] arr, int k) {
    int left = 0;
    int count = 0;
    Map<Integer, Integer> freq = new HashMap<>();
    
    for (int right = 0; right < arr.length; right++) {
        // Expand: add arr[right]
        freq.merge(arr[right], 1, Integer::sum);
        
        // Shrink: while more than k distinct elements
        while (freq.size() > k) {
            freq.merge(arr[left], -1, Integer::sum);
            if (freq.get(arr[left]) == 0) freq.remove(arr[left]);
            left++;
        }
        
        // Every subarray ending at right and starting from left to right is valid
        // There are (right - left + 1) such subarrays
        count += right - left + 1;
    }
    
    return count;
}
```

**Why does `count += right - left + 1` work?**

When the window is `[left, right]`, all subarrays ending at `right` with start index from `left` to `right` are valid. That's `right - left + 1` subarrays. You're counting all valid subarrays, not just the window itself.

**Execution trace for arr = [1,2,1,2,3], k = 2:**
```
atMostK(arr, 2):
right=0: freq={1:1}, left=0, count += 1 → count=1
right=1: freq={1:1,2:1}, left=0, count += 2 → count=3
right=2: freq={1:2,2:1}, left=0, count += 3 → count=6
right=3: freq={1:2,2:2}, left=0, count += 4 → count=10
right=4: freq={1:2,2:2,3:1} → size=3 > 2
  shrink: remove arr[0]=1 → freq={1:1,2:2,3:1}, left=1
  still size=3 > 2
  shrink: remove arr[1]=2 → freq={1:1,2:1,3:1}, left=2
  still size=3 > 2
  shrink: remove arr[2]=1 → freq={2:1,3:1}, left=3
  size=2, stop
  count += (4-3+1) = 2 → count=12

atMostK(arr, 1):
right=0: freq={1:1}, left=0, count += 1 → count=1
right=1: freq={1:1,2:1} → size=2 > 1
  shrink: remove arr[0]=1 → freq={2:1}, left=1
  count += 1 → count=2
right=2: freq={2:1,1:1} → size=2 > 1
  shrink: remove arr[1]=2 → freq={1:1}, left=2
  count += 1 → count=3
right=3: freq={1:1,2:1} → size=2 > 1
  shrink: remove arr[2]=1 → freq={2:1}, left=3
  count += 1 → count=4
right=4: freq={2:1,3:1} → size=2 > 1
  shrink: remove arr[3]=2 → freq={3:1}, left=4
  count += 1 → count=5

exactlyK = 12 - 5 = 7
```

---

## 5. Real-World Applications

### 1. TCP Sliding Window Protocol

TCP uses a sliding window for flow control between sender and receiver. The receiver advertises a "window size" — the number of bytes it can buffer. The sender can transmit up to that many bytes before waiting for an acknowledgment (ACK). As ACKs arrive, the window slides forward, allowing more data to be sent. This prevents the sender from overwhelming a slow receiver. The window size dynamically adjusts based on network conditions (congestion control), making it a variable-size sliding window in practice.

### 2. API Rate Limiting

The sliding window algorithm is the gold standard for rate limiting. Instead of counting requests in fixed time buckets (which allows burst traffic at bucket boundaries), a sliding window counts requests in the last N seconds from the current moment. AWS API Gateway, Cloudflare, and Redis-based rate limiters use this approach. A common implementation stores request timestamps in a sorted set, removes entries older than N seconds, and checks if the count exceeds the limit. This gives smooth, fair rate limiting without the "double burst" problem of fixed windows.

### 3. Stream Processing

Apache Flink and Apache Kafka Streams use sliding windows and tumbling windows for real-time analytics. A sliding window might compute "average CPU usage over the last 5 minutes, updated every 30 seconds." The window slides forward every 30 seconds, dropping old data and incorporating new data. This is exactly the fixed-size window pattern applied to time-series data streams. Flink's `SlidingEventTimeWindows` and `SlidingProcessingTimeWindows` implement this directly.

### 4. Database Query Optimization

Time-series databases like InfluxDB and TimescaleDB compute moving averages using sliding window functions. A query like `SELECT moving_average(value, 7) FROM metrics` computes a 7-period moving average by maintaining a running sum and updating it as the window slides. Without the sliding window optimization, this would require O(n*k) work. With it, it's O(n). PostgreSQL's window functions (`OVER (ROWS BETWEEN 6 PRECEDING AND CURRENT ROW)`) use the same principle.

### 5. Network Monitoring

Packet analysis tools like Wireshark and network intrusion detection systems (SNORT, Suricata) track metrics over sliding time windows. For example, detecting a DDoS attack by counting packets from a source IP in the last 1 second. If the count exceeds a threshold, an alert fires. The window slides forward with each new packet, adding the new packet and dropping packets older than 1 second. This is the variable-size window pattern applied to a time dimension.

### 6. Financial Systems

Moving averages are fundamental to technical analysis in trading. A Simple Moving Average (SMA) over 20 days is a fixed-size sliding window: sum the last 20 closing prices, divide by 20, slide forward one day. An Exponential Moving Average (EMA) is a weighted variant. Trading platforms compute these in real time as new price data arrives. High-frequency trading systems use sliding windows over millisecond-level data to detect patterns and execute trades.

---

## 6. Problem Categories and Solutions

---

### Category A: Fixed-Size Window

---

#### LC 643 — Maximum Average Subarray I

**Difficulty:** Easy
**Companies:** Amazon, Google, Meta, Microsoft, Facebook

**Problem:** Given an integer array `nums` and an integer `k`, find the contiguous subarray of length `k` that has the maximum average value. Return the maximum average.

**Why sliding window:** Fixed window size k, looking for maximum sum (which gives maximum average). Classic fixed-size window.

**Hint:** Don't divide by k at every step. Maintain the sum, find the maximum sum, then divide once at the end.

```java
class Solution {
    public double findMaxAverage(int[] nums, int k) {
        int n = nums.length;
        
        // Build the first window
        double windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += nums[i];
        }
        
        double maxSum = windowSum;
        
        // Slide the window
        for (int right = k; right < n; right++) {
            // Add new element, remove element that left the window
            windowSum += nums[right];
            windowSum -= nums[right - k];
            
            maxSum = Math.max(maxSum, windowSum);
        }
        
        // Divide once at the end
        return maxSum / k;
    }
}
```

**Time:** O(n) | **Space:** O(1)

**Key insight:** Dividing inside the loop is wasteful and introduces floating-point operations unnecessarily. Find the max sum, divide once.

---

#### LC 1456 — Maximum Number of Vowels in a Substring of Given Length

**Difficulty:** Medium
**Companies:** Amazon, Google, Meta, Microsoft

**Problem:** Given a string `s` and an integer `k`, return the maximum number of vowel letters in any substring of `s` with length `k`.

**Why sliding window:** Fixed window size k, counting vowels. When the window slides, you add 1 if the new character is a vowel, subtract 1 if the leaving character was a vowel.

```java
class Solution {
    public int maxVowels(String s, int k) {
        Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u');
        
        int vowelCount = 0;
        
        // Build first window
        for (int i = 0; i < k; i++) {
            if (vowels.contains(s.charAt(i))) vowelCount++;
        }
        
        int maxVowels = vowelCount;
        
        // Slide the window
        for (int right = k; right < s.length(); right++) {
            // Add new character
            if (vowels.contains(s.charAt(right))) vowelCount++;
            
            // Remove leaving character
            if (vowels.contains(s.charAt(right - k))) vowelCount--;
            
            maxVowels = Math.max(maxVowels, vowelCount);
        }
        
        return maxVowels;
    }
}
```

**Time:** O(n) | **Space:** O(1) — the vowel set is constant size

---

#### LC 567 — Permutation in String

**Difficulty:** Medium
**Companies:** Google, Amazon, Microsoft, Meta, Bloomberg

**Problem:** Given strings `s1` and `s2`, return true if `s2` contains a permutation of `s1`. In other words, one of `s1`'s permutations is a substring of `s2`.

**Why sliding window:** A permutation has the same character frequencies as the original. So you need a fixed window of size `s1.length()` in `s2` where the frequency map matches `s1`'s frequency map.

**Hint:** Use the `formed` counter pattern from Template 4. Avoid comparing arrays at every step.

```java
class Solution {
    public boolean checkInclusion(String s1, String s2) {
        if (s1.length() > s2.length()) return false;
        
        int k = s1.length();
        int[] p1Freq = new int[26];
        int[] windowFreq = new int[26];
        
        // Build frequency map for s1
        for (char c : s1.toCharArray()) {
            p1Freq[c - 'a']++;
        }
        
        // Count how many distinct characters s1 has
        int required = 0;
        for (int f : p1Freq) if (f > 0) required++;
        
        int formed = 0;
        int left = 0;
        
        for (int right = 0; right < s2.length(); right++) {
            // Add character at right
            char c = s2.charAt(right);
            windowFreq[c - 'a']++;
            
            // Check if this character now matches s1's requirement
            if (p1Freq[c - 'a'] > 0 && windowFreq[c - 'a'] == p1Freq[c - 'a']) {
                formed++;
            }
            
            // Shrink window if it exceeds size k
            if (right - left + 1 > k) {
                char leftChar = s2.charAt(left);
                
                // Check if removing this character breaks a match
                if (p1Freq[leftChar - 'a'] > 0 && windowFreq[leftChar - 'a'] == p1Freq[leftChar - 'a']) {
                    formed--;
                }
                
                windowFreq[leftChar - 'a']--;
                left++;
            }
            
            // Valid permutation found
            if (right - left + 1 == k && formed == required) {
                return true;
            }
        }
        
        return false;
    }
}
```

**Time:** O(n) where n = s2.length() | **Space:** O(1) — arrays of size 26

**Key insight:** The `formed` counter avoids O(26) array comparison at every step. When `formed == required`, the window is a valid permutation.

---

#### LC 438 — Find All Anagrams in a String

**Difficulty:** Medium
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg

**Problem:** Given strings `s` and `p`, return an array of all the start indices of `p`'s anagrams in `s`.

**Why sliding window:** Same as LC 567, but collect all valid window start indices instead of returning on first match.

```java
class Solution {
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        
        if (s.length() < p.length()) return result;
        
        int k = p.length();
        int[] pFreq = new int[26];
        int[] windowFreq = new int[26];
        
        for (char c : p.toCharArray()) pFreq[c - 'a']++;
        
        int required = 0;
        for (int f : pFreq) if (f > 0) required++;
        
        int formed = 0;
        int left = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            windowFreq[c - 'a']++;
            
            if (pFreq[c - 'a'] > 0 && windowFreq[c - 'a'] == pFreq[c - 'a']) {
                formed++;
            }
            
            if (right - left + 1 > k) {
                char leftChar = s.charAt(left);
                
                if (pFreq[leftChar - 'a'] > 0 && windowFreq[leftChar - 'a'] == pFreq[leftChar - 'a']) {
                    formed--;
                }
                
                windowFreq[leftChar - 'a']--;
                left++;
            }
            
            // Collect all valid windows (difference from LC 567)
            if (right - left + 1 == k && formed == required) {
                result.add(left);
            }
        }
        
        return result;
    }
}
```

**Time:** O(n) | **Space:** O(1)

---

#### LC 239 — Sliding Window Maximum

**Difficulty:** Hard
**Companies:** Google, Amazon, Microsoft, Bloomberg, Adobe, Apple, Citadel, DoorDash, Goldman Sachs (48 companies total)

**Problem:** Given an integer array `nums` and an integer `k`, return an array of the maximum values in each sliding window of size k.

**Why this is different:** You can't just track the current maximum. When the maximum element leaves the window, you need the next maximum instantly. A simple variable won't work.

**The trick:** Use a monotonic deque (double-ended queue) that stores indices. The deque maintains elements in decreasing order. The front of the deque is always the index of the maximum element in the current window.

**Hint:** This is a hybrid problem: fixed-size sliding window + monotonic deque. The sliding window handles the window boundaries; the deque handles efficient maximum queries.

```java
class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        
        // Deque stores INDICES, not values
        // Elements in deque are in decreasing order of their values
        // Front of deque = index of maximum element in current window
        Deque<Integer> deque = new ArrayDeque<>();
        
        int resultIdx = 0;
        
        for (int right = 0; right < n; right++) {
            // Step 1: Remove indices from the BACK that are smaller than nums[right]
            // They can never be the maximum while nums[right] is in the window
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[right]) {
                deque.pollLast();
            }
            
            // Add current index to the back
            deque.addLast(right);
            
            // Step 2: Remove indices from the FRONT that are outside the window
            // A valid index must satisfy: index >= right - k + 1
            if (deque.peekFirst() < right - k + 1) {
                deque.pollFirst();
            }
            
            // Step 3: Once we have a full window (right >= k-1), record the maximum
            // The maximum is always at the front of the deque
            if (right >= k - 1) {
                result[resultIdx++] = nums[deque.peekFirst()];
            }
        }
        
        return result;
    }
}
```

**Dry Run for nums = [1, 3, -1, -3, 5, 3, 6, 7], k = 3:**

```
right=0: nums[0]=1
  deque empty, add 0 → deque=[0]
  right < k-1, no result yet

right=1: nums[1]=3
  nums[deque.last()]=nums[0]=1 < 3, remove 0 → deque=[]
  add 1 → deque=[1]
  right < k-1, no result yet

right=2: nums[2]=-1
  nums[deque.last()]=nums[1]=3 >= -1, don't remove
  add 2 → deque=[1,2]
  front=1 >= right-k+1=0, valid
  right >= k-1=2, record nums[deque.front()]=nums[1]=3 → result=[3]

right=3: nums[3]=-3
  nums[deque.last()]=nums[2]=-1 >= -3, don't remove
  add 3 → deque=[1,2,3]
  front=1 >= right-k+1=1, valid
  record nums[1]=3 → result=[3,3]

right=4: nums[4]=5
  nums[deque.last()]=nums[3]=-3 < 5, remove 3 → deque=[1,2]
  nums[deque.last()]=nums[2]=-1 < 5, remove 2 → deque=[1]
  nums[deque.last()]=nums[1]=3 < 5, remove 1 → deque=[]
  add 4 → deque=[4]
  front=4 >= right-k+1=2, valid
  record nums[4]=5 → result=[3,3,5]

right=5: nums[5]=3
  nums[deque.last()]=nums[4]=5 >= 3, don't remove
  add 5 → deque=[4,5]
  front=4 >= right-k+1=3, valid
  record nums[4]=5 → result=[3,3,5,5]

right=6: nums[6]=6
  nums[deque.last()]=nums[5]=3 < 6, remove 5 → deque=[4]
  nums[deque.last()]=nums[4]=5 < 6, remove 4 → deque=[]
  add 6 → deque=[6]
  front=6 >= right-k+1=4, valid
  record nums[6]=6 → result=[3,3,5,5,6]

right=7: nums[7]=7
  nums[deque.last()]=nums[6]=6 < 7, remove 6 → deque=[]
  add 7 → deque=[7]
  front=7 >= right-k+1=5, valid
  record nums[7]=7 → result=[3,3,5,5,6,7]

Final: [3,3,5,5,6,7]
```

**Time:** O(n) — each element is added and removed from the deque at most once.
**Space:** O(k) — deque holds at most k indices.

**Key insight:** The deque is monotonically decreasing. When a new element arrives, all smaller elements behind it are useless (they'll never be the maximum while this element is in the window). So we pop them. This is the "monotonic deque" pattern.

---

### Category B: Variable Window — Longest/Maximum

---

#### LC 3 — Longest Substring Without Repeating Characters

**Difficulty:** Medium
**Companies:** Amazon, Google, Meta, Microsoft, Bloomberg, Adobe, Apple, Uber, Goldman Sachs, TikTok, Yandex (112 companies total)

**Problem:** Given a string `s`, find the length of the longest substring without repeating characters.

**Why sliding window:** You want the longest contiguous substring satisfying a condition (no duplicates). Classic variable window — expand right, shrink left when a duplicate appears.

**Hint:** Use a HashMap to track the last seen index of each character. When a duplicate is found, jump `left` directly to `lastSeen[c] + 1` instead of incrementally shrinking. This is an optimization over the basic template.

```java
class Solution {
    public int lengthOfLongestSubstring(String s) {
        // Maps character to its most recent index in the string
        Map<Character, Integer> lastSeen = new HashMap<>();
        
        int left = 0;
        int maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            
            // If character was seen before AND it's inside the current window
            // (lastSeen.get(c) >= left), move left past it
            if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
                // Jump left to one position after the duplicate
                left = lastSeen.get(c) + 1;
            }
            
            // Update last seen index for this character
            lastSeen.put(c, right);
            
            // Window [left, right] has no duplicates — update result
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
}
```

**Dry Run for s = "abcabcbb":**

```
left=0, maxLen=0, lastSeen={}

right=0, c='a': not in map → lastSeen={a:0}, maxLen=max(0,1)=1
right=1, c='b': not in map → lastSeen={a:0,b:1}, maxLen=max(1,2)=2
right=2, c='c': not in map → lastSeen={a:0,b:1,c:2}, maxLen=max(2,3)=3
right=3, c='a': lastSeen[a]=0 >= left=0 → left=0+1=1
  lastSeen={a:3,b:1,c:2}, maxLen=max(3,3-1+1)=max(3,3)=3
right=4, c='b': lastSeen[b]=1 >= left=1 → left=1+1=2
  lastSeen={a:3,b:4,c:2}, maxLen=max(3,4-2+1)=max(3,3)=3
right=5, c='c': lastSeen[c]=2 >= left=2 → left=2+1=3
  lastSeen={a:3,b:4,c:5}, maxLen=max(3,5-3+1)=max(3,3)=3
right=6, c='b': lastSeen[b]=4 >= left=3 → left=4+1=5
  lastSeen={a:3,b:6,c:5}, maxLen=max(3,6-5+1)=max(3,2)=3
right=7, c='b': lastSeen[b]=6 >= left=5 → left=6+1=7
  lastSeen={a:3,b:7,c:5}, maxLen=max(3,7-7+1)=max(3,1)=3

Answer: 3 (substring "abc")
```

**Why `lastSeen.get(c) >= left` matters:**

If a character was seen before but its last occurrence is to the LEFT of `left`, it's no longer in the current window. We shouldn't move `left` backward. The `>= left` check prevents this.

**Time:** O(n) | **Space:** O(min(n, charset_size))

---

#### LC 424 — Longest Repeating Character Replacement

**Difficulty:** Medium
**Companies:** Google, Amazon, Microsoft, Meta, Bloomberg

**Problem:** Given a string `s` and an integer `k`, you can replace at most `k` characters in the string. Return the length of the longest substring containing the same letter after performing at most `k` replacements.

**Why sliding window:** You want the longest window where `(window_size - max_frequency_in_window) <= k`. The left side is the number of characters you need to replace.

**Hint:** The key insight is that you only need to track the maximum frequency seen so far (`maxFreq`). You never need to decrease `maxFreq` when shrinking — if the window shrinks, the condition `windowSize - maxFreq <= k` is still satisfied (since windowSize decreases). This means `left` only moves when the window would be invalid at its current size.

```java
class Solution {
    public int characterReplacement(String s, int k) {
        int[] freq = new int[26];
        int left = 0;
        int maxFreq = 0; // Maximum frequency of any character in the current window
        int result = 0;
        
        for (int right = 0; right < s.length(); right++) {
            // Add character at right
            freq[s.charAt(right) - 'A']++;
            
            // Update maxFreq — only need to check the newly added character
            maxFreq = Math.max(maxFreq, freq[s.charAt(right) - 'A']);
            
            // Window size = right - left + 1
            // Characters to replace = windowSize - maxFreq
            // If this exceeds k, shrink the window by one
            int windowSize = right - left + 1;
            if (windowSize - maxFreq > k) {
                // Shrink: remove the leftmost character
                freq[s.charAt(left) - 'A']--;
                left++;
                // Note: we don't update maxFreq here — see explanation below
            }
            
            // Window is now valid (or same size as before)
            result = Math.max(result, right - left + 1);
        }
        
        return result;
    }
}
```

**Why don't we update `maxFreq` when shrinking?**

This is the subtle trick. `maxFreq` represents the best frequency we've ever seen. When we shrink the window, the window size decreases by 1. The condition `windowSize - maxFreq <= k` is now satisfied again (since windowSize went down). We don't need to recompute `maxFreq` because:

1. If the character we removed was the most frequent, `maxFreq` is now slightly too high. But that's fine — it means the window condition is actually stricter than necessary, so we'll only grow the window when we find an even better frequency. The result is still correct.
2. We never shrink by more than 1 per iteration, so the window size stays the same or grows. We're looking for the maximum window size, so we never need to shrink aggressively.

**Time:** O(n) | **Space:** O(1) — array of size 26

**Key insight:** The condition `windowSize - maxFreq <= k` is the heart of this problem. The window is valid when the number of "minority" characters (those that need replacing) is at most k.

---

#### LC 904 — Fruit Into Baskets

**Difficulty:** Medium
**Companies:** Google, Amazon, Microsoft

**Problem:** You have two baskets, each holding one type of fruit. Given an array `fruits` where `fruits[i]` is the type of fruit at tree i, return the maximum number of fruits you can collect starting from any tree, picking one fruit per tree, stopping when you'd need a third type.

**Why sliding window:** This is "longest subarray with at most 2 distinct values" in disguise. Variable window, expand right, shrink left when distinct count exceeds 2.

**Hint:** Recognize the disguise. "Two baskets" = "at most 2 distinct." Once you see this, it's a direct application of Template 2.

```java
class Solution {
    public int totalFruit(int[] fruits) {
        Map<Integer, Integer> basket = new HashMap<>(); // fruit type → count
        int left = 0;
        int result = 0;
        
        for (int right = 0; right < fruits.length; right++) {
            // Add fruit at right to basket
            basket.merge(fruits[right], 1, Integer::sum);
            
            // If more than 2 types, shrink from left
            while (basket.size() > 2) {
                basket.merge(fruits[left], -1, Integer::sum);
                if (basket.get(fruits[left]) == 0) {
                    basket.remove(fruits[left]);
                }
                left++;
            }
            
            // Window has at most 2 distinct fruits
            result = Math.max(result, right - left + 1);
        }
        
        return result;
    }
}
```

**Time:** O(n) | **Space:** O(1) — at most 3 entries in the map at any time

---

#### LC 159 — Longest Substring with At Most Two Distinct Characters

**Difficulty:** Medium (Premium)
**Companies:** Google, Amazon, Meta, Microsoft

**Problem:** Given a string `s`, return the length of the longest substring that contains at most two distinct characters.

**Why sliding window:** Direct application of Template 2 with condition `freq.size() > 2`.

```java
class Solution {
    public int lengthOfLongestSubstringTwoDistinct(String s) {
        Map<Character, Integer> freq = new HashMap<>();
        int left = 0;
        int result = 0;
        
        for (int right = 0; right < s.length(); right++) {
            freq.merge(s.charAt(right), 1, Integer::sum);
            
            while (freq.size() > 2) {
                char leftChar = s.charAt(left);
                freq.merge(leftChar, -1, Integer::sum);
                if (freq.get(leftChar) == 0) freq.remove(leftChar);
                left++;
            }
            
            result = Math.max(result, right - left + 1);
        }
        
        return result;
    }
}
```

**Time:** O(n) | **Space:** O(1)

**Note:** LC 904 (Fruit Into Baskets) and LC 159 are the same problem with different wording. Recognizing this pattern saves time in interviews.

---

#### LC 340 — Longest Substring with At Most K Distinct Characters

**Difficulty:** Medium (Premium)
**Companies:** Google, Amazon, Meta, Microsoft

**Problem:** Given a string `s` and an integer `k`, return the length of the longest substring that contains at most `k` distinct characters.

**Why sliding window:** Generalization of LC 159. Same template, just replace `2` with `k`.

```java
class Solution {
    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        if (k == 0) return 0;
        
        Map<Character, Integer> freq = new HashMap<>();
        int left = 0;
        int result = 0;
        
        for (int right = 0; right < s.length(); right++) {
            freq.merge(s.charAt(right), 1, Integer::sum);
            
            while (freq.size() > k) {
                char leftChar = s.charAt(left);
                freq.merge(leftChar, -1, Integer::sum);
                if (freq.get(leftChar) == 0) freq.remove(leftChar);
                left++;
            }
            
            result = Math.max(result, right - left + 1);
        }
        
        return result;
    }
}
```

**Time:** O(n) | **Space:** O(k)

**The progression:** LC 3 (0 repeats) → LC 159 (2 distinct) → LC 340 (k distinct) → LC 904 (2 distinct, disguised). These are all the same template.

---

#### LC 1004 — Max Consecutive Ones III

**Difficulty:** Medium
**Companies:** Google, Amazon, Microsoft, Meta

**Problem:** Given a binary array `nums` and an integer `k`, return the maximum number of consecutive 1s in the array if you can flip at most `k` 0s.

**Why sliding window:** You want the longest window where the number of 0s is at most k. This is exactly Template 2 with condition `zerosInWindow > k`.

**Hint:** This is the same structure as LC 424. The condition is `(windowSize - countOfOnes) <= k`, which is equivalent to `countOfZeros <= k`.

```java
class Solution {
    public int longestOnes(int[] nums, int k) {
        int left = 0;
        int zerosInWindow = 0;
        int result = 0;
        
        for (int right = 0; right < nums.length; right++) {
            // Expand: if new element is 0, increment zero count
            if (nums[right] == 0) zerosInWindow++;
            
            // Shrink: if too many zeros, move left until valid
            while (zerosInWindow > k) {
                if (nums[left] == 0) zerosInWindow--;
                left++;
            }
            
            // Window has at most k zeros
            result = Math.max(result, right - left + 1);
        }
        
        return result;
    }
}
```

**Time:** O(n) | **Space:** O(1)

**Key insight:** "Flip at most k zeros" = "window with at most k zeros." The window condition is simple enough that you don't need a HashMap — just a counter.

---

### Category C: Variable Window — Shortest/Minimum

---

#### LC 209 — Minimum Size Subarray Sum

**Difficulty:** Medium
**Companies:** Google, Amazon, Microsoft, Meta, Bloomberg, Apple

**Problem:** Given an array of positive integers `nums` and a positive integer `target`, return the minimal length of a subarray whose sum is greater than or equal to `target`. Return 0 if no such subarray exists.

**Why sliding window:** Positive integers only (no negatives), so the sum is monotonically increasing as the window expands. Classic Template 3 — expand until valid, then shrink while valid.

**Hint:** The positive-integers constraint is critical. It guarantees that shrinking the window always decreases the sum, so the sliding window property holds.

```java
class Solution {
    public int minSubArrayLen(int target, int[] nums) {
        int left = 0;
        int windowSum = 0;
        int result = Integer.MAX_VALUE;
        
        for (int right = 0; right < nums.length; right++) {
            // Expand: add nums[right]
            windowSum += nums[right];
            
            // Shrink: while window sum meets target, try to minimize
            while (windowSum >= target) {
                // This window is valid — record its length
                result = Math.min(result, right - left + 1);
                
                // Try to shrink further
                windowSum -= nums[left];
                left++;
            }
        }
        
        return result == Integer.MAX_VALUE ? 0 : result;
    }
}
```

**Time:** O(n) | **Space:** O(1)

**Key insight:** Update result INSIDE the while loop. Every iteration of the while loop represents a valid window, and you want the smallest one.

---

#### LC 76 — Minimum Window Substring

**Difficulty:** Hard
**Companies:** Google, Amazon, Meta, Microsoft, Apple, Uber, Bloomberg, Adobe, Oracle, LinkedIn

**Problem:** Given strings `s` and `t`, return the minimum window substring of `s` such that every character in `t` (including duplicates) is included in the window. Return empty string if no such window exists.

**Why sliding window:** You need the shortest window in `s` that contains all characters of `t`. Expand until all characters are covered, then shrink to minimize.

**Hint:** This is Template 3 combined with Template 4's frequency map approach. The `formed` counter tells you when the window is valid (contains all required characters with correct frequencies).

```java
class Solution {
    public String minWindow(String s, String t) {
        if (s.length() < t.length()) return "";
        
        // Frequency map for t
        Map<Character, Integer> tFreq = new HashMap<>();
        for (char c : t.toCharArray()) {
            tFreq.merge(c, 1, Integer::sum);
        }
        
        // How many distinct characters in t need to be satisfied
        int required = tFreq.size();
        
        // How many distinct characters in current window satisfy t's requirement
        int formed = 0;
        
        // Frequency map for current window (only track chars in t)
        Map<Character, Integer> windowFreq = new HashMap<>();
        
        int left = 0;
        int minLen = Integer.MAX_VALUE;
        int minLeft = 0; // Start index of the minimum window
        
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            
            // Only track characters that appear in t
            if (tFreq.containsKey(c)) {
                windowFreq.merge(c, 1, Integer::sum);
                
                // Check if this character's count now satisfies t's requirement
                if (windowFreq.get(c).equals(tFreq.get(c))) {
                    formed++;
                }
            }
            
            // Shrink from left while window is valid (contains all of t)
            while (formed == required) {
                // This window is valid — record if it's the smallest
                int windowLen = right - left + 1;
                if (windowLen < minLen) {
                    minLen = windowLen;
                    minLeft = left;
                }
                
                // Try to shrink: remove character at left
                char leftChar = s.charAt(left);
                if (tFreq.containsKey(leftChar)) {
                    // Check if removing this breaks the requirement
                    if (windowFreq.get(leftChar).equals(tFreq.get(leftChar))) {
                        formed--;
                    }
                    windowFreq.merge(leftChar, -1, Integer::sum);
                }
                
                left++;
            }
        }
        
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minLeft, minLeft + minLen);
    }
}
```

**Dry Run for s = "ADOBECODEBANC", t = "ABC":**

```
tFreq = {A:1, B:1, C:1}, required = 3

right=0, c='A': windowFreq={A:1}, A matches → formed=1
right=1, c='D': not in tFreq, skip
right=2, c='O': not in tFreq, skip
right=3, c='B': windowFreq={A:1,B:1}, B matches → formed=2
right=4, c='E': not in tFreq, skip
right=5, c='C': windowFreq={A:1,B:1,C:1}, C matches → formed=3

formed==required=3, enter shrink loop:
  window=[0,5]="ADOBEC", len=6, minLen=6, minLeft=0
  remove s[0]='A': A matches (count 1==1) → formed=2, windowFreq={A:0,B:1,C:1}, left=1
  formed != required, exit shrink loop

right=6, c='O': skip
right=7, c='D': skip
right=8, c='E': skip
right=9, c='B': windowFreq={A:0,B:2,C:1}, B count=2 != tFreq[B]=1, formed stays 2
right=10, c='A': windowFreq={A:1,B:2,C:1}, A matches → formed=3

formed==required=3, enter shrink loop:
  window=[1,10]="DOBECODEBA", len=10, 10 > minLen=6, no update
  remove s[1]='D': not in tFreq, left=2
  window=[2,10]="OBECODEBA", len=9, 9 > 6, no update
  remove s[2]='O': not in tFreq, left=3
  window=[3,10]="BECODEBA", len=8, 8 > 6, no update
  remove s[3]='B': B count=2 != tFreq[B]=1, formed stays 3, windowFreq={A:1,B:1,C:1}, left=4
  window=[4,10]="ECODEBA", len=7, 7 > 6, no update
  remove s[4]='E': not in tFreq, left=5
  window=[5,10]="CODEBA", len=6, 6 == minLen=6, no update (same length)
  remove s[5]='C': C count=1==1 → formed=2, windowFreq={A:1,B:1,C:0}, left=6
  formed != required, exit shrink loop

right=11, c='N': skip
right=12, c='C': windowFreq={A:1,B:1,C:1}, C matches → formed=3

formed==required=3, enter shrink loop:
  window=[6,12]="ODEBANC", len=7, 7 > 6, no update
  remove s[6]='O': not in tFreq, left=7
  window=[7,12]="DEBANC", len=6, 6 == minLen=6, no update
  remove s[7]='D': not in tFreq, left=8
  window=[8,12]="EBANC", len=5, 5 < 6 → minLen=5, minLeft=8
  remove s[8]='E': not in tFreq, left=9
  window=[9,12]="BANC", len=4, 4 < 5 → minLen=4, minLeft=9
  remove s[9]='B': B count=1==1 → formed=2, windowFreq={A:1,B:0,C:1}, left=10
  formed != required, exit shrink loop

End of string.
Answer: s.substring(9, 9+4) = "BANC"
```

**Time:** O(|s| + |t|) | **Space:** O(|t|)

**Key insight:** Only track characters that appear in `t`. The `formed` counter avoids iterating over the entire map to check validity. Update result INSIDE the shrink loop.

---

#### LC 862 — Shortest Subarray with Sum at Least K

**Difficulty:** Hard
**Companies:** (not in provided list)

**Problem:** Given an integer array `nums` and an integer `k`, return the length of the shortest non-empty subarray with a sum of at least `k`. Return -1 if no such subarray exists.

**Why this is NOT pure sliding window:** The array can contain negative numbers. With negatives, the sum is not monotonically increasing as the window expands. Shrinking the window might not decrease the sum (if you remove a negative number, the sum actually increases). The sliding window invariant breaks.

**The correct approach:** Prefix sums + monotonic deque. Compute prefix sums, then use a deque to find the shortest subarray with sum >= k in O(n).

```java
class Solution {
    public int shortestSubarray(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        
        // Build prefix sum array
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }
        
        // Monotonic deque stores indices of prefix array
        // Maintains increasing order of prefix values
        Deque<Integer> deque = new ArrayDeque<>();
        int result = Integer.MAX_VALUE;
        
        for (int i = 0; i <= n; i++) {
            // While the current prefix minus the front of deque >= k,
            // we found a valid subarray — record and remove from front
            while (!deque.isEmpty() && prefix[i] - prefix[deque.peekFirst()] >= k) {
                result = Math.min(result, i - deque.pollFirst());
            }
            
            // Maintain increasing order: remove indices from back
            // where prefix value >= current prefix value
            // (they can never give a shorter valid subarray)
            while (!deque.isEmpty() && prefix[deque.peekLast()] >= prefix[i]) {
                deque.pollLast();
            }
            
            deque.addLast(i);
        }
        
        return result == Integer.MAX_VALUE ? -1 : result;
    }
}
```

**Time:** O(n) | **Space:** O(n)

**Key takeaway:** When you see "shortest subarray with sum >= k" and the array has negative numbers, don't reach for sliding window. Reach for prefix sums + monotonic deque. This is a common trap in Hard problems.

---

### Category D: Counting / Exactly-K Problems

---

#### LC 930 — Binary Subarrays With Sum

**Difficulty:** Medium
**Companies:** Google, Amazon, Meta

**Problem:** Given a binary array `nums` and an integer `goal`, return the number of non-empty subarrays with a sum equal to `goal`.

**Why sliding window:** Count subarrays with EXACTLY `goal` sum. Use Template 5: `atMost(goal) - atMost(goal - 1)`.

**Hint:** Direct counting of "exactly goal" is hard. The at-most trick converts it to two easy problems.

```java
class Solution {
    public int numSubarraysWithSum(int[] nums, int goal) {
        return atMost(nums, goal) - atMost(nums, goal - 1);
    }
    
    private int atMost(int[] nums, int goal) {
        if (goal < 0) return 0; // Edge case: goal-1 when goal=0
        
        int left = 0;
        int windowSum = 0;
        int count = 0;
        
        for (int right = 0; right < nums.length; right++) {
            windowSum += nums[right];
            
            // Shrink while sum exceeds goal
            while (windowSum > goal) {
                windowSum -= nums[left];
                left++;
            }
            
            // All subarrays ending at right with start from left to right are valid
            count += right - left + 1;
        }
        
        return count;
    }
}
```

**Time:** O(n) | **Space:** O(1)

**Why `if (goal < 0) return 0`?** When `goal = 0`, we call `atMost(nums, -1)`. A sum can't be at most -1 for a binary array (all elements are 0 or 1, so sums are non-negative). Return 0 immediately.

---

#### LC 992 — Subarrays with K Different Integers

**Difficulty:** Hard
**Companies:** Google, Amazon, Microsoft, Meta

**Problem:** Given an integer array `nums` and an integer `k`, return the number of good subarrays of `nums`. A good subarray has exactly `k` different integers.

**Why sliding window:** Count subarrays with EXACTLY k distinct integers. Template 5: `atMost(k) - atMost(k-1)`.

```java
class Solution {
    public int subarraysWithKDistinct(int[] nums, int k) {
        return atMostK(nums, k) - atMostK(nums, k - 1);
    }
    
    private int atMostK(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        int left = 0;
        int count = 0;
        
        for (int right = 0; right < nums.length; right++) {
            // Expand: add nums[right]
            freq.merge(nums[right], 1, Integer::sum);
            
            // Shrink: while more than k distinct elements
            while (freq.size() > k) {
                freq.merge(nums[left], -1, Integer::sum);
                if (freq.get(nums[left]) == 0) freq.remove(nums[left]);
                left++;
            }
            
            // All subarrays ending at right with start from left to right
            // have at most k distinct integers
            count += right - left + 1;
        }
        
        return count;
    }
}
```

**Dry Run for nums = [1,2,1,2,3], k = 2:**

```
atMostK(nums, 2):

right=0: freq={1:1}, size=1 <= 2, count += 1 → count=1
  (subarrays: [1])

right=1: freq={1:1,2:1}, size=2 <= 2, count += 2 → count=3
  (subarrays ending at 1: [2], [1,2])

right=2: freq={1:2,2:1}, size=2 <= 2, count += 3 → count=6
  (subarrays ending at 2: [1], [2,1], [1,2,1])

right=3: freq={1:2,2:2}, size=2 <= 2, count += 4 → count=10
  (subarrays ending at 3: [2], [1,2], [2,1,2], [1,2,1,2])

right=4: freq={1:2,2:2,3:1}, size=3 > 2
  shrink: remove nums[0]=1 → freq={1:1,2:2,3:1}, left=1, size=3 > 2
  shrink: remove nums[1]=2 → freq={1:1,2:1,3:1}, left=2, size=3 > 2
  shrink: remove nums[2]=1 → freq={2:1,3:1}, left=3, size=2 <= 2
  count += (4-3+1) = 2 → count=12
  (subarrays ending at 4: [3], [2,3])

atMostK(nums, 2) = 12

atMostK(nums, 1):

right=0: freq={1:1}, size=1 <= 1, count += 1 → count=1
right=1: freq={1:1,2:1}, size=2 > 1
  shrink: remove nums[0]=1 → freq={2:1}, left=1, size=1 <= 1
  count += 1 → count=2
right=2: freq={2:1,1:1}, size=2 > 1
  shrink: remove nums[1]=2 → freq={1:1}, left=2, size=1 <= 1
  count += 1 → count=3
right=3: freq={1:1,2:1}, size=2 > 1
  shrink: remove nums[2]=1 → freq={2:1}, left=3, size=1 <= 1
  count += 1 → count=4
right=4: freq={2:1,3:1}, size=2 > 1
  shrink: remove nums[3]=2 → freq={3:1}, left=4, size=1 <= 1
  count += 1 → count=5

atMostK(nums, 1) = 5

Answer: 12 - 5 = 7

Verification: subarrays with exactly 2 distinct:
[1,2], [2,1], [1,2,1], [2,1,2], [1,2,1,2], [1,2], [2,3] → 7 ✓
```

**Time:** O(n) | **Space:** O(k)

---

#### LC 1248 — Count Number of Nice Subarrays

**Difficulty:** Medium
**Companies:** (not in provided list)

**Problem:** Given an array `nums` of integers and an integer `k`, return the number of nice subarrays. A nice subarray has exactly `k` odd numbers.

**Why sliding window:** Count subarrays with exactly k odd numbers. Template 5: `atMost(k) - atMost(k-1)`.

**Hint:** Treat odd numbers as 1 and even numbers as 0. Then "exactly k odd numbers" becomes "subarray sum equals k." Same as LC 930.

```java
class Solution {
    public int numberOfSubarrays(int[] nums, int k) {
        return atMost(nums, k) - atMost(nums, k - 1);
    }
    
    private int atMost(int[] nums, int k) {
        int left = 0;
        int oddCount = 0;
        int count = 0;
        
        for (int right = 0; right < nums.length; right++) {
            // Odd numbers contribute 1, even numbers contribute 0
            if (nums[right] % 2 == 1) oddCount++;
            
            // Shrink while too many odd numbers
            while (oddCount > k) {
                if (nums[left] % 2 == 1) oddCount--;
                left++;
            }
            
            count += right - left + 1;
        }
        
        return count;
    }
}
```

**Time:** O(n) | **Space:** O(1)

---

### Category E: Advanced / Hybrid

---

#### LC 480 — Sliding Window Median

**Difficulty:** Hard
**Companies:** Google, Amazon, Microsoft, Bloomberg

**Problem:** Given an array `nums` and an integer `k`, return the median of each window of size k as it slides through the array.

**Why this is different:** Finding the median of a window requires knowing the middle element(s). A simple sum or count won't work. You need a data structure that supports efficient insertion, deletion, and median query.

**The trick:** Maintain two heaps — a max-heap for the lower half and a min-heap for the upper half. The median is either the top of the max-heap (odd k) or the average of both tops (even k). When the window slides, add the new element and remove the outgoing element from the appropriate heap.

**Hint:** Java's `PriorityQueue` doesn't support O(log n) removal by value. Use a lazy deletion approach with a `HashMap` to track elements that should be removed.

```java
class Solution {
    public double[] medianSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        double[] result = new double[n - k + 1];
        
        // maxHeap: lower half (max at top)
        // minHeap: upper half (min at top)
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        
        // Lazy deletion: track elements to be removed
        Map<Integer, Integer> toRemove = new HashMap<>();
        
        // Helper: balance heaps so maxHeap.size() == minHeap.size() or maxHeap.size() == minHeap.size() + 1
        // maxHeap holds the smaller half, minHeap holds the larger half
        
        // Initialize with first k elements
        for (int i = 0; i < k; i++) {
            maxHeap.offer(nums[i]);
        }
        // Move top k/2 elements to minHeap
        for (int i = 0; i < k / 2; i++) {
            minHeap.offer(maxHeap.poll());
        }
        
        // Record first median
        result[0] = getMedian(maxHeap, minHeap, k);
        
        for (int i = k; i < n; i++) {
            int incoming = nums[i];
            int outgoing = nums[i - k];
            
            // Add incoming element
            if (incoming <= maxHeap.peek()) {
                maxHeap.offer(incoming);
            } else {
                minHeap.offer(incoming);
            }
            
            // Mark outgoing element for lazy removal
            toRemove.merge(outgoing, 1, Integer::sum);
            
            // Rebalance heaps
            // After adding, one heap might be too large
            if (incoming <= maxHeap.peek()) {
                // Added to maxHeap — if maxHeap is too large, move top to minHeap
                if (maxHeap.size() > minHeap.size() + 1) {
                    minHeap.offer(maxHeap.poll());
                }
            } else {
                // Added to minHeap — if minHeap is too large, move top to maxHeap
                if (minHeap.size() > maxHeap.size()) {
                    maxHeap.offer(minHeap.poll());
                }
            }
            
            // Lazy deletion: remove invalid tops
            while (!maxHeap.isEmpty() && toRemove.getOrDefault(maxHeap.peek(), 0) > 0) {
                toRemove.merge(maxHeap.poll(), -1, Integer::sum);
            }
            while (!minHeap.isEmpty() && toRemove.getOrDefault(minHeap.peek(), 0) > 0) {
                toRemove.merge(minHeap.poll(), -1, Integer::sum);
            }
            
            // Rebalance again after lazy deletion
            while (maxHeap.size() > minHeap.size() + 1) {
                minHeap.offer(maxHeap.poll());
            }
            while (minHeap.size() > maxHeap.size()) {
                maxHeap.offer(minHeap.poll());
            }
            
            result[i - k + 1] = getMedian(maxHeap, minHeap, k);
        }
        
        return result;
    }
    
    private double getMedian(PriorityQueue<Integer> maxHeap, PriorityQueue<Integer> minHeap, int k) {
        if (k % 2 == 1) {
            return maxHeap.peek();
        } else {
            return ((double) maxHeap.peek() + minHeap.peek()) / 2.0;
        }
    }
}
```

**Time:** O(n log k) | **Space:** O(k)

**Key insight:** The two-heap approach maintains the median in O(1) query time. Insertion and deletion are O(log k). The lazy deletion avoids the O(k) cost of removing an arbitrary element from a heap.

---

#### LC 30 — Substring with Concatenation of All Words

**Difficulty:** Hard
**Companies:** Google, Amazon, Microsoft

**Problem:** Given a string `s` and an array of strings `words` (all same length), return all starting indices of substrings in `s` that are a concatenation of each word in `words` exactly once.

**Why this is different:** The window size is fixed (`words.length * wordLen`), but you're matching whole words, not individual characters. You need to slide the window word-by-word, not character-by-character.

**Hint:** Run `wordLen` separate sliding window passes, each starting at offset 0, 1, ..., wordLen-1. Within each pass, slide the window by one word at a time.

```java
class Solution {
    public List<Integer> findSubstring(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        
        if (s == null || s.length() == 0 || words == null || words.length == 0) {
            return result;
        }
        
        int wordLen = words[0].length();
        int wordCount = words.length;
        int windowLen = wordLen * wordCount;
        
        if (s.length() < windowLen) return result;
        
        // Frequency map for words
        Map<String, Integer> wordFreq = new HashMap<>();
        for (String w : words) wordFreq.merge(w, 1, Integer::sum);
        
        // Run wordLen separate passes
        for (int offset = 0; offset < wordLen; offset++) {
            Map<String, Integer> windowFreq = new HashMap<>();
            int left = offset;
            int formed = 0; // Number of words correctly placed in window
            
            for (int right = offset; right + wordLen <= s.length(); right += wordLen) {
                String word = s.substring(right, right + wordLen);
                
                if (wordFreq.containsKey(word)) {
                    windowFreq.merge(word, 1, Integer::sum);
                    
                    // If this word's count matches required, increment formed
                    if (windowFreq.get(word).equals(wordFreq.get(word))) {
                        formed++;
                    }
                    
                    // Shrink window if it has too many words
                    while (windowFreq.get(word) > wordFreq.get(word)) {
                        String leftWord = s.substring(left, left + wordLen);
                        if (windowFreq.get(leftWord).equals(wordFreq.get(leftWord))) {
                            formed--;
                        }
                        windowFreq.merge(leftWord, -1, Integer::sum);
                        left += wordLen;
                    }
                    
                    // Valid window found
                    if (formed == wordFreq.size()) {
                        result.add(left);
                        
                        // Slide window by one word
                        String leftWord = s.substring(left, left + wordLen);
                        if (windowFreq.get(leftWord).equals(wordFreq.get(leftWord))) {
                            formed--;
                        }
                        windowFreq.merge(leftWord, -1, Integer::sum);
                        left += wordLen;
                    }
                } else {
                    // Invalid word — reset window
                    windowFreq.clear();
                    formed = 0;
                    left = right + wordLen;
                }
            }
        }
        
        return result;
    }
}
```

**Time:** O(n * wordLen) | **Space:** O(wordCount)

**Key insight:** The `wordLen` separate passes ensure you don't miss any valid starting position. Within each pass, the window slides word-by-word, making it O(n/wordLen) per pass, O(n) total per pass, O(n * wordLen) overall.

---

#### LC 395 — Longest Substring with At Least K Repeating Characters

**Difficulty:** Medium
**Companies:** (not in provided list)

**Problem:** Given a string `s` and an integer `k`, return the length of the longest substring such that every character in the substring appears at least `k` times.

**Why this is tricky for sliding window:** The condition "every character appears at least k times" is not monotonic. Adding a character might make the window valid or invalid in non-obvious ways. Shrinking might not help.

**The correct approach:** Divide and conquer, OR sliding window with a fixed number of unique characters.

**Sliding window approach:** Iterate over the number of unique characters in the window (1 to 26). For each target unique count `u`, find the longest window with exactly `u` unique characters where all have frequency >= k.

```java
class Solution {
    public int longestSubstring(String s, int k) {
        int result = 0;
        
        // Try all possible counts of unique characters (1 to 26)
        for (int uniqueTarget = 1; uniqueTarget <= 26; uniqueTarget++) {
            int[] freq = new int[26];
            int left = 0;
            int uniqueCount = 0;    // Distinct characters in window
            int validCount = 0;     // Characters with frequency >= k
            
            for (int right = 0; right < s.length(); right++) {
                int c = s.charAt(right) - 'a';
                
                if (freq[c] == 0) uniqueCount++;
                freq[c]++;
                if (freq[c] == k) validCount++;
                
                // Shrink if too many unique characters
                while (uniqueCount > uniqueTarget) {
                    int lc = s.charAt(left) - 'a';
                    if (freq[lc] == k) validCount--;
                    freq[lc]--;
                    if (freq[lc] == 0) uniqueCount--;
                    left++;
                }
                
                // Window has exactly uniqueTarget unique chars
                // If all are valid (freq >= k), update result
                if (uniqueCount == uniqueTarget && uniqueCount == validCount) {
                    result = Math.max(result, right - left + 1);
                }
            }
        }
        
        return result;
    }
}
```

**Time:** O(26 * n) = O(n) | **Space:** O(1)

**Key insight:** By fixing the number of unique characters, you make the window condition monotonic. The outer loop over `uniqueTarget` (1 to 26) is the trick that makes sliding window applicable here.

---

## 7. Common Mistakes and Edge Cases

### Common Mistakes

| Mistake | Why it happens | How to fix |
|---------|---------------|------------|
| Using `if` instead of `while` for shrinking | Assumes one shrink step is always enough | Always use `while` — the window might need multiple shrinks |
| Off-by-one in window size | Forgetting that `right - left + 1` is the size, not `right - left` | Remember: inclusive on both ends, so add 1 |
| Updating result before shrinking (in shortest window) | Confusing Template 2 and Template 3 | For shortest: update INSIDE shrink loop. For longest: update AFTER shrink loop |
| Not resetting state when window resets | Forgetting to clear frequency maps or counters | When left jumps past right (invalid word in LC 30), clear all state |
| Applying sliding window with negative numbers | Assuming sum is monotonic | Check for negatives. Use prefix sum + deque for LC 862 |
| Not handling `goal - 1 < 0` in Template 5 | `atMost(-1)` should return 0 | Add `if (k < 0) return 0` at the start of `atMost` |
| Comparing Integer objects with `==` instead of `.equals()` | Auto-unboxing pitfall in Java | Use `.equals()` for Integer comparisons, or unbox explicitly |
| Forgetting to update `maxFreq` correctly in LC 424 | Thinking you need to recompute it when shrinking | You don't — `maxFreq` only needs to increase, never decrease |
| Moving `left` backward | Thinking you need to "undo" a jump | Both pointers only move right. Never move left backward |
| Incorrect frequency map cleanup | Leaving zero-count entries in the map | After decrementing, check `if (freq.get(key) == 0) freq.remove(key)` |

### Edge Cases to Always Test

**Empty input:**
```java
// Always check at the start
if (s == null || s.length() == 0) return 0;
if (nums == null || nums.length == 0) return 0;
```

**Single element:**
- Window of size 1 should work correctly
- `right - left + 1 = 1` when `right == left`

**All elements the same:**
- `s = "aaaa"`, `k = 2` — the entire string might be the answer
- Frequency map has one entry with count = n

**Window size equals array length:**
- `k = n` — only one window exists
- The loop body executes once

**No valid window exists:**
- Return 0, -1, or empty string as specified
- Always initialize result to `Integer.MAX_VALUE` (for min) or 0 (for max) and handle the "no valid window" case

**k = 0:**
- "At most 0 distinct" means only empty subarrays — return 0
- "Exactly 0" — depends on problem context

**All characters the same in anagram problems:**
- `s = "aaaa"`, `p = "aa"` — multiple valid windows

---

## 8. Pattern Comparison

| Pattern | When to use | Key difference from Sliding Window |
|---------|-------------|-----------------------------------|
| **Sliding Window** | Contiguous subarray/substring, condition maintained incrementally, all elements non-negative (for sum problems) | The baseline pattern |
| **Two Pointers** | Sorted array, pair sum problems, palindrome checks, merging sorted arrays | Two pointers don't always define a contiguous window; they can move independently or toward each other. Sliding window is a special case of two pointers where both move right |
| **Prefix Sum** | Sum of any subarray (not necessarily contiguous window), problems with negative numbers, 2D sum queries | Prefix sum answers arbitrary range queries in O(1) after O(n) preprocessing. Sliding window only works for contiguous windows with monotonic conditions |
| **Kadane's Algorithm** | Maximum subarray sum (can include negatives), no fixed window size | Kadane's handles negatives by deciding at each element whether to extend the current subarray or start fresh. Sliding window can't handle negatives in sum problems |
| **Binary Search on Answer** | "Find minimum window size such that..." when the answer space is monotonic | Binary search on the answer + O(n) validation. Use when the window size itself is the answer and you can check validity in O(n). Sliding window directly finds the answer in O(n) without binary search |

### Sliding Window vs Two Pointers — The Subtle Distinction

Two pointers is the broader category. Sliding window is a specific two-pointer technique where:
1. Both pointers move in the same direction (left to right)
2. The region between them is always a contiguous window
3. The window expands and contracts based on a condition

Classic two-pointer problems (like finding a pair that sums to a target in a sorted array) move pointers toward each other. That's not sliding window.

### Sliding Window vs Prefix Sum — When to Choose

Use sliding window when:
- The condition is about the current window's state (sum, distinct count, frequency)
- The condition is monotonic (adding elements makes it "more valid" or "less valid" in a predictable way)
- You want O(1) space

Use prefix sum when:
- You need to answer multiple range queries
- The array has negative numbers (breaks sliding window's monotonic property)
- You need the sum of non-contiguous ranges

---

## 9. Quick Reference Cheat Sheet

### Decision Flowchart

```
Problem involves contiguous subarray/substring?
├── NO  → Not sliding window (try DP, backtracking, two pointers on sorted)
└── YES → Is window size fixed?
          ├── YES → Template 1 (Fixed Window)
          │         Add new element, remove element at (right - k)
          └── NO  → What are you optimizing?
                    ├── LONGEST/MAXIMUM → Template 2
                    │   Expand right, shrink left WHILE invalid
                    │   Update result AFTER shrink loop
                    ├── SHORTEST/MINIMUM → Template 3
                    │   Expand right, shrink left WHILE valid
                    │   Update result INSIDE shrink loop
                    ├── COUNT with EXACTLY K → Template 5
                    │   atMost(k) - atMost(k-1)
                    └── ANAGRAM/PERMUTATION → Template 4
                        Frequency map + formed counter
```

### Template Selector

| Problem says... | Use template |
|----------------|-------------|
| "subarray of size k" | Template 1 |
| "maximum/minimum average of size k" | Template 1 |
| "permutation/anagram in string" | Template 4 (fixed window) |
| "longest substring without..." | Template 2 |
| "longest substring with at most k..." | Template 2 |
| "maximum consecutive ones with k flips" | Template 2 |
| "minimum size subarray with sum >= target" | Template 3 |
| "minimum window substring" | Template 3 + Template 4 |
| "count subarrays with exactly k..." | Template 5 |
| "sliding window maximum/minimum" | Template 1 + Monotonic Deque |
| "sliding window median" | Template 1 + Two Heaps |

### Time/Space Complexity Summary

| Problem | Time | Space | Template |
|---------|------|-------|----------|
| LC 643 Max Average Subarray | O(n) | O(1) | T1 |
| LC 567 Permutation in String | O(n) | O(1) | T4 |
| LC 438 Find All Anagrams | O(n) | O(1) | T4 |
| LC 239 Sliding Window Maximum | O(n) | O(k) | T1 + Deque |
| LC 3 Longest Without Repeating | O(n) | O(n) | T2 |
| LC 424 Longest Repeating Replacement | O(n) | O(1) | T2 |
| LC 904 Fruit Into Baskets | O(n) | O(1) | T2 |
| LC 1004 Max Consecutive Ones III | O(n) | O(1) | T2 |
| LC 209 Min Size Subarray Sum | O(n) | O(1) | T3 |
| LC 76 Minimum Window Substring | O(n) | O(t) | T3 + T4 |
| LC 930 Binary Subarrays With Sum | O(n) | O(1) | T5 |
| LC 992 Subarrays K Different | O(n) | O(k) | T5 |
| LC 1248 Count Nice Subarrays | O(n) | O(1) | T5 |
| LC 480 Sliding Window Median | O(n log k) | O(k) | T1 + Heaps |
| LC 30 Substring Concatenation | O(n * wLen) | O(words) | T4 variant |

### Key Invariants to Remember

```
Fixed window:    right - left + 1 == k  (always)
Longest window:  window is VALID after the while loop
Shortest window: window is INVALID after the while loop
Counting:        count += right - left + 1  (all subarrays ending at right)
```

---

## 10. Practice Roadmap

Work through problems in this order. The goal is pattern recognition, not memorization. After each problem, ask: "What was the key insight? What template did I use? What would break if I used a different template?"

### Week 1 — Easy (Build the Foundation)

Target: 15 minutes per problem. If you exceed 20 minutes, look at the hint, not the solution.

| Day | Problem | Focus |
|-----|---------|-------|
| 1 | LC 643 — Maximum Average Subarray I | Fixed window basics |
| 2 | LC 1456 — Maximum Number of Vowels | Fixed window with character tracking |
| 3 | LC 1004 — Max Consecutive Ones III | Variable window, simple condition |

After Week 1, you should be able to: identify fixed vs variable window, write Template 1 and Template 2 from memory.

### Week 2 — Medium Core (The Essential Six)

Target: 25 minutes per problem. These are the most commonly asked problems.

| Day | Problem | Focus |
|-----|---------|-------|
| 4 | LC 3 — Longest Without Repeating | Variable window + HashMap |
| 5 | LC 567 — Permutation in String | Fixed window + frequency map |
| 6 | LC 438 — Find All Anagrams | Same as 567, collect all results |
| 7 | LC 209 — Minimum Size Subarray Sum | Shortest window template |
| 8 | LC 424 — Longest Repeating Replacement | Variable window, non-obvious condition |
| 9 | LC 904 — Fruit Into Baskets | Recognize the disguise |

After Week 2, you should be able to: write all five templates from memory, recognize the "at most K distinct" pattern, distinguish longest vs shortest templates.

### Week 3 — Medium Advanced (Deepen the Pattern)

Target: 30 minutes per problem.

| Day | Problem | Focus |
|-----|---------|-------|
| 10 | LC 930 — Binary Subarrays With Sum | Exactly K = At Most K - At Most K-1 |
| 11 | LC 1248 — Count Nice Subarrays | Same trick, different disguise |
| 12 | LC 992 — Subarrays K Different | Exactly K with distinct count |
| 13 | LC 340 — Longest K Distinct (Premium) | Generalize LC 159 |
| 14 | LC 395 — At Least K Repeating | Recognize when pure sliding window fails |

After Week 3, you should be able to: apply Template 5 confidently, recognize when a problem needs a non-standard approach.

### Week 4 — Hard (Interview Differentiators)

Target: 40 minutes per problem. These separate good candidates from great ones.

| Day | Problem | Focus |
|-----|---------|-------|
| 15 | LC 76 — Minimum Window Substring | Hard version of Template 3 + 4 |
| 16 | LC 239 — Sliding Window Maximum | Monotonic deque |
| 17 | LC 480 — Sliding Window Median | Two heaps + lazy deletion |
| 18 | LC 30 — Substring Concatenation | Word-level sliding window |

After Week 4, you should be able to: handle any sliding window variant in an interview, explain the monotonic deque and two-heap approaches clearly.

### Total: 18 problems over 4 weeks

This is not an exhaustive list. It's a curated path. After completing this roadmap, you'll have seen every major variant of the sliding window pattern. New problems will feel like variations on themes you already know.

---

*Document 1 of 20 — DSA Prep Series*
