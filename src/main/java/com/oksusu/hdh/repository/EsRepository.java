package com.oksusu.hdh.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.elasticsearch.index.search.MultiMatchQuery.QueryBuilder;
import org.elasticsearch.percolator.QueryAnalyzer;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.SearchSortValues;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder.Order;
import org.elasticsearch.search.internal.FilteredSearchContext;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Repository;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oksusu.hdh.config.EsConfig;
import com.oksusu.hdh.domain.EsSearchVO;

@Repository
public class EsRepository {

	
	private Client client;

	@Resource
	private Client dev;

	@Resource
	private Client bmt;

	@Resource
	private EsConfig cofig; //수정 / 빈 확인

	public Client changeClient(EsSearchVO vo) throws Exception {

		if (vo.getConfig().equals("dev")) {
			client = dev;
		} else if (vo.getConfig().equals("bmt")) {
			client = bmt;
		} 
		return client;
	}

	public List<String> searchIndexList(EsSearchVO vo, String config) throws Exception {

		List<String> list = new ArrayList<>();

		String[] res = client.admin().indices().getIndex(new GetIndexRequest()).actionGet().getIndices();
		list = Arrays.asList(res);
		Collections.sort(list);

		return list;
	}

	public List<String> typeListMappings(EsSearchVO vo) throws Exception {

		List<String> typeList = new ArrayList<>();
		GetMappingsResponse res = null;
		res = client.admin().indices().getMappings(new GetMappingsRequest().indices(vo.getIndex().toString())).get();

		ImmutableOpenMap<String, MappingMetaData> mapping = res.getMappings().get(vo.getIndex());
		 //System.out.println("immutableopenmap" + mapping.toString());
		 //System.out.println("res2222????" + res.toString());
		for (ObjectObjectCursor<String, MappingMetaData> c : mapping) {
			if (c != null) {
				//System.out.println("if문!" +c.toString());
				typeList.add(c.key);
			}
		}

		Collections.sort(typeList);

		return typeList;

	}

	public Map<String, List<Object>> onlyOneIndexSearch(EsSearchVO vo, int searchSize, List<String> idkey,
			List<String> idvalue, String sortData) throws Exception{

		Map<String, List<Object>> oneIndexList = new HashMap<>();
		
		
		
		SearchRequestBuilder req = client.prepareSearch(vo.getIndex()).setFrom(0).setSize(searchSize);
		
		String valueField;
	//	String keyField;
		
		if(idvalue.get(0) != "") {
			for(int i = 0; i < idvalue.size(); i++) {
				valueField = idvalue.get(i);
				if(valueField.indexOf("*") >= 0) {
				req.setQuery(QueryBuilders.matchAllQuery()).setQuery(QueryBuilders.wildcardQuery(valueField, "* ?"))
				.setQuery(QueryBuilders.queryStringQuery(valueField)).setFrom(0).setSize(searchSize);
				}
			}
		}else {
				req.setQuery(QueryBuilders.matchAllQuery()).setFrom(0).setSize(searchSize);
		}
		
		if(!"".equals(vo.getSortType())) {
			for(int i = 0; i < idvalue.size(); i++) {
				valueField = idvalue.get(i);
			}
			if("DESC".equals(vo.getSortType())) {
				req.addSort(sortData, SortOrder.DESC);
			}else if("ASC".equals(vo.getSortType())) {
				req.addSort(sortData, SortOrder.ASC);
			}
			
		}
		
		
		
		SearchResponse onlyIndex = req.get();
		
		int total = (int) onlyIndex.getHits().getTotalHits();
		int totals = onlyIndex.getHits().getHits().length;
		
		Map<String, Object> hitsData = new HashMap<>();
		hitsData.put("totalSearchData", total);
		hitsData.put("searchData", totals);
		
		Map<String, Object> totalHitsData = new HashMap<>();
		totalHitsData.put("datas", hitsData);
		
		List<Object> totalData = new ArrayList<>();
		totalData.add(totalHitsData);
		
		List<Object> mapData = new ArrayList<>();
		for (SearchHit hits : onlyIndex.getHits().getHits()) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("_index", hits.getIndex());
			map.put("_type", hits.getType());
			map.put("_id", hits.getId());
			map.put("_source", hits.getSourceAsMap());
			
			mapData.add(map);
		}
		oneIndexList.put("data", mapData);
		oneIndexList.put("totalData", totalData);
		

		if(oneIndexList.get("data").size() == 0) {
			oneIndexList.put("data", mapData);
		}
		
