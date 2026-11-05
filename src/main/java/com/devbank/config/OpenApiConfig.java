package com.devbank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI devBankOpenApi() {
        return new OpenAPI().info(new Info()
                .title("DevBank API")
                .description("API REST de um banco digital: clientes, contas, transações e extrato.")
                .version("1.0"));
    }
}