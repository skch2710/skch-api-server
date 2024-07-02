package com.skch.skchhostelservice;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableAsync
@OpenAPIDefinition(info = @Info(title = "Spring Boot", version = "3.2.2", description = "Hostel Server"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class SkchHostelServiceApplication {

	@Value("${app.timezone}")
	private String timeZone;

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	public static void main(String[] args) {
		SpringApplication.run(SkchHostelServiceApplication.class, args);
	}

}
