package com.mercia.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mercia.exception.InvalidRecordException;
import com.mercia.model.Transaction;

class CustomSkipListenerTest {

    private CustomSkipListener listener;

    @BeforeEach
    void setUp() {
        listener = new CustomSkipListener();
    }

    @Test
    void shouldCollectSkippedRecordDuringRead() {

        listener.onSkipInRead(
                new InvalidRecordException("Invalid CSV row"));

        List<String> records = listener.getSkippedRecords();

        assertEquals(1, records.size());
        assertTrue(records.get(0).contains("READ"));
        assertTrue(records.get(0).contains("Invalid CSV row"));
    }

    @Test
    void shouldCollectSkippedRecordDuringProcessing() {

        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");

        listener.onSkipInProcess(
                transaction,
                new InvalidRecordException("Invalid amount"));

        List<String> records = listener.getSkippedRecords();

        assertEquals(1, records.size());
        assertTrue(records.get(0).contains("PROCESS"));
        assertTrue(records.get(0).contains("TXN001"));
        assertTrue(records.get(0).contains("Invalid amount"));
    }

    @Test
    void shouldReturnAllCollectedRecords() {

        listener.onSkipInRead(
                new RuntimeException("Read error"));

        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN002");

        listener.onSkipInProcess(
                transaction,
                new RuntimeException("Process error"));

        List<String> records = listener.getSkippedRecords();

        assertEquals(2, records.size());
        assertTrue(records.get(0).contains("Read error"));
        assertTrue(records.get(1).contains("Process error"));
    }

    @Test
    void shouldWriteSkippedRecordsToCsv() throws Exception {

        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");

        listener.onSkipInRead(
                new RuntimeException("Invalid row"));

        listener.onSkipInProcess(
                transaction,
                new RuntimeException("Invalid amount"));

        listener.writeSkippedRecords();

        Path outputFile = Path.of(
                "output", "skipped_transactions.csv");

        assertTrue(Files.exists(outputFile));

        List<String> lines = Files.readAllLines(outputFile);

        assertEquals(
                "stage,transaction_id,error_message",
                lines.get(0));

        assertEquals(3, lines.size());
        assertTrue(lines.get(1).contains("READ"));
        assertTrue(lines.get(2).contains("TXN001"));
    }

    @Test
    void shouldWriteHeaderWhenNoRecordsAreSkipped()
            throws Exception {

        listener.writeSkippedRecords();

        Path outputFile = Path.of(
                "output", "skipped_transactions.csv");

        assertTrue(Files.exists(outputFile));

        List<String> lines = Files.readAllLines(outputFile);

        assertEquals(1, lines.size());
        assertEquals(
                "stage,transaction_id,error_message",
                lines.get(0));
    }
}
