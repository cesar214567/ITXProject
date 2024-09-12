package com.example.backend.database.services.strategies;

import com.example.backend.database.entities.ProductDao;
import reactor.core.publisher.Flux;

public interface SortingStrategy {
    Flux<ProductDao> applySorting(Boolean asc);
}