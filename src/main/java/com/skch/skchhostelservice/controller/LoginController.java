package com.skch.skchhostelservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skch.skchhostelservice.dto.LoginRequest;
import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.service.LoginService;

@RestController
@RequestMapping("/api/authenticate")
public class LoginController {
	
	@Autowired
	private LoginService loginService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		Result response = loginService.login(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOTP(@RequestBody LoginRequest request) {
		Result response = loginService.verifyOTP(request);
		return ResponseEntity.ok(response);
	}

}
