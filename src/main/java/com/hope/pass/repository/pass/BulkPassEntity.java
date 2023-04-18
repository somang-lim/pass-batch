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
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "bulk_pass")
public class BulkPassEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bulkPassSeq;

	@Setter
	private Integer packageSeq;

	@Setter
	private String userGroupId;


	@Setter
	@Enumerated(EnumType.STRING)
	private BulkPassStatus status;

	@Setter
	private Integer count;


	@Setter
	private LocalDateTime startedAt;

	@Setter
	private LocalDateTime endedAt;

}
