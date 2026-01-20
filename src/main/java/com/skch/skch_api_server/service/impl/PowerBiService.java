package com.skch.skch_api_server.service.impl;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.skch.skch_api_server.common.PowerBIProps;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PowerBiService {
	
	private final PowerBIProps props;
	private final RestClient powerBIRestClient;
	private ConfidentialClientApplication app;
	
	@PostConstruct
	void init() throws Exception {
		this.app = ConfidentialClientApplication
				.builder(props.getClientId(), ClientCredentialFactory.createFromSecret(props.getClientSecret()))
				.proxy(new Proxy(Type.HTTP, new InetSocketAddress(props.getProxyHost(), props.getProxyPort())))
				.authority(props.getAuthorityUrl() + props.getTenantId()).build();
	}
	
	public Responce generateEmbedToken() {
		String accessToken = getAccessToken();
		
		ReportDetails reportDetails = getEmbededReportDetails(accessToken);
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Map<String, Object> body = Map.of("accessLevel", "View", "identities",
				new Object[] { Map.of("username", username, "roles", new String[] { props.getRole() }, "datasets",
						new String[] { reportDetails.getDatasetId() }) });
		try {
			String url = String.format(props.getEmbedBaseUrl() + props.getEmbedTokenUrl(),
					props.getWorkspaceId(), props.getReportId());
			EmbedTokenDetails embedTokenDetails = powerBIRestClient.post()
					.uri(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).body(body).retrieve()
					.body(EmbedTokenDetails.class);
			
			log.info("Generated Power BI Embed Token successfully  : {}", embedTokenDetails);
			Responce responce = new Responce();
			responce.setReportDetails(reportDetails);
			responce.setEmbedTokenDetails(embedTokenDetails);
		return responce;
		} catch (Exception e) {
			log.error("Error generating Power BI Embed Token", e);
			throw new IllegalStateException("Failed to generate Power BI Embed Token", e);
		}
	}
	
	public ReportDetails getEmbededReportDetails(String accessToken) {
		System.out.println("Access Token: " + accessToken);
		try {
			String url = String.format(props.getEmbedBaseUrl() , props.getWorkspaceId(), props.getReportId());
			ReportDetails object = powerBIRestClient.get()
					.uri(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).retrieve().body(ReportDetails.class);
			log.info("Fetched Power BI Embedded Report Details successfully  : {}", object);
			return object;
		} catch( Exception e) {
			log.error("Error fetching Power BI Embedded Report Details", e);
			throw new IllegalStateException("Failed to fetch Power BI Embedded Report Details", e);
		}
	}
	
	private String getAccessToken() {
		try {
			ClientCredentialParameters parameters = ClientCredentialParameters
					.builder(Set.of(props.getResourceUrl())).build();
			return app.acquireToken(parameters).join().accessToken();
		} catch (Exception e) {
			log.error("Error while getting Azure AD Access Token", e);
			throw new IllegalStateException("Failed to acquire Azure AD token", e);
		}
	}
}

@Getter
@Setter
class Responce {
	
	private ReportDetails reportDetails;
	private EmbedTokenDetails embedTokenDetails;
	
}

@Getter
@Setter
class ReportDetails {
	private String id;
	private String reportType;
	private String name;
	private String webUrl;
	private String embedUrl;
	private boolean isFromPbix;
	private boolean isOwnedByMe;
	private String datasetId;
	private String datasetWorkspaceId;
	private Object[] users;
	private Object[] subscriptions;
}

@Getter
@Setter
class EmbedTokenDetails {
	private String token;
	private String tokenId;
	private String expiration;
}

