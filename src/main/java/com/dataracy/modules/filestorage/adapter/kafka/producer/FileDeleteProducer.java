package com.dataracy.modules.filestorage.adapter.kafka.producer;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileDeleteProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.producer.file-delete.topic:file-delete-topic}")
    private String fileDeleteTopic;

    /**
     * 파일 삭제 이벤트를 지정된 Kafka 토픽에 비동기적으로 발송합니다.
     *
     * @param fileUrl 삭제할 파일의 URL. null이거나 비어 있으면 이벤트를 발송하지 않습니다.
     */
    public void sendDeleteEvent(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            LoggerFactory.kafka().logWarning("fileDeleteTopic", "파일 삭제 이벤트 발송을 위한 파일 url이 존재하지 않습니다.");
            return;
        }
        kafkaTemplate.send(fileDeleteTopic, fileUrl)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        LoggerFactory.kafka().logError(fileDeleteTopic, "파일 삭제 이벤트 발송 처리 실패", ex);
                        // 필요시 재시도 로직 또는 예외 처리
                    } else {
                        LoggerFactory.kafka().logProduce(fileDeleteTopic, "파일 삭제 이벤트 발송됨");
                    }
                });
    }
}
