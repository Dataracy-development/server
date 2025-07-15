package com.dataracy.modules.behaviorlog.adapter.kafka.provider;

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

    /**
     * BehaviorLog 객체를 Kafka 토픽으로 비동기 전송합니다.
     *
     * behaviorLog의 userId가 존재하면 이를 메시지 키로 사용하고, 없을 경우 anonymousId를 키로 사용합니다.
     * 두 값 모두 null인 경우 로그를 남기고 전송을 수행하지 않습니다.
     */
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
                        log.error("Kafka 메시지 전송 실패 - topic={}, key={}, error={}",
                                topic, key, ex.getMessage(), ex);
                    } else {
                        log.trace("Kafka 메시지 전송 성공 - topic={}, partition={}, offset={}, key={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                key);
                    }
                });
    }
}
