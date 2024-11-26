package com.skch.skch_api_server.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationUtils {

	private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private static final Validator validator = factory.getValidator();

	public static <T> Map<String, String> validate(T object) {
//		log.info("Starting at validate....");
		Map<String, String> errors = new HashMap<>();
		try {
			Set<ConstraintViolation<T>> violations = validator.validate(object);

			for (ConstraintViolation<T> violation : violations) {
				String propertyPath = violation.getPropertyPath().toString();
				String message = violation.getMessage();
				errors.put(propertyPath, message);
			}
//			log.info("Ending at validate....");
		} catch (Exception e) {
			log.error("Error in validate ::" + e);
		}
		return errors;
	}

}
