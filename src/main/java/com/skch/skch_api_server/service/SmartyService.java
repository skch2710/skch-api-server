package com.skch.skch_api_server.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.skch.skch_api_server.dto.LookupDto;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.dto.SmartyFileUploadDTO;

public interface SmartyService {
	
	public Result getSingleRequest(LookupDto dto);
	
	public Result getBulkRequest(List<LookupDto> dtoList);
	
	public Result uploadSmartyFile(MultipartFile file, SmartyFileUploadDTO dto);

}
