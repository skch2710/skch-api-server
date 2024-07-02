package com.skch.skchhostelservice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skch.skchhostelservice.model.Audit;
import com.skch.skchhostelservice.util.Utility;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "createdById", "createdDate", "modifiedById", "modifiedDate" })
public class HostellerDTO extends Audit {

	private Long hostellerId;

	@NotNull(message = "Name cannot be null")
	@Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
	@Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Full Name Not a Valid")
	private String fullName;

	@NotNull(message = "Email cannot be null")
	@Email(message = "Email should be valid")
	private String emailId;

	@Pattern(regexp = "^[0-9]*$", message = "Phone number is not valid")
	private String phoneNumber;

	@NotNull(message = "Fee cannot be null")
	@Pattern(regexp = "^-?\\d{1,3}(,\\d{3})*(\\.\\d{1,3})?$|^-?\\d+(\\.\\d{1,3})?$", message = "Fee is not valid")
	private String fee;

	@NotNull(message = "Joining Date cannot be null")
	@Pattern(regexp = "^(0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-\\d{4}$", message = "Joining Date(dd-MM-yyyy) is not valid")
	private String joiningDate;

	private String address;
	private String proof;

	@NotNull(message = "Reason cannot be null")
	@NotBlank(message = "Reason cannot be Blank")
	private String reason;

	@Pattern(regexp = "^(0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-\\d{4}$", message = "Vacated Date(dd-MM-yyyy) is not valid")
	private String vacatedDate;

	private String active;
	
	private String error;
	
	public HostellerDTO(List<String> cellValues) {
		this.fullName = cellValues.get(0);
		this.emailId = cellValues.get(1);
		this.phoneNumber = cellValues.get(2);
		this.fee = cellValues.get(3);
		this.joiningDate = cellValues.get(4);
		this.address = cellValues.get(5);
		this.proof = cellValues.get(6);
		this.reason = cellValues.get(7);
		this.vacatedDate = cellValues.get(8);
		this.active = cellValues.get(9);
		Utility.updateFields(this, "C");
	}

}
