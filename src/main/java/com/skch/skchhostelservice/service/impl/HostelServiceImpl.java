package com.skch.skchhostelservice.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.skch.skchhostelservice.common.Constant;
import com.skch.skchhostelservice.dao.HostellerDAO;
import com.skch.skchhostelservice.dao.HostellerGridDAO;
import com.skch.skchhostelservice.dao.PaymentHistoryDAO;
import com.skch.skchhostelservice.dto.ColumnFilter;
import com.skch.skchhostelservice.dto.HostellerDTO;
import com.skch.skchhostelservice.dto.HostellerGridDTO;
import com.skch.skchhostelservice.dto.HostellerSearch;
import com.skch.skchhostelservice.dto.PaymentHistoryDTO;
import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.dto.SearchResult;
import com.skch.skchhostelservice.exception.CustomException;
import com.skch.skchhostelservice.mapper.ObjectMapper;
import com.skch.skchhostelservice.model.Hosteller;
import com.skch.skchhostelservice.model.HostellerGrid;
import com.skch.skchhostelservice.model.PaymentHistory;
import com.skch.skchhostelservice.service.HostelService;
import com.skch.skchhostelservice.util.DateUtility;
import com.skch.skchhostelservice.util.ExcelUtil;
import com.skch.skchhostelservice.util.PdfHelper;
import com.skch.skchhostelservice.util.Utility;
import com.skch.skchhostelservice.util.ValidationUtils;

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
					hosteller = hostellerDAO.save(hosteller);

					result.setStatusCode(HttpStatus.OK.value());
					result.setSuccessMessage("Saved Successfully...");
					result.setData(hosteller);
				} else {
					Hosteller serverHosteller = hostellerDAO.findByHostellerId(dto.getHostellerId());

					hosteller = MAPPER.fromHostellerDTO(dto);
					hosteller.setActive(serverHosteller.getActive());
					hosteller.setCreatedDate(serverHosteller.getCreatedDate());
					hosteller.setCreatedById(serverHosteller.getCreatedById());
					hosteller.setVacatedDate(serverHosteller.getVacatedDate());

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
		log.info("Starting at getHostellers.....");
		Result result = new Result();
		try {
			if (!search.isExportExcel() && !search.isExportPdf() && !search.isExportZip()) {
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
				result.setBao(getHostelGridExcel(hostellerGridList));
				result.setFileName("Hostel_Data.xlsx");
				result.setType(MediaType.APPLICATION_OCTET_STREAM);
			} else if (search.isExportPdf()) {
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

			log.info("Ending at getHostellers.....");
		} catch (Exception e) {
			log.error("Error at getHostellers :: " + e);
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
			Map<String, String> clf = new HashMap<>(Map.of(Constant.FULL_NAME, ""));

			if (search.getColumnFilters() != null) {
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
			log.error("Error at getHostelRecords :: " + e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return hostellerGridList;
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

				Cell cell3 = row.createCell(3);
				cell3.setCellStyle(currencyStyle);
				cell3.setCellValue(Utility.toDouble(Hosteller.getFee()));

				row.createCell(4).setCellValue(DateUtility.dateToString(Hosteller.getJoiningDate(), "yyyy-MM-dd"));

				row.createCell(5).setCellValue(Utility.nullCheck(Hosteller.getAddress()));
				row.createCell(6).setCellValue(Utility.nullCheck(Hosteller.getProof()));
				row.createCell(7).setCellValue(Utility.nullCheck(Hosteller.getReason()));

				row.createCell(8).setCellValue(DateUtility.dateToString(Hosteller.getVacatedDate(), "yyyy-MM-dd"));
				row.createCell(9).setCellValue(Utility.nullCheck(Hosteller.getActive()) ? "Yes" : "No");
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

			PdfPTable mainTable = PdfHelper.createTable(10, 5, 5, 100);
			mainTable.setTotalWidth(new float[] { 12, 14, 12, 8, 8, 12, 10, 10, 8, 6 });

			// Add table headers
			List<String> headers = Arrays.asList(ExcelUtil.HOSTEL_HEADERS);
			headers.forEach(header -> {
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
					log.error("error in getHostelZip :: ", e);
					throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}, executor);

			CompletableFuture<ByteArrayOutputStream> pdf = CompletableFuture.supplyAsync(() -> {
				try {
					log.info(">>Thread Name: " + Thread.currentThread());
					return getHostelGridPdf(hostellerGridList);
				} catch (Exception e) {
					log.error("error in getHostelZip :: ", e);
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
		XSSFWorkbook workbook = null;
		try {
			long intialTime = System.currentTimeMillis();

			if (ExcelUtil.excelType(file)) {

				workbook = new XSSFWorkbook(file.getInputStream());
				XSSFSheet sheet = workbook.getSheetAt(0);
				Row headerRow = sheet.getRow(0);

				if (ExcelUtil.headerCheck(headerRow, ExcelUtil.HOSTEL_HEADERS)) {
					long totalRecords = sheet.getLastRowNum();
					if (totalRecords > 0) {
						// Method to Save the Data
						getRowValues(sheet);
						result.setData("Uploaded " + totalRecords + " records.");
					} else {
						result.setData("Empty Template Uploaded");
					}
				} else {
					result.setErrorMessage("Headers not matched");
				}
			} else {
				result.setErrorMessage("The uploaded file is not Present or Not an Excel file");
			}
		
		long finalTime = System.currentTimeMillis();
		log.info("???>>>???::: TotalTime : " + (finalTime - intialTime));
		
		} catch (Exception e) {
			log.error("Error in uploadFile :: " + e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Autowired
	private HostelBatch hostelBatch;
	
	public void getRowValues(XSSFSheet sheet) {
		ArrayList<HostellerDTO> dataList = new ArrayList<>();
		List<HostellerDTO> errorList = new ArrayList<>();
		List<CompletableFuture<Void>> features = new ArrayList<>();
		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		int batchSize = 1000;
//		Map<String,Integer> mapData = new HashMap<>();
		List<Integer> succCount = new ArrayList<>();
		
		sheet.forEach(row -> {
			if (row.getRowNum() == 0) {
				return;
			}
			List<String> cellValues = IntStream.range(0, ExcelUtil.HOSTEL_HEADERS.length).mapToObj(i -> {
				Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				return ExcelUtil.getCellValue(cell);
			}).collect(Collectors.toList());
			
			HostellerDTO dto = new HostellerDTO(cellValues);
			Map<String, String> errors = ValidationUtils.validate(dto);

			if (errors.isEmpty()) {
				if (!dataList.isEmpty() && dataList.size() % batchSize == 0) {
					features.add(saveRecordsInBatch(new ArrayList<>(dataList), executor));
					succCount.add(dataList.size());
					dataList.clear();
				}
				dataList.add(dto);
			} else {
				String error = errors.values().stream().collect(Collectors.joining(","));
				dto.setError(error);
				errorList.add(dto);
				log.info("Error :: " + error);
			}
		});
		
		if (!dataList.isEmpty()) {
			features.add(saveRecordsInBatch(new ArrayList<>(dataList), executor));
			succCount.add(dataList.size());
		}

//		CompletableFuture<Void> allFeatures = CompletableFuture.allOf(features.toArray(new CompletableFuture[0]));
//		allFeatures.join();
		
		int sum = succCount.stream().mapToInt(Integer::intValue).sum();
		log.info("List DTO Size :: " + sum);
//		log.info("List Error Size :: " + errorList.size());
//		log.info("List Error Size :: " + errorList);

		executor.shutdown();
	}
	
	public CompletableFuture<Void> saveRecordsInBatch(ArrayList<HostellerDTO> records, 
			ExecutorService executor) {
//		log.info("Started method.... Batch size: " + records.size());
		return CompletableFuture.runAsync(() -> {
			try {
				hostelBatch.saveInBatch(records);
			} catch (Exception e) {
				log.error("Error in saveRecordsInBatch :: " + e);
				throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}, executor);
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
