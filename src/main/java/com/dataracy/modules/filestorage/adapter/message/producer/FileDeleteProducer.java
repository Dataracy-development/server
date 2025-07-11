package com.dataracy.modules.filestorage.adapter.message.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileDeleteProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 지정된 파일 URL을 Kafka의 "file-delete-topic" 토픽에 삭제 이벤트 메시지로 전송합니다.
     *
     * @param fileUrl 삭제할 파일의 URL
     */
    public void sendDeleteEvent(String fileUrl) {
        kafkaTemplate.send("file-delete-topic", fileUrl);
    }
}
