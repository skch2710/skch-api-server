package com.skch.skch_api_server.util;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.skch.skch_api_server.dto.JwtDTO;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
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
	
	public void setCacheLogout(HttpServletResponse response) {
		// ACCESS TOKEN
		ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", "")
				.httpOnly(true).secure(true)
				.sameSite("None").path("/")
				.maxAge(Duration.ofMinutes(0)).build();

		// REFRESH TOKEN
		ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", "")
				.httpOnly(true).secure(true)
				.sameSite("Lax").path("/authenticate")
				.maxAge(Duration.ofHours(0)).build();

		response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
	}

}
