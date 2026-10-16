package com.mercia.step;

import java.util.HashSet;
import java.util.Set;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.mercia.exception.InvalidRecordException;
import com.mercia.model.SummaryDto;
import com.mercia.model.Transaction;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class DuplicateProcessor implements ItemProcessor<Transaction, Transaction>{

	private final SummaryDto summaryDto;
	private final Set<String> existingTransaction = new HashSet<>();
	@Override
	public Transaction process(Transaction item) throws Exception {
		// TODO Auto-generated method stub
		if(existingTransaction.contains(item.getTransactionId()))
		{
			summaryDto.incrementFailedRecords();
			throw new InvalidRecordException("Duplicate record");
		}
		existingTransaction.add(item.getTransactionId());
		return item;
	}

}
