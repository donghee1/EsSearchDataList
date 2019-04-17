package com.oksusu.hdh.boot.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.oksusu.hdh.boot.config.CommonProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableConfigurationProperties
public class SlackNotifier {
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private CommonProperties commonPro;
	
	/*
	 * webkook 참고 블로그 - https://java.ihoney.pe.kr/447
	 * slack attachment guide - https://api.slack.com/docs/message-attachments
	 */
	
	/**
	 * Slack Message Attachment Class
	 * - attachment 항목을 추가하려면 guide 확인 후 해당 필드 추가
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class SlackMessageAttachement {
		private String color;
//		private String pretext;
		private String title;
		private String text;
	}
	
	/**
	 * Slack Message Class
	 * - message 항목을 추가하려면 guide 확인 후 해당 필드 추가
	 */
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class SlackMessage {
//		private String text;
		private String channel;
		private List<SlackMessageAttachement> attachments;
		
//		void addAttachment(SlackMessageAttachement attachement) {
//			if (this.attachments == null) {
//				this.attachments = Lists.newArrayList();
//			}
//			
//			this.attachments.add(attachement);
//		}
	}
	
	public boolean notify(SlackMessageAttachement message) {
		SlackMessage slackMessage = SlackMessage
				.builder().channel(commonPro.getWebHookChannel())
				.attachments(Lists.newArrayList(message)).build();
		
		try {
			restTemplate.postForEntity(commonPro.getWebHookUrl(), slackMessage, String.class);
			return true;
		} catch (Exception e) {
			log.error("Occur Exception: {}", e);
			return false;
		}
	}
}
