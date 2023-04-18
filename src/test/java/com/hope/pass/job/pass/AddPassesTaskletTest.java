package com.hope.pass.job.pass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.hope.pass.repository.pass.BulkPassEntity;
import com.hope.pass.repository.pass.BulkPassRepository;
import com.hope.pass.repository.pass.BulkPassStatus;
import com.hope.pass.repository.pass.PassEntity;
import com.hope.pass.repository.pass.PassRepository;
import com.hope.pass.repository.pass.PassStatus;
import com.hope.pass.repository.user.UserGroupMappingEntity;
import com.hope.pass.repository.user.UserGroupMappingRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class) // JUnit5
class AddPassesTaskletTest {

	@Mock
	private StepContribution stepContribution;

	@Mock
	private ChunkContext chunkContext;

	@Mock
	private PassRepository passRepository;

	@Mock
	private BulkPassRepository bulkPassRepository;

	@Mock
	private UserGroupMappingRepository userGroupMappingRepository;

	// @InjectMocks 클래스의 인스턴스를 생성하고 @Mock 으로 생성된 객체를 주입한다.
	@InjectMocks
	private AddPassesTasklet addPassesTasklet;


	@Test
	void test_execute() throws Exception {
		// given
		final String userGroupId = "GROUP";
		final String userId = "A1000000";
		final Integer packageSeq = 1;
		final Integer count = 10;

		final LocalDateTime now = LocalDateTime.now();

		final BulkPassEntity bulkPassEntity = new BulkPassEntity();
		bulkPassEntity.setPackageSeq(packageSeq);
		bulkPassEntity.setUserGroupId(userGroupId);
		bulkPassEntity.setStatus(BulkPassStatus.READY);
		bulkPassEntity.setCount(count);
		bulkPassEntity.setStartedAt(now);
		bulkPassEntity.setEndedAt(now.plusDays(60));

		final UserGroupMappingEntity userGroupMappingEntity = new UserGroupMappingEntity();
		userGroupMappingEntity.setUserGroupId(userGroupId);
		userGroupMappingEntity.setUserId(userId);

		// when
		when(bulkPassRepository.findByStatusAndStartedAtGreaterThan(eq(BulkPassStatus.READY), any())).thenReturn(List.of(bulkPassEntity));
		when(userGroupMappingRepository.findByUserGroupId(eq("GROUP"))).thenReturn(List.of(userGroupMappingEntity));

		RepeatStatus repeatStatus = addPassesTasklet.execute(stepContribution, chunkContext);

		// then
		// execute 의 return 값이 RepeatStatus 값을 확인한다.
		assertEquals(RepeatStatus.FINISHED, repeatStatus);

		// 추가된 PassEntity 값을 확인한다.
		ArgumentCaptor<List> passEntitiesCaptor = ArgumentCaptor.forClass(List.class);
		verify(passRepository, times(1)).saveAll(passEntitiesCaptor.capture());
		final List<PassEntity> passEntities = passEntitiesCaptor.getValue();

		assertEquals(1, passEntities.size());

		final PassEntity passEntity = passEntities.get(0);
		assertEquals(packageSeq, passEntity.getPackageSeq());
		assertEquals(userId, passEntity.getUserId());
		assertEquals(PassStatus.READY, passEntity.getStatus());
		assertEquals(count, passEntity.getRemainingCount());
	}

}
