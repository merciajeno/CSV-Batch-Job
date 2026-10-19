package com.mercia;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mercia.listener.CustomSkipListener;
import com.mercia.model.Summary;
import com.mercia.model.SummaryDto;
import com.mercia.repository.SummaryRepository;
import com.mercia.service.JobService;

import lombok.AllArgsConstructor;

@SpringBootApplication
@AllArgsConstructor
public class CsvJobApplication implements CommandLineRunner{

	private final JobService jobService;
	private final SummaryRepository summaryRepository;
	private final SummaryDto summaryDto;
	private final CustomSkipListener customSkipListener;
	
	public static void main(String[] args) {
		SpringApplication.run(CsvJobApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//jobService.restartJob();
		jobService.runJob();
		
		if(jobService.previousJobSuccess("myJob"))// if previous job success, then run the next job
		  jobService.runCsvJob();
		
		if(jobService.previousJobSuccess("csvJob"))
		{
		Summary summary = new Summary();
		summary.setTotalInvalidRecords(summaryDto.getTotalInvalidRecords());
		summary.setTotalDuplicateRecords(summaryDto.getDuplicateRecords());
		summary.setTotalMatchedRecords(summaryDto.getTotalMatchedRecords());
		summary.setTotalMisMatchedRecords(summaryDto.getTotalMisMatchedRecords());
		summary.setTotalMissingRecords(summaryDto.getTotalMissingRecords());
		summary.setTotalRecords(summaryDto.getTotalRecords());
		summaryRepository.save(summary);
		
		customSkipListener.writeSkippedRecords();
		}
	}

}
