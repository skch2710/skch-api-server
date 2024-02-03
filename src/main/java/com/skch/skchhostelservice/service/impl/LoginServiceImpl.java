package com.skch.skchhostelservice.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.skch.skchhostelservice.dao.UsersDAO;
import com.skch.skchhostelservice.dto.JwtDTO;
import com.skch.skchhostelservice.dto.LoginRequest;
import com.skch.skchhostelservice.dto.LoginResponse;
import com.skch.skchhostelservice.dto.Navigation;
import com.skch.skchhostelservice.dto.Result;
import com.skch.skchhostelservice.dto.SubNavigarion;
import com.skch.skchhostelservice.dto.UserDTO;
import com.skch.skchhostelservice.exception.CustomException;
import com.skch.skchhostelservice.mapper.ObjectMapper;
import com.skch.skchhostelservice.model.Users;
import com.skch.skchhostelservice.service.LoginService;
import com.skch.skchhostelservice.util.CacheService;
import com.skch.skchhostelservice.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
	
	private ObjectMapper MAPPER = ObjectMapper.INSTANCE;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UsersDAO userDAO;

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
						UserDTO userDTO = MAPPER.fromUserModel(user);
						loginResponse.setUser(userDTO);
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
				loginResponse.setUser(MAPPER.fromUserModel(user));
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
						navigation.setResourceId(obj.getResource().getResourceId());
						navigation.setResourceName(obj.getResource().getResourceName());
						navigation.setIcon(obj.getResource().getIcon());
						navigation.setResourcePath(obj.getResource().getResourcePath());
						navigation.setDisplayOrder(obj.getResource().getDisplayOrder());
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
					navigation.setResourceId(obj.getResource().getResourceId());
					navigation.setResourceName(obj.getResource().getResourceName());
					navigation.setIcon(obj.getResource().getIcon());
					navigation.setResourcePath(obj.getResource().getResourcePath());
					navigation.setDisplayOrder(obj.getResource().getDisplayOrder());
					navList.add(navigation);	
					});
				if(!listNav.isEmpty()) {
					nav.setResourceName(listNav.get(0).getResource().getParentName());
					nav.setIcon(listNav.get(0).getResource().getParentIcon());
					nav.setResourcePath(listNav.get(0).getResource().getParentPath());
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
	
}
