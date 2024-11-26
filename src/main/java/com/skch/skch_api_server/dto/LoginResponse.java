package com.skch.skch_api_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
	
	private Boolean isOtpEnable;
	private String otp;
	private JwtDTO jwtDTO;
	private UserDTO user;
	private Object navigations;
	
}
