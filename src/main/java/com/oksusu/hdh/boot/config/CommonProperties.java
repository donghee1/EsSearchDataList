package com.oksusu.hdh.boot.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class CommonProperties {
	private List<Map<String, Object>> serverInfos = new ArrayList<>();
	private String webHookUrl;
	private String webHookChannel;
	private String listenerPath;
	private String propertiesPath;
	private String checkParam;
	private String solveParam;
	private String propertiesParam;
}
