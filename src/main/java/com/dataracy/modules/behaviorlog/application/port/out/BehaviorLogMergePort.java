package com.dataracy.modules.behaviorlog.application.port.out;

import java.util.Optional;

/**
 * Redis를 활용한 행동 로그 병합 포트
 */
public interface BehaviorLogMergePort {

    /**
     * 익명 사용자 ID와 실제 사용자 ID를 병합합니다.
     *
     * @param anonymousId 익명 사용자 ID
     * @param userId 실제 사용자 ID
     * @throws RuntimeException Redis 연결 오류 또는 데이터 저장 실패 시
     */
    void merge(String anonymousId, Long userId);

    /**
     * 익명 사용자 ID에 대응하는 병합된 사용자 ID를 조회합니다.
     *
     * @param anonymousId 익명 사용자 ID
     * @return 병합된 사용자 ID 문자열, 없을 경우 null
     * @throws RuntimeException Redis 연결 오류 또는 데이터 조회 실패 시
     */
    Optional<Long> findMergedUserId(String anonymousId);
}