		return oneIndexList;

	}

	// 인덱스와 타입을 검색하여 결과값을 도출하는 메서드 입니다.
	public Map<String, List<Object>> indexAndTypeSearch(EsSearchVO vo, List<String> idkey, List<String> idvalue,
			int searchSize, String sortData) throws Exception {
		Map<String, List<Object>> dataList = new HashMap<>();
		SearchRequestBuilder req = null;	
		BoolQueryBuilder bool = new BoolQueryBuilder();
			req = client.prepareSearch(vo.getIndex()).setTypes(vo.getType()).setFrom(0).setSize(searchSize);
			String valueField = null;
		if(idvalue.get(0) != "" && idkey.get(0) == "") {
			for(int i = 0; i < idvalue.size(); i++) {
				valueField = idvalue.get(i);
				req.setQuery(QueryBuilders.matchAllQuery()).setQuery(QueryBuilders.queryStringQuery(valueField))
				.setSize(searchSize).setFrom(0);
			}
		}else {
				req.setQuery(QueryBuilders.matchAllQuery()).setFrom(0).setSize(searchSize);
		}
		
			if("DESC".equals(vo.getSortType())) {
				req.addSort(sortData, SortOrder.DESC);	
			}else if("ASC".equals(vo.getSortType())) {
				req.addSort(sortData, SortOrder.ASC);
		}
		
		SearchResponse res = req.get();
		
		Map<String, Object> hitData = new HashMap<>();
		long total = res.getHits().getTotalHits();
		int totals = res.getHits().getHits().length;
		hitData.put("totalSearchData", total);
		
		hitData.put("searchData", totals);
		
		Map<String, Object> totalHitsData = new HashMap<>();
		totalHitsData.put("datas", hitData);
		
		List<Object> totalHits = new ArrayList<>();
		totalHits.add(totalHitsData);
		
		List<Object> mapData = new ArrayList<>();
		for (SearchHit hits : res.getHits().getHits()) {
			Map<String, Object> typeMap = new LinkedHashMap<>();

			typeMap.put("_index", hits.getIndex());
			typeMap.put("_type", hits.getType());
			typeMap.put("_id", hits.getId());
			typeMap.put("_source", hits.getSource());
			
			mapData.add(typeMap);
			
		}
		dataList.put("data", mapData);
		dataList.put("totalData", totalHits);
		

		if(dataList.get("data").size() == 0) {
			dataList.put("data", mapData);
		}
		
		return dataList;
	}

	// documents field 값을 도출하는 (key & Value) 기능입니다.
	public Map<String, List<Object>> keyAndValueSearch(EsSearchVO vo, List<String> idkey, List<String> idvalue,
			int searchSize, String sortData) throws Exception {
		
		Map<String, List<Object>> keyValueList = new HashMap<>();

		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchRequestBuilder srb = null;
		
		if(vo.getIndex() == null) {
			System.out.println("Error!");
			return null;
		}else if (!"".equals(vo.getIndex()) && !"".equals(vo.getType())) {
			srb = client.prepareSearch(vo.getIndex()).setTypes(vo.getType());
		}
		
		String keyField;
		String valueField = null;

		if(!"".equals(vo.getIndex()) || !"".equals(vo.getType())) {
			if("".equals(vo.getSearchType())) {
				for(int i=0; i<idkey.size(); i++) {
					 keyField= idkey.get(i);
					 valueField = idvalue.get(i);
					 
					if(keyField != null) {
						if(valueField.indexOf("*") >= 0) {
							bool.must(QueryBuilders.matchAllQuery())
							.must(QueryBuilders.wildcardQuery(keyField, valueField));
						} else {
							bool.must(QueryBuilders.matchAllQuery())
							.must(QueryBuilders.matchQuery(keyField, valueField));
							
						}
					
					}else if(idkey.get(0) == "") {
						//밸류값만 검색할 때
						bool.must(QueryBuilders.matchAllQuery())
						.must(QueryBuilders.queryStringQuery(valueField))
						.must(QueryBuilders.simpleQueryStringQuery(valueField));
						
					}
						
				}
				
			}else if ("and".equals(vo.getSearchType())) {
				for (int i = 0; i < idkey.size(); i++) {
					 keyField = idkey.get(i);
					 valueField = idvalue.get(i);
					if (keyField != null) {
						if (valueField.indexOf("*") >= 0) {
							bool.must(QueryBuilders.matchAllQuery())
								.must(QueryBuilders.wildcardQuery(keyField, valueField));
						} else {
							bool.must(QueryBuilders.matchAllQuery())
								.must(QueryBuilders.matchQuery(keyField, valueField));

						}
					}else if(idkey.get(0) == "") {
						//밸류값만 검색할 때
						bool.must(QueryBuilders.matchAllQuery())
						.must(QueryBuilders.queryStringQuery(valueField))
						.must(QueryBuilders.simpleQueryStringQuery(valueField));
						
					}
				}
			}else if ("or".equals(vo.getSearchType())) {
				for (int i = 0; i < idkey.size(); i++) {
					 keyField = idkey.get(i);
					 valueField = idvalue.get(i);
					if (keyField != null) {
						if (valueField.indexOf("*") >= 0) {
							bool.must(QueryBuilders.matchAllQuery())
							.should(QueryBuilders.wildcardQuery(keyField, valueField));
						} else {
							bool.should(QueryBuilders.boolQuery()
									.should(QueryBuilders.matchQuery(keyField, valueField)).minimumShouldMatch(1)
									.must(QueryBuilders.matchAllQuery()).should(QueryBuilders.simpleQueryStringQuery(valueField)));
							
							//.should(QueryBuilders.matchQuery(keyField, valueField))
						}
					}else if(idkey.get(i) == "") {
						//밸류값만 검색할 때
						bool.must(QueryBuilders.matchAllQuery())
						.must(QueryBuilders.queryStringQuery(valueField))
						.should(QueryBuilders.simpleQueryStringQuery(valueField));
						
					}
				}

			}
				
		}
		
		srb = client.prepareSearch(vo.getIndex()).setTypes(vo.getType()).setQuery(bool).setFrom(0).setSize(searchSize);
		
		if(!"".equals(vo.getSortType())) {
				if("DESC".equals(vo.getSortType())) {
					srb.addSort(sortData, SortOrder.DESC);
				}else if("ASC".equals(vo.getSortType())) {
					srb.addSort(sortData, SortOrder.ASC);
				}else if("".equals(vo.getSortType())) {
				}
		}

		
		SearchResponse keyAndValueList = srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).get();
		// System.out.println("keyAndValue" + keyAndValue);
		// 검색 결과의 값을 가지고 옴
		
		List<Object> data = new ArrayList<>();
		List<Object> data2 = new ArrayList<>();
		for (SearchHit hit : keyAndValueList.getHits().getHits()) {
			Map<String, Object> map = new LinkedHashMap<>();
			

			map.put("_index", hit.getIndex());
			map.put("_type", hit.getType());
			map.put("_Id", hit.getId());
			map.put("_source", hit.getSourceAsMap());
			
			data.add(map);
		}
		 
		long total = keyAndValueList.getHits().getTotalHits();
		int totals = keyAndValueList.getHits().getHits().length;
		
		Map<String, Object> totalData = new HashMap<>();
		totalData.put("totalSearchData", total);
		totalData.put("searchData", totals);
		
		Map<String, Object> list = new HashMap<>();
		list.put("datas",totalData);
		data2.add(list);
		
		keyValueList.put("data", data);
		keyValueList.put("totalData", data2);

		if(keyValueList.get("data").size() == 0) {
			keyValueList.put("data", data);
		}
		
		return keyValueList;

	}

	public Map<String, List<Object>> indexAndKeyValueSearch(EsSearchVO vo, List<String> idkey
			, List<String> idvalue, int searchSize, String sortData)throws Exception {

		Map<String, List<Object>> data = new HashMap<>();
		List<Object> dataList = new ArrayList<>();
		
		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchRequestBuilder srb = null;

		srb = client.prepareSearch(vo.getIndex());
		
		if(!"".equals(vo.getSortType())) {
			for(int i = 0; i < idvalue.size(); i++) {
				if("DESC".equals(vo.getSortType())) {
					srb.addSort(sortData, SortOrder.DESC);
				}else if("ASC".equals(vo.getSortType())) {
					srb.addSort(sortData, SortOrder.ASC);
				}else if("".equals(vo.getSortType())) {
				}
			}	
		}

		if("".equals(vo.getSearchType())) {
			for(int i=0; i<idkey.size(); i++) {
				String keyField= idkey.get(i);
				String valueField = idvalue.get(i);
				if(keyField != null) {
					if(valueField.indexOf("*") >= 0) {
						bool.must(QueryBuilders.matchAllQuery())
						.must(QueryBuilders.wildcardQuery(keyField, valueField));
					} else {
						bool.must(QueryBuilders.matchAllQuery())
						.must(QueryBuilders.matchQuery(keyField, valueField));
					}
				
				} else if(idkey.get(i) == "") {
						//밸류값만 검색할 때
						bool.must(QueryBuilders.matchAllQuery())
						.must(QueryBuilders.queryStringQuery(valueField))
						.must(QueryBuilders.simpleQueryStringQuery(valueField));
				}
					
			}
			
		}else if ("and".equals(vo.getSearchType())) {
			for (int i = 0; i < idkey.size(); i++) {

				String keyField = idkey.get(i);
				String valueField = idvalue.get(i);
				if (keyField != null) {
					if (valueField.indexOf("*") >= 0) {
						bool.must(QueryBuilders.wildcardQuery(keyField, valueField))
							.must(QueryBuilders.termQuery(keyField, valueField));
					} else {
						bool.must(QueryBuilders.matchAllQuery())
							.must(QueryBuilders.matchQuery(keyField, valueField));

					}
			}
			}
		}else if ("or".equals(vo.getSearchType())) {
			for (int i = 0; i < idkey.size(); i++) {

				String keyField = idkey.get(i);
				String valueField = idvalue.get(i);
				if (keyField != null) {
					if (valueField.indexOf("*") >= 0) {
						bool.should(QueryBuilders.wildcardQuery(keyField, valueField))
							.must(QueryBuilders.termQuery(keyField, valueField));
					} else {
						bool.should(QueryBuilders.matchAllQuery())
						.should(QueryBuilders.matchQuery(keyField, valueField))
						.should(QueryBuilders.rangeQuery(valueField)).minimumShouldMatch(1);
						
						//.should(QueryBuilders.matchQuery(keyField, valueField))
						
					}
				}else if(idkey.get(0) == "") {
					//밸류값만 검색할 때
					bool.must(QueryBuilders.matchAllQuery())
					.must(QueryBuilders.queryStringQuery(valueField))
					.should(QueryBuilders.simpleQueryStringQuery(valueField));
					
				}
			}

		}
		
		SearchResponse res = srb.setQuery(bool).setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFrom(0).setSize(searchSize)
				.get();
		
		List<Object> mapData = new ArrayList<>();
		for (SearchHit hit : res.getHits().getHits()) {
			Map<String, Object> map = new LinkedHashMap<>();
			
			map.put("_index", hit.getIndex());
			map.put("_type", hit.getType());
			map.put("_Id", hit.getId());
			map.put("_source", hit.getSourceAsMap());
			
			mapData.add(map);
		}

		long total = res.getHits().getTotalHits();
		int totals = res.getHits().getHits().length;
		
		Map<String, Object> totalData = new HashMap<>();
		totalData.put("totalSearchData", total);
		totalData.put("searchData", totals);
		
		Map<String, Object> list = new HashMap<>();
		list.put("datas",totalData);
		dataList.add(list);
		
		data.put("data", mapData);
		data.put("totalData", dataList);
		
		
		
		
		if(data.get("data").size() == 0) {
			data.put("data", mapData);
		}
		
		
		return data;
		}

	

	public Map<String, List<Object>> idSearch(EsSearchVO vo, int searchSize) throws Exception {

		Map<String, List<Object>> dataList = new HashMap<>();
		Map<String, Object> map = new LinkedHashMap<>();
		List<Object> mapData = new ArrayList<>();
		BoolQueryBuilder bool = new BoolQueryBuilder();
		GetRequestBuilder req;
		SearchRequestBuilder srb;
		req = client.prepareGet(vo.getIndex(), vo.getType(), vo.getId());
		bool.must(QueryBuilders.matchAllQuery());
		srb = client.prepareSearch(vo.getIndex()).setTypes(vo.getType());
		SearchResponse srs = srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFrom(0).setSize(searchSize).get();
		
		GetResponse res1 = req.get();
		
		Map<String, Object> idData = res1.getSourceAsMap();
		//idData가 널이 아니면 1 널이면 0 값은 무조건 1개
		int totals; // 검색 결과 값
		
		if(idData != null) {
			totals = 1;
			srs = srb.setSize(totals).get();
		}else {
			totals = 0;
			srs = srb.setSize(totals).get();
		}
		long total = srs.getHits().getHits().length; // 전체 데이터의 값
				
		Map<String, Object> totalData = new HashMap<>();
		totalData.put("totalSearchData", total);
		totalData.put("searchData", totals);
		
		Map<String, Object> list = new HashMap<>();
		list.put("datas",totalData);
		
		List<Object> totalHitsData = new ArrayList<>();
		
		totalHitsData.add(list);
		
		map.put("_index", res1.getIndex());
		map.put("_type", res1.getType());
		map.put("_Id", res1.getId());
		map.put("_source", res1.getSourceAsMap());
		
		mapData.add(map);
		
		dataList.put("totalData", totalHitsData);
		dataList.put("data", mapData);
		
		if(dataList.get("data").size() == 0) {
			
			dataList.put("data", mapData);
		}
		
		return dataList;
	}



	
}
