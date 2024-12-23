package com.skch.skch_api_server.config;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.skch.skch_api_server.dto.FileUploadDTO;

@Configuration
@EnableWebMvc
public class MultipartConfig implements WebMvcConfigurer {

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new Converter());
		converters.add(new FileUploadConverter());
	}
}

class Converter extends MappingJackson2HttpMessageConverter {

	@Override
	public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
		return type.getTypeName().equals(Object.class.getName());
	}
}

class FileUploadConverter extends MappingJackson2HttpMessageConverter {

	@Override
	public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
		return type.getTypeName().equals(FileUploadDTO.class.getName());
	}
}
