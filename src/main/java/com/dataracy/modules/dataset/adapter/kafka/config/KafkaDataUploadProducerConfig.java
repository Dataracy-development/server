package com.dataracy.modules.dataset.adapter.kafka.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.record.CompressionType;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;

import jakarta.annotation.PostConstruct;

@Configuration
public class KafkaDataUploadProducerConfig {

  private static final int BATCH_SIZE = 64_000;

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public ProducerFactory<String, DataUploadEvent> dataUploadEventProducerFactory() {
    Map<String, Object> config = new HashMap<>();
    // 필수 엔드포인트
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    // 직렬화 (key=String, value=JSON)
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    // ----- 안정성/정합성 핵심 (성능 최적화) -----
    config.put(ProducerConfig.ACKS_CONFIG, "1"); // 성능 향상: 리더만 확인 (데이터 업로드는 중요도 낮음)
    config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 멱등 프로듀서(중복 방지)
    config.put(
        ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // 성능 향상: 동시 요청 수 증가 (순서 보장 포기)

    // ----- 재시도/타임아웃/성능 최적화 -----
    config.put(ProducerConfig.RETRIES_CONFIG, 3); // 재시도 횟수 감소 (성능 향상)
    config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15_000); // 타임아웃 단축
    config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 60_000); // 배송 타임아웃 단축
    config.put(ProducerConfig.LINGER_MS_CONFIG, 10); // 배칭 지연 시간 증가 (처리량 향상)
    config.put(
        ProducerConfig.COMPRESSION_TYPE_CONFIG,
        CompressionType.LZ4.name().toLowerCase(Locale.ENGLISH));

    // ----- 성능 최적화 배치/버퍼 설정 -----
    config.put(ProducerConfig.BATCH_SIZE_CONFIG, BATCH_SIZE); // 배치 크기 증가 (처리량 향상)
    config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 134_217_728); // 버퍼 메모리 증가 (128MB)

    // ----- JSON 타입 헤더 비활성(전역 yml과 일치) -----
    // 컨슈머가 타입 헤더 없이도 역직렬화하도록 운용 중이면 명시적으로 false
    config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean
  public KafkaTemplate<String, DataUploadEvent> dataUploadEventKafkaTemplate() {
    return new KafkaTemplate<>(dataUploadEventProducerFactory());
  }

  @PostConstruct
  public void validateProperties() {
    if (bootstrapServers == null || bootstrapServers.isBlank()) {
      throw new IllegalStateException(
          "Kafka bootstrap servers 설정이 누락되었습니다. spring.kafka.bootstrap-servers 프로퍼티를 확인해주세요.");
    }
  }
}
