package com.skch.skch_api_server.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.skch.skch_api_server.common.AuthProps;
import com.skch.skch_api_server.dto.JwtDTO;
import com.skch.skch_api_server.dto.LoginRequest;
import com.skch.skch_api_server.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUtil {
	
    private final RestClient restClient;
    private final AuthProps authProps;
	
	public static final String SUPER_USER = "Super User";
	
	@Value("${app.url}")
	private String url;
	
	@Value("${app.auth-cred}")
	private String clientCred;
	
	public JwtDTO getToken(LoginRequest request) {
		JwtDTO dto = null;
		try {
			Map<String,String> values = new HashMap<>();
			values.put("url", url);
			values.put("clientCred", clientCred);
			values.put("userName", request.getEmailId());
			values.put("pwd", request.getPassword());
			values.put("grantType", "custom_pwd");
			
			dto = RestClientHelper.getTokens(values,JwtDTO.class);
		}catch(Exception e) {
			log.error("Error in getToken method...:: ",e);
		}
		return dto;
	}
	
	public JwtDTO getRefreshToken(String token) {
		JwtDTO dto = null;
		log.info(">>>>>Starting at get refresh token...");
		try {
			Map<String,String> values = new HashMap<>();
			values.put("url", url);
			values.put("clientCred", clientCred);
			values.put("grantType", "refresh_token");
			values.put("refresh_token", token);
			
			dto = RestClientHelper.getRefreshToken(values,JwtDTO.class);
		}catch(Exception e) {
			log.error("Error in getRefreshToken method...:: ",e);
		}
		return dto;
	}
	
	public static Long getUserId() {
		Long result = 1L;
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			String auth = authentication.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority) // Extract authority as string
					.filter(authority -> authority != null && authority.startsWith("USER_ID"))
					.findFirst().orElse("");

			result = Long.valueOf(AESUtils.decrypt(Utility.check(auth) ? auth.split(":")[1].trim() : "1L"));
		} catch (Exception e) {
			log.error("Error in getUserId...:: ",e);
		}
		return result;
	}
	
	public static String getUserName() {
		String result = "system";
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			result = authentication.getName();
		} catch (Exception e) {
			log.error("Error in getUserName...:: ", e);
		}
		return result;
	}
	
	
	public static Boolean checkAccess(String resource) {
		Boolean result = false;
		log.info(">>>>>Resource :: {}",resource);
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			result = authentication.getAuthorities().stream()
					.anyMatch(ga -> ga.getAuthority().equals(resource));
		} catch (Exception e) {
			log.error("Error in checkAccess...:: ", e);
		}
		return result;
	}
	
	public static Boolean checkAccess(String resource,String other) {
		Boolean result = false;
		System.out.println(other);
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			result = authentication.getAuthorities().stream()
					.anyMatch(ga -> ga.getAuthority().equals(resource));
		} catch (Exception e) {
			log.error("Error in checkAccess...:: ", e);
		}
		return result;
	}
	
	public static boolean checkProfileMatch(String emailId) {
		boolean result = false;
		System.out.println(emailId);
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			log.info(">>>>>Authentication Name :: {}",authentication.getName());
			result = authentication.getName().equalsIgnoreCase(emailId);
		} catch (Exception e) {
			log.error("Error in checkAccess...:: ", e);
		}
		return result;
	}
	
	// Authorization Code Grant Type
	public JwtDTO getAuthCodeTokens(String code, String codeVerifier) {
	    try {
	        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
	        requestBody.add("grant_type", "authorization_code");
	        requestBody.add("client_id", authProps.getClientId());
	        requestBody.add("code", code);
	        requestBody.add("redirect_uri", authProps.getRedirectUri());
	        requestBody.add("code_verifier", codeVerifier);

	        ResponseEntity<JwtDTO> response =
	        			restClient.post()
	                        .uri(authProps.getServer().getTokenUrl())
	                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	                        .body(requestBody)
	                        .retrieve()
	                        .toEntity(JwtDTO.class);

	        return response.getBody();

	    } catch (Exception e) {
	        log.error("Error exchanging authorization code for tokens", e);
	        throw new CustomException("Failed to get tokens using authorization code", HttpStatus.BAD_REQUEST);
	    }
	}


}
