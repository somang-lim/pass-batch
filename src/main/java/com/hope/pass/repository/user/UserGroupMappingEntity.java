package com.hope.pass.repository.user;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.hope.pass.repository.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@IdClass(UserGroupMappingId.class)
@Entity
@Table(name = "user_group_mapping")
public class UserGroupMappingEntity extends BaseEntity {

	@Setter
	@Id
	private String userGroupId;

	@Setter
	@Id
	private String userId;


	private String userGroupName;

	private String description;

}
