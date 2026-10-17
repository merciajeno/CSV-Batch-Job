package com.mercia.step;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mercia.model.SummaryDto;
import com.mercia.model.Transaction;
import com.mercia.repository.TransactionRepository;

public class ReconciliationServiceTest {

	private  SummaryDto summaryDto;
	private TransactionRepository transactionRepository;
	private ReconciliationProcessor reconciliationProcessor;
	private Transaction sampleTransaction;
	
	@BeforeEach
	void setUp()
	{
		summaryDto = mock(SummaryDto.class);
		transactionRepository = mock(TransactionRepository.class);
		reconciliationProcessor = new ReconciliationProcessor(summaryDto, transactionRepository);
		sampleTransaction = new Transaction();
	}
	
	@Test
	void testWithMissingRecord() throws Exception
	{
		
		sampleTransaction.setTransactionId("TXN001");
		when(transactionRepository.findById(any(String.class))).thenReturn(Optional.empty());
		reconciliationProcessor.process(sampleTransaction);
		verify(summaryDto,times(1)).incrementMissingRecords();
	}
	
	@Test
	void testWithMatchedRecords() throws Exception {

	    sampleTransaction.setTransactionId("TXN001");
	    sampleTransaction.setAmount(100.00);

	    Transaction existingTransaction = new Transaction();
	    existingTransaction.setTransactionId("TXN001");
	    existingTransaction.setAmount(100.00);

	    when(transactionRepository.findById("TXN001"))
	        .thenReturn(Optional.of(existingTransaction));

	    Transaction result = reconciliationProcessor.process(sampleTransaction);

	    verify(summaryDto, times(1)).incrementMatchedRecords();
	    verify(summaryDto, never()).incrementMisMatchedRecords();

	    assertNull(result);
	}

	@Test
	void testWithMisMatchedRecords() throws Exception {

	    sampleTransaction.setTransactionId("TXN001");
	    sampleTransaction.setAmount(234.00);

	    Transaction existingTransaction = new Transaction();
	    existingTransaction.setTransactionId("TXN001");
	    existingTransaction.setAmount(100.00);

	    when(transactionRepository.findById("TXN001"))
	        .thenReturn(Optional.of(existingTransaction));

	    Transaction result = reconciliationProcessor.process(sampleTransaction);

	    verify(summaryDto, times(1)).incrementMisMatchedRecords();
	    verify(summaryDto, never()).incrementMatchedRecords();

	    assertSame(sampleTransaction, result);
	}
}
