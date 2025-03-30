package com.skch.skch_api_server.job;

import java.time.Duration;
import java.util.Date;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class SchedulerJob {
	
//	@Scheduled(cron = "0/5 * * * * *")
	public void testSchedulerJob() throws Exception {
		log.info("Hii...test1 SchedulerJob Startrd for 5 sec "+new Date());
		Thread.sleep(Duration.ofSeconds(8));
		log.info("Hii...test1 SchedulerJob End for 5 sec "+new Date());
	}
	
//	@Scheduled(fixedDelay = 5000) //Every 5 Sec
//	@Scheduled(cron = "0/5 * * * * *")
	public void testSchedulerJob2() throws Exception {
		log.info("Hii...test2 SchedulerJob Started "+new Date());
		Thread.sleep(Duration.ofSeconds(12));
		log.info("Hii...test2 SchedulerJob End "+new Date());
	}
	
//	@Scheduled(cron = "0/5 * * * * *")
	public void testSchedulerJob3() throws Exception {
		log.info("Hii...test3 SchedulerJob Startrd for 5 sec "+new Date());
		Thread.sleep(Duration.ofSeconds(8));
		log.info("Hii...test3 SchedulerJob End for 5 sec "+new Date());
	}
	
//	@Scheduled(fixedDelay = 5000) //Every 5 Sec
//	@Scheduled(cron = "0/5 * * * * *")
	public void testSchedulerJob4() throws Exception {
		log.info("Hii...test4 SchedulerJob Started "+new Date());
		Thread.sleep(Duration.ofSeconds(12));
		log.info("Hii...test4 SchedulerJob End "+new Date());
	}
	
	/*
	 * One Table schedular_jobs_java
	 * 
	 * job_id , Job_Name , scheduler_cron , start_date , end_date, genarated_date 
	 * 
	 * If Job Started start_date is now and end date is null
	 * Then Job End update end_date
	 */

}
