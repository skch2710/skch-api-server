package com.skch.skch_api_server.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skch.skch_api_server.dto.FileUploadDTO;
import com.skch.skch_api_server.dto.ProfileRequest;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.dto.UserDTO;
import com.skch.skch_api_server.exception.CustomException;
import com.skch.skch_api_server.service.UserService;
import com.skch.skch_api_server.util.AESUtils;
import com.skch.skch_api_server.util.EmailSender;
import com.skch.skch_api_server.util.JwtUtil;

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
	
	@PostMapping("/profile")
//	@PreAuthorize("hasAnyAuthority('User-R')")
	@PreAuthorize("hasAuthority('User-R') and @jwtUtil.checkProfileMatch(#p0.getEmailId())")
	@Operation(summary="Get User Profile",description = "Return the User Profile based on EmailId")
	public ResponseEntity<?> profile(@RequestBody ProfileRequest request) {
//		if(!JwtUtil.checkProfileMatch(request.getEmailId())) {
//			throw new CustomException("Access Denied to fetch other user profile",HttpStatus.FORBIDDEN);
//		}
		Result result = userService.profile(request);
		return ResponseEntity.ok(result);
	}

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
	@PreAuthorize("hasAnyAuthority('User-R')")
	@Operation(summary="get Navigations",description = "Return the Navigations based on User")
	public ResponseEntity<?> getNav(@PathVariable("userId") Long userId){
		Result result = userService.navigations(userId);
		System.out.println(JwtUtil.getUserId());
		log.info("Result :: {}",result);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/nav-two")
	@Operation(summary="get Navigations",description = "Return the Navigations based on User")
	public ResponseEntity<?> getNavTwo(@RequestParam("userId") String userId){
//		Result result = userService.navigations(userId);
		System.out.println(JwtUtil.getUserId());
		return ResponseEntity.ok("Access");
	}
	
	/**
	 * Get the User Bulk Upload Template
	 * @return
	 */
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
	
	/**
	* This Method is validate the file and Upload
	*
	* @param file
	* @param dto
	* @return result
	*/
	@PostMapping(path = "/upload-user-file", consumes = "multipart/form-data")
	public ResponseEntity<?> uploadFile(@RequestPart(required = true, name = "file") MultipartFile file,
			@RequestPart(required = false, name = "dto") FileUploadDTO dto) {
		Result result = userService.uploadUserFile(file,dto);
		return ResponseEntity.ok(result);
	}
	
	@Autowired
	private EmailSender emailSender;
	
	@GetMapping("/user-mail")
	public ResponseEntity<?> getUserMail() throws UnsupportedEncodingException {

		Map<String, Object> mapModel = new HashMap<>();
		mapModel.put("toMail", "temp2710@outlook.com");
		mapModel.put("htmlFile", "create-password.ftlh");
		mapModel.put("subject", "Create Password");

		String uuid = UUID.randomUUID().toString().split("-")[0];
		Long timeMilli = System.currentTimeMillis();
		System.out.println(uuid);
		
		String link = "https://localhost:8080/createPassword?uuid=" + AESUtils.encrypt(uuid + "#" + timeMilli);
//        String encodedLink = URLEncoder.encode(link, StandardCharsets.UTF_8.name());
		mapModel.put("createPasswordLink", link);

		emailSender.sendEmail(mapModel);
		
		return ResponseEntity.ok("Mail Sent.");
	}
}
