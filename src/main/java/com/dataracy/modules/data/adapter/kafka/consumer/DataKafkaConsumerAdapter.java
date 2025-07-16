package com.dataracy.modules.data.adapter.kafka.consumer;

import com.dataracy.modules.data.application.dto.request.MetadataParseRequest;
import com.dataracy.modules.data.application.port.in.MetadataParseUseCase;
import com.dataracy.modules.data.domain.model.event.DataUploadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataKafkaConsumerAdapter {

    private final MetadataParseUseCase metadataParseUseCase;

    @KafkaListener(
            topics = "${spring.kafka.consumer.extract-metadata.topic:data-uploaded}",
            groupId = "${spring.kafka.consumer.extract-metadata.group-id:metadata-consumer}"
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
