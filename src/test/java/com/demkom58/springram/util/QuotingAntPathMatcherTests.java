package com.demkom58.springram.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class QuotingAntPathMatcherTests {
    @Test
    void tokenizePath() {
        final var matcher = new QuotingAntPathMatcher(" ");
        assertArrayEquals(new String[]{"mm", "m", "mmm", "mmmm mmm"}, matcher.tokenizePath("mm m \"mmm\" \"mmmm mmm\""));
        assertArrayEquals(new String[]{}, matcher.tokenizePath("\"\" \"     \""));
    }
}
