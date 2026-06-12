package com.example.demo.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageCalculatorTest {
    @Test
    void calculateTotalPagesReturnsZeroWhenTotalElementsIsZero() {
        assertThat(PageCalculator.calculateTotalPages(0, 10)).isZero();
    }

    @Test
    void calculateTotalPagesReturnsExactPageCount() {
        assertThat(PageCalculator.calculateTotalPages(20, 10)).isEqualTo(2);
    }

    @Test
    void calculateTotalPagesRoundsUpPageCount() {
        assertThat(PageCalculator.calculateTotalPages(21, 10)).isEqualTo(3);
    }

    @Test
    void calculateTotalPagesReturnsOneWhenTotalElementsFitsInOnePage() {
        assertThat(PageCalculator.calculateTotalPages(1, 10)).isEqualTo(1);
    }

    @Test
    void calculateOffsetReturnsZeroWhenPageIsFirstPage() {
        assertThat(PageCalculator.calculateOffset(0, 10)).isZero();
    }

    @Test
    void calculateOffsetReturnsOffsetForSecondPage() {
        assertThat(PageCalculator.calculateOffset(1, 10)).isEqualTo(10);
    }

    @Test
    void calculateOffsetReturnsOffsetForLaterPage() {
        assertThat(PageCalculator.calculateOffset(2, 3)).isEqualTo(6);
    }
}
