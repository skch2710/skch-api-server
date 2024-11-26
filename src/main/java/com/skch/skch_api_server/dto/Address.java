package com.skch.skch_api_server.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("country_name")
    private String countryName;

    @SerializedName("address_purpose")
    private String addressPurpose;

    @SerializedName("address_type")
    private String addressType;

    @SerializedName("address_1")
    private String address1;
    
    @SerializedName("address_2")
    private String address2;

    @SerializedName("city")
    private String city;

    @SerializedName("state")
    private String state;

    @SerializedName("postal_code")
    private String postalCode;

    @SerializedName("telephone_number")
    private String telephoneNumber;
}

