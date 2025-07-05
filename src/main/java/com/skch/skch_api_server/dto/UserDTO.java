package com.skch.skch_api_server.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private Long userId;

	private String emailId;

	private String firstName;

	private String lastName;

	private String pwdSalt;

	private String phoneNumber;

	private String dob;

	private Long roleId;

	private List<UserPrivilegeDTO> userPrivilege;
	
	private BigDecimal salary;
	
	private String status;
	
	private String typeOfUser;
	
	private String place;

	public UserDTO(String firstName, String lastName,String emailId,Long roleId, BigDecimal salary) {
		this.emailId = emailId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.roleId = roleId;
		this.salary = salary;
	}

//	private Long createdById;
//
//	private Long modifiedById;
	
	

}
