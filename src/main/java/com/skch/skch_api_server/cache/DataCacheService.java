package com.skch.skch_api_server.cache;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.skch.skch_api_server.dao.RoleDAO;
import com.skch.skch_api_server.model.Roles;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataCacheService {

	private final RoleDAO roleDAO;
	
	@Cacheable(value = "rolesCache", key = "'allRoles'")
	public List<Roles> getAllRoles(){
		log.info(">>>Fetching roles from database in Cache Service...");
		try {
			return roleDAO.findAll();
		} catch (Exception e) {
			log.error("Error fetching roles from database: {}", e.getMessage());
			throw e; // throw the exception to be handled by the caller
		}
	}
	
}
