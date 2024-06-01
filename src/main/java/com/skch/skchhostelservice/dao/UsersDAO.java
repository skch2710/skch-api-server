package com.skch.skchhostelservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.skch.skchhostelservice.model.Users;

public interface UsersDAO extends JpaRepository<Users, Long> {

	Users findByEmailIdIgnoreCase(String emailId);

	Users findByUserId(Long userId);

	Boolean existsByEmailIdIgnoreCase(String emailId);

	@Query(value = "SELECT user_id FROM hostel.users", nativeQuery = true)
	List<Long> getAllUserIds();
	
	Long countByIsActiveTrue();

}
