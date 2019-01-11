package com.oksusu.hdh.utility;

import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

// 현재 파일만 생성했을 땐 잘 돌아간다. 내가봤을 땐 서버 실행 후 야들이 Es 경로를 잡아준다.
// 

//@Configuration
//public class BmtEsConfig {
//	
//	private String bmtClusterName = "els-cluster";
//	
//	private String bmtHost = "1.255.98.108";
//	
//	@Value("${spring.elasticsearch.jest.proxy.port}")
//	private int port;
//	
//	@Bean
//	public Client client() throws Exception{
//		
//		Settings settings = Settings.builder()
//				.put("cluster.name", bmtClusterName).build();
//		
//		System.out.println("start!! bmtServer!!!!!!");
//		return new PreBuiltTransportClient(settings)
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(bmtHost), port));
//		
//	}
//	
//}
