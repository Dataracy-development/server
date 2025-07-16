package com.dataracy.modules.data.adapter.kafka.config;

import com.dataracy.modules.data.domain.model.event.DataUploadEvent;
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
public class KafkaDataUploadConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * DataUploadEvent 메시지를 처리하기 위한 Kafka ConsumerFactory 빈을 생성합니다.
     *
     * Kafka bootstrap 서버, consumer group ID, key/value deserializer 등 필요한 설정을 적용하여
     * DataUploadEvent 객체를 안전하게 역직렬화할 수 있도록 구성된 ConsumerFactory를 반환합니다.
     *
     * @return DataUploadEvent 메시지 소비를 위한 ConsumerFactory 인스턴스
     */
    @Bean
    public ConsumerFactory<String, DataUploadEvent> dataUploadConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "data-upload-metadata-consumer-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<DataUploadEvent> valueDeserializer = new JsonDeserializer<>(DataUploadEvent.class);
        valueDeserializer.addTrustedPackages("com.dataracy.modules.data.domain.model.event.DataUploadEvent");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Kafka 리스너에서 DataUploadEvent 메시지를 병렬로 처리할 수 있도록 설정된 KafkaListenerContainerFactory 빈을 생성합니다.
     *
     * @return DataUploadEvent 메시지 처리를 위한 ConcurrentKafkaListenerContainerFactory 인스턴스
     */
    @Bean(name = "dataUploadEventKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, DataUploadEvent> dataUploadEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DataUploadEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(dataUploadConsumerFactory());
        return factory;
    }
}
