package com.mercia.listener;

import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

import com.mercia.model.Transaction;

@Component
public class CustomSkipListener implements SkipListener<Transaction, Transaction>{

	public void onSkipInRead(Throwable t) {
		
	}
}
