package com.dataracy.modules.filestorage.adapter.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileDeleteProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.producer.file-delete.topic:file-delete-topic}")
    private String fileDeleteTopic;

    /**
     * 파일 삭제 이벤트를 지정된 Kafka 토픽에 비동기적으로 전송합니다.
     *
     * @param fileUrl 삭제 대상 파일의 URL. 값이 null이거나 비어 있으면 이벤트를 전송하지 않습니다.
     */
    public void sendDeleteEvent(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            log.warn("파일 URL이 비어있어 삭제 이벤트를 전송하지 않습니다.");
            return;
        }
        kafkaTemplate.send(fileDeleteTopic, fileUrl)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("파일 삭제 이벤트 전송 실패: {}", fileUrl, ex);
                        // 필요시 재시도 로직 또는 예외 처리
                    } else {
                        log.info("파일 삭제 이벤트 전송 완료: {}", fileUrl);
                    }
                });
    }
}
