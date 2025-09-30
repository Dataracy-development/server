package com.dataracy.modules.dataset.adapter.kafka.config;

import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaDataUploadConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // yml에서 오버라이드 가능하도록 프로퍼티화 (기본값은 현재 사용 중인 그룹)
    @Value("${spring.kafka.consumer.extract-metadata.group-id:data-upload-metadata-consumer-group}")
    private String groupId;

    // 폴링 튜닝도 yml로 바꿀 수 있게 해둠 (원하면 제거 가능)
    @Value("${spring.kafka.consumer.properties.max-poll-records:200}")
    private int maxPollRecords;

    @Value("${spring.kafka.listener.data-upload.concurrency:1}")
    private int concurrency;

    /**
     * DataUploadEvent용 ConsumerFactory
     * - 타입 헤더를 사용하지 않는 환경에서 안전하게 역직렬화되도록 기본 타입/신뢰 패키지 지정
     * - read_committed / auto-commit off 등 운영 안정 설정
     */
    @Bean
    public ConsumerFactory<String, DataUploadEvent> dataUploadConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 키/값 디시리얼라이저 "클래스"만 지정 (인스턴스 X)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // JsonDeserializer 설정은 모두 props로만!
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, DataUploadEvent.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.dataracy.modules.dataset.domain.model.event");

        // ⛔ 여기에서 JsonDeserializer 인스턴스를 new 해서 넘기지 마세요
        return new DefaultKafkaConsumerFactory<>(props);
    }


    /**
     * 공통 에러 핸들러 (블로킹 리트라이 + DLT)
     * - 1s → 2s → 4s → 8s → 16s, 최대 5회
     * - 포이즌/역직렬화류는 즉시 DLT
     *
     * @param dltTemplate DLT 발행용 템플릿(일반적으로 Json KafkaTemplate 주입)
     */
    @Bean
    public DefaultErrorHandler dataUploadErrorHandler(
            @Qualifier("dataUploadEventKafkaTemplate")  // ← DataUploadEvent용 KafkaTemplate 빈 이름
            KafkaTemplate<String, DataUploadEvent> dltTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                dltTemplate,
                (rec, ex) -> new TopicPartition(rec.topic() + "-dlt", rec.partition())
        );

        ExponentialBackOffWithMaxRetries backoff = new ExponentialBackOffWithMaxRetries(5);
        backoff.setInitialInterval(1000);
        backoff.setMultiplier(2.0);
        backoff.setMaxInterval(16000);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backoff);
        handler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                org.apache.kafka.common.errors.SerializationException.class,
                org.springframework.kafka.support.serializer.DeserializationException.class
        );
        // 참고: spring-messaging 의존성 추가 시 MessageConversionException도 추가 가능
        return handler;
    }


    /**
     * 리스너 컨테이너 팩토리
     * - 공통 에러 핸들러 연결 → 모든 리스너에서 @RetryableTopic 없이 재시도/백오프/ DLT 적용
     * - 동시성은 트래픽에 따라 yml로 조절
     */
    @Bean(name = "dataUploadEventKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, DataUploadEvent>
    dataUploadEventKafkaListenerContainerFactory(DefaultErrorHandler dataUploadErrorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, DataUploadEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(dataUploadConsumerFactory());

        factory.setCommonErrorHandler(dataUploadErrorHandler);
        factory.setConcurrency(concurrency);

        return factory;
    }
}