/* Sample Response:
{
 "reportDetails": {
   "id": "a8d25e01-b4bf-4a96-98cc-7a085066a8b9",
   "reportType": "PowerBIReport",
   "name": "Excluded Drugs Overview",
   "webUrl": "https://app.powerbi.com/groups/70da9656-faa1-4248-b8c0-41e328ad42dc/reports/a8d25e01-b4bf-4a96-98cc-7a085066a8b9",
   "embedUrl": "https://app.powerbi.com/reportEmbed?reportId=a8d25e01-b4bf-4a96-98cc-7a085066a8b9&groupId=70da9656-faa1-4248-b8c0-41e328ad42dc&w=2&config=eyJjbHVzdGVyVXJsIjoiaHR0cHM6Ly9XQUJJLVdFU1QtVVMtQi1QUklNQVJZLXJlZGlyZWN0LmFuYWx5c2lzLndpbmRvd3MubmV0IiwiZW1iZWRGZWF0dXJlcyI6eyJ1c2FnZU1ldHJpY3NWTmV4dCI6dHJ1ZX19",
   "isFromPbix": true,
   "isOwnedByMe": true,
   "datasetId": "c18b2a6b-dc51-4fdf-9814-de5b2d9bd5ab",
   "datasetWorkspaceId": "70da9656-faa1-4248-b8c0-41e328ad42dc",
   "users": [],
   "subscriptions": []
 },
 "embedTokenDetails": {
   "token": "H4sIAAAAAAAEAB3UtQ7sCBIF0H95qUcy2-2RJjBjmzkzM7NX--_bs_lN6tSt-s8fM3n6Kcn__P2HS9JNdd8yqNMdSkAbcJpIdijumpwrC_E3imFrHPYaGBnocQ_WNhgBNF76o4YneYKDmsKN872cuvbcToLLl4VEtoQA0wTcgmQt3OgaT3YSLCGqU61at6WdL6vDGxQzntLbVBff-lE8dESEPM7iKk8PdxsvuikIK2D7sJOe9OPl6Zis57vo8rofDqQDxwwQ1JMCT04yo77tHcuW-5o18YPDgNY-c62RZWm0gyaw4zYLCy6IafkoaEzpC2YjMwKUa8tO07mTB4BncQXpKl0gleAPTapER2ixwsvjZzQWeYlvlbNX37HNbmft9A9nqo8fU4nzRdfqDYr24s8LoSO__36vJQk2XlrmSd1_436Pzz1CN-pQRIkslFKS52cQPt0goBfbDmrmzz-T3ofW3bVXIJNGB4ZwRLgqamurfCttWbsVZaqeqZmCEt9B7mvVH1BZtf62AQlvQ4SytsRGR1HjzcHdqtOzVIZ1O8q-qNkXuyEpLlNffptN4_bCpAHPZn57ItZoY6YghXeeGDrSf0KrdAl8_0UJFM7Foub5wZWGc4DN9wbEpfeus8lODlw-5Koll_pp8dwZ1-QT0alO6COXcYLceUEpymiaqGDKniEohvV8PN7emc5pqy1OEwOkFEmGUb2b9bSDSqIod2SbLpaOmDOMOGfHk5ko5qXTx-JjGTqe4Jq4HgKGoYrgjI9c664uBaBMDaQ5zxMYuxpxYllTocs436rfEHfL7-2Lod5OjIa3eO7AgEhrrIsTAu6x-I6eAKaKSqg_EiKOxui2jZbWHL3PkSVg0-mXCN7oXGV69Cmel6H5azSxxlCa2CmjhNtcKsapK6OtchpKMrdziGmY_7iL-XzNglKaVXA3Rd3VKIrwVhjrFLifa3Cobg4B-2CHrXA4DP3z1x92feZ9Uovnd451hk7SqfBRkyFVJ9pMFtBwUjaRej4KRMM7yOQB_ytHYLQyIp-RhMX9mlshz_UdSt_ocEOxYRFCOX1DpopKNQJKPl-gEaz2soIb4i0cGDPQ8NFFg66yo9LktYgem2i1uQ8jfLJOV8NO4UnJpGjtrd2VXirncf9EHBV-nHelTYNgEpnhYbnl6-PiPWf6mACgrhQe5VjMuwXMFEFXfIrF8qq-FxJMrwy1Rgxd6ncxu1Ehs3bCKfXGmDC8P9TmfMUh4T6nQ97ezIBx9zr7CvbsqpC4ZWVdJTON-3CpT0qms2_bR5iYNKw_yheZ-zG6oGR9VoTCDqsZ9Nk0vBGJSn-pbh4ateqff_5l_v2EYpX9n_I5EPznQbMW7s93eLWTRYOB_n_Kaaox2Y-1-MXQuonRZgRDX05qzdXcwpkCY7KfWZDw5kyPaVmaWiHPEufEdM4FDd8ZQs3LZ42OdoLE0EdJL330bNZuQu3zNbGyYD-MqLH1VNkmJBD6M2qartPgC6-WBvxCOiILiaCXvJ5NuB-CtS4stwGU4FNkavYJUk8pZJOzNwzFe6rb9eHx1DDnmczJYbXRxZVbMk_0NIuloK9VLg46h5luMJsaQyPgXdhMi6P23UIUX1aSbAY1meb4V9mNvC40WEk7My9gB1gTKjqlzJOKhTvNtW-wQUCuubxjdFYphSH3GsUtrG0uCi52fMPEJaobBK7leFidGJl88sg8zQsaYgAAbZD6VhHm-pf5v_8DHCizBYIGAAA=.eyJjbHVzdGVyVXJsIjoiaHR0cHM6Ly9XQUJJLVdFU1QtVVMtQi1QUklNQVJZLXJlZGlyZWN0LmFuYWx5c2lzLndpbmRvd3MubmV0IiwiZXhwIjoxNzY4MzkwMjAwLCJhbGxvd0FjY2Vzc092ZXJQdWJsaWNJbnRlcm5ldCI6dHJ1ZX0=",
   "tokenId": "3e9e709e-3224-4697-83da-6cf6639f54ce",
   "expiration": "2026-01-14T11:30:00Z"
 }
}
*/
