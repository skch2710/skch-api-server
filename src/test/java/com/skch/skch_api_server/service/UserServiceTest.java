//package com.skch.skch_api_server.service;
//
//import static org.hamcrest.CoreMatchers.any;
//import static org.mockito.Mockito.when;
//
//import java.util.Date;
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentMatchers;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//
//import com.skch.skch_api_server.dao.UsersDAO;
//import com.skch.skch_api_server.dto.Result;
//import com.skch.skch_api_server.dto.UserDTO;
//import com.skch.skch_api_server.dto.UserPrivilegeDTO;
//import com.skch.skch_api_server.exception.CustomException;
//import com.skch.skch_api_server.mapper.ObjectMapper;
//import com.skch.skch_api_server.model.Resource;
//import com.skch.skch_api_server.model.Roles;
//import com.skch.skch_api_server.model.UserPrivilege;
//import com.skch.skch_api_server.model.UserRole;
//import com.skch.skch_api_server.model.Users;
//import com.skch.skch_api_server.service.impl.UserServiceImpl;
//import com.skch.skch_api_server.util.Utility;
//
//import lombok.extern.slf4j.Slf4j;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//
//@ExtendWith(MockitoExtension.class)
//@Slf4j
//public class UserServiceTest {
//	
//	@Mock
//	private UsersDAO usersDAO;
//	
//	@InjectMocks
//	private UserServiceImpl userServiceImpl;
//	
//	@Mock
//	private ObjectMapper MAPPER;
//	
//	@Test
//    void testSaveNewUser_Success() {
//		
//		log.info("Starting testSaveNewUser_Success...");
//		
//		log.info(""+ArgumentMatchers.<UserDTO>any());
//		
//		UserDTO dto = new UserDTO();
//	    dto.setEmailId("test@example.com");
//	    dto.setRoleId(1L);
//
//	    UserPrivilegeDTO privilegeDTO = new UserPrivilegeDTO(1L, 1L, true, false, false);
//	    dto.setUserPrivilege(List.of(privilegeDTO));
//
//	    Mockito.when(usersDAO.findByEmailIdIgnoreCase("test@example.com")).thenReturn(null);
//
//	    Users userEntity = new Users();
//	    when(MAPPER.fromUserDTO(dto)).thenReturn(userEntity);
//
//	    // Mocking the privilege mapping
//	    UserPrivilege userPrivilege = new UserPrivilege();
//	    when(MAPPER.fromUserPrivilegeDTO(privilegeDTO)).thenReturn(userPrivilege);
//
//	    when(usersDAO.save(userEntity)).thenReturn(userEntity);
//
//	    Result result = userServiceImpl.saveOrUpdateUser(dto);
//
//        assertEquals(HttpStatus.OK.value(), result.getStatusCode());
//        assertEquals("User Saved .....", result.getSuccessMessage());
//        assertNotNull(result.getData());
//    }
//
//	/*
//    @Test
//    void testSaveNewUser_EmailExists() {
//        UserDTO dto = new UserDTO();
//        dto.setEmailId("test@example.com");
//
//        when(usersDAO.findByEmailIdIgnoreCase("test@example.com")).thenReturn(new Users());
//
//        Result result = userService.saveOrUpdateUser(dto);
//
//        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode());
//        assertEquals("Email Id Already Exist...", result.getErrorMessage());
//    }
//
//    @Test
//    void testUpdateUser_Success() {
//        UserDTO dto = new UserDTO();
//        dto.setUserId(1L);
//        dto.setEmailId("test@example.com");
//        dto.setFirstName("Updated");
//        dto.setLastName("User");
//        dto.setDob("1990-01-01");
//        dto.setPhoneNumber("1234567890");
//        dto.setRoleId(2L);
//        dto.setUserPrivilege(List.of(
//                new UserPrivilegeDTO(1L, true, false, true)
//        ));
//
//        Users existingUser = new Users();
//        UserRole userRole = new UserRole();
//        userRole.setRoles(new Roles(1L));
//        existingUser.setUserRole(userRole);
//        existingUser.setUserPrivilege(List.of(
//                new UserPrivilege(new Resource(1L))
//        ));
//
//        when(usersDAO.findByEmailIdIgnoreCase("test@example.com")).thenReturn(existingUser);
//        when(usersDAO.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        try (MockedStatic<Utility> mockedUtil = Mockito.mockStatic(Utility.class)) {
//            mockedUtil.when(() -> Utility.dateConvert(any())).thenReturn(new Date());
//            mockedUtil.when(() -> Utility.updateFields(any(), eq("U"))).thenReturn(null);
//
//            Result result = userService.saveOrUpdateUser(dto);
//
//            assertEquals(HttpStatus.OK.value(), result.getStatusCode());
//            assertEquals("Updated...", result.getSuccessMessage());
//            assertNotNull(result.getData());
//        }
//    }
//
//    @Test
//    void testExceptionInSaveOrUpdateUser() {
//        UserDTO dto = new UserDTO();
//        dto.setEmailId("error@example.com");
//
//        when(usersDAO.findByEmailIdIgnoreCase(any())).thenThrow(new RuntimeException("DB error"));
//
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            userService.saveOrUpdateUser(dto);
//        });
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
//        assertEquals("DB error", exception.getMessage());
//    }
//	*/
//
//}
