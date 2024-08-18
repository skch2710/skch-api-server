package com.skch.skchhostelservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.skch.skchhostelservice.model.UploadFile;

public interface UploadFileDAO extends JpaRepository<UploadFile, Long> {

	@Transactional
	@Modifying(clearAutomatically = true)
    @Query(value = "CALL public.proc_validate_users_data(?1)", nativeQuery = true)
    void callUsersProc(Long uploadFileId);
	
}
