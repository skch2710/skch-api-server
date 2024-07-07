package com.skch.skchhostelservice.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.skch.skchhostelservice.model.Users;
import com.skch.skchhostelservice.util.Utility;

public interface UsersDAO extends JpaRepository<Users, Long> {

	Users findByEmailIdIgnoreCase(String emailId);

	Users findByUserId(Long userId);

	Boolean existsByEmailIdIgnoreCase(String emailId);

	@Query(value = "SELECT user_id FROM hostel.users", nativeQuery = true)
	List<Long> getAllUserIds();
	
	Long countByIsActiveTrue();
	
	@Query(value = "SELECT json_object_agg(up.resource_id, r.resource_name) AS data_json\r\n"
			+ "FROM hostel.user_privileges up \r\n"
			+ "JOIN hostel.resource r ON up.resource_id = r.resource_id\r\n"
			+ "WHERE up.user_id =1;", nativeQuery = true)
	String getUserPrivilegesJson();
	
	default Map<Long, String> getUserPrivilegesMap() {
        return Utility.parseJsonToMap(getUserPrivilegesJson(), Long.class, String.class);
    }
	
	@Query(value = "SELECT json_object_agg(hosteller_id,email_id) AS data_json\r\n"
			+ "FROM hostel.hostellers;", nativeQuery = true)
	Page<String> getUserPrivilegesJson(Pageable pageable);

}
