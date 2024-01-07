package com.skch.skchhostelservice.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utility {

	public static String isBlank(String input) {
		return !input.isBlank() ? input : null;
	}
	
}
