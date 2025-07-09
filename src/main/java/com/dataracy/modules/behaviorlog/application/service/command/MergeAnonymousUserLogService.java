package com.dataracy.modules.behaviorlog.application.service.command;

import com.dataracy.modules.behaviorlog.application.port.in.MergeAnonymousUserLogUseCase;
import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogMergePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MergeAnonymousUserLogService implements MergeAnonymousUserLogUseCase {

    private final BehaviorLogMergePort behaviorLogMergePort;

    @Override
    public void merge(String anonymousId, Long userId) {
        behaviorLogMergePort.merge(anonymousId, userId);
    }
}
