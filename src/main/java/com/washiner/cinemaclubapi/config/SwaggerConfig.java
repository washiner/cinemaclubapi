package com.washiner.cinemaclubapi.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configura o botão Authorize no Swagger para aceitar JWT
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CinemaClub API")
                        .description("API de cinema com JWT, Roles, CORS e Refresh Token")
                        .version("v1")
                        .contact(new Contact()
                                .name("Washiner")
                                .email("washiner@email.com")
                        )
                );
    }
}
