package com.hope.pass.repository.pass;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hope.pass.repository.BaseEntity;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "bulk_pass")
public class BulkPassEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bulkPassSeq;

	private Integer packageSeq;

	private String userGroupId;


	@Enumerated(EnumType.STRING)
	private BulkPassStatus status;

	private Integer count;


	private LocalDateTime startedAt;

	private LocalDateTime endedAt;

}
