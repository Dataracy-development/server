package com.dataracy.modules.behaviorlog.application.port.in;

import com.dataracy.modules.behaviorlog.domain.model.BehaviorLog;

/**
 * 행동 로그 유스케이스
 */
public interface LogBehaviorUseCase {
    void log(BehaviorLog log);
}
