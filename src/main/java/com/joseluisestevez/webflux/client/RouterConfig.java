package com.joseluisestevez.webflux.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.joseluisestevez.webflux.client.handler.ProductHandler;

@Configuration
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler productHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/api/client"), productHandler::list).andRoute(RequestPredicates.GET("/api/client/{id}"),
                productHandler::view);
    }
}
