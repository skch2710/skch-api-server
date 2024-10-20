package com.skch.skchhostelservice.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NppesResponse {

    @SerializedName("result_count")
    private int resultCount;

    @SerializedName("results")
    private List<Results> results;
}
