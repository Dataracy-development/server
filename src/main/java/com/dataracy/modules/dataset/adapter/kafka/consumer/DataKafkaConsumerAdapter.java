package com.dataracy.modules.dataset.adapter.kafka.consumer;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.dto.request.metadata.ParseMetadataRequest;
import com.dataracy.modules.dataset.application.port.in.command.metadata.ParseMetadataUseCase;
import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataKafkaConsumerAdapter {
    private final ParseMetadataUseCase parseMetadataUseCase;

    @Value("${spring.kafka.consumer.extract-metadata.topic:data-uploaded}")
    private String dataUploadedTopic;

    /**
     * Kafka에서 데이터 업로드 이벤트를 수신하여 해당 파일의 메타데이터를 파싱하고 저장합니다.
     *
     * @param event 데이터 업로드 이벤트 정보
     *
     * 예외 발생 시 Kafka의 재시도 메커니즘을 활성화하기 위해 예외를 다시 던집니다.
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.extract-metadata.topic:data-uploaded}",
            groupId = "${spring.kafka.consumer.extract-metadata.group-id:data-upload-metadata-consumer-group}",
            containerFactory = "dataUploadEventKafkaListenerContainerFactory"
    )
    public void consume(DataUploadEvent event) {
        try {
            LoggerFactory.kafka().logConsume(dataUploadedTopic, "데이터셋 업로드 이벤트 수신됨: dataId=" + event.getDataId());
            parseMetadataUseCase.parseAndSaveMetadata(
                    new ParseMetadataRequest(
                            event.getDataId(),
                            event.getFileUrl(),
                            event.getOriginalFilename()
                    )
            );
            LoggerFactory.kafka().logConsume(dataUploadedTopic, "데이터셋 업로드 이벤트 처리 완료: dataId=" + event.getDataId());
        } catch (Exception e) {
            LoggerFactory.kafka().logError(dataUploadedTopic, "데이터셋 업로드 이벤트 처리 실패: dataId=" + event.getDataId(), e);
            throw e; // 재시도를 위해 예외 재던지기
        }
    }
}
