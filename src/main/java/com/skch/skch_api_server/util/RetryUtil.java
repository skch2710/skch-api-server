package com.skch.skch_api_server.util;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RetryUtil {

	@Retryable(retryFor = { RuntimeException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
	public void getRetry() {
		int attemptCount = RetrySynchronizationManager.getContext().getRetryCount() + 1;
		log.info("Attempting {} operation...", attemptCount);
		throw new RuntimeException("Failed");
	}

}
