package com.hope.pass.repository.user;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.hope.pass.repository.BaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonType;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@TypeDef(name = "json", typeClass = JsonType.class)
@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {

	@Id
	private String userId;

	private String userName;

	@Enumerated(EnumType.STRING)
	private UserStatus status;

	private String phone;


	// json 형태로 저장되어 있는 문자열 데이터를 Map 으로 매핑한다.
	@Type(type = "json")
	private Map<String, Object> meta;


	public String getUuid() {
		String uuid = null;
		if (meta.containsKey("uuid")) {
			uuid = String.valueOf(meta.get("uuid"));
		}
		return uuid;
	}

}
