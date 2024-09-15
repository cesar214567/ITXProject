package com.example.backend.entrypoints.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MultiQueryDTO {
    @NotNull(message = "queryAttributes cannot be null")
    private List<QueryWeightDTO> queryAttributes;
    @NotNull(message = "asc cannot be null")
    private Boolean asc;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class QueryWeightDTO{
        private String attribute;
        private Double weight;
    }
}
