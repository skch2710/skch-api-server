package com.skch.skchhostelservice.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skch.skchhostelservice.dto.FileUploadDTO;
import com.skch.skchhostelservice.dto.HostellerDTO;
import com.skch.skchhostelservice.dto.HostellerSearch;
import com.skch.skchhostelservice.dto.PaymentHistoryDTO;
import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.exception.CustomException;
import com.skch.skchhostelservice.service.HostelService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/hostel")
//@RequestMapping("/hostel")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class HostelController {

	@Autowired
	private HostelService hostelService;
	
	@PostMapping("/save-update-hosteller")
//	@PreAuthorize("hasAnyAuthority('Super User')")
	public ResponseEntity<?> saveUpdateHosteller(@RequestBody HostellerDTO dto) {
		Result result = hostelService.saveOrUpdateHosteller(dto);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/save-update-payment")
//	@PreAuthorize("hasAnyAuthority('Super User')")
	public ResponseEntity<?> saveUpdatePayment(@RequestBody PaymentHistoryDTO dto) {
		Result result = hostelService.saveOrUpdatePaymentHistory(dto);
		return ResponseEntity.ok(result);
	}
	
//	@GetMapping("/get-hostellers")
////	@PreAuthorize("hasAnyAuthority('Super User')")
//	public ResponseEntity<?> getHostellers() {
//		Result result = hostelService.getHostellers();
//		return ResponseEntity.ok(result);
//	}
	
	@PostMapping("/get-hostellers")
//	@PreAuthorize("hasAnyAuthority(@jwtUtil.SUPER_USER)")
	@PreAuthorize("hasAuthority(@jwtUtil.SUPER_USER)")
//	@PreAuthorize("@jwtUtil.checkAccess('Super User')")
//	@PreAuthorize("@jwtUtil.checkAccess(@jwtUtil.SUPER_USER)")
	public ResponseEntity<?> getHostellers(@RequestBody HostellerSearch search){
		try {
			if (!search.isExportExcel() && !search.isExportPdf() && !search.isExportZip()) {
				Result result = hostelService.getHostellers(search);
				return ResponseEntity.ok(result);
			}else {
				Result result = hostelService.getHostellers(search);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(result.getType());
				headers.setContentDispositionFormData("attachment", result.getFileName());
				InputStreamResource inputStreamResource = new InputStreamResource(
						new ByteArrayInputStream(result.getBao().toByteArray()));
				result.getBao().flush();// Flush the output stream
				return ResponseEntity.ok().headers(headers).body(inputStreamResource);
			}
		} catch (Exception e) {
			log.error("error in getHostellers Controller :: ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//SpEL parameter Not Working #search.fullName

	/**
	* This Method is validate the file and Upload
	*
	* @param file
	* @param dto
	* @return result
	*/
	@PostMapping(path = "/upload-file", consumes = "multipart/form-data")
	public ResponseEntity<?> uploadFile(@RequestPart(required = true, name = "file") MultipartFile file,
			@RequestPart(required = false, name = "dto") FileUploadDTO dto) {
		Result result = hostelService.uploadFile(file);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/hostel-template")
	public ResponseEntity<?> getHostelTemplate() {
		try {
			ByteArrayOutputStream bao = hostelService.getHostelTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", "Hostel_Template.xlsx");
			InputStreamResource inputStreamResource = new InputStreamResource(
					new ByteArrayInputStream(bao.toByteArray()));
			bao.flush();// Flush the output stream
			return ResponseEntity.ok().headers(headers).body(inputStreamResource);
		} catch (Exception e) {
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
