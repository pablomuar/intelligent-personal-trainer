package com.intelligent_personal_trainer.trainer_service.llm;

import io.modelcontextprotocol.client.transport.customizer.McpAsyncHttpClientRequestCustomizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class McpConnectionConfiguration {

    private final LoadBalancerClient loadBalancerClient;

    @Value("${mcp.connection.customizer.services}")
    private List<String> servicesToCustomize;

    @Value("${mcp.connection.customizer.google-maps-api-key}")
    private String googleMapsApiKey;

    @Bean
    public McpAsyncHttpClientRequestCustomizer connectionCustomizer() {
        return (builder, method, endpoint, body, context) -> Mono.defer(() -> {
            if (endpoint.toString().contains("googleapis.com")) {
                builder.header("X-Goog-Api-Key", googleMapsApiKey);
            }

            servicesToCustomize.forEach(service -> {
                if (endpoint.toString().contains(service)) {
                    changeLoadBalancerURI(builder, endpoint, service);
                }
            });

            return Mono.just(builder);
        });
    }

    private void changeLoadBalancerURI(HttpRequest.Builder builder, URI endpoint, String serviceName) {
        try {
            ServiceInstance instance = loadBalancerClient.choose(serviceName);
            if (instance != null) {
                URI resolvedUri = new URI(
                        instance.getUri().getScheme(),
                        instance.getUri().getUserInfo(),
                        instance.getHost(),
                        instance.getPort(),
                        endpoint.getPath(),
                        endpoint.getQuery(),
                        endpoint.getFragment()
                );

                log.debug("MCP Connection configurator: {} -> {}", serviceName, resolvedUri);
                builder.uri(resolvedUri);

            } else {
                log.warn("No MCP Connection configurator available for: {}", serviceName);
            }

        } catch (Exception e) {
            log.error("Error in MCP Connection configurator: {}", e.getMessage());
        }
    }
}
