package com.example.backend.entrypoints;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ProductRouter {
    @Bean
    public RouterFunction<ServerResponse> userRouterFunction(ProductHandler productHandler) {
        return RouterFunctions.
                route(POST("/v1/products"), productHandler::create)
                .andRoute(GET("/v1/products/index"), productHandler::getProductsByQuery)
                .andRoute(POST("/v2/products/index"), productHandler::getProductsByMultiQuery);

    }
}
