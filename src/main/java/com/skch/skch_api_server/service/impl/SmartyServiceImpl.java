package com.skch.skch_api_server.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.skch.skch_api_server.dto.LookupDto;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.service.SmartyService;
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

}
