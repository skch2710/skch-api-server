package com.skch.skchhostelservice.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utility {

	public static String isBlank(String input) {
		return !input.isBlank() ? input : null;
	}
	
	public static LocalDateTime dateConverts(String input){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
		LocalDateTime dateTime = LocalDateTime.parse(input, formatter);
		return dateTime;
	}
	
	public static LocalDate dateConvert(String input){
		LocalDate date = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			date = LocalDate.parse(input, formatter);
		} catch (Exception e) {
			log.error("Error in convert Date :: "+e);
		}
		return date;
	}
	
}
