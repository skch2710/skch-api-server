package com.skch.skchhostelservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skch.skchhostelservice.dto.HostellerDTO;
import com.skch.skchhostelservice.dto.HostellerSearch;
import com.skch.skchhostelservice.dto.PaymentHistoryDTO;
import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.service.HostelService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
//@RequestMapping("/api/v1/hostel")
@RequestMapping("/hostel")
//@SecurityRequirement(name = "bearerAuth")
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
//	@PreAuthorize("hasAnyAuthority('Super User')")
	public ResponseEntity<?> getHostellers(@RequestBody HostellerSearch search) throws InterruptedException {
		Result result = hostelService.getHostellers(search);
//		Thread.sleep(5000);
		return ResponseEntity.ok(result);
	}

}
