package com.dataracy.modules.common.config;

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
     * Kafka에서 String 키와 Long 값을 처리하는 ConsumerFactory 빈을 생성합니다.
     *
     * Kafka 부트스트랩 서버, 키/값 역직렬화 방식, 컨슈머 그룹 ID 등 필수 속성을 설정하여
     * String-Key, Long-Value 메시지를 처리할 수 있도록 구성된 DefaultKafkaConsumerFactory를 반환합니다.
     *
     * @return String 키와 Long 값을 처리하는 Kafka ConsumerFactory
     */
    @Bean
    public ConsumerFactory<String, Long> longConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // ✅ 수정됨
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "long-consumer-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Kafka bootstrap 서버 설정이 비어 있는지 검증하며, 비어 있을 경우 예외를 발생시킵니다.
     *
     * @throws IllegalStateException Kafka bootstrap 서버 설정이 비어 있을 때 발생합니다.
     */
    @PostConstruct
    public void validateProperties() {
        if (bootstrapServers.isBlank()) {
            throw new IllegalStateException("Kafka Producer 설정이 올바르지 않습니다.");
        }
    }
}
