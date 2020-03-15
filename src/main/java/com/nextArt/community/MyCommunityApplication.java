package com.nextArt.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.nextArt.community.mapper")
public class MyCommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyCommunityApplication.class, args);
	}

}
