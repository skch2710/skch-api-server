package com.skch.skch_api_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {
	private int pageNumber;
	private int pageSize;
	private String sortBy;
	private String sortOrder;
	private ColumnFilter[] columnFilters;
	private boolean exportExcel;
	private boolean exportCsv;
	private boolean exportPdf;
	private boolean exportZip;
	private boolean fullLoad;
}
