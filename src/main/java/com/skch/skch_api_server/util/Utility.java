package com.skch.skch_api_server.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.language.Soundex;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.skch.skch_api_server.dto.JsonTest;
import com.skch.skch_api_server.model.Audit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utility {

	public static String isBlank(String input) {
		return input == null || !input.isBlank() ? input : null;
	}
	
	public static LocalDateTime dateConverts(String input){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
		LocalDateTime dateTime = LocalDateTime.parse(input, formatter);
		return dateTime;
	}
	
	public static LocalDate dateConvert(String input){
		LocalDate date = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			date = LocalDate.parse(input, formatter);
		} catch (Exception e) {
			log.error("Error in convert Date :: "+e);
		}
		return date;
	}
	
	public static ByteArrayOutputStream createExcel() throws IOException {
		SXSSFWorkbook workbook = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook = new SXSSFWorkbook();
//		Sheet sheet = workbook.createSheet("Test Sheet");
		workbook.createSheet("Test Sheet");
		workbook.write(baos);
		workbook.close();
		return baos;
	}

	public static ByteArrayOutputStream createPdf(byte[] imageData) throws IOException, DocumentException {
		Rectangle pagesize = new Rectangle(1754, 1240);
		Document document = new Document(pagesize, 30, 30, 45, 30);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PdfWriter.getInstance(document, baos);
		document.open();
		document.add(new Paragraph("Test Pdf"));
		
		document.add(createImage(imageData));
		
		document.close();

		return baos;
	}
	
	public static Image createImage(byte[] imageData) {
		Image image = null;
		try {
			// Create an Image object
			image = Image.getInstance(imageData);
			image.setAlignment(Image.ALIGN_CENTER);
			// Set position and size of the image
			image.scalePercent(30f);
		} catch (Exception e) {
			log.error("error in createImage", e);
		}
		return image;
	}

	public static ByteArrayOutputStream createZip(byte[] imageData) throws IOException, DocumentException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(baos))) {
			createFileToZip(zipOutputStream, "sample.xlsx", createExcel());
			createFileToZip(zipOutputStream, "sample.pdf", createPdf(imageData));
			zipOutputStream.finish();
		}

		return baos;
	}

	public static void createFileToZip(ZipOutputStream zos, String fileName, ByteArrayOutputStream baos) {
		log.info("Inside Zip Entry... :: "+fileName);
		try {
			if (baos != null) {
				// Add the byte array to the zip as an entry
				ZipEntry zipEntry = new ZipEntry(fileName);
				zos.putNextEntry(zipEntry);
				zos.write(baos.toByteArray());
				zos.closeEntry();
			}
		} catch (Exception e) {
			log.error("Error in Zip File Adding :: ",e);
		}
	}

	public static ByteArrayOutputStream pathToBos(String path) throws IOException {
		Path filePath = Paths.get(path);
		byte[] bytes = Files.readAllBytes(filePath);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(bytes);

		return outputStream;
	}
	
	public static byte[] pathToByte(String path) throws IOException {
		Path filePath = Paths.get(path);
		byte[] bytes = Files.readAllBytes(filePath);
		return bytes;
	}
	
	public static String getDbColumn(String input) {
		return input != null && !input.isBlank() ? 
				input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase() : "";
	}
	
	public static String nullCheck(String input) {
		return input != null ? input : "";
	}
	
	public static Boolean nullCheck(Boolean input) {
		return input != null ? input : false;
	}
	
	public static BigDecimal toBigDecimal(String input) {
		if(input != null && !input.isBlank()) {
			return new BigDecimal(input);
		}
		return null;
	}
	
	public static Double toDouble(BigDecimal input) {
		return input != null ? input.doubleValue() : 0d;
	}
	
	public static Boolean check(String input) {
		return input != null && !input.isBlank() ? true : false;
	}
	
	public static int totalPages(Long totalElements,int pageSize) {
		return (int) Math.ceil((double) totalElements / pageSize);
	}
	
	public static <T> void updateFields(T model,String type) {
		try {
			Field[] fields = Audit.class.getDeclaredFields();
			for(Field field : fields) {
				field.setAccessible(true);
				if(field.getName().equals("createdById") && type.equals("C")
						|| field.getName().equals("modifiedById")) {
					field.set(model, JwtUtil.getUserId());
				}else if(field.getName().equals("createdDate") && type.equals("C")
						|| field.getName().equals("modifiedDate")) {
					field.set(model, LocalDateTime.now());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Trim the String variables in Class
	 */
	public static <T> void trimFields(T obj) {
	    try {
	        Field[] fields = obj.getClass().getDeclaredFields();
	        for (Field field : fields) {
	            field.setAccessible(true);
	            if (field.getType().equals(String.class) && ObjectUtils.isNotEmpty(field.get(obj))) {
	                String trimmedValue = ((String) field.get(obj)).trim();
	                System.out.println(trimmedValue);
	                field.set(obj, trimmedValue);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static String soundex(String input) {
		String output = "";
		try {
			if (input != null && !input.isBlank()) {
				Soundex soundex = new Soundex();
				output = soundex.soundex(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	public static String mapToJson(Map<String,String> map) {
		Gson gson = new Gson();
        String json = gson.toJson(map);
        return json;
	}
	
	public static Map<String,String> jsonToMap(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, String>>(){}.getType();
        return gson.fromJson(json, type);
	}
	
	public static <K, V> Map<K, V> parseJsonToMap(String jsonData, Class<K> keyClass, Class<V> valueClass) {
		Gson gson = new Gson();
        Type type = TypeToken.getParameterized(Map.class, keyClass, valueClass).getType();
        return gson.fromJson(jsonData, type);
    }
	
	public static <K, V> Map<K, V> parseJsonToMapTwo(String jsonData, Class<K> keyClass, Class<V> valueClass) {
        Map<K, V> map = null;
		try {
			Gson gson = new Gson();
			try (JsonReader reader = new JsonReader(new StringReader(jsonData))) {
				map = new HashMap<>();
				reader.beginObject();
				while (reader.hasNext()) {
				    K key = gson.fromJson(reader.nextName(), keyClass);
				    V value = gson.fromJson(reader.nextString(), valueClass);
				    map.put(key, value);
				}
				reader.endObject();
			}
		} catch (Exception e) {
			log.error("Error in parseJsonToMapTwo :: ",e);
		}
        return map;
    }
	
	public static void main(String[] args) {
		
		JsonTest test = new JsonTest();
		test.setEmailId("");
		
		trimFields(test);
		
		List<JsonTest> listTest = new ArrayList<>();
		listTest.add(test);
		
		Gson gson = new Gson();
        String json = gson.toJson(listTest);
        
        System.out.println(json);
        
        System.out.println(test);
		
        
	}
	
}
