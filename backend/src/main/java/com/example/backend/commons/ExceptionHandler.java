package com.example.backend.commons;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ExceptionHandler {

    public Mono<ServerResponse> handleException(Exception ex) {
        if (ex instanceof ResponseStatusException responseStatusException) {
            return handleResponseStatusException(responseStatusException);
        } else if (ex instanceof ConstraintViolationException constraintViolationException) {
            return handleConstraintViolationException(constraintViolationException);
        } else if (ex instanceof TransactionSystemException transactionSystemException) {
            return handleTransactionSystemException(transactionSystemException);
        } else if (ex instanceof DuplicateKeyException) {
            return handleFoundDuplicated();
        } else {
            // Handle other exceptions

            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue("Internal Server Error");
        }
    }

    private Mono<ServerResponse> handleResponseStatusException(ResponseStatusException ex) {
        return ServerResponse.status(ex.getStatusCode())
                .bodyValue(Objects.requireNonNull(ex.getReason()));
    }

    private Mono<ServerResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(Map.of(
                "error",ex.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .toList()
        ));
    }

    private Mono<ServerResponse> handleTransactionSystemException(TransactionSystemException ex) {
        Throwable rootCause = ex.getMostSpecificCause();
        if (rootCause instanceof ConstraintViolationException constraintViolationException) {
            return handleConstraintViolationException(constraintViolationException);
        } else {
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue("Transaction System Exception: " + ex.getMessage());
        }
    }

    public Mono<ServerResponse> handleFoundDuplicated(){
        return ServerResponse.status(HttpStatus.CONFLICT).bodyValue(Map.of(
                "error","founded a duplicate"
        ));
    }
}
