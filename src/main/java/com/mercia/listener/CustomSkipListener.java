package com.mercia.listener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

import com.mercia.model.Transaction;

@Component
public class CustomSkipListener
        implements SkipListener<Transaction, Transaction> {

    private final Queue<String> skippedRecords =
            new ConcurrentLinkedQueue<>();

    @Override
    public void onSkipInRead(Throwable t) {
        skippedRecords.add(
                "READ,,\"" + t.getLocalizedMessage() + "\"");
    }

    @Override
    public void onSkipInProcess(Transaction item, Throwable t) {
        skippedRecords.add(
                "PROCESS," + item.getTransactionId()
                + ",\"" + t.getLocalizedMessage() + "\"");
    }

    public List<String> getSkippedRecords() {
        return new ArrayList<>(skippedRecords);
    }
    public void writeSkippedRecords() throws IOException {

        Path path = Path.of("output", "skipped_transactions.csv");

        Files.createDirectories(path.getParent());

        List<String> records = getSkippedRecords();

        List<String> lines = new ArrayList<>();
        lines.add("stage,transaction_id,error_message");
        lines.addAll(records);

        Files.write(
                path,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }
}
