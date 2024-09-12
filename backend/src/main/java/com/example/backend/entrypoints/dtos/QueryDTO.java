package com.example.backend.entrypoints.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class QueryDTO {
    @NotNull(message = "SortBy cannot be null")
    private String sortBy;
    @NotNull(message = "asc cannot be null")
    private Boolean asc;
}
