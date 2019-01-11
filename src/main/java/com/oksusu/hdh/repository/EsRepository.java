package com.oksusu.hdh.repository;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.elasticsearch.action.admin.indices.get.GetIndexRequest;

import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.oksusu.hdh.domain.EsTest;
import com.oksusu.hdh.mapper.BoardMapper;

@Repository
public class EsRepository {

	@Autowired
	private Client client;
				
	@Autowired
	private Client dev;

	@Autowired
	private Client bmt;
	
	public Client changeClient(String server) {
		if(server.equals("dev")){
			client = dev;
		}else if(server.equals("bmt")) {
			client = bmt;
		}
			return client;
	}
	public List<String> searchIndexList(String index, String config) throws Exception {

		List<String> list = null;

		if (index == null && config == null) {
			String[] res = client.admin().indices().getIndex(new GetIndexRequest()).actionGet().getIndices();
			list = Arrays.asList(res);

		} else if ("dev".equals(config) && index == null) {
			String[] devRes = dev.admin().indices().getIndex(new GetIndexRequest()).actionGet().getIndices();
			list = Arrays.asList(devRes);

		} else if ("bmt".equals(config) && index == null) {
			String[] bmtRes = bmt.admin().indices().getIndex(new GetIndexRequest()).actionGet().getIndices();
			list = Arrays.asList(bmtRes);

		} else if ("test".equals(config) && index == null) {
			String[] res = client.admin().indices().getIndex(new GetIndexRequest()).actionGet().getIndices();
			list = Arrays.asList(res);
		}

		return list;
	}

	public List<String> typeListMappings(EsTest vo, String config) throws Exception {

		List<String> typeList = new ArrayList<>();
		// System.out.println("typeList index data ::: " + vo.getIndex());
		// System.out.println("typeList config data :::" + config);
		GetMappingsResponse res = null;

		if ("dev".equals(config)) {
			res = dev.admin().indices().getMappings(new GetMappingsRequest().indices(vo.getIndex().toString())).get();
		} else if ("bmt".equals(config)) {
			res = bmt.admin().indices().getMappings(new GetMappingsRequest().indices(vo.getIndex().toString())).get();
		} else if ("".equals(config) || "test".equals(config) && vo.getIndex() != null) {
			res = client.admin().indices().getMappings(new GetMappingsRequest().indices(vo.getIndex().toString()))
					.get();
		}
		ImmutableOpenMap<String, MappingMetaData> mapping = res.getMappings().get(vo.getIndex());
		// System.out.println("immutableopenmap" + mapping.toString());
		for (ObjectObjectCursor<String, MappingMetaData> c : mapping) {
			if (c != null) {
				// c의 키값을 타입리스트에 add한다.
				typeList.add(c.key);
			}
		}

		return typeList;

	}

