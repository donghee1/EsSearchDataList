package com.oksusu.hdh.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //그냥 컨트롤러는 안된다. 왜지? 컨트롤러에 리퀘스트맵핑 잡아줘도 안됨. 하위 메서드도 맵핑 잡아줘도 안됨. 
				// 메인메서드에 레스트 컨트롤러 걸고 하위 메서드에 맵핑 잡아주면 하위 메서드 타입이 스트링인데 리스폰즈바디가 안들어가서 그런가?!
@RequestMapping("/1") //현재 현상에선 잘나온다!!!!! 다음단
public class MainController {

	@RequestMapping("/hello")
	public String Hello() {
		
		return "hi~ Hello!!!";
	}
	
}
