package com.example.backend.entrypoints;

import com.example.backend.commons.ExceptionHandler;
import com.example.backend.commons.TechLogger;
import com.example.backend.commons.ValidatorConfig;
import com.example.backend.domain.models.Product;
import com.example.backend.domain.usecases.ProductUseCase;
import com.example.backend.entrypoints.dtos.CreateProductDTO;
import com.example.backend.entrypoints.dtos.MultiQueryDTO;
import com.example.backend.entrypoints.dtos.QueryDTO;
import com.example.backend.entrypoints.mappers.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class ProductHandler {
    private final ProductUseCase productUseCase;
    private final TechLogger techLogger;
    private final ExceptionHandler exceptionHandler;
    private final ValidatorConfig validatorConfig;
    public Mono<ServerResponse> create(ServerRequest serverRequest){
        return serverRequest.bodyToMono(CreateProductDTO.class)
                .flatMap(validatorConfig::validate)
                .map(ProductMapper::mapCreateDTOtoProduct)
                .flatMap(productUseCase::saveOrUpdateProduct)
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(Exception.class, exceptionHandler::handleException);
    }

    public Mono<ServerResponse> getProductsByQuery(ServerRequest serverRequest){
        return Mono.just(serverRequest)
                .map(ProductMapper::mapQueryDTOToQuery)
                .flatMap(query -> ServerResponse.ok().body(productUseCase.findProductsOrderedByQuery(query),Product.class))
                .doOnError(techLogger::logInfo)
                .onErrorResume(Exception.class, exceptionHandler::handleException);
    }

    public Mono<ServerResponse> getProductsByMultiQuery(ServerRequest serverRequest){
        return serverRequest.bodyToMono(MultiQueryDTO.class)
                .map(ProductMapper::mapQueryDTOToMultiQuery)
                .flatMap(query -> ServerResponse.ok().body(productUseCase.findProductsOrderedByMultiQuery(query),Product.class))
                .doOnError(techLogger::logInfo)
                .onErrorResume(Exception.class, exceptionHandler::handleException);
    }

}
