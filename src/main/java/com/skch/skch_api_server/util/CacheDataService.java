package com.skch.skch_api_server.util;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CacheDataService {
	
	private static final SecureRandom RANDOM = new SecureRandom();
//	private static final DecimalFormat OTP_FORMAT = new DecimalFormat("000000");
	
	@Cacheable(value = "sampleDataCache", key = "#p0")
	public Map<String,String> getDataFromCache(String key) {
		log.info("Fetching data for key: {}", key);
		return  sampleData();
	}
	
	
	public Map<String,String> sampleData() {
		log.info("Generating sample data");
		Map<String,String> data = new HashMap<>();
		for(int i=0;i<10;i++) {
			data.put(UUID.randomUUID().toString(), String.format("%06d", RANDOM.nextInt(1_000_000)));
		}
		return data;
	}

}
