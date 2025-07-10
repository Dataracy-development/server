package com.dataracy.modules.behaviorlog.adapter.message.kafka;

import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogSendProducerPort;
import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorLogKafkaProducerAdapter implements BehaviorLogSendProducerPort {

    private final KafkaTemplate<String, BehaviorLog> kafkaTemplate;

    @Value("${spring.kafka.producer.behavior-log.topic:behavior-logs}")
    private String topic;

    @Override
    public void send(BehaviorLog behaviorLog) {
        String key = behaviorLog.getUserId() != null
                ? behaviorLog.getUserId()
                : behaviorLog.getAnonymousId();

        if (key == null) {
            log.warn("Kafka 전송 건에 key가 null입니다. 로그 전송 무시됨.");
            return;
        }

        kafkaTemplate.send(topic, key, behaviorLog)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka 메시지 전송 실패: {}", ex.getMessage(), ex);
                    } else {
                        log.debug("Kafka 메시지 전송 성공 - topic={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
