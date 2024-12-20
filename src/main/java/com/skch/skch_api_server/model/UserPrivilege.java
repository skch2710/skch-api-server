package com.skch.skch_api_server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_privileges", schema = "hostel")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "users" })
public class UserPrivilege  extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_privileges_id")
    private Long userPrivilegesId;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = true)
	private Users users;

    @OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "resource_id", nullable = true)
	private Resource resource;

    @Column(name = "read_only_flag")
    private Boolean readOnlyFlag;

    @Column(name = "read_write_flag")
    private Boolean readWriteFlag;

    @Column(name = "terminate_flag")
    private Boolean terminateFlag;

    @Column(name = "is_active")
    private Boolean isActive;

}
