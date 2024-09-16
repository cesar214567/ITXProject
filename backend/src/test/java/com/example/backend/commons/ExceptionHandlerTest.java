package com.example.backend.commons;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebFluxTest
@ExtendWith(SpringExtension.class)
class ExceptionHandlerTest {

    private ExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ExceptionHandler();
    }


    @Test
    void handleConstraintViolationException() {
        ConstraintViolation<String> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Invalid field");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));
        StepVerifier.create(exceptionHandler.handleException(ex))
                        .expectNextMatches(serverResponse ->  serverResponse.statusCode()
                                .equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void handleDuplicateKeyException() {
        DuplicateKeyException ex = new DuplicateKeyException("duplicate");
        StepVerifier.create(exceptionHandler.handleException(ex))
                .expectNextMatches(serverResponse ->  serverResponse.statusCode()
                        .equals(HttpStatus.CONFLICT))
                .verifyComplete();
    }
    @Test
    void handleRandomException() {
        Exception ex = new Exception("random");
        StepVerifier.create(exceptionHandler.handleException(ex))
                .expectNextMatches(serverResponse ->  serverResponse.statusCode()
                        .equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

}
