package com.dsa.playground;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        System.out.println("=== DSA Playground Ready ===");

        // Sliding window: max sum subarray of fixed size k
        int[] nums = {2, 1, 5, 1, 3, 2};
        int k = 3;
        int result = maxSumSubarray(nums, k);
        System.out.println("maxSumSubarray({2,1,5,1,3,2}, k=3) = " + result); // expected: 9
    }

    /**
     * Fixed-size sliding window template.
     * Finds the maximum sum of any contiguous subarray of length k.
     *
     * Time:  O(n)
     * Space: O(1)
     */
    public int maxSumSubarray(int[] nums, int k) {
        if (nums == null || nums.length < k || k <= 0) {
            throw new IllegalArgumentException("Invalid input");
        }

        // Build the initial window of size k
        int windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += nums[i];
        }

        int maxSum = windowSum;

        // Slide the window: add next element, remove first element of previous window
        for (int i = k; i < nums.length; i++) {
            windowSum += nums[i] - nums[i - k];
            maxSum = Math.max(maxSum, windowSum);
        }

        return maxSum;
    }
}
