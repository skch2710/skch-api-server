package com.skch.skchhostelservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrivilegeDTO {

	private Long userPrivilegesId;

	private Long resourceId;

	private Boolean readOnlyFlag;

	private Boolean readWriteFlag;

	private Boolean terminateFlag;

}
