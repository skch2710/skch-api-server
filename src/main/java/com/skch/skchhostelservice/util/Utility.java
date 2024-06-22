package com.skch.skchhostelservice.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.language.Soundex;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.skch.skchhostelservice.model.Audit;

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

	private static void createFileToZip(ZipOutputStream zipOutputStream, String fileName, ByteArrayOutputStream baos)
			throws IOException {
		if (baos != null) {
			// Add the byte array to the zip as an entry
			ZipEntry zipEntry = new ZipEntry(fileName);
			zipOutputStream.putNextEntry(zipEntry);
			zipOutputStream.write(baos.toByteArray());
			zipOutputStream.closeEntry();
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
	
	public static BigDecimal toNum(String input) {
		if(input != null && !input.isBlank()) {
			return new BigDecimal(input);
		}
		return null;
	}
}
