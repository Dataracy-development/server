package com.dataracy.modules.behaviorlog.support.mdc;

/**
 * MDC 키 상수 정의
 */
public final class MdcKey {

    public static final String REQUEST_ID     = "requestId";
    public static final String SESSION_ID     = "sessionId";
    public static final String USER_ID        = "userId";
    public static final String ANONYMOUS_ID   = "anonymousId";
    public static final String ACTION         = "action";
    public static final String IP             = "ip";
    public static final String PATH           = "path";
    public static final String METHOD         = "method";
    public static final String DB_LATENCY     = "dbLatency";
    public static final String REFERRER       = "referrer";
    public static final String STAY_TIME      = "stayTime";
    public static final String NEXT_PATH      = "nextPath";

    private MdcKey() {
    }
}
