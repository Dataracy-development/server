package com.dataracy.modules.common.config.adapter.kafka;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaLongConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:}")
    private String bootstrapServers;

    /**
     * Kafka 메시지의 String 키와 Long 값을 처리하는 리스너 컨테이너 팩토리 빈을 생성합니다.
     *
     * @param longConsumerFactory Kafka ConsumerFactory 인스턴스
     * @return String 키와 Long 값을 위한 ConcurrentKafkaListenerContainerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Long> longKafkaListenerContainerFactory(
            ConsumerFactory<String, Long> longConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Long> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(longConsumerFactory);
        return factory;
    }

    /**
     * String 키와 Long 값을 처리하는 Kafka ConsumerFactory 빈을 생성합니다.
     *
     * Kafka 부트스트랩 서버, 역직렬화 방식, 컨슈머 그룹 ID, 오프셋 리셋 정책, 자동 커밋 비활성화, 최대 폴 레코드 수 등
     * 주요 속성을 설정하여 String-Key, Long-Value 메시지 처리를 위한 DefaultKafkaConsumerFactory를 반환합니다.
     *
     * @return String 키와 Long 값을 처리하는 Kafka ConsumerFactory 인스턴스
     */
    @Bean
    public ConsumerFactory<String, Long> longConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "long-consumer-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Kafka Consumer의 bootstrap 서버 설정이 비어 있는지 확인하고, 비어 있을 경우 예외를 발생시킵니다.
     *
     * @throws IllegalStateException bootstrap 서버 설정이 비어 있거나 유효하지 않을 때 발생합니다.
     */
    @PostConstruct
    public void validateProperties() {
        if (bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka Consumer bootstrap-servers 설정이 올바르지 않습니다.");
        }
    }
}
