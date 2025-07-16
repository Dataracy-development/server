package com.dataracy.modules.behaviorlog.adapter.kafka.config;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaBehaviorLogConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, BehaviorLog> behaviorLogConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "behavior-log-consumer-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<BehaviorLog> valueDeserializer = new JsonDeserializer<>(BehaviorLog.class, false);
        valueDeserializer.addTrustedPackages("com.dataracy.modules.behaviorlog.domain.model");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean(name = "behaviorLogKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BehaviorLog> behaviorLogKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BehaviorLog> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(behaviorLogConsumerFactory());
        return factory;
    }
}
