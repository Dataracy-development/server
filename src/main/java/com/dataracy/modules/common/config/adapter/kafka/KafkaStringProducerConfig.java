package com.dataracy.modules.common.config.adapter.kafka;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaStringProducerConfig extends AbstractKafkaProducerConfig<String> {
    /**
     * Kafka 부트스트랩 서버 설정이 올바른지 검증합니다.
     *
     * 이 메서드는 빈 초기화 후 자동으로 호출되어, Kafka 프로듀서의 부트스트랩 서버 구성이 유효한지 확인합니다.
     */
    @PostConstruct
    public void validate() {
        validateBootstrapServers();
    }

    /**
     * Kafka 메시지 키에 사용할 직렬화 클래스로 StringSerializer를 반환합니다.
     *
     * @return Kafka 메시지 키 직렬화에 사용되는 StringSerializer 클래스
     */
    @Override
    protected Class<?> keySerializer() {
        return org.apache.kafka.common.serialization.StringSerializer.class;
    }

    /**
     * Kafka 메시지 값의 직렬화에 사용할 StringSerializer 클래스를 반환합니다.
     *
     * @return Kafka 값 직렬화에 사용되는 StringSerializer 클래스
     */
    @Override
    protected Class<?> valueSerializer() {
        return org.apache.kafka.common.serialization.StringSerializer.class;
    }

    /**
     * Kafka 메시지의 String 키와 값을 처리하는 KafkaTemplate 빈을 생성합니다.
     *
     * @return String 키와 값을 사용하는 KafkaTemplate 인스턴스
     */
    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
