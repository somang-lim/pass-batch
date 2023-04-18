package com.hope.pass.repository.statistics;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hope.pass.repository.BaseEntity;
import com.hope.pass.repository.booking.BookingEntity;
import com.hope.pass.repository.booking.BookingStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "statistics")
public class StatisticsEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer statisticsSeq;

	@Setter
	private LocalDateTime statisticsAt; // 일 단위

	@Setter
	private int allCount;

	@Setter
	private int attendedCount;

	@Setter
	private int cancelledCount;


	public static StatisticsEntity create(final BookingEntity bookingEntity) {
		StatisticsEntity statisticEntity = new StatisticsEntity();
		statisticEntity.setStatisticsAt(bookingEntity.getStatisticsAt());
		statisticEntity.setAllCount(1);

		if (bookingEntity.isAttended()) {
			statisticEntity.setAttendedCount(1);
		}

		if (BookingStatus.CANCELLED.equals(bookingEntity.getStatus())) {
			statisticEntity.setCancelledCount(1);
		}

		return statisticEntity;
	}

	public void add(final BookingEntity bookingEntity) {
		allCount++;

		if (bookingEntity.isAttended()) {
			attendedCount++;
		}

		if (BookingStatus.CANCELLED.equals(bookingEntity.getStatus())) {
			cancelledCount++;
		}
	}

}
