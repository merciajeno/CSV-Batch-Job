package com.mercia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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

        // Arrange: prepare summary values
        when(summaryDto.getTotalRecords()).thenReturn(10L);
        when(summaryDto.getTotalInvalidRecords()).thenReturn(1L);
        when(summaryDto.getDuplicateRecords()).thenReturn(2L);
        when(summaryDto.getTotalMatchedRecords()).thenReturn(3L);
        when(summaryDto.getTotalMisMatchedRecords()).thenReturn(2L);
        when(summaryDto.getTotalMissingRecords()).thenReturn(2L);

        // Act
        application.run();

        // Assert: verify both jobs were invoked
        verify(jobService).runJob();
        verify(jobService).runCsvJob();

        // Verify the summary was saved
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
}