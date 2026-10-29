
package com.HoraCerta.horacerta.adapter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

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
                                """));
    }
}
