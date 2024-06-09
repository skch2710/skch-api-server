package com.skch.skchhostelservice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.Date;

public class DateUtil {
	
	/**
	 * FORMATS 
	 * 
	 * yyyy-MM-dd HH:mm:ss a
	 * yyyy-MM-dd
	 * MM/dd/yyyy
	 * MMddyyyy
	 * ddMMyyyyHHmmss
	 * MM.dd
	 * HH:mm a MMMM dd, yyyy  -- 15:05
	 * hh:mm a MMMM dd, yyyy  -- 03:05
	 * h:mm a MMM d, yyyy    
	 * MMM d, yyyy
	 * MMM dd, yyyy
	 * MMMM dd, yyyy
	 * yyyy-MM-dd h:mm a -- 2023-08-01 7:50 AM 
	 */

	public static String dateToString(LocalDateTime date, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return date.format(formatter);
	}

	public static String dateToString(LocalDate date, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return date.format(formatter);
	}
	
	public static LocalDate stringToDate(String date,String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDate.parse(date,formatter);
	}
	
	public static LocalDateTime stringToDateTime(String date,String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.parse(date,formatter);
	}
	
	public static String dateToString(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	public static Date stringToDates(String date, String format) {
		Date output = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			output = formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	public static LocalDate toLocalDate(Date date) {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return localDate;
	}
	
	public static LocalDateTime toLocalDateTime(Date date) {
		LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		return localDate;
	}
	
	public static Date toDate(LocalDate localDate) {
		Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return date;
	}
	
	public static Date toDate(LocalDateTime localDateTime) {
		Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		return date;
	}
	
	public static LocalDate getMQ(LocalDate input, String type) {
		LocalDate result = null;
		if (type.equals("M")) {
			result = input.plusMonths(1).withDayOfMonth(14);
		} else if (type.equals("Q")) {
			int quater = input.get(IsoFields.QUARTER_OF_YEAR);
			if (quater == 4) {
				result = input.plusYears(1).withMonth(1).withDayOfMonth(14);
			} else {
				result = input.withMonth((quater * 3) + 1).withDayOfMonth(14);
			}
		}
		return result;
	}
}
