package com.dataracy.modules.behaviorlog.application.service.command;

import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogMergePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MergeAnonymousUserLogServiceTest {

    @Mock
    private BehaviorLogMergePort behaviorLogMergePort;

    @InjectMocks
    private MergeAnonymousUserLogService mergeAnonymousUserLogService;

    @Nested
    @DisplayName("익명 사용자 로그 병합")
    class MergeAnonymousUserLog {

        @Test
        @DisplayName("익명 사용자 로그 병합 성공")
        void mergeAnonymousUserLogSuccess() {
            // given
            String anonymousId = "anonymous-123";
            Long userId = 1L;

            // when
            mergeAnonymousUserLogService.merge(anonymousId, userId);

            // then
            then(behaviorLogMergePort).should().merge(anonymousId, userId);
        }

        @Test
        @DisplayName("다른 익명 사용자 로그 병합 성공")
        void mergeDifferentAnonymousUserLogSuccess() {
            // given
            String anonymousId = "anonymous-456";
            Long userId = 2L;

            // when
            mergeAnonymousUserLogService.merge(anonymousId, userId);

            // then
            then(behaviorLogMergePort).should().merge(anonymousId, userId);
        }

        @Test
        @DisplayName("null 익명 ID로 로그 병합")
        void mergeWithNullAnonymousId() {
            // given
            String anonymousId = null;
            Long userId = 1L;

            // when
            mergeAnonymousUserLogService.merge(anonymousId, userId);

            // then
            then(behaviorLogMergePort).should().merge(anonymousId, userId);
        }

        @Test
        @DisplayName("null 사용자 ID로 로그 병합")
        void mergeWithNullUserId() {
            // given
            String anonymousId = "anonymous-123";
            Long userId = null;

            // when
            mergeAnonymousUserLogService.merge(anonymousId, userId);

            // then
            then(behaviorLogMergePort).should().merge(anonymousId, userId);
        }
    }
}
