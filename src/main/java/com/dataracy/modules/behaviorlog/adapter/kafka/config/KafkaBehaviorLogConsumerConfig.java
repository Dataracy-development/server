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

    /**
     * BehaviorLog 메시지를 소비하기 위한 Kafka ConsumerFactory 빈을 생성합니다.
     *
     * Kafka 서버 주소, 컨슈머 그룹, 키/값 역직렬화 설정을 포함하며,
     * BehaviorLog 객체 역직렬화를 위해 신뢰할 수 있는 패키지를 지정합니다.
     *
     * @return BehaviorLog 타입의 메시지를 처리하는 ConsumerFactory 인스턴스
     */
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

    /**
     * `BehaviorLog` 메시지 처리를 위한 Kafka 리스너 컨테이너 팩토리 빈을 생성합니다.
     *
     * 이 팩토리는 `behaviorLogConsumerFactory()`에서 생성된 컨슈머 팩토리를 사용하여
     * `BehaviorLog` 객체의 병렬 소비를 지원하는 Kafka 리스너 컨테이너를 제공합니다.
     *
     * @return `BehaviorLog` 타입의 메시지를 처리하는 Kafka 리스너 컨테이너 팩토리
     */
    @Bean(name = "behaviorLogKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BehaviorLog> behaviorLogKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BehaviorLog> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(behaviorLogConsumerFactory());
        return factory;
    }
}
