package com.dataracy.modules.behaviorlog.application.service.command;

import com.dataracy.modules.behaviorlog.application.port.out.BehaviorLogMergePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

        @ParameterizedTest
        @CsvSource({
                "anonymous-123, 1",      // 정상 케이스 1
                "anonymous-456, 2",      // 정상 케이스 2
                ", 1",                    // null 익명 ID
                "anonymous-123,"         // null 사용자 ID
        })
        @DisplayName("익명 사용자 로그 병합 - 모든 케이스에서 merge 호출")
        void mergeAnonymousUserLog(String anonymousId, Long userId) {
            // when
            mergeAnonymousUserLogService.merge(anonymousId, userId);

            // then
            then(behaviorLogMergePort).should().merge(anonymousId, userId);
        }
    }
}
