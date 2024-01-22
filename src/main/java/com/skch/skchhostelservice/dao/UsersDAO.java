package com.skch.skchhostelservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skchhostelservice.model.Users;

public interface UsersDAO extends JpaRepository<Users, Long> {

	Users findByEmailIdIgnoreCase(String emailId);
	
	Users findByUserId(Long userId);

}
