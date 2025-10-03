/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.config.web;

import java.lang.reflect.Type;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * multipart/form-data 처리 시 Jackson이 처리하지 않도록 한다. Jackson은 파일과 json이 함께 있을때 dto json을 octec-stream으로
 * 간주하여 에러가 발생할 수 있다.
 */
@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
  public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
    super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
  }

  @Override
  protected boolean canWrite(MediaType mediaType) {
    return false;
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    return false;
  }

  @Override
  public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
    return false;
  }
}
