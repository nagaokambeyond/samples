package com.example.demo.util

object PageCalculator {
    fun calculateTotalPages(totalElements: Long, size: Int): Int {
        if (totalElements == 0L) {
            return 0
        }
        return ((totalElements + size - 1) / size).toInt()
    }

    fun calculateOffset(page: Int, size: Int): Long {
        return page.toLong() * size
    }
}
