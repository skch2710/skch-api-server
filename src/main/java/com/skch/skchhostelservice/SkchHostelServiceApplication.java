package com.skch.skchhostelservice;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@EnableAsync
@OpenAPIDefinition(info = @Info(title = "Spring Boot",version = "3.2.2",description = "Hostel Server"))
@SecurityScheme(
	    name = "bearerAuth",
	    type = SecuritySchemeType.HTTP,
	    bearerFormat = "JWT",
	    scheme = "bearer"
	)
public class SkchHostelServiceApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("${app.timezone}"));
		SpringApplication.run(SkchHostelServiceApplication.class, args);
	}

}
