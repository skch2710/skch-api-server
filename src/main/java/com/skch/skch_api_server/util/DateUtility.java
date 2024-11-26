package com.skch.skch_api_server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtility {

	private DateUtility() {
		throw new IllegalStateException("DateUtility class");
	}

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
		if(date != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
			return date.format(formatter);
		}
		return "";
	}

	public static String dateToString(LocalDate date, String format) {
		if(date != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
			return date.format(formatter);
		}
		return "";
	}
	
	public static LocalDate stringToDate(String date,String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDate.parse(date,formatter);
	}
	
	public static LocalDateTime stringToDateTime(String date,String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.parse(date,formatter);
	}
	
	public static LocalDateTime stringToDateTimes(String date, String format) {
		if(date != null && !date.isBlank()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
	        LocalDate localDate = LocalDate.parse(date, formatter);
	        return localDate.atStartOfDay(); // Combines the date with the start of the day
		}
        return null;
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
	
	public static LocalDateTime getLongMilli(Long timeMilli) {
		return timeMilli != null ? 
			LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMilli), ZoneId.systemDefault())
		: null ;
	}
	
	public static void main(String[] args) {
		long value = System.currentTimeMillis();
		
		log.info("DateTime in UTC :: {}",toLocalDateTimeUtc(value));
		
		log.info("Date in UTC :: {}",toLocalDateUtc(value));
		
		long date = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli();
		
		log.info("Long Date in UTC :: {}",date);
	}
	
	public static LocalDateTime toLocalDateTimeUtc(Long timeMilli) {
		return timeMilli != null ? 
			LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMilli), ZoneOffset.UTC)
		: null ;
	}
	
	public static LocalDate toLocalDateUtc(Long timeMilli) {
		return timeMilli != null ? 
			LocalDate.ofInstant(Instant.ofEpochMilli(timeMilli), ZoneOffset.UTC)
		: null ;
	}
	
}
