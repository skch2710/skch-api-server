package com.skch.skchhostelservice.controller;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Results {

    @SerializedName("created_epoch")
    private String createdEpoch;

    @SerializedName("enumeration_type")
    private String enumerationType;

    @SerializedName("last_updated_epoch")
    private String lastUpdatedEpoch;

    @SerializedName("number")
    private String number;
    
    @SerializedName("basic")
    private Basic basic;

    @SerializedName("addresses")
    private List<Address> addresses;
    
    @SerializedName("practiceLocations")
    private List<Address> practiceLocations;

    @SerializedName("taxonomies")
    private List<Taxonomy> taxonomies;

}