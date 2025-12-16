package com.skch.skch_api_server.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.skch.skch_api_server.dto.JwtDTO;
import com.skch.skch_api_server.dto.LoginRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtUtil {
	
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

}
