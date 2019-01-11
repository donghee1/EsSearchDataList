package com.oksusu.hdh.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.oksusu.hdh.config.DefaultConfig;
import com.oksusu.hdh.config.BmtEsConfig;
import com.oksusu.hdh.config.DevEsConfig;
import com.oksusu.hdh.domain.EsTest;
import com.oksusu.hdh.repository.EsRepository;
import com.oksusu.hdh.service.EsSearchService;
import com.oksusu.hdh.service.EsTestService;
import com.oksusu.hdh.utility.Utility;

@Controller
public class EsSearchController {

	@Autowired
	private EsSearchService service;
	
	@Autowired
	private EsTestService testService;
	
	@Autowired
	private BmtEsConfig bmtconfig;
	
	@Autowired
	private DevEsConfig devconfig;
	
	@Autowired
	private Utility util;
	
	@Autowired
	private EsRepository esRepository;
	
	private static String config = new String();
	
	
	@GetMapping("/checkServer")
	@ResponseBody
	public List<String> checkServer(String config, HttpSession session) throws Exception {
		
		session.setAttribute("config", config); 
		System.out.println("??????????????????????????/" + config);
		// 스프링부트는 어노테이션등록으로 빈생성이 가능하다. 순서의 문제!
		// 스프링부트는 패키지 순서대로 빈을 생성한다. 순서를 바꿔야할 땐 어떡해야 할까?
		
		System.out.println("Start checkServer!!!!!");

		System.out.println("serverData::::" + config);
		List<String> result = null;
		String index = null;
		
		if(config != null) {
			result = service.searchIndexList(index, config);
		}else {
			System.out.println("server check Error!!!!!");
			return null;
		}
		
		System.out.println("result ::::" + result.toString());
		
		return result;
	}
	
	// 첫 웹 페이지 화면!!! -> index List를 뽑아 줌.
	@GetMapping("/list")
	public String startIndexList(Model model, String index, String config)throws Exception {
	
		List<String> list = new ArrayList<>();
		
		list = service.searchIndexList(index, config);
		
		System.out.println("final dataList!!!" + list.toString());
		
		model.addAttribute("data", list);
		
		return "list";
	}
	
	
	@PostMapping("/typeList")
	@ResponseBody
	public List<String> startTypeList(String getIndex, EsTest vo, String config)throws Exception{
		
		System.out.println("typeList Start!");
		
		System.out.println("index?" + getIndex); 
		
		vo.setIndex(getIndex); 
		
		List<String> typeList = new ArrayList<>();	
		//서버 정보를 가지고 옴 체크서버에서 가지고 온게 아니면 의미가 없으니 지워둔디.
		// 초기값이 왜 dev로 나오는지 모르겠음.
		
		
		
		
		System.out.println("config ::::" + config);
	
		// 설정값이 없는데 dev 데이터가 나온다.
		System.out.println("타입리스트의 컨피그 데이터는????" +config);
		typeList = service.typeListMappings(vo, config);
	
		System.out.println("controller typeList" + typeList);
		
		System.out.println("typeList" + typeList);
		

		return typeList;
		
	}
	
	@PostMapping("/startSearch")
	@ResponseBody
	public String startSearch(String index, String type, String id, String[] idkey , String[] idvalue, String config)throws Exception{
		
		List<Map<String, Object>> list = new ArrayList<>();
		String json = null;
	
		
		if(index == null) {
			System.out.println("index Null!! check Error!!");
			return null;
		}else {
		
			json = service.elSearch(index, type, id, idkey, idvalue, config);	
		}
		System.out.println("type ::: " + type);
		System.out.println("id ::: " + id);
		System.out.println("idkey ::: " + idkey);
		System.out.println("idvalue ::: " + idvalue);
		return json;
	}
	
	
	@GetMapping("/board")
	public Map<String, Object> boardList(EsTest vo)throws Exception{
		
			
		
		System.out.println("값이 들어 갔나요!? ");
		
		Map<String, Object> result = new HashMap<String, Object>();
		System.out.println("값이 들어 갔나요!?2222 ");
		
		
		result.put("elasticsearch", testService.GetList(vo));
		System.out.println("result : " + result);
		
		
		return result;
	}
		
	
}
