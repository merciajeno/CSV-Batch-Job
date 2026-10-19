package com.mercia.step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class FileValidationService {

	private final int MAX_SIZE = 10 * 1024 * 1024;// max size of the file 10mb
	private final String validHeader = "transaction_id,account_number,transaction_date,amount,currency,transaction_type,description";

	public boolean validateFile(ClassPathResource fileResource) throws IOException {
		if (!fileResource.exists())
			return false;

		String filename = fileResource.getFilename();
		if (!filename.endsWith(".csv"))
			return false;

		long size = 0;

		size = fileResource.contentLength();

		if (size > MAX_SIZE)
			return false;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileResource.getInputStream()))) {
			String header = reader.readLine();
			if (!header.equals(validHeader))
				return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
