package com.oksusu.hdh.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
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
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.stereotype.Repository;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.fasterxml.jackson.databind.ser.SerializerCache;

import com.oksusu.hdh.domain.EsTest;

@Repository
public class EsRepository {

	@Autowired
	private Client client;
				
	@Autowired
	private Client dev;

	@Autowired
	private Client bmt;
	
//	@Autowired
//	private DataCompare compare;
	
	public Client changeClient(String config) {
		
		//System.out.println("dataType!!!" + config.toString());
		
		if(config.equals("dev")){
			client = dev;
		}else if(config.equals("bmt")) {
			client = bmt;
		}else if(config.equals("test")) {
			client = client; // 자기 자신을 가리키는 법. this 메소드 활용법을 알아보자!
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
		// System.out.println("typeList index data ::: " + vo.getIndex());
		// System.out.println("typeList config data :::" + config);
		GetMappingsResponse res = null;
			res = client.admin().indices().getMappings(new GetMappingsRequest().indices(getIndex.toString()))
					.get();
		
		ImmutableOpenMap<String, MappingMetaData> mapping = res.getMappings().get(getIndex);
		// System.out.println("immutableopenmap" + mapping.toString());
		for (ObjectObjectCursor<String, MappingMetaData> c : mapping) {
			if (c != null) {
				// c의 키값을 타입리스트에 add한다.
				typeList.add(c.key);
			}
		}
		
		Collections.sort(typeList);

		return typeList;

	}

	public List<Map<String, Object>> onlyOneIndexSearch(String index, String config) {

		// System.out.println("onlyOne index!!! "+ index);
		// System.out.println("onlyOne config!!! "+ config);
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

		SearchResponse onlyIndex = null;
		if (index != null) {
			// System.out.println("onlyIndexStart!!!!!!");
			
				onlyIndex = client.prepareSearch(index).get();
			
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
				res = client.prepareSearch(index).setTypes(type).get();
			
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
			String config, String searchType, String sortType) {

		System.out.println("start keyAndValueSearch!!!");
		List<Map<String, Object>> keyValue = new ArrayList<>();

		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchSourceBuilder ssb = new SearchSourceBuilder();
		SearchRequestBuilder srb = null;
		
//		System.out.println("index data :::" + index);
//		System.out.println("type data :::" + type);
//		System.out.println("config data :::" + config);
		
		if (index != null && type != null) {
			
				srb = client.prepareSearch(index).setTypes(type);
			
				//ssb.docValueFields().sort(Collections.sort(keyValue, c));;
				//ssb.fetchSource().includes().equals(index); 값이 없다고 뜸
			
		} else {
			System.out.println("keyAndValue Search Error!!!!!");
			return null;
		}

		if(searchType.length() == 0 || searchType == null) {
			System.out.println("keyAndValue searchType Error!!!!!");
			return null;
		}else {
			
			if("".equals(searchType) || "and".equals(searchType) && idkey != null) {
				for (int i = 0; i < idkey.length; i++) {

					String keyField = idkey[i];
					String valueField = idvalue[i];
					System.out.println("this and type point!!!");
					if (keyField != null) {
						if (valueField.indexOf("*") >= 0) {
							bool.must(QueryBuilders.wildcardQuery(keyField, valueField))
							.must(QueryBuilders.matchAllQuery())
							.must(QueryBuilders.termQuery(keyField, valueField));
						} else {
							bool.must(QueryBuilders.matchQuery(keyField, valueField));
						}
					}
				}
			}else if("or".equals(searchType) && idkey != null) {
				for (int i = 0; i < idkey.length; i++) {

					String keyField = idkey[i];
					String valueField = idvalue[i];
					System.out.println("this or type point!!!");
					if (keyField != null) {
					if (valueField.indexOf("*") >= 0) {
						System.out.println("start shold!!!!!!");
						bool.should(QueryBuilders.wildcardQuery(keyField, valueField));
					} else {
						bool.should(QueryBuilders.matchQuery(keyField, valueField));
					}
				}
				
				}
			}else if("andNot".equals(searchType) && idkey != null) {
				for (int i = 0; i < idkey.length; i++) {

					String keyField = idkey[i];
					String valueField = idvalue[i];
					System.out.println("this andNot type point!!!");
					if (keyField != null) {
					if (valueField.indexOf("*") >= 0) {
						System.out.println("start shold!!!!!!");
						bool.mustNot(QueryBuilders.wildcardQuery(keyField, valueField));
					} else {
						bool.mustNot(QueryBuilders.matchQuery(keyField, valueField));
					}
				}
				
				}
			}
		}
		if (idkey != null) {
			//ssb.query(bool);
			srb.setQuery(bool);
			
			if(sortType.length()>0 || sortType != null) {
				if("ASC".equals(sortType)) {
				System.out.println("this asc point");
				//Collections.sort(keyValue, compare);
				ssb.sort(new ScoreSortBuilder().order(SortOrder.ASC));
				//srb.addSort(new source..order(SortOrder.ASC));
				}else if("DESC".equals(sortType)) {
				System.out.println("this DESC point");
				//Collections.sort(keyValue, compare);
				ssb.sort(new ScoreSortBuilder().order(SortOrder.DESC));
				//srb.addSort(new SortBuilders().scoreSort().order(SortOrder.DESC));
				}
				srb.setSource(ssb);
			}
			
		} else {
			System.out.println("error!!!!!!!!!!!!");
			return null;
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
			String config, String searchType, String sortType) {
		System.out.println("indexAndKeyValueSearch");
		
		
		List<Map<String, Object>> data = new ArrayList<>();
		
		BoolQueryBuilder bool = new BoolQueryBuilder();
		SearchRequestBuilder srb = null;

			srb = client.prepareSearch(index);
		if(searchType.length()>0 && "and".equals(searchType)) {
			System.out.println("andType Search!!!");
			if (idkey.length > 0) {
				for (int i = 0; i < idkey.length; i++) {
					bool.must(QueryBuilders.matchQuery(idkey[i], idvalue[i]));
					srb.setQuery(bool);
				}
			}
		}else if("or".equals(searchType)) {
			System.out.println("orType Search!!!");
			if (idkey.length > 0) {
				for (int i = 0; i < idkey.length; i++) {
					bool.should(QueryBuilders.matchQuery(idkey[i], idvalue[i]));
					srb.setQuery(bool);
				}
			}
		}else if("andNot".equals(searchType)) {
			System.out.println("andNotType Search!!!");
			if (idkey.length > 0) {
				for (int i = 0; i < idkey.length; i++) {
					bool.mustNot(QueryBuilders.matchQuery(idkey[i], idvalue[i]));
					srb.setQuery(bool);
				}
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
		
		if(sortType.length()>0 || sortType != null) {
			if("ASC".equals(sortType)) {
			//Collections.sort(data, new DataCompare());
			}else if("DESC".equals(sortType)) {
			//Collections.sort(data, new DataCompare());
			}
		}
		
		System.out.println("data" + data);
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

		// System.out.println("res6??" + dataList.toString());

		return dataList;
	}
	

}
