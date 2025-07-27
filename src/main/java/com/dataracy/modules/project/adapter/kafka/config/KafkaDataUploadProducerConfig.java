package com.dataracy.modules.project.adapter.kafka.config;

import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.producer.ProducerConfig;
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

    @Value("${spring.kafka.bootstrap-servers:}")
    private String bootstrapServers;

    /**
     * DataUploadEvent 메시지를 전송하기 위한 Kafka ProducerFactory 빈을 생성합니다.
     *
     * Kafka 프로듀서의 부트스트랩 서버, 직렬화 방식, ack, 재시도, 배치 크기 등 주요 설정을 포함합니다.
     *
     * @return DataUploadEvent 타입의 메시지를 처리하는 ProducerFactory 인스턴스
     */
    @Bean
    public ProducerFactory<String, DataUploadEvent> dataUploadEventProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        config.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * DataUploadEvent 메시지를 전송하기 위한 KafkaTemplate 빈을 생성합니다.
     *
     * @return DataUploadEvent 타입의 메시지를 전송할 수 있는 KafkaTemplate 인스턴스
     */
    @Bean
    public KafkaTemplate<String, DataUploadEvent> dataUploadEventKafkaTemplate() {
        return new KafkaTemplate<>(dataUploadEventProducerFactory());
    }

    /**
     * Kafka bootstrap 서버 설정이 비어 있는지 검증하며, 누락 시 예외를 발생시킵니다.
     *
     * Kafka 프로듀서가 정상적으로 동작하기 위해 필수적인 spring.kafka.bootstrap-servers 프로퍼티가 설정되어 있는지 확인합니다.
     *
     * @throws IllegalStateException spring.kafka.bootstrap-servers 프로퍼티가 비어 있거나 누락된 경우 발생합니다.
     */
    @PostConstruct
    public void validateProperties() {
        if (bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka bootstrap servers 설정이 누락되었습니다. spring.kafka.bootstrap-servers 프로퍼티를 확인해주세요.");
        }
    }
}
