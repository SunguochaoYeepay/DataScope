package com.datascope.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.mybatis.spring.annotation.MapperScan;

/**
 * DataScope 应用程序入口
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.datascope")
@MapperScan("com.datascope.infrastructure.mapper")
public class DataScopeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataScopeApplication.class, args);
    }
}