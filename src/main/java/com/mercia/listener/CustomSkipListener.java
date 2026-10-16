package com.mercia.listener;

import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

import com.mercia.model.Transaction;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomSkipListener implements SkipListener<Transaction, Transaction>{

	public void onSkipInRead(Throwable t) {
		log.info(t.getMessage());
	}
	
	public void onSkipInProcess(Transaction item,Throwable t)
	{
		
	}
}
