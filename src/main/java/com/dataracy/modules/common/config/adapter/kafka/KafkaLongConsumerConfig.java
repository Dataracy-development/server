package com.dataracy.modules.common.config.adapter.kafka;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class KafkaLongConsumerConfig extends AbstractKafkaConsumerConfig<Long> {
    /**
     * Kafka 부트스트랩 서버 구성을 검증합니다.
     *
     * 빈 생성 후 Kafka 연결 설정이 올바른지 확인하기 위해 호출됩니다.
     */
    @PostConstruct
    public void validate() {
        validateBootstrap();
    }

    /**
     * Kafka 메시지 키를 역직렬화할 때 사용할 역직렬화 클래스(StringDeserializer)를 반환합니다.
     *
     * @return Kafka 메시지 키에 사용할 StringDeserializer 클래스
     */
    @Override
    protected Class<?> keyDeserializer() {
        return StringDeserializer.class;
    }

    /**
     * Kafka 메시지 값의 역직렬화에 사용할 LongDeserializer 클래스를 반환합니다.
     *
     * @return Long 값을 역직렬화하는 데 사용되는 클래스
     */
    @Override
    protected Class<?> valueDeserializer() {
        return LongDeserializer.class;
    }

    /**
     * Kafka 컨슈머 그룹 ID로 "long-consumer-group"을 반환합니다.
     *
     * @return Kafka 컨슈머 그룹 ID 문자열
     */
    @Override
    protected String groupId() {
        return "long-consumer-group";
    }

    /**
     * Kafka 메시지의 String 키와 Long 값을 처리하는 리스너 컨테이너 팩토리를 생성하여 빈으로 등록합니다.
     *
     * @return String 키와 Long 값을 지원하는 ConcurrentKafkaListenerContainerFactory 인스턴스
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Long> longKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Long>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
