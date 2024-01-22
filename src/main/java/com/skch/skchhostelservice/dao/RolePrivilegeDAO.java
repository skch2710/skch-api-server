package com.skch.skchhostelservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skchhostelservice.model.RolePrivilege;

public interface RolePrivilegeDAO extends JpaRepository<RolePrivilege, Long> {
	
	List<RolePrivilege> findByRoles_RoleId(Long rleId);

}
