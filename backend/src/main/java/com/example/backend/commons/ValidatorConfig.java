package com.example.backend.commons;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ValidatorConfig {
    public final Validator validator;
    public final TechLogger techLogger;

    public ValidatorConfig(TechLogger techLogger) {
        this.techLogger = techLogger;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public <T> Mono<T> validate(T object){
        return Mono.just(validator.validate(object))
                .doOnError(techLogger::logInfo)
                .flatMap(violations -> {
                    if (violations.isEmpty()){
                        return Mono.just(object);
                    }
                    return Mono.error(new ConstraintViolationException("Los campos no pueden ser nulos",violations));
                })
                .doOnError(techLogger::logInfo);
    }
}
