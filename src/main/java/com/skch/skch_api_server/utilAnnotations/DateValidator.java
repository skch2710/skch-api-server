package com.skch.skch_api_server.utilAnnotations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.apache.commons.lang3.ObjectUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<DateValidate, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (ObjectUtils.isEmpty(value)) {
			return true;
		}
		return isDateValid(value);
	}

	public boolean isDateValid(String value) {
		try {
			//uuuuMMdd
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu")
					.withResolverStyle(ResolverStyle.STRICT);
			LocalDate.parse(value, formatter);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
