package com.example.backend.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class TechLogger {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    public void logInfo(Object object) {
        try{
            this.logger.info(mapper.writeValueAsString(object));
        }catch (JsonProcessingException e){
            this.logger.warning("ocurrio un error");
        }
    }
}
