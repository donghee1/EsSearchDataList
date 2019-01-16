package com.oksusu.hdh.service;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.Client;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oksusu.hdh.domain.EsSearchVO;
import com.oksusu.hdh.domain.EsTest;
import com.oksusu.hdh.mapper.BoardMapper;
import com.oksusu.hdh.repository.EsRepository;

@Service
public  class EsSearchServiceImpl implements EsSearchService {

		@Autowired
		EsRepository repository;

		@Autowired
		BoardMapper mapper;
		
		
		@Override
		public List<String> searchIndexList(String index, String config) {
			
			System.out.println("Start search Project!");
			
			List<String> result = null;
			try {
				
				result = repository.searchIndexList(index, config);
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("searchIndex 오류!!!");
			}
			
			return result;

		}
		
		
		@Override
		public List<String> typeListMappings(EsTest vo, String config) throws Exception {
			
			
			List<String> typeList = new ArrayList<>();
			
			if(vo == null || config == null) {
				System.out.println("typeList Search Error!!!");
				return null;
			}
			
			typeList = repository.typeListMappings(vo, config);
			
			return typeList;
		
		}
		@Override
		public String elSearch(String index, String type, String id, String[] idkey,
				String[] idvalue, String config, String searchType, String sortType) throws Exception {
			
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> list = new ArrayList<>();
			List<Map<String, Object>> idList = new ArrayList<>();
			
			String json ;
				if( "".equals(type) && idkey.length == 0 && idvalue.length == 0 ) {
					System.out.println("indexSearch Start!!!!!");
					list = repository.onlyOneIndexSearch(index, config);
					json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
					System.out.println("json??" + json.toString());
				}else if( type != null && "".equals(id) && idkey.length == 0 && idvalue.length == 0 && searchType.length() == 0 && sortType.length() == 0) {
					System.out.println("typeSearch Start!!!!!");
					 list = repository.indexAndTypeSearch(index, type, config);
					json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
				}else if (!"".equals(type) && !"".equals(id) && idkey.length == 0 && idvalue.length == 0 && searchType.length() == 0 && sortType.length() == 0) {
					System.out.println("idSearch Start!!!!!");
					idList = repository.idSearch(index,type,id, config);
					json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(idList);
				}else if( "".equals(type) && idkey.length > 0 && idvalue.length > 0 && searchType.length() > 0 && sortType.length() > 0 ) {
					System.out.println("typeOut SearchStart!!!");
					list = repository.indexAndKeyValueSearch(index, idkey, idvalue, config, searchType, sortType);
					json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
					//keyValue 한개 검색 가능 or 한개이상 검색 가능!
				}else if( idkey.length > 0 && idvalue.length > 0  && !"".equals(type) && searchType.length() > 0 && sortType.length() > 0) {
					System.out.println("allSearch Start!!!!!");
					list = repository.keyAndVlaueSearch(index, type, idkey, idvalue, config, searchType, sortType);
					json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
				}else {
					System.out.println("No Searxh!!!!");
					return null;
				}
				
			return json;
		}


		@Override
		public Client dataType(String config) throws Exception {
			
			Client data = repository.changeClient(config);
			
			return data;
		}


		
		

}
