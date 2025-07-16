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

    /**
     * 지정된 데이터 ID, 파일 URL, 원본 파일명을 기반으로 데이터 업로드 이벤트를 생성하여 Kafka 토픽에 비동기적으로 발송합니다.
     *
     * @param dataId 업로드된 데이터의 고유 식별자
     * @param fileUrl 업로드된 파일의 저장 위치 URL
     * @param originalFilename 업로드된 파일의 원본 파일명
     */
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

    /**
     * Kafka 토픽 설정이 비어 있는지 검증합니다.
     *
     * 토픽이 비어 있으면 {@code IllegalStateException}을 발생시킵니다.
     */
    @PostConstruct
    public void validateTopic() {
        if (topic.isBlank()) {
            throw new IllegalStateException("Kafka topic이 올바르게 설정되지 않았습니다.");
        }
    }
}
