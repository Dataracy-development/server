package com.dataracy.modules.dataset.adapter.kafka.producer;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.port.out.command.event.DataUploadEventPort;
import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataKafkaProducerAdapter implements DataUploadEventPort {
    private final KafkaTemplate<String, DataUploadEvent> kafkaTemplate;

    @Value("${spring.kafka.producer.extract-metadata.topic:data-uploaded}")
    private String dataUploadTopic;

    /**
     * 데이터 업로드 이벤트를 생성하여 지정된 Kafka 토픽에 비동기적으로 발송합니다.
     *
     * @param dataId 업로드된 데이터의 고유 식별자
     * @param dataFileUrl 업로드된 파일의 저장 위치 URL
     * @param originalFilename 업로드된 파일의 원본 파일명
     */
    @Override
    public void sendUploadEvent(Long dataId, String dataFileUrl, String originalFilename) {
        DataUploadEvent event = new DataUploadEvent(dataId, dataFileUrl, originalFilename);
        kafkaTemplate.send(dataUploadTopic, String.valueOf(dataId), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        LoggerFactory.kafka().logError(dataUploadTopic, "데이터셋 업로드 이벤트 발송 처리 실패: dataId=" + dataId, ex);
                    } else {
                        LoggerFactory.kafka().logProduce(dataUploadTopic, "데이터셋 업로드 이벤트 발송됨: dataId=" + dataId);
                    }
                });
    }

    /**
     * Kafka 데이터 업로드 이벤트 토픽 설정이 비어 있는지 검증합니다.
     *
     * 토픽 이름이 비어 있을 경우 애플리케이션 실행을 중단하기 위해 {@code IllegalStateException}을 발생시킵니다.
     */
    @PostConstruct
    public void validateTopic() {
        if (dataUploadTopic.isBlank()) {
            throw new IllegalStateException("Kafka topic이 올바르게 설정되지 않았습니다.");
        }
    }
}
