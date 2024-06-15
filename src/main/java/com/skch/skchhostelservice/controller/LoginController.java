package com.skch.skchhostelservice.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skch.skchhostelservice.common.Constant;
import com.skch.skchhostelservice.dto.FileUploadDTO;
import com.skch.skchhostelservice.dto.JwtDTO;
import com.skch.skchhostelservice.dto.LoginRequest;
import com.skch.skchhostelservice.dto.ReqSearch;
import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.exception.CustomException;
import com.skch.skchhostelservice.service.LoginService;
import com.skch.skchhostelservice.util.DateUtility;
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
	
	@GetMapping("/soundex-test")
	public ResponseEntity<?> soundexTest() {
		Map<String,String> output = new HashMap<>();
		output.put("Sathish", Utility.soundex("Sathish"));
		output.put("Satish", Utility.soundex("Satish"));
		output.put("Kumar", Utility.soundex("Kumar"));
		output.put("kumaaar", Utility.soundex("kumaaar"));
		
		return ResponseEntity.ok(output);
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
    
    @PostMapping("/get-jwt-refresh-token")
	public ResponseEntity<?> getRefreshToken(@RequestBody JwtDTO dto) {
		
    	JwtDTO result = jwtUtil.getRefreshToken(dto.getRefresh_token());
		
		return ResponseEntity.ok(result);
	}
}
