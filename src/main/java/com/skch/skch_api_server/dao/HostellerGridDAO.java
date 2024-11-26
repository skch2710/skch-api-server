package com.skch.skch_api_server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skch.skch_api_server.model.HostellerGrid;

public interface HostellerGridDAO extends JpaRepository<HostellerGrid, Long> {

	@Query(value = "SELECT * FROM public.fn_get_hostellers(:fullName, :emailId, :sortBy, :sortOrder,"
			+ " :pageNamber, :pageSize, :export, :clfFullName);", nativeQuery = true)
	List<HostellerGrid> getHostelData(@Param("fullName") String fullName, 
			@Param("emailId") String emailId,
			@Param("sortBy") String sortBy, @Param("sortOrder") String sortOrder,
			@Param("pageNamber") Integer pageNamber, @Param("pageSize") Integer pageSize, 
			@Param("export") Boolean export,
			@Param("clfFullName") String clfFullName);

}
