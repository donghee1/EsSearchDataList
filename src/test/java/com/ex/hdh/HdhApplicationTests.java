package com.ex.hdh;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.oksusu.hdh.mapper.BoardMapper;
import com.oksusu.hdh.repository.EsRepository;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class HdhApplicationTests {

	@Autowired
	private BoardMapper mapper;
	
	@Autowired
	EsRepository es;
	
	//private SqlSessionFactory factory;
	
	//private DataSource ds;
	
	@Test
	public void contextLoads() {
	}

	//인서트는 db에 저장만 하면되므로 void를 선언한다(return X)
	@Test
	public void testMapper() throws Exception{
		
		
		
		JSONObject jon = new JSONObject();
		
		jon.put("data", "parse");
		jon.put("torrent", "false");
		
		//false 값만 나온다. 키값이 동일해서 덮어 썼다고 생각한다.
		System.out.println("json 테스트>>>>>>>>>>>>"+jon);
		
		JSONObject jon1 = new JSONObject();
		
		jon1.put("result", jon);
		
		System.out.println("jon1 테스트>>>>>>>>>>>>>>"+jon1);
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("map", "sucess");
		//map.put("jon", jon.get(jon));
		map.put("map2", jon1);
		//map.put("map2", jon1.get("result"));
		
		System.out.println("map은?   " +map);
		
		
		
		
		
	}
	
}
