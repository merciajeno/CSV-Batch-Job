package com.mercia.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mercia.exception.InvalidRecordException;
import com.mercia.model.SummaryDto;
import com.mercia.model.Transaction;

class DuplicateProcessorTest {

    private SummaryDto summaryDto;
    private DuplicateProcessor duplicateProcessor;

    @BeforeEach
    void setUp() {
        summaryDto = mock(SummaryDto.class);
        duplicateProcessor = new DuplicateProcessor(summaryDto);
    }

    @Test
    void shouldReturnTransactionWhenNotDuplicate() throws Exception {

        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN101");

        Transaction result = duplicateProcessor.process(transaction);

        assertNotNull(result);
        assertSame(transaction, result);
    }

    @Test
    void shouldThrowExceptionWhenTransactionIsDuplicate() throws Exception {

        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId("TXN101");

        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId("TXN101");

        duplicateProcessor.process(transaction1);
        assertThrows(
            InvalidRecordException.class,
            () -> duplicateProcessor.process(transaction2)
        );

        verify(summaryDto, times(1)).incrementDuplicateRecords();
    }

    @Test
    void shouldProcessDifferentTransactionsSuccessfully() throws Exception {

        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId("TXN101");

        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId("TXN102");

        assertSame(transaction1, duplicateProcessor.process(transaction1));
        assertSame(transaction2, duplicateProcessor.process(transaction2));
    }
}