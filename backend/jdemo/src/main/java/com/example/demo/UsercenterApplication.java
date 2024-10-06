package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * springboot app run main entry
 * @EnableScheduling 在springboot中开启对 定时任务 的支持
 */
@EnableScheduling
@SpringBootApplication
@MapperScan("com.example.demo.mapper")
public class UsercenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsercenterApplication.class, args);
	}

}
