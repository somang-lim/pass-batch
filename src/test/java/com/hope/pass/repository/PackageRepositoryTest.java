package com.hope.pass.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.hope.pass.repository.packaze.PackageEntity;
import com.hope.pass.repository.packaze.PackageRepository;

import lombok.extern.slf4j.Slf4j;

// @DataJpaTest

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class PackageRepositoryTest {

	@Autowired
	private PackageRepository packageRepository;

	@Test
	void test_save() {
		// given
		PackageEntity entity = new PackageEntity();
		entity.setPackageName("바디 챌린지 PT 12주");
		entity.setPeriod(84);

		// when
		packageRepository.save(entity);

		// then
		assertNotNull(entity.getPackageSeq());
	}

	@Test
	void test_findByCreatedAtAfter() {
		// given
		LocalDateTime dateTime = LocalDateTime.now().minusMinutes(1);

		PackageEntity entity1 = new PackageEntity();
		entity1.setPackageName("학생 전용 3개월");
		entity1.setPeriod(90);
		packageRepository.save(entity1);

		PackageEntity entity2 = new PackageEntity();
		entity2.setPackageName("학생 전용 6개월");
		entity2.setPeriod(180);
		packageRepository.save(entity2);

		// when
		final List<PackageEntity> packageEntities = packageRepository.findByCreatedAtAfter(dateTime, PageRequest.of(0, 1, Sort.by("packageSeq").descending()));

		// then
		assertEquals(1, packageEntities.size());
		assertEquals(entity2.getPackageSeq(), packageEntities.get(0).getPackageSeq());
	}

	@Test
	void test_updateCountAndPeriod() {
		// given
		PackageEntity entity = new PackageEntity();
		entity.setPackageName("바디프로필 이벤트 4개월");
		entity.setPeriod(90);
		packageRepository.save(entity);

		// when
		int updatedCount = packageRepository.updateCountAndPeriod(entity.getPackageSeq(), 30, 120);
		final PackageEntity updatedEntity = packageRepository.findById(entity.getPackageSeq()).get();

		// then
		assertEquals(1, updatedCount);
		assertEquals(30, updatedEntity.getCount());
		assertEquals(120, updatedEntity.getPeriod());
	}

	@Test
	void test_delete() {
		// given
		PackageEntity entity = new PackageEntity();
		entity.setPackageName("제거할 이용권");
		entity.setCount(1);
		PackageEntity newEntity = packageRepository.save(entity);

		// when
		packageRepository.deleteById(newEntity.getPackageSeq());

		// then
		assertTrue(packageRepository.findById(newEntity.getPackageSeq()).isEmpty());
	}

}
