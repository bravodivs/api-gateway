package com.api.gateway.filter;

import com.api.gateway.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class RedirectionFilter extends AbstractGatewayFilterFactory<RedirectionFilter.Config>
        implements Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RedirectionFilter.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    public RedirectionFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            logger.info("Applying the redirection filter");
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (token != null && !token.isBlank() && !token.isEmpty()) {
                return validateToken(token)
                        .flatMap(isValid -> {
                            if (isValid) {
                                return chain.filter(exchange);
                            } else {
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                return exchange.getResponse().setComplete();
                            }
                        });
            }

            logger.info("No Auth header found. Continuing.");
            return chain.filter(exchange);
        };
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Boolean> validateToken(String token) {
        return webClientBuilder.baseUrl("lb://user-service/api/user").build()
                .get()
                .uri("/auth/validate-token")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new CustomException("Token not valid", HttpStatus.UNAUTHORIZED)))
                .bodyToMono(String.class)
                .map(responseBody -> {
                    logger.info("Token validation response: {}", responseBody);
                    return true;
                })
                .onErrorResume(throwable -> {
                    logger.error("Error during token validation", throwable);
                    return Mono.just(false);
                });
    }

    public static class Config {
        // Name some fields
    }
}


