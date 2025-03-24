package com.datascope.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * DataScope应用程序入口类
 * 禁用DataSourceAutoConfiguration以使用自定义数据源配置
 * 通过自定义的DatabaseInitializationConfig类管理数据库初始化流程
 * 使用Spring Boot标准SQL初始化机制代替Flyway
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class
}, scanBasePackages = "com.datascope")
@ComponentScan(basePackages = "com.datascope")
@EntityScan(basePackages = {"com.datascope.domain.model", "com.datascope.domain.entity"})
@EnableJpaRepositories(basePackages = "com.datascope.domain.repository")
@EnableRedisRepositories(basePackages = "com.datascope.domain.repository")
@EnableTransactionManagement
public class DataScopeApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataScopeApplication.class, args);
    }
}
