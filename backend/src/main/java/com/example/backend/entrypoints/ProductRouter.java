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
                route(POST("/products"), productHandler::create)
                .andRoute(POST("/products/index"), productHandler::getProducts);
                //.andRoute(GET("/products"), productHandler::updateUser);

    }
}
