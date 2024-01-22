package com.skch.skchhostelservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skchhostelservice.model.Roles;

public interface RoleDAO extends JpaRepository<Roles, Long> {

}
