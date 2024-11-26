package com.skch.skch_api_server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skch_api_server.model.UserPrivilege;

public interface UserPrivilegeDAO extends JpaRepository<UserPrivilege, Long> {
	
	List<UserPrivilege> findByUsers_EmailId(String emailId);

}
