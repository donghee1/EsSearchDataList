package com.oksusu.hdh.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Repository;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
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

	public List<Map<String, Object>> onlyOneIndexSearch(String index, String config, Integer searchSize) {

		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

		SearchResponse onlyIndex = null;
		if (index != null && searchSize == null) {

			onlyIndex = client.prepareSearch(index).get();

		} else if (index != null && searchSize > 0) {

			onlyIndex = client.prepareSearch(index).setFrom(0).setSize(searchSize).get();
		}

		for (SearchHit hits : onlyIndex.getHits().getHits()) {

			Map<String, Object> map = new LinkedHashMap<>();

			map.put("_index", hits.getIndex());
			map.put("_type", hits.getType());
			map.put("_id", hits.getId());
			map.put("_source", hits.getSourceAsMap());

			mapList.add(map);

		}

		return mapList;

	}

	// 인덱스와 타입을 검색하여 결과값을 도출하는 메서드 입니다.
	public List<Map<String, Object>> indexAndTypeSearch(String index, String type, String config, Integer searchSize)
			throws Exception {

		List<Map<String, Object>> list = new ArrayList<>();

		SearchResponse res = null;

		if (index != null && type != null && searchSize == null) {
			res = client.prepareSearch(index).setTypes(type).setFrom(0).get();

		} else if (index != null && type != null && searchSize != null) {
			res = client.prepareSearch(index).setTypes(type).setFrom(0).setSize(searchSize).get();
		}

		for (SearchHit hits : res.getHits().getHits()) {
			Map<String, Object> typeMap = new LinkedHashMap<>();

			typeMap.put("_type", hits.getType());
			typeMap.put("_id", hits.getId());
			typeMap.put("_source", hits.getSource());

			list.add(typeMap);

		}

		return list;
	}

	// documents field 값을 도출하는 (key & Value) 기능입니다.
	public List<Map<String, Object>> keyAndVlaueSearch(String index, String type, String[] idkey, String[] idvalue,
			String config, String searchType, Integer searchSize) {
		
		System.out.println("index!!!"+index);
		System.out.println("SIze!!!"+searchSize);
		
		List<Map<String, Object>> keyValue = new ArrayList<>();

		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchSourceBuilder ssb = new SearchSourceBuilder();
		SearchRequestBuilder srb = null;

		srb = client.prepareSearch(index).setTypes(type);

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
							.should(QueryBuilders.termQuery(keyField, valueField));
					} else {
						System.out.println("OR false");
						bool.should(QueryBuilders.matchAllQuery())
							.should(QueryBuilders.matchQuery(keyField, valueField));

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
		for (SearchHit hit : keyAndValue.getHits().getHits()) {
			Map<String, Object> map = new LinkedHashMap<>();

			map.put("_index", hit.getIndex());
			map.put("_type", hit.getType());
			map.put("_Id", hit.getId());
			map.put("_source", hit.getSourceAsMap());

			keyValue.add(map);

		}

		return keyValue;

	}

	public List<Map<String, Object>> indexAndKeyValueSearch(String index, String[] idkey, String[] idvalue,
			String config, String searchType, Integer searchSize) {
		System.out.println("indexAndKeyValueSearch");

		List<Map<String, Object>> data = new ArrayList<>();

		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchRequestBuilder srb = null;

		srb = client.prepareSearch(index);

		if (idkey.length > 0) {
			for (int i = 0; i < idkey.length; i++) {
				bool.must(QueryBuilders.matchQuery(idkey[i], idvalue[i]));
				srb.setQuery(bool);
			}
		}

		SearchResponse response = srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setFrom(0).setSize(searchSize)
				.get();

		for (SearchHit hit : response.getHits().getHits()) {
			Map<String, Object> map = new LinkedHashMap<>();

			map.put("_index", hit.getIndex());
			map.put("_type", hit.getType());
			map.put("_Id", hit.getId());
			map.put("_source", hit.getSourceAsMap());

			data.add(map);
		}

		return data;
	}

	public List<Map<String, Object>> idSearch(String index, String type, String id, String config) throws Exception {

		List<Map<String, Object>> dataList = new ArrayList<>();
		Map<String, Object> map = new LinkedHashMap<>();
		GetRequestBuilder req = null;

		req = client.prepareGet(index, type, id);

		GetResponse res1 = req.get();
		map.put("_index", res1.getIndex());
		map.put("_type", res1.getType());
		map.put("_Id", res1.getId());
		map.put("_source", res1.getSourceAsMap());

		dataList.add(map);

		return dataList;
	}

}
