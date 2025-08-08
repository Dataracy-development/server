package com.dataracy.modules.filestorage.adapter.kafka.consumer;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileDeleteConsumer {

    private final FileStoragePort fileStoragePort;

    @Value("${spring.kafka.consumer.file-delete.topic:file-delete-topic}")
    private String fileDeletedTopic;

    /**
     * Kafka 메시지로 전달된 파일 URL을 받아 해당 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 URL
     *
     * 파일 삭제 중 예외가 발생하면 에러로 기록되며, 예외는 재전파되지 않습니다.
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.file-delete.topic:file-delete-topic}",
            groupId = "${spring.kafka.consumer.file-delete.group-id:file-delete-consumer-group}"
    )
    public void consume(String fileUrl) {
        try {
            LoggerFactory.kafka().logConsume(fileDeletedTopic, "파일 삭제 이벤트 수신됨: " + fileUrl);
            fileStoragePort.delete(fileUrl);
            LoggerFactory.kafka().logConsume(fileDeletedTopic, "파일 삭제 이벤트 처리 완료: " + fileUrl);
        } catch (Exception e) {
            LoggerFactory.kafka().logError(fileDeletedTopic, "파일 삭제 이벤트 처리 실패: " + fileUrl, e);
//            throw e; // DLQ 설정에 따라 이동
        }
    }
}
