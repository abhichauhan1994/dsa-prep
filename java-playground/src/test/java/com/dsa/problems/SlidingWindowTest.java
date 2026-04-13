package com.dsa.problems;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class SlidingWindowTest {

    @Test
    @DisplayName("Max sum subarray - normal cases")
    void testMaxSumSubarray() {
        assertEquals(9, SlidingWindow.maxSumSubarray(new int[]{2, 1, 5, 1, 3, 2}, 3));
        assertEquals(7, SlidingWindow.maxSumSubarray(new int[]{2, 3, 4, 1, 5}, 2));
    }

    @Test
    @DisplayName("Max sum subarray - edge cases")
    void testMaxSumSubarrayEdge() {
        assertEquals(5, SlidingWindow.maxSumSubarray(new int[]{5}, 1));
        assertEquals(14, SlidingWindow.maxSumSubarray(new int[]{2, 1, 5, 1, 3, 2}, 6));
    }

    @Test
    @DisplayName("LC 3 - Longest substring without repeating characters")
    void testLongestSubstring() {
        assertEquals(3, SlidingWindow.lengthOfLongestSubstring("abcabcbb"));
        assertEquals(1, SlidingWindow.lengthOfLongestSubstring("bbbbb"));
        assertEquals(3, SlidingWindow.lengthOfLongestSubstring("pwwkew"));
        assertEquals(0, SlidingWindow.lengthOfLongestSubstring(""));
    }

    // --- Add more tests as you solve problems ---
    //
    // @Test
    // @DisplayName("LC 209 - Minimum size subarray sum")
    // void testMinSubArrayLen() {
    //     assertEquals(2, SlidingWindow.minSubArrayLen(7, new int[]{2,3,1,2,4,3}));
    // }
}
