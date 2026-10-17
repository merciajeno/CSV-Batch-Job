package com.mercia.model;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

@Component
public class SummaryDto {

	private final AtomicLong totalRecords = new AtomicLong(0);
    private final AtomicLong totalMatchedRecords = new AtomicLong(0);
    private final AtomicLong totalMisMatchedRecords = new AtomicLong(0);
    private final AtomicLong totalMissingRecords = new AtomicLong(0);
    private final AtomicLong totalInvalidRecords = new AtomicLong(0);
    private final AtomicLong totalDuplicateRecords = new AtomicLong(0);

    public void incrementTotalRecords() {
        totalRecords.incrementAndGet();
    }

    public void incrementMatchedRecords() {
        totalMatchedRecords.incrementAndGet();
    }

    public void incrementMisMatchedRecords() {
        totalMisMatchedRecords.incrementAndGet();
    }

    public void incrementMissingRecords() {
        totalMissingRecords.incrementAndGet();
    }

    public void incrementInvalidRecords() {
        totalInvalidRecords.incrementAndGet();
    }
    
    public void incrementDuplicateRecords()
    {
    	totalDuplicateRecords.incrementAndGet();
    }

    // Thread-safe getters to read current values
    public long getTotalRecords() {
        return totalRecords.get();
    }

    public long getTotalMatchedRecords() {
        return totalMatchedRecords.get();
    }

    public long getTotalMisMatchedRecords() {
        return totalMisMatchedRecords.get();
    }

    public long getTotalMissingRecords() {
        return totalMissingRecords.get();
    }

    public long getTotalInvalidRecords() {
        return totalInvalidRecords.get();
    }
    
    public long getDuplicateRecords()
    {
    	return totalDuplicateRecords.get();
    }
    
}
