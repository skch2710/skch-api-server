package com.skch.skchhostelservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.skch.skchhostelservice.dto.HostellerDTO;
import com.skch.skchhostelservice.dto.HostellerGridDTO;
import com.skch.skchhostelservice.dto.PaymentHistoryDTO;
import com.skch.skchhostelservice.dto.UserDTO;
import com.skch.skchhostelservice.dto.UserPrivilegeDTO;
import com.skch.skchhostelservice.model.Hosteller;
import com.skch.skchhostelservice.model.HostellerGrid;
import com.skch.skchhostelservice.model.PaymentHistory;
import com.skch.skchhostelservice.model.UserPrivilege;
import com.skch.skchhostelservice.model.Users;
import com.skch.skchhostelservice.util.AESUtils;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ObjectMapper {

	ObjectMapper INSTANCE = Mappers.getMapper(ObjectMapper.class);

	@Mapping(source = "joiningDate", target = "joiningDate", dateFormat = "dd-MM-yyyy")
	@Mapping(target = "vacatedDate", ignore=true)
	@Mapping(source = "dob", target = "dob", dateFormat = "dd-MM-yyyy")
	Hosteller fromHostellerDTO(HostellerDTO dto);
	List<Hosteller> fromHostellerDTO(List<HostellerDTO> dtos);

	@Mapping(source = "feeDate", target = "feeDate", dateFormat = "MM/dd/yyyy")
	PaymentHistory fromPaymentHistoryDTO(PaymentHistoryDTO dto);
	List<PaymentHistory> fromPaymentHistoryDTO(List<PaymentHistoryDTO> dtos);

	@Mapping(source = "dob", target = "dob", dateFormat = "MM/dd/yyyy")
	Users fromUserDTO(UserDTO dto);
	List<Users> fromUserDTO(List<UserDTO> dto);
	
	@Mapping(source = "dob", target = "dob", dateFormat = "MM/dd/yyyy")
	UserDTO fromUserModel(Users user);
	List<UserDTO> fromUserModel(List<Users> users);
	
	UserPrivilege fromUserPrivilegeDTO(UserPrivilegeDTO userPrivilegeDTO);
	List<UserPrivilege> fromUserPrivilegeDTO(List<UserPrivilegeDTO> userPrivilegeDTO);
	
	@Mapping(source = "resource.resourceId", target = "resourceId")
	UserPrivilegeDTO fromUserPrivilegeModel(UserPrivilege userPrivilege);
	List<UserPrivilegeDTO> fromUserPrivilegeModel(List<UserPrivilege> userPrivileges);
	
	@Mapping(source = "joiningDate", target = "joiningDate", dateFormat = "MM/dd/yyyy")
	HostellerDTO formHostelModel(Hosteller hostellers);
	List<HostellerDTO> formHostelModel(List<Hosteller> allHostellers);
	
//	default String mapName(HostellerGrid hosteller) {
//		String output = hosteller.getPhoneNumber() != null && 
//				!hosteller.getPhoneNumber().isBlank() ? 
//						AESUtils.encrypt(hosteller.getPhoneNumber()) : "";
//		return output;
//	}
	
	@Named("mapPhoneNumber")
	default String mapPhoneNumber(String input) {
		String output = input != null && 
				!input.isBlank() ? AESUtils.encrypt(input) : "";
		return output;
	}
	
	@Named("mapEncprict")
	default String mapEncprict(String input) {
		String output = input != null && 
				!input.isBlank() ? AESUtils.encrypt(input) : "";
		return output;
	}
	
	@Mapping(source = "dob", target = "dob", dateFormat = "MM/dd/yyyy")
	@Mapping(source = "joiningDate", target = "joiningDate", dateFormat = "MM/dd/yyyy")
	@Mapping(target = "phoneNumber", expression = "java(mapEncprict(hosteller.getPhoneNumber()))")
	@Mapping(target = "address", expression = "java(mapEncprict(hosteller.getAddress()))")
	HostellerGridDTO formHostelGridModel(HostellerGrid hosteller);
	List<HostellerGridDTO> formHostelGridModel(List<HostellerGrid> hostellersList);
}
