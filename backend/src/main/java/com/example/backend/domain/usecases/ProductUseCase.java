package com.example.backend.domain.usecases;

import com.example.backend.domain.models.Product;
import com.example.backend.domain.models.Query;
import com.example.backend.domain.models.gateways.ProductGateway;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class ProductUseCase {
    private final ProductGateway productGateway;
    public Mono<Product> saveOrUpdateProduct(@Valid Product product){
        return productGateway.save(product);
    }

    public Flux<Product> findProductsOrderedBy(@Valid Query query){
        return productGateway.findProductsOrderedBy(query);
    }

}
