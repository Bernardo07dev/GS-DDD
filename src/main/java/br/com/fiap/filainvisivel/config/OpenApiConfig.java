package br.com.fiap.filainvisivel.config;

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
                        .title("Fila Invisível API")
                        .description("API RESTful para previsão de lotação em UBS e recomendação de melhor horário ao cidadão (Global Solution FIAP - CitizenTech).")
                        .version("1.0.0")
                        .contact(new Contact().name("Equipe Fila Invisível")));
    }
}
