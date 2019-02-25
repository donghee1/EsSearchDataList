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
	private EsConfig cofig;

	public Client changeClient(EsSearchVO vo) throws Exception {

		if (vo.getConfig().equals("dev")) {
			client = dev;
		} else if (vo.getConfig().equals("bmt")) {
			client = bmt;
		} 
		return client;
	}

	public List<String> searchIndexList(String index, String config) throws Exception {

		List<String> list = new ArrayList<>();

		String[] res = client.admin().indices().getIndex(new GetIndexRequest()).actionGet().getIndices();
		list = Arrays.asList(res);
		Collections.sort(list);

		return list;
	}

	public List<String> typeListMappings(String getIndex, String config) throws Exception {

		List<String> typeList = new ArrayList<>();
		GetMappingsResponse res = null;
		res = client.admin().indices().getMappings(new GetMappingsRequest().indices(getIndex.toString())).get();

		ImmutableOpenMap<String, MappingMetaData> mapping = res.getMappings().get(getIndex);
		// System.out.println("immutableopenmap" + mapping.toString());
		for (ObjectObjectCursor<String, MappingMetaData> c : mapping) {
			if (c != null) {
				typeList.add(c.key);
			}
		}

		Collections.sort(typeList);

		return typeList;

	}

	public Map<String, List<Object>> onlyOneIndexSearch(String index, String type, String config, Integer searchSize, Integer total, String[] idkey, String[] idvalue, String sortType, String sortData) {

	Map<String, List<Object>> mapList = new HashMap<>();
	
		System.out.println("onlyindex!!! start");
	
		
		SearchRequestBuilder req = client.prepareSearch(index).setFrom(0).setSize(searchSize);
		
		String valueField;
		
		if(idvalue.length > 0) {
			for(int i = 0; i < idvalue.length; i++) {
				valueField = idvalue[i];
				if(valueField != null) {
						req.setQuery(QueryBuilders.moreLikeThisQuery(idvalue))
								.setQuery(QueryBuilders.queryStringQuery(valueField));	
				
				}	
			}	
		}
		
		if(!"".equals(sortType)) {
			for(int i = 0; i < idvalue.length; i++) {
				valueField = idvalue[i];
			}
			if("DESC".equals(sortType)) {
				req.addSort(sortData, SortOrder.DESC);
			}else if("ASC".equals(sortType)) {
				req.addSort(sortData, SortOrder.ASC);
			}
			
		}
		
		SearchResponse onlyIndex = req.get();
		
		total = (int) onlyIndex.getHits().getTotalHits();
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
		mapList.put("data", mapData);
		mapList.put("totalData", totalData);
		
		return mapList;

	}

	// 인덱스와 타입을 검색하여 결과값을 도출하는 메서드 입니다.
	public Map<String, List<Object>> indexAndTypeSearch(String index, String[] idkey, String[] idvalue, String type, String config, Integer searchSize, Integer total, String sortType, String sortData)
			throws Exception {

		Map<String, List<Object>> list = new HashMap<>();
		SearchRequestBuilder req = null;

			System.out.println("======================");
			System.out.println(index);
			System.out.println(type);
			System.out.println(searchSize);
			System.out.println(sortType);
			System.out.println(sortData);
			
			req = client.prepareSearch(index).setTypes(type).setFrom(0).setSize(searchSize);
			String valueField;
		if(idkey.length == 0) {
			for(int i = 0; i < idvalue.length; i++) {
				valueField = idvalue[i];
				req.setQuery(QueryBuilders.moreLikeThisQuery(idvalue))
				.setQuery(QueryBuilders.queryStringQuery(valueField)).setFrom(0).setSize(searchSize);
				
			}
		}
		
		System.out.println("sortType" + sortType);
		if(!"".equals(sortType)){
			System.out.println("sort Start!!!");
			for(int i = 0; i < idvalue.length; i++) {
				valueField = idvalue[i];
		}
			if("DESC".equals(sortType)) {
				req.addSort(sortData, SortOrder.DESC);	
			}else if("ASC".equals(sortType)) {
				req.addSort(sortData, SortOrder.ASC);
		}
	}
		
		SearchResponse res = req.get();
		
		Map<String, Object> hitData = new HashMap<>();
		total = (int) res.getHits().getTotalHits();
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
		list.put("data", mapData);
		list.put("totalData", totalHits);
		return list;
	}

	// documents field 값을 도출하는 (key & Value) 기능입니다.
	public Map<String, List<Object>> keyAndValueSearch(String index, String type, String[] idkey, String[] idvalue,
			String config, String searchType, Integer searchSize, Integer total, String sortType, String sortData) {
		
		
		Map<String, List<Object>> keyValue = new HashMap<>();

		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchRequestBuilder srb = null;
		
		if(index == null) {
			System.out.println("Error!");
			return null;
		}else if (index != null && type != null) {
			srb = client.prepareSearch(index).setTypes(type);	
		}
		
				
		
		String keyField;
		String valueField;

		System.out.println("start keyandvalue");
		
		if(!"".equals(index) || !"".equals(type)) {
			if("".equals(searchType)) {
				System.out.println("searchType defualt!!!");
				for(int i=0; i<idkey.length; i++) {
					 keyField= idkey[i];
					 valueField = idvalue[i];
					if(keyField != null) {
						if(valueField.indexOf("*") >= 0) {
							bool.must(QueryBuilders.matchAllQuery())
							.must(QueryBuilders.wildcardQuery(keyField, valueField));
						} else {
							System.out.println("defualt data search!!");
							bool.must(QueryBuilders.matchAllQuery())
							.must(QueryBuilders.matchQuery(keyField, valueField));
						}
					
					}else if(idkey[0] == "") {
						//밸류값만 검색할 때
						System.out.println("밸류값만 검색하자!!!");
						bool.must(QueryBuilders.matchAllQuery())
						.should(QueryBuilders.moreLikeThisQuery(idvalue)).must(QueryBuilders.queryStringQuery(valueField));
						
					}
						
				}
				
			}else if ("and".equals(searchType)) {
				System.out.println("searchType AND!!!");
				for (int i = 0; i < idkey.length; i++) {

					 keyField = idkey[i];
					 valueField = idvalue[i];
					if (keyField != null) {
						if (valueField.indexOf("*") >= 0) {
							bool.must(QueryBuilders.matchAllQuery())
								.must(QueryBuilders.wildcardQuery(keyField, valueField));
						} else {
							bool.must(QueryBuilders.matchAllQuery())
								.must(QueryBuilders.matchQuery(keyField, valueField));

						}
					}else if(idkey[0] == "") {
						//밸류값만 검색할 때
						System.out.println("밸류값만 검색하자!!!");
						bool.must(QueryBuilders.matchAllQuery())
						.should(QueryBuilders.moreLikeThisQuery(idvalue)).must(QueryBuilders.queryStringQuery(valueField));
						
					}
				}
			}else if ("or".equals(searchType)) {
				System.out.println("searchType OR!!!");
				for (int i = 0; i < idkey.length; i++) {

					 keyField = idkey[i];
					 valueField = idvalue[i];
					if (keyField != null) {
						if (valueField.indexOf("*") >= 0) {
							bool.must(QueryBuilders.matchAllQuery())
							.should(QueryBuilders.wildcardQuery(keyField, valueField));
						} else {
							bool.should(QueryBuilders.boolQuery()
									.should(QueryBuilders.matchQuery(keyField, valueField)).minimumShouldMatch(1)
									.must(QueryBuilders.matchAllQuery()));
							
							//.should(QueryBuilders.matchQuery(keyField, valueField))
						}
					}else if(idkey[0] == "") {
						//밸류값만 검색할 때
						System.out.println("밸류값만 검색하자!!!");
						bool.must(QueryBuilders.matchAllQuery())
						.should(QueryBuilders.moreLikeThisQuery(idvalue)).must(QueryBuilders.queryStringQuery(valueField));
						
					}
				}

			}
				
		}
			
			srb.setQuery(bool);
			
			if(!"".equals(searchType)) {
			
				if(!"".equals(sortType)) {
					for(int i = 0; i < idvalue.length; i++) {
						valueField = idvalue[i];
					}
					System.out.println("start Sort!");
						if("DESC".equals(sortType)) {
							System.out.println("DESC!!!!!");
							srb.addSort(sortData, SortOrder.DESC);
						}else if("ASC".equals(sortType)) {
							System.out.println("ASC");
							srb.addSort(sortData, SortOrder.ASC);
						}else if("".equals(sortType)) {
							System.out.println("next!!!");
					}	
				}
			}
			
			srb = client.prepareSearch(index).setTypes(type).setFrom(0).setSize(searchSize);
		
		SearchResponse keyAndValue = srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).get();
		
		// System.out.println("keyAndValue" + keyAndValue);
		// 검색 결과의 값을 가지고 옴
		
		List<Object> data = new ArrayList<>();
		List<Object> data2 = new ArrayList<>();
		for (SearchHit hit : keyAndValue.getHits().getHits()) {
			Map<String, Object> map = new LinkedHashMap<>();
			
			map.put("_index", hit.getIndex());
			map.put("_type", hit.getType());
			map.put("_Id", hit.getId());
			map.put("_source", hit.getSourceAsMap());
			
			data.add(map);
		}
		 
		total = (int) keyAndValue.getHits().getTotalHits();
		int totals = keyAndValue.getHits().getHits().length;
		System.out.println("totals"+ totals) ;
		
		Map<String, Object> totalData = new HashMap<>();
		totalData.put("totalSearchData", total);
		totalData.put("searchData", totals);
		
		Map<String, Object> list = new HashMap<>();
		list.put("datas",totalData);
		data2.add(list);
		
		keyValue.put("data", data);
		keyValue.put("totalData", data2);
		
		
		System.out.println("End keyAndValue Search!!!" + keyValue.toString());
		
		return keyValue;

	}

	public Map<String, List<Object>> indexAndKeyValueSearch(String index, String[] idkey, String[] idvalue,
			String config, String searchType, Integer searchSize, Integer total, String sortType, String sortData) {

		Map<String, List<Object>> data = new HashMap<>();
		List<Object> dataList = new ArrayList<>();
		
		System.out.println("indexAndKeyValueSearch");
		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchRequestBuilder srb = null;

		srb = client.prepareSearch(index);
		
		if(!"".equals(sortType)) {
			System.out.println("start Sort!");
			for(int i = 0; i < idvalue.length; i++) {
				if("DESC".equals(sortType)) {
					srb.addSort(sortData, SortOrder.DESC);
				}else if("ASC".equals(sortType)) {
					srb.addSort(sortData, SortOrder.ASC);
				}else if("".equals(sortType)) {
					System.out.println("next!!!");
				}
			}	
		}

		if("".equals(searchType)) {
			System.out.println("repository22222222");
			for(int i=0; i<idkey.length; i++) {
				String keyField= idkey[i];
				String valueField = idvalue[i];
				if(keyField != null) {
					if(valueField.indexOf("*") >= 0) {
						bool.must(QueryBuilders.matchAllQuery())
						.must(QueryBuilders.wildcardQuery(keyField, valueField));
					} else {
						System.out.println("this point!!!!");
						bool.must(QueryBuilders.matchAllQuery())
						.must(QueryBuilders.matchQuery(keyField, valueField));
					}
				
				} else if(idkey[0] == "") {
						//밸류값만 검색할 때
						System.out.println("밸류값만 검색하자!!!");
						bool.must(QueryBuilders.matchAllQuery())
						.should(QueryBuilders.moreLikeThisQuery(idvalue)).must(QueryBuilders.queryStringQuery(valueField));
				}
					
			}
			
		}else if ("and".equals(searchType)) {
			for (int i = 0; i < idkey.length; i++) {

				String keyField = idkey[i];
				String valueField = idvalue[i];
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
		}else if ("or".equals(searchType)) {
			for (int i = 0; i < idkey.length; i++) {

				String keyField = idkey[i];
				String valueField = idvalue[i];
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
				}else if(idkey[0] == "") {
					//밸류값만 검색할 때
					System.out.println("밸류값만 검색하자!!!");
					bool.must(QueryBuilders.matchAllQuery())
					.should(QueryBuilders.moreLikeThisQuery(idvalue)).must(QueryBuilders.queryStringQuery(valueField));
					
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

		total = (int) res.getHits().getTotalHits();
		int totals = res.getHits().getHits().length;
		System.out.println("totals"+ totals) ;
		
		Map<String, Object> totalData = new HashMap<>();
		totalData.put("totalSearchData", total);
		totalData.put("searchData", totals);
		
		Map<String, Object> list = new HashMap<>();
		list.put("datas",totalData);
		dataList.add(list);
		
		data.put("data", mapData);
		data.put("totalData", dataList);
		
		return data;
		}

	

	public Map<String, List<Object>> idSearch(String index, String type, String id, String config, Integer total) throws Exception {

		Map<String, List<Object>> dataList = new HashMap<>();
		Map<String, Object> map = new LinkedHashMap<>();
		List<Object> mapData = new ArrayList<>();
		BoolQueryBuilder bool = new BoolQueryBuilder();
		GetRequestBuilder req;
		SearchRequestBuilder srb;
		req = client.prepareGet(index, type, id);
		bool.must(QueryBuilders.matchAllQuery());
		srb = client.prepareSearch(index).setTypes(type);
		SearchResponse srs = srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFrom(0).get();
		
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
		total = (int) srs.getHits().getHits().length; // 전체 데이터의 값
				
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
		
		return dataList;
	}

	
}
