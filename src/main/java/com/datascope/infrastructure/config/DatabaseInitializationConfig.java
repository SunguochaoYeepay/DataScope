package com.datascope.infrastructure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库初始化配置类，不使用Flyway，使用简单的SQL脚本初始化：
 * 1. 执行 schema.sql 创建必要的表结构
 * 2. 执行 data.sql 初始化数据
 */
@Configuration
public class DatabaseInitializationConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializationConfig.class);

    @Autowired
    private Environment environment;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.sql.init.schema-locations:classpath:db/schema.sql}")
    private String schemaLocations;

    @Value("${spring.sql.init.data-locations:classpath:db/data.sql}")
    private String dataLocations;

    /**
     * 创建主数据源，所有组件共享此数据源
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }

    /**
     * 创建数据库结构初始化器，负责执行schema.sql文件
     */
    @Bean
    public DataSourceInitializer schemaInitializer(DataSource dataSource, ResourceLoader resourceLoader) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setEnabled(!isTestEnvironment());

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        
        // 处理多个脚本路径
        String[] locations = schemaLocations.split(",");
        for (String location : locations) {
            String trimmedLocation = location.trim();
            logger.info("Adding schema script: {}", trimmedLocation);
            try {
                populator.addScript(resourceLoader.getResource(trimmedLocation));
            } catch (Exception e) {
                logger.warn("Could not find schema script: {}", trimmedLocation, e);
            }
        }
        
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    /**
     * 创建数据初始化器，负责执行data.sql文件
     */
    @Bean
    public DataSourceInitializer dataInitializer(DataSource dataSource, ResourceLoader resourceLoader) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setEnabled(!isTestEnvironment());

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        
        // 处理多个脚本路径
        String[] locations = dataLocations.split(",");
        for (String location : locations) {
            String trimmedLocation = location.trim();
            logger.info("Adding data script: {}", trimmedLocation);
            try {
                populator.addScript(resourceLoader.getResource(trimmedLocation));
            } catch (Exception e) {
                logger.warn("Could not find data script: {}", trimmedLocation, e);
            }
        }
        
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    /**
     * 检查当前环境是否为测试环境
     */
    private boolean isTestEnvironment() {
        return Arrays.asList(environment.getActiveProfiles()).contains("test");
    }
} 