package com.skch.skch_api_server.config;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.skch.skch_api_server.exception.ErrorResponse;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CORSFilter implements Filter {

	private static final Pattern XSS_PATTERN = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletResponse response = (HttpServletResponse) res;
		final HttpServletRequest request = (HttpServletRequest) req;

		try {
			// Set CORS headers
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
			response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
			response.setHeader("Access-Control-Max-Age", "3600");

			// Handle CORS preflight requests
			if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
				response.setStatus(HttpServletResponse.SC_OK);
				return;
			}
			
			// Bypass XSS validation for multipart requests
			if (request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart/")) {
				chain.doFilter(req, res);
				log.info(">>>>Inside Upload....{}",request.getContentType());
				return;
			}

			CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
			
			// XSS validation logic
			if (hasXSS(wrappedRequest.getRequestBody())) {
				throw new RuntimeException("Potential XSS detected in request body!");
			}
			
			// XSS validation for path variables and request parameters
            if (hasXSSInParamsOrPath(request)) {
                throw new RuntimeException("Potential XSS detected in path or request parameters!");
            }

			chain.doFilter(wrappedRequest, res);
		} catch (RuntimeException e) {
			log.error("Error in CORS :: {}", e.getMessage(), e);
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			response.setContentType("application/json");
			ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(),
					"Potential XSS detected in request body!", e.getMessage());
			response.getWriter().write(new Gson().toJson(errorResponse));
		}catch (Exception e) {
			log.error("Error in CORS :: {}", e.getMessage(), e);
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			response.setContentType("application/json");
			ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Internal Server Error.", e.getMessage());
			response.getWriter().write(new Gson().toJson(errorResponse));
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	public static boolean hasXSS(String requestBody) {
		// Return true if XSS patterns are detected in the request body
		Matcher matcher = XSS_PATTERN.matcher(requestBody);
		return matcher.find();
	}
	
	// Check for XSS in request parameters and path variables
    private boolean hasXSSInParamsOrPath(HttpServletRequest request) {
        // Check request parameters
        for (String[] paramValue : request.getParameterMap().values()) {
            for (String value : paramValue) {
            	log.info("Param Value : "+value);
                if (hasXSS(value)) {
                    return true;
                }
            }
        }
        // Check path variables (from the URI)
        try {
            String decodedPath = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8.name());
            log.info(decodedPath);
            if (hasXSS(decodedPath)) {
                return true;
            }
        } catch (Exception e) {
            return false; // Fail safe in case of decoding issues
        }

        return false;
    }

}