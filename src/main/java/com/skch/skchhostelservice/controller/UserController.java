package com.skch.skchhostelservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.dto.UserDTO;
import com.skch.skchhostelservice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
//@RequestMapping("/api/v1/user")
@RequestMapping("/user")
//@SecurityRequirement(name = "bearerAuth")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/save-update-user")
//	@PreAuthorize("hasAnyAuthority('Super User','Admin')")
//	@PreAuthorize("hasAuthority(#accesId + '-User-R') or hasAuthority('User-R')")
//	@PreAuthorize("hasAuthority(#object.accesId + '-User-R') or hasAuthority('User-R')")
//	@PreAuthorize("hasAuthority(#accesId[0] + '-User-R') or hasAuthority('User-R')")
//	@PreAuthorize("hasAuthority(#object.accesId[0] + '-User-R') or hasAuthority('User-R')")
	@Operation(summary="Save or Update User",description = "Save or Update the User")
	public ResponseEntity<?> saveOrUpdateUser(@RequestBody UserDTO dto) {
		Result result = userService.saveOrUpdateUser(dto);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/nav/{userId}")
//	@PreAuthorize("hasAnyAuthority('Super User','Admin')")
//	@PreAuthorize("hasAnyAuthority('User-R')")
	@Operation(summary="get Navigations",description = "Return the Navigations based on User")
	public ResponseEntity<?> getNav(@PathVariable("userId") Long userId){
		Result result = userService.navigations(userId);
		return ResponseEntity.ok(result);
	}
}
