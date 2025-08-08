package com.dataracy.modules.common.config.adapter.kafka;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaLongProducerConfig extends AbstractKafkaProducerConfig<Long> {

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
        return org.apache.kafka.common.serialization.LongSerializer.class;
    }

    @Bean
    public KafkaTemplate<String, Long> longKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
