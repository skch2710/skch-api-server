package com.skch.skch_api_server.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Result extends FileDetails {

	private int statusCode;
	private String successMessage;
	private String errorMessage;
	private Object data;

	public Result(Object data) {
		this.data = data;
	}
}
