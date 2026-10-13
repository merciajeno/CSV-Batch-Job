package com.mercia.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.JobOperatorFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Bean
    public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setDatabaseType("POSTGRES");
        factory.setTablePrefix("batch_"); 
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean //to include metadata db from the jar file script
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        //from the JAR file schema script
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
	Job myJob(Step myTaskletStep,JobRepository jobRepository)
	{
		return new JobBuilder("myJob",jobRepository)
				.start(myTaskletStep)
				.build();
	}
	
	@Bean
	Job secondJob(Step myTaskletStep,JobRepository jobRepository)
	{
		return new JobBuilder("csvJob",jobRepository)
				.start(myTaskletStep)
				.build();
	}
	
	@Bean
    Step myTaskletStep(JobRepository jobRepository, 
	                          PlatformTransactionManager transactionManager, 
	                          Tasklet myTasklet) {
	    return new StepBuilder("myTaskletStep", jobRepository)
	            .tasklet(myTasklet, transactionManager)
	            .build();
	}

	@Bean
	Tasklet myTasklet() {
	    return (contribution, chunkContext) -> {
	        // Write your custom logic here
	    	//throw new RuntimeException("Someone is dead!!!");
	        System.out.println("Executing task without reader, writer, or processor!");
	        return RepeatStatus.FINISHED;
	    };
	}
}
