package com.hope.pass.job.statistic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hope.pass.repository.statistics.AggregatedStatistics;
import com.hope.pass.repository.statistics.StatisticsRepository;
import com.hope.pass.util.CustomCSVWriter;
import com.hope.pass.util.LocalDateTimeUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@StepScope
@Component
public class MakeDailyStatisticsTasklet implements Tasklet {

	@Value("#{jobParameters[from]}")
	private String fromString;

	@Value("#{jobParameters[to]}")
	private String toString;

	private final StatisticsRepository statisticsRepository;


	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		final LocalDateTime from = LocalDateTimeUtils.parse(fromString);
		final LocalDateTime to = LocalDateTimeUtils.parse(toString);

		final List<AggregatedStatistics> statisticsList = statisticsRepository.findByStatisticsAtBetweenAndGroupBy(from, to);

		List<String[]> data = new ArrayList<>();
		data.add(new String[]{"statisticsAt", "allCount", "attendedCount", "cancelledCount"});
		for (AggregatedStatistics statistics : statisticsList) {
			data.add(new String[] {
				LocalDateTimeUtils.format(statistics.getStatisticsAt()),
				String.valueOf(statistics.getAllCount()),
				String.valueOf(statistics.getAttendedCount()),
				String.valueOf(statistics.getCancelledCount())
			});
		}

		CustomCSVWriter.write("daily_statistics_" + LocalDateTimeUtils.format(from, LocalDateTimeUtils.YYYY_MM_DD) + ".csv", data);

		return RepeatStatus.FINISHED;
	}

}
