package com.joseluisestevez.webflux.client.handler;

import java.net.URI;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.joseluisestevez.webflux.client.RouterConfig;
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
        return errorHandler(productService.findById(id).flatMap(p -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(p))
                .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<ProductDto> product = request.bodyToMono(ProductDto.class);
        return product.flatMap(p -> {
            if (p.getCreateAt() == null) {
                p.setCreateAt(new Date());
            }
            return productService.save(p);
        }).flatMap(p -> ServerResponse.created(URI.create(RouterConfig.ROUTE_PREFIX.concat(p.getId()))).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(p)).onErrorResume(error -> {
                    WebClientResponseException errorResponse = (WebClientResponseException) error;
                    if (errorResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(errorResponse.getResponseBodyAsString());
                    }
                    return Mono.error(errorResponse);
                });
    }

    public Mono<ServerResponse> edit(ServerRequest request) {
        Mono<ProductDto> product = request.bodyToMono(ProductDto.class);
        String id = request.pathVariable("id");
        return errorHandler(product.flatMap(p -> productService.update(p, id)).flatMap(p -> ServerResponse
                .created(URI.create(RouterConfig.ROUTE_PREFIX.concat(p.getId()))).contentType(MediaType.APPLICATION_JSON).bodyValue(p)));

    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return errorHandler(productService.delete(id).then(ServerResponse.noContent().build()));
    }

    public Mono<ServerResponse> upload(ServerRequest request) {
        String id = request.pathVariable("id");
        return errorHandler(request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file")).cast(FilePart.class)
                .flatMap(file -> productService.upload(file, id)).flatMap(p -> ServerResponse
                        .created(URI.create(RouterConfig.ROUTE_PREFIX.concat(id))).contentType(MediaType.APPLICATION_JSON).bodyValue(p)));
    }

    private static Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            WebClientResponseException errorResponse = (WebClientResponseException) error;
            if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                Map<String, Object> body = new HashMap<>();
                body.put("error", "Product not found: ".concat(errorResponse.getMessage()));
                body.put("timestamp", LocalDate.now());
                body.put("status", errorResponse.getStatusCode().value());
                return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(body);
            }
            return Mono.error(errorResponse);
        });
    }
}
