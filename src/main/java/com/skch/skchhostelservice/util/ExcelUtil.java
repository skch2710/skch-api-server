package com.skch.skchhostelservice.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelUtil {
	
	public static final String[] HOSTEL_HEADERS = {"Full Name","Email Id","Phone Number","fee",
			"Joining Date","Address","Proof","Reason","Vacated Date","Active"};
	
	public static final List<String> EXCEL_MIME_TYPES = Arrays.asList(
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );
	
	/**
	 * Check the File is Excel or Not
	 * @param fileType
	 * @return Boolean
	 */
	public static Boolean excelType(MultipartFile file) {
		if (file != null && !file.isEmpty() &&
				EXCEL_MIME_TYPES.contains(file.getContentType())) {
			return true;
		}
		return false;
	}
	
	/**
	 * This Method is check the Headers
	 * 
	 * @param headerRow
	 * @param headers
	 * @return boolean
	 */
	public static Boolean headerCheck(Row headerRow, String[] headers) {
		if (headerRow != null && headerRow.getPhysicalNumberOfCells() == headers.length) {
			List<String> excelHeaders = new ArrayList<>();
			for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
				String header = getCellValue(headerRow.getCell(i));
				excelHeaders.add(header.trim());
			}
			if (Arrays.equals(headers, excelHeaders.toArray())) {
				return true;
			}
		}
		return false;
	}
	
	private static final DecimalFormat df = new DecimalFormat("#");
	
	public static String getCellValue(Cell cell) {
		CellType cellType = cell.getCellType();
		switch (cellType) {
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return DateUtility.dateToString(cell.getLocalDateTimeCellValue(), "yyyyMMdd");
				} else {
					return df.format(cell.getNumericCellValue());
				}
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA:
				return cell.getCellFormula();
			case BLANK:
				return "";
			default:
				return "Unsupported Cell Type";
		}
	}
	
	/**
	 * Share the file into directory
	 * @param file
	 * @param dir
	 */
	public static void sharedFolder(MultipartFile file, String dir) {
		try {
			File uploadDir = new File(dir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
			String originalFilename = file.getOriginalFilename();
			String fileName = "FileName_" + DateUtility.dateToString(LocalDateTime.now(), "MMddyyyy_HHmmss")
					+ "." + originalFilename.split("\\.")[1];
			Path filePath = Paths.get(dir, fileName);
			Files.write(filePath, file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * List of Files in Directory
	 * @param dir
	 */
	public static void folderFiles(String dir) {
		try {
			File directory = new File(dir);
			if (directory.exists() && directory.isDirectory()) {
				File[] files = directory.listFiles();
				List<String> fileNames = new ArrayList<>();
				if (files != null && files.length > 0) {
					for (File file : files) {
						if (file.isFile()) {
							String fileName = file.getName();
							String dateNum = fileName.split("_")[1];

							LocalDate date = DateUtility.stringToDate(dateNum, "MMddyyyy");
							System.out.println(date);

							if (date.isBefore(LocalDate.now())) {
								fileNames.add(fileName);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is delete the file in directory
	 * @param directory
	 * @param fileName
	 * @return boolean
	 */
	public static Boolean deleteFile(String directory, String fileName) {
		Boolean isDeleted = false;
		try {
			File file = new File(directory, fileName);
			isDeleted = false;
			if (file.exists()) {
				isDeleted = file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isDeleted;
	}
	
	public static void shiftRows(Sheet sheet,int startRow,int endRow,int n) {
		sheet.shiftRows(startRow, endRow, n);
//		sheet.shiftRows(startRow, endRow, n, true, true);
	}
	
	public static void updateCellValue(Sheet sheet,int rowIndex,int colIndex,String value) {
		try {
			Row row = sheet.getRow(rowIndex);
			if(row == null) {
				row = sheet.createRow(rowIndex);
			}
			Cell cell = row.getCell(colIndex,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellValue(Utility.nullCheck(value));
		} catch (Exception e) {
			log.error("Error in update Cell Value :: ",e);
		}
	}
	
}
