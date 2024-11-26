package com.skch.skch_api_server.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class Audit {

	@Column(name = "created_by_id")
	private Long createdById;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "modified_by_id")
	private Long modifiedById;

	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

}
