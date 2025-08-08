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

    /**
     * Kafka 소비자에 대한 기본 구성 속성 맵을 반환합니다.
     *
     * 반환되는 맵에는 부트스트랩 서버, 그룹 ID, 오프셋 리셋 정책, 자동 커밋 비활성화, 최대 폴 레코드 수, 키 및 값 디시리얼라이저 클래스가 포함됩니다.
     *
     * @return Kafka 소비자 구성을 위한 속성 맵
     */
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

    /**
     * Kafka ConsumerFactory 인스턴스를 생성하여 반환합니다.
     *
     * baseConsumerProps()에서 정의된 Kafka 소비자 설정을 사용하여 DefaultKafkaConsumerFactory를 생성합니다.
     *
     * @return Kafka 메시지 소비를 위한 ConsumerFactory 인스턴스
     */
    public ConsumerFactory<String, V> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(baseConsumerProps());
    }

    /**
 * Kafka 메시지 키에 사용할 역직렬화 클래스 타입을 반환합니다.
 *
 * @return Kafka 메시지 키 역직렬화에 사용될 클래스 타입
 */
protected abstract Class<?> keyDeserializer();
    /**
 * Kafka 값 역직렬화에 사용할 Deserializer 클래스 타입을 반환합니다.
 *
 * @return 값 역직렬화에 사용되는 Deserializer 클래스
 */
protected abstract Class<?> valueDeserializer();
    /**
 * Kafka 컨슈머 그룹 ID를 반환합니다.
 *
 * 이 메서드는 구체적인 컨슈머 그룹 ID를 지정하기 위해 하위 클래스에서 구현되어야 합니다.
 *
 * @return 사용할 Kafka 컨슈머 그룹 ID
 */
protected abstract String groupId();

    /**
     * Kafka Consumer의 bootstrap-servers 설정이 비어 있는지 검증합니다.
     *
     * 설정 값이 null이거나 공백일 경우 IllegalStateException을 발생시킵니다.
     */
    protected void validateBootstrap() {
        if (bootstrapServers == null || bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka Consumer bootstrap-servers 설정이 비어 있습니다.");
        }
    }
}
