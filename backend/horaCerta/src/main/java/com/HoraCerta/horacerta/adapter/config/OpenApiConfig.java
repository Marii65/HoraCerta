
package com.HoraCerta.horacerta.adapter.config;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI horaCertaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HoraCerta API")
                        .version("1.0.0")
                        .description("""
                                API para gerenciamento de manutenção preventiva
                                de equipamentos por horímetro.

                                Permite cadastrar equipamentos, planos de manutenção,
                                leituras de horímetro e ordens de serviço, além de
                                acompanhar manutenções próximas do vencimento
                                ou atrasadas.
                                """))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "Informe o token JWT obtido no login."
                                        )
                        ))
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME)
                );
    }
}
