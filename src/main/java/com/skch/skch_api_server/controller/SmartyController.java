package com.skch.skch_api_server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skch.skch_api_server.dto.LookupDto;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.service.SmartyService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
//@RequestMapping("/api/v1/smarty")
@RequestMapping("/smarty")
//@SecurityRequirement(name = "bearerAuth")
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

}
