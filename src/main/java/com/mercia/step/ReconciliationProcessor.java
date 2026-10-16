package com.mercia.step;

import java.util.Optional;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.mercia.model.SummaryDto;
import com.mercia.model.Transaction;
import com.mercia.repository.TransactionRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class ReconciliationProcessor implements ItemProcessor<Transaction, Transaction>{

	private final SummaryDto summaryDto;
	private final TransactionRepository transactionRepository;
	@Override
	public  Transaction process(Transaction item) throws Exception {
		// TODO Auto-generated method stub
		String txnId = item.getTransactionId();
		
		Optional<Transaction> transactionByid = transactionRepository.findById(txnId);
		
		if(transactionByid.isPresent())
		{
			
			
			Transaction existingTransaction = transactionByid.get();
			double existingAmount = existingTransaction.getAmount();
			if(existingAmount!=item.getAmount())
			{	
				summaryDto.incrementMisMatchedRecords();
				log.info("Updated:"+item.getTransactionId());
			//existingTransaction.setAmount(item.getAmount());
			return item;// we want to update only the mismatched records
			}
			summaryDto.incrementMatchedRecords();
		}
		summaryDto.incrementMissingRecords();
		return null;
	}

}
