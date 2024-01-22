package com.skch.skchhostelservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Navigation {
	
	private String resourceName;
	private String icon;
	private String resourcePath;
	private Long displayOrder;

}
