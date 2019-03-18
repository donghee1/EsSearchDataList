package com.oksusu.hdh.service;

import java.util.List;
import java.util.Map;

import com.oksusu.hdh.domain.EsSearchVO;

public interface EsSearchService {

	public List<String> searchIndexList(EsSearchVO vo, String config) throws Exception;

	public List<String> typeListMappings(EsSearchVO vo) throws Exception;

	public Map<String, List<Object>> elSearch(List<String> idkey, List<String> idvalue,
			int searchSize, String sortData, EsSearchVO vo) throws Exception;
	
	public void dataType(EsSearchVO vo) throws Exception;

	

	
}
