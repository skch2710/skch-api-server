package com.skch.skch_api_server.util;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.skch.skch_api_server.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestClientHelper {
	
	public static RetryTemplate retryTemplate() {
		Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
		retryableExceptions.put(Exception.class, true);
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3, retryableExceptions);
		return RetryTemplate.builder().customPolicy(retryPolicy).fixedBackoff(2000).build();
	}
	
	public static <T> T getRetry(String url, Class<T> responseType) {
		try {
			return retryTemplate().execute(context -> {
				try {
					ResponseEntity<T> response = RestClient.create().get().uri(url)
							.retrieve().toEntity(responseType);
					return response.getBody();
				} catch (Exception e) {
					int count = context.getRetryCount() + 1;
					log.error("Error in getRetry #{} attempt failed : {}", count, e.getMessage(), e);
					throw e;
				}
			});
		} catch (Exception e) {
			log.error("Error in getRetry ", e);
			return null;
		}
	}
	
	public static <T> T get(String url, String auth, Class<T> responseType) {
		ResponseEntity<T> response = RestClient.create().get()
				.uri(url).header(HttpHeaders.AUTHORIZATION, auth)
				.retrieve().toEntity(responseType);
		return response.getBody();
	}
	
	public static <T> T post(String url, String auth,Object object, Class<T> responseType) {
		ResponseEntity<T> response = RestClient.create().post()
				.uri(url)
//				.header(HttpHeaders.AUTHORIZATION, auth)
				.body(object)
				.retrieve().toEntity(responseType);
		return response.getBody();
	}
	
	public static <T> T getTokens(Map<String,String> values, Class<T> responseType) {
		ResponseEntity<T> response = null;
		try {
			String encodedCredentials = new String(Base64.getEncoder().encode(values.get("clientCred").getBytes()));

			// Create the request body with username and pwd
			MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
			requestBody.add("grant_type", values.get("grantType"));
			requestBody.add("username", values.get("userName"));
			requestBody.add("password", values.get("pwd"));
			
			response= RestClient.create().post()
					  .uri(values.get("url"))
					  .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
					  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
					  .body(requestBody)
					  .retrieve()
					  .toEntity(responseType);
			
		} catch (Exception e) {
			log.error("Error in getTokens...",e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		return response!=null ? response.getBody() : null;
	}
	
	public static <T> T getRefreshToken(Map<String,String> values, Class<T> responseType) {
		ResponseEntity<T> response = null;
		try {
			String encodedCredentials = new String(Base64.getEncoder().encode(values.get("clientCred").getBytes()));

			// Create the request body with username and password
			MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
			requestBody.add("grant_type", values.get("grantType"));
			requestBody.add("refresh_token", values.get("refresh_token"));
			
			response= RestClient.create().post()
					  .uri(values.get("url"))
					  .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
					  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
					  .body(requestBody)
					  .retrieve()
					  .toEntity(responseType);
			
		} catch (Exception e) {
			log.error("Error in getRefreshToken...",e);
			throw new CustomException(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		return response!=null ? response.getBody() : null;
	}
	
}
