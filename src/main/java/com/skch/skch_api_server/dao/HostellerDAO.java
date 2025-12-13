package com.skch.skch_api_server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.skch.skch_api_server.model.Hosteller;

@Repository
public interface HostellerDAO extends JpaRepository<Hosteller, Long> {
	
	Hosteller findByHostellerId(Long hostellerId);
	
	List<Hosteller> findByFullNameStartingWithIgnoreCaseAndEmailIdStartingWithIgnoreCase(String fullName,String emailId);
	
	@Query(value = "SELECT json_build_object('min_dob', MIN(dob),'max_dob', MAX(dob)) AS dob_range FROM hostel.hostellers", nativeQuery = true)
	String getMaxMinDob();

}
