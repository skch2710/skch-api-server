package com.skch.skch_api_server.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@DynamicUpdate
@Table(name = "payment_history", schema = "hostel")
public class PaymentHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Long paymentId;

	@Column(name = "hosteller_id")
	private Long hostellerId;

	@Column(name = "fee_paid")
	private BigDecimal feePaid;

	@Column(name = "fee_due")
	private BigDecimal feeDue;

	@Column(name = "fee_date")
	private LocalDate feeDate;

	@Column(name = "payment_mode")
	private String paymentMode;

	@Column(name = "created_by_id")
	private Long createdById;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "modified_by_id")
	private Long modifiedById;

	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

}
