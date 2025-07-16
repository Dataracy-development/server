package com.dataracy.modules.data.adapter.kafka.provider;

import com.dataracy.modules.data.application.port.out.DataKafkaProducerPort;
import com.dataracy.modules.data.domain.model.event.DataUploadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataKafkaProducerAdapter implements DataKafkaProducerPort {

    private final KafkaTemplate<String, DataUploadEvent> kafkaTemplate;

    @Value("${spring.kafka.producer.extract-metadata.topic:data-uploaded}")
    private String topic;

    @Override
    public void sendUploadEvent(Long dataId, String fileUrl, String originalFilename) {
        DataUploadEvent event = new DataUploadEvent(dataId, fileUrl, originalFilename);
        kafkaTemplate.send(topic, String.valueOf(dataId), event);
        log.info("[Kafka] 데이터 업로드 이벤트 발송: {}", event);
    }
}
