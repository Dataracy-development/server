package com.dataracy.config;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;

import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;

/** 테스트용 설정 클래스 외부 의존성(Kafka, Redis 등)을 모킹하여 테스트 환경을 격리합니다. */
@TestConfiguration
@Profile("test")
public class TestConfig {

  /** Kafka Producer 모킹 */
  @Bean
  @Primary
  @SuppressWarnings("unchecked")
  public KafkaTemplate<String, String> kafkaTemplate() {
    return mock(KafkaTemplate.class);
  }

  @Bean
  @Primary
  @SuppressWarnings("unchecked")
  public KafkaTemplate<String, Long> longKafkaTemplate() {
    return mock(KafkaTemplate.class);
  }

  @Bean
  @Primary
  @SuppressWarnings("unchecked")
  public KafkaTemplate<String, BehaviorLog> behaviorLogKafkaTemplate() {
    return mock(KafkaTemplate.class);
  }

  /** Behavior Log Producer 모킹 */
  @Bean
  @Primary
  public BehaviorLogSendProducerPort behaviorLogSendProducerPort() {
    return mock(BehaviorLogSendProducerPort.class);
  }
}
