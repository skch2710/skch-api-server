package com.skch.skchhostelservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skchhostelservice.model.UserPrivilege;

public interface UserPrivilegeDAO extends JpaRepository<UserPrivilege, Long> {

}
