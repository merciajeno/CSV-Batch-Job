package com.mercia.mapper;

import java.time.LocalDate;

import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import com.mercia.exception.InvalidRecordException;
import com.mercia.model.SummaryDto;
import com.mercia.model.Transaction;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TransactionMapper implements FieldSetMapper<Transaction>{

	private final SummaryDto summaryDto;
	@Override
	public Transaction mapFieldSet(FieldSet fieldSet) throws BindException {
		// TODO Auto-generated method stub
		Transaction transaction = new Transaction();
		
		String transactionId = fieldSet.readString(0);
		if(transactionId.isEmpty())
		{
			summaryDto.incrementFailedRecords();
			throw new InvalidRecordException("Transaction Id is empty");
		}
		
		transaction.setTransactionId(transactionId);
		transaction.setAccountNumber(fieldSet.readString(1));
		
		String date = fieldSet.readString(2);
		transaction.setTransactionDate(LocalDate.parse(date));
		
		String amount = fieldSet.readString(3);
		if (amount == null || !amount.matches("\\d+(\\.\\d+)?")) {
			summaryDto.incrementFailedRecords();
		    throw new InvalidRecordException("Transaction amount is invalid");
		}
		
		transaction.setAmount(Double.parseDouble(amount));
		transaction.setCurrency(fieldSet.readString(4));
		transaction.setTransactionType(fieldSet.readString(5));
		transaction.setDescription(fieldSet.readString(6));
		
		summaryDto.incrementTotalRecords();
		return transaction;
	}

}
