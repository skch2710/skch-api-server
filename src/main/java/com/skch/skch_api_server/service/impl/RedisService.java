package com.skch.skch_api_server.service.impl;

import java.time.Duration;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
	
	private static final String KEY_PREFIX = "USER_SESSION:";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final JwtDecoder jwtDecoder;

    public void saveSession(String userId, String token, Duration ttl) {
        String redisKey = KEY_PREFIX + userId;
        String sid = extractSid(token);
        log.info("Saving session for userId={} with sid={}", userId, sid);
        redisTemplate
            .opsForValue()
            .set(redisKey, sid, ttl)
            .block();
    }
    
    public String extractSid(String accessToken) {
        Jwt jwt = jwtDecoder.decode(accessToken);
        return jwt.getClaimAsString("sid");
    }

}
