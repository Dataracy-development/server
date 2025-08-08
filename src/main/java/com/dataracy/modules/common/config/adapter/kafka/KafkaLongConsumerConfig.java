package com.dataracy.modules.common.config.adapter.kafka;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class KafkaLongConsumerConfig extends AbstractKafkaConsumerConfig<Long> {

    @Override
    protected Class<?> keyDeserializer() {
        return StringDeserializer.class;
    }

    @Override
    protected Class<?> valueDeserializer() {
        return LongDeserializer.class;
    }

    @Override
    protected String groupId() {
        return "long-consumer-group";
    }

    @PostConstruct
    public void validate() {
        validateBootstrap();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Long> longKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Long>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
