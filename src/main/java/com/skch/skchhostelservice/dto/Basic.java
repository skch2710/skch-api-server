package com.skch.skchhostelservice.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Basic {
	
	@SerializedName("organization_name")
	private String organizationName;

    @SerializedName(value = "first_name", alternate = "authorized_official_first_name")
    private String firstName;

    @SerializedName(value = "last_name", alternate = "authorized_official_last_name")
    private String lastName;

    @SerializedName("gender")
    private String gender;

    @SerializedName("enumeration_date")
    private String enumerationDate;

    @SerializedName("last_updated")
    private String lastUpdated;

    @SerializedName("certification_date")
    private String certificationDate;

    @SerializedName("status")
    private String status;
}

