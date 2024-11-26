package com.skch.skch_api_server.service;

import java.io.ByteArrayOutputStream;

import com.skch.skch_api_server.dto.LoginRequest;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.dto.ValidateLinkDTO;

public interface LoginService {

	Result login(LoginRequest request);

	Result verifyOTP(LoginRequest request);
	
	ByteArrayOutputStream getPdf();
	
	Result validateUuid(ValidateLinkDTO dto);
	
}
