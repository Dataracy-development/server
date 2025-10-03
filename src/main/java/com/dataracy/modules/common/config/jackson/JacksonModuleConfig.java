/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.config.jackson;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JacksonModuleConfig {
  private final ObjectMapper objectMapper;

  /**
   * ObjectMapper에 Java 8 날짜 및 시간 API 처리를 위한 JavaTimeModule을 등록합니다.
   *
   * <p>이미 JavaTimeModule이 등록되어 있지 않은 경우에만 추가로 등록합니다.
   */
  @PostConstruct
  public void registerModules() {
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    if (!objectMapper.getRegisteredModuleIds().contains(javaTimeModule.getModuleName())) {
      objectMapper.registerModule(javaTimeModule);
    }
  }
}
