package com.skch.skch_api_server.dto;

import java.io.ByteArrayOutputStream;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true, value = { "bao", "fileName", "type" })
public class FileDetails {

	private ByteArrayOutputStream bao;
	private String fileName;
	private MediaType type;

}
