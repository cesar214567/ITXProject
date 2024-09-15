package com.example.backend.domain.models;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Product {
    private String id;
    private String name;
    private Integer salesUnit;
    private Map<String, Integer> stock;
    private Double weightedValue;
}
