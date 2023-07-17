package com.example.flashsaleproject.models;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity implements Serializable {
	
	@CreatedBy
	@Column(length=50, updatable=false)
	@JsonIgnore
	private String createdBy;
	
	@CreatedDate
	@Column(updatable=false)
	@JsonIgnore
	private Date createdAt = new Date();
	
	@LastModifiedBy
	@Column(length=50)
	@JsonIgnore
	private String lastModifiedBy;
	
	@LastModifiedDate
	@Column()
	@JsonIgnore
	private Date lastModifiedAt = new Date();
	
	
}
