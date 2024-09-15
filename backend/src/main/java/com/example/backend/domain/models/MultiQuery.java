package com.example.backend.domain.models;

import com.example.backend.entrypoints.dtos.MultiQueryDTO;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MultiQuery {
    private List<QueryWeight> queryAttributes;
    private Boolean asc;

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class QueryWeight{
        private String attribute;
        private Double weight;
    }
}
