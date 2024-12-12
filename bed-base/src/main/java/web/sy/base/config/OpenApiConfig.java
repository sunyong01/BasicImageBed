package web.sy.base.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("图床服务 API")
                        .version("1.0")
                        .description("基础图床服务API文档")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://github.com/sunyong01/BasicImageBed")))
                .components(new Components()
                        // JWT认证方案
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("管理系统JWT认证token"))
                        // API Token认证方案
                        .addSecuritySchemes("ApiTokenAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("tokenId|token")
                                        .description("API Token认证，格式为: Bearer {tokenId}|{token}")));
    }
} 