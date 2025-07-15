package com.dataracy.modules.filestorage.adapter.kafka.consumer;

import com.dataracy.modules.filestorage.application.port.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileDeleteConsumer {

    private final FileStoragePort fileStoragePort;

    /**
     * Kafka에서 전달된 파일 URL을 받아 해당 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 URL
     */
    @KafkaListener(topics = "file-delete-topic", groupId = "file-delete-consumer")
    public void consume(String fileUrl) {
        try {
            fileStoragePort.delete(fileUrl);
            log.info("[Kafka] 파일 삭제 완료: {}", fileUrl);
        } catch (Exception e) {
            log.error("[Kafka] 파일 삭제 실패 - DLQ로 이동 예정: {}", fileUrl, e);
//            throw e; // DLQ 설정에 따라 이동
        }
    }
}