	public List<Map<String, Object>> onlyOneIndexSearch(String index, String config) {

		// System.out.println("onlyOne index!!! "+ index);
		// System.out.println("onlyOne config!!! "+ config);
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

		SearchResponse onlyIndex = null;
		if (index != null) {
			// System.out.println("onlyIndexStart!!!!!!");
			if ("test".equals(config) || "".equals(config)) {
				onlyIndex = client.prepareSearch(index).get();
			} else if ("dev".equals(config)) {
				onlyIndex = dev.prepareSearch(index).get();
			} else if ("bmt".equals(config)) {
				onlyIndex = bmt.prepareSearch(index).get();
			}
		} else {
			System.out.println("onlyIndex Search Error!!!!!");
			return null;
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
	public List<Map<String, Object>> indexAndTypeSearch(String index, String type, String config) throws Exception {

		List<Map<String, Object>> list = new ArrayList<>();

		SearchResponse res = null;

		if (index != null && type != null) {
			if (("".equals(config) || index == null) || ("test".equals(config) && config != null)) {
				res = client.prepareSearch(index).setTypes(type).get();
			} else if ("dev".equals(config)) {
				res = dev.prepareSearch(index).setTypes(type).get();
			} else if ("bmt".equals(config)) {
				res = bmt.prepareSearch(index).setTypes(type).get();
			}
		} else {
			System.out.println("indexAndType Search Error!!!!!");
			return null;
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
			String config) {

		System.out.println("start keyAndValueSearch!!!");
		List<Map<String, Object>> keyValue = new ArrayList<>();

		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchRequestBuilder srb = null;

//		System.out.println("index data :::" + index);
//		System.out.println("type data :::" + type);
//		System.out.println("config data :::" + config);

		if (index != null && type != null) {
			System.out.println("this keyAndValue point!!");
			if (("".equals(config) || config == null) || "test".equals(config)) {
				srb = client.prepareSearch(index).setTypes(type);
			} else if ("dev".equals(config)) {
				srb = dev.prepareSearch(index).setTypes(type);
			} else if ("bmt".equals(config)) {
				srb = bmt.prepareSearch(index).setTypes(type);
			}
		} else {
			System.out.println("keyAndValue Search Error!!!!!");
		}

		if (idkey != null) {
			for (int i = 0; i < idkey.length; i++) {

				String keyField = idkey[i];
				String valueField = idvalue[i];
				// System.out.println("this keyField!! " + keyField.indexOf("*") + " " +
				// valueField.indexOf("*"));

				if (keyField != null) {
					if (valueField.indexOf("*") >= 0) {
						bool.must(QueryBuilders.wildcardQuery(keyField, valueField));
					} else {
						bool.must(QueryBuilders.termQuery(keyField, valueField));
					}
					if (keyField != null) {
						if (valueField.indexOf("*") >= 0) {
							System.out.println("start shold!!!!!!");
							bool.should(QueryBuilders.wildcardQuery(keyField, valueField));
						} else {
							bool.should(QueryBuilders.termQuery(keyField, valueField));
						}
					}
				}
			}
		} else {
			System.out.println("error!!!!!!!!!!!!");
			return null;
		}
		if (idkey != null) {
			srb.setQuery(bool);
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
			String config) {
		System.out.println("indexAndKeyValueSearch");
		List<Map<String, Object>> data = new ArrayList<>();

		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchRequestBuilder srb = null;

		if ("".equals(config) || "test".equals(config)) {
			srb = client.prepareSearch(index);
		} else if ("dev".equals(config)) {
			srb = dev.prepareSearch(index);
		} else if ("bmt".equals(config)) {
			srb = bmt.prepareSearch(index);
		}

		if (idkey.length > 0) {
			for (int i = 0; i < idkey.length; i++) {
				bool.must(QueryBuilders.matchQuery(idkey[i], idvalue[i]));
				srb.setQuery(bool);
			}
		}

		SearchResponse response = srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).get();

		for (SearchHit hit : response.getHits().getHits()) {
			Map<String, Object> map = new LinkedHashMap<>();

			map.put("_index", hit.getIndex());
			map.put("_type", hit.getType());
			map.put("_Id", hit.getId());
			map.put("_source", hit.getSourceAsMap());

			data.add(map);
		}
		System.out.println("data" + data);
		return data;
	}

	public List<Map<String, Object>> idSearch(String index, String type, String id, String config) throws Exception {

		List<Map<String, Object>> dataList = new ArrayList<>();
		Map<String, Object> map = new LinkedHashMap<>();
		GetRequestBuilder req = null;

		if ("".equals(config) || "test".equals(config)) {
			req = client.prepareGet(index, type, id);
		} else if ("dev".equals(config)) {
			req = dev.prepareGet(index, type, id);
		} else if ("bmt".equals(config)) {
			req = bmt.prepareGet(index, type, id);
		}

		GetResponse res1 = req.get();
		map.put("_index", res1.getIndex());
		map.put("_type", res1.getType());
		map.put("_Id", res1.getId());
		map.put("_source", res1.getSourceAsMap());

		dataList.add(map);

		// System.out.println("res6??" + dataList.toString());

		return dataList;
	}

}
