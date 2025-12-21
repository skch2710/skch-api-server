package com.skch.skch_api_server.service;

import java.io.ByteArrayOutputStream;

import org.springframework.web.multipart.MultipartFile;

import com.skch.skch_api_server.dto.HostellerDTO;
import com.skch.skch_api_server.dto.HostellerInactive;
import com.skch.skch_api_server.dto.HostellerSearch;
import com.skch.skch_api_server.dto.PaymentHistoryDTO;
import com.skch.skch_api_server.dto.Result;

public interface HostelService {
	
	Result saveOrUpdateHosteller(HostellerDTO dto);
	
	Result saveOrUpdatePaymentHistory(PaymentHistoryDTO dto);
	
	Result getHostellers(HostellerSearch search);
	
	Result uploadFile(MultipartFile file);
	
	ByteArrayOutputStream getHostelTemplate();

	Result inactiveHosteller(HostellerInactive dto);
}
