package com.datascope.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.datascope")
@EnableJpaRepositories({
  "com.datascope.domain.repository",
  "com.datascope.infrastructure.repository"
})
@EntityScan({"com.datascope.domain.model.datasource", "com.datascope.domain.entity"})
public class DataScopeApplication {

  public static void main(String[] args) {
    SpringApplication.run(DataScopeApplication.class, args);
  }
}
