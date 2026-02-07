package com.skch.skch_api_server.util;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.skch.skch_api_server.dto.JwtDTO;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheUtil {
	
	@Value("${app.token-expiry}")
	private long tokenExpiry;

	public void setCache(HttpServletResponse response, JwtDTO dto) {
		// ACCESS TOKEN (short-lived)
		ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", dto.getAccess_token())
				.httpOnly(true).secure(true)
				.sameSite("None").path("/")
				.maxAge(Duration.ofMinutes(tokenExpiry)).build();

		// REFRESH TOKEN (long-lived)
		ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", dto.getRefresh_token())
				.httpOnly(true).secure(true)
				.sameSite("Lax").path("/authenticate")
				.maxAge(Duration.ofHours(tokenExpiry)).build();

		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
	}

}
