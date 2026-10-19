package com.mercia.exception;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;

class CustomSkipPolicyTest {

    private CustomSkipPolicy skipPolicy;

    @BeforeEach
    void setUp() {
        skipPolicy = new CustomSkipPolicy();
    }

    @Test
    void shouldSkipInvalidRecordException() {

        boolean result = skipPolicy.shouldSkip(
                new InvalidRecordException("Invalid transaction"), 0);

        assertTrue(result);
    }

    @Test
    void shouldSkipFlatFileParseException() {

        Throwable exception = mock(FlatFileParseException.class);

        assertTrue(skipPolicy.shouldSkip(exception, 0));
    }

    @Test
    void shouldNotSkipOtherExceptions() {

        assertFalse(skipPolicy.shouldSkip(
                new RuntimeException("Unexpected error"), 0));
    }

    @Test
    void shouldNotSkipWhenSkipCountReachesMaximum() {

        assertFalse(skipPolicy.shouldSkip(
                new InvalidRecordException("Invalid transaction"),
                Integer.MAX_VALUE));
    }
}
