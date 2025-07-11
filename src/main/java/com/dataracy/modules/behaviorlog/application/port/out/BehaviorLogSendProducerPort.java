package com.dataracy.modules.behaviorlog.application.port.out;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;

/**
 * Kafka에 전송 포트
 */
public interface BehaviorLogSendProducerPort {
    /**
     * 행동 로그 객체를 외부 시스템(예: Kafka)으로 전송합니다.
     *
     * @param log 외부로 전송할 BehaviorLog 객체
     */
    void send(BehaviorLog log);
}
