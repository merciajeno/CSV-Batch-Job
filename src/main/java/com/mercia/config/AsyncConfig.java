package com.mercia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@Configuration
public class AsyncConfig {

	@Bean
	AsyncTaskExecutor taskExecutor() {
	 return new VirtualThreadTaskExecutor();
	}
	
}
