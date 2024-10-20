package com.skch.skchhostelservice.util;

import org.apache.commons.lang3.ObjectUtils;

import com.google.gson.Gson;
import com.skch.skchhostelservice.dto.NppesResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NppesApi {
	
	public static void main(String[] args) {
		NppesResponse response = getProviderDetails("1285895961");
		
		if (ObjectUtils.isNotEmpty(response) && response.getResultCount() > 0) {
			System.out.println("Response Found...");
		} else {
			System.out.println("Response Not Found...");
		}
		
		System.out.println(response);
	}
	
	private static  String NPPES_API_URL = "https://npiregistry.cms.hhs.gov/api/?version=2.1&number=";

	public static NppesResponse getProviderDetails(String npiNumber) {
		NppesResponse response = null;
		try {
//			String url = UriComponentsBuilder.fromHttpUrl(NPPES_API_URL).queryParam("version", "2.1")
//					.queryParam("number", npiNumber).toUriString();
			
			String url = NPPES_API_URL + npiNumber;
//			String jsonData = RestHelper.getRetry(url, String.class);
			String jsonData = RestClientHelper.getRetry(url, String.class);
			System.out.println(jsonData);
			Gson gson = new Gson();
			response = gson.fromJson(jsonData, NppesResponse.class);
		} catch (Exception e) {
			log.error("Error in getProviderDetails :: ", e);
		}
		return response;
	}

}
