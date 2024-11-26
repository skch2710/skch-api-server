package com.skch.skch_api_server.util;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.extern.slf4j.Slf4j;

@EnableCaching
@Service
@Slf4j
public class CacheService {
	
	private LoadingCache<Object, String> otpCache;
    private LoadingCache<Object, Object> passwordLinkCache;

    public CacheService(@Value("${app.otp-expiry}") Integer EXPIRE_MINS,
                        @Value("${app.passwordlink-expiry}") Integer EXPIRE_HOURS) {
        log.debug("Initializing CacheService");
        otpCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES)
                .concurrencyLevel(10) // Adjust based on your application's requirements
                .build(CacheLoader.from(key -> null));
        
        passwordLinkCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_HOURS, TimeUnit.HOURS)
                .concurrencyLevel(10) // Adjust based on your application's requirements
                .build(CacheLoader.from(key -> null));
    }

    public String generateOTP(String key) {
        String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
        otpCache.put(key, otp);
        log.debug("Generated OTP for key: {}", key);
        return otp;
    }

    public Object generatePasswordLinkSessionId(String key) {
        Instant now = Instant.now();
        passwordLinkCache.put(key, now);
        return now;
    }

	public String getOtp(String key) {
		log.debug("Fetching OTP for key: {}", key);
		try {
			return otpCache.get(key);
		} catch (Exception e) {
			log.error("error in getOtp ");
			return "0";
		}
	}

	public Object getPasswordLinkSessionId(String key) {
		try {
			return passwordLinkCache.get(key);
		} catch (Exception e) {
			return "0";
		}
	}

    public void clearOTP(String key) {
        otpCache.invalidate(key);
    }

    public void clearPasswordLinkSessionId(String key) {
        passwordLinkCache.invalidate(key);
    }

}
