package com.skch.skchhostelservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skchhostelservice.model.Resource;

public interface ResourceDAO extends JpaRepository<Resource, Long> {

}
