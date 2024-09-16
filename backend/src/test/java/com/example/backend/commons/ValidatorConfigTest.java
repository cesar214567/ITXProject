package com.example.backend.commons;

import com.example.backend.entrypoints.dtos.CreateProductDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;
@WebFluxTest
@ExtendWith(SpringExtension.class)
class ValidatorConfigTest {

    private ValidatorConfig validatorConfig;
    private TechLogger techLogger;
    private Validator validator;

    @BeforeEach
    void setUp() {
        techLogger = mock(TechLogger.class);
        validator = mock(Validator.class);
        validatorConfig = new ValidatorConfig(techLogger);
    }

    @Test
    void validateValidObject() {
        // Arrange
        Object validObject = new Object();
        when(validator.validate(validObject)).thenReturn(Set.of()); // No violations

        // Act
        Mono<Object> result = validatorConfig.validate(validObject);

        // Assert
        StepVerifier.create(result)
                .expectNext(validObject)
                .verifyComplete();

        verify(techLogger, never()).logInfo(any()); // Logger should not be called
    }

    @Test
    void validateInvalidObject() {
        // Arrange
        CreateProductDTO invalidObject = CreateProductDTO.builder().build();
        ConstraintViolation<CreateProductDTO> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Field cannot be null");
        when(validator.validate(invalidObject)).thenReturn(Set.of(violation)); // Violation present

        // Act
        Mono<Object> result = validatorConfig.validate(invalidObject);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assert throwable instanceof ConstraintViolationException;
                    ConstraintViolationException exception = (ConstraintViolationException) throwable;
                    assertEquals("Los campos no pueden ser nulos", exception.getMessage());
                    assertFalse(exception.getConstraintViolations().isEmpty());
                })
                .verify();

        verify(techLogger, times(1)).logInfo(any()); // Logger should be called once
    }
}
