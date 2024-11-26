package com.skch.skch_api_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/test")
//@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
public class TestController {
	
	@GetMapping("/test")
	@Operation(summary="get Navigations",description = "Return the Navigations based on User")
	public ResponseEntity<?> getNavTwo(@RequestParam("userId") String userId){
//		Result result = userService.navigations(userId);
//		System.out.println(JwtUtil.getUserId());
		return ResponseEntity.ok("Access :: "+userId);
	}
	

}
