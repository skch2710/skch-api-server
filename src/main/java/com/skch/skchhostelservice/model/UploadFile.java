package com.skch.skchhostelservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "upload_file", schema = "public")
public class UploadFile {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "upload_file_id", nullable = false)
    private Long uploadFileId;

    @Column(name = "file_name", length = 50)
    private String fileName;

    @Column(name = "upload_type", length = 25)
    private String uploadType;

    @Column(name = "uploaded_by_id")
    private Long uploadedById;

    @Column(name = "uploaded_date")
    private LocalDateTime uploadedDate;

    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "total_count")
    private Long totalCount;

    @Column(name = "success_count")
    private Long successCount;

    @Column(name = "failure_count")
    private Long failureCount;

    @Column(name = "is_mail_sent", length = 1)
    private String isMailSent;

	public UploadFile() {
		this.uploadFileId = 0L;
		this.isMailSent = "N";
		this.uploadedDate = LocalDateTime.now();
		this.statusId = 1;
	}

}
