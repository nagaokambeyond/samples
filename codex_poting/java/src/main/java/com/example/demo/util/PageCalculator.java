package com.example.demo.util;

public final class PageCalculator {
    private PageCalculator() {
    }

    public static int calculateTotalPages(long totalElements, int size) {
        if (totalElements == 0) {
            return 0;
        }
        return (int) ((totalElements + size - 1) / size);
    }

    public static long calculateOffset(int page, int size) {
        return (long) page * size;
    }
}
