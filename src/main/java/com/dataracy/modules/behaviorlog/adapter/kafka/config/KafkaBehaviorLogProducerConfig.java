package com.dataracy.modules.behaviorlog.adapter.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;

import jakarta.annotation.PostConstruct;

@Configuration
public class KafkaBehaviorLogProducerConfig {

  private static final int BUFFER_MEMORY_CONFIG = 67_108_864;

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  /**
   * BehaviorLog 메시지 전송을 위한 Kafka ProducerFactory 빈을 생성합니다.
   *
   * <p>Kafka 서버 주소와 직렬화 설정이 적용된 ProducerFactory를 반환하며, 이 Factory는 String 타입의 키와 BehaviorLog 타입의 값을
   * 가진 메시지를 Kafka로 전송할 수 있도록 구성됩니다.
   *
   * @return BehaviorLog 메시지 전송이 가능한 ProducerFactory 인스턴스
   */
  @Bean
  public ProducerFactory<String, BehaviorLog> behaviorLogProducerFactory() {
    Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    // ----- 성능 최적화 설정 -----
    config.put(ProducerConfig.ACKS_CONFIG, "1"); // 성능 향상: 리더만 확인 (로그는 중요도 낮음)
    config.put(ProducerConfig.RETRIES_CONFIG, 2); // 재시도 횟수 감소
    config.put(ProducerConfig.BATCH_SIZE_CONFIG, 32_768); // 배치 크기 증가 (처리량 향상)
    config.put(ProducerConfig.LINGER_MS_CONFIG, 5); // 배칭 지연 시간 증가
    config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, BUFFER_MEMORY_CONFIG); // 버퍼 메모리 증가 (64MB)
    config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // 동시 요청 수 증가

    return new DefaultKafkaProducerFactory<>(config);
  }

  /**
   * BehaviorLog 메시지를 Kafka로 전송할 수 있는 KafkaTemplate 빈을 생성합니다.
   *
   * @return BehaviorLog 객체를 전송하는 KafkaTemplate 인스턴스
   */
  @Bean
  public KafkaTemplate<String, BehaviorLog> behaviorLogKafkaTemplate() {
    return new KafkaTemplate<>(behaviorLogProducerFactory());
  }

  @PostConstruct
  public void validateProperties() {
    if (bootstrapServers.isBlank()) {
      throw new IllegalStateException(
          "Kafka bootstrap servers 설정이 누락되었습니다. spring.kafka.bootstrap-servers 프로퍼티를 확인해주세요.");
    }
  }
}
