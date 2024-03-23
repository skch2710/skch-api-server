package com.skch.skchhostelservice.service;

import com.skch.skchhostelservice.dto.HostellerDTO;
import com.skch.skchhostelservice.dto.PaymentHistoryDTO;
import com.skch.skchhostelservice.dto.Result;

public interface HostelService {
	
	Result saveOrUpdateHosteller(HostellerDTO dto);
	
	Result saveOrUpdatePaymentHistory(PaymentHistoryDTO dto);
	
	Result getHostellers();

}
