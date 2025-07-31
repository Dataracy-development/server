package com.dataracy.modules.common.logging;

public class FilterLogger extends BaseLogger {

    /**
     * 지정된 경로에 필터가 적용되었음을 디버그 레벨로 기록합니다.
     *
     * @param path 필터가 적용된 경로
     */
    public void logApplied(String path) {
        debug("[Filter 적용] path={}", path);
    }

    /**
     * 필터가 지정된 경로에 대한 접근을 차단했음을 경고 레벨로 기록합니다.
     *
     * @param path   차단된 요청의 경로
     * @param reason 차단 사유
     */
    public void logBlocked(String path, String reason) {
        warn("[Filter 차단] path={} reason={}", path, reason);
    }
}
