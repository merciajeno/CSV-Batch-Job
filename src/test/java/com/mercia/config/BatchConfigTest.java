package com.mercia.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.transaction.PlatformTransactionManager;

import com.mercia.exception.CustomSkipPolicy;
import com.mercia.listener.CustomSkipListener;
import com.mercia.mapper.TransactionMapper;
import com.mercia.model.Transaction;
import com.mercia.repository.TransactionRepository;
import com.mercia.step.DuplicateProcessor;
import com.mercia.step.FileValidationService;
import com.mercia.step.ReconciliationProcessor;


@ExtendWith(MockitoExtension.class)
class BatchConfigTest {

    @Mock
    private TransactionRepository transactionRepository;

    private BatchConfig batchConfig;
    
    @Mock
    private FileValidationService validatorService;
    
    @Mock
    private TransactionMapper mapper;

    @BeforeEach
    void setUp() {
        batchConfig = new BatchConfig();
    }

    @Test
    void shouldSaveTransactions() throws Exception {

        ItemWriter<Transaction> writer =
                batchConfig.writer(transactionRepository);

        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");

        Chunk<Transaction> chunk =
                new Chunk<>(List.of(transaction));

        writer.write(chunk);

        
    }
    
    @Test
    void shouldCreateMyJob() {

        JobRepository jobRepository = mock(JobRepository.class);
        Step firstStep = mock(Step.class);

        Job job = batchConfig.myJob(firstStep, jobRepository);

        assertEquals("myJob", job.getName());
    }
    
    @Test
    void shouldCreateCsvJob() {

        JobRepository jobRepository = mock(JobRepository.class);
        Step secondStep = mock(Step.class);

        Job job = batchConfig.secondJob(secondStep, jobRepository);

        assertEquals("csvJob", job.getName());
    }
    
    @Test
    void shouldCreateFirstStep() {

        PlatformTransactionManager transactionManager =
                mock(PlatformTransactionManager.class);
        JobRepository repository = mock(JobRepository.class);
        FlatFileItemReader<Transaction> reader = mock(FlatFileItemReader.class);
        ItemWriter<Transaction> writer = mock(ItemWriter.class);
        CustomSkipPolicy skipPolicy = mock(CustomSkipPolicy.class);
        CustomSkipListener skipListener = mock(CustomSkipListener.class);
        DuplicateProcessor processor = mock(DuplicateProcessor.class);
        AsyncTaskExecutor taskExecutor = mock(AsyncTaskExecutor.class);

        Step step = batchConfig.firstStep(
                transactionManager,
                repository,
                reader,
                writer,
                skipPolicy,
                skipListener,
                processor,
                taskExecutor
        );

        assertNotNull(step);
        assertEquals("jobStep", step.getName());
    }

    @Test
    void shouldCreateSecondStep() {

    	PlatformTransactionManager transactionManager =
                mock(PlatformTransactionManager.class);
        JobRepository repository = mock(JobRepository.class);
        FlatFileItemReader<Transaction> reader = mock(FlatFileItemReader.class);
        ItemWriter<Transaction> writer = mock(ItemWriter.class);
        CustomSkipPolicy skipPolicy = mock(CustomSkipPolicy.class);
        CustomSkipListener skipListener = mock(CustomSkipListener.class);
        ReconciliationProcessor processor = mock(ReconciliationProcessor.class);
        AsyncTaskExecutor taskExecutor = mock(AsyncTaskExecutor.class);

        Step step = batchConfig.secondStep(
                transactionManager,
                repository,
                reader,
                writer,
                skipPolicy,
                skipListener,
                processor,
                taskExecutor
        );

        assertNotNull(step);
        assertEquals("jobStep", step.getName());
    }
    
    @Test
    void shouldCreateDataSourceInitializer() {

        DataSource dataSource = mock(DataSource.class);

        DataSourceInitializer initializer =
                batchConfig.dataSourceInitializer(dataSource);

        assertNotNull(initializer);
    }
    
    @Test
    void shouldCreateReaderWhenFileIsValid() {

        when(validatorService.validateFile(any(ClassPathResource.class)))
                .thenReturn(true);

        var reader = batchConfig.reader(
                mapper, "transactions.csv", validatorService);

        assertNotNull(reader);
        assertEquals("sharedCsvReader", reader.getName());
    }

    @Test
    void shouldThrowExceptionWhenFileIsInvalid() {

        when(validatorService.validateFile(any(ClassPathResource.class)))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> batchConfig.reader(
                        mapper, "transactions.csv", validatorService)
        );

        assertEquals("Not a valid file", exception.getMessage());
    }
}