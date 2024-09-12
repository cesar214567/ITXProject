package com.example.backend.domain.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Query {
    private String sortBy;
    private Boolean asc;
}
