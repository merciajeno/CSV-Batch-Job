package com.mercia.config;

import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import com.mercia.mapper.TransactionMapper;
import com.mercia.model.Transaction;

public class BatchConfigTest {

	@Autowired
	private FlatFileItemReader<Transaction> reader;
	
	@Autowired
	private TransactionMapper mapper;
	
	@Test
	void readValidFile_Success()
	{
	
	}
}
