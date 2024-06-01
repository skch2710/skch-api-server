package com.skch.skchhostelservice.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skch.skchhostelservice.dto.LoginRequest;
import com.skch.skchhostelservice.dto.ReqSearch;
import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.exception.CustomException;
import com.skch.skchhostelservice.service.LoginService;
import com.skch.skchhostelservice.util.Utility;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/authenticate")
@Slf4j
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


	@PostMapping("/generate-pdf")
	public ResponseEntity<?> generatePdf(@RequestBody ReqSearch search) throws Exception {
		try {
			ByteArrayOutputStream outputStream = loginService.getPdf();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "Sample.pdf");

			InputStreamResource inputStreamResource = new InputStreamResource(
					new ByteArrayInputStream(outputStream.toByteArray()));

			outputStream.flush();// Flush the output stream

			return ResponseEntity.ok().headers(headers).body(inputStreamResource);
		} catch (Exception e) {
			log.error("Error in Get Pdf Controller....",e);
			throw new CustomException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/soundex-test")
	public ResponseEntity<?> soundexTest() {
		Map<String,String> output = new HashMap<>();
		output.put("Sathish", Utility.soundex("Sathish"));
		output.put("Satish", Utility.soundex("Satish"));
		output.put("Kumar", Utility.soundex("Kumar"));
		output.put("kumaaar", Utility.soundex("kumaaar"));
		
		return ResponseEntity.ok(output);
	}
}
