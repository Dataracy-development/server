package com.dataracy.modules.common.config.adapter.kafka;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaStringProducerConfig extends AbstractKafkaProducerConfig<String> {

    @PostConstruct
    public void validate() {
        validateBootstrapServers();
    }

    @Override
    protected Class<?> keySerializer() {
        return org.apache.kafka.common.serialization.StringSerializer.class;
    }

    @Override
    protected Class<?> valueSerializer() {
        return org.apache.kafka.common.serialization.StringSerializer.class;
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
