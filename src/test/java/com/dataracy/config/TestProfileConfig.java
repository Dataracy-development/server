package com.dataracy.config;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;

import com.dataracy.modules.auth.application.port.out.token.ManageRefreshTokenPort;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import com.dataracy.modules.email.application.port.out.code.ManageEmailCodePort;

/** 테스트 프로파일용 설정 모든 외부 의존성을 모킹합니다. */
@TestConfiguration
@Profile("test")
public class TestProfileConfig {

  // Kafka 모킹
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

  // Port 모킹
  @Bean
  @Primary
  public BehaviorLogSendProducerPort behaviorLogSendProducerPort() {
    return mock(BehaviorLogSendProducerPort.class);
  }

  @Bean
  @Primary
  public ManageEmailCodePort manageEmailCodePort() {
    return mock(ManageEmailCodePort.class);
  }

  @Bean
  @Primary
  public ManageRefreshTokenPort manageRefreshTokenPort() {
    return mock(ManageRefreshTokenPort.class);
  }
}
