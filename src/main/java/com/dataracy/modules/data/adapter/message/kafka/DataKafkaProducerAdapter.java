package com.dataracy.modules.data.adapter.message.kafka;

import com.dataracy.modules.data.application.port.out.DataKafkaProducerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataKafkaProducerAdapter implements DataKafkaProducerPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendUploadEvent(Long dataId, String fileUrl, String filename) {
        kafkaTemplate.send("data.uploaded", Map.of(
                "dataId", dataId,
                "fileUrl", fileUrl,
                "filename", filename
        ));
    }
}
