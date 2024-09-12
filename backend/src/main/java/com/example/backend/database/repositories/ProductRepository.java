package com.example.backend.database.repositories;

import com.example.backend.database.entities.ProductDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<ProductDao, UUID> {
}