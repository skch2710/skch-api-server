package com.skch.skch_api_server.util;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisCacheOtp {

	private static final String OTP_PREFIX = "OTP:";
	private final StringRedisTemplate redisTemplate;

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final DecimalFormat OTP_FORMAT = new DecimalFormat("000000");

	@Value("${app.otp-expiry}")
	private Integer expireMins;

	private String getRedisKey(String key) {
		return OTP_PREFIX + key;
	}

	public String generateOTP(String key) {
		String otp = OTP_FORMAT.format(RANDOM.nextInt(1_000_000));
		redisTemplate.opsForValue()
				.set(getRedisKey(key), otp, Duration.ofMinutes(expireMins));
		return otp;
	}

	public Optional<String> getOtp(String key) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(getRedisKey(key)));
	}

	public void clearOTP(String key) {
		redisTemplate.delete(getRedisKey(key));
	}

}