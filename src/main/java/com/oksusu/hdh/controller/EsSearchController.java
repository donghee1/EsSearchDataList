package com.oksusu.hdh.controller;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.annotation.JsonView;
import com.oksusu.hdh.config.EsConfig;
import com.oksusu.hdh.domain.EsSearchVO;
import com.oksusu.hdh.service.EsSearchService;


@Controller
public class EsSearchController {

	@Resource
	private EsSearchService service;
	
	@Resource
	private MappingJackson2JsonView jsonView;
	
	
	@GetMapping("/")
	public ModelAndView MainPage(Model model) {
		
		ModelAndView mv = new ModelAndView(jsonView);
		
		//mv.addObject(jsonView);
		mv.setView(jsonView);
		mv.setViewName("/mainPage");
		return mv;
	}
	
	
	@GetMapping("/checkServer")
	@ResponseBody
	public List<String> checkServer(String config) throws Exception {

		List<String> result = null;
		String index = null;
		EsSearchVO vo = new EsSearchVO();
		vo.setConfig(config);
		//data type 정의!! 
		service.dataType(vo);
		
		if(config != null) {
			result = service.searchIndexList(index, config);
			
		}else {
			System.out.println("server check Error!!!!!");
			return null;
		}
		
		return result;
	}
	
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
	public List<String> startTypeList(String getIndex, String config)throws Exception{
		
		List<String> typeList = new ArrayList<>();	
		
		typeList = service.typeListMappings(getIndex, config);
	
	
		return typeList;
		
	}
	
	//토탈 히츠 매개변수 타입 변경하기 인티저에서 롱으로. 만약 오류 날 경우 객체화 방법 찾아야 함.
	// 위 문제가 해결되면 ajax 통신으로 어떻게 매개변수 값을 보낼껀지 판단하자.(스트링형으로 보내주고있다 현재...)
	// 
	@PostMapping("/startSearch")
	@ResponseBody
	public Map<String, List<Object>> startSearch(String index, String type, String id, String[] idkey
			, String[] idvalue, String config, String searchType, String sortType, String sortData, Integer searchSize, Integer total)throws Exception{
		
		Map<String, List<Object>> data = new HashMap<>();
		//String json = null;
		
		if(index.length() == 0) {
			System.out.println("index Null!! check Error!!");
			return null;
		}else {
			if(searchSize == null || searchSize >= 0) {
				data = service.elSearch(index, type, id, idkey, idvalue
						, config, searchType, searchSize, total, sortType, sortData);
				
			}
			
			
			
		}
		return data;
	}
	
	
		
	
}
