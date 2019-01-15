package com.oksusu.hdh.controller;



import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oksusu.hdh.domain.EsTest;
import com.oksusu.hdh.service.EsSearchService;
import com.oksusu.hdh.service.EsTestService;



@Controller
public class EsSearchController {

	@Autowired
	private EsSearchService service;
	
	@Autowired
	private EsTestService testService;
	
	

	@GetMapping("/mainPage")
	public String MainPage(Model model) {
		
		MappingJackson2JsonView json = new MappingJackson2JsonView();
		
		model.addAttribute(json);
		
		System.out.println("mv?" + model);
		
		return "mainPage";
	}
	
	
	@GetMapping("/checkServer")
	@ResponseBody
	public List<String> checkServer(String config) throws Exception {

		List<String> result = null;
		String index = null;
		
		System.out.println("config?" + config);
		if(config != null) {
			result = service.searchIndexList(index, config);
		}else {
			System.out.println("server check Error!!!!!");
			return null;
		}
		
		return result;
	}
	
	// 첫 웹 페이지 화면!!! -> index List를 뽑아 줌.
	@GetMapping("/list")
	public List<String> startIndexList(String index, String config)throws Exception {
	
		//***********연구필요!
		//MappingJackson2JsonView mv = new MappingJackson2JsonView();

		List<String> list = new ArrayList<>();
		
		list = service.searchIndexList(index, config);
		
		//System.out.println("final dataList!!!" + list.toString());
		
		
		
		
		return list;
	}
	
	
	@PostMapping("/typeList")
	@ResponseBody
	public List<String> startTypeList(String getIndex, EsTest vo, String config)throws Exception{
		
		System.out.println("typeList Start!");
		
		vo.setIndex(getIndex); 
		
		List<String> typeList = new ArrayList<>();	
		//서버 정보를 가지고 옴 체크서버에서 가지고 온게 아니면 의미가 없으니 지워둠.
		
		typeList = service.typeListMappings(vo, config);
	
		//System.out.println("typeList" + typeList);
		

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
//		System.out.println("type ::: " + type);
//		System.out.println("id ::: " + id);
//		System.out.println("idkey ::: " + idkey);
//		System.out.println("idvalue ::: " + idvalue);
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
