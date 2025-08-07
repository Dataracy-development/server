package com.dataracy.modules.common.config.adapter.kafka;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.producer.ProducerConfig;
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
public class KafkaStringProducerConfig {

    @Value("${spring.kafka.bootstrap-servers:}")
    private String bootstrapServers;

    /**
     * Kafka 프로듀서를 위한 String 타입 키와 값을 처리하는 ProducerFactory 빈을 생성합니다.
     *
     * Kafka 서버 주소, String 직렬화, 전체 커밋 확인(acks=all), 재시도 횟수(3회) 등의 설정이 적용됩니다.
     *
     * @return String 키와 값을 처리하는 Kafka ProducerFactory 인스턴스
     */
    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * 문자열 키와 값을 사용하는 Kafka 메시지 전송을 위한 KafkaTemplate 빈을 생성합니다.
     *
     * @return 문자열 키와 값을 처리하는 KafkaTemplate 인스턴스
     */
    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }

    @PostConstruct
    public void validateProperties() {
        if (bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka Producer 설정이 올바르지 않습니다.");
        }
    }
}
