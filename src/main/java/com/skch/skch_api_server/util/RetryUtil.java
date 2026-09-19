package com.skch.skch_api_server.util;

import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RetryUtil {

	// Default 3 attempts with 1 second backoff

//	@Retryable(retryFor = {
//			RuntimeException.class }, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	@Retryable(includes = RuntimeException.class, maxRetries = 4, delay = 2000, multiplier = 2, maxDelay = 10000, jitter = 500)
	public String getRetry() {
//		int attemptCount = RetrySynchronizationManager.getContext().getRetryCount() + 1;
		log.info("Attempting operation...");
		try {
			// Simulate operation that may fail
			if (true) { // 70% chance to fail
				throw new RuntimeException("Simulated failure");
			}
			log.info("Operation succeeded on attempt");
		} catch (Exception e) {
			log.error("Operation failed on attempt: {}", e.getMessage());
			throw e; // Rethrow to trigger retry
		}
		return "Operation completed successfully.";
	}
	
	//Check if the retry attempts are exhausted and execute recovery logic
	public String recover(Exception e) {
		log.info("All retry attempts exhausted. Executing recovery logic.",e);
		return "Recovery action executed after retries.";
	}

}
