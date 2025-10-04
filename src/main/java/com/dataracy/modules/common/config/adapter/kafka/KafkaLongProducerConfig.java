package com.dataracy.modules.common.config.adapter.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import jakarta.annotation.PostConstruct;

@Configuration
public class KafkaLongProducerConfig extends AbstractKafkaProducerConfig<Long> {
  /**
   * Kafka 부트스트랩 서버 설정이 올바른지 검증합니다.
   *
   * <p>빈 생성 후 Kafka 연결 설정의 유효성을 확인합니다.
   */
  @PostConstruct
  public void validate() {
    validateBootstrapServers();
  }

  /**
   * Kafka 프로듀서의 키 직렬화 클래스로 StringSerializer를 지정합니다.
   *
   * @return String 타입 키에 사용할 직렬화 클래스
   */
  @Override
  protected Class<?> keySerializer() {
    return org.apache.kafka.common.serialization.StringSerializer.class;
  }

  /**
   * Kafka 값 직렬화기로 LongSerializer 클래스를 지정합니다.
   *
   * @return Kafka 값 직렬화기로 사용할 LongSerializer 클래스
   */
  @Override
  protected Class<?> valueSerializer() {
    return org.apache.kafka.common.serialization.LongSerializer.class;
  }

  /**
   * String 키와 Long 값을 사용하는 KafkaTemplate 빈을 생성하여 반환합니다.
   *
   * @return String 키와 Long 값을 처리하는 KafkaTemplate 인스턴스
   */
  @Bean
  public KafkaTemplate<String, Long> longKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
