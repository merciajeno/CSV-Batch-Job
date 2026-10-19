package com.mercia.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SummaryDtoTest {

    private SummaryDto summaryDto;

    @BeforeEach
    void setUp() {
        summaryDto = new SummaryDto();
    }

    @Test
    void shouldIncrementAllCounters() {

        summaryDto.incrementTotalRecords();
        summaryDto.incrementMatchedRecords();
        summaryDto.incrementMisMatchedRecords();
        summaryDto.incrementMissingRecords();
        summaryDto.incrementInvalidRecords();
        summaryDto.incrementDuplicateRecords();

        assertEquals(1, summaryDto.getTotalRecords());
        assertEquals(1, summaryDto.getTotalMatchedRecords());
        assertEquals(1, summaryDto.getTotalMisMatchedRecords());
        assertEquals(1, summaryDto.getTotalMissingRecords());
        assertEquals(1, summaryDto.getTotalInvalidRecords());
        assertEquals(1, summaryDto.getDuplicateRecords());
    }

    @Test
    void shouldStartAllCountersAtZero() {

        assertEquals(0, summaryDto.getTotalRecords());
        assertEquals(0, summaryDto.getTotalMatchedRecords());
        assertEquals(0, summaryDto.getTotalMisMatchedRecords());
        assertEquals(0, summaryDto.getTotalMissingRecords());
        assertEquals(0, summaryDto.getTotalInvalidRecords());
        assertEquals(0, summaryDto.getDuplicateRecords());
    }
}
