package com.dataracy.modules.common.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;

@Configuration
public class JacksonModuleConfig {

    private final ObjectMapper objectMapper;

    public JacksonModuleConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void registerModules() {
        objectMapper.registerModule(new JavaTimeModule());
    }
}
