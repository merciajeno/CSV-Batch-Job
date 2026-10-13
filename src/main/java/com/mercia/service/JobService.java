package com.mercia.service;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JobService {

	private final JobOperator jobOperator;
	private final Job myJob;
	
	public void runJob() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, InvalidJobParametersException
	{
		JobParameters jobParameters = new JobParametersBuilder().
				addString("csv", "myCsv")
				.addLong("timeStamp",System.currentTimeMillis())
				.toJobParameters();
		
		jobOperator.run(myJob, jobParameters);
	}
	
	public void runCsvJob()
	{
		
	}
}
