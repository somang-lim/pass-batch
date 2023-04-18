package com.hope.pass.job.pass;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Future;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.hope.pass.repository.booking.BookingEntity;
import com.hope.pass.repository.booking.BookingRepository;
import com.hope.pass.repository.booking.BookingStatus;
import com.hope.pass.repository.pass.PassEntity;
import com.hope.pass.repository.pass.PassRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class UsePassesJobConfig {
	private final int CHUNK_SIZE = 5;

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;
	private final PassRepository passRepository;
	private final BookingRepository bookingRepository;


	@Bean
	public Job usePassesJob() {
		return jobBuilderFactory.get("usePassesJob")
				.start(usePassesStep())
				.build();
	}

	@Bean
	public Step usePassesStep() {
		return stepBuilderFactory.get("usePassesStep")
				.<BookingEntity, Future<BookingEntity>>chunk(CHUNK_SIZE)
				.reader(usePassesItemReader())
				.processor(usePassesAsyncItemProcessor())
				.writer(usePassesAsyncItemWriter())
				.build();
	}


	@Bean
	public JpaCursorItemReader<BookingEntity> usePassesItemReader() {
		return new JpaCursorItemReaderBuilder<BookingEntity>()
				.name("usePassesItemReader")
				.entityManagerFactory(entityManagerFactory)
				.queryString(
					"select b from BookingEntity b join fetch b.passEntity where b.status = :status and b.usedPass = false and b.endedAt < :endedAt")
				.parameterValues(Map.of("status", BookingStatus.COMPLETED, "endedAt", LocalDateTime.now()))
				.build();
	}

	@Bean
	public AsyncItemProcessor<BookingEntity, BookingEntity> usePassesAsyncItemProcessor() {
		AsyncItemProcessor<BookingEntity, BookingEntity> asyncItemProcessor = new AsyncItemProcessor<>();
		asyncItemProcessor.setDelegate(usePassesItemProcessor());
		asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());

		return asyncItemProcessor;
	}

	@Bean
	public ItemProcessor<BookingEntity, BookingEntity> usePassesItemProcessor() {
		return bookingEntity -> {
			PassEntity passEntity = bookingEntity.getPassEntity();
			passEntity.setRemainingCount(passEntity.getRemainingCount() - 1);
			bookingEntity.setPassEntity(passEntity);

			bookingEntity.setUsedPass(true);

			return bookingEntity;
		};
	}

	@Bean
	public AsyncItemWriter<BookingEntity> usePassesAsyncItemWriter() {
		AsyncItemWriter<BookingEntity> asyncItemWriter = new AsyncItemWriter();
		asyncItemWriter.setDelegate(usePassesItemWriter());

		return asyncItemWriter;
	}

	@Bean
	public ItemWriter<BookingEntity> usePassesItemWriter() {
		return bookingEntities -> {
			for (BookingEntity bookingEntity : bookingEntities) {
				int updatedCount = passRepository.updateRemainingCount(bookingEntity.getPassSeq(), bookingEntity.getPassEntity().getRemainingCount());

				if (updatedCount > 0) {
					bookingRepository.updateUsedPass(bookingEntity.getPassSeq(), bookingEntity.isUsedPass());
				}
			}
		};
	}

}
