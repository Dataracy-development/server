package com.dataracy.modules.data.adapter.message.kafka.config;

import com.dataracy.modules.data.domain.model.event.DataUploadEvent;
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

    @Bean
    public KafkaTemplate<String, DataUploadEvent> dataUploadEventKafkaTemplate() {
        return new KafkaTemplate<>(dataUploadEventProducerFactory());
    }

    @PostConstruct
    public void validateProperties() {
        if (bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka bootstrap servers 설정이 누락되었습니다. spring.kafka.bootstrap-servers 프로퍼티를 확인해주세요.");
        }
    }
}
