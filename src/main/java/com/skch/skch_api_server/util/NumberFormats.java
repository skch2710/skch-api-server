package com.skch.skch_api_server.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NumberFormats {
	
	public static Boolean isNumeric(Object number) {
	    return number != null && number.toString().matches("^-?\\d*\\.?\\d*$");
	}
	
	public static String numFormart(Object input, String format) {
		String output = "";
		try {
			if (input != null && isNumeric(input)) {
				DecimalFormat df = new DecimalFormat(format);
				return df.format(input);
			}
		} catch (Exception e) {
			log.error("Error in numFormart :: ",e);
		}
		return output;
	}
	
	public static void main(String[] args) {
		
		DecimalFormat df = new DecimalFormat("00");

        Long value = 3L;

        String output = df.format(value);

        System.out.println(output);
        
        System.out.println(numFormart(value,"00"));
        
        Double dv = 412.54d;
        
        System.out.println("Doble Value : "+numFormart(dv,"00"));
        
        BigDecimal bd = new BigDecimal(-4561412.54);
        
        System.out.println("Doble Value : "+numFormart(bd,"00"));
        
        System.out.println("Doble Value : "+numFormart(bd,"#,###"));
        
        System.out.println("Doble Value : "+numFormart(bd,"#,###.00"));
        
        System.out.println("Doble Value : "+numFormart(bd,"$ #,###.00"));
        
        System.out.println("Doble Value : "+numFormart(bd,"$ #,###.00;($ #,###.00)"));
        
        System.out.println("Doble Value : "+numFormart(bd,"#,###.00;(#,###.00)"));
        
        System.out.println("Doble Value : "+numFormart(bd,"#,###;(#,###)"));
		
	}

}
