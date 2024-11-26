package com.skch.skch_api_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Navigation {

	private Long resourceId;
	private String resourceName;
	private String icon;
	private String resourcePath;
	private Long displayOrder;

}
