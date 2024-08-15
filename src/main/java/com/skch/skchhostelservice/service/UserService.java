package com.skch.skchhostelservice.service;

import java.io.ByteArrayOutputStream;

import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.dto.UserDTO;

public interface UserService {
	
	Result saveOrUpdateUser(UserDTO dto);
	
	Result navigations(Long userId);
	
	ByteArrayOutputStream getUserTemplate();

}
