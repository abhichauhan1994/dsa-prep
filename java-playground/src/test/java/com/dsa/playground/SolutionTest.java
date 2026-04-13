package com.dsa.playground;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {

    private Runner runner;

    @BeforeEach
    void setUp() {
        runner = new Runner();
    }

    // -----------------------------------------------------------------------
    // maxSumSubarray tests
    // -----------------------------------------------------------------------

    @Test
    void maxSumSubarray_typicalCase() {
        int[] nums = {2, 1, 5, 1, 3, 2};
        assertEquals(9, runner.maxSumSubarray(nums, 3));
    }

    @Test
    void maxSumSubarray_windowCoversWholeArray() {
        int[] nums = {4, 2, 7};
        assertEquals(13, runner.maxSumSubarray(nums, 3));
    }

    @Test
    void maxSumSubarray_windowSizeOne() {
        int[] nums = {3, 1, 4, 1, 5, 9};
        assertEquals(9, runner.maxSumSubarray(nums, 1));
    }

    @Test
    void maxSumSubarray_allNegative() {
        int[] nums = {-3, -1, -4, -1, -5};
        assertEquals(-2, runner.maxSumSubarray(nums, 2)); // -1 + -1
    }

    @Test
    void maxSumSubarray_singleElement() {
        int[] nums = {42};
        assertEquals(42, runner.maxSumSubarray(nums, 1));
    }

    @Test
    void maxSumSubarray_maxAtStart() {
        int[] nums = {10, 9, 1, 2, 3};
        assertEquals(19, runner.maxSumSubarray(nums, 2));
    }

    @Test
    void maxSumSubarray_maxAtEnd() {
        int[] nums = {1, 2, 3, 9, 10};
        assertEquals(19, runner.maxSumSubarray(nums, 2));
    }

    @Test
    void maxSumSubarray_invalidInput_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> runner.maxSumSubarray(new int[]{1, 2}, 5)); // k > length
    }

    @Test
    void maxSumSubarray_nullInput_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> runner.maxSumSubarray(null, 3));
    }

    // -----------------------------------------------------------------------
    // HOW TO ADD MORE TESTS
    // -----------------------------------------------------------------------
    // 1. Add your algorithm method to Runner.java (or create a new class).
    // 2. Copy the pattern below and fill in your own values.
    //
    // @Test
    // void myAlgorithm_description() {
    //     int[] input = {1, 2, 3};
    //     int expected = 42;
    //     assertEquals(expected, runner.myAlgorithm(input));
    // }
    // -----------------------------------------------------------------------
}
