package com.oksusu.hdh;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
//@MapperScan({"com.oksusu.hdh.mapper"})
public class OksusuApplication extends SpringBootServletInitializer {
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OksusuApplication.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(OksusuApplication.class, args);
		
	}
	

    
 
      
}
