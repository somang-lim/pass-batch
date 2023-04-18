package com.hope.pass.repository.pass;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

// ReportingPolicy.IGNORE: 일치하지 않은 필드를 무시한다.
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PassModelMapper {

	PassModelMapper INSTANCE = Mappers.getMapper(PassModelMapper.class);

	// 필드명이 다르거나 custom 하게 매핑해주기 위해서는 @Mapping 을 추가하면 된다.
	@Mapping(target = "status", qualifiedByName = "defaultStatus")
	@Mapping(target = "remainingCount", source = "bulkPassEntity.count")
	PassEntity toPassEntity(BulkPassEntity bulkPassEntity, String userId);

	// BulkPassStatus 와 관계 없이 PassStatus 값을 설정한다.
	@Named("defaultStatus")
	default PassStatus status(BulkPassStatus status) {
		return PassStatus.READY;
	}

}
