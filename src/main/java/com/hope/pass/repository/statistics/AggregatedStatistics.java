package com.hope.pass.repository.statistics;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AggregatedStatistics {

	private LocalDateTime statisticsAt; // 일 단위

	private long allCount;

	private long attendedCount;

	private long cancelledCount;


	public void merge(final AggregatedStatistics statistics) {
		allCount += statistics.getAllCount();
		attendedCount += statistics.getAttendedCount();
		cancelledCount += statistics.getCancelledCount();
	}

}
