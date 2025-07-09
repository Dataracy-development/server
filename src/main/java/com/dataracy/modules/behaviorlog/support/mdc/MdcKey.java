package com.dataracy.modules.behaviorlog.support.mdc;

/**
 * MDC(Mapped Diagnostic Context)에서 사용하는 키들을 상수로 정의합니다.
 * 로그백(logback) 설정에서 이 key들을 기반으로 로그 템플릿에 값을 삽입할 수 있습니다.
 */
public class MdcKey {
    public static final String REQUEST_ID = "requestId";     // 요청 ID
    public static final String SESSION_ID = "sessionId";     // 세션 ID
    public static final String USER_ID = "userId";           // 로그인 유저 ID
    public static final String ANONYMOUS_ID = "anonymousId"; // 익명 유저 ID
    public static final String ACTION = "action"; // 익명 유저 ID
    public static final String IP = "ip"; // 클라이언트 ip
    public static final String PATH = "path"; // 경로
    public static final String METHOD = "method"; // http 메서드

}
