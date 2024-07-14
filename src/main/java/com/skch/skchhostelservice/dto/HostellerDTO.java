package com.skch.skchhostelservice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opencsv.bean.CsvBindByName;
import com.skch.skchhostelservice.model.Audit;

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
	@CsvBindByName(column = "Full Name")
	private String fullName;

	@NotNull(message = "Email cannot be null")
	@Email(message = "Email should be valid")
	@CsvBindByName(column = "Email Id")
	private String emailId;

	@Pattern(regexp = "^[0-9]*$", message = "Phone number is not valid")
	@CsvBindByName(column = "Phone Number")
	private String phoneNumber;
	
	@NotNull(message = "DOB cannot be null")
	@Pattern(regexp = "^(0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-\\d{4}$", message = "DOB(dd-MM-yyyy) is not valid")
	@CsvBindByName(column = "DOB")
	private String dob;

	@NotNull(message = "Fee cannot be null")
	@Pattern(regexp = "^-?\\d{1,3}(,\\d{3})*(\\.\\d{1,3})?$|^-?\\d+(\\.\\d{1,3})?$", message = "Fee is not valid")
	@CsvBindByName(column = "Fee")
	private String fee;

	@NotNull(message = "Joining Date cannot be null")
	@Pattern(regexp = "^(0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-\\d{4}$", message = "Joining Date(dd-MM-yyyy) is not valid")
	@CsvBindByName(column = "Joining Date")
	private String joiningDate;

	@CsvBindByName(column = "Address")
	private String address;
	
	@CsvBindByName(column = "Proof")
	private String proof;

	@NotNull(message = "Reason cannot be null")
	@NotBlank(message = "Reason cannot be Blank")
	@CsvBindByName(column = "Reason")
	private String reason;

	@Pattern(regexp = "^(0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-\\d{4}$", message = "Vacated Date(dd-MM-yyyy) is not valid")
	@CsvBindByName(column = "Vacated Date")
	private String vacatedDate;

	@CsvBindByName(column = "Active")
	private String active;
	
	@JsonIgnore
	private String error;
	
	public HostellerDTO(List<String> cellValues,Long userId) {
		this.fullName = cellValues.get(0);
		this.emailId = cellValues.get(1);
		this.phoneNumber = cellValues.get(2);
		this.dob = cellValues.get(3);
		this.fee = cellValues.get(4);
		this.joiningDate = cellValues.get(5);
		this.address = cellValues.get(6);
		this.proof = cellValues.get(7);
		this.reason = cellValues.get(8);
		this.vacatedDate = cellValues.get(9);
		this.active = cellValues.get(10);
		this.setCreatedById(userId);
		this.setModifiedById(userId);
//		Utility.updateFields(this, "C");
	}
	
	public HostellerDTO(String[] cellValues, Long userId) {
        this.fullName = cellValues[0];
        this.emailId = cellValues[1];
        this.phoneNumber = cellValues[2];
        this.dob = cellValues[3];
        this.fee = cellValues[4];
        this.joiningDate = cellValues[5];
        this.address = cellValues[6];
        this.proof = cellValues[7];
        this.reason = cellValues[8];
        this.vacatedDate = cellValues[9];
        this.active = cellValues[10];
        this.setCreatedById(userId);
        this.setModifiedById(userId);
    }

}
