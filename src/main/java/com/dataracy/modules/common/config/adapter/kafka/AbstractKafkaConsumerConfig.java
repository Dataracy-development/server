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

    // 운영에서 쉽게 바꿀 수 있게 프로퍼티화
    @Value("${spring.kafka.consumer.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    @Value("${spring.kafka.consumer.properties.max-poll-records:100}")
    private Integer maxPollRecords;

    /**
     * Kafka 소비자 기본 설정
     */
    protected Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);     // 성공 시에만 커밋
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer());
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed"); // 트랜잭션 producer 대비 안전
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
