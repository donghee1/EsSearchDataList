package com.oksusu.hdh;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@SpringBootApplication
@MapperScan({"com.oksusu.hdh.mapper"})
public class OksusuApplication {

	public static void main(String[] args) {
		SpringApplication.run(OksusuApplication.class, args);
		
	}
	
	/**
     * SqlSessionFactory Bean 설정
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource)throws Exception{
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource);
            
            // xml 파일 경로 클레스패스를 등록해 준다.
            
            Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath:mappers/*Mapper.xml");
            
            sessionFactory.setMapperLocations(res);
            
            
            return sessionFactory.getObject();
    }
//    @Bean
//    public MappingJackson2JsonView jsonView() {
//    		return new MappingJackson2JsonView();
//    }
    
 
      
}
