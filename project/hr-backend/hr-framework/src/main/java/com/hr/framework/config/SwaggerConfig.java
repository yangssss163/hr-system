package com.hr.framework.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HR 管理系统 API")
                        .version("1.0.0")
                        .description("HR 管理系统后端接口文档")
                        .contact(new Contact()
                                .name("HR Team")
                                .email("admin@hr.com")));
    }
}
