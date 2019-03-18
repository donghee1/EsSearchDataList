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
		public List<String> searchIndexList(EsSearchVO vo, String config) {
			
			List<String> result = null;
			try {
				
				result = repository.searchIndexList(vo, config);
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("searchIndex 오류!!!");
			}
			
			return result;

		}
		
		@Override
		public List<String> typeListMappings(EsSearchVO vo) throws Exception {
			
			List<String> typeList = new ArrayList<>();
			
			if(vo.getIndex() == null || vo.getConfig() == null) {
				System.out.println("typeList Search Error!!!");
				return null;
			}
			
			typeList = repository.typeListMappings(vo);
			
			return typeList;
		
		}
		
		@Override
		public void dataType(EsSearchVO vo) throws Exception {
			
			Client data = repository.changeClient(vo);
			
		}

		@Override
		public Map<String, List<Object>> elSearch(List<String> idkey, List<String> idvalue,
				int searchSize, String sortData, EsSearchVO vo) throws Exception {
			Map<String, List<Object>> mapList = new HashMap<>();

			System.out.println("serviceimpl" + vo.toString());
			
			if(searchSize == 0) {
				searchSize = 10;
			}
			//String json = null ;
				if ("".equals(vo.getType()) && "".equals(idkey.get(0))) {
					System.out.println("onlyone!!!!");
					mapList = repository.onlyOneIndexSearch(vo, searchSize, idkey, idvalue, sortData);
				
				}else if( !"".equals(vo.getType()) && "".equals(vo.getId()) && "".equals(idkey.get(0))) {
					System.out.println("typeSearch!!!");
					mapList = repository.indexAndTypeSearch(vo, idkey, idvalue, searchSize,sortData);
				
				}else if (!"".equals(vo.getType()) && !"".equals(vo.getId()) && "".equals(idkey.get(0)) && "".equals(idvalue.get(0)) ) {
					System.out.println("아이디값이 왜 안될까요?");
					mapList = repository.idSearch(vo, searchSize);
					//json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
				
				}else if( "".equals(vo.getType()) && idkey.get(0) != "" && idvalue.get(0) != "") {
					mapList = repository.indexAndKeyValueSearch(vo, idkey, idvalue, searchSize, sortData);
					//json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
					//keyValue 한개 검색 가능 or 한개이상 검색 가능!
				
				}else if( idkey.get(0) != "" && idvalue.get(0) != ""  && !"".equals(vo.getType()) && "".equals(vo.getId())) {
					mapList = repository.keyAndValueSearch(vo, idkey, idvalue, searchSize,sortData);
					//json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
				
				}else {
					return null;
				}

				
			return mapList;
		}


		
		

}
