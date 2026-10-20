package com.mercia.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.explore.JobExplorer;

class JobServiceTest {

    private JobOperator jobOperator;
    private Job myJob;
    private Job secondJob;
    private JobExplorer jobExplorer;
    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobOperator = mock(JobOperator.class);
        myJob = mock(Job.class);
        secondJob = mock(Job.class);
        jobExplorer = mock(JobExplorer.class);

        jobService = new JobService(
            jobOperator, myJob, secondJob, jobExplorer
        );
    }

    @Test
    void shouldRunMyJob() throws Exception {

        jobService.runJob();

        verify(jobOperator).run(
            eq(myJob), any(JobParameters.class)
        );
    }

    @Test
    void shouldRunCsvJob() throws Exception {

        jobService.runCsvJob();

        verify(jobOperator).run(
            eq(secondJob), any(JobParameters.class)
        );
    }
    

    @Test
    void shouldNotRestartWhenNoPreviousInstanceExists()
            throws Exception {

        when(jobExplorer.getLastJobInstance("myJob"))
            .thenReturn(null);

        jobService.restartJob();

        verify(jobOperator, never()).restart(any(JobExecution.class));
    }

    @Test
    void shouldNotRestartWhenPreviousInstanceSuccess() throws NoSuchJobInstanceException, JobRestartException, NoSuchJobException
    {
    	JobInstance instance = mock(JobInstance.class);
        JobExecution execution = mock(JobExecution.class);

        when(instance.getInstanceId()).thenReturn(10L);
        when(jobExplorer.getLastJobInstance("myJob"))
            .thenReturn(instance);
        when(jobOperator.getExecutions(10L))
            .thenReturn(List.of(20L));
        when(jobExplorer.getJobExecution(20L))
            .thenReturn(execution);
        when(execution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        jobService.restartJob();

        verify(jobOperator,never()).restart(execution);
    }
    @Test
    void shouldRestartLastExecution() throws Exception {

        JobInstance instance = mock(JobInstance.class);
        JobExecution execution = mock(JobExecution.class);

        when(instance.getInstanceId()).thenReturn(10L);
        when(jobExplorer.getLastJobInstance("myJob"))
            .thenReturn(instance);
        when(jobOperator.getExecutions(10L))
            .thenReturn(List.of(20L));
        when(jobExplorer.getJobExecution(20L))
            .thenReturn(execution);
        when(execution.getStatus()).thenReturn(BatchStatus.FAILED);
        jobService.restartJob();

        verify(jobOperator).restart(execution);
    }
    
    @Test
    void shouldReturnTrueWhenJobSuccess() throws NoSuchJobInstanceException
    {
    	 JobInstance instance = mock(JobInstance.class);
         JobExecution execution = mock(JobExecution.class);

         when(instance.getInstanceId()).thenReturn(10L);
         when(jobExplorer.getLastJobInstance("myJob"))
             .thenReturn(instance);
         when(jobOperator.getExecutions(10L))
             .thenReturn(List.of(20L));
         when(jobExplorer.getJobExecution(20L))
             .thenReturn(execution);
         when(execution.getStatus()).thenReturn(BatchStatus.COMPLETED);
         assertTrue(jobService.previousJobSuccess("myJob"));
    }
    
    @Test
    void shouldReturnFalseWhenJobFailed() throws NoSuchJobInstanceException
    {
    	JobInstance instance = mock(JobInstance.class);
        JobExecution execution = mock(JobExecution.class);

        when(instance.getInstanceId()).thenReturn(10L);
        when(jobExplorer.getLastJobInstance("myJob"))
            .thenReturn(instance);
        when(jobOperator.getExecutions(10L))
            .thenReturn(List.of(20L));
        when(jobExplorer.getJobExecution(20L))
            .thenReturn(execution);
        when(execution.getStatus()).thenReturn(BatchStatus.FAILED);
        assertFalse(jobService.previousJobSuccess("myJob"));
    }
}