package com.skch.skch_api_server.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.skch.skch_api_server.dao.UserValidationDAO;
import com.skch.skch_api_server.dao.UsersDAO;
import com.skch.skch_api_server.dto.JwtDTO;
import com.skch.skch_api_server.dto.LoginRequest;
import com.skch.skch_api_server.dto.LoginResponse;
import com.skch.skch_api_server.dto.Navigation;
import com.skch.skch_api_server.dto.Result;
import com.skch.skch_api_server.dto.SubNavigarion;
import com.skch.skch_api_server.dto.UserDTO;
import com.skch.skch_api_server.dto.UserPrivilegeDTO;
import com.skch.skch_api_server.dto.ValidateLinkDTO;
import com.skch.skch_api_server.exception.CustomException;
import com.skch.skch_api_server.mapper.ObjectMapper;
import com.skch.skch_api_server.model.UserValidation;
import com.skch.skch_api_server.model.Users;
import com.skch.skch_api_server.service.LoginService;
import com.skch.skch_api_server.util.AESUtils;
import com.skch.skch_api_server.util.CacheService;
import com.skch.skch_api_server.util.DateUtility;
import com.skch.skch_api_server.util.JwtUtil;
import com.skch.skch_api_server.util.PdfHelper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
	
	private ObjectMapper MAPPER = ObjectMapper.INSTANCE;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UsersDAO userDAO;
	
	@Autowired
	private UserValidationDAO userValidationDAO;

	/** The otp service. */
	@Autowired
	public CacheService cacheService;

	@Autowired
	private JwtUtil jwtUtil;

	@Value("${app.isOtpEnable}")
	private Boolean isOtpEnable;

	@Override
	public Result login(LoginRequest request) {
		Result result = null;
		LoginResponse loginResponse = null;
		try {
			log.info("Decrypted Password :: {} ",AESUtils.decrypt(request.getPassword()));
			request.setPassword(AESUtils.decrypt(request.getPassword()));
			result = new Result();
			loginResponse = new LoginResponse();
			Users user = userDAO.findByEmailIdIgnoreCase(request.getEmailId().toLowerCase());

			if (user == null) {
				result.setErrorMessage("Invalid Email Address or Not Registered.");
				result.setStatusCode(HttpStatus.BAD_REQUEST.value());
			} else {
				if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPasswordSalt())) {
					result.setErrorMessage("Invalid PassWord.");
					result.setStatusCode(HttpStatus.BAD_REQUEST.value());
				} else {
					loginResponse.setIsOtpEnable(isOtpEnable);
					if (isOtpEnable) {
						String otp = cacheService.generateOTP(request.getEmailId().toLowerCase().trim());
						loginResponse.setOtp(otp);
						
						result.setData(loginResponse);
						result.setSuccessMessage("OTP has been send to Mail");
						result.setStatusCode(HttpStatus.OK.value());
					} else {
						JwtDTO jwtDTO = jwtUtil.getToken(request);
						List<UserPrivilegeDTO> upr = MAPPER.fromUserPrivilegeModel(user.getUserPrivilege());
						UserDTO userDto = MAPPER.fromUserModel(user);
						userDto.setUserPrivilege(upr);
						loginResponse.setUser(userDto);
						loginResponse.setNavigations(getNavigations(user));
						loginResponse.setJwtDTO(jwtDTO);
						
						result.setData(loginResponse);
						result.setSuccessMessage("Login Succesfully.....");
						result.setStatusCode(HttpStatus.OK.value());
					}
				}
			}
		} catch (Exception e) {
			log.error("Error in login...::", e);
			throw new CustomException("Error in Login :: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}

	@Override
	public Result verifyOTP(LoginRequest request) {
		Result resut = null;
		LoginResponse loginResponse = null;
		try {
			resut = new Result();
			loginResponse = new LoginResponse();
			String emailId = request.getEmailId().toLowerCase().trim();
			String serverOtp = cacheService.getOtp(emailId);
			if (request.getOtp().equals(serverOtp)) {
				Users user = userDAO.findByEmailIdIgnoreCase(emailId);
				resut.setStatusCode(HttpStatus.OK.value());
				resut.setSuccessMessage("OTP is successfully validated");
				cacheService.clearOTP(emailId);

				JwtDTO jwtDTO = jwtUtil.getToken(request);
				List<UserPrivilegeDTO> upr = MAPPER.fromUserPrivilegeModel(user.getUserPrivilege());
				UserDTO userDto = MAPPER.fromUserModel(user);
				userDto.setUserPrivilege(upr);
				loginResponse.setUser(userDto);
				loginResponse.setJwtDTO(jwtDTO);
				loginResponse.setNavigations(getNavigations(user));
				resut.setData(loginResponse);

			} else if (Integer.valueOf(serverOtp) == 0) {
				resut.setStatusCode(HttpStatus.NOT_FOUND.value());
				resut.setSuccessMessage("OTP is expired");
			} else {
				resut.setStatusCode(HttpStatus.NOT_FOUND.value());
				resut.setSuccessMessage("OTP is invalid");
			}
		} catch (Exception e) {
			log.error("Error in verifyOTP ... ", e);
			throw new CustomException("Error in verifyOTP :: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resut;
	}

	public Object getNavigations(Users user) {
		SortedMap<Long, Object> navMap = new TreeMap<>();
		try {
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
		} catch (Exception e) {
			log.error("Error in getNavigations ... ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return navMap.values();
	}

	@Override
	public ByteArrayOutputStream getPdf(){
		ByteArrayOutputStream baos;
		try {
//			Rectangle pagesize = new Rectangle(1754, 1240);
			// Create a new document
			Document document = new Document(PageSize.A4, 30, 30, 45, 30);
//			Document document = new Document(pagesize, 30, 30, 45, 30);

			baos = new ByteArrayOutputStream();

			PdfWriter.getInstance(document, baos);// Create a new PDF writer

			document.open(); // Open the document

			PdfPTable totalCard = PdfHelper.createNoBorderTable(3,0f,0f,92);
			totalCard.setTotalWidth(new float[] {49f,2f,49f});
			PdfPTable frontCard = frontCard();
			PdfPTable middleSpacee = PdfHelper.createTable(1,0f,0f,100);
			PdfPTable backCard = backCard();
			
			totalCard.addCell(frontCard);
			totalCard.addCell(middleSpacee);
			totalCard.addCell(backCard);
			
			document.add(totalCard);
			
			document.close(); // Close the document
		} catch (Exception e) {
			log.error("error in generate Pdf ", e);
			throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return baos;
	}
	
	public PdfPTable frontCard() throws Exception {
		String bgmPath = "src/main/resources/images/Card Front.png";
		String logoPath = "src/main/resources/images/logo-one.png";
		
		PdfPTable frontCard = PdfHelper.createTable(1,0f,0f,100);
		frontCard.getDefaultCell().setBorder(1);
		frontCard.getDefaultCell().setBorderColor(new BaseColor(245,245,245));
		PdfPCell cell = new PdfPCell();
		
		PdfPTable innerTable = PdfHelper.createTable(1,15f,15f,100);
		PdfHelper.createLogo(innerTable,logoPath,5,0,0,25,Element.ALIGN_LEFT);
		PdfHelper.noBorderCell(innerTable,"Sathish Kumar",10,null,5,Element.ALIGN_LEFT);
		
		cell.addElement(innerTable);
		
		PdfHelper.imageBgm(bgmPath, frontCard,cell, 158);
		
		return frontCard;
	}
	
	public PdfPTable backCard() throws Exception {
		String bgmPath = "src/main/resources/images/Card Back.png";
		String logoPath = "src/main/resources/images/logo-one.png";
		
		PdfPTable backCard = PdfHelper.createTable(1,0f,0f,100);
		PdfPCell cell = new PdfPCell();
		
		PdfPTable innerTable = PdfHelper.createTable(1,15f,15f,100);
		PdfHelper.noBorderCell(innerTable,"Sathish Kumar",10,null,10,Element.ALIGN_LEFT);
		
		PdfHelper.createLogo(innerTable,logoPath,0,0,5,20,Element.ALIGN_RIGHT);
		cell.addElement(innerTable);
		
		PdfHelper.imageBgm(bgmPath, backCard,cell, 158);
		return backCard;
	}

	/**
	 * Validate the Email Link
	 * 
	 * @param dto
	 * @return result
	 */
	@Override
	public Result validateUuid(ValidateLinkDTO dto) {
		Result result = new Result();
		try {
			if (ObjectUtils.isNotEmpty(dto.getEnv()) && dto.getEnv().equals("Dev")
					&& ObjectUtils.isNotEmpty(dto.getUuid())) {
				String decryptedUuid = "";
				try {
					decryptedUuid = AESUtils.decrypt(dto.getUuid());
				}catch(Exception e) {
					log.error("Error in UUID decrypt :: {}", e.getMessage(), e);
					result.setStatusCode(HttpStatus.BAD_REQUEST.value());
					result.setErrorMessage("Link not valid.");
					return result;
				}
				UserValidation userValidation = userValidationDAO
						.findByUuidTypeAndUuidLink(dto.getLinkType(),decryptedUuid);
				String[] uuidData = decryptedUuid.split("#");
				Long timeMilli = uuidData.length > 1 ?  Long.valueOf(uuidData[1]) : null;
				LocalDateTime generateTime = DateUtility.getLongMilli(timeMilli);

				log.info("Generated Date :: {}",generateTime);
				
				if (generateTime != null && !generateTime.isBefore(LocalDateTime.now().minusDays(1)) 
						&& ObjectUtils.isNotEmpty(userValidation)) {
					result.setStatusCode(HttpStatus.OK.value());
					result.setErrorMessage("Link Valid.");
				} else {
					result.setStatusCode(HttpStatus.BAD_REQUEST.value());
					result.setErrorMessage("Link expired.");
				}
				if (ObjectUtils.isNotEmpty(userValidation) 
						&& ObjectUtils.isNotEmpty(userValidation.getUuidLink())) {
					userValidation.setUuidLink(null);
					userValidationDAO.save(userValidation);
				}
			} else {
				result.setStatusCode(HttpStatus.BAD_REQUEST.value());
				result.setErrorMessage("Link not valid.");
			}
		} catch (Exception e) {
			log.error("Error in validateUuid :: {}", e.getMessage(), e);
			throw new CustomException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}
	
}
