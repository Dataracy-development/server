package com.dataracy.modules.behaviorlog.application.port.in;

/**
 * 익명 사용자 로그를 실제 사용자와 병합하는 유스케이스
 */
public interface MergeAnonymousUserLogUseCase {
    void merge(String anonymousId, Long userId);
}
