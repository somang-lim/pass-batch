package com.hope.pass.job.pass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.hope.pass.config.TestBatchConfig;
import com.hope.pass.repository.pass.PassEntity;
import com.hope.pass.repository.pass.PassRepository;
import com.hope.pass.repository.pass.PassStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@ContextConfiguration(classes = {ExpirePassesJobConfig.class, TestBatchConfig.class})
@SpringBatchTest
@SpringBootTest
class ExpirePassesJobConfigTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private PassRepository passRepository;


	@Test
	void test_expirePassesStep() throws Exception {
		// given
		addPassEntities(10);

		// when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		JobInstance jobInstance = jobExecution.getJobInstance();

		// then
		assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
		assertEquals("expirePassesJob", jobInstance.getJobName());
	}

	private void addPassEntities(int size) {
		final LocalDateTime now = LocalDateTime.now();
		final Random random = new Random();

		List<PassEntity> passEntities = new ArrayList<>();
		for (int i = 0; i < size; ++i) {
			PassEntity entity = new PassEntity();
			entity.setPackageSeq(1);
			entity.setUserId("A" + 1000000 + i);
			entity.setStatus(PassStatus.PROGRESSED);
			entity.setRemainingCount(random.nextInt(11));
			entity.setStartedAt(now.minusDays(60));
			entity.setEndedAt(now.minusDays(1));

			passEntities.add(entity);
		}
		passRepository.saveAll(passEntities);
	}

}
