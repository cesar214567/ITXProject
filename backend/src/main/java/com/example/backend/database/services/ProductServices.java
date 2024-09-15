package com.example.backend.database.services;

import com.example.backend.commons.TechLogger;
import com.example.backend.database.mappers.ProductDAOMapper;
import com.example.backend.database.repositories.ProductRepository;
import com.example.backend.database.services.strategies.SortByMultiQueryStrategy;
import com.example.backend.database.services.strategies.SortingStrategy;
import com.example.backend.domain.models.MultiQuery;
import com.example.backend.domain.models.Product;
import com.example.backend.domain.models.Query;
import com.example.backend.domain.models.gateways.ProductGateway;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;


@Component
@AllArgsConstructor
public class ProductServices implements ProductGateway {
    private ProductRepository productRepository;
    private TechLogger techLogger;
    private final ReactiveMongoTemplate mongoTemplate;
    SortByMultiQueryStrategy multiQueryStrategy;
    private final Map<String, SortingStrategy> strategyMap;
    @Override
    public Mono<Product> save(Product product) {

        return Mono.just(ProductDAOMapper.mapProductDAO(product))
                .flatMap(productRepository::save)
                .map(ProductDAOMapper::mapDAOToProduct)
                .doOnError(techLogger::logInfo);
    }
    @Override
    public Flux<Product> findProductsOrderedByQuery(Query query) {
        var strategy = strategyMap.getOrDefault(query.getSortBy(),null);
        if(strategy==null){
            return Flux.empty();
        }
        return strategy.applySorting(query.getAsc())
                .map(ProductDAOMapper::mapDAOToProduct)
                .doOnError(techLogger::logInfo);

    }


    @Override
    public Flux<Product> findProductsOrderedByMultiQuery(MultiQuery multiQuery) {
        return multiQueryStrategy.applySorting(multiQuery)
                .map(ProductDAOMapper::mapDAOToProduct)
                .doOnError(techLogger::logInfo);


    }
}
