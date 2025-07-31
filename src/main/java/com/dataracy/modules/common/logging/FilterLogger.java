package com.dataracy.modules.common.logging;

public class FilterLogger extends BaseLogger {

    public void logApplied(String path) {
        debug("[Filter 적용] path={}", path);
    }

    public void logBlocked(String path, String reason) {
        warn("[Filter 차단] path={} reason={}", path, reason);
    }
}
