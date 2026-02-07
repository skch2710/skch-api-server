package com.skch.skch_api_server.util;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import com.skch.skch_api_server.dto.JwtDTO;
import com.skch.skch_api_server.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

	private final RestClient restClient;
	
	private final RestTemplate restTemplate;

	// Changed to volatile for thread-safe visibility
	private volatile JwtDTO cachedToken;
	private volatile Instant expiresAt;

	private final String TOKEN_URL = "http://localhost:8061/oauth2/token";
	private final String CLIENT_CREDENTIALS = "sathish_ch:Sathish@123";

	public String getValidAccessToken() {
		JwtDTO currentToken = this.cachedToken;
		Instant currentExpiresAt = this.expiresAt;
		if (currentToken != null && currentExpiresAt != null 
				&& Instant.now().isBefore(currentExpiresAt)) {
			return currentToken.getAccess_token();
		}
		return refreshTokenIfNeeded().getAccess_token();
	}

	private synchronized JwtDTO refreshTokenIfNeeded() {
		if (cachedToken != null && expiresAt != null && Instant.now().isBefore(expiresAt)) {
			return cachedToken;
		}
		JwtDTO newToken = fetchToken();
		this.cachedToken = newToken;
		this.expiresAt = Instant.now().plusSeconds(Math.max(0, newToken.getExpires_in() - 30));
		log.debug("Refreshed MFA token. Expires at: {}", expiresAt);
		return newToken;
	}

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
	
	
	//RestTemplate
	
	public JwtDTO fetchTokenRestTemplate() {
		log.info(">>>>Attempting fetchToken....");
		try {
			// Create the request headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setBasicAuth(CLIENT_CREDENTIALS, CLIENT_CREDENTIALS);

			// Create the request body
			MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
			requestBody.add("grant_type", "client_credentials");

			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
			
			// Send the POST request to obtain the access token
			ResponseEntity<JwtDTO> responseEntity = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, requestEntity,
					JwtDTO.class);

			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error in fetchToken : ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void clearToken() {
		this.cachedToken = null;
		this.expiresAt = null;
	}
}
