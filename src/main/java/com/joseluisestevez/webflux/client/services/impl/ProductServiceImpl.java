package com.joseluisestevez.webflux.client.services.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.joseluisestevez.webflux.client.dto.ProductDto;
import com.joseluisestevez.webflux.client.services.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private WebClient webClient;

    @Override
    public Flux<ProductDto> findAll() {
        return webClient.get().accept(MediaType.APPLICATION_JSON).exchange().flatMapMany(response -> response.bodyToFlux(ProductDto.class));
    }

    @Override
    public Mono<ProductDto> findById(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        return webClient.get().uri("/{id}", params).accept(MediaType.APPLICATION_JSON)
                // .retrieve().bodyToMono(ProductDto.class) // otra forma
                .exchange().flatMap(response -> response.bodyToMono(ProductDto.class));
    }

    @Override
    public Mono<ProductDto> save(ProductDto productDto) {
        return webClient.post().accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).bodyValue(productDto)
                // .body(BodyInserters.fromValue(productDto)) // otra forma
                .retrieve().bodyToMono(ProductDto.class);
    }

    @Override
    public Mono<ProductDto> update(ProductDto productDto, String id) {
        return webClient.put().uri("/{id}", Collections.singletonMap("id", id)).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).bodyValue(productDto).retrieve().bodyToMono(ProductDto.class);
    }

    @Override
    public Mono<Void> delete(String id) {
        return webClient.delete().uri("/{id}", Collections.singletonMap("id", id)).exchange().then();
    }

}
