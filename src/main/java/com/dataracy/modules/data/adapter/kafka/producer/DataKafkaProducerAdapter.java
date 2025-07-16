package com.dataracy.modules.data.adapter.kafka.producer;

import com.dataracy.modules.data.application.port.out.DataKafkaProducerPort;
import com.dataracy.modules.data.domain.model.event.DataUploadEvent;
import jakarta.annotation.PostConstruct;
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
        log.info("[Kafka] 데이터 업로드 이벤트 발송: {}", event);
        kafkaTemplate.send(topic, String.valueOf(dataId), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[Kafka] 데이터 업로드 이벤트 발송 실패: {}", event, ex);
                        // 필요시 재시도 로직 또는 예외 처리
                    } else {
                        log.trace("[Kafka] 데이터 업로드 이벤트 발송 성공: {}", event);
                    }
                });
    }

    @PostConstruct
    public void validateTopic() {
        if (topic.isBlank()) {
            throw new IllegalStateException("Kafka topic이 올바르게 설정되지 않았습니다.");
        }
    }
}
