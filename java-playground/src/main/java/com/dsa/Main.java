package com.dsa;

import com.dsa.problems.SlidingWindow;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== DSA Playground ===\n");

        // --- Sliding Window example ---
        int[] arr = {2, 1, 5, 1, 3, 2};
        int k = 3;
        System.out.println("Max sum subarray of size " + k + ": "
            + SlidingWindow.maxSumSubarray(arr, k));
        System.out.println("Input: " + Arrays.toString(arr));

        System.out.println("Longest substring without repeating: "
            + SlidingWindow.lengthOfLongestSubstring("abcabcbb"));

        // --- Add your code here ---
        // Solve a problem, print the result, move on.

        System.out.println("\n=== Done ===");
    }
}
