package com.dataracy.modules.behaviorlog.application.port.out;

import java.util.Optional;

/**
 * Redis를 활용한 행동 로그 병합 포트
 */
public interface BehaviorLogMergePort {

    /**
 * 익명 사용자 ID와 실제 사용자 ID를 Redis에 병합하여 저장합니다.
 *
 * @param anonymousId 병합할 익명 사용자 ID
 * @param userId 병합 대상이 되는 실제 사용자 ID
 * @throws RuntimeException Redis 연결 오류 또는 데이터 저장 실패 시 발생합니다.
 */
    void merge(String anonymousId, Long userId);

    /**
 * 주어진 익명 사용자 ID에 매핑된 병합된 사용자 ID를 조회합니다.
 *
 * @param anonymousId 병합된 사용자 ID를 조회할 익명 사용자 ID
 * @return 병합된 사용자 ID가 존재하면 해당 값을 포함한 Optional, 없으면 빈 Optional
 * @throws RuntimeException Redis 연결 오류 또는 데이터 조회 실패 시 발생
 */
    Optional<Long> findMergedUserId(String anonymousId);
}
