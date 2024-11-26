package com.skch.skch_api_server.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "role_privileges", schema = "hostel")
public class RolePrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_privileges_id")
    private Long rolePrivilegesId;

    @OneToOne
	@JoinColumn(name = "role_id", nullable = true)
	private Roles roles;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "read_only_flag")
    private boolean readOnlyFlag;

    @Column(name = "read_write_flag")
    private boolean readWriteFlag;

    @Column(name = "terminate_flag")
    private boolean terminateFlag;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_by_id")
    private Long createdById;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "modified_by_id")
    private Long modifiedById;

    @Column(name = "modified_date")
    private Date modifiedDate;
}