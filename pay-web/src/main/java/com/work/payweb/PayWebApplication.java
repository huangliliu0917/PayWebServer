package com.work.payweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
//@EnableFeignClients
@MapperScan("com.work.generaldb.mapper")
@ComponentScan(basePackages = {"com.work.*"})
@SpringBootApplication
public class PayWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayWebApplication.class, args);
	}
}
