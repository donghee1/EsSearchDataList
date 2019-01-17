package com.oksusu.hdh.service;


import java.util.List;



public interface EsSearchService {


	public List<String> searchIndexList(String index, String config)throws Exception;
	
	public List<String> typeListMappings(String getIndex, String config) throws Exception;
	
	public String elSearch(String index, String type, String id, String[] idkey, String[] idvalue
			, String config, Integer searchSize)throws Exception;

	public void dataType(String config)throws Exception;

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	public List<Map<String, Object>> onlyOneIndex(String index) throws Exception;
//	
//	public List<Map<String, Object>> SearchIndexAndType(String index, String type)throws Exception;
//
//	public List<Map<String, Object>> SearchkeyAndValue(String index, String type, String[] idkey, String[] idvalue) throws Exception;
//	
//	public List<Map<String, Object>> SearchIndexAndKeyValue(String index, String[] idkey, String[] idvalue) throws Exception;
//
//	public List<Map<String, Object>> multiSearchKeyAndValue(String index, String type, String[] idkey, String[] idvalue)throws Exception;

	//public Object deleteIndex(BoardVO vo)throws Exception;

	//public int upsert(BoardVO vo)throws Exception;

	

	//public List<Object> TypeList()throws Exception;

	

	//public List<Map<String, Object>> fieldMapping(String indexData, String typeData);

	//public List<Map<String, Object>> searchIndexAndType(String indexData, String typeData) throws Exception;

	//public Map<String, Object> documents(String indexData, String typeData, String id) throws Exception;

	

	//public Object indexAndTypeSearch(String index, String type) throws Exception;

	

//
//	public Object insert(BoardVO vo)throws Exception;
//
//	public Object update1(BoardVO vo)throws Exception;
//
//	public int delete(BoardVO vo)throws Exception;
//
//	public Object search(BoardVO vo)throws Exception;
//
//	public Object bulk(BoardVO vo)throws Exception;
//
//	public Object GetOne(BoardVO vo)throws Exception;

	//public int deleteType(BoardVO vo) throws Exception;


	//public List<Map<String, Object>> GetIdex(BoardVO vo)throws Exception;

	
	

	

	
	



	


	
}
