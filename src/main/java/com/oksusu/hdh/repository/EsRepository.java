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
import org.elasticsearch.percolator.QueryAnalyzer;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.internal.FilteredSearchContext;
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

	public Map<String, List<Object>> onlyOneIndexSearch(String index, String config, Integer searchSize, Integer total) {

	Map<String, List<Object>> mapList = new HashMap<>();
	
		SearchResponse onlyIndex = null;
		if (index != null && searchSize == null) {

			onlyIndex = client.prepareSearch(index).get();

		} else if (index != null && searchSize > 0) {

			onlyIndex = client.prepareSearch(index).setFrom(0).setSize(searchSize).get();
		}
		total = (int) onlyIndex.getHits().getTotalHits();
		int totals = onlyIndex.getHits().getHits().length;
		
		//totalData.add(total);
		//searchData.add(totals);
		
		Map<String, Object> hitsData = new HashMap<>();
		hitsData.put("totalSearchData", total);
		hitsData.put("search Data", totals);
		
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
		mapList.put("map", mapData);
		mapList.put("totalData", totalData);
		
		return mapList;

	}

	// 인덱스와 타입을 검색하여 결과값을 도출하는 메서드 입니다.
	public Map<String, List<Object>> indexAndTypeSearch(String index, String type, String config, Integer searchSize, Integer total)
			throws Exception {

		Map<String, List<Object>> list = new HashMap<>();

		SearchResponse res = null;

		if (index != null && type != null && searchSize == null) {
			res = client.prepareSearch(index).setTypes(type).setFrom(0).get();

		} else if (index != null && type != null && searchSize != null) {
			res = client.prepareSearch(index).setTypes(type).setFrom(0).setSize(searchSize).get();
		}
		Map<String, Object> hitData = new HashMap<>();
		total = (int) res.getHits().getTotalHits();
		int totals = res.getHits().getHits().length;
		hitData.put("Total Search Data", total);
		hitData.put("Search Data", totals);
		
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
		list.put("map", mapData);
		list.put("totalData", totalHits);
		
		return list;
	}

	// documents field 값을 도출하는 (key & Value) 기능입니다.
	public Map<String, List<Object>> keyAndVlaueSearch(String index, String type, String[] idkey, String[] idvalue,
			String config, String searchType, Integer searchSize, Integer total) {
		
		
		Map<String, List<Object>> keyValue = new HashMap<>();

		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchRequestBuilder srb = null;

		srb = client.prepareSearch(index).setTypes(type);

		if ("and".equals(searchType)) {
			System.out.println("and point");
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
			System.out.println("or point");
			for (int i = 0; i < idkey.length; i++) {

				String keyField = idkey[i];
				String valueField = idvalue[i];
				if (keyField != null) {
					if (valueField.indexOf("*") >= 0) {
						bool.should(QueryBuilders.wildcardQuery(keyField, valueField))
							.must(QueryBuilders.termQuery(keyField, valueField));
					} else {
						bool.should(QueryBuilders.boolQuery()
								.should(QueryBuilders.matchQuery(keyField, valueField)).minimumShouldMatch(1)
								.must(QueryBuilders.matchAllQuery()));
						
						//.should(QueryBuilders.matchQuery(keyField, valueField))
						
						
					}
				}
			}

		}
		
		if(idkey != null && searchSize == null) {
			srb.setQuery(bool).setFrom(0);
		}else if(idkey != null && searchSize != null){
			srb.setQuery(bool).setFrom(0).setSize(searchSize);	
		}
		
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
		totalData.put("Total Search Data", total);
		totalData.put("Search Data", totals);
		
		Map<String, Object> list = new HashMap<>();
		list.put("datas",totalData);
		data2.add(list);
		
		System.out.println("test data" + data2.size());
		keyValue.put("map", data);
		keyValue.put("totalData", data2);
		System.out.println("keyValue" + keyValue);
		
		return keyValue;

	}

	public Map<String, List<Object>> indexAndKeyValueSearch(String index, String[] idkey, String[] idvalue,
			String config, String searchType, Integer searchSize, Integer total) {
		System.out.println("indexAndKeyValueSearch");

		Map<String, List<Object>> data = new HashMap<>();

		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchRequestBuilder srb = null;

		srb = client.prepareSearch(index);

		if ("and".equals(searchType)) {
			System.out.println("and point");
			for (int i = 0; i < idkey.length; i++) {

				String keyField = idkey[i];
				String valueField = idvalue[i];
				if (keyField != null) {
					if (valueField.indexOf("*") >= 0) {
						System.out.println("AND true");
						bool.must(QueryBuilders.wildcardQuery(keyField, valueField))
							.must(QueryBuilders.termQuery(keyField, valueField));
					} else {
						System.out.println("OR false");
						bool.must(QueryBuilders.matchAllQuery())
							.must(QueryBuilders.matchQuery(keyField, valueField));

					}
				}
			}
		}else if ("or".equals(searchType)) {
			System.out.println("or point");
			for (int i = 0; i < idkey.length; i++) {

				String keyField = idkey[i];
				String valueField = idvalue[i];
				if (keyField != null) {
					if (valueField.indexOf("*") >= 0) {
						System.out.println("OR true");
						bool.should(QueryBuilders.wildcardQuery(keyField, valueField))
							.must(QueryBuilders.termQuery(keyField, valueField));
					} else {
						System.out.println("OR false");
						bool.should(QueryBuilders.matchAllQuery())
						.should(QueryBuilders.matchQuery(keyField, valueField))
						.should(QueryBuilders.rangeQuery(valueField)).minimumShouldMatch(1);
						
						//.should(QueryBuilders.matchQuery(keyField, valueField))
						
						
						System.out.println("뭔데 값이 없는 값인데!" +bool);
					}
				}
			}

		}
		
		

		SearchResponse res = srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFrom(0).setSize(searchSize)
				.get();

		total = (int) res.getHits().getTotalHits();
		int totals = res.getHits().getHits().length;
		
		Map<String, Object> hitsData = new HashMap<>();
		hitsData.put("Total Index Data", total);
		hitsData.put("Search Data", totals);
		
		List<Object> totalHitsData = new ArrayList<>();
		totalHitsData.add(hitsData);
		
		
		List<Object> mapData = new ArrayList<>();
		for (SearchHit hit : res.getHits().getHits()) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("_index", hit.getIndex());
			map.put("_type", hit.getType());
			map.put("_Id", hit.getId());
			map.put("_source", hit.getSourceAsMap());

			mapData.add(map);
		}

		data.put("totalData", totalHitsData);
		data.put("map", mapData);
		
		return data;
	}

	public Map<String, List<Object>> idSearch(String index, String type, String id, String config, Integer total) throws Exception {

		Map<String, List<Object>> dataList = new HashMap<>();
		Map<String, Object> map = new LinkedHashMap<>();
		List<Object> mapData = new ArrayList<>();
		GetRequestBuilder req = null;

		req = client.prepareGet(index, type, id);

		GetResponse res1 = req.get();
		map.put("_index", res1.getIndex());
		map.put("_type", res1.getType());
		map.put("_Id", res1.getId());
		map.put("_source", res1.getSourceAsMap());
		
		mapData.add(map);
		
		dataList.put("map", mapData);

		return dataList;
	}

}
