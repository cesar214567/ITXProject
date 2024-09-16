package com.example.backend.entrypoints;

import com.example.backend.commons.ExceptionHandler;
import com.example.backend.commons.TechLogger;
import com.example.backend.commons.ValidatorConfig;
import com.example.backend.domain.models.MultiQuery;
import com.example.backend.domain.models.Product;
import com.example.backend.domain.models.Query;
import com.example.backend.domain.usecases.ProductUseCase;
import com.example.backend.entrypoints.dtos.CreateProductDTO;
import com.example.backend.entrypoints.dtos.MultiQueryDTO;
import com.example.backend.entrypoints.dtos.QueryDTO;
import com.example.backend.entrypoints.mappers.ProductMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    @Mock
    private ProductUseCase productUseCase;

    @Mock
    private TechLogger techLogger;

    @Mock
    private ExceptionHandler exceptionHandler;

    @Mock
    private ValidatorConfig validatorConfig;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private ProductHandler productHandler;

    @Mock
    private ServerResponse.BodyBuilder serverResponse;

    @BeforeAll
    private static void setup(){
        Product product = new Product();
        Query query = new Query();
        MultiQuery multiQuery = new MultiQuery();
        var mock = mockStatic(ProductMapper.class);
        mock.when(()->ProductMapper.mapCreateDTOtoProduct(any(CreateProductDTO.class)))
            .thenReturn(product);
        mock.when(()->ProductMapper.mapQueryDTOToQuery(any(ServerRequest.class)))
            .thenReturn(query);
        mock.when(()->ProductMapper.mapQueryDTOToMultiQuery(any(MultiQueryDTO.class)))
                .thenReturn(multiQuery);
    }

    @Test
    void shouldCreateProductSuccessfully() {
        CreateProductDTO createProductDTO = new CreateProductDTO();  // Create a dummy DTO
        Product product = new Product();  // Create a dummy Product object
        // Arrange
        // Mock serverRequest.bodyToMono to return the CreateProductDTO
        when(serverRequest.bodyToMono(any(Class.class))).thenReturn(Mono.just(createProductDTO));

        // Mock validatorConfig.validate to return a Mono with the same DTO
        when(validatorConfig.validate(any(CreateProductDTO.class))).thenReturn(Mono.just(createProductDTO));

        // Mock productUseCase.saveOrUpdateProduct to return a Mono with the product
        when(productUseCase.saveOrUpdateProduct(any(Product.class))).thenReturn(Mono.just(product));


        // Act & Assert
        StepVerifier.create(productHandler.create(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();

        // Verify interactions
        verify(serverRequest, times(1)).bodyToMono(CreateProductDTO.class);
        verify(validatorConfig, times(1)).validate(createProductDTO);
        verify(productUseCase, times(1)).saveOrUpdateProduct(any(Product.class));
    }

    @Test
    void shouldHandleValidationFailure() {
        // Arrange
        CreateProductDTO createProductDTO = new CreateProductDTO();
        when(serverRequest.bodyToMono(CreateProductDTO.class)).thenReturn(Mono.just(createProductDTO));
        when(validatorConfig.validate(any(CreateProductDTO.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Validation failed")));

        when(exceptionHandler.handleException(any(IllegalArgumentException.class)))
                .thenReturn(Mono.just(ServerResponse.badRequest().build().block()));

        // Act & Assert
        StepVerifier.create(productHandler.create(serverRequest))
                .expectNextMatches(response -> response.statusCode().is4xxClientError())
                .verifyComplete();

        verify(validatorConfig, times(1)).validate(any(CreateProductDTO.class));
        verify(exceptionHandler, times(1)).handleException(any(IllegalArgumentException.class));
    }

    @Test
    void shouldGetProductsByQuerySuccessfully() {
        // Arrange
        Query query = Query.builder().build();
        Product product = new Product();
        when(productUseCase.findProductsOrderedByQuery(any(Query.class)))
                .thenReturn(Flux.just(product));
        // Act & Assert
        StepVerifier.create(productHandler.getProductsByQuery(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(productUseCase, times(1)).findProductsOrderedByQuery(any(Query.class));
    }

    @Test
    void shouldGetProductsByMultiQuerySuccessfully() {
        // Arrange
        MultiQueryDTO multiQueryDTO = new MultiQueryDTO();
        Product product = new Product();

        when(serverRequest.bodyToMono(MultiQueryDTO.class)).thenReturn(Mono.just(multiQueryDTO));
        when(productUseCase.findProductsOrderedByMultiQuery(any(MultiQuery.class))).thenReturn(Flux.just(product));

        // Act & Assert
        StepVerifier.create(productHandler.getProductsByMultiQuery(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(productUseCase, times(1)).findProductsOrderedByMultiQuery(any(MultiQuery.class));
    }

    @Test
    void shouldHandleExceptionDuringGetProductsByQuery() {
        // Arrange
        when(productUseCase.findProductsOrderedByQuery(any(Query.class)))
                .thenReturn(Flux.error(new Exception()));

        when(exceptionHandler.handleException(any(Exception.class)))
                .thenReturn(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // Act & Assert
        StepVerifier.create(productHandler.getProductsByQuery(serverRequest))
                .expectNextMatches(response -> response.statusCode().is5xxServerError())
                .verifyComplete();

        verify(productUseCase, times(1)).findProductsOrderedByQuery(any(Query.class));
        verify(exceptionHandler, times(1)).handleException(any(Exception.class));
    }
    @Test
    void shouldHandleExceptionDuringGetProductsByMultiQuery() {
        // Arrange
        when(productUseCase.findProductsOrderedByMultiQuery(any(MultiQuery.class)))
                .thenReturn(Flux.error(new Exception()));

        when(exceptionHandler.handleException(any(Exception.class)))
                .thenReturn(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // Act & Assert
        StepVerifier.create(productHandler.getProductsByMultiQuery(serverRequest))
                .expectNextMatches(response -> response.statusCode().is5xxServerError())
                .verifyComplete();

        verify(productUseCase, times(1)).findProductsOrderedByMultiQuery(any(MultiQuery.class));
        verify(exceptionHandler, times(1)).handleException(any(Exception.class));
    }
}
