package com.hope.pass.job.pass;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class AddPassesJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final AddPassesTasklet addPassesTasklet;


	@Bean
	public Job addPassesJob() {
		return jobBuilderFactory.get("addPassesJob")
			.start(addPassesStep())
			.build();
	}

	@Bean
	public Step addPassesStep() {
		return stepBuilderFactory.get("addPassesStep")
			.tasklet(addPassesTasklet)
			.build();
	}

}
