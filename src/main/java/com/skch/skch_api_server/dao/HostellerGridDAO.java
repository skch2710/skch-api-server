package com.skch.skch_api_server.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.skch.skch_api_server.model.HostellerGrid;

import jakarta.persistence.QueryHint;

public interface HostellerGridDAO extends JpaRepository<HostellerGrid, Long> {

	@Query(value = "SELECT * FROM public.fn_get_hostellers(:fullName, :emailId, :sortBy, :sortOrder,"
			+ " :pageNamber, :pageSize, :export, :clfFullName);", nativeQuery = true)
	@QueryHints({
	    @QueryHint(name = "org.hibernate.readOnly", value = "true"),
	    @QueryHint(name = "org.hibernate.fetchSize", value = "25"),
	    @QueryHint(name = "org.hibernate.cacheable", value = "true"),
	    @QueryHint(name = "jakarta.persistence.cache.retrieveMode", value = "USE"),
	    @QueryHint(name = "jakarta.persistence.cache.storeMode", value = "USE")
	    // @QueryHint(name = "jakarta.persistence.query.timeout", value = "2000")
	})
	List<HostellerGrid> getHostelData(@Param("fullName") String fullName, 
			@Param("emailId") String emailId,
			@Param("sortBy") String sortBy, @Param("sortOrder") String sortOrder,
			@Param("pageNamber") Integer pageNamber, @Param("pageSize") Integer pageSize, 
			@Param("export") Boolean export,
			@Param("clfFullName") String clfFullName);

}
