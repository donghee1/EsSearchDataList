package com.oksusu.hdh.service;

import java.util.List;

import com.oksusu.hdh.domain.EsSearchVO;

public interface EsSearchService {

	public List<String> searchIndexList(String index, String config) throws Exception;

	public List<String> typeListMappings(String getIndex, String config) throws Exception;

	public String elSearch(String index, String type, String id, String[] idkey, String[] idvalue, String config,
			String searchType, Integer searchSize) throws Exception;

	public void dataType(EsSearchVO vo) throws Exception;

}
