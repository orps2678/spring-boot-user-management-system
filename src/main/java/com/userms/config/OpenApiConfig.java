package com.userms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    // API 配置參數
    @Value("${app.api.title}")
    private String apiTitle;
    
    @Value("${app.api.description}")
    private String apiDescription;
    
    @Value("${app.api.version}")
    private String apiVersion;
    
    @Value("${app.api.contact.name}")
    private String contactName;
    
    @Value("${app.api.contact.email}")
    private String contactEmail;
    
    @Value("${app.api.contact.url}")
    private String contactUrl;
    
    @Value("${app.api.license.name}")
    private String licenseName;
    
    @Value("${app.api.license.url}")
    private String licenseUrl;
    
    // 服務器配置參數
    @Value("${app.server.dev-url}")
    private String devServerUrl;
    
    @Value("${app.server.prod-url}")
    private String prodServerUrl;
    
    // 常數定義
    private static final String SECURITY_DESCRIPTION = """
            Please enter JWT Token here, format: `Bearer {token}`
            
            ## How to Get Token
            1. Use `/auth/register` to register account
            2. Use `/auth/login` to login and get Token
            3. Copy the returned `token` value
            4. Enter Token in the input box below (no need to manually add Bearer prefix)
            """;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                .info(getApiInfo())
                .servers(getServers())
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, getSecurityScheme()));
    }

    private Info getApiInfo() {
        return new Info()
                .title(apiTitle)
                .description(apiDescription)
                .version(apiVersion)
                .contact(new Contact()
                        .name(contactName)
                        .email(contactEmail)
                        .url(contactUrl))
                .license(new License()
                        .name(licenseName)
                        .url(licenseUrl));
    }

    private List<Server> getServers() {
        return List.of(
                new Server()
                        .url(devServerUrl)
                        .description("Development Environment"),
                new Server()
                        .url(prodServerUrl)
                        .description("Production Environment")
        );
    }

    private SecurityScheme getSecurityScheme() {
        return new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description(SECURITY_DESCRIPTION);
    }
}