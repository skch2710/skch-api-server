package com.skch.skchhostelservice.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.skch.skchhostelservice.common.Constant;
import com.skch.skchhostelservice.dao.UsersDAO;
import com.skch.skchhostelservice.dto.FileUploadDTO;
import com.skch.skchhostelservice.dto.JwtDTO;
import com.skch.skchhostelservice.dto.LoginRequest;
import com.skch.skchhostelservice.dto.ReqSearch;
import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.exception.CustomException;
import com.skch.skchhostelservice.service.LoginService;
import com.skch.skchhostelservice.util.DateUtility;
import com.skch.skchhostelservice.util.ExcelUtil;
import com.skch.skchhostelservice.util.JwtUtil;
import com.skch.skchhostelservice.util.Utility;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/authenticate")
@Slf4j
public class LoginController {
	
	@Autowired
	private LoginService loginService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		Result response = loginService.login(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOTP(@RequestBody LoginRequest request) {
		Result response = loginService.verifyOTP(request);
		return ResponseEntity.ok(response);
	}


	@PostMapping("/generate-pdf")
	public ResponseEntity<?> generatePdf(@RequestBody ReqSearch search) throws Exception {
		try {
			ByteArrayOutputStream outputStream = loginService.getPdf();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("attachment", "Sample.pdf");

			InputStreamResource inputStreamResource = new InputStreamResource(
					new ByteArrayInputStream(outputStream.toByteArray()));

			outputStream.flush();// Flush the output stream

			return ResponseEntity.ok().headers(headers).body(inputStreamResource);
		} catch (Exception e) {
			log.error("Error in Get Pdf Controller....",e);
			throw new CustomException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Autowired
	private UsersDAO usersDAO;
	
	@GetMapping("/soundex-test")
	public ResponseEntity<?> soundexTest() {
		Map<String,String> output = new HashMap<>();
		output.put("Sathish", Utility.soundex("Sathish"));
		output.put("Satish", Utility.soundex("Satish"));
		output.put("Kumar", Utility.soundex("Kumar"));
		output.put("kumaaar", Utility.soundex("kumaaar"));
		
//		Map<Long,String> mapData = usersDAO.getUserPrivilegesMap();
//		System.out.println(mapData.get(4L));
		
		/*
		int page = 0;
        int size = 2; // Adjust the page size based on your memory constraints
        Map<Long, String> resultMap = new HashMap<>();
        
        Page<String> jsonPage;
        do {
            jsonPage = usersDAO.getUserPrivilegesJson(PageRequest.of(page, size));
            for (String jsonData : jsonPage) {
                resultMap.putAll(Utility.parseJsonToMap(jsonData, Long.class, String.class));
            }
            page++;
            System.out.println(resultMap);
        } while (jsonPage.hasNext()); */
		
		Object object = usersDAO.findByTest("skch@outlook.com");
		Gson gson = new Gson();
		String json = gson.toJson(object);
		log.info(">>>>"+json);
		JsonArray array = JsonParser.parseString(json).getAsJsonArray();
		
        Long id = array.get(0).getAsLong();
        String email = array.get(1).getAsString();
        
        System.out.println("ID: " + id);
        System.out.println("Email: " + email);
		
		log.info(">>>>"+array);
		
		return ResponseEntity.ok(object);
	}
	
    @PostMapping(path = "/upload-file", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadFile(@RequestPart(required = true, name="file") MultipartFile file,
    		@RequestPart(required = false, name="dto") FileUploadDTO dto) {
        
    	if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file was provided or the file is empty.");
        }
    	
    	System.out.println("DTO : "+dto);
    	
        try {
            // Ensure the upload directory exists
            File uploadDir = new File(Constant.UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            // Get the file's original name
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.badRequest().body("File name is invalid.");
            }
            // Create the file path
            Path filePath = Paths.get(Constant.UPLOAD_DIR, originalFilename);
            // Save the file locally
            Files.write(filePath, file.getBytes());

            return ResponseEntity.ok("File uploaded successfully: " + originalFilename);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            		.body("Error occurred while uploading the file: " + e);
        }
    }
    
    @PostMapping(path ="/csv-excel", consumes = "multipart/form-data")
	public ResponseEntity<?> csvToExcel(@RequestPart(required = true, name="file") MultipartFile file){
		try {
			log.info(file.getContentType());
			if(file != null && file.getContentType().equals(ExcelUtil.CSV_TYPE)) {
				ByteArrayOutputStream outputStream = ExcelUtil.convertCsvToExcel(file);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.setContentDispositionFormData("attachment", "Sample.xlsx");

				InputStreamResource inputStreamResource = new InputStreamResource(
						new ByteArrayInputStream(outputStream.toByteArray()));

				outputStream.flush();// Flush the output stream

				return ResponseEntity.ok().headers(headers).body(inputStreamResource);
			}else {
				return ResponseEntity.badRequest().body("Not a CSV File");
			}
		} catch (Exception e) {
			log.error("Error in csvToExcel....",e);
			throw new CustomException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    
    @PostMapping(path ="/csv-file-reader", consumes = "multipart/form-data")
	public ResponseEntity<?> getCsvReader(@RequestPart(required = true, name="file") MultipartFile file){
		try {
			log.info(file.getContentType());
			if(file != null && file.getContentType().equals(ExcelUtil.CSV_TYPE)) {
				
				ExcelUtil.readFirstLine(file);
				
				return ResponseEntity.ok("File Details... ");
			}else {
				return ResponseEntity.ok("File Not a CSV: ");
			}
		} catch (Exception e) {
			log.error("Error in csvToExcel....",e);
			throw new CustomException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
    
    @PostMapping(path ="/excel-csv", consumes = "multipart/form-data")
	public ResponseEntity<?> excelToCsv(@RequestPart(required = true, name="file") MultipartFile file){
//    	SXSSFWorkbook workbook = null;
    	File tempFile = null;
		try {
			log.info("Starting file.......");
			// Save the file to a temporary location
//            tempFile = File.createTempFile("upload", ".tmp");
//            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//                fos.write(file.getBytes());
//            }
//			log.info(file.getContentType());
//			
//			InputStream inputStream = new FileInputStream(tempFile);
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
			log.info("Size of Records :: ");
			ByteArrayOutputStream outputStream = ExcelUtil.getCsvFile(sheet);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", "Sample.csv");

			InputStreamResource inputStreamResource = new InputStreamResource(
					new ByteArrayInputStream(outputStream.toByteArray()));

			outputStream.flush();// Flush the output stream

			return ResponseEntity.ok().headers(headers).body(inputStreamResource);
		} catch (Exception e) {
			log.error("Error in csvToExcel....",e);
			throw new CustomException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    
    @DeleteMapping("/delete-file")
    public ResponseEntity<?> deleteFile(@RequestBody FileUploadDTO dto) {
        if (dto.getFileName() == null || dto.getFileName().isEmpty()) {
            return ResponseEntity.badRequest().body("File name must be provided.");
        }
        try {
            // Create the file path
            File file = new File(Constant.UPLOAD_DIR, dto.getFileName());

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
            }

            if (file.delete()) {
                return ResponseEntity.ok("File deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                		.body("Failed to delete the file.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            		.body("Error occurred while deleting the file: " + e);
        }
    }
    
    @GetMapping("/list-files")
    public ResponseEntity<?> listFiles() {
        File directory = new File(Constant.UPLOAD_DIR);

        if (!directory.exists() || !directory.isDirectory()) {
            return ResponseEntity.status(404).body("Directory not found.");
        }
        File[] files = directory.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                	String fileName = file.getName();
                	String dateNum = fileName.split("_")[1];
                	
                	LocalDate date = DateUtility.stringToDate(dateNum, "MMddyyyy");
                	System.out.println(date);
                	
                	if(date.isBefore(LocalDate.now())) {
                		fileNames.add(fileName);
                	}
                }
            }
        }else {
        	return ResponseEntity.ok("No files found in the directory.");
        }

        return ResponseEntity.ok(fileNames);
    }
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final JwtDecoder jwtDecoder;

    @Autowired
    public LoginController(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }
    
    @PostMapping("/get-jwt-refresh-token")
	public ResponseEntity<?> getRefreshToken(@RequestBody JwtDTO dto) {
    	JwtDTO result = jwtUtil.getRefreshToken(dto.getRefresh_token());
		return ResponseEntity.ok(result);
	}
    
    @GetMapping("/get-jwt-access-token")
   	public ResponseEntity<?> getAccessToken() {
    	LoginRequest dto = new LoginRequest("skch@outlook.com","S@th!$h","");
       	JwtDTO result = jwtUtil.getToken(dto);
   		return ResponseEntity.ok(result);
   	}
    
    
    @GetMapping("/jwt-token-time")
   	public ResponseEntity<?> getTokenTime() {
    	
    	Jwt jwt = jwtDecoder.decode("eyJraWQiOiI0NTk4MjNhZC04OTdiLTRmNjYtYjJlMS0xMmJhMjU0MDI1MjkiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJza2NoQG91dGxvb2suY29tIiwiYXVkIjoic2F0aGlzaF9jaCIsIm5iZiI6MTcyMTI4MTY4MywidXNlcl9uYW1lIjoic2tjaEBvdXRsb29rLmNvbSIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA2MCIsImV4cCI6MTcyMTMyNDg4MywiaWF0IjoxNzIxMjgxNjgzLCJqdGkiOiIzNzg3YjRkYy01NWY2LTRmMmUtOTZjYy0wYzE0ODI2YzI5YjQiLCJhdXRob3JpdGllcyI6WyJVc2VyLVIiLCJNb250aGx5LVIiLCJZZWFybHktUiIsIlN1cGVyIFVzZXIiLCJGdWxsIFJlcG9ydHMtUiIsIkhvc3RlbGxlcnMtUiIsIlVTRVJfSUQgOiA1RXhFZ0MxU0RocHROSEtuUVpwSDZnPT0iLCJIb21lLVIiLCJVU0VSIFVVSUQgOiBudWxsIl19.bqo4K5qUNVVzMrj-ed1IWaAQY0OCVw2_LEYcNjCW5xg04-MvJzaAwIHW2-yq_B2vFg8c-llUjOMej8hecLGQrjjjgNUgH0a6fklux1y1VWCQ143Oan519EdCOTuIilzv_LHteUGWuW523ZqX1yVxGfK_vivbRDA1qF4Lvw1LbgZZgg9LGXZ4aJm0l99YsAbq91opHPZ8lOhtBz1Vc-ekLH482ABdGvjYTQtem1JSELRBE7vRxVa9s3TMYn8ck2z19R2vN9hdXKMWr9hMDEShjZyb76j-1QqBVL8LFnVnWJHq6ZkAtkgtqZzqM-car5miBvNP-wR60KVBfAWvkib4sQ");
    	
    	log.info("Time Expire :: "+jwt.getExpiresAt().isBefore(LocalDateTime.now().plusSeconds(30).atZone(ZoneId.systemDefault()).toInstant()));
    	
    	log.info("Time Expire :: "+DateUtility.dateToString(LocalDateTime.ofInstant(jwt.getExpiresAt(),ZoneId.systemDefault()), "yyyy-MM-dd HH:mm:ss a"));
    	
   		return ResponseEntity.ok(jwt);
   	}
}
