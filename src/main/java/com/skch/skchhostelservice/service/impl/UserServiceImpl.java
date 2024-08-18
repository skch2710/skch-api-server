package com.skch.skchhostelservice.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.skch.skchhostelservice.common.Constant;
import com.skch.skchhostelservice.dao.UsersDAO;
import com.skch.skchhostelservice.dto.Navigation;
import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.dto.SubNavigarion;
import com.skch.skchhostelservice.dto.UserDTO;
import com.skch.skchhostelservice.dto.UserPrivilegeDTO;
import com.skch.skchhostelservice.dto.UsersFileDTO;
import com.skch.skchhostelservice.exception.CustomException;
import com.skch.skchhostelservice.mapper.ObjectMapper;
import com.skch.skchhostelservice.model.Resource;
import com.skch.skchhostelservice.model.Roles;
import com.skch.skchhostelservice.model.UserPrivilege;
import com.skch.skchhostelservice.model.UserRole;
import com.skch.skchhostelservice.model.Users;
import com.skch.skchhostelservice.service.UserService;
import com.skch.skchhostelservice.util.ExcelUtil;
import com.skch.skchhostelservice.util.JwtUtil;
import com.skch.skchhostelservice.util.Utility;
import com.skch.skchhostelservice.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	private ObjectMapper MAPPER = ObjectMapper.INSTANCE;

	@Autowired
	private UsersDAO usersDAO;
	
	@Autowired
	private BatchInsert batchInsert;

	/**
	 * User Save Or Update Method
	 */
	@Override
	public Result saveOrUpdateUser(UserDTO dto) {
		Result result = new Result();
		try {
			Users user = usersDAO.findByEmailIdIgnoreCase(dto.getEmailId());
			if (dto.getUserId() == null || dto.getUserId() == 0) {
				if (user != null) {
					result.setStatusCode(HttpStatus.BAD_REQUEST.value());
					result.setErrorMessage("Email Id Already Exist...");
				} else {
					Users saveUser = MAPPER.fromUserDTO(dto);

					saveUser.setIsActive(true);
//					saveUser.setCreatedDate(new Date());
//					saveUser.setModifiedDate(new Date());
					Utility.updateFields(saveUser,"C");
					String userUuid = UUID.randomUUID().toString() + "#" + System.currentTimeMillis();
					saveUser.setUserUuid(userUuid);
					// Set The User Role
					setUserRoles(dto, saveUser);

					// Set User Privileges
					setUserPrivileges(dto, saveUser);

					Users serverUser = usersDAO.save(saveUser);
					result.setData(serverUser);
					result.setStatusCode(HttpStatus.OK.value());
					result.setSuccessMessage("User Saved .....");

				}
			} else {
				if (user != null) {
					// Update Properties from dto
					user.setFirstName(dto.getFirstName());
					user.setLastName(dto.getLastName());
					user.setDob(Utility.dateConvert(dto.getDob()));
					user.setPhoneNumber(dto.getPhoneNumber());
//					user.setModifiedById(dto.getModifiedById());
//					user.setModifiedDate(new Date());
					Utility.updateFields(user,"U");
					
					if (user.getUserRole().getRoles().getRoleId() != dto.getRoleId()) {
						Roles role = new Roles();
						role.setRoleId(dto.getRoleId());
						user.getUserRole().setRoles(role);
//						user.getUserRole().setModifiedById(dto.getModifiedById());
//						user.getUserRole().setModifiedDate(new Date());
						Utility.updateFields(user.getUserRole(),"U");
					}

					List<UserPrivilege> userPrivileges = user.getUserPrivilege();
					userPrivileges.sort(
							Comparator.comparingLong(userPrivilege -> userPrivilege.getResource().getResourceId()));
					List<UserPrivilegeDTO> userPrivilegeDTOs = dto.getUserPrivilege();
					Collections.sort(userPrivilegeDTOs, Comparator.comparing(UserPrivilegeDTO::getResourceId));

					for (int i = 0; i < userPrivilegeDTOs.size(); i++) {
						UserPrivilege userPrivilege = userPrivileges.get(i);
						UserPrivilegeDTO userPrivilegeDTO = userPrivilegeDTOs.get(i);

						userPrivilege.setReadOnlyFlag(userPrivilegeDTO.getReadOnlyFlag());
						userPrivilege.setReadWriteFlag(userPrivilegeDTO.getReadWriteFlag());
						userPrivilege.setTerminateFlag(userPrivilegeDTO.getTerminateFlag());
//						userPrivilege.setModifiedById(dto.getModifiedById());
//						userPrivilege.setModifiedDate(new Date());
						Utility.updateFields(userPrivilege,"U");

					}
					user.setUserPrivilege(userPrivileges);

					user = usersDAO.save(user);
					result.setData(user);
					result.setStatusCode(HttpStatus.OK.value());
					result.setSuccessMessage("Updated...");

				}
			}
		} catch (Exception e) {
			log.error("Error in saveOrUpdateUser.... ::" + e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

	public void setUserRoles(UserDTO dto, Users users) {
		try {
			UserRole userRole = new UserRole();
			Roles roles = new Roles();
			roles.setRoleId(dto.getRoleId());
			userRole.setRoles(roles);
			userRole.setActive(true);
//			userRole.setCreatedById(dto.getCreatedById());
//			userRole.setCreatedDate(new Date());
//			userRole.setModifiedById(dto.getModifiedById());
//			userRole.setModifiedDate(new Date());
			Utility.updateFields(userRole,"C");
			userRole.setUsers(users);

			users.setUserRole(userRole);
		} catch (Exception e) {
			log.error("Error in setUserRoles .... ::" + e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void setUserPrivileges(UserDTO dto, Users users) {
		try {
			List<UserPrivilege> userPrivilegeList = new ArrayList<>();
			for (UserPrivilegeDTO userPrivilegeDTO : dto.getUserPrivilege()) {
				UserPrivilege userPrivilege = MAPPER.fromUserPrivilegeDTO(userPrivilegeDTO);
				Resource resource = new Resource();
				resource.setResourceId(userPrivilegeDTO.getResourceId());
				userPrivilege.setResource(resource);
				userPrivilege.setIsActive(true);
//				userPrivilege.setCreatedById(dto.getCreatedById());
//				userPrivilege.setCreatedDate(new Date());
//				userPrivilege.setModifiedById(dto.getModifiedById());
//				userPrivilege.setModifiedDate(new Date());
				Utility.updateFields(userPrivilege,"C");

				userPrivilege.setUsers(users);
				userPrivilegeList.add(userPrivilege);
			}
			users.setUserPrivilege(userPrivilegeList);
		} catch (Exception e) {
			log.error("Error in setUserPrivileges .... ::" + e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Result navigations(Long userId) {
		Result result = new Result();
		SortedMap<Long, Object> navMap = new TreeMap<>();
		try {
			Users user = usersDAO.findByUserId(userId);

			user.getUserPrivilege().stream()
					.filter(obj -> obj.getIsActive() && obj.getReadOnlyFlag() 
							&& obj.getResource().getIsSubnav().equals("N"))
					.forEach(obj -> {
						Navigation navigation = new Navigation();
						BeanUtils.copyProperties(obj.getResource(), navigation);
						navMap.put(obj.getResource().getDisplayOrder(), navigation);
					});
			
			user.getUserPrivilege().stream()
			.filter(obj -> obj.getIsActive() && obj.getReadOnlyFlag() 
					&& obj.getResource().getIsSubnav().equals("Y"))
			.collect(Collectors.groupingBy(obj->obj.getResource().getDisplayOrder()))
			.forEach((displyOrder,listNav)->{
				List<Navigation> navList = new ArrayList<>();
				SubNavigarion nav = new SubNavigarion();
				listNav.forEach(obj -> {
					Navigation navigation = new Navigation();
					BeanUtils.copyProperties(obj.getResource(), navigation);
					navList.add(navigation);	
					});
				if(!listNav.isEmpty()) {
					nav.setResourceName(listNav.get(0).getResource().getParentName());
					nav.setIcon(listNav.get(0).getResource().getParentIcon());
					nav.setDisplayOrder(listNav.get(0).getResource().getDisplayOrder());
					nav.setSubNav(navList);
					navMap.put(listNav.get(0).getResource().getDisplayOrder(), nav);
				}
			});
			result.setData(navMap.values());
		} catch (Exception e) {
			log.error("Error in setUserPrivileges .... ::" + e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return result;
	}

	/**
	 * Download the User Template
	 */
	@Override
	public ByteArrayOutputStream getUserTemplate() {
		ByteArrayOutputStream bao = null;
		Workbook workbook = null;
		try {
			FileInputStream inputStream = new FileInputStream(Constant.USER_TEMPLATE);
			workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			
			// Create the dropdown list values
			String[] dropdownValuesRoles = { "Admin", "Super User" };
			String[] dropdownValuesActive = { "No", "Yes" };
			
			String[] emailIds = usersDAO.findAllEmailId();
			log.info("List :: "+ Arrays.asList(emailIds));
			
			// Define the range of cells where the dropdown should be added
			ExcelUtil.dropDownValues(sheet, dropdownValuesRoles, 1, 1048575, 5, 5);
			ExcelUtil.dropDownValues(sheet, dropdownValuesActive, 1, 1048575, 6, 6);

			bao = new ByteArrayOutputStream();
			workbook.write(bao); // Write the workbook to temp byte array
		} catch (Exception e) {
			log.error("Error in get User Template :: ", e);
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

	/**
	 * Upload User File 
	 * @param file
	 * @return result
	 */
	@Override
	public Result uploadUserFile(MultipartFile file) {
		Result result = new Result();
		XSSFWorkbook workbook = null;
		CSVReader csvReader = null;
		try {
			long intialTime = System.currentTimeMillis();
			Long userId = JwtUtil.getUserId();

			if (ExcelUtil.excelType(file)) {
				workbook = new XSSFWorkbook(file.getInputStream());
				XSSFSheet sheet = workbook.getSheetAt(0);
				String headerCheck = ExcelUtil.headerCheck(sheet, ExcelUtil.USER_HEADERS);
				log.info(headerCheck);
				if (headerCheck.isBlank()) {
					long totalRecords = sheet.getLastRowNum();
						// Run Method Async
						CompletableFuture.runAsync(() -> {
								getRowValues(sheet, userId);
						});
						
						result.setData("Uploaded " + totalRecords + " records.");
					}{
					result.setErrorMessage(headerCheck);
				}
			}else if(ExcelUtil.csvType(file)) {
				csvReader = new CSVReader(new InputStreamReader(file.getInputStream(),
						StandardCharsets.UTF_8));
				
				List<String[]> csvDataList = csvReader.readAll();
				
				String headerCheck = ExcelUtil.headerCheckCsv(csvDataList,ExcelUtil.USER_HEADERS);
				log.info(headerCheck);
				if (headerCheck.isBlank()) {
					long totalRecords = csvDataList.size() - 1;
					
					CompletableFuture.runAsync(() -> {
						getCsvValues(csvDataList,userId);
		            });
					
					log.info("Count of Records :: "+totalRecords);
					
					result.setData("Uploaded " + totalRecords + " records.");
				} else {
					result.setErrorMessage(headerCheck);
				}
			}else {
				result.setErrorMessage("The uploaded file is not Present or Not CSV or an Excel file");
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
				if(csvReader != null) {
					csvReader.close();
				}
			} catch (Exception e) {
				log.error("Error in Closing workbook or csvReader :: ",e);
			}
		}
		return result;
	}
	
	/**
	 * Method to Excel Sheet Data in Threads Batch Update
	 * @param sheet
	 * @param userId
	 */
	public void getRowValues(XSSFSheet sheet,Long userId) {
		ArrayList<UsersFileDTO> dataList = new ArrayList<>();
		List<UsersFileDTO> errorList = new ArrayList<>();
		List<CompletableFuture<Void>> features = new ArrayList<>();
		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		int batchSize = 1000;
		List<Integer> succCount = new ArrayList<>();
		
		try {
			sheet.forEach(row -> {
				if (row.getRowNum() == 0) {
					return;
				}
				List<String> cellValues = IntStream.range(0, ExcelUtil.USER_HEADERS.size()).mapToObj(i -> {
					Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					return ExcelUtil.getCellValue(cell);
				}).collect(Collectors.toList());
				
				UsersFileDTO dto = new UsersFileDTO(cellValues);
				Map<String, String> errors = ValidationUtils.validate(dto);

				if (errors.isEmpty()) {
					if (!dataList.isEmpty() && dataList.size() % batchSize == 0) {
						
						features.add(CompletableFuture.runAsync(() -> 
							batchInsert.saveInBatchUsers(dataList), executor));
						
						succCount.add(dataList.size());
						dataList.clear();
					}
					dataList.add(dto);
				} else {
					String error = errors.values().stream().collect(Collectors.joining(","));
					dto.setErrorMessage(error);
					errorList.add(dto);
					log.info("Error :: " + error);
				}
			});
			
			if (!dataList.isEmpty()) {
				features.add(CompletableFuture.runAsync(() -> 
					batchInsert.saveInBatchUsers(dataList), executor));
				succCount.add(dataList.size());
			}

			// Wait for all threads to finish and collect their results
//        CompletableFuture.allOf(features.toArray(new CompletableFuture[0])).join();
			
			int sum = succCount.stream().mapToInt(Integer::intValue).sum();
			log.info("List DTO Size :: " + sum);
		} catch (Exception e) {
			log.error("Error in get Row Values :: ",e);
		} finally {
			executor.shutdown();
		}
	}
	
	/**
	 * Method to Save Records Csv to DB
	 */
	public void getCsvValues(List<String[]> csvDataList ,Long userId) {
		ArrayList<UsersFileDTO> dataList = new ArrayList<>();
		List<UsersFileDTO> errorList = new ArrayList<>();
		List<CompletableFuture<Void>> features = new ArrayList<>();
		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		int batchSize = 1000;
		Map<String, Integer> countMap = new HashMap<>(Map.of("S", 0, "F", 0));
		
		try {
			//Remove Headers
			csvDataList.removeFirst(); //From Java 21
//			csvDataList.remove(0);
			
			csvDataList.forEach(data ->{
				
				UsersFileDTO dto = new UsersFileDTO(Arrays.asList(data));
				Map<String, String> errors = ValidationUtils.validate(dto);

				if (errors.isEmpty()) {
					if (!dataList.isEmpty() && dataList.size() % batchSize == 0) {
						features.add(CompletableFuture.runAsync(() -> 
							batchInsert.saveInBatchUsers(dataList), executor));
						Integer count = countMap.get("S") + dataList.size();
						countMap.put("S", count);
						dataList.clear();
					}
					dataList.add(dto);
				} else {
					String error = errors.values().stream().collect(Collectors.joining(","));
					dto.setErrorMessage(error);
					errorList.add(dto);
//					Integer count = countMap.get("F") + errorList.size();
//					countMap.put("F", count);
					log.info("Error :: " + error);
				}
				
			});
			
			if (!dataList.isEmpty()) {
				features.add(CompletableFuture.runAsync(() -> 
					batchInsert.saveInBatchUsers(dataList), executor));
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

}
