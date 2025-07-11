package com.dataracy.modules.behaviorlog.application.port.in;

/**
 * 익명 사용자 로그를 실제 사용자와 병합하는 유스케이스
 */
public interface MergeAnonymousUserLogUseCase {
    /**
     * 익명 사용자 ID에 연결된 로그를 지정된 실제 사용자 ID의 로그로 병합합니다.
     *
     * @param anonymousId 병합할 익명 사용자의 식별자
     * @param userId 로그를 병합할 실제 사용자의 식별자
     */
    void merge(String anonymousId, Long userId);
}
