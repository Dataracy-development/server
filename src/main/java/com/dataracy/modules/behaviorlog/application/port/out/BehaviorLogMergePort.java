package com.dataracy.modules.behaviorlog.application.port.out;

/**
 * 행동 로그 병합 포트
 */
public interface BehaviorLogMergePort {
    void merge(String anonymousId, Long userId);
}
