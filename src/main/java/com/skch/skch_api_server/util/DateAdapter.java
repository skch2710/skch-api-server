package com.skch.skch_api_server.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateAdapter<T> extends TypeAdapter<T> {

	private String type = "";

	@Override
	public void write(JsonWriter out, T value) throws IOException {
		if (value instanceof LocalDate) {
			out.value(((LocalDate) value).toString());
			type = "date";
		} else if (value instanceof LocalDateTime) {
			out.value(((LocalDateTime) value).toString());
			type = "dateTime";
		} else {
			log.error("DateTypeAdapter write");
			throw new IllegalArgumentException("Unsupported type: " + value.getClass());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public T read(JsonReader in) throws IOException {
		String stringValue = in.nextString();
		T t = null ;
		try {
			if (type.equals("date")) {
				t = (T) LocalDate.parse(stringValue);
			} else if(type.equals("dateTime")){
				t = (T) LocalDateTime.parse(stringValue);
			}
		} catch (Exception e) {
			log.error("DateTypeAdapter read");
		}
		return t;
	}
}
