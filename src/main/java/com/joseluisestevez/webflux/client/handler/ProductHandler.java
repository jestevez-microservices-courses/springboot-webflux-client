package com.joseluisestevez.webflux.client.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.joseluisestevez.webflux.client.dto.ProductDto;
import com.joseluisestevez.webflux.client.services.ProductService;

import reactor.core.publisher.Mono;

@Component
public class ProductHandler {

    @Autowired
    private ProductService productService;

    public Mono<ServerResponse> list(ServerRequest request) {

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(productService.findAll(), ProductDto.class);

    }

    public Mono<ServerResponse> view(ServerRequest request) {
        String id = request.pathVariable("id");
        return productService.findById(id).flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
