package com.skch.skch_api_server.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class LinkUtil {
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		String uuid = UUID.randomUUID().toString();
		
		Long timeMilli = System.currentTimeMillis();
		
		String link = "https://localhost:8080/createPassword?uuid="+ AESUtils.encrypt(uuid+"#"+timeMilli);
		
		System.out.println(link);
		
		String encodeLink = URLEncoder.encode(link, StandardCharsets.UTF_8.name());
		
		System.out.println(encodeLink);
		
		String decodeLink = URLDecoder.decode(encodeLink, StandardCharsets.UTF_8.name());

		System.out.println(decodeLink);
		
		System.out.println();
		
	}

}
