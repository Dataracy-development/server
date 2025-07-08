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
public class BehaviorLogKafkaProducer implements BehaviorLogSendProducerPort {
    private final KafkaTemplate<String, BehaviorLog> kafkaTemplate;

    // 토픽 이름
    @Value("${spring.kafka.behavior-log.topic:behavior-logs}")
    private String topic;

    /**
     * BehaviorLog 객체를 Kafka 토픽으로 비동기 전송합니다.
     *
     * @param behaviorLog 전송할 행동 로그 객체
     */
    @Override
    public void send(BehaviorLog behaviorLog) {
        // 익명 id는 비로그인, 로그인 유저 모두 쿠키에 값을 보유하고 있기 때문에 null여부를 요청 시 파악한다.
        String key = behaviorLog.getAnonymousId();
        if (key == null || key.isEmpty()) {
            key = behaviorLog.getUserId() != null ? behaviorLog.getUserId() : "unknown";
        }

        kafkaTemplate.send(topic, key, behaviorLog)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka 메시지 전송 실패", ex);
                    } else {
                        log.debug("[Kafka Producer] 행동 로그 전송 완료");
                    }
                });
    }
}
