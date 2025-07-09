package com.dataracy.modules.behaviorlog.application.port.in;

/**
 * 익명 유저가 로그인 할 경우 로그 병합
 */
public interface MergeAnonymousUserLogUseCase {
    void merge(String anonymousId, Long userId);
}
