package com.dataracy.modules.behaviorlog.application.port.out;

/**
 * Redis를 활용한 행동 로그 병합 포트
 */
public interface BehaviorLogMergePort {

    void merge(String anonymousId, Long userId);

    String findMergedUserId(String anonymousId);
}
