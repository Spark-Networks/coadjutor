package net.spark;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

class RandomTest {
    @RepeatedTest(10)
    public void shouldReturnNumberBetweenRange() {
        int start = 10;
        int end = 100;

        int actual = Random.between(start, end);

        Assertions.assertTrue(actual < end);
        Assertions.assertTrue(actual >= start);
    }
}