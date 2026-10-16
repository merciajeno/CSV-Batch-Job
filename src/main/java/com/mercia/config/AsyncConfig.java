package com.mercia.config;

import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.mercia.model.Transaction;
import com.mercia.step.DuplicateProcessor;

@Configuration
public class AsyncConfig {

	@Bean
	AsyncTaskExecutor taskExecutor() {
	 return new SimpleAsyncTaskExecutor();
	}
	
	@Bean
	AsyncItemProcessor<Transaction, Transaction> asyncItemProcessor(DuplicateProcessor processor,TaskExecutor taskExecutor)
	{
		AsyncItemProcessor<Transaction,Transaction> asyncItemProcessor = new AsyncItemProcessor<>(processor);
	    asyncItemProcessor.setTaskExecutor(taskExecutor);
	
	    return asyncItemProcessor;
	}
	
	@Bean
	AsyncItemWriter<Transaction> asyncWriter(ItemWriter<Transaction> itemWriter) {
	    AsyncItemWriter<Transaction> asyncItemWriter = new AsyncItemWriter<>(itemWriter);
	    
	    return asyncItemWriter;
	}
}
