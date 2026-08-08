package com.skch.skch_api_server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

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
	
	public static String objToString(Object value, String format) {
	    if (value == null) {
	        return "";
	    }
	    if (value instanceof LocalDate) {
	        return ((LocalDate) value).format(DateTimeFormatter.ofPattern(format));
	    }
	    if (value instanceof LocalDateTime) {
	        return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(format));
	    }
	    if (value instanceof Date) {
	        return new SimpleDateFormat(format).format((Date) value);
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
	
	//Full Date Time Format
	public static LocalDateTime stringToDateTime(String date,String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.parse(date,formatter);
	}
	
	//Only Date Format
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
		/*long value = System.currentTimeMillis();
		
		log.info("DateTime in UTC :: {}",toLocalDateTimeUtc(value));
		
		log.info("Date in UTC :: {}",toLocalDateUtc(value));
		
		long date = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli();
		
		log.info("Long Date in UTC :: {}",date);
		
		LocalDateTime dateTime = LocalDateTime.now();
		
		LocalTime time = LocalTime.now();
		
		System.out.println(dateTime.getDayOfWeek() == DayOfWeek.SUNDAY);
		
		System.out.println(dateTime.getHour());
		
		System.out.println(dateTime.getMinute());
		
		System.out.println(time.isAfter(LocalTime.of(16, 29)));
		
		System.out.println(time.isBefore(LocalTime.of(17, 01)));
		
		System.out.println(LocalTime.of(17, 01));
		
		//Local Date Methods
		
		//Starting of Day in Month
		LocalDate date = LocalDate.now();
		LocalDate firstDayOfMonth = date.withDayOfMonth(1);
		System.out.println(firstDayOfMonth);
		
		//Ending of Day in Month
		LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());
		System.out.println(lastDayOfMonth);
		
		//Using TemporalAdjusters to get the first and last day of the month
		System.out.println(date.with(TemporalAdjusters.firstDayOfMonth()));
		System.out.println(date.with(TemporalAdjusters.lastDayOfMonth()));
		
		//Length of Month
		int lengthOfMonth = date.lengthOfMonth();
		System.out.println(lengthOfMonth);
		
		//Current Day of Month
		int currentDayOfMonth = date.getDayOfMonth();
		System.out.println(currentDayOfMonth);
		
		//Current Day of Week
		DayOfWeek currentDayOfWeek = date.getDayOfWeek();
		System.out.println(currentDayOfWeek);
		System.out.println(currentDayOfWeek.getValue()); // 1 (Monday) to 7 (Sunday)
		System.out.println(currentDayOfWeek == DayOfWeek.SATURDAY); // true if it's Sunday
		
		//Current Week of Year
		int currentWeekOfYear = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
		System.out.println(currentWeekOfYear);
		
		//First Sunday of the Month
		LocalDate firstSundayOfMonth = date.with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY));
		System.out.println(firstSundayOfMonth);
		
		//Last Sunday of the Month
		LocalDate lastSundayOfMonth = date.with(TemporalAdjusters.lastInMonth(DayOfWeek.SUNDAY));
		System.out.println(lastSundayOfMonth);
		
		//Last Working Day of the Month (Assuming working days are Monday to Friday)
		LocalDate lastWorkingDay = date.with(TemporalAdjusters.lastDayOfMonth());

		if (lastWorkingDay.getDayOfWeek() == DayOfWeek.SATURDAY) {
		    lastWorkingDay = lastWorkingDay.minusDays(1);
		} else if (lastWorkingDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
		    lastWorkingDay = lastWorkingDay.minusDays(2);
		}

		System.out.println(lastWorkingDay);
		
		*/
		
		   LocalDate date = LocalDate.of(2026, 8, 10);
	       System.out.println(date.getMonth() + " -- " + date.getYear());

	       System.out.println("Week Map :: " + getWeekMap(date));
	       
	       for (Map.Entry<String, Map<LocalDate, DayOfWeek>> entry : getWeekMap(date).entrySet()) {
	           String session = entry.getKey();
	           Map<LocalDate, DayOfWeek> week = entry.getValue();
	           
	           System.out.println(session + ":" + week);
	       }
	    
	}
	
	public static Map<String, Map<LocalDate, DayOfWeek>> getWeekMap(LocalDate date) {
		Map<String, Map<LocalDate, DayOfWeek>> weekMap = new LinkedHashMap<>();

		LocalDate firstDay = date.withDayOfMonth(1);
		LocalDate lastDay = date.withDayOfMonth(date.lengthOfMonth());

		int[][] sessions = {{1, 7}, {8, 14}, {15, 21}, {22, lastDay.getDayOfMonth()}};

		for (int session = 0; session < sessions.length; session++) {

			int startDay = sessions[session][0];
			int endDay = sessions[session][1];

			Map<LocalDate, DayOfWeek> week = new LinkedHashMap<>();

			for (int day = startDay; day <= endDay; day++) {
				LocalDate currentDate = firstDay.withDayOfMonth(day);
				week.put(currentDate, currentDate.getDayOfWeek());
			}

			weekMap.put("Session-" + (session + 1), week);
		}

		return weekMap;
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
	
	public static boolean checkBetween(LocalDate startDate, LocalDate endDate, LocalDate targetDate) {
		if (ObjectUtils.isEmpty(endDate)) {
			return (targetDate.isAfter(startDate) || targetDate.isEqual(startDate));
		}
//		return targetDate.isAfter(startDate) && targetDate.isBefore(endDate);
		return (targetDate.isAfter(startDate) || targetDate.isEqual(startDate))
				&& (targetDate.isBefore(endDate) || targetDate.isEqual(endDate));
	}
	
	public static boolean checkBetween(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime targetDate) {
		if (ObjectUtils.isEmpty(endDate)) {
			return (targetDate.isAfter(startDate) || targetDate.isEqual(startDate));
		}
//		return targetDate.isAfter(startDate) && targetDate.isBefore(endDate);
		return (targetDate.isAfter(startDate) || targetDate.isEqual(startDate))
				&& (targetDate.isBefore(endDate) || targetDate.isEqual(endDate));
	}
	
	public static LocalDate maxDate(LocalDate date1 , LocalDate date2) {
		return Collections.max(List.of(date1, date2));
	}
	
	public static LocalDate minDate(LocalDate date1 , LocalDate date2) {
		return Collections.min(List.of(date1, date2));
	}
	
}
