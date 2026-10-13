package com.mercia.service;

import java.util.List;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.explore.JobExplorer;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JobService {

	private final JobOperator jobOperator;
	private final Job myJob;
	private final Job secondJob;
	private final JobExplorer jobExplorer;
	
	public void runJob() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, InvalidJobParametersException
	{
		JobParameters jobParameters = new JobParametersBuilder().
				addString("csv", "myCsv")
				.addLong("timeStamp",System.currentTimeMillis())
				.toJobParameters();
		
		jobOperator.run(myJob, jobParameters);
	}
	
	public void runCsvJob() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, InvalidJobParametersException
	{
		JobParameters jobParameters = new JobParametersBuilder().
				addString("mercia", "murderer")
				.addLong("timeStamp",System.currentTimeMillis())
				.toJobParameters();
		
		jobOperator.run(secondJob, jobParameters);
	}
	
	public void restartJob() throws NoSuchJobException, JobRestartException, NoSuchJobInstanceException
	{
		JobInstance lastInstance = jobExplorer.getLastJobInstance("myJob");

		if (lastInstance != null) {
		    // 2. Get execution IDs for this instance (ordered reverse chronologically)
		    List<Long> executionIds = jobOperator.getExecutions(lastInstance.getInstanceId());
		    
		    if (!executionIds.isEmpty()) {
		        Long lastExecutionId = executionIds.get(0);
		        // Load the JobExecution object if needed via JobExplorer or handle by ID
		        JobExecution lastExecution = jobExplorer.getJobExecution(lastExecutionId);
		       // System.out.println(lastExecution.toString());
		        jobOperator.restart(lastExecution);
		    }
		}
			
		
	}
}
