package com.skch.skchhostelservice.controller;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Basic {

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
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

