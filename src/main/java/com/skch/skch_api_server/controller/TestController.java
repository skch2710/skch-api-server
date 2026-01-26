package com.skch.skch_api_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skch.skch_api_server.dto.JwtDTO;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.util.ApiCalling;
import com.skch.skch_api_server.util.RetryUtil;

import io.swagger.v3.oas.annotations.Operation;

@RestController
//@RequestMapping("/api/v1/test")
@RequestMapping("/test")
//@SecurityRequirement(name = "bearerAuth")
public class TestController {
	
	@Autowired
	private RetryUtil retryUtil;
	
	@Autowired
	private ApiCalling apiCalling;
	
	@GetMapping("/test/{resource}")
	@Operation(summary="get Navigations",description = "Return the Navigations based on User")
//	@PreAuthorize("hasAnyAuthority('Super User')")
//	@PreAuthorize("@jwtUtil.checkAccess(#p0)")
//	@PreAuthorize("hasAuthority(#p0)")
	public ResponseEntity<?> getNavTwo(@PathVariable("resource") String resource){
		retryUtil.getRetry();
		return ResponseEntity.ok("Access :: "+resource);
	}
	
	@PostMapping("/test-post")
	@Operation(summary="get Navigations",description = "Return the Navigations based on User")
//	@PreAuthorize("hasAnyAuthority(#p0.getResource())")
//	@PreAuthorize("@jwtUtil.checkAccess(#p0.getResource())")
	public ResponseEntity<?> getNav(@RequestBody JwtDTO jwtDTO){
		
		Result result = apiCalling.data();
		
		return ResponseEntity.ok(result);
	}
	
	

}
