package com.skch.skch_api_server.util;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class PkceService {

	private static final String PKCE_VERIFIER = "PKCE_VERIFIER";

	public void storeVerifier(HttpSession session, String verifier) {
		session.setAttribute(PKCE_VERIFIER, verifier);
	}

	public String getVerifier(HttpSession session) {
		return (String) session.getAttribute(PKCE_VERIFIER);
	}

	public void clear(HttpSession session) {
		session.removeAttribute(PKCE_VERIFIER);
	}
}
