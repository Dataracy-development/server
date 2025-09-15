package com.dataracy.modules.common.config.adapter.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.record.CompressionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKafkaProducerConfig<V> {
    @Value("${spring.kafka.bootstrap-servers:}")
    private String bootstrapServers;

    /**
     * Kafka 프로듀서의 기본 설정 값을 담은 맵을 반환합니다.
     * 반환되는 맵에는 bootstrap servers, acks, retries 설정이 포함되어 있습니다.
     *
     * @return Kafka 프로듀서의 기본 설정 값 맵
     */
    protected Map<String, Object> baseProducerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // ---- 안정성/주문 보장 ----
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1); // 순서 중요시 1

        // ---- 재시도/타임아웃/성능 ----
        props.put(ProducerConfig.RETRIES_CONFIG, 10);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30_000);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120_000);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, CompressionType.LZ4.name().toLowerCase());

        return props;
    }

    /**
     * Kafka 프로듀서의 키 직렬화에 사용할 클래스 타입을 반환합니다.
     * 구체 서브클래스에서 반드시 구현해야 하며, 키 직렬화에 적합한 Serializer 클래스를 지정해야 합니다.
     *
     * @return Kafka 프로듀서 키 직렬화에 사용할 클래스
     */
    protected abstract Class<?> keySerializer();

    /**
     * Kafka 프로듀서의 값(Value) 직렬화에 사용할 직렬화 클래스 타입을 반환합니다.
     *
     * @return 값 직렬화에 사용할 클래스 타입
     */
    protected abstract Class<?> valueSerializer();

    /**
     * Kafka 프로듀서를 위한 ProducerFactory 인스턴스를 생성합니다.
     * 기본 프로듀서 설정과 서브클래스에서 지정한 Key/Value 직렬화 클래스를 포함하여 ProducerFactory를 반환합니다.
     *
     * @return Kafka 메시지 전송에 사용할 ProducerFactory 인스턴스
     */
    public ProducerFactory<String, V> producerFactory() {
        Map<String, Object> props = baseProducerProps();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer());
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Kafka bootstrap-servers 설정이 비어 있는지 검증합니다.
     * 설정 값이 null이거나 공백일 경우 IllegalStateException을 발생시킵니다.
     *
     * @throws IllegalStateException Kafka bootstrap-servers 설정이 비어 있을 때 발생합니다.
     */
    protected void validateBootstrapServers() {
        if (bootstrapServers == null || bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka bootstrap-servers 설정이 비어 있습니다.");
        }
    }
}
