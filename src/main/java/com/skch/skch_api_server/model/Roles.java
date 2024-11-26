package com.skch.skch_api_server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "roles", schema = "hostel")
public class Roles extends Audit{
	
	@Id
    @Column(name = "role_id")
    private Long roleId;
    
    @Column(name = "role_name", nullable = false)
    private String roleName;
    
    @Column(name = "is_active")
    private boolean isActive;
    
    @Column(name = "is_external_role")
    private boolean isExternalRole;
    
    @Column(name = "note", columnDefinition = "text")
    private String note;
    
}
