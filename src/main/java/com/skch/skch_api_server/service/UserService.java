package com.skch.skch_api_server.service;

import java.io.ByteArrayOutputStream;

import org.springframework.web.multipart.MultipartFile;

import com.skch.skch_api_server.dto.FileUploadDTO;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.dto.UserDTO;

public interface UserService {
	
	Result saveOrUpdateUser(UserDTO dto);
	
	Result navigations(Long userId);
	
	ByteArrayOutputStream getUserTemplate();
	
	Result uploadUserFile(MultipartFile file,FileUploadDTO dto);

}
