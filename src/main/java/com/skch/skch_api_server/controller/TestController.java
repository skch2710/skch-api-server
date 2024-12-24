package com.skch.skch_api_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skch.skch_api_server.dto.JwtDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/test")
//@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
public class TestController {
	
	@GetMapping("/test/{resource}")
	@Operation(summary="get Navigations",description = "Return the Navigations based on User")
//	@PreAuthorize("hasAnyAuthority('Super User')")
//	@PreAuthorize("@jwtUtil.checkAccess(#p0)")
	@PreAuthorize("hasAuthority(#p0)")
	public ResponseEntity<?> getNavTwo(@PathVariable("resource") String resource){
		return ResponseEntity.ok("Access :: "+resource);
	}
	
	@PostMapping("/test-post")
	@Operation(summary="get Navigations",description = "Return the Navigations based on User")
	@PreAuthorize("hasAnyAuthority(#p0.getResource())")
//	@PreAuthorize("@jwtUtil.checkAccess(#p0.getResource())")
	public ResponseEntity<?> getNav(@RequestBody JwtDTO jwtDTO){
		return ResponseEntity.ok("Access :: "+jwtDTO);
	}
	

}
