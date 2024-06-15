package com.skch.skchhostelservice.service;

import org.springframework.web.multipart.MultipartFile;

import com.skch.skchhostelservice.dto.HostellerDTO;
import com.skch.skchhostelservice.dto.HostellerSearch;
import com.skch.skchhostelservice.dto.PaymentHistoryDTO;
import com.skch.skchhostelservice.dto.Result;

public interface HostelService {
	
	Result saveOrUpdateHosteller(HostellerDTO dto);
	
	Result saveOrUpdatePaymentHistory(PaymentHistoryDTO dto);
	
	Result getHostellers(HostellerSearch search);
	
	Result uploadFile(MultipartFile file);

}
