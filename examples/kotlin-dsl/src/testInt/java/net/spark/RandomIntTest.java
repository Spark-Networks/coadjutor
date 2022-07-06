package net.spark;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RandomIntTest {
    @RepeatedTest(10)
    @Order(0)
    public void shouldReturnNumberBetweenRange() {
        int start = Random.between(0, 10);
        int end = Random.between(30, 50);

        int actual = Random.between(start, end);

        assertTrue(actual < end);
        assertTrue(actual >= start);
    }

    @Test
    @Order(1)
    void failingTestOne() {
        throw new RuntimeException("first failed test", new RuntimeException("first failed test nested"));
    }

    @Order(2)
    @RepeatedTest(4)
    void successTestTwo() {
        assertTrue(true);
    }


    @Test
    @Order(3)
    void failingTestTwo() {
        throw new RuntimeException("second failed test", new RuntimeException("second failed test nested"));
    }
}