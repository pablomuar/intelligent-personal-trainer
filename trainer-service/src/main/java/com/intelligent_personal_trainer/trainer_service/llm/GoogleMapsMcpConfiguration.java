package com.intelligent_personal_trainer.trainer_service.llm;

import io.modelcontextprotocol.client.transport.customizer.McpAsyncHttpClientRequestCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GoogleMapsMcpConfiguration {

    @Value("${GOOGLE_MAPS_API_KEY}")
    private String googleMapsApiKey;

    @Bean
    public McpAsyncHttpClientRequestCustomizer googleMapsAuthCustomizer() {
        return (builder, method, endpoint, body, context) -> Mono.defer(() -> {
            // Validación de contexto (opcional): asegurar que estamos añadiendo
            // la cabecera solo al servidor correcto si hubiera múltiples.
            if (endpoint.toString().contains("googleapis.com")) {
                builder.header("X-Goog-Api-Key", googleMapsApiKey);
            }
            return Mono.just(builder);
        });
    }
}
