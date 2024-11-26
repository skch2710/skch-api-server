package com.skch.skch_api_server.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skch_api_server.model.Resource;

public interface ResourceDAO extends JpaRepository<Resource, Long> {

}
