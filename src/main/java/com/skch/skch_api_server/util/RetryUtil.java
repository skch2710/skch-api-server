package com.skch.skch_api_server.util;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RetryUtil {

	// Default 3 attempts with 1 second backoff

	@Retryable(retryFor = {
			RuntimeException.class }, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 1.5))
	public String getRetry() {
		int attemptCount = RetrySynchronizationManager.getContext().getRetryCount() + 1;
		log.info("Attempting #{} operation...", attemptCount);
		try {
			// Simulate operation that may fail
			if (true) { // 70% chance to fail
				throw new RuntimeException("Simulated failure");
			}
			log.info("Operation succeeded on attempt #{}", attemptCount);
		} catch (Exception e) {
			log.error("Operation failed on attempt #{}: {}", attemptCount, e.getMessage());
			throw e; // Rethrow to trigger retry
		}
		return "Operation completed successfully.";
	}
	
	@Recover
	public String recover(Exception e) {
		log.info("All retry attempts exhausted. Executing recovery logic.",e);
		return "Recovery action executed after retries.";
	}

}
