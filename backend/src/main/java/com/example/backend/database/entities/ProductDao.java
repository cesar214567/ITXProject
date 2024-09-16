package com.example.backend.database.entities;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)  // This enables the builder
@Document(collection = "products")
public class ProductDao {
    @Id
    @Field(name = "_id")  // This is optional as @Id already maps to _id by default
    private String id;
    @Indexed(unique = true)
    private String name;
    private Integer salesUnit;
    private Map<String, Integer> stock;
    private Double weightedValue;
    private Double stockSum;
}
