package com.skch.skch_api_server.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skch_api_server.model.UserRole;

public interface UserRoleDAO extends JpaRepository<UserRole, Long> {

}
