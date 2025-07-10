package com.dataracy.modules.behaviorlog.adapter.message.kafka.config;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
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
public class KafkaBehaviorLogProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Kafka 프로듀서 설정을 기반으로 BehaviorLog 객체를 전송하는 ProducerFactory 빈을 생성합니다.
     *
     * @return BehaviorLog 메시지 전송을 위한 Kafka ProducerFactory 인스턴스
     */
    @Bean
    public ProducerFactory<String, BehaviorLog> behaviorLogProducerFactory() {
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
     * BehaviorLog 객체를 전송하기 위한 KafkaTemplate 빈을 생성합니다.
     *
     * @return BehaviorLog 메시지를 Kafka 토픽으로 전송할 수 있는 KafkaTemplate 인스턴스
     */
    @Bean
    public KafkaTemplate<String, BehaviorLog> behaviorLogKafkaTemplate() {
        return new KafkaTemplate<>(behaviorLogProducerFactory());
    }
}
