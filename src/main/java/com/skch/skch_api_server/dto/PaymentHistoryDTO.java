package com.skch.skch_api_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryDTO {

	private Long paymentId;
	private Long hostellerId;
	private String feePaid;
	private String feeDue;
	private String feeDate;
	private String paymentMode;
	private Long createdById;
	private Long modifiedById;

}
