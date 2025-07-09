package com.dataracy.modules.behaviorlog.domain.support;

/**
 * MDC(Mapped Diagnostic Context)에서 사용하는 키들을 상수로 정의합니다.
 * 로그백(logback) 설정에서 이 key들을 기반으로 로그 템플릿에 값을 삽입할 수 있습니다.
 */
public final class BehaviorLogMdcKey {
    public static final String USER_ID = "userId";
    public static final String ANONYMOUS_ID = "anonymousId";
    public static final String REQUEST_ID = "requestId";
    public static final String PATH = "path";

    private BehaviorLogMdcKey() {
    }
}
