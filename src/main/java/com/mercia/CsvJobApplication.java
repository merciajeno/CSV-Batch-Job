package com.mercia;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mercia.service.JobService;

import lombok.AllArgsConstructor;

@SpringBootApplication
@AllArgsConstructor
public class CsvJobApplication implements CommandLineRunner{

	private final JobService jobService;
	
	public static void main(String[] args) {
		SpringApplication.run(CsvJobApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		//jobService.restartJob();
		jobService.runJob();
		jobService.runCsvJob();
	}

}
