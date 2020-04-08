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
    public static final String ROUTE_PREFIX = "/api/client";

    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler productHandler) {
        return RouterFunctions.route(RequestPredicates.GET(ROUTE_PREFIX), productHandler::list)
                .andRoute(RequestPredicates.GET(ROUTE_PREFIX.concat("/{id}")), productHandler::view)
                .andRoute(RequestPredicates.POST(ROUTE_PREFIX), productHandler::create)
                .andRoute(RequestPredicates.PUT(ROUTE_PREFIX.concat("/{id}")), productHandler::edit)
                .andRoute(RequestPredicates.DELETE(ROUTE_PREFIX.concat("/{id}")), productHandler::delete)
                .andRoute(RequestPredicates.POST(ROUTE_PREFIX.concat("/upload/{id}")), productHandler::upload);
    }
}
