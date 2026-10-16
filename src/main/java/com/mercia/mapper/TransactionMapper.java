package com.mercia.mapper;

import java.time.LocalDate;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import com.mercia.exception.InvalidRecordException;
import com.mercia.model.SummaryDto;
import com.mercia.model.Transaction;

@Component
@JobScope
public class TransactionMapper implements FieldSetMapper<Transaction>{

	  private final SummaryDto summaryDto;
	    private final String jobName;

	    public TransactionMapper(
	            SummaryDto summaryDto,
	            @Value("#{jobExecution.jobInstance.jobName}") String jobName) {
	        this.summaryDto = summaryDto;
	        this.jobName = jobName;
	    }
	
	@Override
	public Transaction mapFieldSet(FieldSet fieldSet) throws BindException {
		// TODO Auto-generated method stub
		Transaction transaction = new Transaction();
		if(jobName.equals("myJob"))
			 summaryDto.incrementTotalRecords();
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
		
		
		return transaction;
	}

}
