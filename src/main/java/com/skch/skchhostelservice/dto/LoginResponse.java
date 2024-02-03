package com.skch.skchhostelservice.dto;

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
