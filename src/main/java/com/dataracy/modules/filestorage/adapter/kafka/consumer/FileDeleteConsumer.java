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
     * Kafka 메시지로 전달된 파일 URL을 받아 해당 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 URL (Kafka 토픽에서 수신)
     *
     * 파일 삭제 중 예외가 발생하면 에러로 기록되며, 예외는 재전파되지 않습니다.
     */
    @KafkaListener(
            topics = "${spring.kafka.consumer.file-delete.topic:file-delete-topic}",
            groupId = "${spring.kafka.consumer.file-delete.group-id:file-delete-consumer-group}"
    )
    public void consume(String fileUrl) {
        log.info("[Kafka] 파일 삭제 이벤트 수신됨: fileUrl:{}", fileUrl);
        try {
            fileStoragePort.delete(fileUrl);
            log.info("[Kafka] 파일 삭제 이벤트 처리 완료: fileUrl:{}", fileUrl);
        } catch (Exception e) {
            log.error("[Kafka] 파일 삭제 이벤트 처리 실패: fileUrl:{}", fileUrl, e);
//            throw e; // DLQ 설정에 따라 이동
        }
    }
}
