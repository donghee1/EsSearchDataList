package com.oksusu.hdh.utility;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.support.RequestContext;

import com.oksusu.hdh.config.DevEsConfig;
import com.oksusu.hdh.repository.EsRepository;

@Configuration
public class Utility {
	
	@Autowired
	EsRepository repository;
	
	public static Client serverChoice(String config) throws Exception{
		
		System.out.println("start Util!!!!" + config.toString());
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object>map = new HashMap<>();
		HttpServletRequest req = ((ServletRequestAttributes) 
				RequestContextHolder.getRequestAttributes()).getRequest();
		
		HttpSession session = req.getSession();
		
		ServletContext context = session.getServletContext();
		
		WebApplicationContext wContext = WebApplicationContextUtils
				.getWebApplicationContext(context);
		System.out.println("wContext data :::"+wContext);
		Client devBean = null;
		
		
		if("dev".equals(config)) {
			System.out.println("this point dev Util!!!!!!!");
			devBean = (Client) wContext.getBean("dev");	
			map.put("devserver", devBean);
			list.add(map);

			
			System.out.println("devBean data ::: " + list.toString());
			
		}else if("bmt".equals(config)) {
			return null;	
		}
		
		
		return devBean;
	}
	
}
