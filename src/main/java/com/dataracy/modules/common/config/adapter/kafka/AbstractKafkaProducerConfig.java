package com.dataracy.modules.common.config.adapter.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKafkaProducerConfig<V> {

    @Value("${spring.kafka.bootstrap-servers:}")
    private String bootstrapServers;

    protected Map<String, Object> baseProducerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        return props;
    }

    protected void validateBootstrapServers() {
        if (bootstrapServers == null || bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka bootstrap-servers 설정이 비어 있습니다.");
        }
    }

    /**
     * 구체 구현에서 key/value 직렬화 클래스를 지정해야 합니다.
     */
    protected abstract Class<?> keySerializer();
    protected abstract Class<?> valueSerializer();

    /**
     * 공통 ProducerFactory 생성 메서드
     */
    public ProducerFactory<String, V> producerFactory() {
        Map<String, Object> props = baseProducerProps();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer());
        return new DefaultKafkaProducerFactory<>(props);
    }
}
