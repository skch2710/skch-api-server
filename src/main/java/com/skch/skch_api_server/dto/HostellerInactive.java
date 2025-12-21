package com.skch.skch_api_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostellerInactive {
	
	private Long hostellerId;
	private String reason;
	private String emailId;

}
