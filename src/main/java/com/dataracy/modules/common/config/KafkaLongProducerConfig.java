package com.dataracy.modules.common.config;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaLongProducerConfig {

    @Value("${spring.kafka.bootstrap-servers:}")
    private String bootstrapServers;

    /**
     * String 키와 Long 값을 사용하는 Kafka 프로듀서를 위한 ProducerFactory 빈을 생성합니다.
     *
     * Kafka 프로듀서의 부트스트랩 서버, 직렬화 방식, ack 모드, 재시도 횟수 등의 설정을 포함합니다.
     *
     * @return String 키와 Long 값을 처리하는 Kafka ProducerFactory 인스턴스
     */
    @Bean
    public ProducerFactory<String, Long> longProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * String 키와 Long 값을 사용하는 Kafka 프로듀서를 위한 KafkaTemplate 빈을 생성합니다.
     *
     * @return String 키와 Long 값 타입의 KafkaTemplate 인스턴스
     */
    @Bean
    public KafkaTemplate<String, Long> longKafkaTemplate() {
        return new KafkaTemplate<>(longProducerFactory());
    }

    /**
     * Kafka Producer의 bootstrap 서버 설정이 비어 있는지 검증합니다.
     *
     * bootstrapServers 값이 비어 있으면 IllegalStateException을 발생시킵니다.
     *
     * @throws IllegalStateException Kafka Producer 설정이 올바르지 않은 경우
     */
    @PostConstruct
    public void validateProperties() {
        if (bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka Producer 설정이 올바르지 않습니다.");
        }
    }
}
