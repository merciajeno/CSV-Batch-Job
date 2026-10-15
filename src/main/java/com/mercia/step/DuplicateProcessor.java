package com.mercia.step;

import java.util.HashSet;
import java.util.Set;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.mercia.exception.InvalidRecordException;
import com.mercia.model.Transaction;

@Component
public class DuplicateProcessor implements ItemProcessor<Transaction, Transaction>{

	private final Set<String> existingTransaction = new HashSet<>();
	@Override
	public Transaction process(Transaction item) throws Exception {
		// TODO Auto-generated method stub
		if(existingTransaction.contains(item.getTransactionId()))
			throw new InvalidRecordException("Duplicate record");
		existingTransaction.add(item.getTransactionId());
		return item;
	}

}
