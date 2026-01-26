package com.skch.skch_api_server.util;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.skch.skch_api_server.dto.JwtDTO;
import com.skch.skch_api_server.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

	private final RestClient restClient;

	private JwtDTO cachedToken;
	private Instant expiresAt;

	private final String TOKEN_URL = "http://localhost:8061/oauth2/token";
	private final String CLIENT_CREDENTIALS = "sathish_ch:Sathish@123";

	public String getValidAccessToken() {

		// ✅ return cached token if valid
		if (cachedToken != null && expiresAt != null && Instant.now().isBefore(expiresAt)) {
			return cachedToken.getAccess_token();
		}

		// ✅ fetch new token with retry
		JwtDTO newToken = fetchToken();

		long safeSeconds = Math.max(0, newToken.getExpires_in() - 30);
		this.cachedToken = newToken;
		this.expiresAt = Instant.now().plusSeconds(safeSeconds);

		return newToken.getAccess_token();
	}

	@Retryable(retryFor = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
	public JwtDTO fetchToken() {
		int attemptCount = RetrySynchronizationManager.getContext().getRetryCount() + 1;
		log.info("Attempting #{} fetchToken", attemptCount);
		try {
			String encodedCredentials = Base64.getEncoder()
					.encodeToString(CLIENT_CREDENTIALS.getBytes(StandardCharsets.UTF_8));

			MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
			requestBody.add("grant_type", "client_credentials");

			ResponseEntity<JwtDTO> response = restClient.post().uri(TOKEN_URL)
					.header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED).body(requestBody).retrieve()
					.toEntity(JwtDTO.class);

			return response.getBody();
		} catch (Exception e) {
			log.error("Error in fetchToken #{}", attemptCount, e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void clearToken() {
		this.cachedToken = null;
		this.expiresAt = null;
	}
}
