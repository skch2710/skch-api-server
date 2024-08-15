package com.skch.skchhostelservice.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.dto.UserDTO;
import com.skch.skchhostelservice.exception.CustomException;
import com.skch.skchhostelservice.service.UserService;
import com.skch.skchhostelservice.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/user")
//@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
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
		System.out.println(JwtUtil.getUserId());
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/user-template")
	public ResponseEntity<?> getUserTemplate() {
		try {
			ByteArrayOutputStream bao = userService.getUserTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", "User_Template.xlsx");
			InputStreamResource inputStreamResource = new InputStreamResource(
					new ByteArrayInputStream(bao.toByteArray()));
			bao.flush();// Flush the output stream
			return ResponseEntity.ok().headers(headers).body(inputStreamResource);
		} catch (Exception e) {
			log.error("Error in getUserTemplate Controller :: ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
