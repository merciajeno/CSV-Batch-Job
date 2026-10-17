package com.mercia.step;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;

class FileValidationServiceTest {

    private FileValidationService fileValidationService;

    @BeforeEach
    void setUp() {
        fileValidationService = new FileValidationService();
    }

    @TempDir
    Path tempDir;

    @Test
    void shouldReturnTrueForValidCsvFile() throws IOException {

        Path file = tempDir.resolve("transactions.csv");

        Files.writeString(file,
            "transaction_id,account_number,transaction_date,amount,currency,transaction_type,description\n");

        boolean result = fileValidationService.validateFile(
            new ClassPathResource("transactions.csv") {
                @Override
                public boolean exists() {
                    return true;
                }

                @Override
                public String getFilename() {
                    return file.getFileName().toString();
                }

                @Override
                public java.io.InputStream getInputStream() throws IOException {
                    return Files.newInputStream(file);
                }
            }
        );

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenFileDoesNotExist() {

        ClassPathResource resource = new ClassPathResource("missing.csv");

        boolean result = fileValidationService.validateFile(resource);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForNonCsvFile() {

        ClassPathResource resource = new ClassPathResource("transactions.txt") {
            @Override
            public boolean exists() {
                return true;
            }

            @Override
            public String getFilename() {
                return "transactions.txt";
            }
        };

        assertFalse(fileValidationService.validateFile(resource));
    }

    @Test
    void shouldReturnFalseForIncorrectHeader() throws IOException {

        Path file = tempDir.resolve("wrong.csv");
        Files.writeString(file, "id,account,amount\n");

        ClassPathResource resource = new ClassPathResource("wrong.csv") {
            @Override
            public boolean exists() {
                return true;
            }

            @Override
            public String getFilename() {
                return "wrong.csv";
            }

            @Override
            public java.io.InputStream getInputStream() throws IOException {
                return Files.newInputStream(file);
            }
        };

        assertFalse(fileValidationService.validateFile(resource));
    }
}