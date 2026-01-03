package com.skch.skch_api_server.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.pjfanning.xlsx.StreamingReader;
import com.opencsv.CSVReader;
import com.skch.skch_api_server.common.Constant;
import com.skch.skch_api_server.dao.UploadFileDAO;
import com.skch.skch_api_server.dao.UsersDAO;
import com.skch.skch_api_server.dto.FileUploadDTO;
import com.skch.skch_api_server.dto.Navigation;
import com.skch.skch_api_server.dto.ProfileDto;
import com.skch.skch_api_server.dto.ProfileRequest;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.dto.SubNavigarion;
import com.skch.skch_api_server.dto.UserDTO;
import com.skch.skch_api_server.dto.UserPrivilegeDTO;
import com.skch.skch_api_server.dto.UsersFileDTO;
import com.skch.skch_api_server.exception.CustomException;
import com.skch.skch_api_server.mapper.ObjectMapper;
import com.skch.skch_api_server.model.Resource;
import com.skch.skch_api_server.model.Roles;
import com.skch.skch_api_server.model.UploadFile;
import com.skch.skch_api_server.model.UserPrivilege;
import com.skch.skch_api_server.model.UserRole;
import com.skch.skch_api_server.model.Users;
import com.skch.skch_api_server.service.UserService;
import com.skch.skch_api_server.util.DateUtility;
import com.skch.skch_api_server.util.ExcelUtil;
import com.skch.skch_api_server.util.JwtUtil;
import com.skch.skch_api_server.util.Utility;
import com.skch.skch_api_server.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	private ObjectMapper MAPPER = ObjectMapper.INSTANCE;

	@Autowired
	private UsersDAO usersDAO;
	
	@Autowired
	private BatchInsert batchInsert;
	
	@Autowired
	private UploadFileDAO uploadFileDAO;
	
	@Value("${app.batch-size}")
	private int batchSize;

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
//					Utility.updateFields(saveUser,"C");
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
//			Utility.updateFields(userRole,"C");
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
//				Utility.updateFields(userPrivilege,"C");

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
	    try (FileInputStream inputStream = new FileInputStream(Constant.USER_TEMPLATE);
	         Workbook workbook = new XSSFWorkbook(inputStream);
	         ByteArrayOutputStream bao = new ByteArrayOutputStream()) {

	        Sheet sheet = workbook.getSheetAt(0);

	        // Create the dropdown list values
	        String[] dropdownValuesRoles = { "Admin", "Super User" };
	        String[] dropdownValuesActive = { "No", "Yes" };

	        // Define the range of cells where the dropdown should be added
	        ExcelUtil.dropDownValues(sheet, dropdownValuesRoles, 1, 1048575, 5, 5);
	        ExcelUtil.dropDownValues(sheet, dropdownValuesActive, 1, 1048575, 6, 6);

	        workbook.write(bao); // Write the workbook to the ByteArrayOutputStream
	        return bao;

	    } catch (Exception e) {
	        log.error("Error in getUserTemplate :: ", e);
	        throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


	/**
	 * Upload User File 
	 * @param file
	 * @return result
	 */
	@Override
	public Result uploadUserFile(MultipartFile file, FileUploadDTO dto) {
		Result result = new Result();
		Long userId = JwtUtil.getUserId();

		try {
			long initialTime = System.currentTimeMillis();

			if (ExcelUtil.excelType(file)) {
				try (Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096)
						.open(file.getInputStream())) {

					Sheet sheet = ExcelUtil.getFirstSheet(workbook);
					if (dto.isValidation()) {
						log.info("Total Rows :: " + sheet.getLastRowNum());
						String headerCheck = ExcelUtil.headerCheck(sheet, ExcelUtil.USER_HEADERS);
						if (headerCheck.isBlank()) {
							long totalRecords = sheet.getLastRowNum();

							result.setData("Uploaded " + totalRecords + " records.");
							result.setStatusCode(HttpStatus.OK.value());
							result.setSuccessMessage("Successfully Uploaded");
						} else {
							result.setErrorMessage(headerCheck);
							result.setStatusCode(HttpStatus.BAD_REQUEST.value());
						}
					} else {
//	                    ExcelUtil.getCsvFileNew(sheet, ExcelUtil.USER_HEADERS.size());
						long totalRecords = sheet.getLastRowNum();
						UploadFile uploadFile = saveUploadFile(file, userId, totalRecords);

						getRowValues(sheet, uploadFile);
						log.info("Before calling SP...");
						uploadFileDAO.callUsersProc(uploadFile.getUploadFileId()); // Calling SP

						result.setData("Uploaded " + totalRecords + " records.");
						result.setStatusCode(HttpStatus.OK.value());
						result.setSuccessMessage("Successfully Uploaded");
					}
				}
			} else if (ExcelUtil.csvType(file)) {
				try (CSVReader csvReader = new CSVReader(
						new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

					List<String[]> csvDataList = csvReader.readAll();

					String headerCheck = ExcelUtil.headerCheckCsv(csvDataList, ExcelUtil.USER_HEADERS);
					log.info(headerCheck);
					if (headerCheck.isBlank()) {
						long totalRecords = csvDataList.size() - 1L;

						UploadFile uploadFile = saveUploadFile(file, userId, totalRecords);

						CompletableFuture.runAsync(() -> {
							getCsvValues(csvDataList, uploadFile);
							log.info("Before calling SP...");
							uploadFileDAO.callUsersProc(uploadFile.getUploadFileId()); // Calling SP
						});

						log.info("Count of Records :: " + totalRecords);

						result.setData("Uploaded " + totalRecords + " records.");
						result.setStatusCode(HttpStatus.OK.value());
						result.setSuccessMessage("Successfully Uploaded");
					} else {
						result.setErrorMessage(headerCheck);
						result.setStatusCode(HttpStatus.BAD_REQUEST.value());
					}
				}
			} else {
				result.setErrorMessage("The uploaded file is not Present or Not CSV or an Excel file");
				result.setStatusCode(HttpStatus.BAD_REQUEST.value());
			}

			long finalTime = System.currentTimeMillis();
			log.info("???>>>???::: TotalTime : " + (finalTime - initialTime));

		} catch (Exception e) {
			log.error("Error in uploadUserFile :: ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return result;
	}

	
	/**
	 * To Save to Upload File
	 * @param userId
	 * @return uploadFile
	 */
	public UploadFile saveUploadFile(MultipartFile file,Long userId,Long totalCount) {
		UploadFile uploadFile = null;
		try {
			uploadFile = new UploadFile();
			uploadFile.setUploadedById(userId);
			uploadFile.setUploadType("Users");
			String fileType = file.getOriginalFilename().split("\\.")[1];
			String fileName = "Users_" + 
					DateUtility.dateToString(LocalDateTime.now(ZoneId.of("Asia/Kolkata")), "yyyyMMddHHmmss") 
						+"."+ fileType;
			uploadFile.setFileName(fileName);
			uploadFile.setTotalCount(totalCount);
			
			uploadFile = uploadFileDAO.save(uploadFile);
		} catch (Exception e) {
			log.error("Error in saveUploadFile :: ",e);
		}
		return uploadFile;
	}
	
	/**
	 * Method to Excel Sheet Data in Threads Batch Update
	 * @param sheet
	 * @param userId
	 */
	public void getRowValues(Sheet sheet, UploadFile uploadFile) {
		List<UsersFileDTO> dataList = new ArrayList<>();
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		log.info("Starting getRowValues.....");
		try {
			sheet.forEach(row -> {
				if (row.getRowNum() == 0) {
					return; // Skip header row
				}
				// Extract cell values
				List<String> cellValues = IntStream.range(0, ExcelUtil.HOSTEL_HEADERS.size()).mapToObj(i -> {
					Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					return ExcelUtil.getCellValue(cell);
				}).toList();

				// Map row to DTO
				UsersFileDTO dto = new UsersFileDTO(cellValues);
				dto.setUploadFileId(uploadFile.getUploadFileId());
				dto.setStatus("success");

				// Validate the DTO
				Map<String, String> errors = ValidationUtils.validate(dto);
				if (!errors.isEmpty()) {
					dto.setErrorMessage(String.join(",", errors.values()));
					dto.setStatus("fail");
				}

				// Add to batch
				dataList.add(dto);

				// Process batch when it reaches the batch size
				if (!dataList.isEmpty() && dataList.size() % batchSize == 0) {
					processBatch(dataList, futures, executor);
				}
			});

			// Process any remaining records in the batch
			if (!dataList.isEmpty()) {
				processBatch(dataList, futures, executor);
			}

			// Wait for all threads to complete
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

			log.info("Ending getRowValues.....");
		} catch (Exception e) {
			log.error("Error in getRowValues :: ", e);
		} finally {
			executor.shutdown();
		}
	}

	/**
	 * Processes a batch of data by submitting it asynchronously for saving.
	 */
	private void processBatch(List<UsersFileDTO> dataList, List<CompletableFuture<Void>> futures,
			ExecutorService executor) {
		List<UsersFileDTO> batch = new ArrayList<>(dataList);
		futures.add(CompletableFuture.runAsync(() -> batchInsert.saveInBatchUsers(batch), executor));
		dataList.clear();
	}
	
	/**
	 * Method to Save Records Csv to DB
	 */
	public void getCsvValues(List<String[]> csvDataList, UploadFile uploadFile) {
		ArrayList<UsersFileDTO> dataList = new ArrayList<>();
		List<CompletableFuture<Void>> features = new ArrayList<>();
		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		try {
			//Remove Headers
			csvDataList.removeFirst(); //From Java 21 //csvDataList.remove(0);
			
			csvDataList.forEach(data ->{
				
				UsersFileDTO dto = new UsersFileDTO(Arrays.asList(data));
				Map<String, String> errors = ValidationUtils.validate(dto);
				
				dto.setUploadFileId(uploadFile.getUploadFileId());
				dto.setStatus("success");
				if (!errors.isEmpty()) {
					String error = errors.values().stream().collect(Collectors.joining(", "));
					dto.setErrorMessage(error);
					dto.setStatus("fail");
				}
				dataList.add(dto);
				
				if (!dataList.isEmpty() && dataList.size() % batchSize == 0) {
					processBatch(dataList,features,executor);
				}
			});
			
			if (!dataList.isEmpty()) {
				processBatch(dataList,features,executor);
			}

			// Wait for all threads to finish and collect their results
	        CompletableFuture.allOf(features.toArray(new CompletableFuture[0])).join();

		} catch (Exception e) {
			log.error("Error in getCsvValues :: ", e);
		} finally {
			executor.shutdown();
		}
	}

	/**
	 * Get Profile Details
	 * 
	 * @param request
	 * @return result
	 */
	@Override
	public Result profile() {
		Result result = new Result();
		try {
//			Thread.sleep(Duration.ofSeconds(10));
			String emailId = JwtUtil.getUserName();
			log.info("Profile Request :: {}", emailId);
			Users user = usersDAO.findByEmailIdIgnoreCase(emailId);
			if (user != null) {
				UserDTO userDto = MAPPER.fromUserModel(user);
				ProfileDto dto = new ProfileDto();
				dto.setUser(userDto);
				dto.setNavigations(getNavigations(user));
				result.setData(dto);
				result.setStatusCode(HttpStatus.OK.value());
				result.setSuccessMessage("Profile Data Fetched Successfully.");
			} else {
				result.setErrorMessage("User Not Found.");
				result.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			log.error("Error in profile :: ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

	public Object getNavigations(Users user) {
		SortedMap<Long, Object> navMap = new TreeMap<>();
		try {
			user.getUserPrivilege().stream().filter(
					obj -> obj.getIsActive() && obj.getReadOnlyFlag() && obj.getResource().getIsSubnav().equals("N"))
					.forEach(obj -> {
						Navigation navigation = new Navigation();
						BeanUtils.copyProperties(obj.getResource(), navigation);
						navMap.put(obj.getResource().getDisplayOrder(), navigation);
					});

			user.getUserPrivilege().stream().filter(
					obj -> obj.getIsActive() && obj.getReadOnlyFlag() && obj.getResource().getIsSubnav().equals("Y"))
					.collect(Collectors.groupingBy(obj -> obj.getResource().getDisplayOrder()))
					.forEach((displyOrder, listNav) -> {
						List<Navigation> navList = new ArrayList<>();
						SubNavigarion nav = new SubNavigarion();
						listNav.forEach(obj -> {
							Navigation navigation = new Navigation();
							BeanUtils.copyProperties(obj.getResource(), navigation);
							navList.add(navigation);
						});
						if (!listNav.isEmpty()) {
							nav.setResourceName(listNav.get(0).getResource().getParentName());
							nav.setIcon(listNav.get(0).getResource().getParentIcon());
							nav.setDisplayOrder(listNav.get(0).getResource().getDisplayOrder());
							nav.setSubNav(navList);
							navMap.put(listNav.get(0).getResource().getDisplayOrder(), nav);
						}
					});
		} catch (Exception e) {
			log.error("Error in getNavigations :: ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return navMap.values();
	}

}
