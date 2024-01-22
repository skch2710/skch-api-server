package com.skch.skchhostelservice.dto;

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

	private Long createdById;

	private Long modifiedById;

}
