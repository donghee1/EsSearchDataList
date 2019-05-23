package com.oksusu.hdh.boot.scheduler;

import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oksusu.hdh.boot.config.CommonProperties;
import com.oksusu.hdh.boot.util.SlackNotifier;
import com.oksusu.hdh.boot.util.SlackNotifier.SlackMessageAttachement;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SlackScheduler {

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	private SlackNotifier slackNotifier;
	
	@Autowired
    private CommonProperties commonProp;
	
	private enum _SLACK_COLOR { good, warning, danger}	// green, yellow, red
	
	/**
	 * server check scheduler
	 * - properties 의 서버를 체크하여 설정에 따라 slack 에 webhook 으로 메시지를 전송한다.
	 * 
	 */
	@Scheduled(cron = "0 */5 * * * *")
//	@Scheduled(cron = "*/10 * * * * *")
	protected void serverSatusNotifier() {
		
		// 책임님 요청사항
		// 현재 리스터가 꺼질 경우 재기동에 관한 url을 슬렉에 던저 주는데
		// 재기동과는 별개로 리스너의 사용여부 확인을 위한 url도 추가할 것!!
		// 4월8일 월요일 개발 예정.
		List<Map<String, Object>> serverInfos = commonProp.getServerInfos();
		String listenerPath = commonProp.getListenerPath();
		String checkParam = commonProp.getCheckParam();
		String solveParam = commonProp.getSolveParam();
		
		HttpHeaders header = new HttpHeaders();
		// getFirst(String) = 지정된 헤더 이름과 연결된 첫 번째 값을 반환합니다.
		// add(String, String) = 헤더 이름의 값 목록에 헤더 값을 추가합니다.
		// set(String, String) = 헤더 값을 단일 문자열 값으로 설정합니다.
		
		// 헤더가 accept일 경우 어플리케이션 제이슨 밸류 타입을 지원한다.
		header.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		System.out.println("header???" + header.toString());
		//for문을 돌면서 맵 타입의 서버 인포에 리스트 타입의 서버인포스의 값을 한개씩 넣어준다.
		for(Map<String, Object> serverInfo:serverInfos) { 
			String serverName = (String) serverInfo.get("name");
			boolean isNotiWhenTrue = (boolean) serverInfo.get("isNotiWhenTrue");
			boolean stopStatus = (boolean) serverInfo.get("stop");
			String serverUrl = (String) serverInfo.get("serverUrl");
			
			//1.255.144.21:8080 + /idxr/set/kafkaListener + status = Y
			String checkUrl = serverUrl + listenerPath + checkParam;
			//1.255.144.21:8080 + /idxr/set/kafkaListener + use_un = Y
			String solveUrl = serverUrl + listenerPath + solveParam;
			
			// 성공? 하면 
			try {
				//httpHeader값을 추가한다. 매개변수는 URL, method(get,post), requsetEntity entity(headers, or body 널일수있음), responseReturn 값의 유형을 입력한다. 
				ResponseEntity<String> response = restTemplate.exchange(checkUrl, HttpMethod.GET, new HttpEntity(header), String.class);
				int httpStatusCode = response.getStatusCodeValue();
				//리스폰스엔티티가 200을 떨구면
				//오브젝트맵퍼를 생성하고, 
				if( httpStatusCode == 200) {
					ObjectMapper mapper = new ObjectMapper();
				
					Map<String, Object> resultMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
					String resultString = (String) resultMap.get("result"); // OK 
					
					if( "OK".equals(resultString)) {
						boolean status = (boolean) resultMap.get("status"); // true or false
						log.info(serverName + " status: " + status);
						
						if(stopStatus != isNotiWhenTrue) {
							System.out.println("stopStatus ON!");
						}else {
							if( !status || (status && isNotiWhenTrue)) {
								String text = "Listener Status: " + status + ((!status)?"\n재기동: " + solveUrl:"");
								_SLACK_COLOR slackColor = ((!status)?_SLACK_COLOR.warning:_SLACK_COLOR.good);
								this.sendWebhook(serverName, text, slackColor); 												// webhook send
							}
						}
							
						
					}else {
						log.info(serverName + " Server Error (result: " + resultString + ")");
						this.sendWebhook(serverName, "Server Error (result: " + resultString + ")", _SLACK_COLOR.danger);	// webhook send
					}
				}else {
					log.info(serverName + " HTTP ERROR : " + httpStatusCode);
					this.sendWebhook(serverName, "HTTP ERROR : " + httpStatusCode, _SLACK_COLOR.danger);					// webhook send
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.sendWebhook(serverName, "Server Exception!!\n" + e.getMessage() + ")", _SLACK_COLOR.danger);			// webhook send
			}
		}
	}
	
	/**
	 * slack webhook 전송
	 * sssssssssssss
	 * 커스텀 컬러(헥사코드)를 적용 하려면 _SLACK_COLOR 대신 String 타입으로 받아야함.
	 * 
	 * @param serverName
	 * @param text
	 * @param slackColor
	 * @return
	 */
	private boolean sendWebhook(String serverName, String text, _SLACK_COLOR slackColor){
		SlackMessageAttachement _message = new SlackMessageAttachement();

		_message.setTitle("[Listener Notification]");
		_message.setText("[" + serverName + "] " + text);
		_message.setColor(slackColor.name());
		
//		log.info(_message.toString());
		
		return slackNotifier.notify( _message);
	}
	
			
}
