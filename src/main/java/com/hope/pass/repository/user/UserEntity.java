package com.hope.pass.repository.user;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hope.pass.repository.BaseEntity;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {

	@Id
	private String userId;

	private String userName;

	@Enumerated(EnumType.STRING)
	private UserStatus status;

	private String phone;

}
