package com.oksusu.hdh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
//@MapperScan({"com.oksusu.hdh.mapper"})
@EnableScheduling
public class OksusuApplication  {
	

	public static void main(String[] args) {
		SpringApplication.run(OksusuApplication.class, args);
		
	}
	

    
 
      
}
