package com.skch.skchhostelservice.service;

import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.dto.UserDTO;

public interface UserService {
	
	Result saveOrUpdateUser(UserDTO dto);
	
	Result navigations(Long userId);

}
