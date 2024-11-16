package com.skch.skchhostelservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skchhostelservice.model.UserPrivilege;

public interface UserPrivilegeDAO extends JpaRepository<UserPrivilege, Long> {
	
	List<UserPrivilege> findByUsers_EmailId(String emailId);

}
