package com.hope.pass.job.notification;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.hope.pass.repository.booking.BookingEntity;
import com.hope.pass.repository.booking.BookingStatus;
import com.hope.pass.repository.notification.NotificationEntity;
import com.hope.pass.repository.notification.NotificationEvent;
import com.hope.pass.repository.notification.NotificationModelMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SendNotificationBeforeClassJobConfig {

	private final int CHUNK_SIZE = 10;

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;
	private final SendNotificationItemWriter sendNotificationItemWriter;


	@Bean
	public Job sendNotificationBeforeClassJob() {
		return jobBuilderFactory.get("sendNotificationBeforeClassJob")
				.start(addNotificationStep())
				.next(sendNotificationStep())
				.build();
	}


	@Bean
	public Step addNotificationStep() {
		return stepBuilderFactory.get("addNotificationStep")
				.<BookingEntity, NotificationEntity>chunk(CHUNK_SIZE)
				.reader(addNotificationItemReader())
				.processor(addNotificationItemProcessor())
				.writer(addNotificationItemWriter())
				.build();
	}

	/**
	 * JpaPagingItemReader: JPA 에서 사용하는 페이징 기법
	 * 쿼리 당 pagSize 만큼 다른 PagingItemReader 와 마찬가지로 Thread-safe 한다.
	 */
	@Bean
	public JpaPagingItemReader<BookingEntity> addNotificationItemReader() {
		return new JpaPagingItemReaderBuilder<BookingEntity>()
				.name("addNotificationItemReader")
				.entityManagerFactory(entityManagerFactory)
				// pageSize: 한 번에 조회할 row 수
				.pageSize(CHUNK_SIZE)
				// 상태(status)가 준비 중이며, 시작일시(startedAt)이 10분 후 시작하는 예약이 알람 대상이 된다.
				.queryString("select b from BookingEntity b join fetch b.userEntity where b.status = :status and b.startedAt = :startedAt order by b.bookingSeq")
				.parameterValues(Map.of("status", BookingStatus.READY, "startedAt", LocalDateTime.now().plusMinutes(10)))
				.build();
	}

	@Bean
	public ItemProcessor<BookingEntity, NotificationEntity> addNotificationItemProcessor() {
		return bookingEntity -> NotificationModelMapper.INSTANCE.toNotificationEntity(bookingEntity, NotificationEvent.BEFORE_CLASS);
	}

	@Bean
	public JpaItemWriter<NotificationEntity> addNotificationItemWriter() {
		return new JpaItemWriterBuilder<NotificationEntity>()
				.entityManagerFactory(entityManagerFactory)
				.build();
	}


	/**
	 * reader 는 synchronized 로 순차적으로 실행되지만, writer 는 multi-thread 로 동작한다.
	 */
	@Bean
	public Step sendNotificationStep() {
		return stepBuilderFactory.get("sendNotificationStep")
				.<NotificationEntity, NotificationEntity>chunk(CHUNK_SIZE)
				.reader(sendNotificationItemReader())
				.writer(sendNotificationItemWriter)
				.taskExecutor(new SimpleAsyncTaskExecutor()) // 가장 간단한 multi-thread TaskExecutor 선언
				.build();
	}

	/**
	 *  SynchronizedItemStreamReader: multi-thread 환경에서 reader 와 writer 는 thread-safe 해야 한다.
	 *  Cursor 기법의 ItemReader 는 thread-safe 하지 않아서 Paging 기법을 사용하거나 synchronized 를 선언하여 순차적으로 수행해야 한다.
	 */
	@Bean
	public SynchronizedItemStreamReader<NotificationEntity> sendNotificationItemReader() {
		JpaCursorItemReader<NotificationEntity> itemReader = new JpaCursorItemReaderBuilder<NotificationEntity>()
				.name("sendNotificationItemReader")
				.entityManagerFactory(entityManagerFactory)
				// 이벤트(event)가 수업 전이며, 발송 여부(sent)가 미발송인 알람이 조회 대상이 된다.
				.queryString("select n from notificationEntity n where n.event = :event and n.sent = :sent")
				.parameterValues(Map.of("event", NotificationEvent.BEFORE_CLASS, "sent", false))
				.build();

		return new SynchronizedItemStreamReaderBuilder<NotificationEntity>()
				.delegate(itemReader)
				.build();
	}

}
