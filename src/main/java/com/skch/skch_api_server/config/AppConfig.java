package com.skch.skch_api_server.config;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.client.RestClient;

import com.skch.skch_api_server.common.PowerBIProps;
import com.smartystreets.api.ClientBuilder;
import com.smartystreets.api.us_street.Client;

@Configuration
@EnableRetry
public class AppConfig {
	
	@Value("${smarty.auth-id}")
	private String authId;
	
	@Value("${smarty.auth-token}")
	private String authToken;

//	@Bean
//    public TaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(5);
//        scheduler.initialize();
//        scheduler.setThreadNamePrefix("Scheduler-");
//        return scheduler;
//    }

	@Bean
	public TaskScheduler taskScheduler() {
		ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(
				Runtime.getRuntime().availableProcessors(), Thread.ofVirtual().factory());
		return new ConcurrentTaskScheduler(scheduledExecutor);
	}
	
	@Bean
	Client smartyClient() {
		return new ClientBuilder(authId, authToken).buildUsStreetApiClient();
	}
	
	@Bean
	RestClient restClient() {
	    return RestClient.create();
	}

	
//	@Bean
//	RestClient powerBIRestClient(PowerBIProps props) {
//		try {
//			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
//				@Override
//				public X509Certificate[] getAcceptedIssuers() {
//					return new X509Certificate[0];
//				}
//				@Override
//				public void checkClientTrusted(X509Certificate[] certs,String authType) {
//				}
//				@Override
//				public void checkServerTrusted(X509Certificate[] certs, String authType) {
//				}
//			} };
//
//			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
//			sslContext.init(null, trustAllCerts, new SecureRandom());
//
//			SSLParameters sslParameters = new SSLParameters();
//
//			HttpClient.Builder httpClientBuilder = HttpClient.newBuilder().sslContext(sslContext)
//					.sslParameters(sslParameters).connectTimeout(Duration.ofSeconds(30));
//
//			httpClientBuilder
//					.proxy(ProxySelector.of(new InetSocketAddress(props.getProxyHost(),
//							props.getProxyPort())));
//			HttpClient httpClient = httpClientBuilder.build();
//			return RestClient.builder()
//					.requestFactory(new JdkClientHttpRequestFactory(httpClient)).build();
//		} catch (Exception e) {
//			throw new RuntimeException("Failed to create RestClient with disabled SSL", e);
//		}
//	}
}
