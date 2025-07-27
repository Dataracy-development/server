package com.dataracy.modules.common.config;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaLongConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:}")
    private String bootstrapServers;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Long> longKafkaListenerContainerFactory(
            ConsumerFactory<String, Long> longConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(longConsumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Long> longConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // ✅ 수정됨
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "long-consumer-group");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @PostConstruct
    public void validateProperties() {
        if (bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka Producer 설정이 올바르지 않습니다.");
        }
    }
}
