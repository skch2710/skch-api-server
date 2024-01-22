package com.skch.skchhostelservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skchhostelservice.model.UserRole;

public interface UserRoleDAO extends JpaRepository<UserRole, Long> {

}
