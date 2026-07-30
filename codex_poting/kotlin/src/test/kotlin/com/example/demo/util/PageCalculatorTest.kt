package com.example.demo.util

import com.example.demo.util.PageCalculator.calculateOffset
import com.example.demo.util.PageCalculator.calculateTotalPages
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class PageCalculatorTest {
    @Test
    fun calculateTotalPagesReturnsZeroWhenTotalElementsIsZero() {
        Assertions.assertThat(calculateTotalPages(0, 10)).isZero()
    }

    @Test
    fun calculateTotalPagesReturnsExactPageCount() {
        Assertions.assertThat(calculateTotalPages(20, 10)).isEqualTo(2)
    }

    @Test
    fun calculateTotalPagesRoundsUpPageCount() {
        Assertions.assertThat(calculateTotalPages(21, 10)).isEqualTo(3)
    }

    @Test
    fun calculateTotalPagesReturnsOneWhenTotalElementsFitsInOnePage() {
        Assertions.assertThat(calculateTotalPages(1, 10)).isEqualTo(1)
    }

    @Test
    fun calculateOffsetReturnsZeroWhenPageIsFirstPage() {
        Assertions.assertThat(calculateOffset(0, 10)).isZero()
    }

    @Test
    fun calculateOffsetReturnsOffsetForSecondPage() {
        Assertions.assertThat(calculateOffset(1, 10)).isEqualTo(10)
    }

    @Test
    fun calculateOffsetReturnsOffsetForLaterPage() {
        Assertions.assertThat(calculateOffset(2, 3)).isEqualTo(6)
    }
}
