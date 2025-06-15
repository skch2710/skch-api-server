package com.skch.skch_api_server.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.skch.skch_api_server.dto.UserDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelUtil {

	public static final List<String> HOSTEL_HEADERS = Arrays.asList("Full Name", "Email Id", "Phone Number", "DOB",
			"Fee", "Joining Date", "Address", "Proof", "Reason", "Vacated Date", "Active");
	
	public static final List<String> USER_HEADERS = Arrays.asList("First Name", "Last Name", "Email Id", "Phone Number",
			"DOB", "Role Name", "Active");

	public static final List<String> EXCEL_MIME_TYPES = Arrays.asList("application/vnd.ms-excel",
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	
	public static final List<String> SMARTY_HEADERS = Arrays.asList("Street", "Secondary", "City", "State", "ZipCode");
	
	public static final String XLS_TYPE = "application/vnd.ms-excel";

	public static final String CSV_TYPE = "text/csv";
	
	private static final char BOM = '\uFEFF';
	
	/**
	 * Check the File is Excel or Not
	 * 
	 * @param fileType
	 * @return Boolean
	 */
	public static boolean excelType(MultipartFile file) {
		if (file != null && !file.isEmpty() && EXCEL_MIME_TYPES.contains(file.getContentType())) {
			return true;
		}
		return false;
	}

	/**
	 * Check the File is CSV or Not
	 * 
	 * @param fileType
	 * @return Boolean
	 */
	public static boolean csvType(MultipartFile file) {
		if (file != null && !file.isEmpty() && CSV_TYPE.equals(file.getContentType())) {
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
	public static Boolean headerCheck(Row headerRow, List<String> headers) {
		if (headerRow != null && headerRow.getPhysicalNumberOfCells() == headers.size()) {
			List<String> excelHeaders = new ArrayList<>();
			for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
				String header = getCellValue(headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
				excelHeaders.add(header.trim());
			}
			if (Arrays.equals(headers.toArray(), excelHeaders.toArray())) {
				return true;
			}
		}
		return false;
	}
	
	public static String headerCheck(Sheet sheet, List<String> headers) {
	    String error = "";
	    try {
	        if (sheet != null) {
	            Iterator<Row> rowIterator = sheet.rowIterator();
	            if (rowIterator.hasNext()) {
	                Row headerRow = rowIterator.next();
	                if (headerRow.getPhysicalNumberOfCells() > 0) {
	                    List<String> excelHeaders = new ArrayList<>();
	                    for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
	                        String header = getCellValue(headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
	                        excelHeaders.add(header.trim());
	                    }
	                    
	                    System.out.println(excelHeaders);
	                    
	                    // Compare the headers with the expected headers
	                    if (Arrays.equals(headers.toArray(), excelHeaders.toArray())) {
	                        error = "";
	                    } else {
	                        List<String> unMatched = IntStream.range(0, headers.size())
	                                .filter(i -> i >= excelHeaders.size() || !headers.get(i).equals(excelHeaders.get(i)))
	                                .mapToObj(headers::get).collect(Collectors.toList());
	                        if (!unMatched.isEmpty()) {
	                            error += "Missing Columns " + String.join(",", unMatched);
	                        } else {
	                            List<String> additionalData = IntStream.range(headers.size(), excelHeaders.size())
	                                    .mapToObj(excelHeaders::get).filter(obj -> obj != null && !obj.isBlank())
	                                    .collect(Collectors.toList());
	                            if (!additionalData.isEmpty()) {
	                                error += "Extra Columns " + String.join(",", additionalData);
	                            }
	                        }
	                    }
	                }
	            } else {
	                error += "Missing Columns " + String.join(",", headers);
	            }

	            // Check if the sheet has more than just the header row
	            if (error.isBlank() && !rowIterator.hasNext()) {
	                error += "Empty Template Uploaded";
	            }
	        } else {
	            error += "Missing Columns " + String.join(",", headers);
	        }
	    } catch (Exception e) {
	        log.error("Error in Header Check :: " + e);
	        error += "Something Went Wrong";
	    }
	    return error;
	}


	public static String headerCheckOld(Sheet sheet, List<String> headers) {
		String error = "";
		try {
			if(sheet != null && sheet.getRow(0) != null
					&& sheet.getRow(0).getPhysicalNumberOfCells() > 0) {
				Row headerRow = sheet.getRow(0);
				List<String> excelHeaders = new ArrayList<>();
				for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
					String header = getCellValue(headerRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
					excelHeaders.add(header.trim());
				}
				if (Arrays.equals(headers.toArray(), excelHeaders.toArray())) {
					error = "";
				} else {
					List<String> unMatched = IntStream.range(0, headers.size())
							.filter(i -> i >= excelHeaders.size() || !headers.get(i).equals(excelHeaders.get(i)))
							.mapToObj(headers::get).collect(Collectors.toList());
					if (!unMatched.isEmpty()) {
						error += "Missing Columns " + String.join(",", unMatched);
					} else {
						List<String> additionalData = IntStream.range(headers.size(), excelHeaders.size())
								.mapToObj(excelHeaders::get).filter(obj -> obj != null && !obj.isBlank())
								.collect(Collectors.toList());
						if (!additionalData.isEmpty()) {
							error += "Extra Columns " + String.join(",", additionalData);
						}
					}
				}
			} else {
				error += "Missing Columns " + String.join(",", headers);
			}
			
			if(error.isBlank() && sheet != null) {
				if(sheet.getPhysicalNumberOfRows() <= 1 || sheet.getRow(1) == null || 
						sheet.getRow(1).getPhysicalNumberOfCells() == 0) {
					error += "Empty Template Uploaded";
				}
			}
			
		} catch (Exception e) {
			log.error("Error in Header Check :: " + e);
			error += "Something Went Wrong";
		}
		return error;
	}
	
	/**
	 * HeaderCheck For CSV
	 * @param csvReader
	 * @param headers
	 * @return String
	 */
	public static String headerCheckCsv(List<String[]> csvDataList, List<String> headers) {
		String error = "";
		try {
			if (ObjectUtils.isNotEmpty(csvDataList) && csvDataList.size() > 1) {
				String[] headerLine = csvDataList.get(0);
				
				if (headerLine[0].charAt(0) == BOM) {
	                headerLine[0] = headerLine[0].substring(1);
	            }
				List<String> headersCsv = Stream.of(headerLine).map(String::trim)
                        .collect(Collectors.toList());
				if (Arrays.equals(headers.toArray(), headersCsv.toArray())) {
					return error;
				} else {
					List<String> unMatched = IntStream.range(0, headers.size())
							.filter(i -> i >= headersCsv.size() || !headers.get(i).equals(headersCsv.get(i)))
							.mapToObj(headers::get).collect(Collectors.toList());
					if (!unMatched.isEmpty()) {
						error += "Missing Columns " + String.join(",", unMatched);
					} else {
						List<String> additionalData = IntStream.range(headers.size(), headersCsv.size())
								.mapToObj(headersCsv::get).filter(obj -> obj != null && !obj.isBlank())
								.collect(Collectors.toList());
						if (!additionalData.isEmpty()) {
							error += "Extra Columns " + String.join(",", additionalData);
						}
					}
				}
			} else {
				error += "Missing Columns " + String.join(",", headers);
			}
			
			if (error.isBlank() && csvDataList.size() >= 2) {
				String[] dataFirst = csvDataList.get(1);
				if(ObjectUtils.isEmpty(dataFirst) || dataFirst.length <= 0) {
					error += "Empty Template";
				}
			}else {
				error += "Empty Template";
			}
			
		} catch (Exception e) {
			log.error("Error in Header Check :: " + e);
			error += "Something Went Wrong";
		}
		return error;
	}

	public static String getCellValue(Cell cell) {
		String output = "";
		try {
			CellType cellType = cell.getCellType();
			switch (cellType) {
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return DateUtility.dateToString(cell.getLocalDateTimeCellValue(), "dd-MM-yyyy");
				} else {
					return new DecimalFormat("#").format(cell.getNumericCellValue());
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
		} catch (Exception e) {
			log.info("Error in get Cell Value :: " + e);
		}
		return output;
	}

	public static String getCellValue(Row row,int cellIndex) {
		String output = "";
		Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
		try {
			CellType cellType = cell.getCellType();
			switch (cellType) {
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return DateUtility.dateToString(cell.getLocalDateTimeCellValue(), "dd-MM-yyyy");
				} else {
					return new DecimalFormat("#").format(cell.getNumericCellValue());
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
		} catch (Exception e) {
			log.info("Error in get Cell Value :: " + e);
		}
		return output;
	}

	
	public static String getCellValues(Cell cell) {
		DataFormatter df = new DataFormatter();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String output = "";
		try {
			if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
				output = dateFormat.format(cell.getDateCellValue());
			} else {
				output = df.formatCellValue(cell);
			}
		} catch (Exception e) {
			log.info("Error in get Cell Value" + e);
		}
		return output;
	}

	/**
	 * Share the file into directory
	 * 
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
			String fileName = "FileName_" + DateUtility.dateToString(LocalDateTime.now(), "MMddyyyy_HHmmss") + "."
					+ originalFilename.split("\\.")[1];
			Path filePath = Paths.get(dir, fileName);
			Files.write(filePath, file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * List of Files in Directory
	 * 
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
	 * 
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

	public static void shiftRows(Sheet sheet, int startRow, int endRow, int n) {
		sheet.shiftRows(startRow, endRow, n);
//		sheet.shiftRows(startRow, endRow, n, true, true);
	}

	public static void updateCellValue(Sheet sheet, int rowIndex, int colIndex, String value) {
		try {
			Row row = sheet.getRow(rowIndex);
			if (row == null) {
				row = sheet.createRow(rowIndex);
			}
			Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			cell.setCellValue(Utility.nullCheck(value));
		} catch (Exception e) {
			log.error("Error in update Cell Value :: ", e);
		}
	}

	public static CellStyle cellStyle(Workbook workbook, String format) {
		CellStyle cellStyle = workbook.createCellStyle();
		DataFormat dataFormat = workbook.createDataFormat();
		cellStyle.setDataFormat(dataFormat.getFormat(format));
		cellStyle.setAlignment(HorizontalAlignment.RIGHT);
		return cellStyle;
	}

	public static ByteArrayOutputStream convertCsvToExcel(MultipartFile csvFile) {
		ByteArrayOutputStream bao = null;
		Workbook workbook = null;
		BufferedReader br = null;
		try {
			
			br = new BufferedReader(new InputStreamReader(csvFile.getInputStream()));
			workbook = new SXSSFWorkbook(); // Using SXSSFWorkbook for large data sets
			Sheet sheet = workbook.createSheet("Sheet1");
			String line;
			int rowIndex = 0;

			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				Row row = sheet.createRow(rowIndex++);
				for (int colIndex = 0; colIndex < values.length; colIndex++) {
					row.createCell(colIndex).setCellValue(values[colIndex]);
				}
			}

			bao = new ByteArrayOutputStream();
			workbook.write(bao);
		} catch (Exception e) {
			log.error("Error in Csv To Excel :: ", e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				log.error("Error in Closing Resources :: ", e);
			}
		}
		return bao;
	}
	
	/**
	 * Read CSV file with Pipe | delimiter
	 * @param csvFile
	 */
	public static void readCsvFile(MultipartFile csvFile) {
		CSVReader csvReader = null;
		try {
			CSVParser parser = new CSVParserBuilder()
					.withSeparator('|').build();
			csvReader = new CSVReaderBuilder(new InputStreamReader(csvFile.getInputStream()))
					.withCSVParser(parser).build();

			List<String[]> dataList = csvReader.readAll();

			log.info("Data :: " + dataList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (csvReader != null) {
					csvReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * Read CSV Data as String[]
	 * @param csvReader
	 */
	public static void csvReadData(CSVReader csvReader) {
		try{
			List<String[]> csvData = csvReader.readAll();
			for (String[] strings : csvData) {
				System.out.println(Arrays.asList(strings));
				break;
			}
		} catch (Exception e) {
			log.error("Error in csvReadData :: ", e);
		}
	}
	
	/**
	 * Get the no.of records in CSV file
	 * @param file
	 * @return long
	 */
	public static long getRecordCount(MultipartFile file){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return reader.lines().count() - 1; // Subtract 1 to exclude the header
        }catch(Exception e) {
        	log.error("Error in csvReadData :: ", e);
        }
		return 0;
    }
	
	public static String[] readFirstLine(MultipartFile csvFile){
		String[] header = null;
        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile.getInputStream()))) {
        	
        	List<String[]> data = reader.readAll();
        	
        	log.info("Number Of Records :: "+(data.size()-1));
        	
        	log.info("Data :: "+data.size());
        	
        	if(ObjectUtils.isNotEmpty(data)) {
        		header = data.get(0);
        		System.out.println("Header :: "+Arrays.asList(header));
				if (data.size() >= 2) {
					System.out.println("Second Line :: " + Arrays.asList(data.get(1)));
				}
        	}
//        	reader.skip(1);//this is skip the records
//        	header = reader.readNext();
        }catch(Exception e) {
        	e.printStackTrace();
        }
		return header;
    }

    public String[] readSecondLine(MultipartFile csvFile){
    	String[] header = null;
        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile.getInputStream()))) {
        	reader.readNext(); // Skip the first line
            return reader.readNext(); // Read the second line
        }catch(Exception e) {
        	e.printStackTrace();
        }
		return header;
    }
	

	public static ByteArrayOutputStream getCsv(Workbook workbook) {
		log.info("Starting at getCsv.......");
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
//		ExecutorService executorService = Executors.newFixedThreadPool(5);
		ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
		try (OutputStreamWriter osw = new OutputStreamWriter(bao);
				
			CSVWriter csvWriter = new CSVWriter(osw, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, 
	        		 CSVWriter.NO_ESCAPE_CHARACTER,CSVWriter.DEFAULT_LINE_END)) {
			
			//CSVWriter.DEFAULT_SEPARATOR is ',' if want change that to '|' also
			
			Sheet sheet = workbook.getSheetAt(0);

			int chunkSize = 10000;
			List<CompletableFuture<List<String[]>>> futures = new ArrayList<>();

			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i += chunkSize) {
				int start = i;
				int end = Math.min(start + chunkSize, sheet.getPhysicalNumberOfRows());
				futures.add(CompletableFuture.supplyAsync(() -> processRows(sheet, start, end), 
						executorService)
						.exceptionally(ex -> {
							ex.printStackTrace();
							return new ArrayList<>();
						}));
			}

			CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

			CompletableFuture<List<String[]>> allDataFuture = allOf.thenApply(
					v -> futures.stream().flatMap(future -> future.join().stream())
					.collect(Collectors.toList()));

			List<String[]> csvData = allDataFuture.get();
			log.info("Size :: "+csvData.size());
			csvWriter.writeAll(csvData);
			log.info("After Write All.....");
			csvWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
			try {
				if(workbook != null) {
				workbook.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bao;
	}

	private static List<String[]> processRows(Sheet sheet, int start, int end) {
		log.info("Process Rows :: " + start + " : " + end);
		return IntStream.range(start, end).mapToObj(sheet::getRow).filter(row -> row != null)
				.map(row -> IntStream.range(0, 21).mapToObj(
						cellNum -> getCellValue(row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)))
						.toArray(String[]::new))
				.collect(Collectors.toList());
	}
	
	public static ByteArrayOutputStream getCsvFile(Sheet sheet) {
		log.info("Starting at getCsvFile.......");
		long intialTime = System.currentTimeMillis();
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
//		ExecutorService executorService = Executors.newFixedThreadPool(5);
		ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
		List<CompletableFuture<StringBuilder>> futures = new ArrayList<>();
		
		try (OutputStreamWriter osw = new OutputStreamWriter(bao);
				BufferedWriter bufferedWriter = new BufferedWriter(osw)) {
			
			int chunkSize = 10000;

			for (int i = 0; i < sheet.getLastRowNum(); i += chunkSize) {
	            int start = i;
	            int end = Math.min(start + chunkSize, sheet.getLastRowNum());
	            futures.add(CompletableFuture.supplyAsync(() -> 
	            processRows(sheet, start, end, 11), executorService));
	        }

	        // Wait for all threads to finish and collect their results
	        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	        
			for (CompletableFuture<StringBuilder> future : futures) {
				bufferedWriter.write(future.join().toString());
			}
			
            bufferedWriter.flush();
            
            long finalTime = System.currentTimeMillis();
            log.info("Total to convert the CSV :: "+(finalTime - intialTime));
            Path filePath = Paths.get("C:/Users/HP/Downloads/", "largeOne.csv");
			Files.write(filePath, bao.toByteArray());
		} catch (Exception e) {
			log.error("Error in getCsvFile :: ",e);
		} finally {
			executorService.shutdown();
		}
		return bao;
	}

	public static StringBuilder processRows(Sheet sheet, int start, int end,int lenght) {
	    StringBuilder sb = new StringBuilder();
	    try {
	        for (int rowIndex = start; rowIndex < end; rowIndex++) {
	            Row row = sheet.getRow(rowIndex);
	            if (row == null) continue;
	            for (int cellIndex = 0; cellIndex < lenght; cellIndex++) {
	                if (cellIndex > 0) {
	                    sb.append('|');
	                }
					sb.append(getCellValue(row, cellIndex));
	            }
	            sb.append(System.lineSeparator());
	        }
	    } catch (Exception e) {
	       log.error("Error in Process Rows :: ",e);
	    }
	    return sb;
	}
	
	public static ByteArrayOutputStream getCsvFileNew(Sheet sheet,Integer size) {
	    log.info("Starting at getCsvFile.......");

	    long initialTime = System.currentTimeMillis();
	    ByteArrayOutputStream bao = new ByteArrayOutputStream();

	    try (OutputStreamWriter osw = new OutputStreamWriter(bao);
	         BufferedWriter bufferedWriter = new BufferedWriter(osw)) {

	        // Process all rows in the sheet
	        StringBuilder sb = processRowsNew(sheet,size); 
	        bufferedWriter.write(sb.toString());

	        bufferedWriter.flush();
	        
	        long finalTime = System.currentTimeMillis();
	        log.info("Total time to convert the CSV :: " + (finalTime - initialTime));
	        Path filePath = Paths.get("C:/Users/HP/Downloads/", "largeOne_"+System.currentTimeMillis()+".csv");
	        Files.write(filePath, bao.toByteArray());
	    } catch (Exception e) {
	        log.error("Error in getCsvFile :: ", e);
	    }

	    return bao;
	}

	public static StringBuilder processRowsNew(Sheet sheet,Integer size) {
		StringBuilder sb = new StringBuilder();
		try {
			Iterator<Row> rowIterator = sheet.rowIterator();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row != null) {
					for (int cellIndex = 0; cellIndex < 11; cellIndex++) {
						if (cellIndex > 0) {
							sb.append('|');
						}
						sb.append(getCellValue(row, cellIndex));
					}
					sb.append(System.lineSeparator());
				}
			}
		} catch (Exception e) {
			log.error("Error in Process Rows :: ", e);
		}
		return sb;
	}




	
	public static StringBuilder processRowsParallelStream(Sheet sheet, int start, int end, int length) {
	    return IntStream.range(start, end).parallel()
	            .mapToObj(rowIndex -> {
	                Row row = sheet.getRow(rowIndex);
	                if (row == null) return "";
	                StringJoiner joiner = new StringJoiner("|");
	                for (int cellIndex = 0; cellIndex < length; cellIndex++) {
	                    joiner.add(getCellValue(row, cellIndex));
	                }
	                return joiner.toString() + System.lineSeparator();
	            })
	            .collect(Collectors.collectingAndThen(Collectors.joining(), StringBuilder::new));
	}

	/**
	 * Write the Drop Down Values to Excel Sheet
	 * @param sheet
	 * @param dropdownValues
	 * @param firstRow
	 * @param lastRow
	 * @param firstColumn
	 * @param lastColumn
	 */
	public static void dropDownValues(Sheet sheet, String[] dropdownValues,
			int firstRow, int lastRow, int firstCol, int lastCol) {
		try {
			// Define the range of cells where the dropdown should be added
			CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
			// Create DataValidationHelper and DataValidationConstraint
			DataValidationHelper validationHelper = sheet.getDataValidationHelper();
			DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(dropdownValues);
			// Create the DataValidation object
			DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
			dataValidation.setSuppressDropDownArrow(true); // Optional
			dataValidation.setShowErrorBox(true); // Ensure that an error box is shown if an invalid value is entered
			dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP); // Stops invalid data entry
			dataValidation.createErrorBox("Microsoft Excel",
					"This value doesn't match the data validation restrictions defined for this cell.");
			// Add the validation to the sheet
			sheet.addValidationData(dataValidation);
		} catch (Exception e) {
			log.error("Error in Excel Dropdown Values :: ",e);
		}
	}
	
	public static Sheet getSheet(String sheetName, Workbook workbook) {
		Sheet sheet = null;
		Iterator<Sheet> sheetIterator = workbook.sheetIterator();
		while (sheetIterator.hasNext()) {
			Sheet existingSheet = sheetIterator.next();
			if (existingSheet.getSheetName().trim().equals(sheetName)) {
				sheet = existingSheet;
				break;
			}
		}
		return sheet;
	}

	public static Sheet getFirstSheet(Workbook workbook) {
		Sheet sheet = null;
		Iterator<Sheet> sheetIterator = workbook.sheetIterator();
		while (sheetIterator.hasNext()) {
			sheet = sheetIterator.next();
			break;
		}
		return sheet;
	}
	
	public static void main(String[] args) throws IOException {
		
		LocalTime startTime = LocalTime.now();
		long startMilli = System.currentTimeMillis();
		
		byte[] data = exportUsersToExcel();
		
		String path = "C:/Users/HP/Desktop/UsersData/ExcelMulti_01.xlsx";
		
		FileUtils.saveByteArrayToFile(path, data);
		
		LocalTime endTime = LocalTime.now();
		long endMilli = System.currentTimeMillis();

		System.out.println("File Placed...."+ Duration.between(startTime, endTime).getSeconds());
		System.out.println("Milli...."+ (endMilli - startMilli));
		
	}
	
	
	public static byte[] exportUsersToExcel() throws IOException {
		
		List<UserDTO> userList = generateUserList();
		List<CompletableFuture<Void>> features = new ArrayList<>();
		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
		
        try (Workbook workbook = new XSSFWorkbook()) {
            // Calculate number of sheets needed
            int totalRecords = userList.size();
            int sheetsNeeded = (int) Math.ceil((double) totalRecords / 25000);
            
            // Create sheets sequentially (must be single-threaded)
            Sheet[] sheets = new Sheet[sheetsNeeded];
            for (int sheetNum = 0; sheetNum < sheetsNeeded; sheetNum++) {
                sheets[sheetNum] = workbook.createSheet("Users_" + (sheetNum + 1));
            }
            
            // Create each sheet
            for (int sheetNum = 0; sheetNum < sheetsNeeded; sheetNum++) {
                int startIndex = sheetNum * 25000;
                int endIndex = Math.min(startIndex + 25000, totalRecords);
                List<UserDTO> subList = userList.subList(startIndex, endIndex);
                
//                createSheet(workbook, "Users_" + (sheetNum + 1), subList);
                Sheet sheet = sheets[sheetNum];
                String sheetName = "Users_"+(sheetNum + 1);
                features.add(CompletableFuture.runAsync(() -> createSheet(sheet, sheetName, subList), executor));
            }
            
			// Wait for all threads to complete
			CompletableFuture.allOf(features.toArray(new CompletableFuture[0])).join();
            
            // Write workbook to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private static void createSheet(Sheet sheet, String sheetName, List<UserDTO> users) {
    	
    	System.out.println("Starting ..."+sheetName);
//        Sheet sheet = workbook.createSheet(sheetName);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Email", "First Name", "Last Name"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        // Create data rows
        int rowNum = 1;
        for (UserDTO user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getEmailId());
            row.createCell(1).setCellValue(user.getFirstName());
            row.createCell(2).setCellValue(user.getLastName());
        }
    }
	
	public static List<UserDTO> generateUserList() {
        List<UserDTO> userList = new ArrayList<>();
        for (int i = 1; i <= 100025; i++) {
            UserDTO user = new UserDTO();
            user.setEmailId("user" + i + "@example.com");
            user.setFirstName("FirstName" + i);
            user.setLastName("LastName" + i);
            userList.add(user);
        }
        return userList;
    }
	
}
