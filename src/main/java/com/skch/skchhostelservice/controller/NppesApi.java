package com.skch.skchhostelservice.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NppesApi {
	
	public static void main(String[] args) {
		NppesResponse response = getProviderDetails("1598013248");
		
		if (ObjectUtils.isNotEmpty(response) && response.getResultCount() > 0) {
			System.out.println("Response Found...");
		} else {
			System.out.println("Response Not Found...");
		}
		
		System.out.println(response);
	}
	
	private static  String NPPES_API_URL = "https://npiregistry.cms.hhs.gov/api/";

	public static NppesResponse getProviderDetails(String npiNumber) {
		NppesResponse response = null;
		try {
			RestTemplate restTemplate = new RestTemplate();

			String url = UriComponentsBuilder.fromHttpUrl(NPPES_API_URL).queryParam("version", "2.1")
					.queryParam("number", npiNumber).toUriString();

			String jsonData = restTemplate.getForObject(url, String.class);
			Gson gson = new Gson();
			response = gson.fromJson(jsonData, NppesResponse.class);
		} catch (Exception e) {
			log.error("Error in getProviderDetails :: ", e);
		}
		return response;
	}

}
