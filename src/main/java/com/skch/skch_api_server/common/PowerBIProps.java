package com.skch.skch_api_server.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "powerbi")
@Getter
@Setter
public class PowerBIProps {
	private String tenantId;
	private String clientId;
	private String clientSecret;
	private String workspaceId;
	private String reportId;
	private String role;
	private String proxyHost;
	private int proxyPort;
	private String authorityUrl;
	private String resourceUrl;
	private String embedBaseUrl;
	private String embedTokenUrl;
}
