package com.oksusu.hdh.boot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oksusu.hdh.boot.config.CommonProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SlackController {
	
	//컴포넌트 내부에서 URL을 요청해야 하는 경우가 있다.
	//스프링에서 http request 요청을 간단히 사용할 수 있도록 지원해주는 기능으로서
	//스프링 안에서 get,post,put,patch,delete REST통신 방식으로 처리하는 기능을 지원해준다.
	//httpheader 와 연관이 있으며 반환을 responseEntity로 받는다.
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
    private CommonProperties commonProp;
	
	
	@RequestMapping(value = "/getProperty", method = RequestMethod.POST)
	public String getProperty(@RequestParam Map<String, String> request) {
		
		String text = request.get("text");
		String responseString = "";
		
		String[] reqProperty = text.split(":", -1);
		
		List<Map<String, Object>> serverInfos = commonProp.getServerInfos();
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		
		String serverName = "";
		String propertiesUrl = "";
		String commandHelp = "/properties [DEV|BMT|PROD]:[propertyName]";
		
		
		try {
			String reqServer = reqProperty[0];
			
			if( reqServer == null || "".equals(reqServer)) {
				 return "Check Your Command\n" + commandHelp;
			}
			
			for(Map<String, Object> serverInfo:serverInfos) {
				serverName = (String) serverInfo.get("name");
				if( serverName.equals(reqServer)) {
					propertiesUrl = (String) serverInfo.get("serverUrl") + commonProp.getPropertiesPath() + commonProp.getPropertiesParam();
				}
			}
			
			log.info("[" + reqServer + "] propertiesUrl: " + propertiesUrl);
			
			if( propertiesUrl == null || "".equals(propertiesUrl)) {
				return "Request Wrong ServerName\n" + commandHelp;
			}else {
				//exchange = httpHeader를 수정할 수 있고 결과를 http ResponseEntity로 반환 받는다.
				//getForObject = 기본 httpHeader를 사용하며 결과를 객체로 반환 받는다.
				//getForEntity = 기본 httpHeader를 사용하며 결과를 http ResponseEntity로 반환 받는다.
				ResponseEntity<String> response = restTemplate.exchange(propertiesUrl, HttpMethod.GET, new HttpEntity(header), String.class);
				System.out.println("response???" + response);
				int httpStatusCode = response.getStatusCodeValue();
				
				if( httpStatusCode == 200) {
					ObjectMapper mapper = new ObjectMapper();
                                                                                         //제네릭 안의 데이터 타입으로 변경해준다.                                    																										
					Map<String, Object> resultMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
					String resultString = (String) resultMap.get("result");
					if( "OK".equals(resultString)) {
						Map<String, String> preoperties = (Map<String, String>) resultMap.get("property");
						System.out.println("프로펕니!!? ::" + preoperties.toString());
						
						if( reqProperty.length > 1) {
							for( int i=1; i<reqProperty.length; i++) {
								String propertyName = reqProperty[i].trim();
								String propertyValue = "존재하지 않는 프로퍼티 입니다.";
								
								if( preoperties.get(propertyName) != null) {
									propertyValue = String.valueOf(preoperties.get(propertyName));
								}
								responseString += propertyName + " : " + propertyValue + "\n";	
							}
						}else {
							responseString = preoperties.toString();
						}
					}else {
						log.info(serverName + " Server Error (result: " + resultString + ")");
					}
				}else {
					log.info(serverName + " HTTP ERROR : " + httpStatusCode);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return responseString;
	}
}
