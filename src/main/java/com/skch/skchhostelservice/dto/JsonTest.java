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
	private String firstName;

	@SerializedName(value = "email_id")
	private String emailId;

}
