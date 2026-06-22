package com.leotech.benefits.authorizer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Benefits Authorizer")
                        .description("Microsserviço de autorização de transações para cartões de benefícios")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Leonardo Dantas")
                                .email("leonardordnt1317@gmail.com")));
    }

}
