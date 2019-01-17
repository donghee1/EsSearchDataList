package com.oksusu.hdh.service;




import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oksusu.hdh.domain.EsTest;

import com.oksusu.hdh.repository.EsRepository;

//@Service
//public class EsTestServiceImpl implements EsTestService {
//
//	
//	@Autowired
//	EsRepository esRepository;
//	
//
//	@Autowired
//	EsTestMapper mapper;
//
//
//	@Override
//	public Object GetList(EsTest vo) throws Exception {
//		System.out.println("값이 넘어 왔너요?service");
//		
//		List<EsTest> list = null;
//		Object result = null;
//			
//			list = mapper.listSelect(vo);
//			
//			System.out.println("list의 값은?  " + list);
//			
////			result = esRepository.GetList(vo, list);
//				
//		
//		return result;
//	}
//
//
//
//
//	
//
//}
