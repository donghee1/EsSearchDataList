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
		
		//슬렉에서 데이터를 내려주나? 
		String text = request.get("text");
		System.out.println("text???" + text.toString());
		String responseString = "";
		
		// ex) BMT:tvingApiUrl:apiCmsServerSvcUseYn
		// 데이터 문자열을 잘라준다 (:) 
		String[] reqProperty = text.split(":", -1);
		System.out.println("reqProperty"+reqProperty[0]);
		
		//server infos는 DEV, BMT, PROD(상용)을 묶음을 뜻함 개별은 name으로 사용.
		List<Map<String, Object>> serverInfos = commonProp.getServerInfos();
		
		//확인
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
			
			//serverinfos의 데이터를 맵의 serverinfo에 반복해서 넣어준다.
			//servername은 맵형태의 serverinfo 에 get을 사용하여 가지고온다. 데이터는 스트링형
			// Q : 실제로 get으로 "name"값을 가지고 왔을 때 app.yml의 name값을 가지고 오는지 확인해보자.
			for(Map<String, Object> serverInfo:serverInfos) {
				serverName = (String) serverInfo.get("name");
				//servername과 reqServer의 값을 비교한다. 두값이 일치한다면 두값의 serverUrl을 변수에 담아준다.
				//serverUrl + 명령어? propertiesPath + 명령어? propertiesParam
				// -> http://1.255.144.21:8080/property?m=getPropertyList 가 된다! 
				if( serverName.equals(reqServer)) {
					propertiesUrl = (String) serverInfo.get("serverUrl") + commonProp.getPropertiesPath() + commonProp.getPropertiesParam();
				}
			}
			
			log.info("[" + reqServer + "] propertiesUrl: " + propertiesUrl);
			
			//위 로직에 propertiesUrl의 값이 널이거나 빈값이면 리턴으로 오류메세지 및 사용 설명서URL을 보내준다.
			if( propertiesUrl == null || "".equals(propertiesUrl)) {
				return "Request Wrong ServerName\n" + commandHelp;
			}else {
				//exchange = httpHeader를 수정할 수 있고 결과를 http ResponseEntity로 반환 받는다.
				//getForObject = 기본 httpHeader를 사용하며 결과를 객체로 반환 받는다.
				//getForEntity = 기본 httpHeader를 사용하며 결과를 http ResponseEntity로 반환 받는다.
				ResponseEntity<String> response = restTemplate.exchange(propertiesUrl, HttpMethod.GET, new HttpEntity(header), String.class);
				int httpStatusCode = response.getStatusCodeValue();
				
				//값이 성공할 경우
				if( httpStatusCode == 200) {
					ObjectMapper mapper = new ObjectMapper();
                                                                                         //제네릭 안의 데이터 타입으로 변경해준다.                                    																										
					Map<String, Object> resultMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
					String resultString = (String) resultMap.get("result");
					
					if( "OK".equals(resultString)) {
						Map<String, String> preoperties = (Map<String, String>) resultMap.get("property");
						
						//프로퍼티 값의 길이가 한개 이상일 경우
						if( reqProperty.length > 1) {
							// property 항목이 있으면 해당 항목 모두 출력
							for( int i=1; i<reqProperty.length; i++) {
								// 네입값을짤라준다? 
								String propertyName = reqProperty[i].trim();
								
								String propertyValue = "존재하지 않는 프로퍼티 입니다.";
								
								if( preoperties.get(propertyName) != null) {
									// boolean 으로 들어있는 항목 때문에 String.valueOf 사용
									propertyValue = String.valueOf(preoperties.get(propertyName));
								}
								responseString += propertyName + " : " + propertyValue + "\n";	
							}
						}else {
							// property 항목이 없으면 전체 출력
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
