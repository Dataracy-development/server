package com.dataracy.modules.dataset.adapter.kafka.config;

import com.dataracy.modules.common.config.adapter.kafka.AbstractKafkaJsonConsumerConfig;
import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaDataUploadConsumerConfig extends AbstractKafkaJsonConsumerConfig<DataUploadEvent> {

    private static final String GROUP_ID = "data-upload-metadata-consumer-group";
    private static final String TRUSTED_PACKAGE = "com.dataracy.modules.dataset.domain.model.event";

    @Bean
    public ConsumerFactory<String, DataUploadEvent> dataUploadConsumerFactory() {
        return consumerFactory(DataUploadEvent.class, GROUP_ID, TRUSTED_PACKAGE);
    }

    @Bean(name = "dataUploadEventKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, DataUploadEvent> dataUploadEventKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, DataUploadEvent>();
        factory.setConsumerFactory(dataUploadConsumerFactory());
        return factory;
    }
}
