package com.skch.skchhostelservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.dto.UserDTO;
import com.skch.skchhostelservice.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/save-update-user")
//	@PreAuthorize("hasAnyAuthority('Super User')")
	public ResponseEntity<?> saveUpdateHosteller(@RequestBody UserDTO dto) {
		Result result = userService.saveOrUpdateUser(dto);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/nav/{userId}")
	public ResponseEntity<?> getNav(@PathVariable("userId") Long userId){
		Result result = userService.navigations(userId);
		return ResponseEntity.ok(result);
	}
}
