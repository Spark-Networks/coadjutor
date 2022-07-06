package net.spark;

public class Random {
    public static int between(int min, int max) {
        return (int) ((Math.random() * (max - 1 - min)) + min);
    }
}
