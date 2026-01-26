package com.skch.skch_api_server.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PkceUtil {

	private static final SecureRandom RANDOM = new SecureRandom();

	public static String generateCodeVerifier() {
		byte[] code = new byte[32];
		RANDOM.nextBytes(code);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
	}

	public static String generateCodeChallenge(String verifier) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(verifier.getBytes(StandardCharsets.US_ASCII));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
		} catch (Exception ex) {
			throw new IllegalStateException("Unable to generate PKCE challenge", ex);
		}
	}
}
