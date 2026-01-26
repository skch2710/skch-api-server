package com.skch.skch_api_server.config;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {

	@Override
	public String resolve(HttpServletRequest request) {
		if (request.getCookies() == null)
			return null;

		for (Cookie cookie : request.getCookies()) {
			if ("ACCESS_TOKEN".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
