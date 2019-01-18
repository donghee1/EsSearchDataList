package com.oksusu.hdh.controller;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.oksusu.hdh.domain.EsSearchVO;
import com.oksusu.hdh.service.EsSearchService;


@Controller
public class EsSearchController {

	@Autowired
	private EsSearchService service;
	
	@GetMapping("/")
	public String MainPage(Model model) {
		
		MappingJackson2JsonView json = new MappingJackson2JsonView();
		
		model.addAttribute(json);
		
		return "/mainPage";
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
		
		//System.out.println("config?" + config);
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
	
	@PostMapping("/startSearch")
	@ResponseBody
	public String startSearch(String index, String type, String id, String[] idkey
			, String[] idvalue, String config, String searchType, String sortType, Integer searchSize)throws Exception{
		//primitive type.
		//System.out.println("searchType" + searchType);
		System.out.println("index" + index);
		System.out.println("size" + searchSize);
		
		List<Map<String, Object>> list = new ArrayList<>();
		String json = null;
		//System.out.println("index" + index);
		//System.out.println("config" + config);
		//System.out.println("dataType" + searchType);
		//System.out.println("sortType" + sortType);
		
		if(index.length() == 0) {
			System.out.println("index Null!! check Error!!");
			return null;
		}else {
			if(searchSize == null || searchSize >= 0) {
				json = service.elSearch(index, type, id, idkey, idvalue
						, config, searchType, searchSize);
			}
		
			
		}
		return json;
	}
	
	
		
	
}
