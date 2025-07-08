package com.dataracy.modules.behaviorlog.adapter.message.kafka.config;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import org.apache.kafka.common.serialization.StringSerializer;
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
     * BehaviorLog 메시지 전송을 위한 Kafka ProducerFactory 빈을 생성합니다.
     *
     * Kafka 서버 주소와 직렬화 설정이 적용된 ProducerFactory를 반환하며, 이 Factory는 String 타입의 키와 BehaviorLog 타입의 값을 가진 메시지를 Kafka로 전송할 수 있도록 구성됩니다.
     *
     * @return BehaviorLog 메시지 전송이 가능한 ProducerFactory 인스턴스
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
     * BehaviorLog 메시지를 Kafka로 전송할 수 있는 KafkaTemplate 빈을 생성합니다.
     *
     * @return BehaviorLog 객체를 전송하는 KafkaTemplate 인스턴스
     */
    @Bean
    public KafkaTemplate<String, BehaviorLog> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
