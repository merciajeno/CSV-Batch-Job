package com.mercia.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.mercia.exception.InvalidRecordException;
import com.mercia.model.SummaryDto;
import com.mercia.model.Transaction;

class TransactionMapperTest {

    private SummaryDto summaryDto;
    private TransactionMapper transactionMapper;
    private FieldSet fieldSet;

    @BeforeEach
    void setUp() {
        summaryDto = mock(SummaryDto.class);
        fieldSet = mock(FieldSet.class);

        // No Spring Batch context needed for a unit test
        transactionMapper = new TransactionMapper(summaryDto, "myJob");
    }

    @Test
    void shouldMapValidRecord() throws BindException {

        when(fieldSet.readString(0)).thenReturn("TXN001");
        when(fieldSet.readString(1)).thenReturn("ACC001");
        when(fieldSet.readString(2)).thenReturn("2026-10-17");
        when(fieldSet.readString(3)).thenReturn("100.50");
        when(fieldSet.readString(4)).thenReturn("INR");
        when(fieldSet.readString(5)).thenReturn("CREDIT");
        when(fieldSet.readString(6)).thenReturn("Salary");

        Transaction result = transactionMapper.mapFieldSet(fieldSet);

        assertEquals("TXN001", result.getTransactionId());
        assertEquals("ACC001", result.getAccountNumber());
        assertEquals(LocalDate.of(2026, 10, 17),
                     result.getTransactionDate());
        assertEquals(100.50, result.getAmount(), 0.001);
        assertEquals("INR", result.getCurrency());
        assertEquals("CREDIT", result.getTransactionType());
        assertEquals("Salary", result.getDescription());

        verify(summaryDto).incrementTotalRecords();
        verify(summaryDto, never()).incrementInvalidRecords();
    }

    @Test
    void shouldThrowExceptionWhenTransactionIdIsEmpty()
            throws BindException {

        when(fieldSet.readString(0)).thenReturn("");

        assertThrows(
            InvalidRecordException.class,
            () -> transactionMapper.mapFieldSet(fieldSet)
        );

        verify(summaryDto).incrementTotalRecords();
        verify(summaryDto).incrementInvalidRecords();
    }

    @Test
    void shouldThrowExceptionWhenAmountIsInvalid()
            throws BindException {

        when(fieldSet.readString(0)).thenReturn("TXN001");
        when(fieldSet.readString(1)).thenReturn("ACC001");
        when(fieldSet.readString(2)).thenReturn("2026-10-17");
        when(fieldSet.readString(3)).thenReturn("abc");

        assertThrows(
            InvalidRecordException.class,
            () -> transactionMapper.mapFieldSet(fieldSet)
        );

        verify(summaryDto).incrementTotalRecords();
        verify(summaryDto).incrementInvalidRecords();
    }

    @Test
    void shouldNotIncrementTotalRecordsForAnotherJob()
            throws BindException {

        TransactionMapper otherJobMapper =
                new TransactionMapper(summaryDto, "anotherJob");

        when(fieldSet.readString(0)).thenReturn("TXN001");
        when(fieldSet.readString(1)).thenReturn("ACC001");
        when(fieldSet.readString(2)).thenReturn("2026-10-17");
        when(fieldSet.readString(3)).thenReturn("100.50");
        when(fieldSet.readString(4)).thenReturn("INR");
        when(fieldSet.readString(5)).thenReturn("CREDIT");
        when(fieldSet.readString(6)).thenReturn("Salary");

        otherJobMapper.mapFieldSet(fieldSet);

        verify(summaryDto, never()).incrementTotalRecords();
    }
}