package com.skch.skchhostelservice.dto;

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
	
	// Custom setter for firstName
    public void setFirstName(String firstName) {
        this.firstName = (firstName != null) ? firstName : "";
    }

    // Custom setter for emailId
    public void setEmailId(String emailId) {
        this.emailId = (emailId != null) ? emailId : "";
    }

}
