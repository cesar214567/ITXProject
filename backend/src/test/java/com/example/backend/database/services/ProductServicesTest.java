package com.example.backend.database.services;

import com.example.backend.commons.TechLogger;
import com.example.backend.database.entities.ProductDao;
import com.example.backend.database.mappers.ProductDAOMapper;
import com.example.backend.database.repositories.ProductRepository;
import com.example.backend.database.services.strategies.SortByMultiQueryStrategy;
import com.example.backend.database.services.strategies.SortingStrategy;
import com.example.backend.domain.models.MultiQuery;
import com.example.backend.domain.models.Product;
import com.example.backend.domain.models.Query;
import com.mongodb.*;
import org.bson.BsonDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductServicesTest {

    private ProductServices productServices;
    private ProductRepository productRepository;
    private TechLogger techLogger;
    private ReactiveMongoTemplate mongoTemplate;
    private SortByMultiQueryStrategy multiQueryStrategy;
    private Map<String, SortingStrategy> strategyMap;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        techLogger = mock(TechLogger.class);
        mongoTemplate = mock(ReactiveMongoTemplate.class);
        multiQueryStrategy = mock(SortByMultiQueryStrategy.class);
        strategyMap = mock(Map.class);

        productServices = new ProductServices(productRepository, techLogger, mongoTemplate, multiQueryStrategy, strategyMap);
    }

    @Test
    void testSaveProductSuccess() {
        Product product = new Product();  // Initialize with relevant fields
        var productDAO = ProductDAOMapper.mapProductDAO(product);

        when(productRepository.save(productDAO)).thenReturn(Mono.just(productDAO));
        var mock = mockStatic(ProductDAOMapper.class);
        mock.when(()-> ProductDAOMapper.mapProductDAO(any(Product.class)))
            .thenReturn(productDAO);
        mock.when(()-> ProductDAOMapper.mapDAOToProduct(any(ProductDao.class)))
                .thenReturn(product);

        StepVerifier.create(productServices.save(product))
                .expectNext(product)
                .verifyComplete();

        verify(productRepository).save(productDAO);
        verify(techLogger, never()).logInfo(any(Throwable.class));
    }

    @Test
    void testSaveProductFailure() {
        Product product = new Product();  // Initialize with relevant fields
        var productDAO = ProductDAOMapper.mapProductDAO(product);
        Throwable exception = createDuplicateKeyException();

        when(productRepository.save(productDAO)).thenReturn(Mono.error(exception));

        StepVerifier.create(productServices.save(product))
                .expectError()
                .verify();
    }

    @Test
    void testFindProductsOrderedByQueryWithValidStrategy() {
        Query query = new Query();
        query.setSortBy("stockSum");
        query.setAsc(true);

        SortingStrategy sortingStrategy = mock(SortingStrategy.class);
        when(strategyMap.getOrDefault(any(String.class),eq(null))).thenReturn(sortingStrategy);
        when(sortingStrategy.applySorting(query.getAsc())).thenReturn(Flux.just(ProductDAOMapper.mapProductDAO(new Product())));

        StepVerifier.create(productServices.findProductsOrderedByQuery(query))
                .expectNextCount(1)
                .verifyComplete();

        verify(sortingStrategy).applySorting(query.getAsc());
        verify(techLogger, never()).logInfo(any(Throwable.class));
    }

    @Test
    void testFindProductsOrderedByQueryWithInvalidStrategy() {
        Query query = new Query();
        query.setSortBy("unknown");

        when(strategyMap.get(query.getSortBy())).thenReturn(null);

        StepVerifier.create(productServices.findProductsOrderedByQuery(query))
                .expectComplete()
                .verify();

        verify(strategyMap).getOrDefault(query.getSortBy(),null);
        verify(techLogger, never()).logInfo(any(Throwable.class));
    }

    @Test
    void testFindProductsOrderedByQueryWithError() {
        Query query = new Query();
        query.setSortBy("errorStrategy");
        query.setAsc(true);

        when(strategyMap.get(query.getSortBy())).thenReturn(null);

        StepVerifier.create(productServices.findProductsOrderedByQuery(query))
                .expectNextCount(0)
                .verifyComplete();

    }

    @Test
    void testFindProductsOrderedByMultiQuery() {
        MultiQuery multiQuery = new MultiQuery();
        when(multiQueryStrategy.applySorting(multiQuery))
                .thenReturn(Flux.just(ProductDAOMapper.mapProductDAO(new Product())));

        StepVerifier.create(productServices.findProductsOrderedByMultiQuery(multiQuery))
                .expectNextCount(1)
                .verifyComplete();

        verify(multiQueryStrategy).applySorting(multiQuery);
        verify(techLogger, never()).logInfo(any(Throwable.class));
    }

    @Test
    void testFindProductsOrderedByMultiQueryWithError() {
        MultiQuery multiQuery = new MultiQuery();
        Throwable exception = new RuntimeException("Error in MultiQuery");

        when(multiQueryStrategy.applySorting(multiQuery)).thenReturn(Flux.error(exception));

        StepVerifier.create(productServices.findProductsOrderedByMultiQuery(multiQuery))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Error in MultiQuery"))
                .verify();

        verify(multiQueryStrategy).applySorting(multiQuery);
        verify(techLogger).logInfo(exception);
    }

    public static DuplicateKeyException createDuplicateKeyException() {
        BsonDocument response = new BsonDocument();
        ServerAddress address = new ServerAddress("localhost", 27017);
        WriteConcernResult writeConcernResult = WriteConcernResult.unacknowledged();
        return new DuplicateKeyException(response,address, writeConcernResult);
    }
    public static MongoQueryException createMongoQueryException() {
        BsonDocument response = new BsonDocument();
        ServerAddress address = new ServerAddress("localhost", 27017);
        return new MongoQueryException(response,address);
    }

}
