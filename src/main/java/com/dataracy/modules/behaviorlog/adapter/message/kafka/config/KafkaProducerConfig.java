package com.dataracy.modules.behaviorlog.adapter.message.kafka.config;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
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
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Kafka 프로듀서를 위한 ProducerFactory를 생성하여 반환합니다.
     *
     * Kafka 서버 주소, 키 직렬화, 값 직렬화 설정이 적용된 ProducerFactory를 제공합니다.
     *
     * @return Kafka에 BehaviorLog 메시지를 전송할 수 있는 ProducerFactory 인스턴스
     */
    @Bean
    public ProducerFactory<String, BehaviorLog> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        // Docker Kafka 주소
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * KafkaTemplate을 생성하여 Kafka에 BehaviorLog 메시지를 전송할 수 있도록 빈으로 등록합니다.
     *
     * @return BehaviorLog 타입의 메시지를 전송하는 KafkaTemplate 인스턴스
     */
    @Bean
    public KafkaTemplate<String, BehaviorLog> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
