package com.datascope.main.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI配置类
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DataScope API文档")
                        .description("DataScope项目接口文档")
                        .version("1.0")
                        .contact(new Contact()
                                .name("DataScope Team")
                                .url("http://localhost:8080/")));
    }
}