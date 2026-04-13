package com.dsa.problems;

import java.util.HashSet;
import java.util.Set;

/**
 * Sliding Window pattern solutions.
 * Reference: ../01-sliding-window.md
 */
public class SlidingWindow {

    /**
     * Fixed-size sliding window: max sum of subarray of size k.
     * Template 1 from Topic 1.
     */
    public static int maxSumSubarray(int[] arr, int k) {
        int windowSum = 0, maxSum = Integer.MIN_VALUE;

        for (int i = 0; i < arr.length; i++) {
            windowSum += arr[i];

            if (i >= k - 1) {
                maxSum = Math.max(maxSum, windowSum);
                windowSum -= arr[i - k + 1];
            }
        }

        return maxSum;
    }

    /**
     * Variable-size sliding window: longest substring without repeating chars.
     * LC 3 - Template 2 from Topic 1.
     */
    public static int lengthOfLongestSubstring(String s) {
        Set<Character> window = new HashSet<>();
        int left = 0, maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            while (window.contains(s.charAt(right))) {
                window.remove(s.charAt(left));
                left++;
            }
            window.add(s.charAt(right));
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }
}
