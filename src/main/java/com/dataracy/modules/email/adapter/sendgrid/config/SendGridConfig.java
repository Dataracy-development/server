/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.email.adapter.sendgrid.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sendgrid.SendGrid;
import jakarta.annotation.PostConstruct;

/** SendGrid Client 설정 */
@Configuration
public class SendGridConfig {
  @Value("${sendgrid.api-key:}")
  private String sendGridApiKey;

  @Bean
  public SendGrid sendGrid() {
    return new SendGrid(sendGridApiKey);
  }

  @PostConstruct
  public void validateProperties() {
    if (sendGridApiKey.isBlank()) {
      throw new IllegalStateException("SendGrid 설정이 올바르지 않습니다.");
    }
  }
}
