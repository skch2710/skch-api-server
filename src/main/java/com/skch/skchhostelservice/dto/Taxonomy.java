package com.skch.skchhostelservice.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Taxonomy {

    @SerializedName("code")
    private String code;

    @SerializedName("taxonomy_group")
    private String taxonomyGroup;

    @SerializedName("desc")
    private String description;

    @SerializedName("state")
    private String state;

    @SerializedName("license")
    private String license;

    @SerializedName("primary")
    private boolean primary;
}

