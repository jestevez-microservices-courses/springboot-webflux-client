package com.joseluisestevez.webflux.client.services;

import com.joseluisestevez.webflux.client.dto.ProductDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<ProductDto> findAll();

    Mono<ProductDto> findById(String id);

    Mono<ProductDto> save(ProductDto productDto);

    Mono<ProductDto> update(ProductDto productDto, String id);

    Mono<Void> delete(String id);
}
