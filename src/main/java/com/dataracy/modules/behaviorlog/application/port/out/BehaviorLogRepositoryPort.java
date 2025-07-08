package com.dataracy.modules.behaviorlog.application.port.out;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;

/**
 * 행동 로그 저장소 포트
 */
public interface BehaviorLogRepositoryPort {
    void save(BehaviorLog log);
}
