package com.mercia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;

import com.mercia.listener.CustomSkipListener;
import com.mercia.model.Summary;
import com.mercia.model.SummaryDto;
import com.mercia.repository.SummaryRepository;
import com.mercia.service.JobService;


class CsvJobApplicationTests {

    private JobService jobService;
    private SummaryRepository summaryRepository;
    private SummaryDto summaryDto;
    private CsvJobApplication application;
    private CustomSkipListener customSkipListener;

    @BeforeEach
    void setUp() {
        jobService = mock(JobService.class);
        summaryRepository = mock(SummaryRepository.class);
        summaryDto = mock(SummaryDto.class);
        customSkipListener = mock(CustomSkipListener.class);

        application = new CsvJobApplication(
                jobService,
                summaryRepository,
                summaryDto,
                customSkipListener
        );
    }

    @Test
    void shouldRunJobsAndSaveSummary() throws Exception {

        when(summaryDto.getTotalRecords()).thenReturn(10L);
        when(summaryDto.getTotalInvalidRecords()).thenReturn(1L);
        when(summaryDto.getDuplicateRecords()).thenReturn(2L);
        when(summaryDto.getTotalMatchedRecords()).thenReturn(3L);
        when(summaryDto.getTotalMisMatchedRecords()).thenReturn(2L);
        when(summaryDto.getTotalMissingRecords()).thenReturn(2L);
        when(jobService.previousJobSuccess("myJob")).thenReturn(true);
        when(jobService.previousJobSuccess("csvJob")).thenReturn(true);
        application.run();

       
        verify(jobService).runJob();
        verify(jobService).runCsvJob();

      
        ArgumentCaptor<Summary> captor =
                ArgumentCaptor.forClass(Summary.class);

        verify(summaryRepository).save(captor.capture());

        Summary savedSummary = captor.getValue();

        assertEquals(10L, savedSummary.getTotalRecords());
        assertEquals(1L, savedSummary.getTotalInvalidRecords());
        assertEquals(2L, savedSummary.getTotalDuplicateRecords());
        assertEquals(3L, savedSummary.getTotalMatchedRecords());
        assertEquals(2L, savedSummary.getTotalMisMatchedRecords());
        assertEquals(2L, savedSummary.getTotalMissingRecords());
    }
    
    @Test
    void shouldFailIfFirstJobFailed() throws NoSuchJobInstanceException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, InvalidJobParametersException
    {
    	when(jobService.previousJobSuccess("myJob")).thenReturn(false);
    	when(jobService.previousJobSuccess("csvJob")).thenReturn(false);
    	verify(jobService,never()).runCsvJob();
    }
    
    @Test
    void noAuditWhenNoJobsRan() throws NoSuchJobInstanceException, IOException 
    {
    	when(jobService.previousJobSuccess("myJob")).thenReturn(false);
    	when(jobService.previousJobSuccess("csvJob")).thenReturn(false);
    	
    	verify(customSkipListener,never()).writeSkippedRecords();
    }
    
    @Test
    void noSummaryWhenNoJobsRan() throws NoSuchJobInstanceException
    {
    	when(jobService.previousJobSuccess("myJob")).thenReturn(false);
    	when(jobService.previousJobSuccess("csvJob")).thenReturn(false);
    	
    	verify(summaryRepository,never()).save(any(Summary.class));
    }
}