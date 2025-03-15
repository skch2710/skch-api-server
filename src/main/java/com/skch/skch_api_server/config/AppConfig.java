package com.skch.skch_api_server.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import com.smartystreets.api.ClientBuilder;
import com.smartystreets.api.us_street.Client;

@Configuration
@EnableRetry
public class AppConfig {
	
	@Value("${smarty.auth-id}")
	private String authId;
	
	@Value("${smarty.auth-token}")
	private String authToken;

//	@Bean
//    public TaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(5);
//        scheduler.initialize();
//        scheduler.setThreadNamePrefix("Scheduler-");
//        return scheduler;
//    }

	@Bean
	public TaskScheduler taskScheduler() {
		ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(
				Runtime.getRuntime().availableProcessors(), Thread.ofVirtual().factory());
		return new ConcurrentTaskScheduler(scheduledExecutor);
	}
	
	@Bean
	Client smartyClient() {
		return new ClientBuilder(authId, authToken).buildUsStreetApiClient();
	}

}
