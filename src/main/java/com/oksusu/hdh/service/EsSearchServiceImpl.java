package com.oksusu.hdh.service;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.elasticsearch.client.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oksusu.hdh.domain.EsSearchVO;
import com.oksusu.hdh.repository.EsRepository;

@Service
public  class EsSearchServiceImpl implements EsSearchService {

		@Autowired
		EsRepository repository;

		
		@Override
		public List<String> searchIndexList(String index, String config) {
			
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
		public List<String> typeListMappings(String getIndex, String config) throws Exception {
			
			List<String> typeList = new ArrayList<>();
			
			if(getIndex == null || config == null) {
				System.out.println("typeList Search Error!!!");
				return null;
			}
			
			typeList = repository.typeListMappings(getIndex, config);
			
			return typeList;
		
		}
		@Override
		public Map<String, List<Object>> elSearch(String index, String type, String id, String[] idkey,
				String[] idvalue, String config, String searchType, Integer searchSize, Integer total, String sortType, String sortData) throws Exception {
			
			ObjectMapper mapper = new ObjectMapper();
			Map<String, List<Object>> mapList = new HashMap<>();
			

			if(searchSize == 0) {
				searchSize = 10;
			}
			//String json = null ;
				if ("".equals(type) && idkey.length == 0 && idvalue.length >= 0) {
					System.out.println("onlyone");
					mapList = repository.onlyOneIndexSearch(index, type, config, searchSize, total, idkey, idvalue, sortType, sortData);
				
				}else if( !"".equals(type) && "".equals(id) && idkey.length == 0 && idvalue.length >= 0) {
					System.out.println("typesearch");
					mapList = repository.indexAndTypeSearch(index, idkey, idvalue, type, config, searchSize, total, sortType,sortData);
					//json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
				
				}else if (!"".equals(type) && !"".equals(id) && idkey.length == 0 && idvalue.length == 0 ) {
					System.out.println("idsearch");
					mapList = repository.idSearch(index,type,id, config, total);
					//json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
				
				}else if( "".equals(type) && idkey[0] != "" && idvalue[0] != "") {
					System.out.println("인덱스 키밸류!!! ");
					mapList = repository.indexAndKeyValueSearch(index, idkey, idvalue, config, searchType, searchSize, total, sortType,sortData);
					//json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
					//keyValue 한개 검색 가능 or 한개이상 검색 가능!
				
				}else if( idkey[0] != "" && idvalue[0] != ""  && !"".equals(type) && "".equals(id)) {
					System.out.println("키앤드밸류서치!!!");
					mapList = repository.keyAndValueSearch(index, type, idkey, idvalue, config, searchType, searchSize, total, sortType,sortData);
					//json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
				
				}else {
					return null;
				}

				
			return mapList;
		}
		
		

		@Override
		public void dataType(EsSearchVO vo) throws Exception {
			
			Client data = repository.changeClient(vo);
			
		}


		
		

}
