package com.skch.skchhostelservice.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skch.skchhostelservice.model.Users;
import com.skch.skchhostelservice.util.Utility;

public interface UsersDAO extends JpaRepository<Users, Long> {

	Users findByEmailIdIgnoreCase(String emailId);

	Users findByUserId(Long userId);

	Boolean existsByEmailIdIgnoreCase(String emailId);

	@Query(value = "SELECT user_id FROM hostel.users", nativeQuery = true)
	List<Long> getAllUserIds();
	
	Long countByIsActiveTrue();
	
	@Query(value = "SELECT json_object_agg(lower(email_id),user_id) AS mail_data FROM hostel.users \r\n"
			+ "	WHERE email_id ILIKE ANY (:emailList);", nativeQuery = true)
	String getExistUsers(@Param("emailList") String[] emailList);
	
	default Map<String, Long> getExistUsersMap(String[] emailList) {
        return Utility.parseJsonToMap(getExistUsers(emailList), String.class, Long.class);
    }
	
	@Query(value = "SELECT json_object_agg(hosteller_id,email_id) AS data_json\r\n"
			+ "FROM hostel.hostellers;", nativeQuery = true)
	Page<String> getUserPrivilegesJson(Pageable pageable);
	
	@Query(value = "SELECT hosteller_id,email_id \r\n"
			+ "FROM hostel.hostellers limit 1;", nativeQuery = true)
	Object findByTest(String emailId);
	
	@Query(value = "SELECT email_id FROM hostel.users order by email_id asc", nativeQuery = true)
	String[] findAllEmailId();

}
