//package com.skch.skch_api_server.config;
//
//import java.util.List;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import com.skch.skch_api_server.dto.FileUploadDTO;
//import com.skch.skch_api_server.dto.SmartyFileUploadDTO;
//
//@Configuration
//public class MultipartConfig implements WebMvcConfigurer {
//
//	@Override
//	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//		converters.add(new FileUploadConverter());
//		converters.add(new SmartyFileUploadConverter());
//	}
//}
//
//class FileUploadConverter extends JacksonJsonHttpMessageConverter {
//	@Override
//	protected boolean supports(Class<?> clazz) {
//		return FileUploadDTO.class.equals(clazz);
//	}
//}
//
//class SmartyFileUploadConverter extends JacksonJsonHttpMessageConverter {
//	@Override
//	protected boolean supports(Class<?> clazz) {
//		return SmartyFileUploadDTO.class.equals(clazz);
//	}
//}