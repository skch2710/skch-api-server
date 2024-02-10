package com.skch.skchhostelservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubNavigarion {
	
	private String resourceName;
	private String icon;
	private Long displayOrder;
	private List<Navigation> subNav;

}
