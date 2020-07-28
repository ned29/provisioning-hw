package com.voverc.provisioning;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Configuration
@ConfigurationProperties(prefix = "provisioning")
public class AppConfig {

	private String domain;
	private String port;
	private List<String> codecs;
}
