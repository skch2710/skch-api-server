package com.skch.skchhostelservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.skch.skchhostelservice.util.DateUtility;
import com.skch.skchhostelservice.util.Utility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hostellers", schema = "hostel")
public class Hosteller extends Audit{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hosteller_id")
	private Long hostellerId;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "fee")
	private BigDecimal fee;

	@Column(name = "joining_date")
	private LocalDate joiningDate;

	@Column(name = "address")
	private String address;

	@Column(name = "proof")
	private String proof;

	@Column(name = "reason")
	private String reason;

	@Column(name = "vacated_date")
	private LocalDateTime vacatedDate;

	@Column(name = "active")
	private Boolean active;

	public Hosteller(List<String> cellValues) {
		this.hostellerId = 0L;
		this.fullName = cellValues.get(0);
		this.emailId = cellValues.get(1);
		this.phoneNumber = cellValues.get(2);
		this.fee = Utility.toNum(cellValues.get(3));
		this.joiningDate = DateUtility.stringToDate(cellValues.get(4), "yyyyMMdd");
		this.address = cellValues.get(5);
		this.proof = cellValues.get(6);
		this.reason = cellValues.get(7);
		this.vacatedDate = DateUtility.stringToDateTimes(cellValues.get(8),"yyyyMMdd");
		this.active = cellValues.get(9).equalsIgnoreCase("Yes");
		Utility.updateFields(this, "C");
	}
	
}
