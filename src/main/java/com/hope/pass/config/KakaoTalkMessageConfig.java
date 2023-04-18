package com.hope.pass.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "kakaotalk")
@Component
public class KakaoTalkMessageConfig {

	private String host;
	private String token;

}
