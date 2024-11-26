package com.skch.skch_api_server.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostellerGridDTO {

	private Long hostellerId;
	private String fullName;
	private String emailId;
	private String phoneNumber;
	private String dob;
	private String fee;
	@JsonFormat(pattern = "MM/dd/yyyy")
	private LocalDate joiningDate;
	private String address;
	private String proof;
	private String reason;
	private String vacatedDate;
	private Boolean active;
	private Long totalCount;

}
