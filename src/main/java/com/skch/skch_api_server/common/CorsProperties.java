package com.skch.skch_api_server.common;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "cors")
@Getter
@Setter
public class CorsProperties {
	private List<String> corsOrigin;
}