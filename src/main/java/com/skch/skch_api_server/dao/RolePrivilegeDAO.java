package com.skch.skch_api_server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skch_api_server.model.RolePrivilege;

public interface RolePrivilegeDAO extends JpaRepository<RolePrivilege, Long> {
	
	List<RolePrivilege> findByRoles_RoleId(Long rleId);

}
