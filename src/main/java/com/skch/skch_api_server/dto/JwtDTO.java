package com.skch.skch_api_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtDTO {

	private String access_token;
	private String refresh_token;
	private String token_type;
	private Long expires_in;
	
	private String resource;

}
