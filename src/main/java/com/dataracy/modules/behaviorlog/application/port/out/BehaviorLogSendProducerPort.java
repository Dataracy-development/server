package com.dataracy.modules.behaviorlog.application.port.out;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;

/**
 * Kafka에 전송 포트
 */
public interface BehaviorLogSendProducerPort {
    void send(BehaviorLog log);
}
