package com.skch.skch_api_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LookupDto {

	private String street;
	private String secondary;
    private String city;
    private String state;
    private String zipCode;

}
