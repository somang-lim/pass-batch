package com.hope.pass.job.pass;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hope.pass.repository.pass.PassEntity;
import com.hope.pass.repository.pass.PassStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class ExpirePassesJobConfig {
	private final int CHUNK_SIZE = 5;

	// @EnableBatchProcessing 을 통해 Bean 으로 제공된 JobBuilderFactory, StepBuilderFactory
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;


	@Bean
	public Job expirePassesJob() {
		return jobBuilderFactory.get("expirePassesJob")
				.start(expirePassesStep())
				.build();
	}

	@Bean
	public Step expirePassesStep() {
		return stepBuilderFactory.get("expirePassesStep")
				.<PassEntity, PassEntity>chunk(CHUNK_SIZE)
				.reader(expirePassesItemReader())
				.processor(expirePassesItemProcessor())
				.writer(expirePassesItemWriter())
				.build();
	}


	/**
	 * JpaCursorItemReader: JpaPagingItemReader 만 지원하다가 Spring 4.3 에서 추가되었다.
	 * 페이징 기법보다 더 높은 성능으로, 데이터 변경에 무관한 무결성 조회가 가능하다.
	 */
	@Bean
	@StepScope
	public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
		return new JpaCursorItemReaderBuilder<PassEntity>()
				.name("expirePassesItemReader")
				.entityManagerFactory(entityManagerFactory)
				.queryString("select p from PassEntity p where p.status = :status and p.endedAt <= :endedAt")
				.parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
				.build();
	}

	@Bean
	public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
		return passEntity -> {
				passEntity.setStatus(PassStatus.EXPIRED);
				passEntity.setExpiredAt(LocalDateTime.now());
				return passEntity;
		};
	}

	/**
	 * JpaItemWriter: JPA 영속성 관리를 위해 EntityManager 를 필수로 설정해야 한다.
	 */
	@Bean
	public JpaItemWriter<PassEntity> expirePassesItemWriter() {
		return new JpaItemWriterBuilder<PassEntity>()
				.entityManagerFactory(entityManagerFactory)
				.build();
	}

}
