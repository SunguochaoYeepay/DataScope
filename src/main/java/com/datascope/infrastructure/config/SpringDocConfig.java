package com.datascope.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置类
 * 用于配置 Swagger UI 界面和 API 文档
 */
@Configuration
public class SpringDocConfig {

  /**
   * 定义 OpenAPI 元数据
   */
  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("DataScope API Documentation")
                .description("API documentation for DataScope application")
                .version("1.0.0")
                .contact(new Contact()
                        .name("DataScope Team")
                        .email("support@datascope.com")
                        .url("https://datascope.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0")))
        .components(new Components()
                .addSecuritySchemes("bearer-key",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
  }
  
  /**
   * 定义分组的 API 文档
   * 显式指定扫描包路径，确保找到所有控制器
   */
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("datascope-public")
        .pathsToMatch("/api/**")
        .packagesToScan("com.datascope.facade.controller")
        .build();
  }
} 