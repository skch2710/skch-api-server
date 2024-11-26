package com.skch.skch_api_server.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersFileDTO {

	@NotEmpty(message = "Email cannot be null")
	@Email(message = "Email should be valid")
	private String emailId;

	@NotEmpty(message = "First Name cannot be null")
	@Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "First Name Not a Valid")
	private String firstName;

	@NotEmpty(message = "Last Name cannot be null")
	@Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Last Name Not a Valid")
	private String lastName;

	@Pattern(regexp = "^[0-9]*$", message = "Phone number is not valid")
	private String phoneNumber;

	@NotEmpty(message = "DOB cannot be null or Empty")
	@Pattern(regexp = "^(0[1-9]|[12]\\d|3[01])(0[1-9]|1[0-2])\\d{4}$", message = "DOB(ddMMyyyy) is not valid")
	private String dob;

	@NotEmpty(message = "Role Name cannot be null")
	@Pattern(regexp = "^(Super User|Admin)$", message = "Role Name must be 'Super User' or 'Admin'")
	private String roleName;

	@NotEmpty(message = "Active cannot be null")
	@Pattern(regexp = "^(Yes|No)$", message = "Active must be 'Yes' or 'No'")
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
