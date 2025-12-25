package com.skch.skch_api_server.controller;

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

import com.skch.skch_api_server.dao.HostellerDAO;
import com.skch.skch_api_server.dto.FileUploadDTO;
import com.skch.skch_api_server.dto.HostellerDTO;
import com.skch.skch_api_server.dto.HostellerInactive;
import com.skch.skch_api_server.dto.HostellerSearch;
import com.skch.skch_api_server.dto.JsonTest;
import com.skch.skch_api_server.dto.PaymentHistoryDTO;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.exception.CustomException;
import com.skch.skch_api_server.service.HostelService;
import com.skch.skch_api_server.util.Utility;

import io.swagger.v3.oas.annotations.Operation;
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
	@PreAuthorize("hasAnyAuthority('Hostellers-W')")
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
	
	@Autowired
	private HostellerDAO hostellerDAO;
	
	@GetMapping("/test-hostellers-json")
	public ResponseEntity<?> getTestHostellersJson() {
		String dataJson = hostellerDAO.getMaxMinDob();
		JsonTest jsonTest = Utility.fromJson(dataJson, JsonTest.class);
		log.info("Min DOB :: {} , Max DOB :: {} ",jsonTest.getMinDob(),jsonTest.getMaxDob());
		return ResponseEntity.ok(jsonTest);
	}
	
	@PostMapping("/get-hostellers")
//	@PreAuthorize("hasAnyAuthority(@jwtUtil.SUPER_USER)")
//	@PreAuthorize("hasAuthority(@jwtUtil.SUPER_USER)")
//	@PreAuthorize("@jwtUtil.checkAccess('Super User')")
//	@PreAuthorize("@jwtUtil.checkAccess(@jwtUtil.SUPER_USER)")
//	@PreAuthorize("@jwtUtil.checkAccess(#search.fullName)")
	public ResponseEntity<?> getHostellers(@RequestBody HostellerSearch search){
		try {
			Result result = hostelService.getHostellers(search);
			if (!search.isExportExcel() && !search.isExportCsv() &&
					!search.isExportPdf() && !search.isExportZip()) {
				return ResponseEntity.ok(result);
			}else {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(result.getType());
				headers.setContentDispositionFormData("attachment", result.getFileName());
				headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
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
		System.out.println("File Name :: " + dto.isValidation());
		Result result = hostelService.uploadFile(file,dto);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/hostel-template")
	public ResponseEntity<?> getHostelTemplate() {
		try {
			ByteArrayOutputStream bao = hostelService.getHostelTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", "Hostel_Template.xlsx");
			headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
			InputStreamResource inputStreamResource = new InputStreamResource(
					new ByteArrayInputStream(bao.toByteArray()));
			bao.flush();// Flush the output stream
			return ResponseEntity.ok().headers(headers).body(inputStreamResource);
		} catch (Exception e) {
			log.error("Error in getHostelTemplate Controller :: ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Inactive Hosteller
	 * @param dto
	 * @return result
	 */
	@PostMapping("/inactive-hosteller")
	@PreAuthorize("hasAnyAuthority('Hostellers-X')")
	@Operation(summary = "Inactive Hosteller", description = "Inactive Hosteller")
	public ResponseEntity<?> inactiveHosteller(@RequestBody HostellerInactive dto) {
		Result result = hostelService.inactiveHosteller(dto);
		return ResponseEntity.ok(result);
	}
	
}
