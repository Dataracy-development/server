package com.dataracy.modules.data.adapter.kafka.config;

import com.dataracy.modules.data.domain.model.event.DataUploadEvent;
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
public class KafkaDataUploadConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, DataUploadEvent> dataUploadConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "data-upload-metadata-consumer-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<DataUploadEvent> valueDeserializer = new JsonDeserializer<>(DataUploadEvent.class);
        valueDeserializer.addTrustedPackages("com.dataracy.modules.data.domain.model.event.DataUploadEvent");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean(name = "dataUploadEventKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, DataUploadEvent> dataUploadEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DataUploadEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(dataUploadConsumerFactory());
        return factory;
    }
}
