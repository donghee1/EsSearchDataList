package com.oksusu.hdh.compare;

import java.util.Comparator;
import java.util.Map;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Ordering;

//@Configuration
//public class DataCompare implements Comparator<Map<String, Object>>{
//
//	@Override
//	@Bean
//	public int compare(Map<String, Object>o1, Map<String, Object> o2) {
//		
//		CompareToBuilder compareToBuilder = new CompareToBuilder();
//		
//		compareToBuilder.append(o1.get("_source"), o2.get("_source"), Ordering.natural().nullsLast());
//		
//		return compareToBuilder.toComparison();
//		// http://1.255.98.109:9200/n_cms_vod/view/_search?size=100&sort=seq_no:desc
//	}
//}
