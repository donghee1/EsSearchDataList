package com.oksusu.hdh.config;

import java.net.InetAddress;

import javax.validation.OverridesAttribute;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;


// 현재 파일만 생성했을 땐 잘 돌아간다. 내가봤을 땐 서버 실행 후 야들이 Es 경로를 잡아준다.
// 
@Configuration
public class EsConfig {
	
	@Value("${spring.data.elasticsearch.cluster-name}")
	private String clusterName;
	
	@Value("${spring.elasticsearch.jest.proxy.host}")
	private String host;
		
	@Value("${spring.elasticsearch.jest.proxy.port}")
	private int port;
	
	@Value("${spring.data.elasticsearch.devCluster-name}")
	private String devClusterName;
	
	@Value("${spring.elasticsearch.jest.proxy.devHost}")
	private String devHost;
	
	@Value("${spring.elasticsearch.jest.proxy.devPort}")
	private int devPort;
	
	@Value("${spring.data.elasticsearch.bmtCluster-name}")
	private String bmtClusterName;
	
	@Value("${spring.elasticsearch.jest.proxy.bmtHost}")
	private String bmtHost;
	
	@Value("${spring.elasticsearch.jest.proxy.bmtPort}")
	private int bmtPort;
	
	
	
	@Bean("client")
	public Client client() throws Exception{
		
		Settings settings = Settings.builder()
				.put("cluster.name", clusterName).build();
		
		return new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
		
	}
	
	@Bean("dev")
	public Client devClient() throws Exception{
		
		Settings settings = Settings.builder()
				.put("cluster.name", devClusterName).build();
		
		return new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(devHost), devPort));
		
	}
	
	@Bean("bmt")
	public Client bmtClient() throws Exception{
		
		Settings settings = Settings.builder()
				.put("cluster.name", bmtClusterName).build();
		
		return new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(bmtHost), bmtPort));
		
	}
//	@Bean("jsonView")
//	public MappingJackson2JsonView jsonView() {
//		System.out.println("JSONVIEW");
//		return new MappingJackson2JsonView();
//	}
	
}
