package com.skch.skchhostelservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.skch.skchhostelservice.dto.HostellerDTO;
import com.skch.skchhostelservice.dto.PaymentHistoryDTO;
import com.skch.skchhostelservice.model.Hosteller;
import com.skch.skchhostelservice.model.PaymentHistory;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ObjectMapper {

	ObjectMapper INSTANCE = Mappers.getMapper(ObjectMapper.class);

	@Mapping(source = "joiningDate",target = "joiningDate",dateFormat = "MM/dd/yyyy")
	Hosteller fromHostellerDTO(HostellerDTO dto);
	List<Hosteller> fromHostellerDTO(List<HostellerDTO> dtos);
	
	@Mapping(source = "feeDate",target = "feeDate",dateFormat = "MM/dd/yyyy")
	PaymentHistory fromPaymentHistoryDTO(PaymentHistoryDTO dto);
	List<PaymentHistory> fromPaymentHistoryDTO(List<PaymentHistoryDTO> dtos);
}
