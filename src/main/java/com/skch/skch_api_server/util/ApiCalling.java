package com.skch.skch_api_server.util;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiCalling {

    private final RestClient restClient;
    private final RestTemplate restTemplate;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

//    @Retryable(retryFor = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @Retryable( includes = Exception.class, maxRetries = 2, delay = 2000 )
    //So maxRetries = 2 means 3 total attempts, not 2.
    public Result data() {

        Result result = new Result();
//        int attemptCount = RetrySynchronizationManager.getContext().getRetryCount() + 1;
        log.info("Attempting #{} data ", 1);

        try {
            String accessToken = tokenService.getValidAccessToken();

            ResponseEntity<Object> response = restClient.post()
                    .uri("http://localhost:8061/api/v1/test/test-post")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("X-ReqHeader", "sathish_ch")
                    .body(Map.of("test", "test"))
                    .retrieve()
                    .toEntity(Object.class);

            result.setData(response.getBody()); 
            return result;

        } catch (Exception e) {
            log.error("Error in data #{} ", 1, e);
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
//    @Retryable(retryFor = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @Retryable( includes = Exception.class, maxRetries = 2, delay = 2000 )
    public Result dataRestTemplate() {
        Result result = new Result();
//        int attemptCount = RetrySynchronizationManager.getContext().getRetryCount() + 1;
        log.info("Attempting #{} data ", 1);
        try {
            String accessToken = tokenService.getValidAccessToken();

            HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(accessToken);
			headers.add("X-ReqHeader", "sathish_ch");

			String requestBody = objectMapper.writeValueAsString(Map.of("test", "test"));
			HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
			String url = "http://localhost:8061/api/v1/test/test-post";

			ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);

            result.setData(response.getBody()); 
            return result;

        } catch (Exception e) {
            log.error("Error in data #{} ", 1, e);
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
