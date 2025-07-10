package com.dataracy.modules.behaviorlog.application.service.command;

import com.dataracy.modules.behaviorlog.application.port.in.MergeAnonymousUserLogUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogMergePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 익명 → 유저 행동 로그 병합 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MergeAnonymousUserLogService implements MergeAnonymousUserLogUseCase {

    private final BehaviorLogMergePort mergePort;

    @Override
    public void merge(String anonymousId, Long userId) {
        log.info("익명 사용자 로그 병합 시작: anonymousId={}, userId={}", anonymousId, userId);
        mergePort.merge(anonymousId, userId);
        log.info("익명 사용자 로그 병합 완료: anonymousId={}, userId={}", anonymousId, userId);
    }
}
