package com.skch.skchhostelservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skchhostelservice.model.Hosteller;

public interface HostellerDAO extends JpaRepository<Hosteller, Long> {
	
	Hosteller findByHostellerId(Long hostellerId);
	
	List<Hosteller> findByFullNameStartingWithIgnoreCaseAndEmailIdStartingWithIgnoreCase(String fullName,String emailId);

}
