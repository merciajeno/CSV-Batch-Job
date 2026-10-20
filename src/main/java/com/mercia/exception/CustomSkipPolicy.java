package com.mercia.exception;

import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

@Component
public class CustomSkipPolicy implements SkipPolicy {

	@Override
	public boolean shouldSkip(Throwable t, long skipCount) {
		return (t instanceof FlatFileParseException || t instanceof InvalidRecordException)
				&& skipCount < Integer.MAX_VALUE;// Worst case: if all the records are
		// invalid
	}
}