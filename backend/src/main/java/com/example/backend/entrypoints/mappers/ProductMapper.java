package com.example.backend.entrypoints.mappers;

import com.example.backend.domain.models.Product;
import com.example.backend.domain.models.Query;
import com.example.backend.entrypoints.dtos.CreateProductDTO;
import com.example.backend.entrypoints.dtos.QueryDTO;
import jakarta.validation.Valid;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.server.ServerRequest;

@UtilityClass
public class ProductMapper {
    public static Product mapCreateDTOtoProduct( CreateProductDTO productDTO){
        return Product.builder()
                .name(productDTO.getName())
                .stock(productDTO.getStock())
                .salesUnit(productDTO.getSalesUnit())
                .build();
    }

    public static Query mapQueryDTOToQuery(ServerRequest serverRequest){
        return Query.builder()
                .sortBy(serverRequest.queryParam("sortBy").orElse(null))
                .asc(Boolean.valueOf(serverRequest.queryParam("asc").orElse("true")))
                .build();
    }

}
