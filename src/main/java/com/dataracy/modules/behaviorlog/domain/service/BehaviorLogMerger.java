package com.dataracy.modules.behaviorlog.domain.service;

import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BehaviorLogMerger {

    private final BehaviorLogRepositoryPort behaviorLogRepositoryPort;

    /**
     * 로그인 시 익명 행동 로그를 userId로 병합
     */
    public void mergeAnonymousLogs(String anonymousId, String userId) {
        behaviorLogRepositoryPort.updateUserIdByAnonymousId(userId, anonymousId);
    }
}
