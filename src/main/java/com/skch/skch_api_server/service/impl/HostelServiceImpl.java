package com.skch.skch_api_server.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.pjfanning.xlsx.StreamingReader;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.skch.skch_api_server.common.Constant;
import com.skch.skch_api_server.dao.HostellerDAO;
import com.skch.skch_api_server.dao.HostellerGridDAO;
import com.skch.skch_api_server.dao.PaymentHistoryDAO;
import com.skch.skch_api_server.dto.ColumnFilter;
import com.skch.skch_api_server.dto.HostellerDTO;
import com.skch.skch_api_server.dto.HostellerGridDTO;
import com.skch.skch_api_server.dto.HostellerSearch;
import com.skch.skch_api_server.dto.PaymentHistoryDTO;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.dto.SearchResult;
import com.skch.skch_api_server.exception.CustomException;
import com.skch.skch_api_server.mapper.ObjectMapper;
import com.skch.skch_api_server.model.Hosteller;
import com.skch.skch_api_server.model.HostellerGrid;
import com.skch.skch_api_server.model.PaymentHistory;
import com.skch.skch_api_server.service.HostelService;
import com.skch.skch_api_server.util.DateUtility;
import com.skch.skch_api_server.util.ExcelUtil;
import com.skch.skch_api_server.util.JwtUtil;
import com.skch.skch_api_server.util.PdfHelper;
import com.skch.skch_api_server.util.Utility;
import com.skch.skch_api_server.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HostelServiceImpl implements HostelService {

	private ObjectMapper MAPPER = ObjectMapper.INSTANCE;

	@Autowired
	private HostellerDAO hostellerDAO;

	@Autowired
	private HostellerGridDAO hostellerGridDAO;

	@Autowired
	private PaymentHistoryDAO paymentHistoryDAO;
	
	@Autowired
	private BatchInsert batchInsert;
	
	@Value("${app.batch-size}")
	private int batchSize;

	/**
	 * Save Or Update the Hosteller
	 * 
	 * @param HostellerDTO dto
	 * @return the result
	 */
	@Override
	public Result saveOrUpdateHosteller(HostellerDTO dto) {

		log.info("Starting at saveOrUpdateHosteller.....");
		Result result = null;
		Hosteller hosteller = null;
		try {
			dto.setFee(Utility.isBlank(dto.getFee()));
			dto.setVacatedDate(Utility.isBlank(dto.getVacatedDate()));
			dto.setJoiningDate(Utility.isBlank(dto.getJoiningDate()));
			dto.setDob(Utility.isBlank(dto.getDob()));

			Map<String, String> errors = ValidationUtils.validate(dto);
			result = new Result();
			if (errors.isEmpty()) {
				log.info(">>>>>> Starting ...." + dto);
				if (dto.getHostellerId() == null || dto.getHostellerId() == 0) {
					hosteller = MAPPER.fromHostellerDTO(dto);
					Utility.updateFields(hosteller, "C");
					hosteller.setActive(true);
					if (hosteller.getJoiningDate() == null) {
						hosteller.setJoiningDate(LocalDate.now());
					}
					
					hosteller.setVacatedDate(DateUtility.stringToDateTimes(dto.getVacatedDate(),"dd-MM-yyyy"));
					
					hosteller = hostellerDAO.save(hosteller);

					result.setStatusCode(HttpStatus.OK.value());
					result.setSuccessMessage("Saved Successfully...");
					result.setData(hosteller);
				} else {
					Hosteller serverHosteller = hostellerDAO.findByHostellerId(dto.getHostellerId());

//					String[] ignore = { "active", "createdById", "createdDate" };
//					BeanUtils.copyProperties(dto, hosteller,ignore);
					
					hosteller = MAPPER.fromHostellerDTO(dto);
					hosteller.setActive(serverHosteller.getActive());
					hosteller.setCreatedDate(serverHosteller.getCreatedDate());
					hosteller.setCreatedById(serverHosteller.getCreatedById());
					hosteller.setVacatedDate(DateUtility.stringToDateTimes(dto.getVacatedDate(),"dd-MM-yyyy"));
					
					Utility.updateFields(hosteller, "U");

					hosteller = hostellerDAO.save(hosteller);

					result.setStatusCode(HttpStatus.OK.value());
					result.setSuccessMessage("Updated Successfully...");
					result.setData(hosteller);
				}
			} else {
				String commaSeparatedValues = errors.values().stream()
		                .collect(Collectors.joining(","));
				
				result.setStatusCode(HttpStatus.BAD_REQUEST.value());
				result.setErrorMessage("Object Is Not Valid :: "+commaSeparatedValues);
				result.setData(errors);
			}
			log.info("Ending at saveOrUpdateHosteller.....");
		} catch (Exception e) {
			log.error("Error at saveOrUpdateHosteller :: " + e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

	/**
	 * Save Or Update the PaymentHistory
	 * 
	 * @param PaymentHistoryDTO dto
	 * @return the result
	 */
	@Override
	public Result saveOrUpdatePaymentHistory(PaymentHistoryDTO dto) {
		log.info("Starting at saveOrUpdatePaymentHistory.....");
		Result result = null;
		PaymentHistory paymentHistory = null;
		try {
			dto.setFeePaid(Utility.isBlank(dto.getFeePaid()));
			dto.setFeeDue(Utility.isBlank(dto.getFeeDue()));
			dto.setFeeDate(Utility.isBlank(dto.getFeeDate()));
			result = new Result();
			if (dto.getPaymentId() == null || dto.getPaymentId() == 0) {
				paymentHistory = MAPPER.fromPaymentHistoryDTO(dto);
				paymentHistory.setCreatedDate(LocalDateTime.now());
				paymentHistory.setModifiedDate(LocalDateTime.now());
				paymentHistory = paymentHistoryDAO.save(paymentHistory);

				result.setStatusCode(HttpStatus.OK.value());
				result.setSuccessMessage("Saved Successfully...");
				result.setData(paymentHistory);
			} else {
				PaymentHistory serverPaymentHistory = paymentHistoryDAO.findByPaymentId(dto.getPaymentId());

				paymentHistory = MAPPER.fromPaymentHistoryDTO(dto);
				paymentHistory.setCreatedDate(serverPaymentHistory.getCreatedDate());
				paymentHistory.setCreatedById(serverPaymentHistory.getCreatedById());
				paymentHistory.setModifiedDate(LocalDateTime.now());

				paymentHistory = paymentHistoryDAO.save(paymentHistory);

				result.setStatusCode(HttpStatus.OK.value());
				result.setSuccessMessage("Updated Successfully...");
				result.setData(paymentHistory);
			}
			log.info("Ending at saveOrUpdatePaymentHistory.....");
		} catch (Exception e) {
			log.error("Error at saveOrUpdatePaymentHistory :: " + e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

	/**
	 * Getting All Hostellers.
	 */
	@Override
	public Result getHostellers(HostellerSearch search) {
		log.info("Starting at getHostellers.....:: {}",System.currentTimeMillis());
		Result result = new Result();
		try {
			if (!search.isExportExcel() && !search.isExportCsv() &&
					!search.isExportPdf() && !search.isExportZip()) {
				List<HostellerGrid> hostellerGridList = getHostelRecords(search);

				if (!hostellerGridList.isEmpty()) {
					SearchResult<HostellerGridDTO> searchResult = new SearchResult<>();
					List<HostellerGridDTO> dtoList = MAPPER.formHostelGridModel(hostellerGridList);
					searchResult.setContent(dtoList);
					searchResult.setPageNo(search.getPageNumber());
					searchResult.setPageSize(search.getPageSize());
					Long totalCount = dtoList.get(0).getTotalCount();
					searchResult.setTotalElements(totalCount);
					int totalPages = Utility.totalPages(totalCount, search.getPageSize());
					searchResult.setTotalPages(totalPages);

					result.setStatusCode(HttpStatus.OK.value());
					result.setSuccessMessage("getting Successfully...");
					result.setData(searchResult);
				} else {
					result.setStatusCode(HttpStatus.NOT_FOUND.value());
					result.setErrorMessage(Constant.NOT_FOUND);
				}
			} else if (search.isExportExcel()) {
				List<HostellerGrid> hostellerGridList = getHostelRecords(search);
				result.setBao(getHostelGridExcelNew(hostellerGridList));
				result.setFileName("Hostel_Data.xlsx");
				result.setType(MediaType.APPLICATION_OCTET_STREAM);
			} else if (search.isExportCsv()) {
				List<HostellerGrid> hostellerGridList = getHostelRecords(search);
				result.setBao(getHostelGridCsv(hostellerGridList));
				result.setFileName("Hostel_Data.csv");
				result.setType(MediaType.APPLICATION_OCTET_STREAM);
			}else if (search.isExportPdf()) {
				List<HostellerGrid> hostellerGridList = getHostelRecords(search);
				result.setBao(getHostelGridPdf(hostellerGridList));
				result.setFileName("Hostel_Data.pdf");
				result.setType(MediaType.APPLICATION_PDF);
			}else if (search.isExportZip()) {
				List<HostellerGrid> hostellerGridList = getHostelRecords(search);
				result.setBao(getHostelZip(hostellerGridList));
				result.setFileName("Hostel_Data.zip");
				result.setType(MediaType.APPLICATION_OCTET_STREAM);
			}

			log.info("Ending at getHostellers.....:: {}",System.currentTimeMillis());
		} catch (Exception e) {
			log.error("Error at getHostellers :: {} ",e.getMessage(), e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

	/**
	 * Get The Hostel Records from DB
	 * 
	 * @param search
	 * @return hostellerGridList
	 * 
	 */
	public List<HostellerGrid> getHostelRecords(HostellerSearch search) {
		List<HostellerGrid> hostellerGridList = new ArrayList<>();
		try {
//			Map<String, String> clf = new HashMap<>(Map.of(Constant.FULL_NAME, ""));
			Map<String, String> clf = new HashMap<>(
					Map.ofEntries(Map.entry(Constant.FULL_NAME, "")));

			if (ObjectUtils.isNotEmpty(search.getColumnFilters())) {
				for (ColumnFilter filter : search.getColumnFilters()) {
					if (clf.containsKey(filter.getColumn().getField())) {
						clf.put(filter.getColumn().getField(), filter.getValue());
					}
				}
			}

			hostellerGridList = hostellerGridDAO.getHostelData(Utility.nullCheck(search.getFullName()),
					Utility.nullCheck(search.getEmailId()), Utility.getDbColumn(search.getSortBy()),
					search.getSortOrder(), search.getPageNumber(), search.getPageSize(), search.isFullLoad(),
					clf.get(Constant.FULL_NAME));

		} catch (Exception e) {
			log.error("Error at getHostelRecords :: {}", e.getMessage(), e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return hostellerGridList;
	}
	
	public ByteArrayOutputStream getHostelGridExcelNew(List<HostellerGrid> hostellerGridList) {

	    try (FileInputStream inputStream = new FileInputStream(Constant.HOSTEL_TEMPLATE);
	         Workbook workbook = new XSSFWorkbook(inputStream);
	         ByteArrayOutputStream bao = new ByteArrayOutputStream()) {

	        Sheet sheet = workbook.getSheetAt(0);
	        CellStyle currencyStyle = ExcelUtil.cellStyle(workbook, Constant.CURRENCY_FORMAT);

	        Map<String, Integer> headerIndexMap = new HashMap<>();
			for (int i = 0; i < ExcelUtil.HOSTEL_HEADERS.size(); i++) {
				headerIndexMap.put(ExcelUtil.HOSTEL_HEADERS.get(i), i);
			}

	        int rowid = 1;
	        for (HostellerGrid hosteller : hostellerGridList) {
	            Row row = sheet.createRow(rowid++);
	            
	            row.createCell(headerIndexMap.get("Full Name"))
	                .setCellValue(Utility.nullCheck(hosteller.getFullName()));

	            row.createCell(headerIndexMap.get("Email Id"))
	                .setCellValue(Utility.nullCheck(hosteller.getEmailId()));

	            row.createCell(headerIndexMap.get("Phone Number"))
	                .setCellValue(Utility.nullCheck(hosteller.getPhoneNumber()));

	            row.createCell(headerIndexMap.get("DOB"))
	                .setCellValue(DateUtility.dateToString(hosteller.getDob(), Constant.DATE_FORMAT));

	            Cell feeCell = row.createCell(headerIndexMap.get("Fee"));
	            feeCell.setCellStyle(currencyStyle);
	            feeCell.setCellValue(Utility.toDouble(hosteller.getFee()));

	            row.createCell(headerIndexMap.get("Joining Date"))
	                .setCellValue(DateUtility.dateToString(hosteller.getJoiningDate(), Constant.DATE_FORMAT));

	            row.createCell(headerIndexMap.get("Address"))
	                .setCellValue(Utility.nullCheck(hosteller.getAddress()));

	            row.createCell(headerIndexMap.get("Proof"))
	                .setCellValue(Utility.nullCheck(hosteller.getProof()));

	            row.createCell(headerIndexMap.get("Reason"))
	                .setCellValue(Utility.nullCheck(hosteller.getReason()));

	            row.createCell(headerIndexMap.get("Vacated Date"))
	                .setCellValue(DateUtility.dateToString(hosteller.getVacatedDate(), Constant.DATE_FORMAT));

	            row.createCell(headerIndexMap.get("Active"))
	                .setCellValue(Utility.nullCheck(hosteller.getActive()) ? "Yes" : "No");
	        }

	        workbook.write(bao); // Write the workbook data to the output stream
	        return bao;

	    } catch (Exception e) {
	        log.error("Error in getHostelGridExcelNew :: ", e);
	        return new ByteArrayOutputStream(); // Return an empty stream on error
	    }
	}


	/**
	 * Get the Hostel Grid Excel
	 * 
	 * @param search
	 * @return bao
	 */
	public ByteArrayOutputStream getHostelGridExcel(List<HostellerGrid> hostellerGridList) {
		ByteArrayOutputStream bao = null;
		Workbook workbook = null;
		try {
			FileInputStream inputStream = new FileInputStream(Constant.HOSTEL_TEMPLATE);
			workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);

			CellStyle currencyStyle = ExcelUtil.cellStyle(workbook, Constant.CURRENCY_FORMAT);

			int rowid = 1;
			for (HostellerGrid Hosteller : hostellerGridList) {
				Row row = sheet.createRow(rowid++);
				row.createCell(0).setCellValue(Utility.nullCheck(Hosteller.getFullName()));
				row.createCell(1).setCellValue(Utility.nullCheck(Hosteller.getEmailId()));
				row.createCell(2).setCellValue(Utility.nullCheck(Hosteller.getPhoneNumber()));

				row.createCell(3).setCellValue(DateUtility.dateToString(Hosteller.getDob(), "yyyy-MM-dd"));
				
				Cell cell4 = row.createCell(4);
				cell4.setCellStyle(currencyStyle);
				cell4.setCellValue(Utility.toDouble(Hosteller.getFee()));

				row.createCell(5).setCellValue(DateUtility.dateToString(Hosteller.getJoiningDate(), "yyyy-MM-dd"));

				row.createCell(6).setCellValue(Utility.nullCheck(Hosteller.getAddress()));
				row.createCell(7).setCellValue(Utility.nullCheck(Hosteller.getProof()));
				row.createCell(8).setCellValue(Utility.nullCheck(Hosteller.getReason()));

				row.createCell(9).setCellValue(DateUtility.dateToString(Hosteller.getVacatedDate(), "yyyy-MM-dd"));
				row.createCell(10).setCellValue(Utility.nullCheck(Hosteller.getActive()) ? "Yes" : "No");
			}

			bao = new ByteArrayOutputStream();
			workbook.write(bao); // Write the workbook to temp byte array
		} catch (Exception e) {
			log.error("Error in getHostelGridExcel :: ", e);
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					log.error("Error in Closing Workbook :: ", e);
				}
			}
		}
		return bao;
	}
	
	/**
	 * Get the Hostel Grid Excel
	 * 
	 * @param search
	 * @return bao
	 */
	public ByteArrayOutputStream getHostelGridCsv(List<HostellerGrid> hostellerGridList) {
	    ByteArrayOutputStream bao = new ByteArrayOutputStream();
	    try (OutputStreamWriter osw = new OutputStreamWriter(bao, StandardCharsets.UTF_8);
	         CSVWriter csvWriter = new CSVWriter(osw, ICSVWriter.DEFAULT_SEPARATOR, ICSVWriter.NO_QUOTE_CHARACTER, 
	        		 ICSVWriter.NO_ESCAPE_CHARACTER,ICSVWriter.DEFAULT_LINE_END)) {

	    	//CSVWriter.DEFAULT_SEPARATOR is ',' if want change that to '|' also
	    	
	        // Write headers
	        String[] headers = ExcelUtil.HOSTEL_HEADERS.toArray(new String[0]);
	        csvWriter.writeNext(headers);

	        // Write data rows
	        hostellerGridList.forEach(model -> csvWriter.writeNext(model.getData()));
	        
	        csvWriter.flush();
	        
	    } catch (Exception e) {
	        log.error("Error in getHostelGridCsv: ", e);
	    }
	    return bao;
	}

	/**
	 * Get the Hostel Grid Pdf
	 * 
	 * @param search
	 * @return bao
	 */
	public ByteArrayOutputStream getHostelGridPdf(List<HostellerGrid> hostellerGridList) {
		ByteArrayOutputStream baos;
		try {
//			Rectangle pagesize = new Rectangle(1754, 1240);
			Document document = new Document(PageSize.A4, 30, 30, 45, 30); // Create a new document
			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos); // Create a new PDF writer
			document.open(); // Open the document

			PdfHelper.lineSeparator(document, "Hosteller Data");

			Paragraph tableTitle = PdfHelper.createParagraph("Number of Hostellers (" + hostellerGridList.size() + ")",
					14, 5, PdfHelper.getPoppinsFont(12, new BaseColor(255, 165, 0)));
			document.add(tableTitle);

			PdfPTable mainTable = PdfHelper.createTable(11, 5, 5, 100);
			mainTable.setTotalWidth(new float[] { 10, 14, 10, 4, 8, 8, 12, 10, 10, 8, 6 });

			// Add table headers
			ExcelUtil.HOSTEL_HEADERS.forEach(header -> {
				PdfHelper.headerCell(mainTable, header, new BaseColor(229, 242, 255),
						PdfHelper.getPoppinsFont(8, null));
			});

			// Table Data
			for (HostellerGrid data : hostellerGridList) {
				PdfHelper.createPdfPCell(mainTable, data.getFullName(), PdfHelper.getPoppinsFont(6, null), 5,
						Element.ALIGN_CENTER);
				PdfHelper.createPdfPCell(mainTable, data.getEmailId(), PdfHelper.getPoppinsFont(6, null), 5,
						Element.ALIGN_CENTER);
				PdfHelper.createPdfPCell(mainTable, data.getPhoneNumber(), PdfHelper.getPoppinsFont(6, null), 5,
						Element.ALIGN_CENTER);
				PdfHelper.createPdfPCell(mainTable, DateUtility.dateToString(data.getDob(),"yyyy-MM-dd"),
						PdfHelper.getPoppinsFont(6, null), 5, Element.ALIGN_CENTER);
				PdfHelper.createPdfPCell(mainTable, PdfHelper.numberFormatGrid(data.getFee()),
						PdfHelper.getPoppinsFont(6, null), 5, Element.ALIGN_RIGHT);
				PdfHelper.createPdfPCell(mainTable, DateUtility.dateToString(data.getJoiningDate(),"yyyy-MM-dd"),
						PdfHelper.getPoppinsFont(6, null), 5, Element.ALIGN_CENTER);
				PdfHelper.createPdfPCell(mainTable, data.getAddress(), PdfHelper.getPoppinsFont(6, null), 5,
						Element.ALIGN_CENTER);
				PdfHelper.createPdfPCell(mainTable, data.getProof(), PdfHelper.getPoppinsFont(6, null), 5,
						Element.ALIGN_CENTER);
				PdfHelper.createPdfPCell(mainTable, data.getReason(), PdfHelper.getPoppinsFont(6, null), 5,
						Element.ALIGN_CENTER);
				PdfHelper.createPdfPCell(mainTable, DateUtility.dateToString(data.getVacatedDate(),"yyyy-MM-dd"),
						PdfHelper.getPoppinsFont(6, null), 5, Element.ALIGN_CENTER);
				PdfHelper.createPdfPCell(mainTable, data.getActive() ? "Yes" : "No", PdfHelper.getPoppinsFont(6, null), 5,
						Element.ALIGN_CENTER);
			}

			document.add(mainTable);

			document.close();// Close the document
		} catch (Exception e) {
			log.error("error in generate Pdf ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return baos;
	}
	
	/**
	 * Get the Hostel Grid ZIP
	 * 
	 * @param hostellerGridList
	 * @return bao
	 */
	public ByteArrayOutputStream getHostelZip(List<HostellerGrid> hostellerGridList) {
		ByteArrayOutputStream baos = null;
		log.info("Inside Zip Starting...");
		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
		try {
			
			CompletableFuture<ByteArrayOutputStream> excel = CompletableFuture.supplyAsync(() -> {
				try {
					log.info(">>Thread Name: " + Thread.currentThread());
					return getHostelGridExcel(hostellerGridList);
				} catch (Exception e) {
					log.error("Error in Excel getHostelZip :: ", e);
					throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}, executor);

			CompletableFuture<ByteArrayOutputStream> pdf = CompletableFuture.supplyAsync(() -> {
				try {
					log.info(">>Thread Name: " + Thread.currentThread());
					return getHostelGridPdf(hostellerGridList);
				} catch (Exception e) {
					log.error("Error in PDF getHostelZip :: ", e);
					throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}, executor);
			
			CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(excel, pdf);
			allOfFuture.get();
			
			baos = new ByteArrayOutputStream();
			try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(baos))) {
				Utility.createFileToZip(zos, "Hostel_Data.xlsx", excel.get());
				Utility.createFileToZip(zos, "Hostel_Data.pdf", pdf.get());
				// Add more files to the zip if needed
				zos.finish();
			}
			log.info("Inside Zip Ending...");
		} catch (Exception e) {
			log.error("error in getHostelZip :: ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			executor.shutdown();
		}
		return baos;
	}

	/**
	 * This Method is validate the file and Upload to shared path
	 *
	 * @param file
	 * @param dto
	 * @return result
	 */
	@Override
	public Result uploadFile(MultipartFile file) {
		Result result = new Result();
		Long userId = JwtUtil.getUserId();

		try {
			long intialTime = System.currentTimeMillis();

			if (ExcelUtil.excelType(file)) {
				try (Workbook workbook = file.getContentType().equals(ExcelUtil.XLS_TYPE)
						? new HSSFWorkbook(file.getInputStream())
						: StreamingReader.builder().rowCacheSize(100).bufferSize(4096)
									.open(file.getInputStream())) {

					Sheet sheet = ExcelUtil.getFirstSheet(workbook);
					String headerCheck = ExcelUtil.headerCheck(sheet, ExcelUtil.HOSTEL_HEADERS);
					log.info(headerCheck);

					if (headerCheck.isBlank()) {
						long totalRecords = sheet.getLastRowNum();
						log.info("Total Records :: " + totalRecords);

						// Method to Save the Data Synchronously
						getRowValues(sheet, userId);

						result.setData("Uploaded " + totalRecords + " records.");
					} else {
						result.setErrorMessage(headerCheck);
					}
				}
			} else if (ExcelUtil.csvType(file)) {
				try (CSVReader csvReader = new CSVReader(
						new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

					List<String[]> csvDataList = csvReader.readAll();
					String headerCheck = ExcelUtil.headerCheckCsv(csvDataList, ExcelUtil.HOSTEL_HEADERS);
					log.info(headerCheck);

					if (headerCheck.isBlank()) {
						long totalRecords = csvDataList.size() - 1L;

						CompletableFuture.runAsync(() -> getCsvValues(csvDataList, userId));

						log.info("Count of Records :: " + totalRecords);

						result.setData("Uploaded " + totalRecords + " records.");
					} else {
						result.setErrorMessage(headerCheck);
					}
				}
			} else {
				result.setErrorMessage("The uploaded file is not Present or Not CSV or an Excel file");
			}

			long finalTime = System.currentTimeMillis();
			log.info("???>>>???::: TotalTime : " + (finalTime - intialTime));

		} catch (Exception e) {
			log.error("Error in uploadFile :: ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

	/**
	 * Method to Excel Sheet Data in Threads Batch Update
	 * @param sheet
	 * @param userId
	 */
	public void getRowValues(Sheet sheet, Long userId) {
		ArrayList<HostellerDTO> dataList = new ArrayList<>();
		List<HostellerDTO> errorList = new ArrayList<>();
		List<CompletableFuture<Void>> features = new ArrayList<>();
		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		Map<String, Integer> countMap = new HashMap<>(Map.of("S", 0, "F", 0));
		try {
			sheet.forEach(row -> {
				if (row.getRowNum() == 0) {
					return;
				}
				List<String> cellValues = IntStream.range(0, ExcelUtil.HOSTEL_HEADERS.size()).mapToObj(i -> {
					Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					return ExcelUtil.getCellValue(cell);
				}).toList();

				HostellerDTO dto = new HostellerDTO(cellValues, userId);
				Map<String, String> errors = ValidationUtils.validate(dto);

				if (errors.isEmpty()) {
					dataList.add(dto);
					if (!dataList.isEmpty() && dataList.size() % batchSize == 0) {
						List<HostellerDTO> batch = new ArrayList<>(dataList);
						features.add(CompletableFuture.runAsync(() -> batchInsert.saveInBatchHostellers(batch), executor));
						Integer count = countMap.get("S") + dataList.size();
						countMap.put("S", count);
						dataList.clear();
					}
				} else {
					dto.setError(String.join(",", errors.values()));
					errorList.add(dto);
					log.info("Validation error: {}", errors);
				}
			});

			if (!dataList.isEmpty()) {
				List<HostellerDTO> batch = new ArrayList<>(dataList);
				features.add(CompletableFuture.runAsync(() -> batchInsert.saveInBatchHostellers(batch), executor));
				Integer count = countMap.get("S") + dataList.size();
				countMap.put("S", count);
			}
			
			countMap.put("F", errorList.size());

			// Wait for all threads to finish and collect their results
        CompletableFuture.allOf(features.toArray(new CompletableFuture[0])).join();
        
		log.info("Success {} records , failed {} records.", countMap.get("S"), countMap.get("F"));

		} catch (Exception e) {
			log.error("Error processing rows :: {} ", e.getMessage(), e);
		} finally {
			executor.shutdown();
		}
	}
	
	/**
	 * Method to Save Records Csv to DB
	 */
	public void getCsvValues(List<String[]> csvDataList ,Long userId) {
		ArrayList<HostellerDTO> dataList = new ArrayList<>();
		List<HostellerDTO> errorList = new ArrayList<>();
		List<CompletableFuture<Void>> features = new ArrayList<>();
		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		Map<String, Integer> countMap = new HashMap<>(Map.of("S", 0, "F", 0));
		
		try {
			//Remove Headers
			csvDataList.removeFirst(); //From Java 21
//			csvDataList.remove(0);
			
			csvDataList.forEach(data ->{
				
				HostellerDTO dto = new HostellerDTO(data,userId);
				Map<String, String> errors = ValidationUtils.validate(dto);

				if (errors.isEmpty()) {
					if (!dataList.isEmpty() && dataList.size() % batchSize == 0) {
						features.add(CompletableFuture.runAsync(() -> 
							batchInsert.saveInBatchHostellers(dataList), executor));
						Integer count = countMap.get("S") + dataList.size();
						countMap.put("S", count);
						dataList.clear();
					}
					dataList.add(dto);
				} else {
					String error = errors.values().stream().collect(Collectors.joining(","));
					dto.setError(error);
					errorList.add(dto);
//					Integer count = countMap.get("F") + errorList.size();
//					countMap.put("F", count);
					log.info("Error :: " + error);
				}
				
			});
			
			if (!dataList.isEmpty()) {
				features.add(CompletableFuture.runAsync(() -> 
					batchInsert.saveInBatchHostellers(dataList), executor));
				Integer count = countMap.get("S") + dataList.size();
				countMap.put("S", count);
			}

			// Wait for all threads to finish and collect their results
//	        CompletableFuture.allOf(features.toArray(new CompletableFuture[0])).join();
			
			log.info("List Success Count :: " + countMap.get("S"));
			log.info("List Error Count :: " + errorList.size());
//			log.info("List Error Size :: " + errorList);

		} catch (Exception e) {
			log.error("Error in getCsvValues :: " + e);
		} finally {
			executor.shutdown();
		}
	}
	
	
	/**
	 * Download the Hostel Template
	 */
	@Override
	public ByteArrayOutputStream getHostelTemplate() {
		ByteArrayOutputStream bao = null;
		Workbook workbook = null;
		try {
			FileInputStream inputStream = new FileInputStream(Constant.HOSTEL_TEMPLATE);
			workbook = new XSSFWorkbook(inputStream);

			bao = new ByteArrayOutputStream();
			workbook.write(bao); // Write the workbook to temp byte array
		} catch (Exception e) {
			log.error("Error in get Hostel Template :: ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (workbook != null) {
					workbook.close();// close the workbook
				}
			} catch (IOException e) {
				log.error("Error in Closing Workbook :: ", e);
			}
		}
		return bao;
	}

}
