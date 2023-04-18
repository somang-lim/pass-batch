package com.hope.pass.repository.booking;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.hope.pass.repository.BaseEntity;
import com.hope.pass.repository.pass.PassEntity;
import com.hope.pass.repository.user.UserEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "booking")
public class BookingEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bookingSeq;

	private Integer passSeq;

	private String userId;


	@Enumerated(EnumType.STRING)
	private BookingStatus status;

	@Setter
	private boolean usedPass;

	private boolean attended;


	private LocalDateTime startedAt;

	private LocalDateTime endedAt;

	private LocalDateTime cancelledAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", insertable = false, updatable = false)
	private UserEntity userEntity;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "passSeq", insertable = false, updatable = false)
	private PassEntity passEntity;


	// endedAt 기준, yyyy-MM-HH 00:00:00
	public LocalDateTime getStatisticsAt() {
		return endedAt.withHour(0).withMinute(0).withSecond(0).withNano(0);
	}
}
