package com.datascope.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.datascope")
@EntityScan("com.datascope.domain.model")
@EnableJpaRepositories("com.datascope.infrastructure.repository")
public class DataScopeApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DataScopeApplication.class, args);
    }
}