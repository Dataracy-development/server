package com.dataracy.modules.common.config.adapter.kafka;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;

@Configuration
public class KafkaLongConsumerConfig extends AbstractKafkaConsumerConfig<Long> {

    // 기본 그룹 ID (리스너에서 groupId를 지정하면 그 값이 우선)
    @Value("${spring.kafka.consumer.long.group-id:long-consumer-group}")
    private String group;

    // 동시성 프로퍼티 (트래픽 보며 조정)
    @Value("${spring.kafka.listener.long.concurrency:1}")
    private Integer concurrency;

    @PostConstruct
    public void validate() {
        validateBootstrap();
    }

    @Override
    protected Class<?> keyDeserializer() {
        return StringDeserializer.class;
    }

    @Override
    protected Class<?> valueDeserializer() {
        return LongDeserializer.class;
    }

    @Override
    protected String groupId() {
        return group;
    }

    /**
     * 공통 에러 핸들러:
     * - 1s → 2s → 4s → 8s → 16s, 총 5회 재시도 후 DLT
     * - 재시도 무의미한 예외는 즉시 DLT
     * - DLT 토픽은 <원본토픽>-dlt 로 송신
     *
     * 주의: DLT로 퍼블리시할 때 Long 값이므로, longKafkaTemplate(String, Long) 사용할 것을 권장.
     */
    @Bean
    public DefaultErrorHandler longKafkaErrorHandler(KafkaTemplate<String, Long> longKafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                longKafkaTemplate,
                (rec, ex) -> new TopicPartition(rec.topic() + "-dlt", rec.partition())
        );

        ExponentialBackOffWithMaxRetries backoff = new ExponentialBackOffWithMaxRetries(5);
        backoff.setInitialInterval(1000);
        backoff.setMultiplier(2.0);
        backoff.setMaxInterval(16000);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backoff);

        // "재시도 무의미" 예외 → 즉시 DLT
        handler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                SerializationException.class,
                DeserializationException.class,
                MessageConversionException.class
        );

        return handler;
    }

    /**
     * Long 전용 리스너 컨테이너 팩토리
     */
    @Bean(name = "longKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Long> longKafkaListenerContainerFactory(
            DefaultErrorHandler longKafkaErrorHandler
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Long>();
        factory.setConsumerFactory(consumerFactory());

        // 공통 에러 핸들러 연결 (@RetryableTopic 없이도 재시도 + DLT 동작)
        factory.setCommonErrorHandler(longKafkaErrorHandler);

        // 동시성 (파티션 수/부하에 맞춰 조정)
        factory.setConcurrency(concurrency);

        // 1건 처리 성공 시마다 즉시 커밋(정합성 강화)
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        // 필요 시 폴 세션/인터벌 등 추가 튜닝 가능
        // factory.getContainerProperties().setPollTimeout(1500);

        return factory;
    }
}
