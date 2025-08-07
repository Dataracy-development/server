package com.dataracy.modules.common.config.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JacksonModuleConfig {

    private final ObjectMapper objectMapper;

    /**
     * Jackson의 ObjectMapper에 Java 8 날짜 및 시간 API 지원을 위한 JavaTimeModule을 등록합니다.
     * 이미 해당 모듈이 등록되어 있지 않은 경우에만 추가로 등록합니다.
     */
    @PostConstruct
    public void registerModules() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        if (!objectMapper.getRegisteredModuleIds().contains(javaTimeModule.getModuleName())) {
            objectMapper.registerModule(javaTimeModule);
        }
    }
}
