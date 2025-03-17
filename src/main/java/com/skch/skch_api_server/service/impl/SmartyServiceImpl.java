package com.skch.skch_api_server.service.impl;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import com.github.pjfanning.xlsx.StreamingReader;
import com.opencsv.CSVReader;
import com.skch.skch_api_server.dto.LookupDto;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.dto.SmartyFileUploadDTO;
import com.skch.skch_api_server.exception.CustomException;
import com.skch.skch_api_server.service.SmartyService;
import com.skch.skch_api_server.util.ExcelUtil;
import com.smartystreets.api.exceptions.BatchFullException;
import com.smartystreets.api.us_street.Batch;
import com.smartystreets.api.us_street.Client;
import com.smartystreets.api.us_street.Lookup;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmartyServiceImpl implements SmartyService {
	
	@Autowired
	private Client client;
	
	@Value("${smarty.batch-size}")
	private int batchSize;

	/**
	 * Request Single Address for Standardize 
	 * 
	 * @param dto
	 * @return result
	 */
	@Override
	public Result getSingleRequest(LookupDto dto) {
		Result result = new Result();
		try {
			Lookup lookup = new Lookup();
			BeanUtils.copyProperties(dto, lookup);
			client.send(lookup);
			
			if(ObjectUtils.isNotEmpty(lookup.getResult())) {
				result.setData(lookup);
				result.setStatusCode(HttpStatus.OK.value());
				result.setSuccessMessage("Address standardized successfully.");
			}else {
				result.setStatusCode(HttpStatus.BAD_REQUEST.value());
				result.setErrorMessage("The request is for a non-standardized address.");
			}
		}catch(Exception e) {
			log.error("Error in SingleRequest :: ", e);
		}
		return result;
	}

	/**
	 * Request Bulk Address for Standardize 
	 * 
	 * @param dtoList
	 * @return result
	 */
	@Override
	public Result getBulkRequest(List<LookupDto> dtoList) {
		Result result = new Result();
		try {
			Map<Boolean, List<Lookup>> bulkProcess = bulkProcess(dtoList);
			result.setData(bulkProcess);
			result.setStatusCode(HttpStatus.OK.value());
			result.setSuccessMessage("The process Completed.");
		}catch(Exception e) {
			log.error("Error in BulkRequest :: ", e);
		}
		return result;
	}
	
	/**
	 * Bulk Process
	 * @param dtoList
	 */
	public Map<Boolean, List<Lookup>> bulkProcess(List<LookupDto> dtoList) {
//        ExecutorService executor = Executors.newFixedThreadPool(10); // Adjust pool size as needed
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        Map<Boolean, List<Lookup>> partitioned = null;
		try {
			
			List<CompletableFuture<Vector<Lookup>>> futures = IntStream.range(0, (dtoList.size() + batchSize - 1) / batchSize)
	                .mapToObj(i -> dtoList.subList(i * batchSize, Math.min((i + 1) * batchSize, dtoList.size())))
	                .map(batchList -> CompletableFuture.supplyAsync(() -> processBatch(batchList), executor))
	                .collect(Collectors.toList());

			log.info("Future Size :: {}",futures.size());
			
			Vector<Lookup> processBatchList = futures.stream()
					.map(future -> future.exceptionally(ex -> {
						log.error("Exception occurred: ", ex);
						return new Vector<Lookup>();
					})).map(CompletableFuture::join) // Waits for each future to complete
					.flatMap(List::stream).collect(Collectors.toCollection(Vector::new));
			
			// Partition into standardized & non-standardized in a single stream
	        partitioned = processBatchList.stream()
	                .collect(Collectors.partitioningBy(lookup -> !lookup.getResult().isEmpty()));

	        List<Lookup> standardized = partitioned.get(true);  // Standardized addresses
	        List<Lookup> nonStandardized = partitioned.get(false); // Non-standardized addresses

	        log.info("Standardized Size {} , Non-standardized Size {} ", 
	        		standardized.size(), nonStandardized.size());
	        
	        //Insert Bulk here for Standardized,Non-standardized 
		} catch (Exception e) {
			log.error("Error in BulkRequest :: ", e);
		}finally {
			executor.shutdown();
		}
		return partitioned;
	}
	
	public Vector<Lookup> processBatch(List<LookupDto> dtoList) {
		Vector<Lookup> allLookups = null;
		try {
			Batch batch = new Batch();
			dtoList.forEach(dto -> {
				try {
					batch.add(fromDto(dto));
				} catch (BatchFullException e) {
					log.error("Error in BatchFullException :: ", e);
				}
			});
			
			client.send(batch);
			allLookups = batch.getAllLookups();
			
		} catch (Exception e) {
			log.error("Error in processBatch :: ", e);
		}
		return allLookups;
	}
	
	public Lookup fromDto(LookupDto dto) {
		Lookup lookup = new Lookup();
		BeanUtils.copyProperties(dto, lookup);
		return lookup;
	}

	/**
	 * Read the file and Send the Request to Smarty
	 * @param file
	 * @param dto
	 * @return result
	 */
	@Override
	public Result uploadSmartyFile(MultipartFile file, SmartyFileUploadDTO dto) {
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		Result result = new Result();
//		Long userId = JwtUtil.getUserId();
		try {
			if (ExcelUtil.excelType(file)) {
				try (Workbook workbook = file.getContentType().equals(ExcelUtil.XLS_TYPE)
						? new HSSFWorkbook(file.getInputStream())
						: StreamingReader.builder().rowCacheSize(100).bufferSize(4096)
									.open(file.getInputStream())) {

					Sheet sheet = ExcelUtil.getFirstSheet(workbook);
					String headerCheck = ExcelUtil.headerCheck(sheet, ExcelUtil.SMARTY_HEADERS);
					log.info(headerCheck);

					if (headerCheck.isBlank()) {
						long totalRecords = sheet.getLastRowNum();
						log.info("Total Records :: " + totalRecords);

						// Method to Save the Data Synchronously
//						getRowValues(sheet, userId);

						result.setData("Uploaded " + totalRecords + " records.");
					} else {
						result.setErrorMessage(headerCheck);
					}
				}
			} else if (ExcelUtil.csvType(file)) {
				try (CSVReader csvReader = new CSVReader(
						new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

					List<String[]> csvDataList = csvReader.readAll();
					String headerCheck = ExcelUtil.headerCheckCsv(csvDataList, ExcelUtil.SMARTY_HEADERS);
					log.info(headerCheck);

					if (headerCheck.isBlank()) {
						long totalRecords = csvDataList.size() - 1L;

//						CompletableFuture.runAsync(() -> getCsvValues(csvDataList, userId));

						log.info("Count of Records :: " + totalRecords);

						result.setData("Uploaded " + totalRecords + " records.");
					} else {
						result.setErrorMessage(headerCheck);
					}
				}
			} else {
				result.setErrorMessage("The uploaded file is not Present or Not CSV or an Excel file");
			}

			log.info(">>>>> TotalTime Token to Complete in MilliSec : {} and Sec : {}", stopWatch.getTotalTimeMillis(),stopWatch.getTotalTimeSeconds());

		} catch (Exception e) {
			log.error("Error in uploadFile :: ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			if (stopWatch.isRunning()) {
		        stopWatch.stop();
		    }
		}
		return result;
	}


}
