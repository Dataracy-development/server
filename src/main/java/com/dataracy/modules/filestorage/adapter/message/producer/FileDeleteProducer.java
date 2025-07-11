package com.dataracy.modules.filestorage.adapter.message.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileDeleteProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendDeleteEvent(String fileUrl) {
        kafkaTemplate.send("file-delete-topic", fileUrl);
    }
}
