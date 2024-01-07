package com.skch.skchhostelservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skchhostelservice.model.Hosteller;

public interface HostellerDAO extends JpaRepository<Hosteller, Long> {
	
	Hosteller findByHostellerId(Long hostellerId);

}
