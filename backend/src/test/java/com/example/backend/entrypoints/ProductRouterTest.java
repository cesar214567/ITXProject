package com.example.backend.entrypoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class ProductRouterTest {

    @Mock
    private ProductHandler productHandler;

    @InjectMocks
    private ProductRouter productRouter;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Build WebTestClient to test routes
        RouterFunction<ServerResponse> routerFunction = productRouter.userRouterFunction(productHandler);
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void testCreateProductRoute() {
        // Arrange
        Mono<ServerResponse> mockResponse = ServerResponse.ok().build();
        when(productHandler.create(any())).thenReturn(mockResponse);

        // Act & Assert
        webTestClient.post()
                .uri("/v1/products")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetProductsByQueryRoute() {
        // Arrange
        Mono<ServerResponse> mockResponse = ServerResponse.ok().build();
        when(productHandler.getProductsByQuery(any())).thenReturn(mockResponse);

        // Act & Assert
        webTestClient.get()
                .uri("/v1/products/index")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetProductsByMultiQueryRoute() {
        // Arrange
        Mono<ServerResponse> mockResponse = ServerResponse.ok().build();
        when(productHandler.getProductsByMultiQuery(any())).thenReturn(mockResponse);

        // Act & Assert
        webTestClient.post()
                .uri("/v2/products/index")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
}
