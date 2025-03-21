package com.datascope.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * OpenAPI配置
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    @Primary
    public OpenAPI dataStudioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DataScope API")
                        .description("DataScope SQL分析平台接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DataScope Team")
                                .email("support@datascope.com")));
    }
}