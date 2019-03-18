package com.oksusu.hdh.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.oksusu.hdh.domain.EsSearchVO;
import com.oksusu.hdh.service.EsSearchService;


@Controller
public class EsSearchController {

	@Resource
	private EsSearchService service;
	
	
	@GetMapping("/")
	public ModelAndView MainPage() {
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/mainPage");
		return mv;
	}
	
	
	@GetMapping("/checkServer")
	@ResponseBody
	public List<String> checkServerList(EsSearchVO vo, @RequestParam Map<String, Object> listMap) throws Exception {

		List<String> result = null;
		vo.setConfig((String) listMap.get("config"));
		System.out.println("??" + vo.getConfig());
		
		String config = vo.getConfig();
		
		
		//data type 정의!! 
		service.dataType(vo);
		
		if(vo.getConfig() != null) {
			result = service.searchIndexList(vo, config);
			
		}else {
			System.out.println("server check Error!!!!!");
			return null;
		}
		
		return result;
	}
	
	@PostMapping("/typeList")
	@ResponseBody
	public List<String> startTypeList(EsSearchVO vo, @RequestParam Map<String, Object> listMap)throws Exception{
		
		List<String> typeList = new ArrayList<>();	
		
		vo.setIndex((String) listMap.get("index"));
		System.out.println("???" + vo.getIndex());
		
		vo.setConfig((String) listMap.get("config"));
		
		typeList = service.typeListMappings(vo);
	
	
		return typeList;
		
	}
	
	//토탈 히츠 매개변수 타입 변경하기 인티저에서 롱으로. 만약 오류 날 경우 객체화 방법 찾아야 함.
	// 위 문제가 해결되면 ajax 통신으로 어떻게 매개변수 값을 보낼껀지 판단하자.(스트링형으로 보내주고있다 현재...)
	// 
	@PostMapping("/startSearch")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Map<String, List<Object>> startSearch(@RequestBody Map<String, Object> listMap)throws Exception{
		
		System.out.println("startsearch!!!!");
		EsSearchVO vo = new EsSearchVO();
		
		List<String> idvalue = (List<String>) listMap.get("idvalue");
		
		List<String> idkey = (List<String>) listMap.get("idkey");
		
		String sizeData = (String) listMap.get("searchSize");
		int searchSize = Integer.parseInt(sizeData);
		String sortData = (String) listMap.get("sortData");
		vo.setConfig((String)listMap.get("config"));
		vo.setIndex((String)listMap.get("index"));
		vo.setType((String)listMap.get("type"));
		vo.setId((String)listMap.get("id"));
		vo.setSortType((String)listMap.get("sortType"));
		vo.setSearchType((String) listMap.get("searchType"));
		System.out.println("searchSize??" + searchSize);
		System.out.println("sizeData??" + sizeData);
		System.out.println("sortdata??" + sortData);
		
		System.out.println("???" + vo.toString());
		Map<String, List<Object>> data = new HashMap<>();
		//String json = null;
		
		if(listMap.size() == 0) {
			System.out.println("index Null!! check Error!!");
			return null;
		}else {
			if(searchSize == 0 || searchSize >= 0) {
				data = service.elSearch(idkey, idvalue, searchSize, sortData, vo);
				
			}
		}
		
		System.out.println("???!!" + data.toString());
		
		return data;
	}
	
//	@InitBinder
//	public void initBinder(WebDataBinder binder) {
//	   binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor(null));
//	}
	
	
		
	
}
