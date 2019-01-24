package com.oksusu.hdh.service;

import java.util.List;
import java.util.Map;

import com.oksusu.hdh.domain.EsSearchVO;

public interface EsSearchService {

	public List<String> searchIndexList(String index, String config) throws Exception;

	public List<String> typeListMappings(String getIndex, String config) throws Exception;

	public Map<String, List<Object>> elSearch(String index, String type, String id, String[] idkey, String[] idvalue, String config,
			String searchType, Integer searchSize, Integer total) throws Exception;

	public void dataType(EsSearchVO vo) throws Exception;

}
