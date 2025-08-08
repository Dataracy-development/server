package com.dataracy.modules.common.config.adapter.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKafkaConsumerConfig<V> {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    protected Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer());
        return props;
    }

    public ConsumerFactory<String, V> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(baseConsumerProps());
    }

    protected abstract Class<?> keyDeserializer();
    protected abstract Class<?> valueDeserializer();
    protected abstract String groupId();

    protected void validateBootstrap() {
        if (bootstrapServers == null || bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka Consumer bootstrap-servers 설정이 비어 있습니다.");
        }
    }
}
