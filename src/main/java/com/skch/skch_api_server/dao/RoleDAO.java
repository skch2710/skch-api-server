package com.skch.skch_api_server.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skch_api_server.model.Roles;

public interface RoleDAO extends JpaRepository<Roles, Long> {

}
