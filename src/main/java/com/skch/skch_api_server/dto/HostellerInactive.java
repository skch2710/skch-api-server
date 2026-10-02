package com.skch.skch_api_server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostellerInactive {

	@NotNull(message = "hostellerId cannot be null", groups = { HostellerInactiveGroup.class })
	@NotBlank(message = "hostellerId cannot be blank")
	private Long hostellerId;

	@NotBlank(message = "reason cannot be null")
	private String reason;

	@Email(message = "emailId should be a valid email address")
	private String emailId;

}
