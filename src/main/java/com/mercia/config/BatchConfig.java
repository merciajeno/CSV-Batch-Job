package com.mercia.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.JobOperatorFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import com.mercia.exception.CustomSkipPolicy;
import com.mercia.listener.CustomSkipListener;
import com.mercia.mapper.TransactionMapper;
import com.mercia.model.Transaction;
import com.mercia.repository.TransactionRepository;
import com.mercia.step.DuplicateProcessor;
import com.mercia.step.FileValidationService;
import com.mercia.step.ReconciliationProcessor;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository
public class BatchConfig {

	@Bean // to include metadata db from the jar file script
	DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		// from the JAR file schema script
		databasePopulator.addScript(new ClassPathResource("org/springframework/batch/core/schema-postgresql.sql"));
		databasePopulator.setContinueOnError(true); // Won't crash on future restarts

		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator);
		return initializer;
	}

	@Bean
	JobOperatorFactoryBean jobOperator(JobRepository jobRepository) {
		JobOperatorFactoryBean jobOperatorFactoryBean = new JobOperatorFactoryBean();
		jobOperatorFactoryBean.setJobRepository(jobRepository);
		return jobOperatorFactoryBean;
	}

	@Bean
	Job myJob(Step firstStep, JobRepository jobRepository) {
		return new JobBuilder("myJob", jobRepository).start(firstStep).build();
	}

	@Bean
	Job secondJob(Step secondStep, JobRepository jobRepository) {
		return new JobBuilder("csvJob", jobRepository).start(secondStep).build();
	}

	@Bean
	Step firstStep(PlatformTransactionManager transactionManager, JobRepository repository,
			FlatFileItemReader<Transaction> reader, ItemWriter<Transaction> writer, CustomSkipPolicy skipPolicy,
			CustomSkipListener skipListener, DuplicateProcessor processor, AsyncTaskExecutor taskExecutor) {
		return new StepBuilder("jobStep", repository)
				.<Transaction, Transaction>chunk(1000)
				.transactionManager(transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.faultTolerant()
				.skipPolicy(skipPolicy)
				.listener(skipListener)
				.taskExecutor(taskExecutor)
				.build();
	}

	@Bean
	Step secondStep(PlatformTransactionManager transactionManager, JobRepository repository,
			FlatFileItemReader<Transaction> reader, ItemWriter<Transaction> writer, CustomSkipPolicy skipPolicy,
			CustomSkipListener skipListener, ReconciliationProcessor processor, AsyncTaskExecutor taskExecutor) {
		return new StepBuilder("jobStep", repository)
				.<Transaction, Transaction>chunk(1000)
				.transactionManager(transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.faultTolerant()
				.skipPolicy(skipPolicy)
				.listener(skipListener)
				.taskExecutor(taskExecutor)
				.build();
	}

	@Bean
	@JobScope
	FlatFileItemReader<Transaction> reader(TransactionMapper mapper,
			@Value("#{jobParameters['input.file.path']}") String filePath
			,FileValidationService validatorService) throws IOException{

		ClassPathResource fileResource = new ClassPathResource(filePath);
		if(!validatorService.validateFile(fileResource))
			throw new RuntimeException("Not a valid file");
		return new FlatFileItemReaderBuilder<Transaction>()
				.name("sharedCsvReader")
				.resource(fileResource)
				.linesToSkip(1) // header
				.delimited()
				.names("transaction_id", "account_number", "transaction_date", "amount", "currency",
						"transaction_type", "description")
				.fieldSetMapper(mapper)
				.build();
	}

	@Bean
	ItemWriter<Transaction> writer(TransactionRepository transactionRepository) {
		return items -> transactionRepository.saveAll(items);
	}

}
