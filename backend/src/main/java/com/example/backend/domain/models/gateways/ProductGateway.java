package com.example.backend.domain.models.gateways;

import com.example.backend.domain.models.Product;
import com.example.backend.domain.models.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductGateway {
    public Mono<Product> save(Product product);
    public Flux<Product> findProductsOrderedBy(Query query);
}
