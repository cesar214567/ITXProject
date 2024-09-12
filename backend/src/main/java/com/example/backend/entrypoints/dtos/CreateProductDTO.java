package com.example.backend.entrypoints.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateProductDTO {
    @NotNull(message = "Product name cannot be null")
    @Size(min = 2, message = "Product name should have at least 2 characters")
    private String name;
    @NotNull(message = "salesUnit cannot be null")
    private Integer salesUnit;
    @NotEmpty(message = "Map cannot be empty")
    private Map<String, Integer> stock;
}
