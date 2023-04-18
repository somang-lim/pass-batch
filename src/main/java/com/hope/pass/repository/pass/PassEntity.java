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
@Table(name = "pass")
public class PassEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer passSeq;

	@Setter
	private Integer packageSeq;

	@Setter
	private String userId;


	@Setter
	@Enumerated(EnumType.STRING)
	private PassStatus status;

	@Setter
	private Integer remainingCount;


	@Setter
	private LocalDateTime startedAt;

	@Setter
	private LocalDateTime endedAt;

	@Setter
	private LocalDateTime expiredAt;

}
