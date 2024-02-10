package com.skch.skchhostelservice.service;

import java.io.ByteArrayOutputStream;

import com.skch.skchhostelservice.dto.LoginRequest;
import com.skch.skchhostelservice.dto.Result;

public interface LoginService {

	Result login(LoginRequest request);

	Result verifyOTP(LoginRequest request);
	
	ByteArrayOutputStream getPdf();
	
}
