/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.config.adapter.kafka;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import jakarta.annotation.PostConstruct;

@Configuration
public class KafkaStringProducerConfig extends AbstractKafkaProducerConfig<String> {

  // Micrometer 관측 활성화 여부 (메트릭 수집)
  @Value("${spring.kafka.producer.string.observation-enabled:false}")
  private Boolean observationEnabled;

  /** bootstrap-servers 유효성 체크 */
  @PostConstruct
  public void validate() {
    validateBootstrapServers();
  }

  /** key 직렬화: String */
  @Override
  protected Class<?> keySerializer() {
    return StringSerializer.class;
  }

  /** value 직렬화: String */
  @Override
  protected Class<?> valueSerializer() {
    return StringSerializer.class;
  }

  /**
   * String → String 템플릿 (AbstractKafkaProducerConfig의 base 옵션: acks=all, enable.idempotence=true,
   * max.in.flight=1, retries/timeout/linger/compression 등 적용)
   *
   * <p>Micrometer 관측: Prometheus 메트릭 수집을 위해 운영 환경에서 활성화 권장
   */
  @Bean(name = "stringKafkaTemplate")
  public KafkaTemplate<String, String> stringKafkaTemplate() {
    KafkaTemplate<String, String> tpl = new KafkaTemplate<>(producerFactory());
    tpl.setObservationEnabled(observationEnabled);
    return tpl;
  }
}
