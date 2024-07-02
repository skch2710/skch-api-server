package com.skch.skchhostelservice.dto;

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
	private String joiningDate;
	private String address;
	private String proof;
	private String reason;
	private String vacatedDate;
	private Boolean active;
	private Long totalCount;

}
