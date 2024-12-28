package com.skch.skch_api_server.dto;

import java.time.LocalDate;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonTest {

	@SerializedName(value = "first_name")
	private String firstName = "";

	@SerializedName(value = "email_id")
	private String emailId = "";
	
	@SerializedName(value = "min_dob")
	private LocalDate minDob;
	
	@SerializedName(value = "max_dob")
	private LocalDate maxDob;
	
	// Custom setter for firstName
    public void setFirstName(String firstName) {
        this.firstName = (firstName != null) ? firstName : "";
    }

    // Custom setter for emailId
    public void setEmailId(String emailId) {
        this.emailId = (emailId != null) ? emailId : "";
    }

}
