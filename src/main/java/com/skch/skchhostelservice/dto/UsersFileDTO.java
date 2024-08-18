package com.skch.skchhostelservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersFileDTO {

	private String emailId;

	private String firstName;

	private String lastName;

	private String phoneNumber;

	private String dob;

	private String roleName;

	private String active;
	
	private String status;

	private String errorMessage;
	
	private Long uploadFileId;
	
	public UsersFileDTO(List<String> cellValues) {
		this.firstName = cellValues.get(0);
		this.lastName = cellValues.get(1);
		this.emailId = cellValues.get(2);
		this.phoneNumber = cellValues.get(3);
		this.dob = cellValues.get(4);
		this.roleName = cellValues.get(5);
		this.active = cellValues.get(6);
	}

}
