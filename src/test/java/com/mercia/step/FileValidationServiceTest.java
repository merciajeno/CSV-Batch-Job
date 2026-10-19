package com.mercia.step;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
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

        ClassPathResource resource = new ClassPathResource("transactions.csv") {
            @Override
            public boolean exists() {
                return Files.exists(file);
            }

            @Override
            public String getFilename() {
                return "transactions.csv";
            }

            @Override
            public java.io.InputStream getInputStream() throws IOException {
                return Files.newInputStream(file);
            }
             
            @Override
            public java.net.URL getURL() throws IOException {
                return file.toUri().toURL();
            }
            
        };

        assertTrue(fileValidationService.validateFile(resource));
    }

    @Test
    void shouldReturnFalseWhenFileDoesNotExist() throws IOException {

        ClassPathResource resource = new ClassPathResource("missing.csv");

        boolean result = fileValidationService.validateFile(resource);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForNonCsvFile() throws IOException {

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
                return Files.exists(file);
            }

            @Override
            public String getFilename() {
                return "wrong.csv";
            }

            @Override
            public java.io.InputStream getInputStream() throws IOException {
                return Files.newInputStream(file);
            }
           
            @Override 
            public URL getURL() throws IOException
            {
            	return file.toUri().toURL();
            }
            
        };

        assertFalse(fileValidationService.validateFile(resource));
    }
}