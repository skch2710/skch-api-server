package com.skch.skch_api_server.service;

import java.util.List;

import com.skch.skch_api_server.dto.LookupDto;
import com.skch.skch_api_server.dto.Result;

public interface SmartyService {
	
	public Result getSingleRequest(LookupDto dto);
	
	public Result getBulkRequest(List<LookupDto> dtoList);

}
