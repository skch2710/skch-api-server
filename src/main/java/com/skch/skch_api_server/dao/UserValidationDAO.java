package com.skch.skch_api_server.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skch.skch_api_server.model.UserValidation;

public interface UserValidationDAO extends JpaRepository<UserValidation, Long> {

	UserValidation findByUuidTypeAndUuidLink(String type, String uuid);
	
//	boolean existsByCreatePwdUuidOrForgotPwdUuidOrResetPwdUuid(String firstUuid, String secondUuid, String thirdUuid);
//
//	@Query(value = "SELECT EXISTS (SELECT 1 FROM hostel.user_validation WHERE create_pwd_uuid = :uuid \r\n"
//			+ " OR forgot_pwd_uuid = :uuid OR reset_pwd_uuid = :uuid ) AS is_present;",nativeQuery = true)
//	boolean existByUuid(@Param("uuid") String uuid);
	
}
