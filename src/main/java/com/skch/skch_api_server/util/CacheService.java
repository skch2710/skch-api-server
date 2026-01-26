package com.skch.skch_api_server.util;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CacheService {

	private final Cache<String, String> otpCache;

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final DecimalFormat OTP_FORMAT = new DecimalFormat("000000");

	public CacheService(@Value("${app.otp-expiry}") Integer expireMins) {
		log.debug("Initializing CacheService");
		this.otpCache = CacheBuilder.newBuilder()
				.expireAfterWrite(expireMins, TimeUnit.MINUTES).concurrencyLevel(10)
				.build();
	}

	public String generateOTP(String key) {
		String otp = OTP_FORMAT.format(RANDOM.nextInt(1_000_000));
		otpCache.put(key, otp);
		log.debug("Generated OTP for key: {}", key);
		return otp;
	}

	public Optional<String> getOtp(String key) {
		return Optional.ofNullable(otpCache.getIfPresent(key));
	}

	public void clearOTP(String key) {
		otpCache.invalidate(key);
	}

}