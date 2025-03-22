package com.skch.skch_api_server.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skch.skch_api_server.dto.LookupDto;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.dto.SmartyFileUploadDTO;
import com.skch.skch_api_server.exception.CustomException;
import com.skch.skch_api_server.service.SmartyService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
//@RequestMapping("/api/v1/smarty")
@RequestMapping("/smarty")
//@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class SmartyController {
	
	@Autowired
	private SmartyService smartyService;
	
	@PostMapping("/single-request")
	@Operation(summary="single request",description = "single request")
	public ResponseEntity<Result> singleRequest(@RequestBody LookupDto dto) {
		Result result = smartyService.getSingleRequest(dto);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/bulk-request")
	@Operation(summary="bulk request",description = "bulk request")
	public ResponseEntity<Result> bulkRequest(@RequestBody List<LookupDto> dto) {
		Result result = smartyService.getBulkRequest(dto);
		return ResponseEntity.ok(result);
	}
	
	/**
	 * Returns the Smarty template based on the specified file type.
	 * 
	 * @param fileType the type of file to generate ("Excel" or "CSV")
	 * @return the generated template as a Result object
	 */
	@GetMapping("/smarty-template/{fileType}")
	public ResponseEntity<InputStreamResource> getSmartyTemplate(@PathVariable("fileType") String fileType) {
		try {
			Result result = smartyService.getSmartyTemplate(fileType);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(result.getType());
			headers.setContentDispositionFormData("attachment", result.getFileName());
			headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
			InputStreamResource inputStreamResource = new InputStreamResource(
					new ByteArrayInputStream(result.getBao().toByteArray()));
			result.getBao().flush();// Flush the output stream
			return ResponseEntity.ok().headers(headers).body(inputStreamResource);
		} catch (Exception e) {
			log.error("Error in getSmartyTemplate Controller :: ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	* This Method is validate the file and Send the Request to Smarty
	*
	* @param file
	* @param dto
	* @return result
	*/
	@PostMapping(path = "/upload-smartys-file", consumes = "multipart/form-data")
	public ResponseEntity<?> uploadFile(@RequestPart(required = true, name = "file") MultipartFile file,
			@RequestPart(required = false, name = "dto") SmartyFileUploadDTO dto) {
		Result result = smartyService.uploadSmartyFile(file,dto);
		return ResponseEntity.ok(result);
	}

}
