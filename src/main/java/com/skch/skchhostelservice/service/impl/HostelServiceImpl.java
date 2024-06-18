package com.skch.skchhostelservice.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import com.skch.skchhostelservice.util.ExcelUtil;
import com.skch.skchhostelservice.util.Utility;

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

			log.info(">>>>>> Starting ...." + dto);
			result = new Result();
			if (dto.getHostellerId() == null || dto.getHostellerId() == 0) {
				hosteller = MAPPER.fromHostellerDTO(dto);
//				hosteller.setCreatedById(JwtUtil.getUserId());
//				hosteller.setModifiedById(JwtUtil.getUserId());
//				hosteller.setCreatedDate(LocalDateTime.now());
//				hosteller.setModifiedDate(LocalDateTime.now());
				Utility.updateFields(hosteller,"C");
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
//				hosteller.setModifiedDate(LocalDateTime.now());

				Utility.updateFields(hosteller,"U");
				
				hosteller = hostellerDAO.save(hosteller);

				result.setStatusCode(HttpStatus.OK.value());
				result.setSuccessMessage("Updated Successfully...");
				result.setData(hosteller);
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

//			String clfFullName = "";
//
//			if (search.getColumnFilters() != null) {
//				for (ColumnFilter filter : search.getColumnFilters()) {
//					if (filter.getColumn().getField().equals("fullName")) {
//						clfFullName = filter.getValue();
//					}
//				}
//			}
			
			Map<String,String> clf = new HashMap<>();
			
			String [] columns = {Constant.FULL_NAME};
			
			for(String column : columns) {
				clf.put(column, "");
			}
			
			if (search.getColumnFilters() != null) {
				for (ColumnFilter filter : search.getColumnFilters()) {
					if (clf.containsKey(filter.getColumn().getField())) {
						clf.put(filter.getColumn().getField(), filter.getValue());
					}
				}
			}

			List<HostellerGrid> allHostellers = hostellerGridDAO.getHostelData(
					Utility.nullCheck(search.getFullName()),Utility.nullCheck(search.getEmailId()),
					Utility.getDbColumn(search.getSortBy()),search.getSortOrder(),
					search.getPageNumber(),search.getPageSize(),search.isExport(),
					clf.get(Constant.FULL_NAME));
			
			if (!allHostellers.isEmpty()) {
				SearchResult<HostellerGridDTO> searchResult = new SearchResult<>();
				List<HostellerGridDTO> dtoList = MAPPER.formHostelGridModel(allHostellers);
				searchResult.setContent(dtoList);
				searchResult.setPageNo(search.getPageNumber());
				searchResult.setPageSize(search.getPageSize());
				Long totalCount = allHostellers.get(0).getTotalCount();
				searchResult.setTotalElements(totalCount);
				int totalPages = Utility.totalPages(totalCount,search.getPageSize());
				searchResult.setTotalPages(totalPages);
				
				result.setStatusCode(HttpStatus.OK.value());
				result.setSuccessMessage("getting Successfully...");
				result.setData(searchResult);
			} else {
				result.setStatusCode(HttpStatus.NOT_FOUND.value());
				result.setErrorMessage(Constant.NOT_FOUND);
			}

			log.info("Ending at getHostellers.....");
		} catch (Exception e) {
			log.error("Error at getHostellers :: " + e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
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
			if (ExcelUtil.excelType(file)) {
				
				workbook = new XSSFWorkbook(file.getInputStream());
				XSSFSheet sheet = workbook.getSheetAt(0);
				Row headerRow = sheet.getRow(0);
				
				if (ExcelUtil.headerCheck(headerRow, ExcelUtil.HOSTEL_HEADERS)) {
					if (sheet.getLastRowNum() > 0) {
						// Method to Save the Data
						getRowValues(sheet);
						result.setData("Saved Data..");
					} else {
						result.setData("Empty Template Uploaded");
					}
				} else {
					result.setErrorMessage("Headers not matched");
				}
			} else {
				result.setErrorMessage("The uploaded file is not Present or Not an Excel file");
			}
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
	
	public void getRowValues(XSSFSheet sheet) {
		
		List<Hosteller> dataList = new ArrayList<>();
		
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.forEachRemaining(row -> {
			if (row.getRowNum() == 0) {
				return;
			}
			List<String> cellValues = IntStream.range(0, ExcelUtil.HOSTEL_HEADERS.length)
					.mapToObj(i -> {
				Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				return ExcelUtil.getCellValue(cell);
			}).collect(Collectors.toList());
			
			dataList.add(new Hosteller(cellValues));
		});
		
		hostellerDAO.saveAll(dataList);
		
		log.info("List DTO :: "+dataList);
		log.info("List DTO Size :: "+dataList.size());
		
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
