package com.skch.skchhostelservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.skch.skchhostelservice.util.DateUtility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class HostellerGrid {

	@Id
	@Column(name = "hosteller_id")
	private Long hostellerId;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "dob")
	private LocalDate dob;

	@Column(name = "fee")
	private BigDecimal fee;

//	@JsonFormat(pattern = "MM/dd/yyyy")
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

	@Column(name = "total_count")
	private Long totalCount;
	
	public String[] getData() {
		return new String[] {
	            this.fullName,
	            this.emailId,
	            this.phoneNumber,
				DateUtility.dateToString(this.dob, "yyyy-MM-dd"),
	            this.fee != null ? this.fee.toString() : "",
	            DateUtility.dateToString(this.joiningDate, "yyyy-MM-dd"),
	            this.address,
	            this.proof,
	            this.reason,
	            DateUtility.dateToString(this.vacatedDate, "yyyy-MM-dd"),
	            this.active != null && this.active ? "Yes" : "No"
	        };
	}

}
