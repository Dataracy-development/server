package com.dataracy.modules.behaviorlog.application.port.out;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;

/**
 * 행동 로그 저장소 포트
 */
public interface BehaviorLogRepositoryPort {
    /**
     * 주어진 BehaviorLog 객체를 저장합니다.
     * 행동 로그 객체를 저장합니다.
     *
     * @param log 저장할 행동 로그 객체
     */
    void save(BehaviorLog log);
}
