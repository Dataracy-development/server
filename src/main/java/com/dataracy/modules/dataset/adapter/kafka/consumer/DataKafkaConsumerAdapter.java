package com.dataracy.modules.dataset.adapter.kafka.consumer;

import com.dataracy.modules.dataset.application.dto.request.MetadataParseRequest;
import com.dataracy.modules.dataset.application.port.in.MetadataParseUseCase;
import com.dataracy.modules.dataset.domain.model.event.DataUploadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataKafkaConsumerAdapter {

    private final MetadataParseUseCase metadataParseUseCase;

    /**
     * Kafka에서 데이터 업로드 이벤트를 수신하여 메타데이터 파싱 및 저장을 트리거합니다.
     *
     * @param event 수신된 데이터 업로드 이벤트 객체
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.extract-metadata.topic:data-uploaded}",
            groupId = "${spring.kafka.consumer.extract-metadata.group-id:data-upload-metadata-consumer-group}",
            containerFactory = "dataUploadEventKafkaListenerContainerFactory"
    )
    public void consume(DataUploadEvent event) {
        log.info("[Kafka] 업로드 이벤트 수신됨: {}", event);
        metadataParseUseCase.parseAndSaveMetadata(
                new MetadataParseRequest(
                        event.getDataId(),
                        event.getFileUrl(),
                        event.getOriginalFilename()
                )
        );
    }
}
