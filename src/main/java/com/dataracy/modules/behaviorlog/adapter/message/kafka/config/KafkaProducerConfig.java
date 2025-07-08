package com.dataracy.modules.behaviorlog.adapter.message.kafka.config;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
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

    /**
     * Kafka 프로듀서를 위한 ProducerFactory 빈을 생성합니다.
     *
     * 이 팩토리는 String 타입의 키와 BehaviorLog 객체를 값으로 하여, JSON 직렬화를 사용해 Kafka에 메시지를 전송할 수 있도록 설정됩니다.
     *
     * @return Kafka 프로듀서 생성을 위한 ProducerFactory 인스턴스
     */
    @Bean
    public ProducerFactory<String, BehaviorLog> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        // Docker Kafka 주소
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * KafkaTemplate을 생성하여 반환합니다.
     *
     * 이 템플릿은 String 타입의 키와 BehaviorLog 타입의 값을 JSON으로 직렬화하여 Kafka 토픽에 메시지를 전송할 수 있도록 지원합니다.
     *
     * @return Kafka에 메시지를 전송하기 위한 KafkaTemplate 인스턴스
     */
    @Bean
    public KafkaTemplate<String, BehaviorLog> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
