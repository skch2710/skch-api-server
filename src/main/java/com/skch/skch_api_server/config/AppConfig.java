package com.skch.skch_api_server.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Configuration
@EnableRetry
public class AppConfig {

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

}
