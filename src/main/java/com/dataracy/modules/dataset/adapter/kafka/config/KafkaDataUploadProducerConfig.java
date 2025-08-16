package com.dataracy.modules.dataset.adapter.kafka.config;

import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;
import jakarta.annotation.PostConstruct;
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

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaDataUploadProducerConfig {

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

        // ----- 안정성/정합성 핵심 -----
        config.put(ProducerConfig.ACKS_CONFIG, "all");                    // 리더+팔로워 커밋까지 대기
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);       // 멱등 프로듀서(중복 방지)
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1); // 재시도 시 순서 역전 방지(주문 중요시 1)

        // ----- 재시도/타임아웃/성능 -----
        config.put(ProducerConfig.RETRIES_CONFIG, 10);                    // 브로커/네트워크 일시 오류 재시도
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30_000);
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120_000);   // request.timeout + linger를 덮는 충분한 값
        config.put(ProducerConfig.LINGER_MS_CONFIG, 5);                   // 소량 TPS에서 배칭 이득
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, CompressionType.LZ4.name().toLowerCase());

        // ----- JSON 타입 헤더 비활성(전역 yml과 일치) -----
        // 컨슈머가 타입 헤더 없이도 역직렬화하도록 운용 중이면 명시적으로 false
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        // (선택) 배치/버퍼: 굳이 오버라이드 안 해도 됩니다. 필요한 경우에만 조정
        // config.put(ProducerConfig.BATCH_SIZE_CONFIG, 32_768);
        // config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67_108_864);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, DataUploadEvent> dataUploadEventKafkaTemplate() {
        return new KafkaTemplate<>(dataUploadEventProducerFactory());
    }

    @PostConstruct
    public void validateProperties() {
        if (bootstrapServers == null || bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka bootstrap servers 설정이 누락되었습니다. spring.kafka.bootstrap-servers 프로퍼티를 확인해주세요.");
        }
    }
}
